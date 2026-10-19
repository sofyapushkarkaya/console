package data;

import model.Statement;
import java.util.ArrayList;
import java.util.List;

public class FilteredStatementRepository implements StatementRepository {
    private final List<Statement> filteredStatements;
    private int currentIndex = 0;

    public FilteredStatementRepository(List<Statement> allStatements, String category) {
        this.filteredStatements = new ArrayList<>();

        for (Statement s : allStatements) {
            if (category == null || s.getCategory().equals(category)) {
                filteredStatements.add(s);
            }
        }

        if (filteredStatements.isEmpty()) {
            throw new IllegalArgumentException("Нет фактов в категории: " + category);
        }
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