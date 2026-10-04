package data;

import model.Statement;
import java.util.List;

public class InMemoryStatementRepository implements StatementRepository {
    private final List<Statement> statements;
    private int currentIndex = 0;

    public InMemoryStatementRepository() {
        this.statements = StatementData.getAllStatements();
    }

    public Statement getNextStatement() {
        Statement statement = statements.get(currentIndex);
        //зацикливание
        currentIndex = (currentIndex + 1) % statements.size();
        return statement;
    }

    public boolean hasNext() {
        // Всегда true, если есть хотя бы один факт
        return !statements.isEmpty();
    }
}
