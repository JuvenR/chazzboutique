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
public class ReporteInventarioDTO {

    private String nombreProducto;
    private String talla;
    private String color;
    private Integer stock;
    private BigDecimal precioUnitario;
    private BigDecimal valorTotal;

    public ReporteInventarioDTO(String nombreProducto, String talla, String color, Integer stock,
            BigDecimal precioUnitario, BigDecimal valorTotal) {
        this.nombreProducto = nombreProducto;
        this.talla = talla;
        this.color= color;
        this.stock = stock;
        this.precioUnitario = precioUnitario;
        this.valorTotal = valorTotal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

   
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

}
