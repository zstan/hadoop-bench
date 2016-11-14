package ru.yandex.hadoop.benchmark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;
import ru.yandex.hadoop.benchmark.Service.ExecutionInfo;
import ru.yandex.hadoop.benchmark.Service.NativeRunningService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zstan on 31.10.16.
 */
public class BenchRunner {

    private static final Logger logger = LogManager.getLogger(BenchRunner.class);

    public static void main(String[] args) {
        BenchContext ctx = null;
        try {
            ctx = BenchContext.instance();
        } catch (Exception e) {
            logger.error(e);
            return;
        }
        List<ExecutionInfo> resultsList = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(1);

        HadoopNativeBenchConfiguration cfg = ctx.getNativeConfiguration();

        cfg.getCommands().forEach(s -> {
            if (s.getEnable()) {
                NativeRunningService service = new NativeRunningService(s);
                long timeStart = System.currentTimeMillis();
                Future<ExecutionInfo> future = pool.submit(service);
                try {
                    ExecutionInfo info = future.get();
                    info.setExecutionTime(System.currentTimeMillis() - timeStart);
                    info.setCreationTime(timeStart);
                    resultsList.add(info);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        pool.shutdown();

        BenchContext finalCtx = ctx;
        resultsList.forEach(s -> { finalCtx.getStoreConnector().storeBenchInfo(s);
            System.out.println(s.getCommand());
            System.out.println("return code: " + s.getReturnCode());
            System.out.println("execution time: " + s.getExecutionTime());
            //System.out.println("output: \n" + s.getExecOutput());
            System.out.println("\n\n");
        });
    }
}
