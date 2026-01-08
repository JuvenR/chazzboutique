/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.dominio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author carli
 */
@Entity
@Table(name = "tblVenta")
public class Venta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id", nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detallesVentas = new java.util.ArrayList<>();

    @Column(name = "fechaVenta", nullable = false)
    private LocalDate fechaVenta;

    @Column(name = "ventaTotal", nullable = false)
    private BigDecimal ventaTotal;

    @Column(name = "estadoVenta", nullable = false)
    private String estadoVenta;

    @Column(name = "descuento", nullable = false)
    private BigDecimal descuento;

    @Column(name = "montoPago", nullable = false)
    private BigDecimal montoPago;

    @Column(name = "cambio", nullable = false)
    private BigDecimal cambio;

    public Venta() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleVenta> getDetallesVentas() {
        return detallesVentas;
    }

    public void setDetallesVentas(List<DetalleVenta> detallesVentas) {
        this.detallesVentas = detallesVentas;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getVentaTotal() {
        return ventaTotal;
    }

    public void setVentaTotal(BigDecimal ventaTotal) {
        this.ventaTotal = ventaTotal;
    }

    public String getEstadoVenta() {
        return estadoVenta;
    }

    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }

    public void addDetalle(DetalleVenta d) {
        detallesVentas.add(d);
        d.setVenta(this);
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
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

}
