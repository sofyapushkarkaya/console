package view;

import logic.BelieveGame;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramPlatform extends TelegramLongPollingBot implements BotPlatform {

    private final BelieveGame game;
    private final String botUsername;

    public TelegramPlatform(BelieveGame game, String botUsername) {
        this.game = game;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        // Читаем токен из переменной окружения BOT_TOKEN
        String token = System.getenv("BOT_TOKEN");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Не задана переменная окружения BOT_TOKEN! Настройте её в Run Configuration.");
        }
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            game.onMessageReceived(userId, text);
        }
    }

    @Override
    public void sendMessage(String userId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}