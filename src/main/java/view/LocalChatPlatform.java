package view;

public class LocalChatPlatform implements BotPlatform {
    @Override
    public void sendMessage(String userId, String text) {
        System.out.println("[Бот -> " + userId + "]: " + text);
    }

    @Override
    public void sendHelp(String userId) {
        System.out.println("[Бот -> " + userId + "]: Я - бот 'Верю/Не верю'. Отвечай 'верю' или 'не верю'. Команды: \\help, \\exit.");
    }
}