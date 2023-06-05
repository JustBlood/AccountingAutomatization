package reports;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class MonthlyReport extends Report {
    public static class Item {
        public Item(String name, int quantity, int priceOfOne, boolean isExpense) {
            this.name = name;
            this.quantity = quantity;
            this.priceOfOne = priceOfOne;
            this.isExpense = isExpense;
        }
        private boolean isExpense;
        private String name;
        private int quantity;
        private int priceOfOne;

        public int calculateSum() {
            return quantity * priceOfOne * (isExpense ? -1 : 1);
        }

        @Override
        public String toString() {
            return String.format("%s, абсолютная сумма: %s",
                    name, Math.abs(calculateSum()));
        }
    }
    private int month;
    private int year;
    private List<Item> items;
    private Item mostProfitable;
    private Item mostUnprofitable;

    public MonthlyReport(String path) {
        super(path);
        items = new ArrayList<>();
    }
    public void readReport() throws OperationNotSupportedException {
        try {
            readFileContentsOrNull();
        } catch (IOException e) {
            throw new OperationNotSupportedException("Ошибка при чтении файла, проверьте путь.");
        }
        serializeReport();
    }

    //region getters
    public Item getMostProfitable() {
        return mostProfitable;
    }

    public Item getMostUnprofitable() {
        return mostUnprofitable;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
    public int getAmount() {
        int amount = 0;
        for (var item : items) {
            amount += item.calculateSum();
        }
        return amount;
    }
    //endregion

    /**
     * @return String, which describe this month report
     */
    public String getReportText() throws OperationNotSupportedException {
        if (Objects.isNull(mostProfitable) || Objects.isNull(mostUnprofitable)) {
            try {
                calculateMaxMinPriceFromData();
            } catch (OperationNotSupportedException e) {
                throw new OperationNotSupportedException("Ошибка при подсчёте максимальной и минимальной транзакции." +
                        "Сначала необходимо сериализовать данные");
            }
        }
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, Locale.of("ru"));
        return String.format("""
                %s, %s год:
                Самый прибыльный товар: %s,
                Самая большая трата: %s
                """, monthName, year, mostProfitable, mostUnprofitable);
    }

    @Override
    protected void serializeReport() throws NumberFormatException {
        if (!isReportFileRead) { return; }

        String[] lines = getFileContents().split("\n");
        String[] path = getPathToReport().split("\\\\");
        String fileName = path[path.length - 1];
        month = Integer.parseInt(fileName.substring(6,8));
        year = Integer.parseInt(fileName.substring(2,6));

        for (int i = 1; i < lines.length; i++) {
            String[] itemValues = lines[i].split(",");
            try {
                var item = serializeItem(itemValues);
                items.add(item);
            } catch (NumberFormatException e) {
                items = new ArrayList<>();
                throw new NumberFormatException(e.getMessage());
            }
        }
    }

    private void calculateMaxMinPriceFromData() throws OperationNotSupportedException {
        // calculate needed data from all items
        if (Objects.isNull(items)) {
            throw new OperationNotSupportedException("items is null. You should serializeReport() firstly");
        }
        for (var item : items) {
            int sum = item.calculateSum();
            if (Objects.isNull(mostProfitable) || Objects.isNull(mostUnprofitable)) {
                mostProfitable = item;
                mostUnprofitable = item;
            }
            if (sum > mostProfitable.calculateSum()) {
                mostProfitable = item;
            }
            if (sum < mostUnprofitable.calculateSum()) {
                mostUnprofitable = item;
            }
        }
    }

    private Item serializeItem(String[] itemValues) {
        // item_name,is_expense,quantity,sum_of_one
        String name = itemValues[0];
        if (!itemValues[1].equalsIgnoreCase("true")
        && !itemValues[1].equalsIgnoreCase("false")) {
            throw new NumberFormatException("isExpense not a boolean value in file");
        }
        boolean isExpense = Boolean.parseBoolean(itemValues[1]);
        int quantity = Integer.parseInt(itemValues[2]);
        int priceOfOne = Integer.parseInt(itemValues[3]);
        return new Item(name, quantity, priceOfOne, isExpense);
    }
}
