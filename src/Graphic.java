
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.math.BigDecimal;

public class Graphic extends JPanel {

    private double[] x;
    private double[] y;
    private final boolean useLagrange;

    public Graphic(boolean useLagrange) {
        this.useLagrange = useLagrange;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 280));
    }

    public void setPoints(double[] x, double[] y) {
        this.x = x.clone();
        this.y = y.clone();
        repaint();
    }

    private double calculate(double argument) {
        if (useLagrange) {
            return Lagrange.calculate(x, y, argument);
        }
        return Newton.calculate(x, y, argument);
    }

    private double chooseStep(double range) {
        double shagSetki = range / 7.0;
        double mashtab = Math.pow(10, Math.floor(Math.log10(shagSetki)));
        double normalized = shagSetki / mashtab;

        if (normalized <= 1) {
            return mashtab;
        }
        if (normalized <= 2) {
            return 2 * mashtab;
        }
        if (normalized <= 5) {
            return 5 * mashtab;
        }
        return 10 * mashtab;
    }

    private String formatNumber(double number) {
        if (Math.abs(number) < 1e-12) {
            number = 0;
        }
        return BigDecimal.valueOf(number)
                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        super.paintComponent(graphics);

        if (x == null || x.length == 0) {
            return;
        }

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int left = 65;
        int right = 25;
        int top = 20;
        int bottom = 45;

        int plotWidth = getWidth() - left - right;
        int plotHeight = getHeight() - top - bottom;
        

        double minX = x[0];
        double maxX = x[0];

        for (double coordinate : x) {
            minX = Math.min(minX, coordinate);
            maxX = Math.max(maxX, coordinate);
        }

        double xRange = maxX - minX;

        if (xRange == 0) {
            xRange = 2;
        }

        minX -= xRange * 0.1;
        maxX += xRange * 0.1;

        int samples = 500;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (int i = 0; i <= samples; i++) {
            double argument = minX + (maxX - minX) * i / samples;
            double result = calculate(argument);

            if (Double.isFinite(result)) {
                minY = Math.min(minY, result);
                maxY = Math.max(maxY, result);
            }
        }

        for (double coordinate : y) {
            minY = Math.min(minY, coordinate);
            maxY = Math.max(maxY, coordinate);
        }

        if (!Double.isFinite(minY) || !Double.isFinite(maxY)) {
            g.dispose();
            return;
        }

        double yRange = maxY - minY;

        if (yRange == 0) {
            yRange = 2;
        }

        minY -= yRange * 0.1;
        maxY += yRange * 0.1;

        double stepX = chooseStep(maxX - minX);
        double stepY = chooseStep(maxY - minY);

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));

        for (int i = 0; i < 1000; i++) {
            double tick = (Math.ceil(minX / stepX) + i) * stepX;

            if (tick > maxX) {
                break;
            }

            int px = left + (int) Math.round(
                    (tick - minX) / (maxX - minX) * plotWidth
            );

            g.setColor(new Color(225, 225, 225));
            g.drawLine(px, top, px, top + plotHeight);

            g.setColor(Color.DARK_GRAY);
            String label = formatNumber(tick);
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, px - labelWidth / 2, top + plotHeight + 18);
        }

        for (int i = 0; i < 1000; i++) {
            double tick = (Math.ceil(minY / stepY) + i) * stepY;

            if (tick > maxY) {
                break;
            }

            int py = top + (int) Math.round(
                    (maxY - tick) / (maxY - minY) * plotHeight
            );

            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, py, left + plotWidth, py);

            g.setColor(Color.DARK_GRAY);
            String label = formatNumber(tick);
            int labelWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, left - labelWidth - 8, py + 4);
        }

        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(1.3f));

        if (minY <= 0 && maxY >= 0) {
            int axisY = top + (int) Math.round(
                    maxY / (maxY - minY) * plotHeight
            );
            g.drawLine(left, axisY, left + plotWidth, axisY);
        }

        if (minX <= 0 && maxX >= 0) {
            int axisX = left + (int) Math.round(
                    -minX / (maxX - minX) * plotWidth
            );
            g.drawLine(axisX, top, axisX, top + plotHeight);
        }

        g.drawString("X", left + plotWidth - 5, top + plotHeight + 35);
        g.drawString("Y", 10, top + 5);

        Path2D.Double curve = new Path2D.Double();
        boolean started = false;

        for (int i = 0; i <= samples; i++) {
            double argument = minX + (maxX - minX) * i / samples;
            double result = calculate(argument);

            if (!Double.isFinite(result)) {
                started = false;
                continue;
            }

            double px = left + (argument - minX)
                    / (maxX - minX) * plotWidth;
            double py = top + (maxY - result)
                    / (maxY - minY) * plotHeight;

            if (!started) {
                curve.moveTo(px, py);
                started = true;
            } else {
                curve.lineTo(px, py);
            }
        }

        Shape oldClip = g.getClip();
        g.setClip(left, top, plotWidth, plotHeight);

        g.setColor(useLagrange
                ? new Color(30, 100, 190)
                : new Color(190, 60, 50));

        g.setStroke(new BasicStroke(2.2f));
        g.draw(curve);

        g.setColor(Color.BLACK);

        for (int i = 0; i < x.length; i++) {
            int px = left + (int) Math.round(
                    (x[i] - minX) / (maxX - minX) * plotWidth
            );
            int py = top + (int) Math.round(
                    (maxY - y[i]) / (maxY - minY) * plotHeight
            );

            g.fillOval(px - 4, py - 4, 8, 8);
        }

        g.setClip(oldClip);
        g.dispose();
    }
}
