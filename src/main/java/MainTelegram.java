import logic.BelieveGame;
import logic.Difficulty;
import logic.userSession;
import view.TelegramPlatform;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainTelegram {
    public static void main(String[] args) {
        try {
            BelieveGame game = new BelieveGame(null);
            String botUsername = "my_believe_game_bot";
            TelegramPlatform platform = new TelegramPlatform(game, botUsername);
            game.setPlatform(platform);

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(platform);

            System.out.println("Telegram-bot is working");

            //фоновый таймер
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                for (Map.Entry<String, userSession> entry : game.getSessions().entrySet()) {
                    String userId = entry.getKey();
                    userSession session = entry.getValue();

                    if (session.getDifficulty() == Difficulty.HARD
                            && session.getCurrentStatement() != null
                            && session.isTimeOut()) {
                        game.handleTimeout(userId, session);
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}