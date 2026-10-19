package logic;

import data.StatementRepository;
import model.Statement;
import data.StatementData;
import data.FilteredStatementRepository;
import view.ConsoleView;

import java.util.List;

public class BelieveGame {
    private StatementRepository repository;
    private final ConsoleView view;
    private Statement currentStatement;

    private int correctAnswers = 0;
    private int totalAnswers = 0;
    private int wrongStreak = 0;

    public BelieveGame(StatementRepository repository, ConsoleView view) {
        this.repository = repository;
        this.view = view;
    }

    public void start() {
        view.print("Привет! Я бот 'Верю/Не верю'. Я буду говорить факт, а ты отвечай 'верю' или 'не верю'. Напиши '\\help' для справки или '\\exit' для выхода.");

        while (true) {
            //проверка если фактов вообще нет в списке
            if (!repository.hasNext()) {
                view.print("Нет ни одного факта для игры. Завершаю работу.");
                break;
            }

            Statement statement = repository.getNextStatement();
            view.print(statement.getText());

            String answer = view.readInput();

            //команда выхода
            if (answer.equals("\\exit") || answer.equals("\\quit")) {
                view.print("Спасибо за игру! Пока!");
                break;
            }

            if (answer.equals("\\help")) {
                view.printHelp();
                continue;
            }
            if (answer.equals("\\category")) {
                selectCategory();
                currentStatement = null;
                wrongStreak = 0;
                continue;
            }


            //чтоб все под одну гребенку было
            answer = answer.trim().toLowerCase();

            boolean isCorrect = false;

            if (answer.equals("верю")) {
                isCorrect = currentStatement.isTrue();
            } else if (answer.equals("не верю") || answer.equals("неверю")) {
                isCorrect = !currentStatement.isTrue();
            } else {
                view.print("Я не понял. Напиши 'верю' или 'не верю'.");
                continue;
            }
            totalAnswers++;
            if (isCorrect) {
                correctAnswers++;
                wrongStreak = 0;
                if (currentStatement.isTrue()) {
                    view.print("Верно! Это правда. " + currentStatement.getExplanation());
                } else {
                    view.print("Верно! Это ложь. " + currentStatement.getExplanation());
                }
            } else {
                wrongStreak++;
                if (currentStatement.isTrue()) {
                    view.print("Неверно! Это правда. " + currentStatement.getExplanation());
                } else {
                    view.print("Неверно! Это ложь. " + currentStatement.getExplanation());
                }

                if (wrongStreak >= 3) {
                    view.print("Вы допустили 3 ошибки подряд. Игра окончена!");
                    break;
                }
            }

            currentStatement = null;
        }

        printFinalStatistics();
    }

    private void selectCategory() {
        List<String> categories = StatementData.getAllCategories();

        view.print("");
        view.print("Выбери категорию:");
        for (int i = 0; i < categories.size(); i++) {
            view.print((i + 1) + ". " + categories.get(i));
        }
        view.print((categories.size() + 1) + ". Все");
        view.print("Введи номер:");

        String input = view.readInput();
        int choice;

        try {
            choice = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            view.print("Неверный ввод. Играю со всеми фактами.");
            choice = categories.size() + 1;
        }

        String category;
        if (choice >= 1 && choice <= categories.size()) {
            category = categories.get(choice - 1);
        } else {
            category = null;
        }

        this.repository = new FilteredStatementRepository(
                StatementData.getAllStatements(), category);

        String catName = (category == null) ? "Все" : category;
        view.print("Отлично! Категория: " + catName);
        view.print("");
    }

    private void printFinalStatistics() {
        view.print("");
        view.print("Итоги игры:");
        view.print("У вас " + correctAnswers + " из " + totalAnswers + " правильных ответов.");

        if (totalAnswers > 0) {
            int percent = (int) Math.round((double) correctAnswers / totalAnswers * 100);
            view.print("Точность: " + percent + "%");
        }
    }
}
