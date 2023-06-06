package model;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Report {
    protected boolean isReportFileRead;
    private String fileContents;
    private String pathToReportFile;

    public Report(String pathToReport) {
        this.pathToReportFile = pathToReport;
    }

    public String getPathToReportFile() {
        return pathToReportFile;
    }

    protected String getFileContents() {
        return fileContents;
    }
    protected abstract void serializeReport();
    protected void readFileContentsOrNull() throws IOException {
        // read items from file
        fileContents = Files.readString(Path.of(pathToReportFile));
        isReportFileRead = true;
    }
    public abstract String getReportText() throws OperationNotSupportedException;
}
