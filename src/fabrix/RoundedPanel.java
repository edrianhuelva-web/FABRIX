package fabrix;

import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {
    private int cornerRadius = 16;
    private Color borderColor = new Color(232, 229, 220);

    public RoundedPanel() {
        setOpaque(false);
    }

    public int getCornerRadius() { return cornerRadius; }
    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; repaint(); }

    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}