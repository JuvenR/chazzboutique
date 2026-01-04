package presentacion;

import com.itextpdf.text.Font;
import com.mycompany.chazzboutiquenegocio.dtos.CategoriaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.ProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.ProveedorDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utils.ButtonEditor;
import utils.ButtonRenderer;

/**
 *
 * @author
 */
public class PnlAnadirProducto extends javax.swing.JPanel {

    private FrmPrincipal frmPrincipal;
    private DefaultTableModel tableModel;
    private ProductoDTO productoSeleccionado = null;

    public PnlAnadirProducto(FrmPrincipal frmPrincipal) {
        initComponents();
        this.setSize(new Dimension(1701, 1080));
        this.frmPrincipal = frmPrincipal;

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Fecha", "Categoría", "Proveedor", "Editar", "Eliminar", "Variante"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo las columnas "Editar" (índice 6) y "Eliminar" (índice 7) son editables
                return column == 6 || column == 7 || column == 8;
            }
        };

        tblProductos.setModel(tableModel);
        tblProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof String && column != 6 && column != 7) { // excluye columnas "Editar" y "Eliminar"
                    String textoOriginal = (String) value;
                    String textoFormateado = formatearTitulo(textoOriginal);
                    setText(textoFormateado);
                }

                return c;
            }
        });

        // Ajustar altura de filas para el spinner
        tblProductos.setRowHeight(40);

        // Configurar header
        JTableHeader header = tblProductos.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 24));
        // Configurar renderers y editores para botones
        tblProductos.getColumn("Editar").setCellRenderer(new ButtonRenderer("Editar"));
        tblProductos.getColumn("Eliminar").setCellRenderer(new ButtonRenderer("Eliminar"));
        tblProductos.getColumn("Variante").setCellRenderer(new ButtonRenderer("Variante"));
        tblProductos.getColumn("Variante").setCellEditor(
                new ButtonEditor(new JCheckBox(), "Variante", row -> abrirVarianteProducto(row))
        );
        tblProductos.getColumn("Editar").setCellEditor(
                new ButtonEditor(new JCheckBox(), "Editar", row -> editarProducto(row))
        );
        tblProductos.getColumn("Eliminar").setCellEditor(
                new ButtonEditor(new JCheckBox(), "Eliminar", row -> eliminarProducto(row))
        );

        tblProductos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int row = tblProductos.getSelectedRow();
                if (row != -1) {
                    txtNombreProducto.setText(tableModel.getValueAt(row, 1).toString());
                    txtDescripcion.setText(tableModel.getValueAt(row, 2).toString());

                    Object proveedorValue = tableModel.getValueAt(row, 3);
                    Object categoriaValue = tableModel.getValueAt(row, 4);

                    for (int i = 0; i < cbxProveedor.getItemCount(); i++) {
                        if (cbxProveedor.getItemAt(i).toString().equals(proveedorValue.toString())) {
                            cbxProveedor.setSelectedIndex(i);
                            break;
                        }
                    }

                    // Establecer la categoría en el comboBox
                    for (int i = 0; i < cbxCategoria.getItemCount(); i++) {
                        if (cbxCategoria.getItemAt(i).toString().equals(categoriaValue.toString())) {
                            cbxCategoria.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        cargarCategorias();
        cargarProveedores();
        cargarProductos();
    }

    private void abrirVarianteProducto(int row) {
        Long idProducto = (Long) tableModel.getValueAt(row, 0);
        PnlAnadirVarianteProducto pnl = new PnlAnadirVarianteProducto(idProducto, frmPrincipal.getVarianteProductoNegocio());
        frmPrincipal.pintarPanelPrincipal(pnl);
    }

    private void cargarCategorias() {
        try {
            List<CategoriaDTO> categorias = frmPrincipal.getCategoriaNegocio().obtenerCategorias();
            cbxCategoria.removeAllItems();
            for (CategoriaDTO categoria : categorias) {
                cbxCategoria.addItem(categoria);
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar categorías: " + e.getMessage());
        }
    }

    private void cargarProveedores() {
        try {
            List<ProveedorDTO> proveedores = frmPrincipal.getProveedorNegocio().obtenerProveedores();
            cbxProveedor.removeAllItems();
            for (ProveedorDTO proveedor : proveedores) {
                cbxProveedor.addItem(proveedor);
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar proveedores: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            List<ProductoDTO> productos = frmPrincipal.getProductoNegocio().obtenerTodosProductos();
            List<CategoriaDTO> categorias = frmPrincipal.getCategoriaNegocio().obtenerCategorias();
            List<ProveedorDTO> proveedores = frmPrincipal.getProveedorNegocio().obtenerProveedores();

            tableModel.setRowCount(0);

            for (ProductoDTO producto : productos) {
                String nombreCategoria = obtenerNombreCategoria(producto.getCategoriaId(), categorias);
                String nombreProveedor = obtenerNombreProveedor(producto.getProveedorId(), proveedores);

                tableModel.addRow(new Object[]{
                    producto.getId(),
                    producto.getNombreProducto(),
                    producto.getDescripcionProducto(),
                    producto.getFechaCreacion(),
                    nombreCategoria,
                    nombreProveedor,
                    "Editar",
                    "Eliminar"
                });
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
        }
    }

    private String obtenerNombreCategoria(Object categoriaId, List<CategoriaDTO> categorias) {
        for (CategoriaDTO categoria : categorias) {
            if (String.valueOf(categoria.getId()).equals(String.valueOf(categoriaId))) {
                return categoria.getNombreCategoria();
            }
        }
        return "Desconocido";
    }

    private String obtenerNombreProveedor(Object proveedorId, List<ProveedorDTO> proveedores) {
        for (ProveedorDTO proveedor : proveedores) {
            if (String.valueOf(proveedor.getId()).equals(String.valueOf(proveedorId))) {
                return proveedor.getNombre();
            }
        }
        return "Desconocido";
    }

    private void agregarProducto() {
        String nombre = txtNombreProducto.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        CategoriaDTO categoria = (CategoriaDTO) cbxCategoria.getSelectedItem();
        ProveedorDTO proveedor = (ProveedorDTO) cbxProveedor.getSelectedItem();

        if (nombre.isEmpty() || categoria == null || proveedor == null) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios.");
            return;
        }

        try {
            if (productoSeleccionado == null) {
                ProductoDTO nuevoProducto = new ProductoDTO(null, nombre, descripcion, categoria.getId(), proveedor.getId());
                frmPrincipal.getProductoNegocio().crearProducto(nuevoProducto);
            } else {
                productoSeleccionado.setNombreProducto(nombre);
                productoSeleccionado.setDescripcionProducto(descripcion);
                productoSeleccionado.setCategoriaId(categoria.getId());
                productoSeleccionado.setProveedorId(proveedor.getId());

                frmPrincipal.getProductoNegocio().actualizarProducto(productoSeleccionado);
            }

            JOptionPane.showMessageDialog(this, "Producto guardado correctamente.");
            limpiarCampos();
            cargarProductos();
            productoSeleccionado = null;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar producto: " + e.getMessage());
        }
    }

    private void editarProducto(int row) {
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            for (ProductoDTO prod : frmPrincipal.getProductoNegocio().obtenerTodosProductos()) {
                if (prod.getId().equals(id)) {
                    this.productoSeleccionado = prod;
                    txtNombreProducto.setText(prod.getNombreProducto());
                    txtDescripcion.setText(prod.getDescripcionProducto());

                    for (int i = 0; i < cbxProveedor.getItemCount(); i++) {
                        if (cbxProveedor.getItemAt(i).getId().equals(prod.getProveedorId())) {
                            cbxProveedor.setSelectedIndex(i);
                            break;
                        }
                    }

                    for (int i = 0; i < cbxCategoria.getItemCount(); i++) {
                        if (cbxCategoria.getItemAt(i).getId().equals(prod.getCategoriaId())) {
                            cbxCategoria.setSelectedIndex(i);
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar producto.");
        }
    }

    private String formatearTitulo(String texto) {
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.length() > 0) {
                sb.append(Character.toUpperCase(palabra.charAt(0)));
                sb.append(palabra.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    private void eliminarProducto(int row) {
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                frmPrincipal.getProductoNegocio().eliminarProducto(id);
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
                limpiarCampos();
                cargarProductos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar el producto: " + e.getMessage());
            }
        }
    }

    private void limpiarCampos() {
        txtNombreProducto.setText("");
        txtDescripcion.setText("");
        cbxCategoria.setSelectedIndex(0);
        cbxProveedor.setSelectedIndex(0);
        tblProductos.clearSelection();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtNombreProducto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        cbxProveedor = new javax.swing.JComboBox<>();
        btnConfirmar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        cbxCategoria = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(1701, 1080));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        jLabel1.setText("Añadir producto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(634, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(562, 562, 562))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Categoría:");

        txtNombreProducto.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Nombre:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Descripción:");

        cbxProveedor.setBackground(new java.awt.Color(255, 255, 254));
        cbxProveedor.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        cbxProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxProveedorActionPerformed(evt);
            }
        });

        btnConfirmar.setBackground(new java.awt.Color(0, 0, 0));
        btnConfirmar.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        btnConfirmar.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmar.setText("Confirmar");
        btnConfirmar.setBorder(null);
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Proveedor:");

        cbxCategoria.setBackground(new java.awt.Color(255, 255, 254));
        cbxCategoria.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tblProductos.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        tblProductos.setForeground(new java.awt.Color(176, 50, 53));
        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblProductos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 955, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 735, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(126, 126, 126)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addComponent(txtNombreProducto, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(cbxCategoria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addComponent(cbxProveedor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnConfirmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(78, 78, 78)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombreProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59)
                        .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(116, 116, 116))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        agregarProducto();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void cbxProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxProveedorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxProveedorActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JComboBox<CategoriaDTO> cbxCategoria;
    private javax.swing.JComboBox<ProveedorDTO> cbxProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtNombreProducto;
    // End of variables declaration//GEN-END:variables
}
