import model.Statement;
import java.util.ArrayList;
import java.util.List;
import data.StatementRepository;

public class FakeStatementRepository implements StatementRepository {
    private final List<Statement> statements = new ArrayList<>();
    private int index = 0;

    public FakeStatementRepository() {
    }

    // Конструктор для быстрого создания
    public FakeStatementRepository(List<Statement> statements) {
        this.statements.addAll(statements);
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void reset() {
        index = 0;
    }

    @Override
    public Statement getNextStatement() {
        if (statements.isEmpty()) {
            return null;
        }
        Statement s = statements.get(index);
        index = (index + 1) % statements.size();
        return s;
    }

    @Override
    public boolean hasNext() {
        return !statements.isEmpty();
    }
}