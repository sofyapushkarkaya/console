import data.FilteredStatementRepository;
import data.StatementData;
import data.StatementRepository;
import logic.BelieveGame;
import view.BotPlatform; // ИЗМЕНЕНО: Импортируем BotPlatform
import view.LocalChatPlatform; // ИЗМЕНЕНО: Импортируем LocalChatPlatform

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // ИЗМЕНЕНО: Создаем платформу для консоли
        BotPlatform platform = new LocalChatPlatform();

        // ОСТАВЛЕНО: Ваш код создания репозитория
        StatementRepository initialRepository = new FilteredStatementRepository(
                StatementData.getAllStatements(),
                null
        );

        // ИЗМЕНЕНО: Передаем platform в игру вместо view
        BelieveGame game = new BelieveGame(initialRepository, platform);

        // ИЗМЕНЕНО: Весь цикл while перенесен сюда из BelieveGame
        Scanner scanner = new Scanner(System.in);
        String userId = "LocalUser"; // В консоли один пользователь

        System.out.println("Добро пожаловать в консольную игру!");
        System.out.println("Введите любое сообщение, чтобы начать (или /exit для выхода).");

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("/exit") || input.equalsIgnoreCase("\\exit")) {
                System.out.println("Выход из игры. Пока!");
                break;
            }

            // ИЗМЕНЕНО: Передаем ввод в игру
            game.onMessageReceived(userId, input);
        }
    }
}