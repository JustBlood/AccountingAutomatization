package bl;

import model.MonthlyReport;
import model.YearReport;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.util.*;

public class ReportsManager {
    // todo: переделать всю логику, ибо это пиздец какая ху-е-та
    private final List<MonthlyReport> monthlyReports;
    private final List<YearReport> yearlyReports;
    private File folder;
    public ReportsManager(File folder) {
        this.folder = folder;
        monthlyReports = new ArrayList<>();
        yearlyReports = new ArrayList<>();
    }

    public String compareReports(int year) {
        // comparing monthly reports with year reports
        return null;
    }

    public void readAllMonthsReports(int year) {
        List<MonthlyReport> monthlyReportList = new ArrayList<>();
        for (var file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().contains("m." + year)) {
                // месячный отчёт необходимого года
                MonthlyReport report = new MonthlyReport(folder.getPath());
                // add month report to map
                monthlyReportList.add(report);
            }
        }
    }

    public void readYearReport(int year) {
        for (var file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().contains("y." + year)) {
                // месячный отчёт необходимого года
                YearReport report = new YearReport(folder.getPath(), year);
                yearlyReports.add(report);
                String reportMessage = report.getReportText();
                System.out.println(reportMessage);
            }
        }
    }

    public String getAllMonthlyReports(int year) {
        try {
            String reportMessage = report.getReportText();
            System.out.println(reportMessage);
        } catch (OperationNotSupportedException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getAllYearlyReports() {
        return null;
    }

    public String getYearlyReport(int year) {
        for (var yearReport : yearlyReports) {
            if (yearReport.getYear() == year) {
                StringBuilder resultReportText = new StringBuilder();
                resultReportText.append(yearReport.getReportText());
                int averageExpense = 0;
                int averageProfit = 0;
                int count = 0;
                for (var monthlyReport : monthlyReports) {
                    if (monthlyReport.getYear() == year) {
                        averageExpense += monthlyReport.getAllExpenses();
                        averageProfit += monthlyReport.getAllProfits();
                        count++;
                    }
                    if (count > 0) {
                        averageExpense /= count;
                        averageProfit /= count;
                    }
                }
                resultReportText.append(String.format("""
                        Средний расход: %s
                        Средний доход: %s
                        """, averageExpense, averageProfit));
                return resultReportText.toString();
            }
        }
        throw new NoSuchElementException("Year does not exist");
    }
}
