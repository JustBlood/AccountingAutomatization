package model;

import java.util.List;

public class YearReportModel {
    public static class MonthInfo {
        private final int month;
        private final int expense;
        private final int profit;

        public MonthInfo(int month, int expense, int profit) {
            this.month = month;
            this.expense = expense;
            this.profit = profit;
        }

        public int getMonth() {
            return month;
        }

        public int getExpense() {
            return expense;
        }

        public int getProfit() {
            return profit;
        }
    }
    private final int year;
    private final List<MonthInfo> monthlyInformation;

    public YearReportModel(int year, List<MonthInfo> monthlyInformation) {
        this.year = year;
        this.monthlyInformation = monthlyInformation;
    }

    public List<MonthInfo> getMonthlyInformation() {
        return monthlyInformation;
    }

    public int getYear() {
        return year;
    }
}
