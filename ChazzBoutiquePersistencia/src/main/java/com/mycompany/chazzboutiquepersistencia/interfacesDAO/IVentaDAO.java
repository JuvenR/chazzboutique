/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.interfacesDAO;

import com.mycompany.chazzboutiquepersistencia.dominio.Venta;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;

/**
 *
 * @author carli
 */
public interface IVentaDAO {

    public Venta registrarVenta(Venta venta) throws PersistenciaException;

    Venta buscarPorId(Long id) throws PersistenciaException;

}
