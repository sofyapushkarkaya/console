package data;

import model.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilteredStatementRepository implements StatementRepository {
    private final List<Statement> statements;
    private int currentIndex = 0;

    public FilteredStatementRepository(List<Statement> statements) {
        if (statements == null || statements.isEmpty()) {
            throw new IllegalArgumentException("Список фактов пуст");
        }
        this.statements = new ArrayList<>(statements); // копия — защита от внешних изменений
    }

    @Override
    public Statement getNextStatement() {
        Statement statement = filteredStatements.get(currentIndex);
        currentIndex = (currentIndex + 1) % filteredStatements.size();
        return statement;
    }

    @Override
    public boolean hasNext() {
        return !filteredStatements.isEmpty();
    }
}