package view;

import model.Answer;
import model.Question;

public class LocalChatPlatform implements BotPlatform {

    @Override
    public void sendMessage(String userId, String text) {
        System.out.println("[Бот -> " + userId + "]: " + text);
    }

    @Override
    public void sendQuestionWithButtons(String userId, Question question) {
        // Выводим сам вопрос
        sendMessage(userId, question.question());

        // Выводим варианты ответов (имитация кнопок в консоли)
        System.out.print("[Варианты ответа: ");
        for (int i = 0; i < question.answers().size(); i++) {
            Answer ans = question.answers().get(i);
            // Можно выводить просто текст ответа, например: "Верю", "Не верю"
            System.out.print("\"" + ans.answer() + "\"");

            // Ставим запятую между вариантами, но не после последнего
            if (i < question.answers().size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}