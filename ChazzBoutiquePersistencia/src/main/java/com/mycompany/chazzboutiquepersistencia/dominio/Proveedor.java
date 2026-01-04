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
 * Entidad que representa un proveedor de productos.
 */
@Entity
@Table(name = "tblProveedor")
public class Proveedor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proveedor_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;

    @Column(name = "correo", nullable = false, length = 50)
    private String correo;

    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;

    @Column(name = "fechaCreacion", nullable = false)
    private LocalDate fechaCreacion;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.PERSIST)
    private List<Producto> productos;

    // Constructor vacío
    public Proveedor() {
    }

    // Constructor completo
    public Proveedor(Long id, String nombre, String telefono, String correo, String direccion, LocalDate fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    // Para mostrar en JComboBox u otros componentes visuales
    @Override
    public String toString() {
        return nombre;
    }
}
