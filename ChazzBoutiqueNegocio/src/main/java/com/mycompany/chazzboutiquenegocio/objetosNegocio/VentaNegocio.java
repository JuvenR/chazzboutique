/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquenegocio.objetosNegocio;

import com.mycompany.chazzboutiquenegocio.dtos.DetalleVentaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VentaDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVentaNegocio;
import com.mycompany.chazzboutiquepersistencia.dominio.DetalleVenta;
import com.mycompany.chazzboutiquepersistencia.dominio.VarianteProducto;
import com.mycompany.chazzboutiquepersistencia.dominio.Venta;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IDetalleVentaDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IUsuarioDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVarianteProductoDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVentaDAO;
import java.time.LocalDate;

/**
 *
 * @author carli
 */
public class VentaNegocio implements IVentaNegocio {

    private final IVentaDAO ventaDAO;
    private final IDetalleVentaDAO detalleVentaDAO;
    private final IVarianteProductoDAO varianteProductoDAO;
    private final IUsuarioDAO usuarioDAO;

    public VentaNegocio(IVentaDAO ventaDAO, IDetalleVentaDAO detalleVentaDAO,
            IVarianteProductoDAO varianteProductoDAO, IUsuarioDAO usuarioDAO) {
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.varianteProductoDAO = varianteProductoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    public VentaDTO registrarVenta(VentaDTO ventaDTO) throws NegocioException {
        try {
            // Validaciones
            if (ventaDTO.getDetalles() == null || ventaDTO.getDetalles().isEmpty()) {
                throw new NegocioException("Debe agregar productos a la venta");
            }
            for (DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
                System.out.println(detalleDTO.toString());
                VarianteProducto variante = varianteProductoDAO.obtenerPorCodigoBarra(detalleDTO.getCodigoVariante());
                if (variante.getStock() < detalleDTO.getCantidad()) {
                    throw new NegocioException("Stock insuficiente para: " + variante.getProducto().getNombre());
                }

                // Convertir DTO a entidad Venta
                Venta venta = new Venta();
                venta.setUsuario(usuarioDAO.buscarPorId(ventaDTO.getUsuarioId()));
                venta.setFechaVenta(LocalDate.now());
                venta.setVentaTotal(ventaDTO.getTotal());
                venta.setEstadoVenta("COMPLETADA");

                // Registrar venta primero para obtener ID
                venta = ventaDAO.registrarVenta(venta);
                ventaDTO.setId(venta.getId());
                // Procesar detalles

                // Crear y registrar detalle
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setVarianteProducto(variante);
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
                detalleVentaDAO.registrarDetalle(detalle);

                // Actualizar stock
                variante.setStock(variante.getStock() - detalleDTO.getCantidad());
                varianteProductoDAO.actualizarVarianteProducto(variante);
            }

            return ventaDTO;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Error al registrar venta: " + ex.getMessage(), ex);
        }
    }
}
