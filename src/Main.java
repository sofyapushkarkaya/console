import data.FilteredStatementRepository;
import data.StatementData;
import data.StatementRepository;
import logic.BelieveGame;
import view.ConsoleView;
import view.ConsoleViewImpl;

public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleViewImpl();

        StatementRepository initialRepository = new FilteredStatementRepository(
                StatementData.getAllStatements(),
                null
        );

        BelieveGame game = new BelieveGame(initialRepository, view);

        game.start();
    }
}