/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.DetalleVenta;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IDetalleVentaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;


/**
 *
 * @author carli
 */
public class DetalleVentaDAO implements IDetalleVentaDAO{

    IConexionBD conexionBD;

    public DetalleVentaDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public DetalleVenta registrarDetalle(DetalleVenta detalle) throws PersistenciaException {
        EntityManager entityManager = conexionBD.getEntityManager();
        EntityTransaction transaccion = null;
        try {
            transaccion = entityManager.getTransaction();
            transaccion.begin();
            entityManager.persist(detalle);
            transaccion.commit();
            return detalle;
        } catch (Exception e) {
            if (transaccion != null && transaccion.isActive()) {
                transaccion.rollback();
            }
            throw new PersistenciaException("Error al registrar detalle de venta", e);
        } finally {
            entityManager.close();
        }
    }

}
