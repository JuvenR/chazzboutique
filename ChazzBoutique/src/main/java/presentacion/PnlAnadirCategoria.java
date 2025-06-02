package presentacion;

import com.itextpdf.text.Font;
import com.mycompany.chazzboutiquenegocio.dtos.CategoriaDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import utils.ButtonEditor;
import utils.ButtonRenderer;

/**
 *
 * @author
 */
public class PnlAnadirCategoria extends javax.swing.JPanel {

    private FrmPrincipal frmPrincipal;
    private DefaultTableModel tableModel;
    private CategoriaDTO categoriaSeleccionada;
    private String imagen;
    private BufferedImage imagenSeleccionadaBuffered;
    private String nombreImagenTemporal;

    public PnlAnadirCategoria(FrmPrincipal frmPrincipal) {
        initComponents();
        this.frmPrincipal = frmPrincipal;
        this.setSize(new Dimension(1701, 1080));

        lblImagen.setPreferredSize(new Dimension(480, 600));

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Descripción", "Imagen", "Editar", "Eliminar"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }
        };
        tblCategorias.setModel(tableModel);

        tblCategorias.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                // Llama al renderizado por defecto
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Verifica si es texto y no un botón (como "Editar" o "Eliminar")
                if (value instanceof String && column != 4 && column != 5) {
                    String textoOriginal = (String) value;
                    String textoFormateado = formatearTitulo(textoOriginal);
                    setText(textoFormateado);
                }

