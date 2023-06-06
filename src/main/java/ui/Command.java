package ui;

public enum Command {
    ReadMonthReports("считать месячные отчёты"),
    ReadYearReport("считать годовой отчёт"),
    CompareReports("сверить отчёты"),
    ShowMonthsInfo("показать месячные отчёты"),
    ShowYearInfo("показать годовой отчёт"),
    Help("Помощь"),
    Exit("Выход");
    private final String title;
    Command(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
