import data.StatementData;
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
    @Override
    public void sendQuestionWithButtons(String userId, String questionText) {
        messages.add(userId + ": " + questionText);
    }
}

public class BelieveGameTest {
    private BelieveGame game;
    private FakeBotPlatform fakePlatform;

    @BeforeEach
    public void setUp() {
        fakePlatform = new FakeBotPlatform();
        game = new BelieveGame(fakePlatform);
    }

    // 1. Проверка добавления факта
    @Test
    public void testAddFact() {
        game.onMessageReceived("User1", "/add_fact");
        // Шаг 1: Категория (выбираем 1 = Животные)
        game.onMessageReceived("User1", "1");
        // Шаг 2: Текст факта
        game.onMessageReceived("User1", "Новый факт про животных");
        // Шаг 3: Правда/Ложь
        game.onMessageReceived("User1", "правда");
        // Шаг 4: Пояснение
        game.onMessageReceived("User1", "Это пояснение");

        // Проверяем, что бот подтвердил добавление
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("Факт успешно добавлен")));
    }

    // 2. Проверка команды /help
    @Test
    public void testHelpCommand() {
        game.onMessageReceived("User1", "/help");
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("ПРАВИЛА ИГРЫ")));
    }

    // 3. Проверка команды /restart
    @Test
    public void testRestartCommand() {
        // Начинаем игру, чтобы статистика была не нулевая
        game.onMessageReceived("User1", "привет");
        game.onMessageReceived("User1", "1");
        game.onMessageReceived("User1", "верю"); // Даем один ответ

        game.onMessageReceived("User1", "/restart");

        // Проверяем, что бот показал "Игра перезапущена" и предложил выбрать категорию
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("Игра перезапущена")));
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("Выбери категорию")));
    }

    // 4. Проверка команды /stats
    @Test
    public void testStatsCommand() {
        game.onMessageReceived("User1", "привет");
        game.onMessageReceived("User1", "1");
        game.onMessageReceived("User1", "верю");

        game.onMessageReceived("User1", "/stats");

        // Проверяем, что бот вывел статистику
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("----------СТАТИСТИКА")));
    }

    // 6. Проверка одновременного добавления фактов (защита от гонки данных)
    @Test
    public void testConcurrentAddFact() throws InterruptedException {
        // Запускаем два потока, которые одновременно пытаются добавить факты
        Thread t1 = new Thread(() -> {
            game.onMessageReceived("User1", "/add_fact");
            game.onMessageReceived("User1", "1");
            game.onMessageReceived("User1", "Факт 1");
            game.onMessageReceived("User1", "правда");
            game.onMessageReceived("User1", "Пояснение 1");
        });

        Thread t2 = new Thread(() -> {
            game.onMessageReceived("User2", "/add_fact");
            game.onMessageReceived("User2", "2");
            game.onMessageReceived("User2", "Факт 2");
            game.onMessageReceived("User2", "ложь");
            game.onMessageReceived("User2", "Пояснение 2");
        });

        t1.start();
        t2.start();
        t1.join(); // Ждем завершения потоков
        t2.join();

        // Проверяем, что оба факта добавились без ошибок
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("Факт успешно добавлен")));
    }
}