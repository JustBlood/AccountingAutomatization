package model;

import java.util.List;

public class MonthReportModel {
    public static class BalanceOperation {
        public BalanceOperation(String name, int quantity, int priceOfOne, boolean isExpense) {
            this.name = name;
            this.quantity = quantity;
            this.priceOfOne = priceOfOne;
            this.isExpense = isExpense;
        }
        private final boolean isExpense;

        private final String name;
        private final int quantity;
        private final int priceOfOne;

        public String getName() {
            return name;
        }

        public boolean isExpense() {
            return isExpense;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getPriceOfOne() {
            return priceOfOne;
        }

        public int getRealProfit() {
            return quantity * priceOfOne * (isExpense ? -1 : 1);
        }
        @Override
        public String toString() {
            return String.format("%s, абсолютная сумма: %s",
                    name, Math.abs(getRealProfit()));
        }
    }
    public MonthReportModel(int month, int year, List<BalanceOperation> balanceOperations,
                            BalanceOperation mostProfitable, BalanceOperation mostUnprofitable) {
        this.month = month;
        this.year = year;
        this.balanceOperations = balanceOperations;
        this.mostProfitable = mostProfitable;
        this.mostUnprofitable = mostUnprofitable;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public List<BalanceOperation> getBalanceOperations() {
        return balanceOperations;
    }

    public BalanceOperation getMostProfitable() {
        return mostProfitable;
    }

    public BalanceOperation getMostUnprofitable() {
        return mostUnprofitable;
    }

    private final int month;
    private final int year;
    private final List<BalanceOperation> balanceOperations;
    private final BalanceOperation mostProfitable;
    private final BalanceOperation mostUnprofitable;
}
