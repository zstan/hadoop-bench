package ru.yandex.hadoop.benchmark.Configuration.JDBC;

import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zstan on 17.11.16.
 */
@XmlRootElement
public class JDBCBenchConfiguration extends HadoopNativeBenchConfiguration {

    private String connectionURL;
    private String connectionDriverName;

    @XmlElement
    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    @XmlElement
    public void setConnectionDriverName(String connectionDriverName) {
        this.connectionDriverName = connectionDriverName;
    }

    @Override
    public String getConnectionURL() {
        return connectionURL;
    }

    @Override
    public String getConnectionDriverName() {
        return connectionDriverName;
    }
}
