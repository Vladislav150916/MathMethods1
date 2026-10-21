
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface window = new UserInterface();
            window.setVisible(true);
        });
    }
}
