package com.mycompany.chazzboutique;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.mycompany.chazzboutiquenegocio.dtos.InicioSesionDTO;
import com.mycompany.chazzboutiquenegocio.dtos.UsuarioDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.*;
import com.mycompany.chazzboutiquenegocio.objetosNegocio.*;
import com.mycompany.chazzboutiquepersistencia.conexion.ConexionBD;
import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.daos.*;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import presentacion.FrmPrincipal;

public class ChazzBoutique {

    public static void main(String[] args) {
        try {
           
            UIManager.setLookAndFeel(new FlatMacLightLaf());
             UIManager.put("Button.disabledBackground", UIManager.get("Button.background"));
            UIManager.put("Button.disabledForeground", UIManager.get("Button.foreground"));

        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ChazzBoutique.class.getName()).log(Level.SEVERE, null, ex);
        }

        IConexionBD conexionBD = new ConexionBD();

        // DAOs
        IUsuarioDAO usuarioDAO = new UsuarioDAO(conexionBD);
        IVentaDAO ventaDAO = new VentaDAO(conexionBD);
        IVarianteProductoDAO varianteProductoDAO = new VarianteProductoDAO(conexionBD);
        IProductoDAO productoDAO = new ProductoDAO(conexionBD);
        IDetalleVentaDAO detalleVentaDAO = new DetalleVentaDAO(conexionBD);
        ICategoriaDAO categoriaDAO = new CategoriaDAO(conexionBD);
        IProveedorDAO proveedorDAO = new ProveedorDAO(conexionBD);
        IReporteDAO reporteDAO = new ReporteDAO(conexionBD);

        // Negocios
        IUsuarioNegocio usuarioNegocio = new UsuarioNegocio(usuarioDAO);
        IVentaNegocio ventaNegocio = new VentaNegocio(ventaDAO, detalleVentaDAO, varianteProductoDAO, usuarioDAO);
        IVarianteProductoNegocio varianteProductoNegocio = new VarianteProductoNegocio(varianteProductoDAO, productoDAO);
        IProductoNegocio productoNegocio = new ProductoNegocio(productoDAO, categoriaDAO, proveedorDAO);
        ICategoriaNegocio categoriaNegocio = new CategoriaNegocio(categoriaDAO);
        IProveedorNegocio proveedorNegocio = new ProveedorNegocio(proveedorDAO);
        IReporteNegocio reporteNegocio = new ReporteNegocio(reporteDAO);

        try {

            UsuarioDTO usuarioRegistrado = usuarioNegocio.iniciarSesion(new InicioSesionDTO("Yalam", "12345"));

            // Creación de ventana principal
            FrmPrincipal frmPrincipal = new FrmPrincipal(
                    usuarioNegocio,
                    ventaNegocio,
                    varianteProductoNegocio,
                    productoNegocio,
                    categoriaNegocio,
                    proveedorNegocio,
                    reporteNegocio,
                    usuarioRegistrado
            );

            frmPrincipal.setVisible(true);
        } catch (NegocioException ex) {
            Logger.getLogger(ChazzBoutique.class.getName()).log(Level.SEVERE, "Error al iniciar sesión", ex);
            System.out.println("Error al iniciar sesión: " + ex.getMessage());
        }
    }
}
