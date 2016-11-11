package ru.yandex.hadoop.benchmark.DAO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Common.BenchConfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by zstan on 11.11.16.
 */
public class StoreConnector {

    private static final Logger logger = LogManager.getLogger(StoreConnector.class);

    public static Connection getStoreConnection(String userName, String password, boolean printInfo,
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
        return DriverManager.getConnection(connectionURL, userName, password);
    }

    public static void testConnectionToStore(BenchConfiguration conf) {
        Connection conn = null;
        try {
            conn = getStoreConnection("", "", true, conf);
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Failed to close metastore connection", e);
        }
    }

}
