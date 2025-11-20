package com.creditcardcalculatorbrionblais.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.swing.JPanel;

class SimpleLineChartPanel extends JPanel {

    private final java.util.List<LocalDate> dates;
    private final java.util.List<BigDecimal> values;
    private final String title;

    public SimpleLineChartPanel(String title,
                                java.util.List<LocalDate> dates,
                                java.util.List<BigDecimal> values) {
        this.title = title;
        this.dates = dates;
        this.values = values;
        setPreferredSize(new Dimension(900, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Anti-aliasing for smoother lines
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Margins
        int margin = 50;
        int left = margin;
        int top = margin;
        int right = w - margin;
        int bottom = h - margin;

        // Draw axes
        g2.drawLine(left, bottom, right, bottom); // X-axis
        g2.drawLine(left, bottom, left, top);     // Y-axis

        // Title
        g2.drawString(title, left, top - 20);

        if (dates.isEmpty() || values.isEmpty()) return;

        // Determine min/max Y
        BigDecimal minVal = values.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal maxVal = values.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ONE);

        double minY = minVal.doubleValue();
        double maxY = maxVal.doubleValue();

        if (maxY == minY) maxY = minY + 1; // avoid divide-by-zero

        int numPoints = dates.size();

        // Scaling
        double xScale = (right - left) / (double) (numPoints - 1);
        double yScale = (bottom - top) / (maxY - minY);

        // Draw line segments
        g2.setColor(Color.BLUE);

        for (int i = 0; i < numPoints - 1; i++) {
            int x1 = (int) (left + i * xScale);
            int y1 = (int) (bottom - (values.get(i).doubleValue() - minY) * yScale);

            int x2 = (int) (left + (i + 1) * xScale);
            int y2 = (int) (bottom - (values.get(i + 1).doubleValue() - minY) * yScale);

            g2.drawLine(x1, y1, x2, y2);
        }

        // Draw min/max labels
        g2.setColor(Color.BLACK);
        g2.drawString(String.format("Min: %.2f", minY), left, bottom + 15);
        g2.drawString(String.format("Max: %.2f", maxY), left, top - 5);
    }
}
