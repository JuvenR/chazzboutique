package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Proveedor;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IProveedorDAO;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProveedorDAO implements IProveedorDAO {

    private final IConexionBD conexionBD;

    public ProveedorDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }
  
    public Proveedor buscarPorId(Long id) throws PersistenciaException {
    EntityManager em = conexionBD.getEntityManager();
    try {
        return em.find(Proveedor.class, id);
    } catch (Exception e) {
        throw new PersistenciaException("Error al buscar proveedor por ID", e);
    } finally {
        em.close();
    }
}

    @Override
    public List<Proveedor> obtenerTodos() throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Proveedor p", Proveedor.class).getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener proveedores", e);
        } finally {
            em.close();
        }
    }
}
