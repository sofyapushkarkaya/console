package view;

import java.util.Scanner;

public class ConsoleViewImpl implements ConsoleView {
    private final Scanner scanner = new Scanner(System.in); //присваивание ввода с клавиатуры

    public void print(String text) {
        System.out.println(text);
    }

    public String readInput() {
        return scanner.nextLine();
    }

    public void printHelp() {
        print("Я - бот 'Верю/Не верю'.");
        print("Я буду говорить факт, а ты отвечай 'верю' или 'не верю'.");
        print("В любой момент можно написать '\\help' для вызова этой справки.");
    }
}
