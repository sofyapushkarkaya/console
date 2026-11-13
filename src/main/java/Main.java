import logic.BelieveGame;
import view.BotPlatform;
import view.TelegramPlatform;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Создаём игру (пока без платформы, передадим позже)
            BelieveGame game = new BelieveGame(null);

            // 2. Создаём Telegram-платформу и передаём ей игру
            String botUsername = "my_believe_game_bot"; // ВАШ username бота (без @)
            TelegramPlatform platform = new TelegramPlatform(game, botUsername);

            // 3. Регистрируем бота в Telegram
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(platform);

            System.out.println("Бот запущен! Напишите ему в Telegram: @" + botUsername);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}