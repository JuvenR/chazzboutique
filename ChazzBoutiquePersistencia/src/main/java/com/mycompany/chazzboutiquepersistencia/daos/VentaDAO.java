/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Venta;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVentaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author carli
 */
public class VentaDAO implements IVentaDAO {

    IConexionBD conexionBD;

    public VentaDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Venta registrarVenta(Venta venta) throws PersistenciaException {
        EntityManager entityManager = conexionBD.getEntityManager();
        EntityTransaction transaccion = null;
        System.out.println(venta);
        try {
            transaccion = entityManager.getTransaction();
            transaccion.begin();
            entityManager.persist(venta);
            transaccion.commit();
            return venta;
        } catch (Exception e) {
            Logger.getLogger(VentaDAO.class.getName()).log(Level.SEVERE, null, e);
            if (transaccion != null && transaccion.isActive()) {
                transaccion.rollback();
            }
            throw new PersistenciaException("Error al registrar venta jaja", e);

        } finally {
            entityManager.close();
        }
    }

}
