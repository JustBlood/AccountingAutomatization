package reports;

public class YearlyReport extends Report {

    public YearlyReport(String pathToReport) {
        super(pathToReport);
    }

    @Override
    protected void serializeReport() {

    }

    private static class MonthItem {
        private boolean isExpense;
        private int month;
        private int amount;
    }

    @Override
    public void createReport() {

    }
}
