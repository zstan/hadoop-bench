package ru.yandex.hadoop.benchmark.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Command;

import java.util.Collections;

/**
 * Created by zstan on 10.11.16.
 */
public class JDBCRunningService implements IRunningService {
    private static final Logger logger = LogManager.getLogger(JDBCRunningService.class);
    private final Command command;

    public JDBCRunningService(final Command cmd) {
        command = cmd;
    }

    @Override
    public ExecutionInfo call() throws Exception {
        int retCode = -1;
        if (!command.getEnable())
            return ExecutionInfo.EMPTY;
        logger.info("sql to run: " + command.getCmd());
        return new ExecutionInfo(Collections.EMPTY_LIST, retCode, command);
    }
}
