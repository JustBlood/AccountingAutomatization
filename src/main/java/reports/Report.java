package reports;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Report {
    protected boolean isReportFileRead;
    private String fileContents;
    private String pathToReport;

    public Report(String pathToReport) {
        this.pathToReport = pathToReport;
    }

    public String getPathToReport() {
        return pathToReport;
    }

    protected String getFileContents() {
        return fileContents;
    }
    protected abstract void serializeReport();
    public void readFileContentsOrNull() throws IOException {
        // read items from file
        fileContents = Files.readString(Path.of(pathToReport));
        isReportFileRead = true;
    }
    public abstract String getReportText() throws OperationNotSupportedException;
}
