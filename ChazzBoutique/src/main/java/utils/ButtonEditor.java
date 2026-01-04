package utils;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ButtonEditor extends DefaultCellEditor {

    protected JButton button;
    private String label;
    private boolean isPushed;
    private Consumer<Integer> onClick;
    private JTable table;

    private Color defaultColor;
    private Color hoverColor;

    public ButtonEditor(JCheckBox checkBox, String label, Consumer<Integer> onClick) {
        super(checkBox);
        this.button = new JButton();
        this.label = label;
        this.onClick = onClick;

        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Colores según el tipo de botón
        if ("Editar".equalsIgnoreCase(label)) {
            defaultColor = new Color(33, 150, 243);
            hoverColor = new Color(66, 165, 245);
        } else if ("Eliminar".equalsIgnoreCase(label)) {
            defaultColor = new Color(244, 67, 54);
            hoverColor = new Color(239, 83, 80);
        } else {
            defaultColor = new Color(96, 125, 139);
            hoverColor = new Color(120, 144, 156);
        }

        button.setBackground(defaultColor);

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }
        });

        button.addActionListener((ActionEvent e) -> {
            fireEditingStopped(); // Detener edición de celda
            if (table != null) {
                onClick.accept(table.getSelectedRow()); // Ejecutar acción
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        button.setText(label);
        button.setBackground(defaultColor); // Reiniciar color por si quedó en hover
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
