package ru.yandex.hadoop.benchmark.Configuration.Common;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zstan on 11.11.16.
 */

public class BenchConfiguration extends Configuration {

    private static final Logger logger = LogManager.getLogger(BenchConfiguration.class);
    private static final Map<String, ConfVars> vars = new HashMap<String, ConfVars>();
    private static URL configDefaultURL = null;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BenchConfiguration.class.getClassLoader();
        }

        configDefaultURL = classLoader.getResource("hadoop-bench-config.xml");
        for (ConfVars confVar : ConfVars.values()) {
            vars.put(confVar.varname, confVar);
        }
    }

    public static enum ConfVars {
        STORE_JDBC_CONNECT_STRING("javax.jdo.option.ConnectionURL",
                "jdbc:derby:;databaseName=default_bench_results_db;create=true",
                "JDBC connect string for a JDBC resultstore"),
        STORE_CONNECTION_DRIVER("javax.jdo.option.ConnectionDriverName",
                "org.apache.derby.jdbc.ClientDriver",
                "Driver class name for a JDBC metastore");

        public final String varname;
        private final String description;
        private final String defaultExpr;

        ConfVars(String varname, Object defaultVal, String description) {
            this.varname = varname;
            this.defaultExpr = defaultVal == null ? null : String.valueOf(defaultVal);
            this.description = description;
        }
    }

    public String getVar(ConfVars var) {
        return getVar(this, var);
    }

    public static String getVar(Configuration conf, ConfVars var) {
        return conf.get(var.varname, var.defaultExpr);
    }

    public BenchConfiguration() {
        if (configDefaultURL != null)
            addResource(configDefaultURL);
    }
}
