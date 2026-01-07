/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.chazzboutiqueapi.ventas;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.chazzboutiquenegocio.dtos.DetalleVentaDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VentaDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVentaNegocio;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TicketService {

    private final IVentaNegocio ventaNegocio;
    private final IVarianteProductoNegocio varianteProductoNegocio;

    public TicketService(IVentaNegocio ventaNegocio,
                         IVarianteProductoNegocio varianteProductoNegocio) {
        this.ventaNegocio = ventaNegocio;
        this.varianteProductoNegocio = varianteProductoNegocio;
    }

    public byte[] generarTicketPdf(Long ventaId) {
        try {
            VentaDTO venta = ventaNegocio.obtenerVentaConDetalles(ventaId);
            if (venta == null) {
                throw new IllegalArgumentException("No existe la venta con id: " + ventaId);
            }
            if (venta.getDetalles() == null) {
                throw new IllegalArgumentException("La venta no tiene detalles: " + ventaId);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Document document = new Document(new Rectangle(227f, 700f));
            final float MARGIN = 15f;

            PdfWriter.getInstance(document, baos);
            document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
            document.open();

            Font headerFont = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);
            Font boldFont   = new Font(Font.FontFamily.COURIER, 10, Font.BOLD);
            Font smallFont  = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL);

            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);

            addImageIfExists(header, "images/chazzLogoBlack.png", 100, 100);

            header.add(new Paragraph("CHAZZ BOUTIQUE", headerFont));
            header.add(new Paragraph("Calle Guillermo Prieto #339, Col. Centro, Los Mochis", normalFont));
            header.add(new Paragraph("Tel: +52 1 668 253 1651 | RFC: CHA220401XYZ", normalFont));
            header.add(new Paragraph("----------------------------------------------", normalFont));
            document.add(header);

            String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
            Paragraph saleInfo = new Paragraph();
            saleInfo.add(new Paragraph("Fecha: " + fechaStr, normalFont));
            saleInfo.add(new Paragraph("Ticket: #" + venta.getId(), normalFont));

            String vendedor = (venta.getUsuarioId() != null) ? ("Usuario #" + venta.getUsuarioId()) : "N/D";
            saleInfo.add(new Paragraph("Vendedor: " + vendedor, normalFont));

            saleInfo.add(new Paragraph("----------------------------------------------", normalFont));
            document.add(saleInfo);

            Paragraph products = new Paragraph();
            products.add(new Paragraph(String.format("%-25s %3s %10s %10s",
                    "ARTÍCULO", "CANT", "P.UNITARIO", "TOTAL"), boldFont));
            products.add(new Paragraph("----------------------------------------------", normalFont));

            for (DetalleVentaDTO detalle : venta.getDetalles()) {
                String codigoVariante = detalle.getCodigoVariante();
                String nombreCorto = (codigoVariante == null) ? "SIN-COD" : codigoVariante;

                try {
                    VarianteProductoDTO variante = null;
                    if (codigoVariante != null && !codigoVariante.isBlank()) {
                        variante = varianteProductoNegocio.obtenerVariantePorCodigoBarra(codigoVariante);
                    }

                    if (nombreCorto.length() > 25) {
                        nombreCorto = nombreCorto.substring(0, 22) + "...";
                    }

                    BigDecimal precioUnitario = safeMoney(detalle.getPrecioUnitario());
                    BigDecimal totalLinea = precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));

                    String linea = String.format("%-25s %3d %10s %10s",
                            nombreCorto,
                            detalle.getCantidad(),
                            formatoMonedaSimple(precioUnitario),
                            formatoMonedaSimple(totalLinea));

                    products.add(new Paragraph(linea, normalFont));

                } catch (NegocioException ignore) {
                    // si falla lookup, igual imprime con lo que trae el detalle
                    BigDecimal precioUnitario = safeMoney(detalle.getPrecioUnitario());
                    BigDecimal totalLinea = precioUnitario.multiply(BigDecimal.valueOf(detalle.getCantidad()));
                    String linea = String.format("%-25s %3d %10s %10s",
                            nombreCorto,
                            detalle.getCantidad(),
                            formatoMonedaSimple(precioUnitario),
                            formatoMonedaSimple(totalLinea));
                    products.add(new Paragraph(linea, normalFont));
                }
            }

            document.add(products);

            
            BigDecimal subtotal = calcularSubtotal(venta);
            BigDecimal descuento = safeMoney(venta.getDescuento());
            BigDecimal total = safeMoney(venta.getTotal());
            BigDecimal pago = safeMoney(venta.getMontoPago());
            BigDecimal cambio = pago.subtract(total);

            Paragraph totals = new Paragraph();
            totals.add(new Paragraph("----------------------------------------------", normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "SUBTOTAL:", formatoMonedaSimple(subtotal)), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "DESCUENTO:", formatoMonedaSimple(descuento)), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "TOTAL:", formatoMonedaSimple(total)), boldFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "PAGO CON:", formatoMonedaSimple(pago)), normalFont));
            totals.add(new Paragraph(String.format("%-15s %15s", "CAMBIO:", formatoMonedaSimple(maxZero(cambio))), normalFont));
            totals.add(new Paragraph("----------------------------------------------\n", normalFont));
            document.add(totals);

            
            Paragraph footer = new Paragraph();
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.add(new Paragraph("¡Gracias por su preferencia!", smallFont));
            footer.add(new Paragraph("Devoluciones en 24 hrs con ticket y etiquetas", smallFont));
            footer.add(new Paragraph("@chazz.boutique", smallFont));

            addImageIfExists(footer, "images/QRreal.png", 70, 70);

            document.add(footer);

            document.close();

            
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando ticket PDF: " + e.getMessage(), e);
        }
    }

    private void addImageIfExists(Paragraph paragraph, String classpathPath, float w, float h) {
        try {
            ClassPathResource res = new ClassPathResource(classpathPath);
            if (!res.exists()) return;

            Image img = Image.getInstance(res.getURL());
            img.scaleToFit(w, h);
            paragraph.add(img);
        } catch (Exception ignored) {
            // si no hay imagen, no truena el ticket
        }
    }

    private BigDecimal calcularSubtotal(VentaDTO venta) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVentaDTO d : venta.getDetalles()) {
            BigDecimal pu = safeMoney(d.getPrecioUnitario());
            subtotal = subtotal.add(pu.multiply(BigDecimal.valueOf(d.getCantidad())));
        }
        return subtotal;
    }

    private BigDecimal safeMoney(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO;
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal maxZero(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO;
        return v.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : v;
    }

    private String formatoMonedaSimple(BigDecimal cantidad) {
        BigDecimal v = (cantidad == null) ? BigDecimal.ZERO : cantidad.setScale(2, RoundingMode.HALF_UP);
        return "$" + v;
    }
}
