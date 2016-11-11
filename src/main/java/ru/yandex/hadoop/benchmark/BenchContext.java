package ru.yandex.hadoop.benchmark;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ru.yandex.hadoop.benchmark.Configuration.Common.BenchConfiguration;
import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;
import ru.yandex.hadoop.benchmark.DAO.StoreConnector;

import static ru.yandex.hadoop.benchmark.Utils.Utils.importConfiguration;

/**
 * Created by zstan on 31.10.16.
 */
public class BenchContext {
    private static final Logger logger = LogManager.getLogger(BenchContext.class);
    private static BenchContext INSTANCE;

    private static final String GENERAL_CONF = "NativeConf.xml";

    private HadoopNativeBenchConfiguration nativeConfiguration;
    private BenchConfiguration benchConfiguration;

    public static BenchContext instance() {
        if (INSTANCE == null) {
            INSTANCE = new BenchContext();
        }
        return INSTANCE;
    }

    private BenchContext() {
        benchConfiguration = new BenchConfiguration();
        StoreConnector.testConnectionToStore(benchConfiguration);
        loadConfiguration();
    }

    private void loadConfiguration() {
        loadNativeBenchmarksConf();
    }

    private void loadNativeBenchmarksConf() {
        nativeConfiguration =
            importConfiguration(GENERAL_CONF, HadoopNativeBenchConfiguration.class);
    }

    public HadoopNativeBenchConfiguration getNativeConfiguration() {
        return nativeConfiguration;
    }

    public BenchConfiguration getBenchConfiguration() {
        return benchConfiguration;
    }
}
