import logic.BelieveGame;
import view.TelegramPlatform;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Создаем игру без платформы
            BelieveGame game = new BelieveGame(null);

            // 2. Создаем платформу
            String botUsername = "BelieveGameBot";
            TelegramPlatform platform = new TelegramPlatform(game, botUsername);

            // 3. ПРИВЯЗЫВАЕМ платформу к игре
            game.setPlatform(platform);

            // 4. Регистрируем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(platform);

            System.out.println("Бот запущен! Напишите ему в Telegram: @" + botUsername);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}