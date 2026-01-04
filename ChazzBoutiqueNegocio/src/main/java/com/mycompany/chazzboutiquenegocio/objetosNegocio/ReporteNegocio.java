/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chazzboutiquenegocio.objetosNegocio;

import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IReporteNegocio;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IReporteDAO;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author carli
 */
public class ReporteNegocio implements IReporteNegocio {

    private IReporteDAO reporteDAO;

    public ReporteNegocio(IReporteDAO reporteDAO) {
        this.reporteDAO = reporteDAO;
    }

    @Override
    public List<ReporteVentaDTO> obtenerDatosVentas(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException {
        try {
            return reporteDAO.obtenerDatosVentas(fechaInicio, fechaFin);
        } catch (Exception e) {
            throw new NegocioException("Error al obtener reporte de ventas", e);
        }
    }

    @Override
    public List<ReporteProductoDTO> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException {
        try {
            return reporteDAO.obtenerProductosMasVendidos(fechaInicio, fechaFin);
        } catch (Exception e) {
            throw new NegocioException("Error al obtener productos más vendidos", e);
        }
    }

    @Override
    public List<ReporteCategoriaDTO> obtenerIngresosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException {
        try {
            return reporteDAO.obtenerIngresosPorCategoria(fechaInicio, fechaFin);
        } catch (Exception e) {
            throw new NegocioException("Error al obtener ingresos por categoría", e);
        }
    }

    @Override
    public List<ReporteInventarioDTO> obtenerInventarioActual() throws NegocioException {
        try {
            return reporteDAO.obtenerInventarioActual();
        } catch (Exception e) {
            throw new NegocioException("Error al obtener el inventario actual", e);
        }
    }

}
