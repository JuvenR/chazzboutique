/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.chazzboutiquepersistencia.interfacesDAO;

import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO;
import com.mycompany.chazzboutiquepersistencia.dtos.ReporteVentaResultadoDTO;
import com.mycompany.chazzboutiquepersistencia.dtos.ReporteVentasDTO;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.PersistenceException;

/**
 *
 * @author carli
 */
public interface IReporteDAO {

    List<ReporteVentaDTO> obtenerDatosVentas(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException;

    List<ReporteProductoDTO> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException;

    List<ReporteCategoriaDTO> obtenerIngresosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) throws PersistenceException;

    List<ReporteInventarioDTO> obtenerInventarioActual() throws PersistenceException;
}
