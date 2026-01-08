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
            if (ventaDTO.getDetalles() == null || ventaDTO.getDetalles().isEmpty()) {
                throw new NegocioException("Debe agregar productos a la venta");
            }

            for (DetalleVentaDTO d : ventaDTO.getDetalles()) {
                VarianteProducto v = varianteProductoDAO.obtenerPorCodigoBarra(d.getCodigoVariante());
                if (v.getStock() < d.getCantidad()) {
                    throw new NegocioException("Stock insuficiente para: " + v.getProducto().getNombre());
                }
            }

            Venta venta = new Venta();
            venta.setUsuario(usuarioDAO.buscarPorId(ventaDTO.getUsuarioId()));
            venta.setFechaVenta(LocalDate.now());
            venta.setVentaTotal(ventaDTO.getTotal());
            venta.setEstadoVenta("COMPLETADA");
            venta.setDescuento(ventaDTO.getDescuento());
            venta.setMontoPago(ventaDTO.getMontoPago());
            venta.setCambio(ventaDTO.getCambio());
            venta.setVentaTotal(ventaDTO.getTotal());
            for (DetalleVentaDTO d : ventaDTO.getDetalles()) {
                VarianteProducto v = varianteProductoDAO.obtenerPorCodigoBarra(d.getCodigoVariante());

                if (v.getStock() < d.getCantidad()) {
                    throw new NegocioException("Stock insuficiente para: " + v.getProducto().getNombre());
                }

                DetalleVenta det = new DetalleVenta();
                det.setVarianteProducto(v);
                det.setCantidad(d.getCantidad());
                det.setPrecioUnitario(d.getPrecioUnitario());

                venta.addDetalle(det);

                v.setStock(v.getStock() - d.getCantidad());
                varianteProductoDAO.actualizarVarianteProducto(v);
            }

            venta = ventaDAO.registrarVenta(venta);
            ventaDTO.setId(venta.getId());
            return ventaDTO;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Error al registrar venta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public VentaDTO obtenerVentaConDetalles(Long ventaId) throws NegocioException {
        try {
            Venta venta = ventaDAO.buscarPorId(ventaId);
            if (venta == null) {
                throw new NegocioException("No existe la venta con id: " + ventaId);
            }

            VentaDTO dto = new VentaDTO();
            dto.setId(venta.getId());
            dto.setUsuarioId(venta.getUsuario().getId());
            dto.setVendedorNombre(venta.getUsuario().getNombreUsuario());
            dto.setDescuento(venta.getDescuento());
            dto.setMontoPago(venta.getMontoPago());
            dto.setCambio(venta.getCambio());

            dto.setFecha(venta.getFechaVenta());
            dto.setTotal(venta.getVentaTotal());
            dto.setEstado(venta.getEstadoVenta());

            java.util.List<DetalleVentaDTO> detalles = new java.util.ArrayList<>();
            for (DetalleVenta det : venta.getDetallesVentas()) {
                DetalleVentaDTO d = new DetalleVentaDTO();
                d.setCantidad(det.getCantidad());
                d.setPrecioUnitario(det.getPrecioUnitario());
                d.setVarianteProductoId(det.getVarianteProducto().getId());
                d.setCodigoVariante(det.getVarianteProducto().getCodigoBarra());
                detalles.add(d);
            }
            dto.setDetalles(detalles);

            return dto;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Error al consultar venta: " + ex.getMessage(), ex);
        }
    }

}
