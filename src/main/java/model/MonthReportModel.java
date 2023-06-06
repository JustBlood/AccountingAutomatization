package model;

import java.util.List;

public record MonthReportModel(int month, int year, List<BalanceOperation> balanceOperations,
                               model.MonthReportModel.BalanceOperation mostProfitable,
                               model.MonthReportModel.BalanceOperation mostUnprofitable) {
    public record BalanceOperation(String name, int quantity, int priceOfOne, boolean isExpense) {

        public int getRealProfit() {
                return quantity * priceOfOne * (isExpense ? -1 : 1);
            }

            @Override
            public String toString() {
                return String.format("%s, абсолютная сумма: %s",
                        name, Math.abs(getRealProfit()));
            }
        }

}
