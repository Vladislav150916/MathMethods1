
public class Newton {
    public static double calculate(double[] x, double[] y, double value) {
        if (x == null || y == null || x.length != y.length || x.length == 0) {
            throw new IllegalArgumentException("Некорректные исходные данные");
        }
        int n = x.length;
        double[] raznosti = y.clone();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (x[i] == x[j]) {
                    throw new IllegalArgumentException("Координаты X должны различаться");
                }
            }
        }
        for (int poryadok = 1; poryadok < n; poryadok++) {
            for (int i = n - 1; i >= poryadok; i--) {
                raznosti[i] = (raznosti[i] - raznosti[i - 1])
                        / (x[i] - x[i - poryadok]);
            }
        }
        double result = raznosti[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            result = result * (value - x[i]) + raznosti[i];
        }
        return result;
    }
}
