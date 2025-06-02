/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package presentacion;

import com.mycompany.chazzboutiquenegocio.dtos.CategoriaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionListener;
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
public class PanelHome extends javax.swing.JPanel {

    private IVarianteProductoNegocio varianteNegocio;
    private FrmPrincipal frmPrincipal;
    private List<CategoriaDTO> categorias;
    private int indiceCarrusel = 0;
    private final int VISTA_MAXIMA = 5;
    private int paginaActual = 1;
    private final int tamanoPagina = 6;
    private String filtroActual = "";
    private boolean hayMasPaginas = true;
    private List<VarianteProductoDTO> listaActualDeVariantes;

    public PanelHome(FrmPrincipal frmPrincipal) {
        initComponents();
        this.frmPrincipal = frmPrincipal;
        this.varianteNegocio = frmPrincipal.getVarianteProductoNegocio();
        cargarCategorias();
        this.setSize(1749, 1177);
        cargarVariantes(paginaActual, tamanoPagina, filtroActual);

        txtBuscador.setForeground(Color.GRAY);
        txtBuscador.setText("Buscar");

// Manejador de focus
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

    private void cargarCategorias() {
        try {
            categorias = frmPrincipal.getCategoriaNegocio().obtenerCategorias();
            mostrarCategorias();

        } catch (NegocioException ex) {
            Logger.getLogger(PanelHome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void mostrarCategorias() {
        List<JPanel> paneles = List.of(panelCategoria1, panelCategoria2, panelCategoria3, panelCategoria4, panelCategoria5);
        List<JLabel> etiquetas = List.of(lblCategoria1, lblCategoria2, lblCategoria3, lblCategoria4, lblCategoria5);
        List<JButton> botones = List.of(btnImagenCategoria1, btnImagenCategoria2, btnImagenCategoria3, btnImagenCategoria4, btnImagenCategoria5);
        if (categorias == null || categorias.isEmpty()) {
            // Ocultar todos los paneles si no hay categorías
            for (JPanel panel : paneles) {
                panel.setVisible(false);
            }
            return;
        }
        int total = categorias.size();

        for (int i = 0; i < VISTA_MAXIMA; i++) {
            int index = (indiceCarrusel + i) % total;
            CategoriaDTO cat = categorias.get(index);

            etiquetas.get(i).setText(capitalizarNombre(cat.getNombreCategoria()));

            File archivoImagen = new File("imagenes/" + cat.getImagenCategoria());
            if (archivoImagen.exists()) {
                ImageIcon icon = new ImageIcon(archivoImagen.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(166, 247, Image.SCALE_SMOOTH);
                botones.get(i).setIcon(new ImageIcon(img));
            } else {
                botones.get(i).setIcon(null); // O una imagen por defecto si quieres
            }

            for (ActionListener al : botones.get(i).getActionListeners()) {
                botones.get(i).removeActionListener(al);
            }

            CategoriaDTO copiaCategoria = cat;
            botones.get(i).addActionListener(evt -> {
                PanelCategoriaProducto panelCategoria = new PanelCategoriaProducto(frmPrincipal, copiaCategoria);
                frmPrincipal.pintarPanelPrincipal(panelCategoria);
            });
        }
    }

    private void cargarVariantes(int pagina, int tamañoPagina, String filtro) {
        try {

            List<JPanel> panelesArticulo = List.of(
                    panelArticulo1, panelArticulo2, panelArticulo3,
                    panelArticulo4, panelArticulo5, panelArticulo6
            );

            List<VarianteProductoDTO> variantes = frmPrincipal.getVarianteProductoNegocio()
                    .buscarVariantesPorNombreProducto(filtro, pagina, tamañoPagina);
            listaActualDeVariantes = variantes;
            long total = frmPrincipal.getVarianteProductoNegocio().contarVariantesPorNombreProducto(filtroActual);
            lblArticulos.setText("Todos (" + total + " artículos)");

            List<VarianteProductoDTO> siguientePagina = frmPrincipal.getVarianteProductoNegocio()
                    .buscarVariantesPorNombreProducto(filtro, pagina + 1, tamañoPagina);

            hayMasPaginas = !siguientePagina.isEmpty();
            // actualizar la interfaz (como ya lo haces)
            List<JLabel> etiquetasNombre = List.of(lblNombreArticulo1, lblNombreArticulo2, lblNombreArticulo3, lblNombreArticulo4, lblNombreArticulo5, lblNombreArticulo6);
            List<JLabel> etiquetasTalla = List.of(lblTallaResult1, lblTallaResult2, lblTallaResult3, lblTallaResult4, lblTallaResult5, lblTallaResult6);
            List<JButton> botonesColor = List.of(btnColor1, btnColor2, btnColor3, btnColor4, btnColor5, btnColor6);
            List<JLabel> etiquetasImagen = List.of(lblImagenArticulo1, lblImagenArticulo2, lblImagenArticulo3, lblImagenArticulo4, lblImagenArticulo5, lblImagenArticulo6);

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
        panelCarrusel = new javax.swing.JPanel();
        btnLeftCategoria = new utils.BotonMenu();
        panelCategoria1 = new javax.swing.JPanel();
        btnImagenCategoria1 = new javax.swing.JButton();
        lblCategoria1 = new javax.swing.JLabel();
        panelCategoria2 = new javax.swing.JPanel();
        btnImagenCategoria2 = new javax.swing.JButton();
        lblCategoria2 = new javax.swing.JLabel();
        panelCategoria3 = new javax.swing.JPanel();
        btnImagenCategoria3 = new javax.swing.JButton();
        lblCategoria3 = new javax.swing.JLabel();
        panelCategoria4 = new javax.swing.JPanel();
        btnImagenCategoria4 = new javax.swing.JButton();
        lblCategoria4 = new javax.swing.JLabel();
        panelCategoria5 = new javax.swing.JPanel();
        btnImagenCategoria5 = new javax.swing.JButton();
        lblCategoria5 = new javax.swing.JLabel();
        btnRightCarrusel = new utils.BotonMenu();
        jPanel10 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
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
        jPanel3 = new javax.swing.JPanel();
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
        lblCategorias = new javax.swing.JLabel();
        lblPagina1 = new javax.swing.JLabel();
        lblArticulos = new javax.swing.JLabel();
        btnRightPagina = new utils.BotonMenu();
        btnLeftPagina1 = new utils.BotonMenu();
        jPanel17 = new javax.swing.JPanel();
        txtBuscador = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 0, 64)); // NOI18N
        lblTitulo.setText("Catalogo");

        panelCarrusel.setBackground(new java.awt.Color(255, 255, 255));
        panelCarrusel.setMinimumSize(new java.awt.Dimension(1920, 259));

        btnLeftCategoria.setPreferredSize(new java.awt.Dimension(29, 259));
        btnLeftCategoria.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
        btnLeftCategoria.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left.png"))); // NOI18N
        btnLeftCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftCategoriaActionPerformed(evt);
            }
        });
        panelCarrusel.add(btnLeftCategoria);

        panelCategoria1.setBackground(new java.awt.Color(255, 255, 255));

        btnImagenCategoria1.setBackground(new java.awt.Color(248, 253, 253));
        btnImagenCategoria1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenPruebaCatalogo.png"))); // NOI18N
        btnImagenCategoria1.setBorderPainted(false);
        btnImagenCategoria1.setContentAreaFilled(false);

        lblCategoria1.setFont(new java.awt.Font("Lucida Bright", 0, 26)); // NOI18N
        lblCategoria1.setText("Camisas");

        javax.swing.GroupLayout panelCategoria1Layout = new javax.swing.GroupLayout(panelCategoria1);
        panelCategoria1.setLayout(panelCategoria1Layout);
        panelCategoria1Layout.setHorizontalGroup(
            panelCategoria1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoria1Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(panelCategoria1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCategoria1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblCategoria1))
                    .addComponent(btnImagenCategoria1))
                .addGap(30, 30, 30))
        );
        panelCategoria1Layout.setVerticalGroup(
            panelCategoria1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoria1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImagenCategoria1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCategoria1)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panelCarrusel.add(panelCategoria1);

        panelCategoria2.setBackground(new java.awt.Color(255, 255, 255));

        btnImagenCategoria2.setBackground(new java.awt.Color(248, 253, 253));
        btnImagenCategoria2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenPruebaCatalogo.png"))); // NOI18N
        btnImagenCategoria2.setBorderPainted(false);
        btnImagenCategoria2.setContentAreaFilled(false);

        lblCategoria2.setFont(new java.awt.Font("Lucida Bright", 0, 26)); // NOI18N
        lblCategoria2.setText("Camisas");

        javax.swing.GroupLayout panelCategoria2Layout = new javax.swing.GroupLayout(panelCategoria2);
        panelCategoria2.setLayout(panelCategoria2Layout);
        panelCategoria2Layout.setHorizontalGroup(
            panelCategoria2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoria2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(panelCategoria2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnImagenCategoria2)
                    .addGroup(panelCategoria2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblCategoria2)))
                .addGap(30, 30, 30))
        );
        panelCategoria2Layout.setVerticalGroup(
            panelCategoria2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoria2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImagenCategoria2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCategoria2)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panelCarrusel.add(panelCategoria2);

        panelCategoria3.setBackground(new java.awt.Color(255, 255, 255));

        btnImagenCategoria3.setBackground(new java.awt.Color(248, 253, 253));
        btnImagenCategoria3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenPruebaCatalogo.png"))); // NOI18N
        btnImagenCategoria3.setBorderPainted(false);
        btnImagenCategoria3.setContentAreaFilled(false);

        lblCategoria3.setFont(new java.awt.Font("Lucida Bright", 0, 26)); // NOI18N
        lblCategoria3.setText("Camisas");

        javax.swing.GroupLayout panelCategoria3Layout = new javax.swing.GroupLayout(panelCategoria3);
        panelCategoria3.setLayout(panelCategoria3Layout);
        panelCategoria3Layout.setHorizontalGroup(
            panelCategoria3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoria3Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(panelCategoria3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCategoria3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblCategoria3))
                    .addComponent(btnImagenCategoria3))
                .addGap(30, 30, 30))
        );
        panelCategoria3Layout.setVerticalGroup(
            panelCategoria3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoria3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImagenCategoria3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCategoria3)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panelCarrusel.add(panelCategoria3);

        panelCategoria4.setBackground(new java.awt.Color(255, 255, 255));

        btnImagenCategoria4.setBackground(new java.awt.Color(248, 253, 253));
        btnImagenCategoria4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenPruebaCatalogo.png"))); // NOI18N
        btnImagenCategoria4.setBorderPainted(false);
        btnImagenCategoria4.setContentAreaFilled(false);

        lblCategoria4.setFont(new java.awt.Font("Lucida Bright", 0, 26)); // NOI18N
        lblCategoria4.setText("Camisas");

        javax.swing.GroupLayout panelCategoria4Layout = new javax.swing.GroupLayout(panelCategoria4);
        panelCategoria4.setLayout(panelCategoria4Layout);
        panelCategoria4Layout.setHorizontalGroup(
            panelCategoria4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoria4Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(panelCategoria4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCategoria4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblCategoria4))
                    .addComponent(btnImagenCategoria4))
                .addGap(30, 30, 30))
        );
        panelCategoria4Layout.setVerticalGroup(
            panelCategoria4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoria4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImagenCategoria4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCategoria4)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panelCarrusel.add(panelCategoria4);

        panelCategoria5.setBackground(new java.awt.Color(255, 255, 255));

        btnImagenCategoria5.setBackground(new java.awt.Color(248, 253, 253));
        btnImagenCategoria5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagenPruebaCatalogo.png"))); // NOI18N
        btnImagenCategoria5.setBorderPainted(false);
        btnImagenCategoria5.setContentAreaFilled(false);

        lblCategoria5.setFont(new java.awt.Font("Lucida Bright", 0, 26)); // NOI18N
        lblCategoria5.setText("Camisas");

        javax.swing.GroupLayout panelCategoria5Layout = new javax.swing.GroupLayout(panelCategoria5);
        panelCategoria5.setLayout(panelCategoria5Layout);
        panelCategoria5Layout.setHorizontalGroup(
            panelCategoria5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCategoria5Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(panelCategoria5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCategoria5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblCategoria5))
                    .addComponent(btnImagenCategoria5))
                .addGap(30, 30, 30))
        );
        panelCategoria5Layout.setVerticalGroup(
            panelCategoria5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCategoria5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnImagenCategoria5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCategoria5)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        panelCarrusel.add(panelCategoria5);

        btnRightCarrusel.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right.png"))); // NOI18N
        btnRightCarrusel.setSimpleIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right.png"))); // NOI18N
        btnRightCarrusel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightCarruselActionPerformed(evt);
            }
        });
        panelCarrusel.add(btnRightCarrusel);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setPreferredSize(new java.awt.Dimension(1310, 420));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        jPanel2.add(panelArticulo1);

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

        jPanel2.add(panelArticulo2);

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

        jPanel2.add(panelArticulo3);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(1298, 196));

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

        jPanel3.add(panelArticulo4);

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

        jPanel3.add(panelArticulo5);

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

        jPanel3.add(panelArticulo6);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblCategorias.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblCategorias.setText("Categorias");

        lblPagina1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblPagina1.setText("Pagina 1");

        lblArticulos.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        lblArticulos.setText("Todos (125 articulos)");

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
                .addComponent(txtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(280, 280, 280)
                .addComponent(lblCategorias)
                .addGap(242, 242, 242)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelCarrusel, javax.swing.GroupLayout.PREFERRED_SIZE, 1743, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(285, 285, 285)
                .addComponent(lblArticulos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(790, 790, 790)
                        .addComponent(btnLeftPagina1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblPagina1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRightPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(lblCategorias))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelCarrusel, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(lblArticulos)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRightPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLeftPagina1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblPagina1)))
                .addContainerGap(92, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnLeftCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftCategoriaActionPerformed
        if (categorias.isEmpty()) {
            return;
        }
        indiceCarrusel = (indiceCarrusel - 1 + categorias.size()) % categorias.size();
        mostrarCategorias();
    }//GEN-LAST:event_btnLeftCategoriaActionPerformed

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
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);
    }//GEN-LAST:event_btnVer1ActionPerformed

    private void btnRightCarruselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightCarruselActionPerformed
        if (categorias.isEmpty()) {
            return;
        }
        indiceCarrusel = (indiceCarrusel + 1 + categorias.size()) % categorias.size();
        mostrarCategorias();
    }//GEN-LAST:event_btnRightCarruselActionPerformed

    private void btnVer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer2ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(1); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);        // TODO add your handling code here:
    }//GEN-LAST:event_btnVer2ActionPerformed

    private void btnVer3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer3ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(2); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);        // TODO add your handling code here:
    }//GEN-LAST:event_btnVer3ActionPerformed

    private void btnVer4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer4ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(3); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);    }//GEN-LAST:event_btnVer4ActionPerformed

    private void btnVer5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer5ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(4); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);    }//GEN-LAST:event_btnVer5ActionPerformed

    private void btnVer6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVer6ActionPerformed
        VarianteProductoDTO seleccionada = listaActualDeVariantes.get(5); // o el índice correspondiente
        PnlVarianteProducto pnl = new PnlVarianteProducto(seleccionada, varianteNegocio, frmPrincipal);
        frmPrincipal.pintarPanelPrincipal(pnl);    }//GEN-LAST:event_btnVer6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnColor1;
    private javax.swing.JButton btnColor2;
    private javax.swing.JButton btnColor3;
    private javax.swing.JButton btnColor4;
    private javax.swing.JButton btnColor5;
    private javax.swing.JButton btnColor6;
    private javax.swing.JButton btnImagenCategoria1;
    private javax.swing.JButton btnImagenCategoria2;
    private javax.swing.JButton btnImagenCategoria3;
    private javax.swing.JButton btnImagenCategoria4;
    private javax.swing.JButton btnImagenCategoria5;
    private utils.BotonMenu btnLeftCategoria;
    private utils.BotonMenu btnLeftPagina1;
    private utils.BotonMenu btnRightCarrusel;
    private utils.BotonMenu btnRightPagina;
    private utils.BotonMenu btnVer1;
    private utils.BotonMenu btnVer2;
    private utils.BotonMenu btnVer3;
    private utils.BotonMenu btnVer4;
    private utils.BotonMenu btnVer5;
    private utils.BotonMenu btnVer6;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblArticulos;
    private javax.swing.JLabel lblCategoria1;
    private javax.swing.JLabel lblCategoria2;
    private javax.swing.JLabel lblCategoria3;
    private javax.swing.JLabel lblCategoria4;
    private javax.swing.JLabel lblCategoria5;
    private javax.swing.JLabel lblCategorias;
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblColor1;
    private javax.swing.JLabel lblColor2;
    private javax.swing.JLabel lblColor3;
    private javax.swing.JLabel lblColor4;
    private javax.swing.JLabel lblColor5;
    private javax.swing.JLabel lblImagenArticulo1;
    private javax.swing.JLabel lblImagenArticulo2;
    private javax.swing.JLabel lblImagenArticulo3;
    private javax.swing.JLabel lblImagenArticulo4;
    private javax.swing.JLabel lblImagenArticulo5;
    private javax.swing.JLabel lblImagenArticulo6;
    private javax.swing.JLabel lblNombreArticulo1;
    private javax.swing.JLabel lblNombreArticulo2;
    private javax.swing.JLabel lblNombreArticulo3;
    private javax.swing.JLabel lblNombreArticulo4;
    private javax.swing.JLabel lblNombreArticulo5;
    private javax.swing.JLabel lblNombreArticulo6;
    private javax.swing.JLabel lblPagina1;
    private javax.swing.JLabel lblTalla1;
    private javax.swing.JLabel lblTalla2;
    private javax.swing.JLabel lblTalla3;
    private javax.swing.JLabel lblTalla4;
    private javax.swing.JLabel lblTalla5;
    private javax.swing.JLabel lblTalla6;
    private javax.swing.JLabel lblTallaResult1;
    private javax.swing.JLabel lblTallaResult2;
    private javax.swing.JLabel lblTallaResult3;
    private javax.swing.JLabel lblTallaResult4;
    private javax.swing.JLabel lblTallaResult5;
    private javax.swing.JLabel lblTallaResult6;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelArticulo1;
    private javax.swing.JPanel panelArticulo2;
    private javax.swing.JPanel panelArticulo3;
    private javax.swing.JPanel panelArticulo4;
    private javax.swing.JPanel panelArticulo5;
    private javax.swing.JPanel panelArticulo6;
    private javax.swing.JPanel panelCarrusel;
    private javax.swing.JPanel panelCategoria1;
    private javax.swing.JPanel panelCategoria2;
    private javax.swing.JPanel panelCategoria3;
    private javax.swing.JPanel panelCategoria4;
    private javax.swing.JPanel panelCategoria5;
    private javax.swing.JTextField txtBuscador;
    // End of variables declaration//GEN-END:variables
}
