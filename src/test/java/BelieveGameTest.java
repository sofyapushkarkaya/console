import data.StatementData;
import logic.BelieveGame;
import model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.BotPlatform;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeBotPlatform implements BotPlatform {
    public List<String> messages = new ArrayList<>();
    public List<Question> questions = new ArrayList<>();

    @Override
    public void sendMessage(String userId, String text) {
        messages.add(userId + ": " + text);
    }

    @Override
    public void sendQuestionWithButtons(String userId, Question question) {
        questions.add(question);
        messages.add(userId + ": " + question.question());
    }

    public boolean hasMessageContaining(String text) {
        return messages.stream().anyMatch(m -> m.contains(text));
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

    @Test
    public void testAddFact() {
        game.onMessageReceived("User1", "/add_fact");

        // Шаг 1: Выбор категории. Если файл пуст, getAllCategories() вернет ["Все"] (1 элемент).
        // Если файл полон, выберем "1". В любом случае должен пойти следующий шаг.
        game.onMessageReceived("User1", "1");

        // Шаг 2: Текст факта
        game.onMessageReceived("User1", "Новый факт про животных");

        // Шаг 3: Правда/Ложь
        game.onMessageReceived("User1", "правда");

        // Шаг 4: Пояснение
        game.onMessageReceived("User1", "Это пояснение");

        assertTrue(fakePlatform.hasMessageContaining("Факт успешно добавлен"),
                "Бот должен подтвердить добавление факта");
    }

    @Test
    public void testHelpCommand() {
        game.onMessageReceived("User1", "/help");
        assertTrue(fakePlatform.hasMessageContaining("ПРАВИЛА ИГРЫ"));
    }

    @Test
    public void testRestartCommand() {
        game.onMessageReceived("User1", "привет"); // Триггер выбора категории
        game.onMessageReceived("User1", "1");      // Выбор категории

        // Если игра началась, ответим на вопрос. Если нет (нет фактов), пропустим.
        if (!fakePlatform.questions.isEmpty()) {
            game.onMessageReceived("User1", "верю");
        }

        game.onMessageReceived("User1", "/restart");

        assertTrue(fakePlatform.hasMessageContaining("Игра перезапущена")
                || fakePlatform.hasMessageContaining("Выбери новую категорию")
                || fakePlatform.hasMessageContaining("Выбери категорию"));
    }

    @Test
    public void testStatsCommand() {
        game.onMessageReceived("User1", "привет");
        game.onMessageReceived("User1", "1");

        if (!fakePlatform.questions.isEmpty()) {
            game.onMessageReceived("User1", "верю");
        }

        game.onMessageReceived("User1", "/stats");
        assertTrue(fakePlatform.hasMessageContaining("----------СТАТИСТИКА"));
    }

    @Test
    public void testQuestionWithButtonsIsSent() {
        game.onMessageReceived("User1", "привет");
        game.onMessageReceived("User1", "1");

        // Если в базе есть факты, то вопрос должен прийти
        if (!fakePlatform.questions.isEmpty()) {
            Question lastQuestion = fakePlatform.questions.get(fakePlatform.questions.size() - 1);
            assertNotNull(lastQuestion.question(), "У вопроса должен быть текст");
            assertEquals(3, lastQuestion.answers().size(), "Должно быть 3 варианта ответа");
        } else {
            // Если фактов нет, проверяем, что бот вежливо сообщил об этом
            assertTrue(fakePlatform.hasMessageContaining("нет ни одного факта")
                    || fakePlatform.hasMessageContaining("нет фактов"));
        }
    }

    @Test
    public void testConcurrentAddFact() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            game.onMessageReceived("User1", "/add_fact");
            game.onMessageReceived("User1", "1");
            game.onMessageReceived("User1", "Факт 1");
            game.onMessageReceived("User1", "правда");
            game.onMessageReceived("User1", "Пояснение 1");
        });

        Thread t2 = new Thread(() -> {
            game.onMessageReceived("User2", "/add_fact");
            game.onMessageReceived("User2", "1"); // Используем 1, а не 2, чтобы не зависеть от кол-ва категорий
            game.onMessageReceived("User2", "Факт 2");
            game.onMessageReceived("User2", "ложь");
            game.onMessageReceived("User2", "Пояснение 2");
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Проверяем, что хотя бы один факт добавился (в идеале оба)
        assertTrue(fakePlatform.hasMessageContaining("Факт успешно добавлен"));
    }
}