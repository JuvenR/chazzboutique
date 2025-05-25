package presentacion;

import com.itextpdf.text.Font;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IReporteNegocio;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO;
import java.awt.Color;
import java.awt.Dimension;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import javax.persistence.PersistenceException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author
 */
public class PnlReporte extends javax.swing.JPanel {

    FrmPrincipal frmPrincipal;
    private List<ReporteInventarioDTO> inventarioActual;

    public PnlReporte(FrmPrincipal frmPrincipal) {
        initComponents();
        this.frmPrincipal = frmPrincipal;
        this.setSize(new Dimension(1701, 1080));
      
        cbxPeriodo.addActionListener(e -> {
            String periodoSeleccionado = (String) cbxPeriodo.getSelectedItem();
            LocalDate hoy = LocalDate.now();
            LocalDate inicio;
            LocalDate fin;

            switch (periodoSeleccionado) {
                case "Semanal":
                    inicio = hoy.with(DayOfWeek.MONDAY);
                    fin = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                    if (fin.isAfter(hoy)) {
                        fin = hoy;
                    }
                    break;
                case "Mensual":
                    inicio = hoy.withDayOfMonth(1);
                    fin = hoy.withDayOfMonth(hoy.lengthOfMonth());
                    break;
                case "Anual":
                    inicio = hoy.withDayOfYear(1);
                    fin = hoy.withDayOfYear(hoy.lengthOfYear());
                    break;
                default: // "Rango de Fechas"
                    fechaInicio.setEnabled(true);
                    fechaFin.setEnabled(true);
                    return; // Salir sin cambiar nada
            }

            if (fin.isAfter(hoy)) {
                fin = hoy;
            }

            // Desactivar manualmente si no es "Rango de Fechas"
            fechaInicio.setEnabled(false);
            fechaFin.setEnabled(false);

            // Convertir a Date para el DateChooser
            Calendar calendarInicio = Calendar.getInstance();
            calendarInicio.set(inicio.getYear(), inicio.getMonthValue() - 1, inicio.getDayOfMonth(), 0, 0, 0);
            calendarInicio.set(Calendar.MILLISECOND, 0);
            java.util.Date dateInicio = calendarInicio.getTime();
            Calendar calendarFin = Calendar.getInstance();
            calendarFin.set(fin.getYear(), fin.getMonthValue() - 1, fin.getDayOfMonth(), 0, 0, 0);
            calendarFin.set(Calendar.MILLISECOND, 0);
            java.util.Date dateFin = calendarFin.getTime();

            // Establecer en los componentes
            fechaInicio.setDate(dateInicio);
            fechaFin.setDate(dateFin);
        });

        cbxTipoReporte1.addActionListener(e -> {
            String tipo = (String) cbxTipoReporte1.getSelectedItem();
            boolean esInventario = "Inventario actual".equals(tipo);

            if (esInventario) {
                // Configuración para Inventario actual
                cbxPeriodo.setSelectedIndex(1); // Semanal
                cbxPeriodo.setEnabled(false);
                fechaInicio.setEnabled(false);
                fechaFin.setEnabled(false);

                // Establecer rango de la semana actual
                LocalDate hoy = LocalDate.now();
                LocalDate inicioSemana = hoy.with(java.time.DayOfWeek.MONDAY);
                LocalDate finSemana = hoy.with(java.time.DayOfWeek.SUNDAY);

                // Conversión correcta de LocalDate a java.util.Date
                fechaInicio.setDate(java.util.Date.from(inicioSemana.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                fechaFin.setDate(java.util.Date.from(finSemana.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            } else {
                // Configuración para otros reportes
                cbxPeriodo.setEnabled(true);
                fechaInicio.setEnabled(true);
                fechaFin.setEnabled(true);
            }
        });

        tblReporte.setRowHeight(40);

        // Configurar header
        JTableHeader header = tblReporte.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        tblReporte.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value instanceof String) {
                    String textoFormateado = formatearTitulo((String) value);
                    setText(textoFormateado);
                }

                return c;
            }
        });

    }

