package view;

public class LocalChatPlatform implements BotPlatform {
    @Override
    public void sendMessage(String userId, String text) {
        System.out.println("[Бот -> " + userId + "]: " + text);
    }

    public void sendQuestionWithButtons(String userId, String questionText) {
        sendMessage(userId, questionText); // Делегируем вызов
    }
    //Здесь приписка "[Бот -> " + userId + "]Э нужна, что бы на выводе\тестах
    //было понятно кому адресовано соо
}