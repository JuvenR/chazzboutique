/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.dtoReportes;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author carli
 */
public class ReporteVentaDTO {
    private Long ventaId;
    private LocalDate fecha;
    private BigDecimal total;
    private String vendedor;

    public ReporteVentaDTO() {
    }

    public ReporteVentaDTO(Long ventaId, LocalDate fecha, BigDecimal total, String vendedor) {
        this.ventaId = ventaId;
        this.fecha = fecha;
        this.total = total;
        this.vendedor = vendedor;
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
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

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }
    
}