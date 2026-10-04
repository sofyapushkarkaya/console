import data.InMemoryStatementRepository;
import data.StatementRepository;
import logic.BelieveGame;
import view.ConsoleView;
import view.ConsoleViewImpl;

public class Main {
    public static void main(String[] args) {
        StatementRepository repository = new InMemoryStatementRepository();
        ConsoleView view = new ConsoleViewImpl();
        BelieveGame game = new BelieveGame(repository, view);
        game.start();
    }
}
