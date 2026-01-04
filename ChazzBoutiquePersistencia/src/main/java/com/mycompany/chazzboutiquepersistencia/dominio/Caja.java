/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 *
 * @author carli
 */
@Entity
@Table(name = "tblCaja")
public class Caja implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caja_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "horaApertura", nullable = false)
    private LocalDateTime horaApertura;
    
    @Column(name = "horaCierre", nullable = false)
    private LocalDateTime horaCierre;
    
    @Column(name = "montoInicial", nullable = false)
    private LocalDateTime montoInicial;

    @Column(name = "estado", nullable = false)
    private String estado;

    public Caja() {
    }


}
