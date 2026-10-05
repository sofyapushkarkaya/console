import view.ConsoleView;

import java.util.ArrayList;
import java.util.List;

//Слово @Override — это аннотация, которая говорит компилятору: "Я думаю,
//что этот метод переопределяет метод из интерфейса (или родительского класса). Проверь меня".
//
//Сама по себе она ничего не делает — программа работает точно так же с ней и без неё.
//Но она спасает от глупых ошибок на этапе компиляции. Давайте разберемся, зачем она нужна.

public class FakeConsoleView implements ConsoleView {
    private final List<String> inputs = new ArrayList<>();
    private final List<String> outputs = new ArrayList<>();
    private int inputIndex = 0;

    public void addInput(String input) {
        inputs.add(input);
    }

    public List<String> getOutputs() {
        return outputs;
    }

    @Override
    public void print(String text) {
        outputs.add(text);
    }

    @Override
    public String readInput() {
        if (inputIndex < inputs.size()) {
            return inputs.get(inputIndex++);
        }
        // Если ввод закончился — возвращаем \exit, чтобы игра не зависла
        return "\\exit";
    }

    @Override
    public void printHelp() {
        outputs.add("HELP_PRINTED");
    }
}
