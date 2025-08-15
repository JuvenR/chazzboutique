/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mycompany.chazzboutiquenegocio.dtos.DetalleVentaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.ProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VentaDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import utils.ColorNameDetector;
import utils.ModeloTablaVentas;
import utils.SpinnerCellEditor;
import utils.SpinnerRenderer;

/**
 *
 * @author carli
 */
public class PanelVenta extends javax.swing.JPanel {

    FrmPrincipal frmPrincipal;
    DefaultTableModel modelo;
    private boolean descuentoAplicado = false;
    private BigDecimal descuentoActual = BigDecimal.ZERO;
    private BigDecimal totalSinDescuento;
    private List<String> filaCodigos = new ArrayList<>();
    private File ultimoTicketGenerado = null;
    private boolean busquedaPorNombre = false;
    private boolean usuarioCambioCombo = false;
    private boolean dialogoCerradoSinSeleccion = false;

    public PanelVenta(FrmPrincipal frmPrincipal) {
        initComponents();
        this.frmPrincipal = frmPrincipal;
        this.modelo = (DefaultTableModel) jTable.getModel();
        this.habilitarCampos(false);
        this.configurarFormatoMoneda(txtMontoPago);
        this.configurarFormatoMoneda(txtDescuento);
        cargarNombresProductos();
        configurarBusquedaToggle();
        btnAplicarDescuento.setEnabled(false);

        // En el constructor, después de inicializar el spinner:
        spnCantidad.setModel(new SpinnerNumberModel(1, 1, 100, 1)); // Valores de 1 a 100, incremento de 1
        // En el constructor, después de inicializar el spinner:
        spnCantidad.addChangeListener(e -> {
            // Verificar si hay un ítem seleccionado en el JComboBox
            if (cbNombreProducto.getSelectedItem() != null
                    && !cbNombreProducto.getSelectedItem().toString().isEmpty()) {
                btnAgregar.setEnabled(true);
            } else {
                btnAgregar.setEnabled(false);
            }

        });

// Configurar el editor del spinner para capturar Enter
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spnCantidad.getEditor();
        editor.getTextField().addActionListener(e -> {
            if (btnAgregar.isEnabled()) {
                agregarProductoATabla();
            }
        });
        cbNombreProducto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                usuarioCambioCombo = true;
            }
        });
        String[] columnNames = {"NOMBRE PRODUCTO", "CANTIDAD", "PRECIO UNITARIO", "SUBTOTAL"};
        modelo = new ModeloTablaVentas(columnNames, 0);
        jTable.setModel(modelo);

        // Configurar spinner para columna de cantidad (columna 1)
        jTable.getColumnModel().getColumn(1).setCellEditor(new SpinnerCellEditor());
        jTable.getColumnModel().getColumn(1).setCellRenderer(new SpinnerRenderer());
        jTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Ajustar altura de filas para el spinner
        jTable.setRowHeight(40);

        // Configurar header
        JTableHeader header = jTable.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Solo agregar directamente si estamos en modo NOMBRE y hay un producto seleccionado
                if (busquedaPorNombre && cbNombreProducto.getSelectedItem() != null) {
                    agregarProductoATabla();
                } else {
                    // Modo CÓDIGO: Siempre buscar primero
                    buscarProductoPorCodigo();
                }
            }
        });

        // Agregar ActionListener al botón Agregar
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarProductoATabla();
            }
        });

        jTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 1) { // Si cambió la columna CANTIDAD
                int row = e.getFirstRow();
                actualizarSubtotal(row);
                actualizarTotales();
            }
        });

        jTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    int cantidad = Integer.parseInt(getCellEditorValue().toString());
                    if (cantidad <= 0) {
                        throw new NumberFormatException();
                    }
                    return super.stopCellEditing();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Ingrese un número válido > 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return false; // Cancela la edición
                }
            }
        });

        btnEliminar.addActionListener(e -> eliminarFilasSeleccionadas());
        btnBorrarTabla.addActionListener(e -> {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea vaciar toda la tabla?",
                    "Confirmar limpieza",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                modelo.setRowCount(0);
                filaCodigos.clear();
                descuentoAplicado = false;
                descuentoActual = BigDecimal.ZERO;
                btnAplicarDescuento.setSelected(false);
                actualizarTotales();
            }
        });

        // Listener para el botón de aplicar descuento
        btnAplicarDescuento.addActionListener(e -> aplicarDescuento());

// Listener para el campo de monto de pago
        txtMontoPago.addActionListener(e -> calcularCambio());
        txtMontoPago.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                calcularCambio();
            }
        });

// Listener para el campo de descuento
        txtDescuento.addActionListener(e -> aplicarDescuento());
        botonMenu3.addActionListener(e -> registrarVenta());

