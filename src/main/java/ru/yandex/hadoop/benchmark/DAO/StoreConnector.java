package ru.yandex.hadoop.benchmark.DAO;

import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Common.BenchConfiguration;
import ru.yandex.hadoop.benchmark.Service.ExecutionInfo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by zstan on 11.11.16.
 */
public class StoreConnector implements AutoCloseable {

    private static final Logger logger = LogManager.getLogger(StoreConnector.class);
    private static StoreConnector INSTANCE;
    private Configuration configuration;
    private static String BENCH_TABLE_NAME = "RESULTS";
    private static String TABLE_INIT = "CREATE TABLE \"" + BENCH_TABLE_NAME + "\" (" +
            "\"ID\" INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
            "\"TEST_NAME\" VARCHAR(128) NOT NULL, \"CREATE_TIME\" BIGINT NOT NULL," +
            " \"COMMAND\" LONG VARCHAR, \"RET_CODE\" BIGINT NOT NULL, \"EXECUTION_TIME\" BIGINT NOT NULL," +
            "\"DATA\" BIGINT, \"INFO\" LONG VARCHAR)";
    private Connection connection;

    public StoreConnector(BenchConfiguration conf) {
        this.configuration = conf;
    }

    private Connection getStoreConnection(String userName, String password, boolean printInfo,
                                                      BenchConfiguration conf) throws ClassNotFoundException, SQLException {

        String connectionURL = conf.getVar(
                BenchConfiguration.ConfVars.STORE_JDBC_CONNECT_STRING);
        String driver = conf.getVar(
                BenchConfiguration.ConfVars.STORE_CONNECTION_DRIVER);
        if (printInfo) {
            logger.info("Store connection URL:\t " + connectionURL);
            logger.info("Store Connection Driver :\t " + driver);
            logger.info("Store connection User:\t " + userName);
        }

        // load required JDBC driver
        Class.forName(driver);

        // Connect using the JDBC URL and user/pass from conf
        connection = DriverManager.getConnection(connectionURL, userName, password);
        return connection;
    }

    public void storeBenchInfo(ExecutionInfo info) {
        Preconditions.checkNotNull(connection);
        Statement state = null;
        try {
            state = connection.createStatement();
            String values = String.format("'%s', %d, '%s', %d, %d", info.getCommand().getName(), info.getCreationTime(),
                    info.getCommand().getCmd(), info.getReturnCode(), info.getExecutionTime());

            state.execute("insert into " + BENCH_TABLE_NAME + " (TEST_NAME, CREATE_TIME, COMMAND, RET_CODE, EXECUTION_TIME) " +
                    "values (" + values + ")");
            state.close();
        } catch (SQLException e) {
            logger.error(e);
        }
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

            /*Statement state1 = connection.createStatement();
            boolean status1 = state1.execute("DROP TABLE RESULTS");
            state1.close();*/

            DatabaseMetaData dbmd = connection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, BENCH_TABLE_NAME, null);

            if(!rs.next()) {
                Statement state = connection.createStatement();
                state.execute(TABLE_INIT);
                state.close();
                return true;
            } else {
                logger.info(rs.getString("TABLE_NAME"));
                return true;
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
