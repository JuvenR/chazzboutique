/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Venta;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVentaDAO;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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

    @Override
    public Venta buscarPorId(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            return em.createQuery(
                    "SELECT v FROM Venta v "
                    + "LEFT JOIN FETCH v.detallesVentas d "
                    + // Venta.detallesVentas
                    "LEFT JOIN FETCH d.varianteProducto vp "
                    + // DetalleVenta.varianteProducto
                    "LEFT JOIN FETCH vp.producto p "
                    + // VarianteProducto.producto
                    "LEFT JOIN FETCH v.usuario u "
                    + // Venta.usuario
                    "WHERE v.id = :id", Venta.class
            ).setParameter("id", id)
                    .getSingleResult();

        } catch (javax.persistence.NoResultException e) {
            return null;
        } catch (Exception e) {
            throw new PersistenciaException("Error buscando venta por id: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

}
