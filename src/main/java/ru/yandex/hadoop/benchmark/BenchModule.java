package ru.yandex.hadoop.benchmark;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;
import ru.yandex.hadoop.benchmark.Configuration.JDBC.JDBCBenchConfiguration;
import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;
import ru.yandex.hadoop.benchmark.Service.JDBCRunningService;

import static ru.yandex.hadoop.benchmark.Utils.Utils.importConfiguration;

/**
 * Created by zstan on 16.11.16.
 */
public class BenchModule extends AbstractModule {

    private static final Logger logger = LogManager.getLogger(BenchModule.class);
    private static final String GENERAL_CONF = "NativeConf.xml";
    private static final String JDBC_CONF    = "JDBCConf.xml";
    private HadoopNativeBenchConfiguration hadoopNativeConfig = importConfiguration(GENERAL_CONF, HadoopNativeBenchConfiguration.class);
    private JDBCBenchConfiguration jdbcConfig = importConfiguration(JDBC_CONF, JDBCBenchConfiguration.class);

    @Override
    protected void configure() {

        bind(IBenchConfiguration.class)
                .annotatedWith(Names.named("cmdConf"))
                .toInstance(hadoopNativeConfig);

        bind(IBenchConfiguration.class)
                .annotatedWith(Names.named("jdbcConf"))
                .toInstance(jdbcConfig);
    }
}
