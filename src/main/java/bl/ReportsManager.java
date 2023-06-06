package bl;

import model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class ReportsManager {
    public static class Operation {
        public Operation(String name, int sum) {
            this.name = name;
            this.sum = sum;
        }

        private final String name;
        private final int sum;

        public int getSum() {
            return sum;
        }

        public String getName() {
            return name;
        }
    }
    public static class MonthReportInformation {
        public MonthReportInformation(String month, Operation mostProfitable, Operation mostUnprofitable) {
            this.month = month;
            this.mostProfitable = mostProfitable;
            this.mostUnprofitable = mostUnprofitable;
        }

        private final String month;
        private final Operation mostProfitable;
        private final Operation mostUnprofitable;

        public String getMonth() {
            return month;
        }

        public Operation getMostProfitable() {
            return mostProfitable;
        }

        public Operation getMostUnprofitable() {
            return mostUnprofitable;
        }
    }
    public static class YearReportInformation {
        private final int year;
        private final List<Operation> operationsProfit;
        private final double averageYearlyProfit;
        private final double averageYearlyExpense;

        public YearReportInformation(int year, List<Operation> operationsProfit, double averageYearlyProfit,
                                     double averageYearlyExpense) {
            if (operationsProfit.size() > 12) {
                throw new IllegalArgumentException("OperationsProfit contains maximum 12 values - 1 for month in year");
            }
            this.year = year;
            this.operationsProfit = operationsProfit;
            this.averageYearlyProfit = averageYearlyProfit;
            this.averageYearlyExpense = averageYearlyExpense;
        }

        public int getYear() {
            return year;
        }

        public List<Operation> getOperationsProfit() {
            return operationsProfit;
        }

        public double getAverageYearlyProfit() {
            return averageYearlyProfit;
        }

        public double getAverageYearlyExpense() {
            return averageYearlyExpense;
        }
    }
    private ReportsModel reportsModel;
    private boolean isMonthlyReportsRead;
    private boolean isYearReportRead;
    private final File folder;
    public ReportsManager(File folder) {
        this.folder = folder;
        reportsModel = new ReportsModel();
    }

    public void readMonthlyReports() throws IOException {
        var files = folder.listFiles();
        if (Objects.isNull(files)) {
            throw new IOException("There is no files in directory.");
        }
        var test = Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toArray();
        for (int i = 0; i < test.length; i++) {
            files[i] = (File) test[i];
        }
        //files = Arrays.stream(files).sorted(Comparator.comparing(File::getName)).toArray();

        int i = 0;
        for (var file : files) {
            if (file.getName().contains("m.")) {
                try {
                    MonthReportModel monthReportModel = deserializeMonthReport(
                            readFile(file.getPath()), file.getName());
                    // add month report to map
                    reportsModel.getMonthlyReports()[i++] = monthReportModel;
                } catch (IOException e) {
                    throw new IOException("Error in reading file");
                }
            }
        }
        isMonthlyReportsRead = true;
    }

    public void readYearReport() throws IOException {
        if (Objects.isNull(folder.listFiles())) {
            throw new IOException("There is no files in directory.");
        }

        int i = 0;
        for (var file : Objects.requireNonNull(folder.listFiles())) {
            if (file.getName().contains("y.")) {
                try {
                    YearReportModel yearReportModel = deserializeYearReport(readFile(file.getPath()), file.getName());
                    // add month report to map
                    reportsModel = new ReportsModel(yearReportModel, reportsModel.getMonthlyReports());
                } catch (IOException e) {
                    throw new IOException("Error in reading file");
                }
            }
        }
        isYearReportRead = true;
    }

    /**
     * @return list of months, where was detected mistake
     */
    public List<MonthReportModel> findReportsMistake() {
        // comparing monthly reports with year reports
        List<MonthReportModel> mistakesMonths = new ArrayList<>();
        var yearReportByMonths = reportsModel.getYearReport().getMonthlyInformation();
        var monthlyReports = reportsModel.getMonthlyReports();
        for (int i = 0; i < monthlyReports.length; i++) {
            if (yearReportByMonths.size() <= i && !Objects.isNull(monthlyReports[i])) {
                mistakesMonths.add(monthlyReports[i]);
                continue;
            } else if (Objects.isNull(monthlyReports[i])) {
                continue;
            }

            var operations = monthlyReports[i].getBalanceOperations();
            int monthProfit = 0;
            int monthExpense = 0;
            for (var operation : operations) {
                int realProfit = operation.getRealProfit();
                if (realProfit < 0) {
                    monthExpense -= realProfit;
                } else {
                    monthProfit += realProfit;
                }
            }

            var monthInfoByYear = yearReportByMonths.get(i);
            if (monthInfoByYear.getExpense() != monthExpense
            || monthInfoByYear.getProfit() != monthProfit) {
                mistakesMonths.add(monthlyReports[i]);
            }
        }
        return mistakesMonths;
    }

    public List<MonthReportInformation> getMonthlyReportsInformation() {
        var monthlyReports = reportsModel.getMonthlyReports();
        List<MonthReportInformation> monthlyReportsInformation = new ArrayList<>();
        for (MonthReportModel monthlyReport : monthlyReports) {
            if (Objects.isNull(monthlyReport)) {
                continue;
            }
            String month = getMonthName(monthlyReport.getMonth());
            var profitable = monthlyReport.getMostProfitable();
            var unprofitable = monthlyReport.getMostUnprofitable();
            Operation mostProfitable = new Operation(profitable.getName(), profitable.getRealProfit());
            Operation mostUnprofitable = new Operation(unprofitable.getName(), Math.abs(unprofitable.getRealProfit()));
            monthlyReportsInformation.add(new MonthReportInformation(month, mostProfitable, mostUnprofitable));
        }
        return monthlyReportsInformation;
    }
    public YearReportInformation getYearReportInformation() {
        var monthsInfo = reportsModel.getYearReport().getMonthlyInformation();
        double averageProfit = 0;
        double averageExpense = 0;
        List<Operation> monthOperationsProfit = new ArrayList<>();
        for (var monthInfo : monthsInfo) {
            averageExpense += monthInfo.getExpense();
            averageProfit += monthInfo.getProfit();
            monthOperationsProfit.add(new Operation(getMonthName(monthInfo.getMonth()), monthInfo.getRealProfit()));
        }
        averageExpense /= monthsInfo.size();
        averageProfit /= monthsInfo.size();
        return new YearReportInformation(
                reportsModel.getYearReport().getYear(),
                monthOperationsProfit,
                averageProfit,
                averageExpense);
    }

    public boolean isMonthlyReportsRead() {
        return isMonthlyReportsRead;
    }

    public boolean isYearReportRead() {
        return isYearReportRead;
    }

    private String getMonthName(int month) {
        return Month.of(month).getDisplayName(
                TextStyle.FULL_STANDALONE, Locale.of("ru"));
    }

    private String readFile(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    private MonthReportModel deserializeMonthReport(String fileContent, String fileName) {
        String[] lines = fileContent.split("\n");

        int month = Integer.parseInt(fileName.substring(6,8));
        int year = Integer.parseInt(fileName.substring(2,6));
        List<MonthReportModel.BalanceOperation> balanceOperations = new ArrayList<>();
        MonthReportModel.BalanceOperation mostProfitable = null;
        MonthReportModel.BalanceOperation mostUnprofitable = null;
        for (int i = 1; i < lines.length; i++) {
            String[] itemValues = lines[i].split(",");
            try {
                // deserialize BalanceOperation
                String name = itemValues[0];
                if (!itemValues[1].equalsIgnoreCase("true")
                        && !itemValues[1].equalsIgnoreCase("false")) {
                    throw new NumberFormatException("isExpense not a boolean value in file");
                }
                boolean isExpense = Boolean.parseBoolean(itemValues[1]);
                int quantity = Integer.parseInt(itemValues[2]);
                int priceOfOne = Integer.parseInt(itemValues[3]);

                MonthReportModel.BalanceOperation operation = new MonthReportModel.BalanceOperation(
                        name, quantity, priceOfOne, isExpense);

                if (Objects.isNull(mostUnprofitable) || operation.getRealProfit() < mostUnprofitable.getRealProfit()) {
                    mostUnprofitable = operation;
                }
                if (Objects.isNull(mostProfitable) || operation.getRealProfit() > mostProfitable.getRealProfit()) {
                    mostProfitable = operation;
                }

                balanceOperations.add(operation);
            } catch (NumberFormatException e) {
                throw new NumberFormatException(e.getMessage());
            }
        }
        return new MonthReportModel(month, year, balanceOperations, mostProfitable, mostUnprofitable);
    }
    private YearReportModel deserializeYearReport(String fileContent, String fileName) {
        String[] lines = fileContent.split("\n");

        int year = Integer.parseInt(fileName.substring(2,6));
        List<YearReportModel.MonthInfo> monthlyInformation = new ArrayList<>();

        for (int i = 1; i < lines.length; i+=2) {
            int month;
            int expense;
            int profit;

            String[] operations1 = lines[i].split(",");
            String[] operations2 = lines[i + 1].split(",");

            try {
                // validate year report file
                validateMonthDataInYearReportFile(operations1, operations2);

                month = Integer.parseInt(operations1[0]);
                int amount1 = Integer.parseInt(operations1[1]);
                int amount2 = Integer.parseInt(operations2[1]);


                boolean isExpense1 = Boolean.parseBoolean(operations1[2]);
                if (isExpense1) {
                    expense = amount1;
                    profit = amount2;
                } else {
                    expense = amount2;
                    profit = amount1;
                }
                monthlyInformation.add(new YearReportModel.MonthInfo(month, expense, profit));
            } catch (NumberFormatException e) {
                throw new NumberFormatException(e.getMessage());
            }
        }

        return new YearReportModel(year, monthlyInformation);
    }

    /**
     * throws NumberFormatException when find any errors in year report file
     * @param operations1 first line operations (profit or expense) in year report by same month
     * @param operations2 second line operations (profit or expense) in year report by same month
     */
    private void validateMonthDataInYearReportFile(String[] operations1, String[] operations2) {
        if ((!operations1[2].equalsIgnoreCase("true")
                && !operations1[2].equalsIgnoreCase("false"))
                || (!operations2[2].equalsIgnoreCase("true")
                && !operations2[2].equalsIgnoreCase("false"))) {
            throw new NumberFormatException("isExpense not a boolean value in file");
        }
        boolean isExpense1 = Boolean.parseBoolean(operations1[2]);
        boolean isExpense2 = Boolean.parseBoolean(operations2[2]);
        if (isExpense1 == isExpense2) {
            throw new NumberFormatException("Can't be 2 expenses/profit lines for month");
        }
        if (!operations2[0].equals(operations1[0])) {
            throw new NumberFormatException("Bad file format. Need 2 lines by month report");
        }
    }
}
