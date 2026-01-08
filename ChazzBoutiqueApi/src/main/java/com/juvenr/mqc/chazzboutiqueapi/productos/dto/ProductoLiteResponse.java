/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.chazzboutiqueapi.productos.dto;

/**
 *
 * @author carli
 */
public class ProductoLiteResponse {

    private Long id;
    private String nombreProducto;

    public ProductoLiteResponse() {
    }

    public ProductoLiteResponse(Long id, String nombreProducto) {
        this.id = id;
        this.nombreProducto = nombreProducto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
}
