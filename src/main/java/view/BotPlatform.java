package view;

public interface BotPlatform {
    void sendMessage(String userId, String text);

    //метод для вопросов с кнопками
    void sendQuestionWithButtons(String userId, String questionText);
}
