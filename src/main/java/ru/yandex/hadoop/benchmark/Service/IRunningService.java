package ru.yandex.hadoop.benchmark.Service;

import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;

import java.util.concurrent.Callable;

/**
 * Created by zstan on 31.10.16.
 */
public interface IRunningService extends Callable<ExecutionInfo> {
}
