package ru.yandex.hadoop.benchmark.Service;

import ru.yandex.hadoop.benchmark.Configuration.Command;

import java.util.List;

/**
 * Created by zstan on 10.11.16.
 */
public class ExecutionInfo {

    private final String execOutput;
    private final int returnCode;
    private final Command command;
    private long executionTime;
    private long createTime;

    private ExecutionInfo() {
        this.execOutput = "";
        this.returnCode = -1;
        this.command = null;
    }

    public ExecutionInfo(List<String> out, int retCode, Command cmd) {
        this.execOutput = String.join("\n", out);
        this.returnCode = retCode;
        this.command = cmd;
    }

    public static ExecutionInfo EMPTY = new ExecutionInfo();

    public ExecutionInfo setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
        return this;
    }

    public String getExecOutput() {
        return execOutput;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public Command getCommand() {
        return command;
    }

    public long getCreationTime() {
        return createTime;
    }

    public void setCreationTime(long createTime) {
        this.createTime = createTime;
    }
}
