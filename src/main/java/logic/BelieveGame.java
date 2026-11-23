package logic;

import data.StatementData;
import model.Statement;
import view.BotPlatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BelieveGame implements Bot {
    private BotPlatform platform;
    private final Map<String, userSession> sessions = new HashMap<>();

    public BelieveGame(BotPlatform platform) {
        this.platform = platform;
    }

    public void setPlatform(BotPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void onMessageReceived(String userId, String text) {
        userSession session = sessions.computeIfAbsent(userId, userSession::new);

        // 1. Проверка на выход
        if (isExitCommand(text)) {
            platform.sendMessage(userId, "Спасибо за игру! Пока!");
            sessions.remove(userId);
            return;
        }

        // 2. Проверка на справку
        if (isHelpCommand(text)) {
            String helpText = "Я - бот верю\\не верю\n\n" +
                    "ПРАВИЛА ИГРЫ:\nнужно отвечать верю или не верю, посмотрим насколько ты доверчивый человек\n\n" +
                    "КОМАНДЫ:\n" +
                    "\\start - \"Round 1. Fight\" (если вдруг забыл, как начинать)\n" +
                    "\\help - помощь, в трудную минуту\n" +
                    "\\exit - обиделся и решил, что лучше пойти поиграть в гта\n" +
                    "\\category - ты личность переменчивая и решил выбрать другую категорию\n" +
                    "\\stats - порадовать своего внутреннего нарцисса и посмотреть насколько хорошо ты отвечаешь, при этом, не прерывая игру\n" +
                    "\\restart - начать всё с чистого листа, но сначала узнать, к чему привел прошлый раунд\n" +
                    "\\add_fact - оставь свой след в этой истории и добавь свой факт в эту игру (при вводе не используй пж энтеры)";

            platform.sendMessage(userId, helpText);
            return;
        }

        // 3. Проверка на добавление факта
        if (text.equalsIgnoreCase("/add_fact") || text.equalsIgnoreCase("\\add_fact")) {
            session.setAddFactStep(1);
            List<String> categories = StatementData.getAllCategories();
            StringBuilder sb = new StringBuilder("Шаг 1/4. Выберите категорию (напишите номер):\n");
            for (int i = 0; i < categories.size(); i++) {
                sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
            }
            platform.sendMessage(userId, sb.toString().trim());
            return;
        }

        // 4. Обработка шагов добавления факта (если процесс запущен)
        if (session.getAddFactStep() > 0) {
            handleAddFactSteps(userId, text, session);
            return;
        }

        // 5. Проверка на рестарт
        if (text.equalsIgnoreCase("/restart") || text.equalsIgnoreCase("\\restart")) {
            if (session.isGameStarted()) {
                platform.sendMessage(userId, "Игра перезапущена!");
                printFinalStatistics(userId, session);
            }
            session.resetGame();
            platform.sendMessage(userId, "Привет! Выбери категорию:");
            selectCategory(userId, session);
            return;
        }

        // 6. Проверка на статистику
        if (text.equalsIgnoreCase("/stats") || text.equalsIgnoreCase("\\stats")) {
            if (session.isGameStarted()) {
                printFinalStatistics(userId, session);
            } else {
                platform.sendMessage(userId, "Вы еще не начали игру. Напишите /category или просто выберите категорию.");
            }
            return;
        }

        // 7. Проверка на смену категории
        if (isCategoryCommand(text)) {
            session.resetGame();
            platform.sendMessage(userId, "Выбери новую категорию:");
            selectCategory(userId, session);
            return;
        }

        // 8. Если ждем выбора категории
        if (session.isAwaitingCategoryChoice()) {
            handleCategoryChoice(userId, text, session);
            return;
        }

        // 9. Если игра еще не начата
        if (!session.isGameStarted()) {
            platform.sendMessage(userId, "Привет! Выбери категорию:");
            selectCategory(userId, session);
            return;
        }

        // 10. Обработка ответа на факт
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

        // Если есть только "Все" — значит фактов нет вообще
        if (categories.size() <= 1) {
            platform.sendMessage(userId, "Пока нет ни одного факта. Попробуйте позже.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categories.size(); i++) {
            sb.append(i + 1).append(". ").append(categories.get(i)).append("\n");
        }
        // Убрали ручное добавление "Все", так как оно уже в списке
        platform.sendMessage(userId, sb.toString().trim());
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
            choice = categories.size(); // "Все" теперь последний элемент
        }

        if (choice >= 1 && choice <= categories.size()) {
            categoryName = categories.get(choice - 1);
        } else {
            categoryName = "Все";
        }

        List<Integer> ids = StatementData.getStatementIdsByCategory(categoryName);
        if (ids.isEmpty()) {
            platform.sendMessage(userId, "В категории \"" + categoryName + "\" нет фактов. Выбери другую.");
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

        if (current == null) {
            platform.sendMessage(userId, "Ошибка: вопрос не найден. Выбери категорию заново (/category).");
            return;
        }

        if (answer.equals("сдаюсь")) {
            platform.sendMessage(userId, "Вы сдались! Игра окончена.");
            printFinalStatistics(userId, session);
            sessions.remove(userId);
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
        platform.sendQuestionWithButtons(userId, next.getText());
    }

    private void printFinalStatistics(String userId, userSession session) {
        platform.sendMessage(userId, "----------СТАТИСТИКА:");
        platform.sendMessage(userId, "У вас " + session.getCorrectAnswers() + " из "
                + session.getTotalAnswers() + " правильных ответов.");
        if (session.getTotalAnswers() > 0) {
            int percent = (int) Math.round((double) session.getCorrectAnswers()
                    / session.getTotalAnswers() * 100);
            platform.sendMessage(userId, "Точность: " + percent + "%");
        }
    }

    private void handleAddFactSteps(String userId, String text, userSession session) {
        int step = session.getAddFactStep();

        if (step == 1) {
            List<String> categories = StatementData.getAllCategories();
            int choice;
            try {
                choice = Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                platform.sendMessage(userId, "Введите номер категории (1, 2 или 3):");
                return;
            }

            if (choice < 1 || choice > categories.size()) {
                platform.sendMessage(userId, "Неверный номер. Введите 1, 2 или 3:");
                return;
            }

            session.setTempCategory(categories.get(choice - 1));
            session.setAddFactStep(2);
            platform.sendMessage(userId, "Шаг 2/4. Введите текст факта (например, 'Пингвины летают'):");
        }
        else if (step == 2) {
            if (text.trim().isEmpty()) {
                platform.sendMessage(userId, "Текст не может быть пустым. Введите текст факта:");
                return;
            }
            session.setTempFactText(text.trim());
            session.setAddFactStep(3);
            platform.sendMessage(userId, "Шаг 3/4. Это правда или ложь? (напишите 'правда' или 'ложь'):");
        }
        else if (step == 3) {
            String input = text.trim().toLowerCase();
            if (input.equals("правда") || input.equals("true")) {
                session.setTempIsTrue(true);
            } else if (input.equals("ложь") || input.equals("false")) {
                session.setTempIsTrue(false);
            } else {
                platform.sendMessage(userId, "Напишите 'правда' или 'ложь':");
                return;
            }
            session.setAddFactStep(4);
            platform.sendMessage(userId, "Шаг 4/4. Введите пояснение (почему это так?):");
        }
        else if (step == 4) {
            if (text.trim().isEmpty()) {
                platform.sendMessage(userId, "Пояснение не может быть пустым. Введите пояснение:");
                return;
            }

            Statement newStatement = new Statement(
                    session.getTempFactText(),
                    session.isTempIsTrue(),
                    text.trim(),
                    session.getTempCategory()
            );
            StatementData.addNewStatement(newStatement);

            session.setAddFactStep(0);
            session.setTempFactText(null);
            session.setTempCategory(null);

            platform.sendMessage(userId, "Факт успешно добавлен в категорию '" + newStatement.getCategory() + "'!");
        }
    }
}