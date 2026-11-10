package view;

public interface BotPlatform {
    void sendMessage(String userId, String text);
    void sendHelp(String userId);
}
