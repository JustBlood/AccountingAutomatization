package ui;

import bl.ReportsManager;

import javax.naming.OperationNotSupportedException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleInterface {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Scanner sc = new Scanner(System.in);
    private static final File folder = new File("src\\main\\resources\\");
    private static final ReportsManager reportsManager = new ReportsManager(folder);
    //todo: сделать консольное отображение
    public static void main(String[] args) {
        printStartMessage();
        while (true) {
            System.out.print("Введите команду: ");
            var command = readUserCommand();
            if (Objects.isNull(command)) {
                continue;
            }
            switch (command) {
                case ReadMonthReports -> {
                    readMonthlyReports();
                } case ReadYearReport -> {
                    readYearReport();
                } case CompareReports -> {
                    compareReports();
                } case ShowMonthsInfo -> {
                    showMonthsInfo();
                } case  ShowYearInfo -> {
                    showYearInfo();
                } case Help -> {
                    printHelpMessage();
                } case Exit -> {
                    System.out.println("Хорошего дня!");
                    System.exit(0);
                }
            }
        }
    }

    private static void readYearReport() {
        try {
            reportsManager.readYearReport();
            System.out.println(ANSI_GREEN + "Годовой отчёт был успешно прочитан" + ANSI_RESET);
        } catch (IOException | NumberFormatException e) {
            printErrorMessage("Ошибка при чтении файла. Проверьте формат и поля файла годового отчёта");
        }

    }

    private static void readMonthlyReports() {
        try {
            reportsManager.readMonthlyReports();
            System.out.println(ANSI_GREEN + "Месячные отчёты были успешно прочитаны" + ANSI_RESET);
        } catch (IOException | NumberFormatException e) {
            printErrorMessage("Ошибка при чтении файла. Проверьте формат и поля файлов с месячными отчётами");
        }
    }

    private static void showYearInfo() {
        if (!reportsManager.isYearReportRead()) {
            printErrorMessage("Годовые отчёты ещё не были прочитаны. Сначала прочитайте годовые отчёты.");
            printErrorMessage("Операция отменена");
            return;
        }

        var yearReport = reportsManager.getYearReportInformation();
        System.out.println(yearReport.getYear() + " год.");
        for (var monthInfo : yearReport.getOperationsProfit()) {
            System.out.printf("""
                    🗓 Месяц: %s, прибыль: %s
                    """, monthInfo.getName(), monthInfo.getSum());
        }
        System.out.printf("""
                
                📉 Средний расход в месяц: %.2f
                """, yearReport.getAverageYearlyExpense());
        System.out.printf("""
                📈 Средний доход в месяц: %.2f
                """, yearReport.getAverageYearlyProfit());
    }

    private static void showMonthsInfo() {
        if (!reportsManager.isMonthlyReportsRead()) {
            printErrorMessage("Месячные отчёты ещё не были прочитаны. Сначала прочитайте месячные отчёты.");
            printErrorMessage("Операция отменена");
            return;
        }

        var monthlyReports = reportsManager.getMonthlyReportsInformation();
        for (var monthReport : monthlyReports) {
            var mostProfitable = monthReport.getMostProfitable();
            var mostUnprofitable = monthReport.getMostUnprofitable();
            String monthName = monthReport.getMonth().substring(0, 1).toUpperCase()
                    + monthReport.getMonth().substring(1);
            System.out.printf("""
                
                🗓 %s:
                💰 Самый прибыльный товар: %s, прибыль: %s
                📉 Самая большая трата: %s, расход: %s
                
                """, monthName,
                    mostProfitable.getName(), mostProfitable.getSum(),
                    mostUnprofitable.getName(), mostUnprofitable.getSum());
        }
        System.out.println(ANSI_GREEN + "Вывод окончен." + ANSI_RESET);
    }

    private static void compareReports() {
        if (!reportsManager.isMonthlyReportsRead() || !reportsManager.isYearReportRead()) {
            if (!reportsManager.isMonthlyReportsRead()) {
                printErrorMessage("Месячные отчёты ещё не были прочитаны. Сначала прочитайте месячные отчёты.");
                printErrorMessage("Операция отменена");
            }
            if (!reportsManager.isYearReportRead()) {
                printErrorMessage("Годовые отчёты ещё не были прочитаны. Сначала прочитайте годовые отчёты.");
                printErrorMessage("Операция отменена");
            }
            return;
        }


        var mistakeMonths = reportsManager.findReportsMistake();
        if (mistakeMonths.size() == 0) {
            System.out.println(ANSI_GREEN + "Ошибок в отчётах не найдено." + ANSI_RESET);
            return;
        }

        for (var mistake : mistakeMonths) {
            System.out.printf(ANSI_RED + "Ошибка в месяце %s\n" + ANSI_RESET, mistake.getMonth());
        }
    }

    public static Command readUserCommand() {
        String commandString = sc.nextLine();
        Command command;
        try {
            command = Command.values()[Integer.parseInt(commandString) - 1];
            return command;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            for (var curCommand : Command.values()) {
                if (curCommand.getTitle().equalsIgnoreCase(commandString)) {
                    return curCommand;
                }
            }
        }
        printErrorMessage(String.format(
                "Неизвестная команда %s. Введите \"Помощь\" для просмотра доступных команд",commandString));
        return null;
    }

    private static void printErrorMessage(String description) {
        System.out.printf(ANSI_RED + "\nОшибка. %s\n" + ANSI_RESET, description);
    }

    public static void printStartMessage() {
        System.out.println("""
                            📆Доброго времени суток. Вас приветствует приложение "Автоматизация бухгалтерии"📆
                                Если Вы здесь впервые, введите "Помощь" для получения списка всех команд.
                """);
    }
    public static void printHelpMessage() {
        System.out.println("""
                                                            🔧ПОМОЩЬ🔧
                Доступные команды:
                1. 📑 "Считать месячные отчёты" - считывает все отчёты из заранее известной папки.
                2. 🧾 "Считать годовой отчёт" - считывает годовой отчёт из заранее известной папки
                3. 📊 "Сверить отчёты" - сверяет данные в месячных и годовом отчётах. Выводит список месяцев, где была допущена ошибка
                4. 📒 "Показать месячные отчёты" - отображает информацию из всех месячных отчётов
                5. 📕 "Показать годовой отчёт" - отображает информацию из годового отчёта
                6. ❓ "Помощь" - выводит данное сообщение
                7. 🚪 "Выход" - завершает работу приложения
                
                            Также Вы можете ввести просто номер команды из списка, если Вам так будет удобнее.
                                                        Удачного пользования.
                """);
    }
}
