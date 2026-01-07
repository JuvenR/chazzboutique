package com.juvenr.mqc.chazzboutiqueapi.config;

import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVentaNegocio;
import com.mycompany.chazzboutiquenegocio.objetosNegocio.ProductoNegocio;
import com.mycompany.chazzboutiquenegocio.objetosNegocio.VarianteProductoNegocio;
import com.mycompany.chazzboutiquenegocio.objetosNegocio.VentaNegocio;
import com.mycompany.chazzboutiquepersistencia.conexion.ConexionBD;
import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.daos.CategoriaDAO;
import com.mycompany.chazzboutiquepersistencia.daos.DetalleVentaDAO;
import com.mycompany.chazzboutiquepersistencia.daos.ProductoDAO;
import com.mycompany.chazzboutiquepersistencia.daos.ProveedorDAO;
import com.mycompany.chazzboutiquepersistencia.daos.UsuarioDAO;
import com.mycompany.chazzboutiquepersistencia.daos.VarianteProductoDAO;
import com.mycompany.chazzboutiquepersistencia.daos.VentaDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.ICategoriaDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IDetalleVentaDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IProductoDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IProveedorDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IUsuarioDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVarianteProductoDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVentaDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NegocioBeansConfig {

    @Bean
    public IConexionBD conexionBD() {
        return new ConexionBD();
    }

    @Bean
    public IVentaDAO ventaDAO(IConexionBD conexionBD) {
        return new VentaDAO(conexionBD);
    }

    @Bean
    public IDetalleVentaDAO detalleVentaDAO(IConexionBD conexionBD) {
        return new DetalleVentaDAO(conexionBD);
    }

    @Bean
    public IVarianteProductoDAO varianteProductoDAO(IConexionBD conexionBD) {
        return new VarianteProductoDAO(conexionBD);
    }

    @Bean
    public IUsuarioDAO usuarioDAO(IConexionBD conexionBD) {
        return new UsuarioDAO(conexionBD);
    }

    @Bean
    public IProductoDAO productoDAO(IConexionBD conexionBD) {
        return new ProductoDAO(conexionBD);
    }
    
        @Bean
    public ICategoriaDAO categoriaDAO(IConexionBD conexionBD) {
        return new CategoriaDAO(conexionBD);
    }
       @Bean
    public IProveedorDAO proveedorDAO(IConexionBD conexionBD) {
        return new ProveedorDAO(conexionBD);
    }


    @Bean
    public IVentaNegocio ventaNegocio(
            IVentaDAO ventaDAO,
            IDetalleVentaDAO detalleVentaDAO,
            IVarianteProductoDAO varianteProductoDAO,
            IUsuarioDAO usuarioDAO
    ) {
        return new VentaNegocio(ventaDAO, detalleVentaDAO, varianteProductoDAO, usuarioDAO);
    }

    @Bean
    public IVarianteProductoNegocio varianteProductoNegocio(
            IVarianteProductoDAO varianteProductoDAO,
            IProductoDAO productoDAO
    ) {
        return new VarianteProductoNegocio(varianteProductoDAO, productoDAO);
    }

    @Bean
    public IProductoNegocio productoNegocio(IProductoDAO productoDAO, ICategoriaDAO categoriaDAO, IProveedorDAO proveedorDAO) {
        return new ProductoNegocio(productoDAO,categoriaDAO, proveedorDAO);
    }

}
