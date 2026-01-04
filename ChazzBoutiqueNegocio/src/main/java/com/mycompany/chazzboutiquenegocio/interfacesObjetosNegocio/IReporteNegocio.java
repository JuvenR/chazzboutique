/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio;

import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteCategoriaDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteInventarioDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteProductoDTO;
import com.mycompany.chazzboutiquepersistencia.dtoReportes.ReporteVentaDTO;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author carli
 */
public interface IReporteNegocio {

    List<ReporteVentaDTO> obtenerDatosVentas(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException;

    List<ReporteProductoDTO> obtenerProductosMasVendidos(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException;

    List<ReporteCategoriaDTO> obtenerIngresosPorCategoria(LocalDate fechaInicio, LocalDate fechaFin) throws NegocioException;

    List<ReporteInventarioDTO> obtenerInventarioActual() throws NegocioException;
}
