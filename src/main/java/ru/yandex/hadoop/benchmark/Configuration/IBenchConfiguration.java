package ru.yandex.hadoop.benchmark.Configuration;

import ru.yandex.hadoop.benchmark.Configuration.Native.Command;

import java.util.List;

/**
 * Created by zstan on 31.10.16.
 */
public interface IBenchConfiguration {
    List<Command> getCommands();
}
