/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Categoria;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.ICategoriaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;


/**
 *
 * @author carli
 */
public class CategoriaDAO implements ICategoriaDAO {

    private final IConexionBD conexionBD;

    public CategoriaDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public void crearCategoria(Categoria categoria) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al crear la categoría", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Categoria buscarPorId(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria == null) {
                throw new PersistenciaException("La categoría con ID " + id + " no existe.");
            }
            return categoria;
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar categoría por ID", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Categoria> obtenerTodasCategorias() throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            TypedQuery<Categoria> query = em.createQuery(
                    "SELECT c FROM Categoria c WHERE c.eliminado = false ORDER BY c.id ASC",
                    Categoria.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener todas las categorías", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Categoria actualizarCategoria(Categoria categoria) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria updated = em.merge(categoria);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al actualizar la categoría", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarCategoria(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria == null) {
                throw new PersistenciaException("La categoría con ID " + id + " no existe.");
            }
            em.remove(categoria);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al eliminar la categoría", e);
        } finally {
            em.close();
        }
    }

}
