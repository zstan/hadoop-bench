package ru.yandex.hadoop.benchmark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Command;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;
import ru.yandex.hadoop.benchmark.Service.ExecutionInfo;
import ru.yandex.hadoop.benchmark.Service.IRunningService;
import ru.yandex.hadoop.benchmark.Service.JDBCRunningService;
import ru.yandex.hadoop.benchmark.Service.NativeRunningService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.reflect.Constructor;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by zstan on 31.10.16.
 */
public class BenchRunner {

    private static final Logger logger = LogManager.getLogger(BenchRunner.class);

    @Parameters(commandDescription = "Hadoop benchmarking tool")
    public static class BenchCmdOptions {

        @Parameter(names = {"--list", "-l"}, description = "list all results")
        public boolean list = false;

        @Parameter(names = {"--benchmarks", "-b"}, description = "benchmarking mode")
        public boolean bench = false;

        @Parameter(names = {"--clear", "-c"}, description = "clear benchmark results")
        public boolean clearResults = false;

        @Parameter(names = {"--help"}, description = "help info", help = true)
        public boolean helpInfo = false;
    }

    static boolean parseCmdArgs(String[] args, BenchCmdOptions cmdOptions) {
        JCommander jCommander = new JCommander(cmdOptions);
        jCommander.setProgramName(BenchRunner.class.getSimpleName());

        try {
            jCommander.parse(args);
            if (cmdOptions.helpInfo || args.length == 0) {
                jCommander.usage();
                return false;
            }
        } catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            jCommander.usage();
            return false;
        }
        return true;
    }

    private static void initRunners(Map<Class<? extends IRunningService>, IBenchConfiguration> runners,
                                    final BenchContext ctx) {

        ExecutorService pool = Executors.newFixedThreadPool(1);
        ListeningExecutorService service = MoreExecutors.listeningDecorator(pool);

        runners.forEach( (k, v) -> {

            v.getAction().forEach(s -> {
                if (s.getEnable()) {
                    Constructor ctor = null;
                    IRunningService runner = null;
                    try {
                        ctor = k.getConstructor(new Class[]{Command.class, IBenchConfiguration.class});
                        runner = (IRunningService) ctor.newInstance(s, v);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    long timeStart = System.currentTimeMillis();
                    ListenableFuture<ExecutionInfo> future = service.submit(runner);

                    Futures.addCallback(future, new FutureCallback<ExecutionInfo>() {

                        public void onSuccess(ExecutionInfo info) {
                            info.setExecutionTime(System.currentTimeMillis() - timeStart);
                            info.setCreationTime(timeStart);

                            ctx.getStoreConnector().storeBenchInfo(info);
                            System.out.println(info.getCommand());
                            System.out.println("return code: " + info.getReturnCode());
                            System.out.println("execution time: " + info.getExecutionTime());
                            //System.out.println("output: \n" + s.getExecOutput());
                            System.out.println("\n\n");
                        }

                        public void onFailure(Throwable thrown) {
                            logger.error(thrown);
                        }
                    });
                }
            });
        });
        pool.shutdown();
    }

    public static void main(String[] args) {

        BenchCmdOptions cmdOptions = new BenchCmdOptions();
        if (!parseCmdArgs(args, cmdOptions))
            return;

        Injector injector = Guice.createInjector(new BenchModule());
        BenchContext ctx = injector.getInstance(BenchContext.class);

        Map<Class<? extends IRunningService>, IBenchConfiguration> runnerConfigurationMap = new HashMap<>();
        runnerConfigurationMap.put(NativeRunningService.class, ctx.getCmdRunConfiguration());
        runnerConfigurationMap.put(JDBCRunningService.class, ctx.getJdbcRunConfiguration());

        if (cmdOptions.clearResults) {
            ctx.getStoreConnector().clearBenchResults();
            return;
        }

        if (cmdOptions.list) {
            ctx.getStoreConnector().listBenchResults();
            return;
        }

        if (cmdOptions.bench) {
            initRunners(runnerConfigurationMap, ctx);
            return;
        }
    }
}
