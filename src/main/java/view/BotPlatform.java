package view;
import model.Question;

public interface BotPlatform {
    void sendMessage(String userId, String text);

    void sendQuestionWithButtons(String userId, Question question);
}