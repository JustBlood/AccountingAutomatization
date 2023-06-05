package app;

import reports.MonthlyReport;
import reports.ReportsManager;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConsoleInterface {
    private static final File folder = new File("src\\main\\resources\\");
    private static final ReportsManager reportsManager = new ReportsManager(folder);
    public static void main(String[] args) {

    }
}
