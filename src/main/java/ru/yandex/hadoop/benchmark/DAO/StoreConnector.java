package ru.yandex.hadoop.benchmark.DAO;

import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Common.BenchConfiguration;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zstan on 11.11.16.
 */
public class StoreConnector implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(StoreConnector.class);
    private static StoreConnector INSTANCE;
    private Configuration configuration;
    private static String BENCH_TABLE_NAME = "BENCH.RESULTS";
    private static String TABLE_INIT = "CREATE TABLE \"" + BENCH_TABLE_NAME + "\" (" +
            "\"ID\" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "\"TEST_NAME\" VARCHAR(128) NOT NULL, \"CREATE_TIME\" INTEGER NOT NULL," +
            " \"COMMAND\" LONG VARCHAR, \"RET_CODE\" INTEGER NOT NULL, \"EXECUTION_TIME\" INTEGER NOT NULL," +
            "\"DATA\" INTEGER, \"INFO\" LONG VARCHAR)";
    private Connection connection;

    public StoreConnector(BenchConfiguration conf) {
        this.configuration = conf;
    }

    public Connection getStoreConnection(String userName, String password, boolean printInfo,
                                                      BenchConfiguration conf) throws ClassNotFoundException, SQLException {

        String connectionURL = conf.getVar(
                BenchConfiguration.ConfVars.STORE_JDBC_CONNECT_STRING);
        String driver = conf.getVar(
                BenchConfiguration.ConfVars.STORE_CONNECTION_DRIVER);
        if (printInfo) {
            logger.info("Metastore connection URL:\t " + connectionURL);
            logger.info("Metastore Connection Driver :\t " + driver);
            logger.info("Metastore connection User:\t " + userName);
        }

        // load required JDBC driver
        Class.forName(driver);

        // Connect using the JDBC URL and user/pass from conf
        connection = DriverManager.getConnection(connectionURL, userName, password);
        return connection;
    }

    public static boolean testConnectionToStore(BenchConfiguration conf) {
        Connection conn = null;
        try {
            try (StoreConnector storeConn = new StoreConnector(conf)) {
                conn = storeConn.getStoreConnection("", "", true, conf);
            }
            logger.info("Storage connection test: Ok");
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to initialise metastore connection", e);
        }
        return false;
    }

    public boolean initStoreConnection(BenchConfiguration conf) {
        try {
            connection = getStoreConnection("", "", true, conf);
            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, BENCH_TABLE_NAME, null);
            if(!rs.next()) {
                CallableStatement smt = connection.prepareCall(TABLE_INIT);
                return smt.execute();
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to get store connection", e);
        }
        return false;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Failed to close store connection", e);
            }
        }
    }
}
