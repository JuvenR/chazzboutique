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
import java.time.LocalDate;
import java.util.List;


/**
 *
 * @author carli
 */
@Entity
@Table(name = "tblProducto")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombreProducto", nullable = false, length = 100)
    private String nombreProducto;

    @Column(name = "descripcionProducto", length = 300)
    private String descripcionProducto;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDate fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.PERSIST)
    private List<VarianteProducto> variantes;

    public Producto() {
    }

    public Producto(Long id, String nombreProducto, String descripcionProducto, LocalDate fechaCreacion, Proveedor proveedor, Categoria categoria) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.fechaCreacion = fechaCreacion;
        this.proveedor = proveedor;
        this.categoria = categoria;
    }

    public Producto(Long id, String nombreProducto, String descripcionProducto, LocalDate fechaCreacion, Proveedor proveedor, Categoria categoria, List<VarianteProducto> variantes) {
        this.id = id;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.fechaCreacion = fechaCreacion;
        this.proveedor = proveedor;
        this.categoria = categoria;
        this.variantes = variantes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombreProducto;
    }

    public void setNombre(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcionProducto;
    }

    public void setDescripcion(String descripcionProducto) {
        this.descripcionProducto = descripcionProducto;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public List<VarianteProducto> getVariantes() {
        return variantes;
    }

    public void setVariantes(List<VarianteProducto> variantes) {
        this.variantes = variantes;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

}
