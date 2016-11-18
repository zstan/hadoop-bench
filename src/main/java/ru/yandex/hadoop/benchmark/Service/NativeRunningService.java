package ru.yandex.hadoop.benchmark.Service;

import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.yandex.hadoop.benchmark.Configuration.Command;
import ru.yandex.hadoop.benchmark.Configuration.IBenchConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zstan on 31.10.16.
 */
public class NativeRunningService implements IRunningService {
    private static final Logger logger = LogManager.getLogger(NativeRunningService.class);
    private final Command command;
    private final IBenchConfiguration configuration;

    public NativeRunningService(final Command cmd, final IBenchConfiguration cfg) {
        command = cmd;
        configuration = cfg;
    }

    @Override
    public ExecutionInfo call() throws Exception {
        int retCode = -1;
        if (!command.getEnable())
            return ExecutionInfo.EMPTY;

        if (!Strings.isNullOrEmpty(command.getPreconditions())) {
            logger.info("run preconditions: " + command.getPreconditions());
            Process pr = Runtime.getRuntime().exec(command.getPreconditions());
            int ret = pr.waitFor();
            if (ret != 0) {
                logger.error("preconditions: " + command.getPreconditions() + " failed with return code: " + ret);
            }
        }
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", command.getCmd());
        List<String> output = new ArrayList<>();
        try {
            logger.info("start: " + command);
            Process p = pb.start();
            OutputBufferThread stdOut = new OutputBufferThread(p.getInputStream());
            OutputBufferThread stdErr = new OutputBufferThread(p.getErrorStream());
            stdOut.start();
            stdErr.start();
            stdOut.join();
            stdErr.join();
            retCode = p.waitFor();
            if (retCode != 0) {
                logger.error(command + " failed with error code " + retCode);
                stdErr.getOutput().stream().forEach(logger::debug);
                output.addAll(stdErr.getOutput());
            }
            output.addAll(stdOut.getOutput());
        } catch (Exception ex) {
            logger.error(command + " failed: " + ex.toString());
            output.forEach(logger::debug);
        }
        return new ExecutionInfo(output, retCode, command);
    }

    /**
     * OutputBufferThread is a background thread for consuming and storing output
     * of the external process.
     */
    private static class OutputBufferThread extends Thread {
        private List<String> output;
        private BufferedReader reader;

        /**
         * Creates a new OutputBufferThread to consume the given InputStream.
         *
         * @param is InputStream to consume
         */
        public OutputBufferThread(InputStream is) {
            this.setDaemon(true);
            output = new ArrayList<String>();
            reader = new BufferedReader(new InputStreamReader(is));
        }

        @Override
        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    output.add(line);
                    line = reader.readLine();
                }
            } catch (IOException ex) {
                throw new RuntimeException("make failed with error code " + ex.toString());
            }
        }

        /**
         * Returns every line consumed from the input.
         *
         * @return List<String> every line consumed from the input
         */
        public List<String> getOutput() {
            return output;
        }
    }
}
