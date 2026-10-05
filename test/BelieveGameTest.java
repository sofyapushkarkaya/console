import logic.BelieveGame;
import model.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BelieveGameTest {
    private FakeStatementRepository repository;
    private FakeConsoleView view;
    private BelieveGame game;

    @BeforeEach
    public void setUp() {
        repository = new FakeStatementRepository();
        view = new FakeConsoleView();
        game = new BelieveGame(repository, view);
    }

    @Test
    public void testCorrectAnswerTrue() {
        repository.addStatement(new Statement("Небо синее", true, "Релеевское рассеяние"));
        view.addInput("верю");
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Верно! Это правда.")));//проверка содержания определенной строки в выводе
    }

    @Test
    public void testIncorrectAnswerFalse() {
        repository.addStatement(new Statement("Земля плоская", false, "Она круглая"));
        view.addInput("верю");
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Неверно! Это ложь.")));
    }

    @Test
    public void testCorrectAnswerNotBelieve() {
        repository.addStatement(new Statement("Земля плоская", false, "Она круглая"));
        view.addInput("не верю");
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Верно! Это ложь.")));
    }

    @Test
    public void testHelpCommandDoesNotSkipQuestion() {
        repository.addStatement(new Statement("Тест", true, "Пояснение"));
        view.addInput("\\help");
        view.addInput("верю");
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().contains("HELP_PRINTED"));
        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Верно! Это правда.")));
    }

    @Test
    public void testUnknownCommandDoesNotSkipQuestion() {
        repository.addStatement(new Statement("Тест", true, "Пояснение"));
        view.addInput("абракадабра");
        view.addInput("верю");
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Я не понял.")));
        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Верно! Это правда.")));
    }

    @Test
    public void testExitCommand() {
        repository.addStatement(new Statement("Тест", true, "Пояснение"));
        view.addInput("\\exit");

        game.start();

        assertTrue(view.getOutputs().stream()
                .anyMatch(s -> s.contains("Спасибо за игру! Пока!")));
    }

    @Test
    public void testInfiniteLoopWithRepeatingFacts() {
        // Один факт — должен повторяться бесконечно
        repository.addStatement(new Statement("Небо синее", true, "Релеевское рассеяние"));
        view.addInput("верю");
        view.addInput("верю");
        view.addInput("верю");
        view.addInput("\\exit");

        game.start();

        long correctAnswers = view.getOutputs().stream()
                .filter(s -> s.contains("Верно! Это правда."))
                .count();
        assertTrue(correctAnswers >= 3);
    }
}
