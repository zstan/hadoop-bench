package ru.yandex.hadoop.benchmark;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
                                    IBenchConfiguration cfg) {
        List<ExecutionInfo> resultsList = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(1);

        runners.forEach( (k, v) -> {

            v.getAction().forEach(s -> {
                if (s.getEnable()) {
                    Constructor ctor = null;
                    IRunningService runner = null;
                    try {
                        ctor = k.getConstructor(new Class[]{Command.class});
                        runner = (IRunningService) ctor.newInstance(s);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    long timeStart = System.currentTimeMillis();
                    Future<ExecutionInfo> future = pool.submit(runner);
                    try {
                        ExecutionInfo info = future.get();
                        info.setExecutionTime(System.currentTimeMillis() - timeStart);
                        info.setCreationTime(timeStart);
                        resultsList.add(info);
                    } catch (InterruptedException | ExecutionException e) {
                        logger.error(e);
                    }
                }
            });

            //BenchContext finalCtx = ctx;
            resultsList.forEach(s -> {
                //finalCtx.getStoreConnector().storeBenchInfo(s);
                System.out.println(s.getCommand());
                System.out.println("return code: " + s.getReturnCode());
                System.out.println("execution time: " + s.getExecutionTime());
                //System.out.println("output: \n" + s.getExecOutput());
                System.out.println("\n\n");
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

        IBenchConfiguration cfg = ctx.getCmdRunConfiguration();

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

        initRunners(runnerConfigurationMap, cfg);

    }
}
