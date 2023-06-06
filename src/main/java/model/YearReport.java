package model;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class YearReport extends Report {
    private static class MonthItem {
        private boolean isExpense;
        private int month;
        private int amount;
        public MonthItem (int month, int amount, boolean isExpense) {
            this.isExpense = isExpense;
            this.month = month;
            this.amount = amount;
        }

        public int getRealAmount() {
            return amount * (isExpense ? -1 : 1);
        }

        @Override
        public String toString() {
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.of("ru"));
            return String.format("%s, прибыль: %s\n", monthName, getRealAmount());
        }
    }
    private int year;
    private List<MonthItem> monthsAmounts;

    public YearReport(String pathToReport, int year) {
        super(pathToReport);
        this.year = year;
        monthsAmounts = new ArrayList<>();
    }

    public void readReport() throws OperationNotSupportedException {
        try {
            readFileContentsOrNull();
        } catch (IOException e) {
            throw new OperationNotSupportedException("Ошибка при чтении файла, проверьте путь.");
        }
        serializeReport();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearReport that = (YearReport) o;
        return year == that.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year);
    }

    @Override
    public String getReportText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year);
        stringBuilder.append(":\n");
        for (var month : monthsAmounts) {
            stringBuilder.append(month.toString());
        }
        return stringBuilder.toString();
    }

    public int getYear() {
        return year;
    }
    @Override
    protected void serializeReport() {
        if (!isReportFileRead) { return; }

        String[] lines = getFileContents().split("\n");

        for (int i = 1; i < lines.length; i++) {
            String[] itemValues = lines[i].split(",");
            try {
                var item = serializeMonthItem(itemValues);
                monthsAmounts.add(item);
            } catch (NumberFormatException e) {
                monthsAmounts = new ArrayList<>();
                throw new NumberFormatException(e.getMessage());
            }
        }
    }

    private MonthItem serializeMonthItem(String[] monthItemValues) {
        int month = Integer.parseInt(monthItemValues[0]);
        int amount = Integer.parseInt(monthItemValues[1]);
        if (!monthItemValues[2].equalsIgnoreCase("true")
                && !monthItemValues[2].equalsIgnoreCase("false")) {
            throw new NumberFormatException("isExpense not a boolean value in file");
        }
        boolean isExpense = Boolean.parseBoolean(monthItemValues[2]);
        return new MonthItem(month, amount, isExpense);
    }
}
