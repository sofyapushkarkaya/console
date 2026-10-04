package data;

import model.Statement;

public interface StatementRepository {
    Statement getNextStatement();
    boolean hasNext();
}
