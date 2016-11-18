package ru.yandex.hadoop.benchmark;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ru.yandex.hadoop.benchmark.Configuration.Common.CommonBenchConfiguration;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;
import ru.yandex.hadoop.benchmark.DAO.StoreConnector;

/**
 * Created by zstan on 31.10.16.
 */
@Singleton
public class BenchContext implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(BenchContext.class);
    private static BenchContext INSTANCE;

    private IBenchConfiguration cmdRunConfiguration;
    private IBenchConfiguration jdbcRunConfiguration;
    private CommonBenchConfiguration benchConfiguration;
    private StoreConnector storeConnector;

    public BenchContext() throws Exception {
        benchConfiguration = new CommonBenchConfiguration();
        if (StoreConnector.testConnectionToStore(benchConfiguration)) {
            storeConnector = new StoreConnector(benchConfiguration);
            if (!storeConnector.initStoreConnection(benchConfiguration)) {
                logger.error("storeConnector init error");
                throw new Exception("storeConnector init error");
            }
        }
    }

    @Inject
    private void injectCmdBenchConfiguration(@Named("cmdConf") IBenchConfiguration conf) {
        cmdRunConfiguration = conf;
    }

    @Inject
    private void injectJDBCBenchConfiguration(@Named("jdbcConf") IBenchConfiguration conf) {
        jdbcRunConfiguration = conf;
    }

    public IBenchConfiguration getCmdRunConfiguration() {
        return cmdRunConfiguration;
    }

    public Configuration getBenchConfiguration() {
        return benchConfiguration;
    }

    public StoreConnector getStoreConnector() {
        return storeConnector;
    }

    public IBenchConfiguration getJdbcRunConfiguration() {
        return jdbcRunConfiguration;
    }

    @Override
    public void close() throws Exception {
        if (storeConnector != null)
            storeConnector.close();
    }
}
