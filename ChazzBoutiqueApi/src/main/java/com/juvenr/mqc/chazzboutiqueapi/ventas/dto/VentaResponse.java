/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.chazzboutiqueapi.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaResponse {

    private Long id;
    private LocalDate fecha;
    private String estado;
    private Long usuarioId;

    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal total;
    private BigDecimal montoPago;
    private BigDecimal cambio;

    private List<VentaDetalleResponse> detalles;
    private TicketInfo ticket;

    public static class TicketInfo {

        private String pdfUrl;

        public TicketInfo(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }

        public String getPdfUrl() {
            return pdfUrl;
        }

        public void setPdfUrl(String pdfUrl) {
            this.pdfUrl = pdfUrl;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public List<VentaDetalleResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalleResponse> detalles) {
        this.detalles = detalles;
    }

    public TicketInfo getTicket() {
        return ticket;
    }

    public void setTicket(TicketInfo ticket) {
        this.ticket = ticket;
    }

}
