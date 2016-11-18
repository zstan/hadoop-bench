package ru.yandex.hadoop.benchmark.Configuration.Native;

/**
 * Created by zstan on 31.10.16.
 */

import ru.yandex.hadoop.benchmark.Configuration.Command;
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
        <preconditions>ls</preconditions>
        <enable>true</enable>
        <name>h1</name>
    </commands>
    <commands>
        <cmd>hadoop2</cmd>
        <preconditions>hadoop fs -rm -r /tmp/teraInput</preconditions>
        <enable>false</enable>
        <name>h2</name>
    </commands>
</hadoopNativeBenchConfiguration>
 */
@XmlRootElement
public class HadoopNativeBenchConfiguration implements IBenchConfiguration {

    List <Command> action = new ArrayList<>();

    public List<Command> getAction() {
        return action;
    }

    @XmlElement
    public void setAction(List<Command> action) {
        this.action = action;
    }
}

