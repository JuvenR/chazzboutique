package com.juvenr.mqc.chazzboutiqueapi.ventas;

import com.juvenr.mqc.chazzboutiqueapi.shared.errors.BusinessException;
import com.juvenr.mqc.chazzboutiqueapi.ventas.dto.CrearVentaRequest;
import com.juvenr.mqc.chazzboutiqueapi.ventas.dto.VentaDetalleResponse;
import com.juvenr.mqc.chazzboutiqueapi.ventas.dto.VentaResponse;
import com.mycompany.chazzboutiquenegocio.dtos.DetalleVentaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VentaDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVentaNegocio;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    private final IVentaNegocio ventaNegocio;
    private final IVarianteProductoNegocio varianteNegocio;

    public VentaService(IVentaNegocio ventaNegocio, IVarianteProductoNegocio varianteNegocio) {
        this.ventaNegocio = ventaNegocio;
        this.varianteNegocio = varianteNegocio;
    }

    public VentaResponse crearVenta(CrearVentaRequest req) {
        try {
            validarRequest(req);

            VentaDTO venta = new VentaDTO();
            venta.setUsuarioId(req.getUsuarioId());
            venta.setFecha(LocalDate.now());
            venta.setEstado("COMPLETADA");

            BigDecimal subtotal = BigDecimal.ZERO;
            List<DetalleVentaDTO> detalles = new ArrayList<>();

            for (CrearVentaRequest.ItemVentaRequest item : req.getDetalles()) {
                String codigo = item.getCodigoBarras().trim();
                int cantidad = item.getCantidad();

                VarianteProductoDTO variante;
                try {
                    variante = varianteNegocio.obtenerVariantePorCodigoBarra(codigo);
                } catch (NegocioException ex) {
                    throw new BusinessException("VARIANTE_ERROR", ex.getMessage());
                }

                if (variante == null) {
                    throw new BusinessException("VARIANTE_NO_EXISTE", "No existe el producto con código: " + codigo);
                }
                if (cantidad <= 0) {
                    throw new BusinessException("CANTIDAD_INVALIDA", "Cantidad inválida para: " + codigo);
                }
                if (variante.getStock() < cantidad) {
                    throw new BusinessException("STOCK_INSUFICIENTE",
                            "Stock insuficiente para " + codigo + ". Disponible: " + variante.getStock());
                }

                BigDecimal precioUnitario = n2(variante.getPrecioVenta());
                BigDecimal sub = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

                DetalleVentaDTO d = new DetalleVentaDTO();
                d.setCodigoVariante(codigo);
                d.setCantidad(cantidad);
                d.setPrecioUnitario(precioUnitario);

                detalles.add(d);
                subtotal = subtotal.add(sub);
            }

            BigDecimal descuento = n2(req.getDescuento());
            if (descuento.compareTo(BigDecimal.ZERO) < 0) descuento = BigDecimal.ZERO;
            if (descuento.compareTo(subtotal) > 0) {
                throw new BusinessException("DESCUENTO_INVALIDO", "El descuento no puede ser mayor al subtotal");
            }

            BigDecimal total = subtotal.subtract(descuento);
            BigDecimal montoPago = n2(req.getMontoPago());

            if (montoPago.compareTo(total) < 0) {
                throw new BusinessException("PAGO_INSUFICIENTE",
                        "Pago insuficiente. Falta: " + n2(total.subtract(montoPago)));
            }

            venta.setDetalles(detalles);
            venta.setDescuento(descuento);
            venta.setTotal(total);
            venta.setMontoPago(montoPago);
            venta.setCambio(montoPago.subtract(total));

            VentaDTO registrada = ventaNegocio.registrarVenta(venta);

            VentaResponse res = new VentaResponse();
            res.setId(registrada.getId());
            res.setFecha(LocalDate.now());
            res.setEstado("COMPLETADA");
            res.setUsuarioId(req.getUsuarioId());

            res.setSubtotal(subtotal);
            res.setDescuento(descuento);
            res.setTotal(total);
            res.setMontoPago(montoPago);
            res.setCambio(montoPago.subtract(total));

            res.setDetalles(mapDetallesResponse(registrada.getDetalles()));

            String ticketUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/ventas/{id}/ticket")
                    .buildAndExpand(registrada.getId())
                    .toUriString();

            res.setTicket(new VentaResponse.TicketInfo(ticketUrl));

            return res;

        } catch (BusinessException e) {
            throw e;
        } catch (NegocioException e) {
            throw new BusinessException("VENTA_ERROR", e.getMessage());
        }
    }

    public Object cotizar(CrearVentaRequest req) {
        validarRequest(req);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<Object> detalles = new ArrayList<>();

        for (CrearVentaRequest.ItemVentaRequest item : req.getDetalles()) {
            String codigo = item.getCodigoBarras().trim();

            VarianteProductoDTO variante;
            try {
                variante = varianteNegocio.obtenerVariantePorCodigoBarra(codigo);
            } catch (NegocioException ex) {
                throw new BusinessException("VARIANTE_ERROR", ex.getMessage());
            }

            if (variante == null) {
                throw new BusinessException("VARIANTE_NO_EXISTE", "No existe el producto con código: " + codigo);
            }
            if (item.getCantidad() <= 0) {
                throw new BusinessException("CANTIDAD_INVALIDA", "Cantidad inválida para: " + codigo);
            }
            if (variante.getStock() < item.getCantidad()) {
                throw new BusinessException("STOCK_INSUFICIENTE",
                        "Stock insuficiente para " + codigo + ". Disponible: " + variante.getStock());
            }

            BigDecimal precio = n2(variante.getPrecioVenta());
            BigDecimal sub = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotal = subtotal.add(sub);

            detalles.add(new Object()); 
        }

        BigDecimal descuento = n2(req.getDescuento());
        if (descuento.compareTo(subtotal) > 0) {
            throw new BusinessException("DESCUENTO_INVALIDO", "El descuento no puede ser mayor al subtotal");
        }

        BigDecimal total = subtotal.subtract(descuento);

        return java.util.Map.of(
                "subtotal", subtotal,
                "descuento", descuento,
                "total", total,
                "detalles", detalles
        );
    }

    private void validarRequest(CrearVentaRequest req) {
        if (req == null) throw new BusinessException("REQ_NULL", "Request vacío");
        if (req.getUsuarioId() == null) throw new BusinessException("USUARIO_REQUERIDO", "usuarioId requerido");
        if (req.getDetalles() == null || req.getDetalles().isEmpty()) {
            throw new BusinessException("VENTA_VACIA", "Debe agregar productos a la venta");
        }
        for (CrearVentaRequest.ItemVentaRequest item : req.getDetalles()) {
            if (item.getCodigoBarras() == null || item.getCodigoBarras().trim().isEmpty()) {
                throw new BusinessException("CODIGO_REQUERIDO", "codigoBarras requerido");
            }
        }
    }

    private BigDecimal n2(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private List<VentaDetalleResponse> mapDetallesResponse(List<DetalleVentaDTO> detallesNegocio) {
        if (detallesNegocio == null) return List.of();

        List<VentaDetalleResponse> out = new ArrayList<>();
        for (DetalleVentaDTO d : detallesNegocio) {
            VentaDetalleResponse r = new VentaDetalleResponse();
            r.setCodigoBarras(d.getCodigoVariante());
            r.setCantidad(d.getCantidad());
            r.setPrecioUnitario(d.getPrecioUnitario());
            r.setImporte(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            out.add(r);
        }
        return out;
    }
}
