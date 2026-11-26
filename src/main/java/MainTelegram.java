import logic.BelieveGame;
import view.TelegramPlatform;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MainTelegram {
    public static void main(String[] args) {
        try {
            // 1. Создаем игру без платформы
            BelieveGame game = new BelieveGame(null);

            // 2. Создаем Telegram-платформу
            String botUsername = "my_believe_game_bot";
            TelegramPlatform platform = new TelegramPlatform(game, botUsername);

            // 3. Связываем игру и платформу
            game.setPlatform(platform);

            // 4. Регистрируем бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(platform);

            System.out.println("Telegram-бот запущен! Напишите ему: @" + botUsername);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}