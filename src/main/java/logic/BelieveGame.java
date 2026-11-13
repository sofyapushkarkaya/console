package logic;

import data.StatementData;
import model.Statement;
import view.BotPlatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BelieveGame implements Bot {
    private BotPlatform platform; // Убрали final, чтобы можно было установить позже
    private final Map<String, userSession> sessions = new HashMap<>();

    public BelieveGame(BotPlatform platform) {
        this.platform = platform;
    }

    // НОВЫЙ МЕТОД: Позволяет привязать платформу после создания игры
    public void setPlatform(BotPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void onMessageReceived(String userId, String text) {
        userSession session = sessions.computeIfAbsent(userId, userSession::new);

        if (isExitCommand(text)) {
            platform.sendMessage(userId, "Спасибо за игру! Пока!");
            sessions.remove(userId);
            return;
        }

        if (isHelpCommand(text)) {
            platform.sendMessage(userId, "Я - бот 'Верю/Не верю'. Отвечай 'верю' или 'не верю'. Команды: \\help, \\exit, \\category (сменить категорию).");
            return;
        }

        // Обработка команды смены категории
        if (isCategoryCommand(text)) {
            session.resetGame();
            platform.sendMessage(userId, "Выбери новую категорию:");
            selectCategory(userId, session);
            return;
        }

        if (session.isAwaitingCategoryChoice()) {
            handleCategoryChoice(userId, text, session);
            return;
        }

        if (!session.isGameStarted()) {
            platform.sendMessage(userId, "Привет! Выбери категорию:");
            selectCategory(userId, session);
            return;
        }

        handleAnswer(userId, text, session);
    }

    private boolean isExitCommand(String text) {
        return text.equalsIgnoreCase("/exit") || text.equalsIgnoreCase("\\exit")
                || text.equalsIgnoreCase("/quit") || text.equalsIgnoreCase("\\quit");
    }

    private boolean isHelpCommand(String text) {
        return text.equalsIgnoreCase("/help") || text.equalsIgnoreCase("\\help");
    }

    private boolean isCategoryCommand(String text) {
        return text.equalsIgnoreCase("/category") || text.equalsIgnoreCase("\\category");
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
        String categoryName = "Все";
        int choice;

        try {
            choice = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            platform.sendMessage(userId, "Неверный ввод. Играю со всеми фактами.");
            choice = categories.size() + 1;
        }

        if (choice >= 1 && choice <= categories.size()) {
            categoryName = categories.get(choice - 1);
        }

        // Получаем ТОЛЬКО ID вопросов, сами объекты не копируем
        List<Integer> ids = StatementData.getStatementIdsByCategory(categoryName);
        if (ids.isEmpty()) {
            platform.sendMessage(userId,
                    "В категории \"" + categoryName + "\" нет фактов. Выбери другую.");
            return;
        }

        session.startGame(ids);
        session.setAwaitingCategoryChoice(false);
        session.resetStats();

        platform.sendMessage(userId, "Отлично! Категория: " + categoryName);
        startNewRound(userId, session);
    }

    private void handleAnswer(String userId, String text, userSession session) {
        String answer = text.trim().toLowerCase();
        boolean isCorrect;
        Statement current = session.getCurrentStatement();

        // Защита на случай, если вопрос не установился
        if (current == null) {
            platform.sendMessage(userId, "Ошибка: вопрос не найден. Выбери категорию заново (/category).");
            return;
        }

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
        // Так как добавлено зацикливание, hasStatements() вернет false только если база фактов совсем пуста
        if (!session.hasStatements()) {
            platform.sendMessage(userId, "Нет фактов для игры. Завершаю работу.");
            return;
        }

        Statement next = session.getNextStatement();

        if (next == null) {
            platform.sendMessage(userId, "Не удалось загрузить вопрос.");
            return;
        }

        session.setCurrentStatement(next);
        platform.sendMessage(userId, next.getText() + "\nВерите? (верю/не верю)");
    }

    private void printFinalStatistics(String userId, userSession session) {
        platform.sendMessage(userId, "Итоги игры:");
        platform.sendMessage(userId, "У вас " + session.getCorrectAnswers() + " из "
                + session.getTotalAnswers() + " правильных ответов.");
        if (session.getTotalAnswers() > 0) {
            int percent = (int) Math.round((double) session.getCorrectAnswers()
                    / session.getTotalAnswers() * 100);
            platform.sendMessage(userId, "Точность: " + percent + "%");
        }
    }
}