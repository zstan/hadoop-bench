/**
 * Created by zstan on 31.10.16.
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
class HadoopNativeBenchConfiguration {

    List <RunElement> commands = new ArrayList<>();

    public List<RunElement> getCommands() {
        return commands;
    }

    @XmlElement
    public void setCommands(List<RunElement> commands) {
        this.commands = commands;
    }
}

class RunElement {

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
}

public class Example {
    public static void main(String[] args) {

        HadoopNativeBenchConfiguration conf = new HadoopNativeBenchConfiguration();
        RunElement e1 = new RunElement();
        e1.setCmd("hadoop1"); e1.setName("h1"); e1.setEnable(true);
        RunElement e2 = new RunElement();
        e2.setCmd("hadoop2"); e2.setName("h2"); e2.setEnable(false);
        List<RunElement> rl = new ArrayList<>();
        rl.add(e1);
        rl.add(e2);
        conf.setCommands(rl);

        try {

            File file = new File("./file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(HadoopNativeBenchConfiguration.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(conf, file);
            jaxbMarshaller.marshal(conf, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }


        try {

            File file = new File("./file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(HadoopNativeBenchConfiguration.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            HadoopNativeBenchConfiguration customer = (HadoopNativeBenchConfiguration) jaxbUnmarshaller.unmarshal(file);
            System.out.println(customer.getCommands().size());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
