package logic;

import data.StatementRepository;
import model.Statement;
import view.ConsoleView;

public class BelieveGame {
    private final StatementRepository repository;
    private final ConsoleView view;

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

            //чтоб все под одну гребенку было
            answer = answer.trim().toLowerCase();
          
            if (answer.equals("верю")) {
                if (statement.isTrue()) {
                    view.print("Верно! Это правда. " + statement.getExplanation());
                } else {
                    view.print("Неверно! Это ложь. " + statement.getExplanation());
                }
            } else if (answer.equals("не верю") || answer.equals("неверю")) {
                if (!statement.isTrue()) {
                    view.print("Верно! Это ложь. " + statement.getExplanation());
                } else {
                    view.print("Неверно! Это правда. " + statement.getExplanation());
                }
            } else {
                view.print("Я не понял. Напиши 'верю' или 'не верю'.");
            }
        }
    }
}
