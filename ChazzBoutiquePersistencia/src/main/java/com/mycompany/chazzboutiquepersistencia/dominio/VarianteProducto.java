/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 *
 * @author carli
 */
@Entity
@Table(name = "tblVarianteProducto")
public class VarianteProducto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "varianteproducto_id", nullable = false, unique = true)
    private Long id;

    @OneToMany(mappedBy = "varianteProducto", cascade = CascadeType.PERSIST)
    private List<DetalleVenta> detallesVentas;

    @Column(name = "codigoBarra", nullable = false, length = 50)
    private String codigoBarra;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "precioCompra", nullable = false)
    private BigDecimal precioCompra;

    @Column(name = "talla", nullable = false)
    private String talla;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "precioVenta", nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;
    
    @Column(name = "urlImagen", length = 255)
    private String urlImagen;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    public VarianteProducto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public List<DetalleVenta> getDetallesVentas() {
        return detallesVentas;
    }

    public void setDetallesVentas(List<DetalleVenta> detallesVentas) {
        this.detallesVentas = detallesVentas;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    @Override
    public String toString() {
        return "VarianteProducto{" + "id=" + id + ", detallesVentas=" + detallesVentas + ", codigoBarra=" + codigoBarra + ", stock=" + stock + ", precioCompra=" + precioCompra + ", talla=" + talla + ", color=" + color + ", precioVenta=" + precioVenta + ", producto=" + producto + '}';
    }

}
