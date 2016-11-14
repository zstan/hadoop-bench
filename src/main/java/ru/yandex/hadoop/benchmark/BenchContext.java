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
public class BenchContext implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(BenchContext.class);
    private static BenchContext INSTANCE;

    private static final String GENERAL_CONF = "NativeConf.xml";

    private HadoopNativeBenchConfiguration nativeConfiguration;
    private BenchConfiguration benchConfiguration;
    private StoreConnector storeConnector;

    public static BenchContext instance() throws Exception {
        if (INSTANCE == null) {
            INSTANCE = new BenchContext();
        }
        return INSTANCE;
    }

    private BenchContext() throws Exception {
        benchConfiguration = new BenchConfiguration();
        if (StoreConnector.testConnectionToStore(benchConfiguration)) {
            storeConnector = new StoreConnector(benchConfiguration);
            if (!storeConnector.initStoreConnection(benchConfiguration)) {
                logger.error("storeConnector init error");
                throw new Exception("storeConnector init error");
            }
        }
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

    public StoreConnector getStoreConnector() {
        return storeConnector;
    }

    @Override
    public void close() throws Exception {
        if (storeConnector != null)
            storeConnector.close();
    }
}