    private void generarReporte() {

        try {
            // Limpiar renderizadores personalizados antes de generar un nuevo reporte
            tblReporte.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer());

            String tipoReporte = (String) cbxTipoReporte1.getSelectedItem();
            // Resto del código...
            if (fechaInicio.getDate() == null || fechaFin.getDate() == null) {
                JOptionPane.showMessageDialog(this,
                        "Debes seleccionar ambas fechas: inicio y fin.",
                        "Fechas incompletas",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fechaInicio = this.fechaInicio.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate fechaFin = this.fechaFin.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hoy = LocalDate.now();

//            if (fechaFin.isBefore(fechaInicio)) {
//                JOptionPane.showMessageDialog(this,
//                        "La fecha de fin no puede ser anterior a la fecha de inicio.",
//                        "Fechas inválidas",
//                        JOptionPane.WARNING_MESSAGE);
//                return;
//            }
//
//            if (fechaFin.isAfter(hoy)) {
//                JOptionPane.showMessageDialog(this,
//                        "La fecha de fin no puede ser posterior a hoy.",
//                        "Fecha inválida",
//                        JOptionPane.WARNING_MESSAGE);
//                return;
//            }

            DefaultTableModel model = new DefaultTableModel();
            IReporteNegocio reporteNegocio = frmPrincipal.getReporteNegocio();

            switch (tipoReporte) {
                case "Ventas":
                    List<ReporteVentaDTO> ventas = reporteNegocio.obtenerDatosVentas(fechaInicio, fechaFin);
                    model.addColumn("ID Venta");
                    model.addColumn("Fecha");
                    model.addColumn("Total");
                    model.addColumn("Vendedor");

                    for (ReporteVentaDTO venta : ventas) {
                        model.addRow(new Object[]{
                            venta.getVentaId(),
                            venta.getFecha(),
                            "$" + venta.getTotal().setScale(2, RoundingMode.HALF_UP),
                            venta.getVendedor()
                        });
                    }
                    break;

                case "Productos más vendidos":
                    List<ReporteProductoDTO> productos = reporteNegocio.obtenerProductosMasVendidos(fechaInicio, fechaFin);
                    model.addColumn("Producto");
                    model.addColumn("Cantidad Vendida");
                    model.addColumn("Total Vendido");
                    model.addColumn("Categoría");

                    for (ReporteProductoDTO producto : productos) {
                        model.addRow(new Object[]{
                            producto.getNombreProducto(),
                            producto.getCantidadVendida(),
                            "$" + producto.getTotalVendido().setScale(2, RoundingMode.HALF_UP),
                            producto.getCategoria()
                        });
                    }
                    break;

                case "Ingresos por categoría":
                    List<ReporteCategoriaDTO> categorias = reporteNegocio.obtenerIngresosPorCategoria(fechaInicio, fechaFin);
                    model.addColumn("Categoría");
                    model.addColumn("Ventas Totales");
                    model.addColumn("Ingresos");
                    model.addColumn("% del Total");

                    for (ReporteCategoriaDTO categoria : categorias) {
                        model.addRow(new Object[]{
                            categoria.getNombreCategoria(),
                            categoria.getVentasTotales(),
                            "$" + categoria.getIngresos().setScale(2, RoundingMode.HALF_UP),
                            categoria.getPorcentaje().setScale(2, RoundingMode.HALF_UP) + "%"
                        });
                    }
                    break;

                case "Inventario actual":
                    this.inventarioActual = reporteNegocio.obtenerInventarioActual();
                    model.addColumn("Producto");
                    model.addColumn("Talla");
                    model.addColumn("Color");
                    model.addColumn("Stock");
                    model.addColumn("Precio Unitario");
                    model.addColumn("Valor Total");

                    for (ReporteInventarioDTO item : inventarioActual) {
                        model.addRow(new Object[]{
                            item.getNombreProducto(),
                            item.getTalla(),
                            "", // Celda vacía que se mostrará con color
                            item.getStock(),
                            "$" + item.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP),
                            "$" + item.getValorTotal().setScale(2, RoundingMode.HALF_UP)
                        });
                    }

                    break;
                default:
                    throw new IllegalArgumentException("Tipo de reporte no válido");
            }

            tblReporte.setModel(model);

            // LUEGO configurar el renderizador solo si es el reporte de inventario
            if ("Inventario actual".equals(tipoReporte) && tblReporte.getColumnCount() > 2) {
                tblReporte.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer(inventarioActual));
            }
        } catch (PersistenceException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al generar el reporte: " + e.getMessage(),
                    "Error de Persistencia",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error en parámetros",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado al generar el reporte: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String formatearTitulo(String texto) {
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
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
        cbxPeriodo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        fechaInicio = new com.toedter.calendar.JDateChooser();
        btnGenerarReporte = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        fechaFin = new com.toedter.calendar.JDateChooser();
        btnConfirmar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblReporte = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        cbxTipoReporte1 = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(1701, 1080));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        jLabel1.setText("Reportes");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(694, 694, 694)
                .addComponent(jLabel1)
                .addContainerGap(756, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        cbxPeriodo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbxPeriodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rango de Fechas", "Semanal", "Mensual", "Anual" }));
        cbxPeriodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxPeriodoActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Periodo");

        fechaInicio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnGenerarReporte.setBackground(new java.awt.Color(0, 0, 0));
        btnGenerarReporte.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        btnGenerarReporte.setForeground(new java.awt.Color(255, 255, 255));
        btnGenerarReporte.setText("Generar reporte");
        btnGenerarReporte.setBorder(null);
        btnGenerarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReporteActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("Hasta");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Desde");

        fechaFin.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnConfirmar.setBackground(new java.awt.Color(0, 0, 0));
        btnConfirmar.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        btnConfirmar.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmar.setText("Confirmar");
        btnConfirmar.setBorder(null);

        tblReporte.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        tblReporte.setForeground(new java.awt.Color(176, 50, 53));
        tblReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Reportes"
            }
        ));
        jScrollPane2.setViewportView(tblReporte);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Tipo de reporte");

        cbxTipoReporte1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cbxTipoReporte1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ventas", "Productos más vendidos", "Ingresos por categoría", "Inventario actual" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(529, 529, 529))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxTipoReporte1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(48, 48, 48)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(fechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(75, 75, 75)
                                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1589, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxTipoReporte1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReporteActionPerformed
        this.generarReporte();
    }//GEN-LAST:event_btnGenerarReporteActionPerformed

    private void cbxPeriodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxPeriodoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxPeriodoActionPerformed

    private static class ColorCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

        private final List<ReporteInventarioDTO> inventario;

        public ColorCellRenderer(List<ReporteInventarioDTO> inventario) {
            this.inventario = inventario;
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row < inventario.size()) {
                String colorHex = inventario.get(row).getColor();
                try {
                    c.setBackground(Color.decode(colorHex));
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                }
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnGenerarReporte;
    private javax.swing.JComboBox<String> cbxPeriodo;
    private javax.swing.JComboBox<String> cbxTipoReporte1;
    private com.toedter.calendar.JDateChooser fechaFin;
    private com.toedter.calendar.JDateChooser fechaInicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblReporte;
    // End of variables declaration//GEN-END:variables
}
