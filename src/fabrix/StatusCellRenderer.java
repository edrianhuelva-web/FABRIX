package fabrix;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JLabel cell = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        
        String text = (value != null) ? value.toString() : "";
        cell.setFont(new Font("SansSerif", Font.BOLD, 12));
        cell.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        
        if (text.matches("\\d+")) {
            cell.setForeground(new Color(215, 45, 45)); 
        } else if (text.equalsIgnoreCase("Pending")) {
            cell.setForeground(new Color(166, 118, 16)); 
        } else if (text.equalsIgnoreCase("Processing")) {
            cell.setForeground(new Color(38, 114, 198)); 
        } else if (text.equalsIgnoreCase("Shipped")) {
            cell.setForeground(new Color(126, 58, 178)); 
        } else if (text.equalsIgnoreCase("Delivered")) {
            cell.setForeground(new Color(36, 130, 68)); 
        } else {
            cell.setForeground(new Color(30, 30, 30)); 
        }
        return cell;
    }
}