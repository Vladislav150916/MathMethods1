
public class Lagrange {

    public static double calculate(double[] x, double[] y, double value) {
        if (x == null || y == null || x.length != y.length || x.length == 0) {
            throw new IllegalArgumentException("Некорректные исходные данные");
        }

        double result = 0;

        for (int i = 0; i < x.length; i++) {
            double slagaemoe = y[i];
            for (int j = 0; j < x.length; j++) {
                if (i != j) {
                    if (x[i] == x[j]) {
                        throw new IllegalArgumentException("Координаты X должны различаться");
                    }
                    slagaemoe *= (value - x[j]) / (x[i] - x[j]);
                }
            }
            result += slagaemoe;
        }
        return result;
    }
}
