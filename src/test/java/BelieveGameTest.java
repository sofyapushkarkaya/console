import logic.BelieveGame;
import model.Statement;
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
    public void sendHelp(String userId) {
        messages.add(userId + ": HELP");
    }
}

public class BelieveGameTest {
    private BelieveGame game;
    private FakeBotPlatform fakePlatform;

    @BeforeEach
    public void setUp() {
        fakePlatform = new FakeBotPlatform();

        // Создаем репозиторий с тремя ЛОЖНЫМИ фактами
        FakeStatementRepository fakeRepo = new FakeStatementRepository();
        fakeRepo.addStatement(new Statement("Ложь 1", false, "Это ложь", "Животные"));
        fakeRepo.addStatement(new Statement("Ложь 2", false, "Это ложь", "Животные"));
        fakeRepo.addStatement(new Statement("Ложь 3", false, "Это ложь", "Животные"));

        game = new BelieveGame(fakeRepo, fakePlatform);
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

        // 2. Вася ошибается 3 раза подряд (отвечает "верю" на ложные факты)
        game.onMessageReceived("Вася", "верю"); // Ошибка 1
        game.onMessageReceived("Вася", "верю"); // Ошибка 2
        game.onMessageReceived("Вася", "верю"); // Ошибка 3

        // Проверяем, что Вася проиграл
        assertTrue(fakePlatform.messages.stream().anyMatch(m -> m.contains("3 ошибки подряд")), "Вася должен был проиграть");

        // 3. Проверяем, что Петя всё еще может играть
        fakePlatform.messages.clear();
        game.onMessageReceived("Петя", "верю");

        // Петя не должен получить сообщение об окончании игры
        assertFalse(fakePlatform.messages.stream().anyMatch(m -> m.contains("Игра окончена")), "Петя не должен был проиграть");
    }
}