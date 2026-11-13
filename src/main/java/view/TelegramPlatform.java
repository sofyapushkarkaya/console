package view;

import logic.BelieveGame;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramPlatform extends TelegramLongPollingBot implements BotPlatform {

    private final BelieveGame game;
    private final String botUsername;

    // Конструктор принимает игру, чтобы иметь возможность передавать ей сообщения
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
        // Вставьте сюда ваш токен от BotFather
        return "ВАШ_ТОКЕН_ЗДЕСЬ";
    }

    // Этот метод вызывается, когда пользователь пишет боту
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            // Передаём сообщение в вашу игру
            game.onMessageReceived(userId, text);
        }
    }

    // Этот метод вызывает игра, когда хочет отправить сообщение
    @Override
    public void sendMessage(String userId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(text);
        try {
            execute(message); // Отправляем в Telegram
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}