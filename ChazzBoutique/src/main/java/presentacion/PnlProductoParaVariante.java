package presentacion;

import com.itextpdf.text.Font;
import com.mycompany.chazzboutiquenegocio.dtos.CategoriaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.ProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.ProveedorDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import java.awt.Color;
import java.awt.Dimension;
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
public class PnlProductoParaVariante extends javax.swing.JPanel {

    private FrmPrincipal frmPrincipal;
    private DefaultTableModel tableModel;
    private ProductoDTO productoSeleccionado = null;

    public PnlProductoParaVariante(FrmPrincipal frmPrincipal) {
        initComponents();
        this.setSize(new Dimension(1701, 1080));
        this.frmPrincipal = frmPrincipal;

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Descripción", "Fecha", "Categoría", "Proveedor", "Agregar Variante"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo las columnas "Editar" (índice 6) y "Eliminar" (índice 7) son editables
                return column == 6;
            }
        };

        tblProductos.setModel(tableModel);
        tblProductos.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof String && column != 6) { // excluye columnas "Editar" y "Eliminar"
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
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        // Configurar renderers y editores para botones
        tblProductos.getColumn("Agregar Variante").setCellRenderer(new ButtonRenderer("Agregar Variante"));

        tblProductos.getColumn("Agregar Variante").setCellEditor(
                new ButtonEditor(new JCheckBox(), "Agregar Variante", row -> agregarVariante(row))
        );

        cargarProductos();
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

    private void agregarVariante(int row) {
        Long idProducto = (Long) tableModel.getValueAt(row, 0);
        PnlAnadirVarianteProducto pnl = new PnlAnadirVarianteProducto(idProducto, frmPrincipal.getVarianteProductoNegocio());
        frmPrincipal.pintarPanelPrincipal(pnl);
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
        btnConfirmar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(1701, 1080));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        jLabel1.setText("Escoger Producto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(609, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(594, 594, 594))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        tblProductos.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1567, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(618, 618, 618))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 679, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
    }//GEN-LAST:event_btnConfirmarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblProductos;
    // End of variables declaration//GEN-END:variables
}
