package ru.yandex.hadoop.benchmark.Configuration;

import java.util.List;

/**
 * Created by zstan on 31.10.16.
 */
public interface IBenchConfiguration {
    List<Command> getAction();

    default String getConnectionURL() {
        return "";
    }

    default String getConnectionDriverName() {
        return "";
    }
}
