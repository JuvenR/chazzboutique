/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquenegocio.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author carli
 */
public class VentaDTO {

    private Long id;
    private Long usuarioId;
    private String vendedorNombre;
    private LocalDate fecha;
    private BigDecimal total;
    private BigDecimal descuento;
    private String estado;
    private List<DetalleVentaDTO> detalles;
    private BigDecimal montoPago;
    private BigDecimal cambio;

    // Constructores
    public VentaDTO() {
    }

    public VentaDTO(Long usuarioId, BigDecimal total, List<DetalleVentaDTO> detalles) {
        this.usuarioId = usuarioId;
        this.total = total;
        this.detalles = detalles;
        this.estado = "COMPLETADA"; // Valor por defecto
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getMontoPago() {
        return montoPago;
    }

    public void setMontoPago(BigDecimal montoPago) {
        this.montoPago = montoPago;
    }

    public BigDecimal getCambio() {
        return cambio;
    }

    public void setCambio(BigDecimal cambio) {
        this.cambio = cambio;
    }

    @Override
    public String toString() {
        return "VentaDTO{" + "id=" + id + ", usuarioId=" + usuarioId + ", fecha=" + fecha + ", total=" + total + ", descuento=" + descuento + ", estado=" + estado + ", detalles=" + detalles + '}';
    }

}
