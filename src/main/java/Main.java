import logic.BelieveGame;
import view.BotPlatform;
import view.LocalChatPlatform;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Создаем платформу для консоли
        //Был view который ждал ввода пользователя
        //Тк переделано для бота, кdоторый не висит в ожидании
        //то платформ умеет только отправлять ответы
        BotPlatform platform = new LocalChatPlatform();

        BelieveGame game = new BelieveGame(platform);
        Scanner scanner = new Scanner(System.in);
        String userId = "LocalUser"; //В консоли один пользователь

        System.out.println("Добро пожаловать в консольную игру!");
        System.out.println("Введите любое сообщение, чтобы начать (или /exit для выхода).");

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("/exit") || input.equalsIgnoreCase("\\exit")) {
                System.out.println("Выход из игры. Пока!");
                break;
            }

            //Передаем ввод в игру
            game.onMessageReceived(userId, input);
        }
    }
}