import com.logdirector.logger.Event;
import com.logdirector.logger.LogdirectorLogger;

import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: sasch_000
 * Date: 25.01.13
 * Time: 09:02
 * Just a small example that simulates a job that copies some files from one folder to another.
 */
public class CopyJobExample {

    public static void main(String[] args) {

        // Initialize the logdirector logger with the server URL and the application key
        LogdirectorLogger.getInstance().initialize("http://localhost:8080/logdirector", "copy_job");

        // Send an event that the job has started
        Event.create("cj_started")
                .setStringAttribute("input_folder", "/public/copyjob/in")
                .setStringAttribute("output_folder", "/public/copyjob/out")
                .log();

        // Simulate copy of a few files
        FileJob[] jobs = new FileJob[]{
                new FileJob("Product5738.xml", 83264, false),
                new FileJob("Product2419.xml", 24729, false),
                new FileJob("Product9672.xml", 58326, true),
                new FileJob("Product5912.xml", 18462, false),
                new FileJob("Product2375.xml", 49572, false),
        };

        for (FileJob fileJob : jobs) {
            processFile(fileJob);
        }

        // Send an event that the job has finished
        Event.create("cj_finished")
                .setDecimalAttribute("duration", 1050d)
                .setNumberAttribute("file_count", 5)
                .setNumberAttribute("success_count", 4)
                .setNumberAttribute("error_count", 1)
                .log();

        // Dispose the logger
        LogdirectorLogger.getInstance().dispose();
    }

    private static void processFile(FileJob fileJob) {

        try {

            copyFile(fileJob.simulateAccessError);

            // Send an event that a file was processed successfully
            Event.create("cj_file_processed")
                    .setStringAttribute("filename", fileJob.fileName)
                    .setNumberAttribute("bytes", fileJob.size)
                    .log();

        } catch (FileNotFoundException e) {

            // Send an event that an error occurred while copying the file
            Event.create("cj_file_copy_error")
                    .setStringAttribute("filename", fileJob.fileName)
                    .setNumberAttribute("bytes", fileJob.size)
                    .setExceptionAttribute("exception", e)
                    .log();
        }
    }

    private static void copyFile(boolean simulateError) throws FileNotFoundException {

        if (simulateError) {
            throw new FileNotFoundException("Access denied");
        }
    }

    private static class FileJob {

        public String fileName;
        public long size;
        public Boolean simulateAccessError;

        private FileJob(String fileName, long size, Boolean simulateAccessError) {
            this.fileName = fileName;
            this.size = size;
            this.simulateAccessError = simulateAccessError;
        }
    }
}
