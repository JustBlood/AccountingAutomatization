package model;

import java.util.List;

public record YearReportModel(int year, List<MonthInfo> monthlyInformation) {
    public record MonthInfo(int month, int expense, int profit) {

        public int getRealProfit() {
                return profit - expense;
            }
        }
}
