
import javax.swing.*;
import java.awt.*;

public class UserInterface extends JFrame {

    private JTextArea coordinatesInput;
    private JTextField lagrangeArgumentInput;
    private JTextField newtonArgumentInput;
    private JLabel lagrangeResultLabel;
    private JLabel newtonResultLabel;
    private JLabel checkStatusLabel;
    private Graphic lagrangeGraphic;
    private Graphic newtonGraphic;

    public UserInterface() {
        setTitle("Лабораторная работа №3 - Интерполяция");
        setSize(1050, 750);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel controlPanel = new JPanel(new BorderLayout(8, 12));
        controlPanel.setPreferredSize(new Dimension(240, 0));

        JPanel pointsPanel = new JPanel(new BorderLayout(5, 5));
        pointsPanel.setBorder(
                BorderFactory.createTitledBorder("Исходные точки")
        );

        coordinatesInput = new JTextArea("-1 2\n4 1\n5 3", 10, 15);
        coordinatesInput.setFont(new Font("Monospaced", Font.PLAIN, 14));

        pointsPanel.add(new JLabel("Каждая строка: X Y"), BorderLayout.NORTH);
        pointsPanel.add(new JScrollPane(coordinatesInput), BorderLayout.CENTER);

        controlPanel.add(pointsPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        inputPanel.setBorder(
                BorderFactory.createTitledBorder("Вычисление")
        );

        inputPanel.add(new JLabel("X для Лагранжа:"));
        lagrangeArgumentInput = new JTextField("2");
        inputPanel.add(lagrangeArgumentInput);

        inputPanel.add(new JLabel("X для Ньютона:"));
        newtonArgumentInput = new JTextField("2");
        inputPanel.add(newtonArgumentInput);

        JButton calculateButton = new JButton("Вычислить");
        calculateButton.addActionListener(e -> calculate());
        inputPanel.add(calculateButton);

        checkStatusLabel = new JLabel("Проверка не выполнена");
        inputPanel.add(checkStatusLabel);

        controlPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);

        JPanel graphsPanel = new JPanel(new GridLayout(2, 1, 0, 12));

        JPanel lagrangePanel = new JPanel(new BorderLayout(5, 5));
        lagrangePanel.setBorder(
                BorderFactory.createTitledBorder("Полином Лагранжа")
        );

        lagrangeResultLabel = new JLabel("Y = ");
        lagrangePanel.add(lagrangeResultLabel, BorderLayout.NORTH);

        lagrangeGraphic = new Graphic(true);
        lagrangePanel.add(lagrangeGraphic, BorderLayout.CENTER);

        JPanel newtonPanel = new JPanel(new BorderLayout(5, 5));
        newtonPanel.setBorder(
                BorderFactory.createTitledBorder("Полином Ньютона")
        );

        newtonResultLabel = new JLabel("Y = ");
        newtonPanel.add(newtonResultLabel, BorderLayout.NORTH);

        newtonGraphic = new Graphic(false);
        newtonPanel.add(newtonGraphic, BorderLayout.CENTER);

        graphsPanel.add(lagrangePanel);
        graphsPanel.add(newtonPanel);

        mainPanel.add(graphsPanel, BorderLayout.CENTER);

        add(mainPanel);
        //calculate();
    }

    private void calculate() {
        try {
            String text = coordinatesInput.getText().trim();

            if (text.isEmpty()) {
                throw new IllegalArgumentException("Введите исходные точки");
            }

            String[] lines = text.split("\\R");
            double[] x = new double[lines.length];
            double[] y = new double[lines.length];

            for (int i = 0; i < lines.length; i++) {
                String[] coordinates = lines[i].trim().split("\\s+");

                if (coordinates.length != 2) {
                    throw new IllegalArgumentException(
                            "В каждой строке должны быть указаны две цифры"
                    );
                }

                x[i] = Double.parseDouble(coordinates[0].replace(',', '.'));
                y[i] = Double.parseDouble(coordinates[1].replace(',', '.'));
            }

            for (int i = 0; i < x.length; i++) {
                for (int j = i + 1; j < x.length; j++) {
                    if (x[i] == x[j]) {
                        throw new IllegalArgumentException(
                                "Координаты X не должны повторяться"
                        );
                    }
                }
            }

            double lagrangeX = Double.parseDouble(
                    lagrangeArgumentInput.getText().trim().replace(',', '.')
            );

            double newtonX = Double.parseDouble(
                    newtonArgumentInput.getText().trim().replace(',', '.')
            );

            double lagrangeY = Lagrange.calculate(x, y, lagrangeX);
            double newtonY = Newton.calculate(x, y, newtonX);

            lagrangeResultLabel.setText(
                    String.format("Y = %.6f", lagrangeY)
            );

            newtonResultLabel.setText(
                    String.format("Y = %.6f", newtonY)
            );

            double epsilon = 1e-9;
            boolean isCorrect = true;

            for (int i = 0; i < x.length; i++) {
                double resultL = Lagrange.calculate(x, y, x[i]);
                double resultN = Newton.calculate(x, y, x[i]);

                if (Math.abs(resultL - y[i]) > epsilon || Math.abs(resultN - y[i]) > epsilon) {
                    isCorrect = false;
                    break;
                }
            }

            if (isCorrect) {
                checkStatusLabel.setText("Проверка: пройдена");
            } else {
                checkStatusLabel.setText("Проверка: не пройдена");
            }

            lagrangeGraphic.setPoints(x, y);
            newtonGraphic.setPoints(x, y);

        } catch (IllegalArgumentException ex) {
            lagrangeResultLabel.setText("Y = ");
            newtonResultLabel.setText("Y = ");
            checkStatusLabel.setText("Проверка не выполнена");

            lagrangeGraphic.setPoints(new double[0], new double[0]);
            newtonGraphic.setPoints(new double[0], new double[0]);

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
