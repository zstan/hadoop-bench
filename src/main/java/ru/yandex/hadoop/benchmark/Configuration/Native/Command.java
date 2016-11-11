package ru.yandex.hadoop.benchmark.Configuration.Native;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zstan on 31.10.16.
 */
public class Command {

    String cmd;
    String name;
    Boolean enable;

    public String getCmd() {
        return cmd;
    }

    @XmlElement
    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Boolean getEnable() {
        return enable;
    }

    @XmlElement
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getName() {
        return name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("running %s as %s", this.getName(), this.getCmd());
    }
}