package reports;

import jdk.jshell.spi.ExecutionControl;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonthlyReport extends Report {
    public static class Item {
        private boolean isExpense;
        private String name;
        private int quantity;
        private int priceOfOne;

        public int calculateSum() {
            return quantity * priceOfOne * (isExpense ? -1 : 1);
        }

        @Override
        public String toString() {
            return String.format("Запись %s, цена/ед: %s, кол-во: %s, общая стоимость: %s",
                    name, priceOfOne, quantity, calculateSum());
        }
    }
    private int month;
    private int year;
    private List<Item> items;
    private Item mostExpensive;
    private Item cheaper;

    public MonthlyReport(String path) {
        super(path);
        items = new ArrayList<>();
    }
    private void createReport() throws OperationNotSupportedException {
        try {
            readFileContentsOrNull();
        } catch (IOException e) {
            throw new OperationNotSupportedException("Ошибка при чтении файла, проверьте путь.");
        }
        serializeReport();
        try {
            calculateMaxMinPriceFromData();
        } catch (OperationNotSupportedException e) {
            throw new OperationNotSupportedException("Ошибка при подсчёте максимальной и минимальной транзакции." +
                    "Сначала необходимо сериализовать данные");
        }
    }

    //region getters
    public Item getMostExpensive() {
        return mostExpensive;
    }

    public Item getCheaper() {
        return cheaper;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
    //endregion

    /**
     * Need @createReport() method before this called.
     * @return String, which describe this month report
     */
    public String getReport() throws OperationNotSupportedException {
        createReport();
        return null;
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
            if (Objects.isNull(mostExpensive) || Objects.isNull(cheaper)) {
                mostExpensive = item;
                cheaper = item;
            }
            if (sum > mostExpensive.calculateSum()) {
                mostExpensive = item;
            }
            if (sum < cheaper.calculateSum()) {
                cheaper = item;
            }
        }
    }

    private Item serializeItem(String[] itemValues) {
        // item_name,is_expense,quantity,sum_of_one
        Item item = new Item();
        item.name = itemValues[0];
        if (!itemValues[1].equalsIgnoreCase("true")
        && !itemValues[1].equalsIgnoreCase("false")) {
            throw new NumberFormatException("isExpense not boolean value in file");
        }
        item.isExpense = Boolean.parseBoolean(itemValues[1]);
        item.quantity = Integer.parseInt(itemValues[2]);
        item.priceOfOne = Integer.parseInt(itemValues[3]);
        return item; // serialize next Item
    }
}