// En el constructor, después de configurar txtDescuento:
        txtDescuento.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                actualizarEstadoBotonDescuento();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                actualizarEstadoBotonDescuento();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                actualizarEstadoBotonDescuento();
            }
        });
        // Aplicar filtros después de inicializar los campos
        ((AbstractDocument) txtDescuento.getDocument()).setDocumentFilter(new NumerosDecimalesFilter());
        ((AbstractDocument) txtMontoPago.getDocument()).setDocumentFilter(new NumerosDecimalesFilter());

        btnImprimir.addActionListener(e -> {
            if (ultimoTicketGenerado == null) {
                JOptionPane.showMessageDialog(this,
                        "No hay tickets generados en esta sesión",
                        "Sin tickets",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                if (ultimoTicketGenerado.exists()) {
                    Desktop.getDesktop().open(ultimoTicketGenerado);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El archivo del ticket ha sido eliminado",
                            "Ticket no encontrado",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al abrir el ticket: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private void configurarBusquedaToggle() {
        btnBusquedaNombre.addActionListener(e -> {
            busquedaPorNombre = btnBusquedaNombre.isSelected();
            actualizarModoBusqueda();
            limpiarCampos(); // Método nuevo que limpiará todo
        });

        // Configurar listener para el combo box
        // En el constructor, modificar el ActionListener del combo box:
        // En el constructor:
        cbNombreProducto.addActionListener(e -> {
            // Solo procesar si fue un cambio iniciado por el usuario
            // y no fue un cierre sin selección
            if (!busquedaPorNombre || !usuarioCambioCombo || dialogoCerradoSinSeleccion) {
                return;
            }

            // Restablecer bandera
            usuarioCambioCombo = false;

            Object selected = cbNombreProducto.getSelectedItem();
            if (selected != null && !selected.toString().isEmpty()) {
                buscarVariantesPorNombre();
            }
        });
    }

    private void actualizarModoBusqueda() {
        txtCodigo.setEnabled(!busquedaPorNombre);
        cbNombreProducto.setEnabled(busquedaPorNombre);

        if (busquedaPorNombre) {
            cbNombreProducto.requestFocus();
        } else {
            txtCodigo.requestFocus();
        }
    }

    private void buscarVariantesPorNombre() {
        String nombre = ((JTextField) cbNombreProducto.getEditor().getEditorComponent())
                .getText().trim();

        if (nombre.isEmpty()) {
            return;
        }

        try {
            List<ProductoDTO> productos = frmPrincipal.getProductoNegocio().buscarPorNombre(nombre);
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron productos con ese nombre",
                        "Búsqueda",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Abrir diálogo solo al ejecutar esta acción (botón o Enter)
            mostrarDialogoVariantes(productos);
            cbNombreProducto.hidePopup();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en búsqueda: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarNombresProductos() {
        try {
            List<ProductoDTO> productos = frmPrincipal.getProductoNegocio().obtenerTodosProductos();
            cbNombreProducto.removeAllItems();
            cbNombreProducto.setEditable(true);

            // Guardar la lista completa
            List<String> nombresProductos = new ArrayList<>();
            for (ProductoDTO producto : productos) {
                nombresProductos.add(producto.getNombreProducto());
            }
            cbNombreProducto.putClientProperty("fullList", nombresProductos);

            // Configurar el editor y el filtrado
            JTextField editor = (JTextField) cbNombreProducto.getEditor().getEditorComponent();
            editor.getDocument().addDocumentListener(new DocumentListener() {
                private Timer timer;

                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (dialogoCerradoSinSeleccion) {
                        return; // No hacer nada si se cerró sin selección
                    }
                    usuarioCambioCombo = true;
                    scheduleFilter();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (dialogoCerradoSinSeleccion) {
                        dialogoCerradoSinSeleccion = false; // Resetear después de borrar
                        return;
                    }
                    usuarioCambioCombo = true;
                    scheduleFilter();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (dialogoCerradoSinSeleccion) {
                        return;
                    }
                    usuarioCambioCombo = true;
                    scheduleFilter();
                }

                private void scheduleFilter() {
                    if (timer != null) {
                        timer.stop();
                    }
                    timer = new Timer(300, evt -> {
                        if (usuarioCambioCombo && !dialogoCerradoSinSeleccion) {
                            filterComboBox();
                        }
                        timer.stop();
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            });
            // Cargar todos los items inicialmente
            nombresProductos.forEach(cbNombreProducto::addItem);
            cbNombreProducto.setSelectedIndex(-1);

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterComboBox() {
        JTextField editor = (JTextField) cbNombreProducto.getEditor().getEditorComponent();
        String filterText = editor.getText().toLowerCase();

        @SuppressWarnings("unchecked")
        List<String> fullList = (List<String>) cbNombreProducto.getClientProperty("fullList");
        if (fullList == null) {
            return;
        }

        // Usar SwingWorker para el filtrado en segundo plano
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return fullList.stream()
                        .filter(item -> item.toLowerCase().contains(filterText))
                        .collect(Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<String> filteredList = get();

                    // Evitar cambios innecesarios
                    if (filteredList.equals(getCurrentModelItems())) {
                        return;
                    }

                    // Actualizar el modelo en el EDT
                    SwingUtilities.invokeLater(() -> {
                        Object selected = cbNombreProducto.getSelectedItem();
                        cbNombreProducto.setModel(new DefaultComboBoxModel<>(filteredList.toArray(new String[0])));
                        cbNombreProducto.setSelectedItem(selected);
                        editor.setText(filterText);

                        if (!filterText.isEmpty() && cbNombreProducto.isShowing()) {
                            cbNombreProducto.showPopup();
                        } else {
                            cbNombreProducto.hidePopup();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private List<String> getCurrentModelItems() {
        List<String> items = new ArrayList<>();
        ComboBoxModel<String> model = cbNombreProducto.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            items.add(model.getElementAt(i));
        }
        return items;
    }

    private void registrarVenta() {
        try {
            // 1. Validar que haya productos en la venta
            if (filaCodigos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay productos en la venta",
                        "Venta vacía",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Obtener totales y validar pago
            BigDecimal total = obtenerTotalDeLabel(lblTotalResult);
            BigDecimal montoPago = obtenerTotalDeTextField(txtMontoPago);

            if (montoPago.compareTo(total) < 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Pago insuficiente. Falta: %s",
                                formatoMoneda(total.subtract(montoPago))),
                        "Error en pago",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Crear DTO de venta
            VentaDTO ventaDTO = new VentaDTO();
            ventaDTO.setUsuarioId(frmPrincipal.getUsuarioRegistrado().getId());
            ventaDTO.setFecha(LocalDate.now());
            ventaDTO.setTotal(total);
            ventaDTO.setMontoPago(montoPago);
            ventaDTO.setCambio(montoPago.subtract(total));
            ventaDTO.setEstado("COMPLETADA");
            ventaDTO.setDescuento(descuentoAplicado ? descuentoActual : BigDecimal.ZERO);

            // 4. Crear lista de detalles usando el mapa
            List<DetalleVentaDTO> detalles = new ArrayList<>();

            for (int i = 0; i < modelo.getRowCount(); i++) {
                String codigoBarras = filaCodigos.get(i);
                if (codigoBarras == null) {
                    continue;
                }

                VarianteProductoDTO variante = frmPrincipal.getVarianteProductoNegocio().obtenerVariantePorCodigoBarra(codigoBarras);
                if (variante == null) {
                    throw new NegocioException("Producto con código " + codigoBarras + " ya no disponible");
                }

                DetalleVentaDTO detalle = new DetalleVentaDTO();
                detalle.setVarianteProductoId(variante.getId());
                detalle.setCodigoVariante(codigoBarras);
                detalle.setCantidad((Integer) jTable.getValueAt(i, 1)); // Columna CANTIDAD
                detalle.setPrecioUnitario(obtenerValorMonedaDeTabla(jTable.getValueAt(i, 2))); // Columna PRECIO UNITARIO

                detalles.add(detalle);
            }

            ventaDTO.setDetalles(detalles);
            System.out.println(ventaDTO.toString());
            // 5. Registrar la venta
            VentaDTO ventaregistrada = frmPrincipal.getVentaNegocio().registrarVenta(ventaDTO);

            // 6. Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "Venta registrada exitosamente\nNúmero de venta: " + ventaregistrada.getId(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            imprimirTicket(ventaregistrada);
            // 7. Resetear la venta
            resetearVenta();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar venta: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetearVenta() {
        modelo.setRowCount(0);
        filaCodigos.clear();
        if (descuentoAplicado) {
            btnAplicarDescuento.setClicked(false);
        }
        // Resetear campos monetarios usando el formateador
        reiniciarCampoMoneda(txtMontoPago);
        reiniciarCampoMoneda(txtDescuento);

        lblTotalResult.setText("$0.00");
        lblTotalResult2.setText("$0.00");
        jLabel13.setText("$0.00");

        limpiarCampos();

        // Resetear estado
        descuentoAplicado = false;
        descuentoActual = BigDecimal.ZERO;
        btnAplicarDescuento.setSelected(false);
        btnAplicarDescuento.setEnabled(false);

        txtCodigo.requestFocus();
    }

    private void reiniciarCampoMoneda(JTextField campo) {
        campo.setText("0.00"); // Establecer valor numérico
        formatearCampoMoneda(campo); // Aplicar formato manualmente
    }

    private void formatearCampoMoneda(JTextField textField) {
        String texto = textField.getText()
                .replace("$", "")
                .replace(",", "")
                .trim();

        try {
            // Lógica de formato existente del focusLost
            if (texto.isEmpty() || texto.equals(".")) {
                texto = "0.00";
            } else if (texto.startsWith(".")) {
                texto = "0" + texto;
            } else if (!texto.contains(".")) {
                texto += ".00";
            } else if (texto.split("\\.")[1].length() == 1) {
                texto += "0";
            }

            BigDecimal amount = new BigDecimal(texto)
                    .setScale(2, RoundingMode.HALF_UP);

            textField.setText(formatoMoneda(amount));
        } catch (NumberFormatException e) {
            textField.setText("$0.00");
        }
    }

    private BigDecimal obtenerTotalDeTextField(JTextField textField) {
        try {
            String texto = textField.getText()
                    .replace("$", "")
                    .replace(",", "")
                    .trim();
            return texto.isEmpty() ? BigDecimal.ZERO : new BigDecimal(texto);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private void eliminarFilasSeleccionadas() {
        int[] filasSeleccionadas = jTable.getSelectedRows();

        if (filasSeleccionadas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione al menos un producto para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar los productos seleccionados?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Ordenar de mayor a menor para evitar problemas con índices
            Arrays.sort(filasSeleccionadas);

            for (int i = filasSeleccionadas.length - 1; i >= 0; i--) {
                int fila = filasSeleccionadas[i];
                if (fila >= 0 && fila < modelo.getRowCount()) {
                    // Eliminar del mapa
                    filaCodigos.remove(fila);
                    // Eliminar fila de la tabla
                    modelo.removeRow(fila);
                }
            }

            // Reindexar el mapa después de eliminar filas
            actualizarTotales();
        }
    }

    private void agregarProductoATabla() {
        try {
            if (busquedaPorNombre) {
                if (cbNombreProducto.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this,
                            "Seleccione un producto de la lista",
                            "Producto no seleccionado",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Verificar si ya tenemos un código seleccionado (después de haber elegido una variante)
                String codigo = txtCodigo.getText().trim();
                if (!codigo.isEmpty()) {
                    // Si ya tenemos un código, agregar el producto directamente
                    int cantidad = (int) spnCantidad.getValue();
                    agregarProductoPorCodigo(codigo, cantidad);
                } else {
                    // Si no hay código, mostrar diálogo para seleccionar variante
                    buscarVariantesPorNombre();
                }
            } else {
                // Lógica original para búsqueda por código
                String codigo = txtCodigo.getText().trim();
                if (codigo.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Escanee un código o active la búsqueda por nombre",
                            "Campo vacío",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int cantidad = (int) spnCantidad.getValue();
                agregarProductoPorCodigo(codigo, cantidad);
            }
        } catch (Exception ex) {
            manejarErrorGenerico(ex);
        }
    }

    private void agregarProductoPorCodigo(String codigo, int cantidad) {
        try {
            if (filaCodigos.contains(codigo)) {
                int opcion = JOptionPane.showConfirmDialog(this,
                        "Este producto ya está en la venta. ¿Desea agregar más unidades?",
                        "Producto duplicado",
                        JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    int rowIndex = filaCodigos.indexOf(codigo);
                    int cantidadActual = (int) jTable.getValueAt(rowIndex, 1);
                    int cantidadNueva = cantidad;

                    // Actualizar cantidad sumando la nueva
                    jTable.setValueAt(cantidadActual + cantidadNueva, rowIndex, 1);
                    actualizarSubtotal(rowIndex);
                    actualizarTotales();
                }
                limpiarCampos();
                return;
            }

            VarianteProductoDTO variante = frmPrincipal.getVarianteProductoNegocio().obtenerVariantePorCodigoBarra(codigo);
            validarYAgregarVariante(variante, codigo, cantidad);

            if (busquedaPorNombre) {
                limpiarSeleccionCombo();
                actualizarModoBusqueda();
            }
        } catch (NegocioException ex) {
            manejarErrorNegocio(ex);
        } catch (Exception ex) {
            manejarErrorGenerico(ex);
        }
    }

    private void validarYAgregarVariante(VarianteProductoDTO variante, String codigo, int cantidad) throws NegocioException {
        if (variante == null) {
            throw new NegocioException("El producto no existe");
        }

        ProductoDTO producto = frmPrincipal.getProductoNegocio().buscarPorId(variante.getProductoId());
        if (cantidad <= 0) {
            throw new NegocioException("Cantidad inválida");
        }
        if (variante.getStock() < cantidad) {
            throw new NegocioException(String.format("Stock insuficiente. Disponible: %d", variante.getStock()));
        }

        agregarFilaTabla(producto, variante, cantidad, codigo);
        actualizarInterfaz();
    }

    private void agregarFilaTabla(ProductoDTO producto, VarianteProductoDTO variante, int cantidad, String codigo) {
        BigDecimal precio = variante.getPrecioVenta();
        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

        modelo.addRow(new Object[]{
            producto.getNombreProducto(),
            cantidad,
            formatoMoneda(precio),
            formatoMoneda(subtotal)
        });

        filaCodigos.add(codigo);
        actualizarTotales();
    }

    private void actualizarInterfaz() {
        limpiarCampos();
        if (busquedaPorNombre) {
            cbNombreProducto.requestFocus();
        } else {
            txtCodigo.requestFocus();
        }
    }

    private void manejarErrorNegocio(NegocioException ex) {
        JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error en operación",
                JOptionPane.ERROR_MESSAGE);
        limpiarCampos();
    }

    private void manejarErrorGenerico(Exception ex) {
        Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, null, ex);
        JOptionPane.showMessageDialog(this,
                "Error inesperado: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
// Método para actualizar los totales

    private BigDecimal obtenerValorMoneda(JTextField textField) {
        try {
            String text = textField.getText().replace("$", "").replace(",", "").trim();
            if (text.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null; // Retornar null en caso de error
        }
    }

    private void aplicarDescuento() {
        try {
            // Verificar si hay productos en la tabla
            if (modelo.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay productos en la venta para aplicar descuento",
                        "Venta vacía",
                        JOptionPane.WARNING_MESSAGE);
                btnAplicarDescuento.setSelected(false);
                btnAplicarDescuento.setIcon(btnAplicarDescuento.getNormalIcon());
                return;
            }

            BigDecimal totalActual = obtenerTotalDeLabel(lblTotalResult2);
            BigDecimal descuento = obtenerValorMoneda(txtDescuento);

            // Validar que haya un total y un descuento válido
            if (totalActual.compareTo(BigDecimal.ZERO) <= 0 || descuento == null || descuento.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El total debe ser mayor a cero y el descuento debe ser un valor válido",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                btnAplicarDescuento.setSelected(false);
                return;
            }

            // Validar que el descuento no sea mayor al total
            if (descuento.compareTo(totalActual) > 0) {
                JOptionPane.showMessageDialog(this,
                        "El descuento no puede ser mayor al total",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                btnAplicarDescuento.setSelected(false);
                return;
            }

            if (!descuentoAplicado) {
                // Aplicar descuento
                this.descuentoActual = descuento;
                BigDecimal totalConDescuento = totalActual.subtract(descuento);

                // Asegurarnos de que el total no sea negativo
                if (totalConDescuento.compareTo(BigDecimal.ZERO) < 0) {
                    totalConDescuento = BigDecimal.ZERO;
                }

                lblTotalResult.setText(formatoMoneda(totalConDescuento));

                // Cambiar estado
                descuentoAplicado = true;
                btnAplicarDescuento.setSelected(true);
            } else {
                // Quitar descuento
                lblTotalResult.setText(formatoMoneda(totalActual));
                descuentoAplicado = false;
                btnAplicarDescuento.setSelected(false);
                descuentoActual = BigDecimal.ZERO;
            }

            // Recalcular cambio si hay monto de pago
            calcularCambio();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en el formato del descuento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            btnAplicarDescuento.setSelected(false);
        }
    }

    private void calcularCambio() {
        try {
            // Obtener el total (con descuento si aplica)
            String totalStr = lblTotalResult.getText().replace("$", "").replace(",", "");
            BigDecimal total = new BigDecimal(totalStr);

            // Obtener monto de pago
            BigDecimal montoPago = obtenerValorMoneda(txtMontoPago);

            // Calcular cambio
            BigDecimal cambio = montoPago.subtract(total);

            // Mostrar solo si es positivo
            if (cambio.compareTo(BigDecimal.ZERO) > 0) {
                jLabel13.setText(formatoMoneda(cambio));
            } else {
                jLabel13.setText("$0.00"); // Ocultar valores negativos
            }

        } catch (NumberFormatException ex) {
            jLabel13.setText("$0.00");
        }
    }

    private void actualizarTotales() {
        // Calcular total sin descuentos
        totalSinDescuento = calcularTotalTabla();

        // Mostrar valores - asegurar que no sea negativo
        BigDecimal totalMostrar = totalSinDescuento.compareTo(BigDecimal.ZERO) > 0
                ? totalSinDescuento : BigDecimal.ZERO;
        lblTotalResult2.setText(formatoMoneda(totalMostrar));

        if (descuentoAplicado) {
            // Si hay descuento aplicado, mantenerlo pero asegurar que no sea negativo
            BigDecimal totalConDescuento = totalSinDescuento.subtract(descuentoActual);
            BigDecimal totalFinal = totalConDescuento.compareTo(BigDecimal.ZERO) > 0
                    ? totalConDescuento : BigDecimal.ZERO;
            lblTotalResult.setText(formatoMoneda(totalFinal));
        } else {
            // Sin descuento - asegurar que no sea negativo
            BigDecimal totalFinal = totalSinDescuento.compareTo(BigDecimal.ZERO) > 0
                    ? totalSinDescuento : BigDecimal.ZERO;
            lblTotalResult.setText(formatoMoneda(totalFinal));
        }

        // Actualizar estado del botón de descuento
        actualizarEstadoBotonDescuento();

        calcularCambio();
    }

    private void actualizarEstadoBotonDescuento() {
        BigDecimal total = obtenerTotalDeLabel(lblTotalResult2);
        BigDecimal descuento = obtenerValorMoneda(txtDescuento);

        // Habilitar solo si hay total positivo y descuento válido (mayor a 0 y menor que el total)
        boolean habilitar = total.compareTo(BigDecimal.ZERO) > 0
                && descuento != null
                && descuento.compareTo(BigDecimal.ZERO) > 0
                && descuento.compareTo(total) <= 0;

        btnAplicarDescuento.setEnabled(habilitar);
    }

    private BigDecimal calcularTotalTabla() {
        if (jTable.getRowCount() == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"));

        for (int i = 0; i < jTable.getRowCount(); i++) {
            try {
                String subtotalStr = jTable.getValueAt(i, 3).toString(); // Columna SUBTOTAL
                Number subtotalNumber = nf.parse(subtotalStr);
                total = total.add(new BigDecimal(subtotalNumber.toString()));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al calcular total: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return total;
    }

    private void buscarProductoPorCodigo() {
        String codigo = txtCodigo.getText().trim();

        if (codigo.isEmpty()) {
            return;
        }

        try {
            // Buscar la variante del producto
            VarianteProductoDTO variante = frmPrincipal.getVarianteProductoNegocio()
                    .obtenerVariantePorCodigoBarra(codigo);

            if (variante == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró ningún producto con ese código",
                        "Producto no encontrado",
                        JOptionPane.WARNING_MESSAGE);
                limpiarCampos();
                habilitarCampos(false);
                return;
            }

            // Buscar información completa del producto
            ProductoDTO producto = frmPrincipal.getProductoNegocio().buscarPorId(variante.getProductoId());

            if (producto == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró información del producto",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                limpiarCampos();
                habilitarCampos(false);
                return;
            }

            // Actualizar combo box con el nombre del producto
            for (int i = 0; i < cbNombreProducto.getItemCount(); i++) {
                if (cbNombreProducto.getItemAt(i).equals(producto.getNombreProducto())) {
                    cbNombreProducto.setSelectedIndex(i);
                    break;
                }
            }

            // Actualizar la interfaz con los datos del producto
            txtPrecio.setText(formatoMoneda(variante.getPrecioVenta()));
            spnCantidad.setValue(1);

            // Configurar color del botón
            if (variante.getColor() != null && !variante.getColor().isEmpty()) {
                try {
                    // Intentar decodificar color hexadecimal (#RRGGBB)
                    btnColor.setBackground(Color.decode(variante.getColor()));
                } catch (NumberFormatException e) {
                    try {
                        // Intentar color por nombre (RED, BLUE, etc.)
                        btnColor.setBackground((Color) Color.class.getField(variante.getColor().toUpperCase()).get(null));
                    } catch (Exception ex) {
                        // Si falla, poner blanco
                        btnColor.setBackground(Color.WHITE);
                    }
                }
            } else {
                btnColor.setBackground(Color.WHITE);
            }

            // Forzar que se vea el color
            btnColor.setOpaque(true);
            btnColor.repaint();

            habilitarCampos(true);

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar producto: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            habilitarCampos(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al buscar producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            limpiarCampos();
            habilitarCampos(false);
        }
    }

    private void actualizarSubtotal(int row) {
        try {
            // Obtener cantidad (nuevo valor editado)
            int cantidad = Integer.parseInt(jTable.getValueAt(row, 1).toString());

            // Obtener precio unitario (sin formato de moneda)
            String precioStr = jTable.getValueAt(row, 2).toString().replace("$", "").replace(",", "");
            BigDecimal precio = new BigDecimal(precioStr);

            // Calcular nuevo subtotal
            BigDecimal subtotal = precio.multiply(new BigDecimal(cantidad));

            // Actualizar celda de SUBTOTAL
            jTable.setValueAt(formatoMoneda(subtotal), row, 3);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        lblTotal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTotalResult = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtMontoPago = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtDescuento = new javax.swing.JTextField();
        jPanel12 = new javax.swing.JPanel();
        btnAplicarDescuento = new utils.BotonToggle();
        botonMenu3 = new utils.BotonMenu();
        btnBorrarTabla = new utils.BotonMenu();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        txtCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cbNombreProducto = new javax.swing.JComboBox<>();
        btnBusquedaNombre = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        txtPrecio = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnColor = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        btnAgregar = new utils.BotonMenu();
        spnCantidad = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        lblTotal2 = new javax.swing.JLabel();
        lblTotalResult2 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btnImprimir = new utils.BotonMenu();
        btnEliminar = new utils.BotonMenu();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(560, 680));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(305, 175));

        lblTotal.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblTotal.setText("TOTAL");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(156, 156, 156));
        jLabel9.setText("MONTO PAGO");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(156, 156, 156));
        jLabel10.setText("CAMBIO");

        lblTotalResult.setBackground(new java.awt.Color(176, 50, 53));
        lblTotalResult.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        lblTotalResult.setForeground(new java.awt.Color(176, 50, 53));
        lblTotalResult.setText("$0.00");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel13.setText("$0.00");

        txtMontoPago.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        txtMontoPago.setPreferredSize(new java.awt.Dimension(92, 31));
        txtMontoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMontoPagoActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(156, 156, 156));
        jLabel8.setText("DESCUENTO");

        txtDescuento.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        txtDescuento.setPreferredSize(new java.awt.Dimension(92, 31));
        txtDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescuentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(lblTotal)
                        .addGap(38, 38, 38)
                        .addComponent(lblTotalResult)
                        .addGap(61, 61, 61)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtMontoPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal)
                    .addComponent(lblTotalResult))
                .addContainerGap())
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        btnAplicarDescuento.setClickedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/removerDescuento.png"))); // NOI18N
        btnAplicarDescuento.setNormalIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnDescuento.png"))); // NOI18N
        btnAplicarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAplicarDescuentoActionPerformed(evt);
            }
        });

        botonMenu3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnPagar.png"))); // NOI18N
        botonMenu3.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnPagar.png"))); // NOI18N

        btnBorrarTabla.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnBorrarProductos.png"))); // NOI18N
        btnBorrarTabla.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnBorrarProductos.png"))); // NOI18N
        btnBorrarTabla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarTablaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(169, 169, 169)
                .addComponent(btnAplicarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnBorrarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(botonMenu3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(btnAplicarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(botonMenu3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBorrarTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(316, 316, 316))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.LINE_END);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1701, 400));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        jLabel1.setText("Punto de Venta");

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(330, 128));

        txtCodigo.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        txtCodigo.setPreferredSize(new java.awt.Dimension(311, 60));
        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel2.setText("Codigo");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel3.add(jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(330, 158));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel3.setText("Nombre");

        cbNombreProducto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbNombreProducto.setPreferredSize(new java.awt.Dimension(311, 60));

        btnBusquedaNombre.setText("Habilitar");
        btnBusquedaNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusquedaNombreActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(266, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBusquedaNombre)
                    .addComponent(cbNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(5, 5, 5)
                .addComponent(cbNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBusquedaNombre)
                .addContainerGap())
        );

        jPanel3.add(jPanel6);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(330, 128));

        txtPrecio.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N
        txtPrecio.setPreferredSize(new java.awt.Dimension(311, 60));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel7.setText("Precio");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel3.add(jPanel9);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(510, 128));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel5.setText("Color");

        btnColor.setBackground(new java.awt.Color(249, 249, 249));
        btnColor.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(0, 0, 0), null, null));
        btnColor.setPreferredSize(new java.awt.Dimension(60, 60));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel6.setText("Cantidad");

        btnAgregar.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnAgregar.png"))); // NOI18N
        btnAgregar.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnAgregar.png"))); // NOI18N

        spnCantidad.setFont(new java.awt.Font("Segoe UI", 0, 20)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(btnColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnCantidad))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );

        jPanel3.add(jPanel8);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(611, 611, 611)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1703, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jTable.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jTable.setForeground(new java.awt.Color(176, 50, 53));
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOMBRE PRODUCTO", "CANTIDAD", "PRECIO UNITARIO", "SUBTOTAL"
            }
        ));
        jTable.setGridColor(new java.awt.Color(204, 204, 204));
        jTable.setRowHeight(40);
        jScrollPane1.setViewportView(jTable);

        lblTotal2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTotal2.setText("TOTAL:");

        lblTotalResult2.setBackground(new java.awt.Color(176, 50, 53));
        lblTotalResult2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTotalResult2.setForeground(new java.awt.Color(176, 50, 53));
        lblTotalResult2.setText("$0.00");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 968, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotal2)
                .addGap(38, 38, 38)
                .addComponent(lblTotalResult2)
                .addGap(90, 90, 90))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal2)
                    .addComponent(lblTotalResult2))
                .addContainerGap())
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        btnImprimir.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnImprimir.png"))); // NOI18N
        btnImprimir.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnImprimir.png"))); // NOI18N

        btnEliminar.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnBasura.png"))); // NOI18N
        btnEliminar.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/btnBasura.png"))); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnImprimir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(119, 119, 119))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        add(jPanel5, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private BigDecimal obtenerValorMonedaDeTabla(Object valorCelda) {
        if (valorCelda == null) {
            return BigDecimal.ZERO;
        }

        try {
            // Convertir a String y limpiar formato
            String valorStr = valorCelda.toString()
                    .replace("$", "") // Quitar símbolo de peso
                    .replace(",", "") // Quitar separadores de miles
                    .trim();

            // Validar que no esté vacío
            if (valorStr.isEmpty()) {
                return BigDecimal.ZERO;
            }

            // Convertir a BigDecimal con precisión de 2 decimales
            return new BigDecimal(valorStr).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Valor monetario inválido: " + valorCelda.toString(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return BigDecimal.ZERO;
        }
    }
    private void txtMontoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMontoPagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMontoPagoActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescuentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescuentoActionPerformed

    private void btnAplicarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAplicarDescuentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAplicarDescuentoActionPerformed

    private void btnBorrarTablaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarTablaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBorrarTablaActionPerformed

    private void btnBusquedaNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusquedaNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBusquedaNombreActionPerformed
    private BigDecimal obtenerTotalDeLabel(JLabel label) {
        try {
            String texto = label.getText()
                    .replace("$", "")
                    .replace(",", "")
                    .trim();

            if (texto.isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String formatoMoneda(BigDecimal cantidad) {
        // 1. Create a Mexican peso locale (modern approach)
        Locale mexico = Locale.forLanguageTag("es-MX");

        // 2. Get currency formatter
        NumberFormat formatter = NumberFormat.getCurrencyInstance(mexico);

        // 3. (Optional) Force Mexican peso symbol if needed
        formatter.setCurrency(Currency.getInstance("MXN"));

        return formatter.format(cantidad);
    }

    private void configurarFormatoMoneda(JTextField textField) {
        textField.setText("$0.00");

        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumerosDecimalesFilter());

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().equals("$0.00")) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                formatearCampoMoneda(textField);
            }
        });
    }

    private void habilitarCampos(boolean habilitar) {
        txtPrecio.setEnabled(false);
        spnCantidad.setEnabled(habilitar);
        btnAgregar.setEnabled(habilitar); // Habilitar el botón de agregar
        btnColor.setEnabled(true);

        // Mantener habilitados según modo búsqueda
        txtCodigo.setEnabled(!busquedaPorNombre);
        cbNombreProducto.setEnabled(busquedaPorNombre);

        if (habilitar) {
            spnCantidad.requestFocus();
        }
    }

    private void limpiarCampos() {
        cbNombreProducto.setSelectedIndex(-1); // Dejar el ComboBox sin selección
        txtCodigo.setText("");
        txtPrecio.setText("");
        spnCantidad.setValue(1);
        btnColor.setBackground(Color.WHITE);
    }

    private void imprimirTicket(VentaDTO ventaDTO) throws IOException {
        try {
            String fileName = "TicketVenta_" + ventaDTO.getId() + ".pdf";
            File ticketFile = new File(fileName);
            Document document = new Document(new Rectangle(227f, 700f));
            final float MARGIN = 15f;

            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            document.open();

            // Configurar fuentes
            Font headerFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
            Font smallFont = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL);

            // Encabezado
            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);

            try {
                Image logo = Image.getInstance("src/main/resources/images/chazzLogoBlack.png");
                logo.scaleToFit(100, 100);
                header.add(logo);
            } catch (Exception e) {
                Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, "Error cargando logo", e);
            }

            header.add(new Paragraph("CHAZZ BOUTIQUE", headerFont));
            header.add(new Paragraph("Calle Guillermo Prieto #339, Col. Centro, Los Mochis", normalFont));
            header.add(new Paragraph("Tel: +52 1 668 253 1651 | RFC: CHA220401XYZ", normalFont));
            header.add(new Paragraph("----------------------------------------------", normalFont));
            document.add(header);

            // Información de venta
            Paragraph saleInfo = new Paragraph();
            saleInfo.add(new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm")), normalFont));
            saleInfo.add(new Paragraph("Ticket: #" + ventaDTO.getId(), normalFont));
            saleInfo.add(new Paragraph("Vendedor: " + frmPrincipal.getUsuarioRegistrado().getNombreUsuario(), normalFont));
            saleInfo.add(new Paragraph("----------------------------------------------", normalFont));
            document.add(saleInfo);

            // Detalle de productos
            Paragraph products = new Paragraph();
            products.add(new Paragraph(String.format("%-25s %3s %10s %10s",
                    "ARTÍCULO", "CANT", "P.UNITARIO", "TOTAL"), boldFont));
            products.add(new Paragraph("----------------------------------------------", normalFont));

            for (DetalleVentaDTO detalle : ventaDTO.getDetalles()) {
                try {
                    VarianteProductoDTO producto = frmPrincipal.getVarianteProductoNegocio()
                            .obtenerVariantePorCodigoBarra(detalle.getCodigoVariante());

                    String nombre = producto.getCodigoBarra();
                    if (nombre.length() > 25) {
                        nombre = nombre.substring(0, 22) + "...";
                    }

                    String linea = String.format("%-25s %3d %10s %10s",
                            nombre,
                            detalle.getCantidad(),
                            formatoMonedaSimple(detalle.getPrecioUnitario()),
                            formatoMonedaSimple(detalle.getPrecioUnitario()
                                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))));

                    products.add(new Paragraph(linea, normalFont));

                } catch (NegocioException e) {
                    Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, "Error obteniendo producto", e);
                }
            }
            document.add(products);

            // Totales
            Paragraph totals = new Paragraph();
            totals.add(new Paragraph("----------------------------------------------", normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "SUBTOTAL:",
                    formatoMonedaSimple(totalSinDescuento)), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "DESCUENTO:",
                    formatoMonedaSimple(ventaDTO.getDescuento())), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "TOTAL:",
                    formatoMonedaSimple(ventaDTO.getTotal())), boldFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "PAGO CON:",
                    formatoMonedaSimple(ventaDTO.getMontoPago())), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "CAMBIO:",
                    formatoMonedaSimple(ventaDTO.getMontoPago().subtract(ventaDTO.getTotal()))), normalFont));
            totals.add(new Paragraph("----------------------------------------------\n", normalFont));
            document.add(totals);

            // Footer
            Paragraph footer = new Paragraph();
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.add(new Paragraph("¡Gracias por su preferencia!", smallFont));
            footer.add(new Paragraph("Devoluciones en 24 hrs con ticket y etiquetas", smallFont));
            footer.add(new Paragraph("@chazz.boutique", smallFont));

            try {
                Image qr = Image.getInstance("src/main/resources/images/QRreal.png");
                qr.scaleToFit(70, 70);
                footer.add(qr);
            } catch (Exception e) {
                Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, "Error QR", e);
            }

            document.add(footer);

            document.close();
            this.ultimoTicketGenerado = ticketFile;

            // Abrir automáticamente
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(fileName));
            }

        } catch (DocumentException | FileNotFoundException e) {
            Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, "Error generando ticket", e);
        }
    }
