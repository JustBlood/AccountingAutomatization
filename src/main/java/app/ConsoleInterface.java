package app;

import reports.MonthlyReport;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleInterface {
    public static void main(String[] args) throws IOException {
        List<MonthlyReport> monthlyReportList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            String path = String.format("src\\main\\resources\\m.20210%d.csv", i);
            MonthlyReport report = new MonthlyReport(path);
            monthlyReportList.add(report);
            try {
                report.getReport();
            } catch (OperationNotSupportedException e) {
                System.out.println(e.getMessage());
            }
            System.out.println(report.getMonth());
            System.out.println(report.getYear());
            System.out.println(report.getCheaper());
            System.out.println(report.getMostExpensive());
        }

    }
}
