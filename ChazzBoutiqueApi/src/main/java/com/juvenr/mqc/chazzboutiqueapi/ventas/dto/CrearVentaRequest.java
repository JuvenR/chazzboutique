/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.chazzboutiqueapi.ventas.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class CrearVentaRequest {

    @NotNull
    private Long usuarioId;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal montoPago;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal descuento; 

    @NotEmpty
    private List<ItemVentaRequest> detalles;

    public static class ItemVentaRequest {

        @NotBlank
        private String codigoBarras;

        @NotNull
        @Min(1)
        private Integer cantidad;

        public String getCodigoBarras() {
            return codigoBarras;
        }

        public void setCodigoBarras(String codigoBarras) {
            this.codigoBarras = codigoBarras;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public List<ItemVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ItemVentaRequest> detalles) {
        this.detalles = detalles;
    }
}
