import logic.BelieveGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.BotPlatform;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeBotPlatform implements BotPlatform {
    public List<String> messages = new ArrayList<>();
    @Override
    public void sendMessage(String userId, String text) {
        messages.add(userId + ": " + text);
    }
}

public class BelieveGameTest {
    private BelieveGame game;
    private FakeBotPlatform fakePlatform;

    @BeforeEach
    public void setUp() {
        fakePlatform = new FakeBotPlatform();
        // Теперь конструктор принимает ТОЛЬКО платформу
        game = new BelieveGame(fakePlatform);
    }

    @Test
    public void testTwoUsersPlayIndependently() {
        // 1. Пользователи заходят и выбирают категории
        game.onMessageReceived("Вася", "привет");
        game.onMessageReceived("Вася", "1"); // Выбирает Животные
        game.onMessageReceived("Петя", "привет");
        game.onMessageReceived("Петя", "3"); // Выбирает Технику

        // Проверяем, что категории выбраны верно
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.startsWith("Вася: Отлично! Категория: Животные")));
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.startsWith("Петя: Отлично! Категория: Техника")));

        // Очищаем историю, чтобы проверять только ответы
        fakePlatform.messages.clear();

        // 2. Заставляем Васю ошибаться 3 раза подряд.
        // Для этого смотрим, какой факт ему выпал, и отвечаем НАОБОРОТ.
        for (int i = 0; i < 3; i++) {
            // Достаем последнее сообщение (вопрос), которое игра отправила Васе
            String lastMessageToVasya = fakePlatform.messages.stream()
                    .filter(m -> m.startsWith("Вася: "))
                    .reduce((first, second) -> second) // берем последнее
                    .orElse("");

            // Если факт правдивый (в сообщении нет слова "ложь", но мы точно знаем из StatementData,
            // что первые два факта про животных - правда, а третий - ложь).
            // Чтобы не гадать, просто заставим его ошибаться на основе текста.

            // В StatementData:
            // 1. Осьминог (правда)
            // 2. Акула (правда)
            // 3. День пауков (ложь)

            String wrongAnswer;
            if (lastMessageToVasya.contains("день пауков")) {
                wrongAnswer = "верю"; // Если факт ложный, а мы верим - это ошибка
            } else {
                wrongAnswer = "не верю"; // Если факт правдивый, а мы не верим - это ошибка
            }

            game.onMessageReceived("Вася", wrongAnswer);
        }

        // Проверяем, что Вася проиграл
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("3 ошибки подряд")), "Вася должен был проиграть");

        // 3. Проверяем, что Петя всё еще может играть
        fakePlatform.messages.clear();
        game.onMessageReceived("Петя", "верю");

        // Петя не должен получить сообщение об окончании игры
        assertFalse(fakePlatform.messages.stream().anyMatch(m -> m.contains("Игра окончена")), "Петя не должен был проиграть");
    }
}