package ru.yandex.hadoop.benchmark.Configuration.Native;

/**
 * Created by zstan on 31.10.16.
 */

import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<hadoopNativeBenchConfiguration>
    <commands>
        <cmd>hadoop1</cmd>
        <enable>true</enable>
        <name>h1</name>
    </commands>
    <commands>
        <cmd>hadoop2</cmd>
        <enable>false</enable>
        <name>h2</name>
    </commands>
</hadoopNativeBenchConfiguration>
 */
@XmlRootElement
public class HadoopNativeBenchConfiguration implements IBenchConfiguration {

    List <Command> commands = new ArrayList<>();

    public List<Command> getCommands() {
        return commands;
    }

    @XmlElement
    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
}

