package view;
import logic.BelieveGame;
import java.util.Scanner;

public class ConsoleAdapter {
    public static void main(String[] args) {
        BotPlatform platform = new LocalChatPlatform();
        // Создаем репозиторий (например, пустой или с фактами)
        data.StatementRepository repo = new data.FilteredStatementRepository(data.StatementData.getAllStatements(), null);
        BelieveGame game = new BelieveGame(repo, platform);

        Scanner scanner = new Scanner(System.in);
        String userId = "LocalUser"; // Для консоли все сообщения от одного юзера

        System.out.println("Добро пожаловать в консольную игру!");
        System.out.println("Введите любое сообщение, чтобы начать (или /exit для выхода).");

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("/exit") || input.equalsIgnoreCase("\\exit")) {
                System.out.println("Выход из игры. Пока!");
                break;
            }

            game.onMessageReceived(userId, input);
        }
    }
}
