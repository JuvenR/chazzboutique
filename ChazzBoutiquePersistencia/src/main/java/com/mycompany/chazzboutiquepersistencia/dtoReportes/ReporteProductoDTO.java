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
public class ReporteProductoDTO {

    private String nombreProducto;
    private Long cantidadVendida;
    private BigDecimal totalVendido;
    private String categoria;

    public ReporteProductoDTO(String nombreProducto, Long cantidadVendida,
            BigDecimal totalVendido, String categoria) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.totalVendido = totalVendido;
        this.categoria = categoria;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(BigDecimal totalVendido) {
        this.totalVendido = totalVendido;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

}
