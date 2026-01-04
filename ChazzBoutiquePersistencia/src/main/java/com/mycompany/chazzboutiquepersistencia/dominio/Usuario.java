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
@Table(name = "tblUsuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long id;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.PERSIST)
    private List<Venta> ventas;
    
    @Column(name = "nombreUsuario", nullable = false, length = 50)
    private String nombreUsuario;

    @Column(name = "contraseña", nullable = false, length = 50)
    private String contraseña;

    @Column(name = "rol", nullable = false, length = 50)
    private String rol;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDate fechaCreacion;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String contraseña, String rol, Boolean activo, LocalDate fechaCreacion) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.rol = rol;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
