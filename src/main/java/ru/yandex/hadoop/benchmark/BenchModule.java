package ru.yandex.hadoop.benchmark;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;
import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;

import javax.inject.Scope;

import static ru.yandex.hadoop.benchmark.Utils.Utils.importConfiguration;

/**
 * Created by zstan on 16.11.16.
 */
public class BenchModule extends AbstractModule {

    private static final Logger logger = LogManager.getLogger(BenchModule.class);
    private static final String GENERAL_CONF = "NativeConf.xml";

    @Override
    protected void configure() {
        bind(IBenchConfiguration.class)
                .toInstance(importConfiguration(GENERAL_CONF, HadoopNativeBenchConfiguration.class));

        try {
            bind(AutoCloseable.class)
                    .to(BenchContext.class)
                    .in(Scopes.SINGLETON);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
