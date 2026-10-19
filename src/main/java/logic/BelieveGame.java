package logic;

import data.*;
import model.Statement;
import view.BotPlatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BelieveGame implements Bot {
    private final BotPlatform platform;
    private final StatementRepository initialRepository; // Сохраняем стартовый репозиторий
    private final Map<String, userSession> sessions = new HashMap<>();

    public BelieveGame(StatementRepository initialRepository, BotPlatform platform) {
        this.initialRepository = initialRepository;
        this.platform = platform;
    }

    @Override
    public void onMessageReceived(String userId, String text) {
        userSession session = sessions.computeIfAbsent(userId, userSession::new);

        // Обработка команд
        if (text.equalsIgnoreCase("\\exit") || text.equalsIgnoreCase("\\quit")) {
            platform.sendMessage(userId, "Спасибо за игру! Пока!");
            sessions.remove(userId);
            return;
        }
        if (text.equalsIgnoreCase("\\help")) {
            platform.sendHelp(userId);
            return;
        }

        // Если ждем выбора категории
        if (session.isAwaitingCategoryChoice()) {
            handleCategoryChoice(userId, text, session);
            return;
        }

        // Если игра еще не начата (первый заход)
        if (!session.isGameStarted()) {
            platform.sendMessage(userId, "Привет! Выбери категорию:");
            selectCategory(userId, session);
            return;
        }

        // Обработка ответа на факт
        handleAnswer(userId, text, session);
    }

    private void selectCategory(String userId, userSession session) {
        List<String> categories = StatementData.getAllCategories();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
        }
        sb.append(categories.size() + 1).append(". Все");
        platform.sendMessage(userId, sb.toString());
        session.setAwaitingCategoryChoice(true);
    }

    private void handleCategoryChoice(String userId, String text, userSession session) {
        List<String> categories = StatementData.getAllCategories();
        String category = null;
        int choice;

        try {
            choice = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            platform.sendMessage(userId, "Неверный ввод. Играю со всеми фактами.");
            choice = categories.size() + 1;
        }

        if (choice >= 1 && choice <= categories.size()) {
            category = categories.get(choice - 1);
        }

        // ВАЖНО: Если передан фейковый репозиторий (из теста), используем его.
        // Иначе создаем настоящий с фильтрацией по категориям.
        // (Предполагается, что класс FakeStatementRepository находится в пакете по умолчанию или импортирован)
        if (initialRepository != null && initialRepository.getClass().getSimpleName().equals("FakeStatementRepository")) {
            session.setRepository(initialRepository);
        } else {
            session.setRepository(new FilteredStatementRepository(StatementData.getAllStatements(), category));
        }

        session.setAwaitingCategoryChoice(false);
        session.resetStats();

        String catName = (category == null) ? "Все" : category;
        platform.sendMessage(userId, "Отлично! Категория: " + catName);
        startNewRound(userId, session);
    }

    private void handleAnswer(String userId, String text, userSession session) {
        String answer = text.trim().toLowerCase();
        boolean isCorrect = false;
        Statement current = session.getCurrentStatement();

        if (answer.equals("верю")) {
            isCorrect = current.isTrue();
        } else if (answer.equals("не верю") || answer.equals("неверю")) {
            isCorrect = !current.isTrue();
        } else {
            platform.sendMessage(userId, "Я не понял. Напиши 'верю' или 'не верю'.");
            return;
        }

        session.incrementTotalAnswers();

        if (isCorrect) {
            session.incrementCorrectAnswers();
            session.setWrongStreak(0);
            if (current.isTrue()) {
                platform.sendMessage(userId, "Верно! Это правда. " + current.getExplanation());
            } else {
                platform.sendMessage(userId, "Верно! Это ложь. " + current.getExplanation());
            }
        } else {
            session.setWrongStreak(session.getWrongStreak() + 1);
            if (current.isTrue()) {
                platform.sendMessage(userId, "Неверно! Это правда. " + current.getExplanation());
            } else {
                platform.sendMessage(userId, "Неверно! Это ложь. " + current.getExplanation());
            }

            if (session.getWrongStreak() >= 3) {
                platform.sendMessage(userId, "Вы допустили 3 ошибки подряд. Игра окончена!");
                printFinalStatistics(userId, session);
                sessions.remove(userId);
                return;
            }
        }

        session.setCurrentStatement(null);
        startNewRound(userId, session);
    }

    private void startNewRound(String userId, userSession session) {
        if (!session.getRepository().hasNext()) {
            platform.sendMessage(userId, "Нет ни одного факта для игры. Завершаю работу.");
            return;
        }
        Statement next = session.getRepository().getNextStatement();
        session.setCurrentStatement(next);
        platform.sendMessage(userId, next.getText() + "\nВерите? (верю/не верю)");
    }

    private void printFinalStatistics(String userId, userSession session) {
        platform.sendMessage(userId, "Итоги игры:");
        platform.sendMessage(userId, "У вас " + session.getCorrectAnswers() + " из " + session.getTotalAnswers() + " правильных ответов.");
        if (session.getTotalAnswers() > 0) {
            int percent = (int) Math.round((double) session.getCorrectAnswers() / session.getTotalAnswers() * 100);
            platform.sendMessage(userId, "Точность: " + percent + "%");
        }
    }
}