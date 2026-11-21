import logic.BelieveGame;
import view.BotPlatform;
import view.TelegramPlatform;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите режим запуска:");
        System.out.println("1. Консоль");
        System.out.println("2. Telegram");
        System.out.print("Ваш выбор (1 или 2): ");

        String choice = scanner.nextLine();

        if (choice.equals("2")) {
            startTelegram();
        } else {
            startConsole(scanner);
        }
    }

    private static void startConsole(Scanner scanner) {
        // Создаем простую платформу для вывода в консоль
        BotPlatform platform = (userId, text) -> System.out.println("Бот: " + text);
        BelieveGame game = new BelieveGame(platform);

        System.out.println("\n консольный режим");
        System.out.println("Напиши 'привет', чтобы начать:");

        while (true) {
            String input = scanner.nextLine();
            game.onMessageReceived("Player1", input);
        }
    }

    private static void startTelegram() {
        try {
            // 1. Создаем игру (пока без платформы)
            BelieveGame game = new BelieveGame(null);

            // 2. Создаем платформу и передаем ей игру
            String botUsername = "BelieveGameBot"; // Имя твоего бота
            TelegramPlatform platform = new TelegramPlatform(game, botUsername);

            // 3. Привязываем платформу к игре
            game.setPlatform(platform);

            // 4. Запускаем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(platform);

            System.out.println("\n telegram режим");
            System.out.println("Бот запущен! Найди его в Telegram: @" + botUsername);
        } catch (Exception e) {
            System.err.println("Ошибка при запуске: " + e.getMessage());
            e.printStackTrace();
        }
    }
}