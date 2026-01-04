/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.dtoReportes;

import java.math.BigDecimal;

/**
 *
 * @author carli
 */
public class ReporteCategoriaDTO {
     private String nombreCategoria;
    private Long ventasTotales;
    private BigDecimal ingresos;
    private BigDecimal porcentaje;

    public ReporteCategoriaDTO(String nombreCategoria, Long ventasTotales, 
                              BigDecimal ingresos, BigDecimal porcentaje) {
        this.nombreCategoria = nombreCategoria;
        this.ventasTotales = ventasTotales;
        this.ingresos = ingresos;
        this.porcentaje = porcentaje;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public Long getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(Long ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
    
    
}
