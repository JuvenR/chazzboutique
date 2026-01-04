/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.daos;

import com.mycompany.chazzboutiquepersistencia.conexion.IConexionBD;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IReporteDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 *
 * @author carli
 */
public class ReporteDAO implements IReporteDAO {

    IConexionBD conexionBD;

    public ReporteDAO(IConexionBD conexionBD) {
        this.conexionBD = conexionBD;
    }

    @Override
    public List<ReporteVentaDTO> obtenerDatosVentas(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String jpql = "SELECT new com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO(v.id, v.fechaVenta, v.ventaTotal, u.nombreUsuario) "
                    + "FROM Venta v JOIN v.usuario u "
                    + "WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin "
                    + "ORDER BY v.fechaVenta DESC";

            TypedQuery<ReporteVentaDTO> query = em.createQuery(jpql, ReporteVentaDTO.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            return query.getResultList();

        } catch (NoResultException | NonUniqueResultException | IllegalArgumentException e) {
            throw new PersistenceException("Error en la consulta de ventas", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReporteProductoDTO> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String jpql = "SELECT new com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO(p.nombreProducto, SUM(dv.cantidad), "
                    + "CAST(SUM(dv.precioUnitario * dv.cantidad) AS DECIMAL(10,2)), c.nombreCategoria) "
                    + "FROM DetalleVenta dv "
                    + "JOIN dv.varianteProducto vp "
                    + "JOIN vp.producto p "
                    + "JOIN p.categoria c "
                    + "JOIN dv.venta v "
                    + "WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin "
                    + "GROUP BY p.nombreProducto, c.nombreCategoria "
                    + "ORDER BY SUM(dv.cantidad) DESC";

            TypedQuery<ReporteProductoDTO> query = em.createQuery(jpql, ReporteProductoDTO.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Error al obtener productos más vendidos", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReporteCategoriaDTO> obtenerIngresosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            // Calcular el total de ventas en el periodo
            BigDecimal totalVentas = em.createQuery(
                    "SELECT SUM(v.ventaTotal) FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin",
                    BigDecimal.class)
                    .setParameter("fechaInicio", fechaInicio)
                    .setParameter("fechaFin", fechaFin)
                    .getSingleResult();

            // Si no hay ventas, establecer totalVentas como 1 para evitar división por cero
            if (totalVentas == null || totalVentas.compareTo(BigDecimal.ZERO) == 0) {
                totalVentas = BigDecimal.ONE;
            }

            // Consulta JPQL corregida
            String jpql = "SELECT new com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO("
                    + "c.nombreCategoria, "
                    + "COUNT(DISTINCT v.id), "
                    + "SUM(d.precioUnitario * d.cantidad), "
                    + "(SUM(d.precioUnitario * d.cantidad) / :totalVentas) * 100) "
                    + "FROM Venta v "
                    + "JOIN v.detallesVentas d "
                    + "JOIN d.varianteProducto vp "
                    + "JOIN vp.producto p "
                    + "JOIN p.categoria c "
                    + "WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin "
                    + "GROUP BY c.nombreCategoria "
                    + "ORDER BY SUM(d.precioUnitario * d.cantidad) DESC";

            TypedQuery<ReporteCategoriaDTO> query = em.createQuery(jpql, ReporteCategoriaDTO.class);
            query.setParameter("fechaInicio", fechaInicio);
            query.setParameter("fechaFin", fechaFin);
            query.setParameter("totalVentas", totalVentas);

            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Error al obtener ingresos por categoría", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ReporteInventarioDTO> obtenerInventarioActual() throws PersistenceException {
        EntityManager em = conexionBD.getEntityManager();
        try {
            String jpql = "SELECT new com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO(p.nombreProducto, vp.talla, vp.color, "
                    + "vp.stock, vp.precioVenta, vp.precioVenta * vp.stock) "
                    + "FROM VarianteProducto vp "
                    + "JOIN vp.producto p "
                    + "WHERE vp.stock > 0 "
                    + "ORDER BY p.nombreProducto, p.descripcionProducto";

            TypedQuery<ReporteInventarioDTO> query = em.createQuery(jpql, ReporteInventarioDTO.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new PersistenceException("Error al obtener el inventario actual", e);
        } finally {
            em.close();
        }
    }

}
