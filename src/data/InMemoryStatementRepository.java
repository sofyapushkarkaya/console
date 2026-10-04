package data;

import model.Statement;
import java.util.List;

public class InMemoryStatementRepository implements StatementRepository {
    private final List<Statement> statements;
    private int currentIndex = 0; //счетчик вопросов

    public InMemoryStatementRepository() {
        this.statements = StatementData.getAllStatements();
    }

    public Statement getNextStatement() {
        if (!hasNext()) {
            return null;
        }
        return statements.get(currentIndex++); //берет по индексу из списка и только потом увеличивает счетчик
    }

    public boolean hasNext() {
        return currentIndex < statements.size();
    }
}
