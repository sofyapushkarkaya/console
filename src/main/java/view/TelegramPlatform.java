package view;

import logic.BelieveGame;
import model.Answer;
import model.Question;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import java.util.ArrayList;
import java.util.List;

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
        else if (update.hasCallbackQuery()) {
            String userId = update.getCallbackQuery().getMessage().getChatId().toString();
            String callbackData = update.getCallbackQuery().getData();
            String answer = callbackData.replace("answer_", "");
            try {
                execute(new AnswerCallbackQuery(update.getCallbackQuery().getId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            game.onMessageReceived(userId, answer);
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

    @Override
    public void sendQuestionWithButtons(String userId, Question question) {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(question.question());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Answer ans : question.answers()) {
            InlineKeyboardButton btn = new InlineKeyboardButton();

            String emoji = switch (ans.emoji()) {
                case OK -> "✅ ";
                case CROSS -> "❌ ";
                case GIVEUP -> "🏳️ ";
            };

            btn.setText(emoji + ans.answer());
            btn.setCallbackData("answer_" + ans.answer().toLowerCase());

            row.add(btn);
        }

        rows.add(row);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}