import data.StatementRepository;
import model.Statement;

import java.util.ArrayList;
import java.util.List;

//нужен чтобы если тесту нужен определнный факт (правдивый/ложный), то не подгадывать
//под список, он может менятся, перемешиваться, высылать факты рандомно, а для тестов
//нужен заранее известный факт

public class FakeStatementRepository implements StatementRepository {
    private final List<Statement> statements = new ArrayList<>();
    private int index = 0;

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    @Override
    public Statement getNextStatement() {
        if (statements.isEmpty()) {
            return null;
        }
        Statement s = statements.get(index);
        index = (index + 1) % statements.size(); // Зацикливание
        return s;
    }

    @Override
    public boolean hasNext() {
        return !statements.isEmpty();
    }
}
