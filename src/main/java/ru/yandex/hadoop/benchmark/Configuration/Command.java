package ru.yandex.hadoop.benchmark.Configuration;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by zstan on 31.10.16.
 */
public class Command {

    String cmd;
    String preconditions;
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
        return name == null ? cmd : name;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public String getPreconditions() {
        return preconditions;
    }

    @XmlElement
    public void setPreconditions(String preconditions) {
        this.preconditions = preconditions;
    }

    @Override
    public String toString() {
        return String.format("running %s as %s", this.getName(), this.getCmd());
    }
}