                return c;
            }
        });

        tblCategorias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblCategorias.getSelectedRow();
                if (row >= 0) {
                    editarCategoria(row);
                }
            }
        });

        tblCategorias.setRowHeight(40);

        JTableHeader header = tblCategorias.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(Color.WHITE);
        header.setBackground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        tblCategorias.getColumn("Editar").setCellRenderer(new ButtonRenderer("Editar"));
        tblCategorias.getColumn("Eliminar").setCellRenderer(new ButtonRenderer("Eliminar"));
        tblCategorias.getColumn("Editar").setCellEditor(new ButtonEditor(new JCheckBox(), "Editar", this::editarCategoria));
        tblCategorias.getColumn("Eliminar").setCellEditor(new ButtonEditor(new JCheckBox(), "Eliminar", this::eliminarCategoria));
        cargarCategorias();
    }

    private void guardarCategoria() {
        String nombre = txtNombreCategoria.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String rutaImagenFinal = " ";

        if (imagenSeleccionadaBuffered == null && categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar una imagen.");
            return;
        }

        if (imagenSeleccionadaBuffered == null && categoriaSeleccionada != null && this.imagen == null) {
            JOptionPane.showMessageDialog(this, "Esta categoría no tiene imagen. Selecciona una imagen para continuar.");
            return;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre es obligatorio.");
            return;
        }

        // Procesar imagen seleccionada si existe
        if (imagenSeleccionadaBuffered != null && nombreImagenTemporal != null) {
            try {
                File carpetaDestino = new File("imagenes/categorias/");
                if (!carpetaDestino.exists()) {
                    carpetaDestino.mkdirs();
                }

                File destino = new File(carpetaDestino, nombreImagenTemporal);

// Si ya existe un archivo con ese nombre, generar uno nuevo único
                int contador = 1;
                while (destino.exists()) {
                    String nombreBase = nombreImagenTemporal.substring(0, nombreImagenTemporal.lastIndexOf('.'));
                    String extension = nombreImagenTemporal.substring(nombreImagenTemporal.lastIndexOf('.') + 1);
                    String nuevoNombre = nombreBase + "_" + contador + "." + extension;
                    destino = new File(carpetaDestino, nuevoNombre);
                    contador++;
                }
                ImageIO.write(imagenSeleccionadaBuffered, "png", destino);
                imagen = "/categorias/" + destino.getName();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage());
            }
        } else if (this.imagen != null) {
            rutaImagenFinal = this.imagen;
        }

        try {
            if (categoriaSeleccionada == null) {
                CategoriaDTO dto = new CategoriaDTO(null, nombre, descripcion, rutaImagenFinal);
                frmPrincipal.getCategoriaNegocio().crearCategoria(dto);
                JOptionPane.showMessageDialog(this, "Categoría registrada.");
            } else {
                // Verificar que la categoría seleccionada aún existe
                boolean existe = false;
                List<CategoriaDTO> categorias = frmPrincipal.getCategoriaNegocio().obtenerCategorias();
                for (CategoriaDTO cat : categorias) {
                    if (cat.getId().equals(categoriaSeleccionada.getId())) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    JOptionPane.showMessageDialog(this, "La categoría seleccionada ya no existe.");
                    categoriaSeleccionada = null;
                    limpiarFormulario();
                    cargarCategorias();
                    return;
                }

                categoriaSeleccionada.setNombreCategoria(nombre);
                categoriaSeleccionada.setDescripcionCategoria(descripcion);
                categoriaSeleccionada.setImagenCategoria(rutaImagenFinal);
                frmPrincipal.getCategoriaNegocio().actualizarCategoria(categoriaSeleccionada);
                JOptionPane.showMessageDialog(this, "Categoría actualizada.");
                categoriaSeleccionada = null;
            }

            limpiarFormulario();
            cargarCategorias();

            // Limpiar imagen temporal
            imagenSeleccionadaBuffered = null;
            nombreImagenTemporal = null;

        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
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

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File imagenSeleccionada = fileChooser.getSelectedFile();

            try {
                BufferedImage ropa = ImageIO.read(imagenSeleccionada);
                nombreImagenTemporal = imagenSeleccionada.getName();

                // Cargar el marco
                URL marcoURL = getClass().getResource("/images/marco_chazz.png");
                if (marcoURL == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró el marco.");
                    return;
                }
                BufferedImage marco = ImageIO.read(marcoURL);

                // Escalar y combinar
                Image ropaEscalada = ropa.getScaledInstance(marco.getWidth(), marco.getHeight(), Image.SCALE_SMOOTH);
                BufferedImage ropaFinal = new BufferedImage(marco.getWidth(), marco.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = ropaFinal.createGraphics();
                g2.drawImage(ropaEscalada, 0, 0, null);
                g2.dispose();

                imagenSeleccionadaBuffered = aplicarMarco(ropaFinal, marco); // Solo la guardamos en memoria

                // Mostrar previsualización
                Image img = imagenSeleccionadaBuffered.getScaledInstance(lblImagen.getWidth(), lblImagen.getHeight(), Image.SCALE_SMOOTH);
                lblImagen.setIcon(new ImageIcon(img));
                lblImagen.setText("");

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al procesar la imagen: " + e.getMessage());
            }

        }
    }

    private void cargarCategorias() {
        tableModel.setRowCount(0);
        try {
            List<CategoriaDTO> categorias = frmPrincipal.getCategoriaNegocio().obtenerCategorias();
            for (CategoriaDTO cat : categorias) {
                tableModel.addRow(new Object[]{
                    cat.getId(),
                    cat.getNombreCategoria(),
                    cat.getDescripcionCategoria(),
                    extraerNombreArchivo(cat.getImagenCategoria()),
                    "Editar",
                    "Eliminar"
                });
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar categorías.");
        }
    }

    private void editarCategoria(int row) {
        Long id = (Long) tableModel.getValueAt(row, 0);
        try {
            for (CategoriaDTO cat : frmPrincipal.getCategoriaNegocio().obtenerCategorias()) {
                if (cat.getId().equals(id)) {
                    this.categoriaSeleccionada = cat;
                    txtNombreCategoria.setText(cat.getNombreCategoria());
                    txtDescripcion.setText(cat.getDescripcionCategoria());

                    String ruta = cat.getImagenCategoria();
                    if (ruta != null && !ruta.isEmpty()) {
                        this.imagen = ruta;

                        if (lblImagen.getWidth() > 0 && lblImagen.getHeight() > 0) {
                            cargarImagenEnLabel(ruta);
                        } else {
                            lblImagen.addComponentListener(new ComponentAdapter() {
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    cargarImagenEnLabel(ruta);
                                    lblImagen.removeComponentListener(this);
                                }
                            });
                        }
                    } else {
                        lblImagen.setIcon(null);
                        lblImagen.setText("");
                        this.imagen = null;
                    }
                    break;
                }
            }
        } catch (NegocioException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar categoría.");
        }
    }

    private void cargarImagenEnLabel(String rutaRelativa) {
        File archivoImagen = new File("imagenes/" + rutaRelativa);
        if (archivoImagen.exists()) {
            ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
            Image originalImage = icon.getImage();

            // Escalado con mejor calidad
            int width = 480;
            int height = 600;
            Image scaledImage = getHighQualityScaledImage(originalImage, width, height);

            lblImagen.setIcon(new ImageIcon(scaledImage));
        } else {
            lblImagen.setIcon(null);
        }
    }

    private void eliminarCategoria(int row) {
        Long id = (Long) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta categoría?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                frmPrincipal.getCategoriaNegocio().eliminarCategoria(id);

                // Verificar si la categoría eliminada es la misma que está seleccionada
                if (categoriaSeleccionada != null && categoriaSeleccionada.getId().equals(id)) {
                    categoriaSeleccionada = null;
                    limpiarFormulario();
                }

                cargarCategorias();
            } catch (NegocioException e) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar la categoría: elimine primero los productos relacionados.");
            }
        }
    }

    private void limpiarFormulario() {
        categoriaSeleccionada = null;
        txtNombreCategoria.setText("");
        txtDescripcion.setText("");
        this.imagen = null;
        // Cargar imagen por defecto desde resources
        URL url = getClass().getResource("/images/1.png");
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(480, 600, Image.SCALE_SMOOTH); // ajusta dimensiones si necesitas
            lblImagen.setIcon(new ImageIcon(img));
        } else {
            System.err.println("No se encontró la imagen /images/1.png");
            lblImagen.setIcon(null); // Limpia el ícono si no se encuentra
        }
    }

    private String extraerNombreArchivo(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return "";
        }
        return new File(ruta).getName(); // obtiene solo 'imagen.png'
    }

    private BufferedImage aplicarMarco(BufferedImage base, BufferedImage marco) {
        BufferedImage combinado = new BufferedImage(
                base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinado.createGraphics();

        g.drawImage(base, 0, 0, null);
        g.drawImage(marco, 0, 0, base.getWidth(), base.getHeight(), null); // Ajustamos tamaño
        g.dispose();

        return combinado;
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
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCategorias = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        txtNombreCategoria = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnConfirmar = new javax.swing.JButton();
        lblImagen = new javax.swing.JLabel();
        btnAgregarImagen = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1701, 1080));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        jLabel1.setText("Añadir categoría");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(599, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(591, 591, 591))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(23, 23, 23))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tblCategorias.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        tblCategorias.setForeground(new java.awt.Color(176, 50, 53));
        tblCategorias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Descripción", "Imagen", "Editar", "Eliminar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblCategorias);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Descripción:");

        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        txtDescripcion.setRows(5);
        jScrollPane1.setViewportView(txtDescripcion);

        txtNombreCategoria.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Nombre:");

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 911, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(80, 80, 80)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtNombreCategoria)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)))
                            .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(122, 122, 122))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addComponent(btnConfirmar, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        lblImagen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/1.png"))); // NOI18N

        btnAgregarImagen.setBackground(new java.awt.Color(0, 0, 0));
        btnAgregarImagen.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        btnAgregarImagen.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarImagen.setText("Agregar Imagen");
        btnAgregarImagen.setBorder(null);
        btnAgregarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarImagenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(177, 177, 177)
                        .addComponent(lblImagen)
                        .addGap(73, 73, 73))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAgregarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(116, 116, 116))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(lblImagen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAgregarImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed
        guardarCategoria();
    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void btnAgregarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarImagenActionPerformed
        seleccionarImagen();
    }//GEN-LAST:event_btnAgregarImagenActionPerformed
    private Image getHighQualityScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarImagen;
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JTable tblCategorias;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtNombreCategoria;
    // End of variables declaration//GEN-END:variables
}
