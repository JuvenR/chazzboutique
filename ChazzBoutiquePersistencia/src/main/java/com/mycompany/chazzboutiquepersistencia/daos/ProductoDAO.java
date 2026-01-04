/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.Producto;
import com.mycompany.chazzboutiquepersistencia.dtos.FiltroProductoDTO;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IProductoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author carli
 */
public class ProductoDAO implements IProductoDAO {

    IConexionBD conexionBD;

    public ProductoDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public Producto buscarPorId(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            return em.find(Producto.class, id);
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar producto por ID", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> buscarPorNombre(String nombre) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p WHERE LOWER(p.nombreProducto) LIKE LOWER(:nombreBusqueda)";

            return em.createQuery(jpql, Producto.class)
                    .setParameter("nombreBusqueda", "%" + nombre + "%")
                    .getResultList();

        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar productos por nombre", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> obtenerTodosProductos() throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            TypedQuery<Producto> query = em.createQuery("SELECT p FROM Producto p", Producto.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener todos los productos", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void crearProducto(Producto producto) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(producto);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al crear el producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void actualizarProducto(Producto producto) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(producto);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al actualizar el producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarProducto(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Producto ref = em.getReference(Producto.class, id);
            em.remove(ref);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al eliminar el producto", e);
        } finally {
            em.close();
        }
    }

    public List<Producto> buscarPorFiltro(FiltroProductoDTO filtro) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            // Construcción dinámica del JPQL
            StringBuilder jpql = new StringBuilder("SELECT p FROM Producto p WHERE p.eliminado = false");
            Map<String, Object> params = new HashMap<>();

            if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
                jpql.append(" AND LOWER(p.nombreProducto) LIKE :nombre");
                params.put("nombre", "%" + filtro.getNombre().toLowerCase() + "%");
            }
            if (filtro.getPrecioMin() != null) {
                jpql.append(" AND p.precio >= :precioMin");
                params.put("precioMin", filtro.getPrecioMin());
            }
            if (filtro.getPrecioMax() != null) {
                jpql.append(" AND p.precio <= :precioMax");
                params.put("precioMax", filtro.getPrecioMax());
            }
            if (filtro.getCategoriaId() != null) {
                jpql.append(" AND p.categoria.id = :catId");
                params.put("catId", filtro.getCategoriaId());
            }

            jpql.append(" ORDER BY p.id ASC");

            TypedQuery<Producto> query = em.createQuery(jpql.toString(), Producto.class);
            params.forEach(query::setParameter);

            // Paginación (convertimos a 0‑based)
            int pagina = Math.max(filtro.getPagina(), 1);
            int tamanio = Math.max(filtro.getTamanio(), 1);
            query.setFirstResult((pagina - 1) * tamanio);
            query.setMaxResults(tamanio);

            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenciaException(
                    String.format("Error al buscar productos con filtro %s", filtro), e
            );
        } finally {
            em.close();
        }
    }

    @Override
    public List<Producto> obtenerPorCategoria(Long categoriaId) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String jpql = "SELECT p FROM Producto p "
                    + "WHERE p.categoria.id = :catId "
                    + "  AND p.eliminado = false "
                    + "ORDER BY p.id ASC";
            return em.createQuery(jpql, Producto.class)
                    .setParameter("catId", categoriaId)
                    .getResultList();
        } catch (Exception e) {
            throw new PersistenciaException(
                    String.format("Error al obtener productos de la categoría %d", categoriaId), e
            );
        } finally {
            em.close();
        }
    }

}
