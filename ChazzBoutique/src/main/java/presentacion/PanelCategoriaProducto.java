/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion;

import com.mycompany.chazzboutiquenegocio.dtos.CategoriaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static utils.Capitalizador.capitalizarNombre;

/**
 *
 * @author carli
 */
public class PanelCategoriaProducto extends javax.swing.JPanel {

    private FrmPrincipal frmPrincipal;
    private CategoriaDTO categoriaSeleccionada;

    private int paginaActual = 1;
    private final int tamanoPagina = 12;
    private String filtroActual = "";
    private boolean hayMasPaginas = true;
    private List<VarianteProductoDTO> listaActualDeVariantes;

    public PanelCategoriaProducto(FrmPrincipal frmPrincipal, CategoriaDTO categoria) {
        initComponents();
        this.frmPrincipal = frmPrincipal;
        this.categoriaSeleccionada = categoria;
        this.lblTitulo.setText(capitalizarNombre(categoria.getNombreCategoria()));
        cargarVariantes(paginaActual, tamanoPagina, filtroActual);
        txtBuscador.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtBuscador.getText().equals("Buscar")) {
                    txtBuscador.setText("");
                    txtBuscador.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtBuscador.getText().isBlank()) {
                    txtBuscador.setText("Buscar");
                    txtBuscador.setForeground(Color.GRAY);
                }
            }
        });
        txtBuscador.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                buscar();
            }

            private void buscar() {
                String texto = txtBuscador.getText().trim();
                if (!texto.equalsIgnoreCase("Buscar")) {
                    filtroActual = texto;
                    paginaActual = 1;
                    cargarVariantes(paginaActual, tamanoPagina, filtroActual);
                }
            }
        });

    }

    private void cargarVariantes(int pagina, int tamañoPagina, String filtro) {
        try {

            List<JPanel> panelesArticulo = List.of(
                    panelArticulo1, panelArticulo2, panelArticulo3,
                    panelArticulo4, panelArticulo5, panelArticulo6, panelArticulo7, panelArticulo8, panelArticulo9, panelArticulo10, panelArticulo11, panelArticulo12
            );

            List<VarianteProductoDTO> variantes = frmPrincipal.getVarianteProductoNegocio()
                    .buscarVariantesPorCategoriaYNombreProducto(this.categoriaSeleccionada.getId().intValue(), filtro, pagina, tamañoPagina);
            listaActualDeVariantes = variantes;
            long total = frmPrincipal.getVarianteProductoNegocio()
                    .contarVariantesPorCategoriaYNombreProducto(categoriaSeleccionada.getId().intValue(), filtro);
            lblArticulos.setText("Todos (" + total + " artículos)");

            List<VarianteProductoDTO> siguientePagina = frmPrincipal.getVarianteProductoNegocio()
                    .buscarVariantesPorNombreProducto(filtro, pagina + 1, tamañoPagina);

            hayMasPaginas = !siguientePagina.isEmpty();
            // actualizar la interfaz (como ya lo haces)
            List<JLabel> etiquetasNombre = List.of(lblNombreArticulo1, lblNombreArticulo2, lblNombreArticulo3, lblNombreArticulo4, lblNombreArticulo5, lblNombreArticulo6, lblNombreArticulo7, lblNombreArticulo8, lblNombreArticulo9, lblNombreArticulo10, lblNombreArticulo11, lblNombreArticulo12);
            List<JLabel> etiquetasTalla = List.of(lblTallaResult1, lblTallaResult2, lblTallaResult3, lblTallaResult4, lblTallaResult5, lblTallaResult6, lblTallaResult7, lblTallaResult8, lblTallaResult9, lblTallaResult10, lblTallaResult11, lblTallaResult12);
            List<JButton> botonesColor = List.of(btnColor1, btnColor2, btnColor3, btnColor4, btnColor5, btnColor6, btnColor7, btnColor8, btnColor9, btnColor10, btnColor11, btnColor12);
            List<JLabel> etiquetasImagen = List.of(lblImagenArticulo1, lblImagenArticulo2, lblImagenArticulo3, lblImagenArticulo4, lblImagenArticulo5, lblImagenArticulo6, lblImagenArticulo7, lblImagenArticulo8, lblImagenArticulo9, lblImagenArticulo10, lblImagenArticulo11, lblImagenArticulo12);

            for (int i = 0; i < tamañoPagina; i++) {
                if (i < variantes.size()) {
                    VarianteProductoDTO dto = variantes.get(i);
                    etiquetasNombre.get(i).setText(capitalizarNombre(dto.getNombreProducto()));
                    etiquetasTalla.get(i).setText(dto.getTalla());
                    botonesColor.get(i).setBackground(Color.decode(dto.getColor()));
                    File archivoImagen = new File("imagenes/" + dto.getUrlImagen());
                    if (archivoImagen.exists()) {
                        ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
                        Image img = icon.getImage().getScaledInstance(83, 123, Image.SCALE_SMOOTH);
                        etiquetasImagen.get(i).setIcon(new ImageIcon(img));
                    } else {
                        etiquetasImagen.get(i).setIcon(null); // O una imagen por defecto si quieres
                    }
                    panelesArticulo.get(i).setVisible(true); // Mostrar panel
                } else {
                    etiquetasNombre.get(i).setText("");
                    etiquetasTalla.get(i).setText("");
                    botonesColor.get(i).setBackground(Color.WHITE);
                    etiquetasImagen.get(i).setIcon(null);
                    panelesArticulo.get(i).setVisible(false); // Ocultar panel
                }
            }
            jPanel10.revalidate();
            jPanel10.repaint();

            // actualizar visibilidad de botones
            btnRightPagina.setEnabled(hayMasPaginas);
            btnLeftPagina1.setEnabled(paginaActual > 1);

        } catch (NegocioException ex) {
            lblArticulos.setText("Todos (0 artículos)");

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
        lblTitulo = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        panelArticulo1 = new javax.swing.JPanel();
        lblImagenArticulo1 = new javax.swing.JLabel();
        lblNombreArticulo1 = new javax.swing.JLabel();
        lblTallaResult1 = new javax.swing.JLabel();
        lblColor = new javax.swing.JLabel();
        lblTalla1 = new javax.swing.JLabel();
        btnColor1 = new javax.swing.JButton();
        btnVer1 = new utils.BotonMenu();
        panelArticulo2 = new javax.swing.JPanel();
        lblImagenArticulo2 = new javax.swing.JLabel();
        lblNombreArticulo2 = new javax.swing.JLabel();
        lblTallaResult2 = new javax.swing.JLabel();
        lblColor1 = new javax.swing.JLabel();
        lblTalla2 = new javax.swing.JLabel();
        btnColor2 = new javax.swing.JButton();
        btnVer2 = new utils.BotonMenu();
        panelArticulo3 = new javax.swing.JPanel();
        lblImagenArticulo3 = new javax.swing.JLabel();
        lblNombreArticulo3 = new javax.swing.JLabel();
        lblTallaResult3 = new javax.swing.JLabel();
        lblColor2 = new javax.swing.JLabel();
        lblTalla3 = new javax.swing.JLabel();
        btnColor3 = new javax.swing.JButton();
        btnVer3 = new utils.BotonMenu();
        panelArticulo4 = new javax.swing.JPanel();
        lblImagenArticulo4 = new javax.swing.JLabel();
        lblNombreArticulo4 = new javax.swing.JLabel();
        lblTallaResult4 = new javax.swing.JLabel();
        lblColor3 = new javax.swing.JLabel();
        lblTalla4 = new javax.swing.JLabel();
        btnColor4 = new javax.swing.JButton();
        btnVer4 = new utils.BotonMenu();
        panelArticulo5 = new javax.swing.JPanel();
        lblImagenArticulo5 = new javax.swing.JLabel();
        lblNombreArticulo5 = new javax.swing.JLabel();
        lblTallaResult5 = new javax.swing.JLabel();
        lblColor4 = new javax.swing.JLabel();
        lblTalla5 = new javax.swing.JLabel();
        btnColor5 = new javax.swing.JButton();
        btnVer5 = new utils.BotonMenu();
        panelArticulo6 = new javax.swing.JPanel();
        lblImagenArticulo6 = new javax.swing.JLabel();
        lblNombreArticulo6 = new javax.swing.JLabel();
        lblTallaResult6 = new javax.swing.JLabel();
        lblColor5 = new javax.swing.JLabel();
        lblTalla6 = new javax.swing.JLabel();
        btnColor6 = new javax.swing.JButton();
        btnVer6 = new utils.BotonMenu();
        panelArticulo7 = new javax.swing.JPanel();
        lblImagenArticulo7 = new javax.swing.JLabel();
        lblNombreArticulo7 = new javax.swing.JLabel();
        lblTallaResult7 = new javax.swing.JLabel();
        lblColor6 = new javax.swing.JLabel();
        lblTalla7 = new javax.swing.JLabel();
        btnColor7 = new javax.swing.JButton();
        btnVer7 = new utils.BotonMenu();
        panelArticulo9 = new javax.swing.JPanel();
        lblImagenArticulo9 = new javax.swing.JLabel();
        lblNombreArticulo9 = new javax.swing.JLabel();
        lblTallaResult9 = new javax.swing.JLabel();
        lblColor8 = new javax.swing.JLabel();
        lblTalla9 = new javax.swing.JLabel();
        btnColor9 = new javax.swing.JButton();
        btnVer9 = new utils.BotonMenu();
        panelArticulo8 = new javax.swing.JPanel();
        lblImagenArticulo8 = new javax.swing.JLabel();
        lblNombreArticulo8 = new javax.swing.JLabel();
        lblTallaResult8 = new javax.swing.JLabel();
        lblColor7 = new javax.swing.JLabel();
        lblTalla8 = new javax.swing.JLabel();
        btnColor8 = new javax.swing.JButton();
        btnVer8 = new utils.BotonMenu();
        panelArticulo10 = new javax.swing.JPanel();
        lblImagenArticulo10 = new javax.swing.JLabel();
        lblNombreArticulo10 = new javax.swing.JLabel();
        lblTallaResult10 = new javax.swing.JLabel();
        lblColor9 = new javax.swing.JLabel();
        lblTalla10 = new javax.swing.JLabel();
        btnColor10 = new javax.swing.JButton();
        btnVer10 = new utils.BotonMenu();
        panelArticulo11 = new javax.swing.JPanel();
        lblImagenArticulo11 = new javax.swing.JLabel();
        lblNombreArticulo11 = new javax.swing.JLabel();
        lblTallaResult11 = new javax.swing.JLabel();
        lblColor10 = new javax.swing.JLabel();
        lblTalla11 = new javax.swing.JLabel();
        btnColor11 = new javax.swing.JButton();
        btnVer11 = new utils.BotonMenu();
        panelArticulo12 = new javax.swing.JPanel();
        lblImagenArticulo12 = new javax.swing.JLabel();
        lblNombreArticulo12 = new javax.swing.JLabel();
        lblTallaResult12 = new javax.swing.JLabel();
        lblColor11 = new javax.swing.JLabel();
        lblTalla12 = new javax.swing.JLabel();
        btnColor12 = new javax.swing.JButton();
        btnVer12 = new utils.BotonMenu();
        lblPagina1 = new javax.swing.JLabel();
        lblArticulos = new javax.swing.JLabel();
        btnRightPagina = new utils.BotonMenu();
        btnLeftPagina1 = new utils.BotonMenu();
        jPanel17 = new javax.swing.JPanel();
        txtBuscador = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        lblTitulo.setText("Pantalones");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        lblImagenArticulo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo1.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo1.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult1.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult1.setText("M");

        lblColor.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor.setText("Color:");

        lblTalla1.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla1.setText("Talla:");

        btnColor1.setBackground(new java.awt.Color(255, 102, 51));

        btnVer1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer1.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo1Layout = new javax.swing.GroupLayout(panelArticulo1);
        panelArticulo1.setLayout(panelArticulo1Layout);
        panelArticulo1Layout.setHorizontalGroup(
            panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo1)
                    .addGroup(panelArticulo1Layout.createSequentialGroup()
                        .addGroup(panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo1Layout.createSequentialGroup()
                                .addComponent(lblColor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla1)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult1)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo1Layout.setVerticalGroup(
            panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo1Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo1Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo1)
                        .addGroup(panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor))
                            .addGroup(panelArticulo1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla1)
                                    .addComponent(lblTallaResult1))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo1);

        lblImagenArticulo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo2.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo2.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult2.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult2.setText("M");

        lblColor1.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor1.setText("Color:");

        lblTalla2.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla2.setText("Talla:");

        btnColor2.setBackground(new java.awt.Color(255, 102, 51));

        btnVer2.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer2.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo2Layout = new javax.swing.GroupLayout(panelArticulo2);
        panelArticulo2.setLayout(panelArticulo2Layout);
        panelArticulo2Layout.setHorizontalGroup(
            panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo2)
                    .addGroup(panelArticulo2Layout.createSequentialGroup()
                        .addGroup(panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo2Layout.createSequentialGroup()
                                .addComponent(lblColor1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla2)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult2)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo2Layout.setVerticalGroup(
            panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo2Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo2Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo2)
                        .addGroup(panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo2Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor1))
                            .addGroup(panelArticulo2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla2)
                                    .addComponent(lblTallaResult2))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo2);

        lblImagenArticulo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo3.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo3.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult3.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult3.setText("M");

        lblColor2.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor2.setText("Color:");

        lblTalla3.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla3.setText("Talla:");

        btnColor3.setBackground(new java.awt.Color(255, 102, 51));

        btnVer3.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer3.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo3Layout = new javax.swing.GroupLayout(panelArticulo3);
        panelArticulo3.setLayout(panelArticulo3Layout);
        panelArticulo3Layout.setHorizontalGroup(
            panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo3)
                    .addGroup(panelArticulo3Layout.createSequentialGroup()
                        .addGroup(panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo3Layout.createSequentialGroup()
                                .addComponent(lblColor2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla3)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult3)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo3Layout.setVerticalGroup(
            panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo3Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo3Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo3)
                        .addGroup(panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo3Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor2))
                            .addGroup(panelArticulo3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla3)
                                    .addComponent(lblTallaResult3))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo3);

        lblImagenArticulo4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo4.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo4.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult4.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult4.setText("M");

        lblColor3.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor3.setText("Color:");

        lblTalla4.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla4.setText("Talla:");

        btnColor4.setBackground(new java.awt.Color(255, 102, 51));

        btnVer4.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer4.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo4Layout = new javax.swing.GroupLayout(panelArticulo4);
        panelArticulo4.setLayout(panelArticulo4Layout);
        panelArticulo4Layout.setHorizontalGroup(
            panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo4)
                    .addGroup(panelArticulo4Layout.createSequentialGroup()
                        .addGroup(panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo4Layout.createSequentialGroup()
                                .addComponent(lblColor3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla4)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult4)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo4Layout.setVerticalGroup(
            panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo4Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo4)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo4Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo4)
                        .addGroup(panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo4Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor3))
                            .addGroup(panelArticulo4Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla4)
                                    .addComponent(lblTallaResult4))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo4);

        lblImagenArticulo5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo5.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo5.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult5.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult5.setText("M");

        lblColor4.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor4.setText("Color:");

        lblTalla5.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla5.setText("Talla:");

        btnColor5.setBackground(new java.awt.Color(255, 102, 51));

        btnVer5.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer5.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo5Layout = new javax.swing.GroupLayout(panelArticulo5);
        panelArticulo5.setLayout(panelArticulo5Layout);
        panelArticulo5Layout.setHorizontalGroup(
            panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo5)
                    .addGroup(panelArticulo5Layout.createSequentialGroup()
                        .addGroup(panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo5Layout.createSequentialGroup()
                                .addComponent(lblColor4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla5)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult5)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo5Layout.setVerticalGroup(
            panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo5Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo5)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo5Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo5)
                        .addGroup(panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo5Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor4))
                            .addGroup(panelArticulo5Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo5Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla5)
                                    .addComponent(lblTallaResult5))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo5);

        lblImagenArticulo6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo6.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo6.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult6.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult6.setText("M");

        lblColor5.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor5.setText("Color:");

        lblTalla6.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla6.setText("Talla:");

        btnColor6.setBackground(new java.awt.Color(255, 102, 51));

        btnVer6.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer6.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo6Layout = new javax.swing.GroupLayout(panelArticulo6);
        panelArticulo6.setLayout(panelArticulo6Layout);
        panelArticulo6Layout.setHorizontalGroup(
            panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo6)
                    .addGroup(panelArticulo6Layout.createSequentialGroup()
                        .addGroup(panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo6Layout.createSequentialGroup()
                                .addComponent(lblColor5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla6)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult6)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo6Layout.setVerticalGroup(
            panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo6Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo6Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo6)
                        .addGroup(panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo6Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor5))
                            .addGroup(panelArticulo6Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo6Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla6)
                                    .addComponent(lblTallaResult6))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo6);

        lblImagenArticulo7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo7.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo7.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult7.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult7.setText("M");

        lblColor6.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor6.setText("Color:");

        lblTalla7.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla7.setText("Talla:");

        btnColor7.setBackground(new java.awt.Color(255, 102, 51));

        btnVer7.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer7.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo7Layout = new javax.swing.GroupLayout(panelArticulo7);
        panelArticulo7.setLayout(panelArticulo7Layout);
        panelArticulo7Layout.setHorizontalGroup(
            panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo7)
                    .addGroup(panelArticulo7Layout.createSequentialGroup()
                        .addGroup(panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo7Layout.createSequentialGroup()
                                .addComponent(lblColor6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla7)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult7)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo7Layout.setVerticalGroup(
            panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo7Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo7Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo7)
                        .addGroup(panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo7Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor6))
                            .addGroup(panelArticulo7Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla7)
                                    .addComponent(lblTallaResult7))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo7);

        lblImagenArticulo9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo9.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo9.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult9.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult9.setText("M");

        lblColor8.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor8.setText("Color:");

        lblTalla9.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla9.setText("Talla:");

        btnColor9.setBackground(new java.awt.Color(255, 102, 51));

        btnVer9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer9.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer9ActionPerformed(evt);
            }
        });

        lblImagenArticulo8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo8.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo8.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult8.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult8.setText("M");

        lblColor7.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor7.setText("Color:");

        lblTalla8.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla8.setText("Talla:");

        btnColor8.setBackground(new java.awt.Color(255, 102, 51));

        btnVer8.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer8.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo8Layout = new javax.swing.GroupLayout(panelArticulo8);
        panelArticulo8.setLayout(panelArticulo8Layout);
        panelArticulo8Layout.setHorizontalGroup(
            panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo8Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo8)
                    .addGroup(panelArticulo8Layout.createSequentialGroup()
                        .addGroup(panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo8Layout.createSequentialGroup()
                                .addComponent(lblColor7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla8)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult8)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo8Layout.setVerticalGroup(
            panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo8Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo8Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo8)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo8Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo8)
                        .addGroup(panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo8Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor7))
                            .addGroup(panelArticulo8Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla8)
                                    .addComponent(lblTallaResult8))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        javax.swing.GroupLayout panelArticulo9Layout = new javax.swing.GroupLayout(panelArticulo9);
        panelArticulo9.setLayout(panelArticulo9Layout);
        panelArticulo9Layout.setHorizontalGroup(
            panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo9Layout.createSequentialGroup()
                .addComponent(panelArticulo8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(lblImagenArticulo9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo9)
                    .addGroup(panelArticulo9Layout.createSequentialGroup()
                        .addGroup(panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo9Layout.createSequentialGroup()
                                .addComponent(lblColor8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor9, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla9)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult9)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo9Layout.setVerticalGroup(
            panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo9Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo9Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo9)
                        .addContainerGap(53, Short.MAX_VALUE))
                    .addGroup(panelArticulo9Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo9)
                        .addGroup(panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo9Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor8))
                            .addGroup(panelArticulo9Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor9, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo9Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla9)
                                    .addComponent(lblTallaResult9))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
            .addGroup(panelArticulo9Layout.createSequentialGroup()
                .addComponent(panelArticulo8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel10.add(panelArticulo9);

        lblImagenArticulo10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo10.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo10.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult10.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult10.setText("M");

        lblColor9.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor9.setText("Color:");

        lblTalla10.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla10.setText("Talla:");

        btnColor10.setBackground(new java.awt.Color(255, 102, 51));

        btnVer10.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer10.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo10Layout = new javax.swing.GroupLayout(panelArticulo10);
        panelArticulo10.setLayout(panelArticulo10Layout);
        panelArticulo10Layout.setHorizontalGroup(
            panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo10Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo10)
                    .addGroup(panelArticulo10Layout.createSequentialGroup()
                        .addGroup(panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo10Layout.createSequentialGroup()
                                .addComponent(lblColor9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor10, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla10)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult10)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo10Layout.setVerticalGroup(
            panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo10Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo10Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo10)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo10Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo10)
                        .addGroup(panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo10Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor9))
                            .addGroup(panelArticulo10Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor10, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla10)
                                    .addComponent(lblTallaResult10))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo10);

        lblImagenArticulo11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo11.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo11.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult11.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult11.setText("M");

        lblColor10.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor10.setText("Color:");

        lblTalla11.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla11.setText("Talla:");

        btnColor11.setBackground(new java.awt.Color(255, 102, 51));

        btnVer11.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer11.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo11Layout = new javax.swing.GroupLayout(panelArticulo11);
        panelArticulo11.setLayout(panelArticulo11Layout);
        panelArticulo11Layout.setHorizontalGroup(
            panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo11)
                    .addGroup(panelArticulo11Layout.createSequentialGroup()
                        .addGroup(panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo11Layout.createSequentialGroup()
                                .addComponent(lblColor10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor11, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla11)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult11)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo11Layout.setVerticalGroup(
            panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo11Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo11Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo11)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo11Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo11)
                        .addGroup(panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo11Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor10))
                            .addGroup(panelArticulo11Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor11, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo11Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla11)
                                    .addComponent(lblTallaResult11))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo11);

        lblImagenArticulo12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/imagencatalogo.png"))); // NOI18N

        lblNombreArticulo12.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblNombreArticulo12.setText("Blusa con Shorts  en Conjuto");

        lblTallaResult12.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTallaResult12.setText("M");

        lblColor11.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblColor11.setText("Color:");

        lblTalla12.setFont(new java.awt.Font("Lucida Bright", 0, 18)); // NOI18N
        lblTalla12.setText("Talla:");

        btnColor12.setBackground(new java.awt.Color(255, 102, 51));

        btnVer12.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer12.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ver.png"))); // NOI18N
        btnVer12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVer12ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelArticulo12Layout = new javax.swing.GroupLayout(panelArticulo12);
        panelArticulo12.setLayout(panelArticulo12Layout);
        panelArticulo12Layout.setHorizontalGroup(
            panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo12Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblImagenArticulo12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombreArticulo12)
                    .addGroup(panelArticulo12Layout.createSequentialGroup()
                        .addGroup(panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnVer12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelArticulo12Layout.createSequentialGroup()
                                .addComponent(lblColor11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnColor12, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39)
                                .addComponent(lblTalla12)))
                        .addGap(18, 18, 18)
                        .addComponent(lblTallaResult12)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        panelArticulo12Layout.setVerticalGroup(
            panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArticulo12Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelArticulo12Layout.createSequentialGroup()
                        .addComponent(lblImagenArticulo12)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelArticulo12Layout.createSequentialGroup()
                        .addComponent(lblNombreArticulo12)
                        .addGroup(panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelArticulo12Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(lblColor11))
                            .addGroup(panelArticulo12Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnColor12, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArticulo12Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelArticulo12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblTalla12)
                                    .addComponent(lblTallaResult12))))
                        .addGap(37, 37, 37)
                        .addComponent(btnVer12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))))
        );

        jPanel10.add(panelArticulo12);

        lblPagina1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblPagina1.setText("Pagina 1");

        lblArticulos.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblArticulos.setText("Pantalones (125 articulos)");

        btnRightPagina.setPreferredSize(new java.awt.Dimension(29, 259));
        btnRightPagina.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right.png"))); // NOI18N
        btnRightPagina.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right.png"))); // NOI18N
        btnRightPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightPaginaActionPerformed(evt);
            }
        });

        btnLeftPagina1.setPreferredSize(new java.awt.Dimension(29, 259));
        btnLeftPagina1.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
        btnLeftPagina1.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
        btnLeftPagina1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftPagina1ActionPerformed(evt);
            }
        });

        jPanel17.setBackground(new java.awt.Color(0, 0, 0));

        txtBuscador.setBackground(new java.awt.Color(0, 0, 0));
        txtBuscador.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        txtBuscador.setForeground(new java.awt.Color(255, 255, 255));
        txtBuscador.setText("Buscar");
        txtBuscador.setToolTipText("");
        txtBuscador.setBorder(null);
        txtBuscador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscadorActionPerformed(evt);
            }
        });

        jLabel42.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buscar.png"))); // NOI18N

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBuscador, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 1623, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitulo)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(107, 107, 107)
                                .addComponent(lblArticulos))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(719, 719, 719)
                        .addComponent(btnLeftPagina1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPagina1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRightPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(lblTitulo)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 827, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(lblArticulos)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRightPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLeftPagina1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblPagina1)))
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRightPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightPaginaActionPerformed
        if (hayMasPaginas) {
            paginaActual++;
            lblPagina1.setText("Pagina " + paginaActual);

            cargarVariantes(paginaActual, tamanoPagina, filtroActual);
        }
    }//GEN-LAST:event_btnRightPaginaActionPerformed

    private void btnLeftPagina1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftPagina1ActionPerformed
        if (paginaActual > 1) {
            paginaActual--;
            lblPagina1.setText("Pagina " + paginaActual);
            cargarVariantes(paginaActual, tamanoPagina, filtroActual);
        }
    }//GEN-LAST:event_btnLeftPagina1ActionPerformed

    private void txtBuscadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscadorActionPerformed
        filtroActual = txtBuscador.getText().trim();
        paginaActual = 1;
        cargarVariantes(paginaActual, tamanoPagina, filtroActual);
    }//GEN-LAST:event_txtBuscadorActionPerformed

    private void btnVer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer1ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(0); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer1ActionPerformed

    private void btnVer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer2ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(1); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer2ActionPerformed

    private void btnVer3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer3ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(2); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer3ActionPerformed

    private void btnVer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer4ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(3); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer4ActionPerformed

    private void btnVer5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer5ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(4); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer5ActionPerformed

    private void btnVer6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer6ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(5); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer6ActionPerformed

    private void btnVer7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer7ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(6); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer7ActionPerformed

    private void btnVer8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer8ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(7); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer8ActionPerformed

    private void btnVer9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer9ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(8); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer9ActionPerformed

    private void btnVer10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer10ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(9); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer10ActionPerformed

    private void btnVer11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer11ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(10); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer11ActionPerformed

    private void btnVer12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer12ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(11); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, frmPrincipal.getVarianteProductoNegocio(), frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer12ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnColor1;
    private javax.swing.JButton btnColor10;
    private javax.swing.JButton btnColor11;
    private javax.swing.JButton btnColor12;
    private javax.swing.JButton btnColor2;
    private javax.swing.JButton btnColor3;
    private javax.swing.JButton btnColor4;
    private javax.swing.JButton btnColor5;
    private javax.swing.JButton btnColor6;
    private javax.swing.JButton btnColor7;
    private javax.swing.JButton btnColor8;
    private javax.swing.JButton btnColor9;
    private utils.BotonMenu btnLeftPagina1;
    private utils.BotonMenu btnRightPagina;
    private utils.BotonMenu btnVer1;
    private utils.BotonMenu btnVer10;
    private utils.BotonMenu btnVer11;
    private utils.BotonMenu btnVer12;
    private utils.BotonMenu btnVer2;
    private utils.BotonMenu btnVer3;
    private utils.BotonMenu btnVer4;
    private utils.BotonMenu btnVer5;
    private utils.BotonMenu btnVer6;
    private utils.BotonMenu btnVer7;
    private utils.BotonMenu btnVer8;
    private utils.BotonMenu btnVer9;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JLabel lblArticulos;
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblColor1;
    private javax.swing.JLabel lblColor10;
    private javax.swing.JLabel lblColor11;
    private javax.swing.JLabel lblColor2;
    private javax.swing.JLabel lblColor3;
    private javax.swing.JLabel lblColor4;
    private javax.swing.JLabel lblColor5;
    private javax.swing.JLabel lblColor6;
    private javax.swing.JLabel lblColor7;
    private javax.swing.JLabel lblColor8;
    private javax.swing.JLabel lblColor9;
    private javax.swing.JLabel lblImagenArticulo1;
    private javax.swing.JLabel lblImagenArticulo10;
    private javax.swing.JLabel lblImagenArticulo11;
    private javax.swing.JLabel lblImagenArticulo12;
    private javax.swing.JLabel lblImagenArticulo2;
    private javax.swing.JLabel lblImagenArticulo3;
    private javax.swing.JLabel lblImagenArticulo4;
    private javax.swing.JLabel lblImagenArticulo5;
    private javax.swing.JLabel lblImagenArticulo6;
    private javax.swing.JLabel lblImagenArticulo7;
    private javax.swing.JLabel lblImagenArticulo8;
    private javax.swing.JLabel lblImagenArticulo9;
    private javax.swing.JLabel lblNombreArticulo1;
    private javax.swing.JLabel lblNombreArticulo10;
    private javax.swing.JLabel lblNombreArticulo11;
    private javax.swing.JLabel lblNombreArticulo12;
    private javax.swing.JLabel lblNombreArticulo2;
    private javax.swing.JLabel lblNombreArticulo3;
    private javax.swing.JLabel lblNombreArticulo4;
    private javax.swing.JLabel lblNombreArticulo5;
    private javax.swing.JLabel lblNombreArticulo6;
    private javax.swing.JLabel lblNombreArticulo7;
    private javax.swing.JLabel lblNombreArticulo8;
    private javax.swing.JLabel lblNombreArticulo9;
    private javax.swing.JLabel lblPagina1;
    private javax.swing.JLabel lblTalla1;
    private javax.swing.JLabel lblTalla10;
    private javax.swing.JLabel lblTalla11;
    private javax.swing.JLabel lblTalla12;
    private javax.swing.JLabel lblTalla2;
    private javax.swing.JLabel lblTalla3;
    private javax.swing.JLabel lblTalla4;
    private javax.swing.JLabel lblTalla5;
    private javax.swing.JLabel lblTalla6;
    private javax.swing.JLabel lblTalla7;
    private javax.swing.JLabel lblTalla8;
    private javax.swing.JLabel lblTalla9;
    private javax.swing.JLabel lblTallaResult1;
    private javax.swing.JLabel lblTallaResult10;
    private javax.swing.JLabel lblTallaResult11;
    private javax.swing.JLabel lblTallaResult12;
    private javax.swing.JLabel lblTallaResult2;
    private javax.swing.JLabel lblTallaResult3;
    private javax.swing.JLabel lblTallaResult4;
    private javax.swing.JLabel lblTallaResult5;
    private javax.swing.JLabel lblTallaResult6;
    private javax.swing.JLabel lblTallaResult7;
    private javax.swing.JLabel lblTallaResult8;
    private javax.swing.JLabel lblTallaResult9;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelArticulo1;
    private javax.swing.JPanel panelArticulo10;
    private javax.swing.JPanel panelArticulo11;
    private javax.swing.JPanel panelArticulo12;
    private javax.swing.JPanel panelArticulo2;
    private javax.swing.JPanel panelArticulo3;
    private javax.swing.JPanel panelArticulo4;
    private javax.swing.JPanel panelArticulo5;
    private javax.swing.JPanel panelArticulo6;
    private javax.swing.JPanel panelArticulo7;
    private javax.swing.JPanel panelArticulo8;
    private javax.swing.JPanel panelArticulo9;
    private javax.swing.JTextField txtBuscador;
    // End of variables declaration//GEN-END:variables
}
