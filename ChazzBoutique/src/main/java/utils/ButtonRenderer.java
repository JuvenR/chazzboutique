package utils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonRenderer extends JButton implements TableCellRenderer {

    private Color defaultColor;
    private Color hoverColor;

    public ButtonRenderer(String text) {
        setText(text);
        setOpaque(true);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 16));
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        if ("Editar".equalsIgnoreCase(text)) {
            defaultColor = new Color(204, 204, 204);
            hoverColor = new Color(66, 165, 245); // Más claro
        } else if ("Eliminar".equalsIgnoreCase(text)) {
            defaultColor = new Color(176, 50, 53);
            hoverColor = new Color(239, 83, 80); // Más claro
        } else {

            defaultColor = new Color(33, 111, 186); // Azul que armoniza con rojo oscuro
            hoverColor = new Color(66, 165, 245);  // Azul más claro para hover

        }

        setBackground(defaultColor);

        // Efecto hover (aunque esto se vuelve a pintar por la tabla)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultColor);
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(defaultColor); // Forzar a color base cada vez
        return this;
    }
}