// Método auxiliar para formato monetario simple

    private String formatoMonedaSimple(BigDecimal cantidad) {
        return "$" + cantidad.setScale(2, RoundingMode.HALF_UP);
    }
    // Clase interna para filtrar números y puntos

    private void buscarProductoPorNombre() {
        String nombre = ((JTextField) cbNombreProducto.getEditor().getEditorComponent())
                .getText().trim();

        if (nombre.isEmpty()) {
            return;
        }

        try {
            List<ProductoDTO> productos = frmPrincipal.getProductoNegocio().buscarPorNombre(nombre);
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontraron productos con ese nombre",
                        "Búsqueda",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Abrir diálogo solo al ejecutar esta acción (botón o Enter)
            mostrarDialogoVariantes(productos);

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en búsqueda: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDialogoVariantes(List<ProductoDTO> productos) {
        JDialog dialog = new JDialog(frmPrincipal, true);
        dialog.setTitle("Variantes de: " + cbNombreProducto.getSelectedItem());

        dialogoCerradoSinSeleccion = false;

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("Color");
        model.addColumn("Talla");
        model.addColumn("Precio");
        model.addColumn("Stock");
        model.addColumn("Código");

        for (ProductoDTO producto : productos) {
            try {
                List<VarianteProductoDTO> variantes = frmPrincipal.getVarianteProductoNegocio()
                        .obtenerVariantesPorProducto(producto.getId());

                for (VarianteProductoDTO variante : variantes) {
                    model.addRow(new Object[]{
                        ColorNameDetector.getColorName(variante.getColor()),
                        variante.getTalla(),
                        formatoMoneda(variante.getPrecioVenta()),
                        variante.getStock(),
                        variante.getCodigoBarra()
                    });
                }
            } catch (NegocioException ex) {
                Logger.getLogger(PanelVenta.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo permitir selección simple

        // Ocultar columna de código
        table.removeColumn(table.getColumnModel().getColumn(4));

        // Botón Aceptar
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Seleccione una variante",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener el código de la variante seleccionada
            String codigo = (String) model.getValueAt(selectedRow, 4);

            // Cargar la variante en los campos pero no agregar a la tabla
            cargarVarianteSeleccionada(codigo);

            dialog.dispose();
        });

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> {
            dialogoCerradoSinSeleccion = true; // Marcar que se cerró sin selección
            dialog.dispose();
            limpiarSeleccionCombo(); // Limpiar la selección
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnAceptar);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Esto se ejecuta cuando se hace clic en la "X"
                dialogoCerradoSinSeleccion = true;
                limpiarSeleccionCombo();
                limpiarCampos();

            }

            @Override
            public void windowClosed(WindowEvent e) {
                // Esto se ejecuta después de que el diálogo se ha cerrado
                if (dialogoCerradoSinSeleccion) {
                    limpiarSeleccionCombo();
                    limpiarCampos();
                }
            }
        });
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setAlwaysOnTop(true); // Asegura que esté sobre otros componentes
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        dialog.toFront();
        dialog.requestFocus();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    private void cargarVarianteSeleccionada(String codigo) {
        try {
            VarianteProductoDTO variante = frmPrincipal.getVarianteProductoNegocio()
                    .obtenerVariantePorCodigoBarra(codigo);

            ProductoDTO producto = frmPrincipal.getProductoNegocio().buscarPorId(variante.getProductoId());

            // Actualizar interfaz
            txtCodigo.setText(codigo);
            txtPrecio.setText(formatoMoneda(variante.getPrecioVenta()));
            System.out.println("Color variante: '" + variante.getColor() + "'");

            // Configurar color
            try {
                btnColor.setBackground(Color.decode(variante.getColor()));
                btnColor.setOpaque(true);
                btnColor.repaint();
            } catch (Exception e) {
                btnColor.setBackground(Color.WHITE);
            }

            // Habilitar campos para permitir agregar manualmente
            habilitarCampos(true);
            spnCantidad.requestFocus();

        } catch (NegocioException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar variante: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarSeleccionCombo() {
        // Desactivar temporalmente la detección de cambios del usuario
        usuarioCambioCombo = false;

        SwingUtilities.invokeLater(() -> {
            cbNombreProducto.setSelectedIndex(-1);
            JTextField editor = (JTextField) cbNombreProducto.getEditor().getEditorComponent();
            editor.setText("");
            cbNombreProducto.hidePopup();

            // Restablecer detección para futuras interacciones
            usuarioCambioCombo = true;
        });
    }

    class NumerosDecimalesFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.insert(offset, string);

            if (esEntradaValida(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            StringBuilder sb = new StringBuilder();
            sb.append(fb.getDocument().getText(0, fb.getDocument().getLength()));
            sb.replace(offset, offset + length, text);

            if (esEntradaValida(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean esEntradaValida(String text) {
            // Permite números, un solo punto, y no permite punto al inicio
            return text.matches("^\\d*\\.?\\d*$") && !text.startsWith(".");
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private utils.BotonMenu botonMenu3;
    private utils.BotonMenu btnAgregar;
    private utils.BotonToggle btnAplicarDescuento;
    private utils.BotonMenu btnBorrarTabla;
    private javax.swing.JRadioButton btnBusquedaNombre;
    private javax.swing.JButton btnColor;
    private utils.BotonMenu btnEliminar;
    private utils.BotonMenu btnImprimir;
    private javax.swing.JComboBox<String> cbNombreProducto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JLabel lblTotalResult;
    private javax.swing.JLabel lblTotalResult2;
    private javax.swing.JSpinner spnCantidad;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescuento;
    private javax.swing.JTextField txtMontoPago;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
