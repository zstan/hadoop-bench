/**
 * Created by zstan on 31.10.16.
 */
import ru.yandex.hadoop.benchmark.Configuration.Command;
import ru.yandex.hadoop.benchmark.Configuration.Native.HadoopNativeBenchConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class Example {
    public static void main(String[] args) {

        HadoopNativeBenchConfiguration conf = new HadoopNativeBenchConfiguration();
        Command e1 = new Command();
        e1.setCmd("hadoop1"); e1.setName("h1"); e1.setEnable(true);
        Command e2 = new Command();
        e2.setCmd("hadoop2"); e2.setName("h2"); e2.setEnable(false);
        List<Command> rl = new ArrayList<>();
        rl.add(e1);
        rl.add(e2);
        conf.setAction(rl);

        try {

            File file = new File("./file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(HadoopNativeBenchConfiguration.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //jaxbMarshaller.marshal(conf, file);
            jaxbMarshaller.marshal(conf, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }


        try {

            File file = new File("./file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(HadoopNativeBenchConfiguration.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            HadoopNativeBenchConfiguration customer = (HadoopNativeBenchConfiguration) jaxbUnmarshaller.unmarshal(file);
            //System.out.println(customer.getCommands().size());

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
