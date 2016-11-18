package ru.yandex.hadoop.benchmark.Service;

import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Command;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

/**
 * Created by zstan on 10.11.16.
 */
public class JDBCRunningService implements IRunningService {
    private static final Logger logger = LogManager.getLogger(JDBCRunningService.class);
    private final Command command;
    private final IBenchConfiguration configuration;
    private Connection connection;


    // TODO: change into connection pool
    public JDBCRunningService(final Command cmd, final IBenchConfiguration cfg) {
        command = cmd;
        configuration = cfg;
        try {
            Class.forName(cfg.getConnectionDriverName());
            DriverManager.getConnection(cfg.getConnectionURL(), "", "");
        } catch (ClassNotFoundException e) {
            logger.error(e);
            throw new RuntimeException("Unable to load hive jdbc driver.", e);
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException("Unable to initiate connection.", e);
        }
    }

    @Override
    public ExecutionInfo call() throws Exception {
        int retCode = -1;
        if (!command.getEnable())
            return ExecutionInfo.EMPTY;
        logger.info("sql to run: " + command.getCmd());
        if (connection != null)
            connection.close();
        return new ExecutionInfo(Collections.EMPTY_LIST, retCode, command);
    }

}
