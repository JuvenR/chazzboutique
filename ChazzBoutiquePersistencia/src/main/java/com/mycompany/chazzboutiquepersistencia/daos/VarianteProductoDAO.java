package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dominio.VarianteProducto;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVarianteProductoDAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class VarianteProductoDAO implements IVarianteProductoDAO {

    private final IConexionBD conexionBD;

    public VarianteProductoDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public VarianteProducto crearVariante(VarianteProducto variante) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(variante);
            tx.commit();
            return variante;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al crear la variante de producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public VarianteProducto buscarPorId(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            VarianteProducto v = em.find(VarianteProducto.class, id);
            return v;
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar variante por ID", e);
        } finally {
            em.close();
        }
    }

    @Override
    public VarianteProducto obtenerPorCodigoBarra(String codigoBarra) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            TypedQuery<VarianteProducto> query = em.createQuery(
                    "SELECT v FROM VarianteProducto v "
                    + "JOIN FETCH v.producto p "
                    + "WHERE v.codigoBarra = :cb AND v.eliminado = false",
                    VarianteProducto.class
            );
            query.setParameter("cb", codigoBarra);
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
            return null; // mejor que truene, así tu negocio decide qué hacer
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener variante por código de barras", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<VarianteProducto> obtenerVariantesPorProducto(Long productoId) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            TypedQuery<VarianteProducto> query = em.createQuery(
                    "SELECT v FROM VarianteProducto v "
                    + "WHERE v.producto.id = :pid AND v.eliminado = false",
                    VarianteProducto.class
            );
            query.setParameter("pid", productoId);
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al obtener variantes por producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public VarianteProducto actualizarVarianteProducto(VarianteProducto variante) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            VarianteProducto updated = em.merge(variante);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al actualizar la variante de producto", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminarVarianteProducto(Long id) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            VarianteProducto v = em.find(VarianteProducto.class, id);
            if (v != null) {
                v.setEliminado(true);
                em.merge(v);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new PersistenciaException("Error al hacer soft delete de la variante", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<VarianteProducto> buscarVariantesPorNombreProducto(String terminoBusqueda, int pagina, int tamañoPagina) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            // Limpiar el filtro: minúsculas y espacios normales
            String filtro = terminoBusqueda.toLowerCase().trim().replaceAll("\\s+", " ");

            TypedQuery<VarianteProducto> query = em.createQuery(
                    "SELECT v FROM VarianteProducto v "
                    + "WHERE v.eliminado = false AND ("
                    + "LOWER(v.producto.nombreProducto) LIKE :busqueda OR "
                    + "LOWER(v.color) LIKE :busqueda OR "
                    + "LOWER(v.talla) LIKE :busqueda"
                    + ")",
                    VarianteProducto.class
            );

            query.setParameter("busqueda", "%" + filtro + "%");
            query.setFirstResult((pagina - 1) * tamañoPagina);
            query.setMaxResults(tamañoPagina);

            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar variantes con paginación", e);
        } finally {
            em.close();
        }
    }

    public long contarVariantesPorNombreProducto(String terminoBusqueda) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String filtro = terminoBusqueda.toLowerCase().trim().replaceAll("\\s+", " ");

            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(v) FROM VarianteProducto v "
                    + "WHERE v.eliminado = false AND ("
                    + "LOWER(v.producto.nombreProducto) LIKE :busqueda OR "
                    + "LOWER(v.color) LIKE :busqueda OR "
                    + "LOWER(v.talla) LIKE :busqueda)",
                    Long.class
            );
            query.setParameter("busqueda", "%" + filtro + "%");

            return query.getSingleResult();
        } catch (Exception e) {
            throw new PersistenciaException("Error al contar variantes", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<VarianteProducto> buscarVariantesPorCategoriaYNombreProducto(int idCategoria, String nombre, int pagina, int tamañoPagina) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();

        try {
            return em.createQuery("""
            SELECT v FROM VarianteProducto v
            WHERE v.producto.categoria.id = :idCategoria
            AND v.eliminado = false
            AND LOWER(v.producto.nombreProducto) LIKE :nombre
        """, VarianteProducto.class)
                    .setParameter("idCategoria", (long) idCategoria)
                    .setParameter("nombre", "%" + nombre.toLowerCase() + "%")
                    .setFirstResult((pagina - 1) * tamañoPagina)
                    .setMaxResults(tamañoPagina)
                    .getResultList();
        } catch (Exception e) {
            throw new PersistenciaException("Error al buscar variantes por categoría y nombre", e);
        }
    }

    @Override
    public long contarVariantesPorCategoriaYNombreProducto(int idCategoria, String nombre) throws PersistenciaException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            return em.createQuery("""
            SELECT COUNT(v) FROM VarianteProducto v
            WHERE v.producto.categoria.id = :idCategoria
            AND v.eliminado = false
            AND LOWER(v.producto.nombreProducto) LIKE :nombre
        """, Long.class)
                    .setParameter("idCategoria", (long) idCategoria)
                    .setParameter("nombre", "%" + nombre.toLowerCase() + "%")
                    .getSingleResult();
        } catch (Exception e) {
            throw new PersistenciaException("Error al contar variantes por categoría y nombre", e);
        }
    }

}
