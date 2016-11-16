package ru.yandex.hadoop.benchmark.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zstan on 31.10.16.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    /**
     * Load an XML document from a file path
     *
     * @param filePath The file path to load
     * @return The loaded Document object
     */
    public static Document loadXMLDoc(String filePath) {
        InputStream instream = null;
        logger.info("Loading configuration file " + filePath);
        instream =
                Utils.class.getClassLoader().getResourceAsStream(filePath);

        if (instream == null) {
            logger.info("Configuation file not present in classpath. File:  " + filePath);
            throw new RuntimeException("Unable to read " + filePath);
        }
        logger.info("Configuation file loaded. File: " + filePath);

        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(instream);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML Parser could not be created.", e);
        } catch (SAXException e) {
            throw new RuntimeException(filePath + " is not properly formed", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read " + filePath, e);
        }

        return document;
    }

    public static <T extends IBenchConfiguration> T importConfiguration(final String resFileName, Class _class) {
        try {
            File file = new File(resFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T out = (T) jaxbUnmarshaller.unmarshal(file);
            return out;

        } catch (JAXBException e) {
            logger.error(e);
        }
        return null;
    }

}
