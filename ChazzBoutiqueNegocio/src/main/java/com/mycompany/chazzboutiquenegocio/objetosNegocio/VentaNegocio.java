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

            // 1) Validar stock primero
            for (DetalleVentaDTO d : ventaDTO.getDetalles()) {
                VarianteProducto v = varianteProductoDAO.obtenerPorCodigoBarra(d.getCodigoVariante());
                if (v.getStock() < d.getCantidad()) {
                    throw new NegocioException("Stock insuficiente para: " + v.getProducto().getNombre());
                }
            }

            // 2) Crear venta UNA vez
            Venta venta = new Venta();
            venta.setUsuario(usuarioDAO.buscarPorId(ventaDTO.getUsuarioId()));
            venta.setFechaVenta(LocalDate.now());
            venta.setVentaTotal(ventaDTO.getTotal());
            venta.setEstadoVenta("COMPLETADA");

            venta = ventaDAO.registrarVenta(venta);
            ventaDTO.setId(venta.getId());

            // 3) Registrar detalles + actualizar stock
            for (DetalleVentaDTO d : ventaDTO.getDetalles()) {
                VarianteProducto v = varianteProductoDAO.obtenerPorCodigoBarra(d.getCodigoVariante());

                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setVarianteProducto(v);
                detalle.setCantidad(d.getCantidad());
                detalle.setPrecioUnitario(d.getPrecioUnitario());

                detalleVentaDAO.registrarDetalle(detalle);

                v.setStock(v.getStock() - d.getCantidad());
                varianteProductoDAO.actualizarVarianteProducto(v);
            }

            return ventaDTO;

        } catch (PersistenciaException ex) {
            throw new NegocioException("Error al registrar venta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public VentaDTO obtenerVentaConDetalles(Long ventaId) throws NegocioException {
        try {
            Venta venta = ventaDAO.buscarPorId(ventaId); // lo agregamos abajo en el DAO
            if (venta == null) {
                throw new NegocioException("No existe la venta con id: " + ventaId);
            }

            // Si tu JPA trae detalles lazy, el DAO debe traerlos con join fetch (abajo).
            // Aquí ya asumimos que vienen cargados.
            VentaDTO dto = new VentaDTO();
            dto.setId(venta.getId());
            dto.setUsuarioId(venta.getUsuario().getId());
            dto.setFecha(venta.getFechaVenta());              // si tu dto usa LocalDate
            dto.setTotal(venta.getVentaTotal());              // ajusta nombre si difiere
            dto.setEstado(venta.getEstadoVenta());

            // Si guardas descuento/montoPago/cambio en venta, mapea también.
            // Si no existen en entidad, no los inventes; el ticket puede recalcular.
            // Detalles
            java.util.List<DetalleVentaDTO> detalles = new java.util.ArrayList<>();
            for (DetalleVenta det : venta.getDetallesVentas()) {    // ajusta getter real (ej: getDetalleVentas)
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
