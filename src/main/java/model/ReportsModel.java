package model;

public class ReportsModel {
    private final YearReportModel yearReport;
    private final MonthReportModel[] monthlyReports;

    public ReportsModel() {
        this.yearReport = null;
        this.monthlyReports = new MonthReportModel[12];
    }

    public ReportsModel(YearReportModel yearReportModel, MonthReportModel[] monthlyReports) {
        if (monthlyReports.length > 12) {
            throw new IllegalArgumentException("MonthlyReports length should be < 12");
        }
        this.yearReport = yearReportModel;
        this.monthlyReports = monthlyReports;
    }

    public YearReportModel getYearReport() {
        return yearReport;
    }


    public MonthReportModel[] getMonthlyReports() {
        return monthlyReports;
    }
}
