package ru.yandex.hadoop.benchmark.Service;

import java.util.concurrent.Callable;

/**
 * Created by zstan on 31.10.16.
 */
public interface RunningService extends Callable<ExecutionInfo> {
}
