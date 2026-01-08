package com.mycompany.chazzboutiquenegocio.objetosNegocio;

import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import com.mycompany.chazzboutiquepersistencia.dominio.Producto;
import com.mycompany.chazzboutiquepersistencia.dominio.VarianteProducto;
import com.mycompany.chazzboutiquepersistencia.excepciones.PersistenciaException;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IProductoDAO;
import com.mycompany.chazzboutiquepersistencia.interfacesDAO.IVarianteProductoDAO;

import java.util.ArrayList;
import java.util.List;

public class VarianteProductoNegocio implements IVarianteProductoNegocio {

    private final IVarianteProductoDAO varianteProductoDAO;
    private final IProductoDAO productoDAO;

    public VarianteProductoNegocio(IVarianteProductoDAO varianteProductoDAO, IProductoDAO productoDAO) {
        this.varianteProductoDAO = varianteProductoDAO;
        this.productoDAO = productoDAO;
    }

    @Override
    public VarianteProductoDTO obtenerVariantePorCodigoBarra(String codigoBarra) throws NegocioException {
        try {
            VarianteProducto variante = varianteProductoDAO.obtenerPorCodigoBarra(codigoBarra);
            return (variante != null) ? convertirA_DTO(variante) : null;
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al obtener la variante por código de barra: " + codigoBarra, e);
        }
    }

    @Override
    public List<VarianteProductoDTO> obtenerVariantesPorProducto(Long productoId) throws NegocioException {
        try {
            List<VarianteProducto> variantes = varianteProductoDAO.obtenerVariantesPorProducto(productoId);
            List<VarianteProductoDTO> resultado = new ArrayList<>();
            for (VarianteProducto variante : variantes) {
                resultado.add(convertirA_DTO(variante));
            }
            return resultado;
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al obtener variantes para el producto ID: " + productoId, e);
        }
    }

    @Override
    public void crearVariante(VarianteProductoDTO dto) throws NegocioException {
        try {
            Producto producto = productoDAO.buscarPorId(dto.getProductoId());
            if (producto == null) {
                throw new NegocioException("Producto no encontrado con ID: " + dto.getProductoId());
            }

            VarianteProducto variante = new VarianteProducto();
            llenarEntidadDesdeDTO(variante, dto);
            variante.setProducto(producto);
            variante.setEliminado(false);

            varianteProductoDAO.crearVariante(variante);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al crear variante", e);
        }
    }

    @Override
    public void actualizarVariante(VarianteProductoDTO dto) throws NegocioException {
        try {
            VarianteProducto variante = varianteProductoDAO.buscarPorId(dto.getId());
            if (variante == null) {
                throw new NegocioException("Variante no encontrada");
            }

            llenarEntidadDesdeDTO(variante, dto);
            varianteProductoDAO.actualizarVarianteProducto(variante);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al actualizar variante", e);
        }
    }

    @Override
    public void eliminarVariante(Long id) throws NegocioException {
        try {
            VarianteProducto variante = varianteProductoDAO.buscarPorId(id);
            if (variante == null) {
                throw new NegocioException("Variante no encontrada");
            }

            varianteProductoDAO.eliminarVarianteProducto(id);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al eliminar variante", e);
        }
    }

    @Override
    public VarianteProductoDTO buscarPorId(Long id) throws NegocioException {
        try {
            VarianteProducto variante = varianteProductoDAO.buscarPorId(id);
            if (variante == null || variante.isEliminado()) {
                throw new NegocioException("Variante no encontrada");
            }
            return convertirA_DTO(variante);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al buscar variante por ID", e);
        }
    }

    private VarianteProductoDTO convertirA_DTO(VarianteProducto variante) {
        VarianteProductoDTO dto = new VarianteProductoDTO(
                variante.getCodigoBarra(),
                variante.getStock(),
                variante.getPrecioCompra(),
                variante.getTalla(),
                variante.getColor(),
                variante.getPrecioVenta(),
                variante.getProducto().getId(),
                variante.getUrlImagen()
        );
        dto.setId(variante.getId());
        dto.setNombreProducto(variante.getProducto() != null ? variante.getProducto().getNombre() : null);
        
        return dto;
    }

    private void llenarEntidadDesdeDTO(VarianteProducto entidad, VarianteProductoDTO dto) {
        System.out.println(dto);
        entidad.setCodigoBarra(dto.getCodigoBarra());
        entidad.setStock(dto.getStock());
        entidad.setPrecioCompra(dto.getPrecioCompra());
        entidad.setPrecioVenta(dto.getPrecioVenta());
        entidad.setTalla(dto.getTalla());
        entidad.setColor(dto.getColor());
        entidad.setUrlImagen(dto.getUrlImagen());
    }

    @Override
    public List<VarianteProductoDTO> buscarVariantesPorNombreProducto(String terminoBusqueda, int pagina, int tamanoPagina) throws NegocioException {
        try {
            List<VarianteProducto> variantes = varianteProductoDAO.buscarVariantesPorNombreProducto(terminoBusqueda, pagina, tamanoPagina);
            List<VarianteProductoDTO> resultado = new ArrayList<>();

            for (VarianteProducto variante : variantes) {
                VarianteProductoDTO dto = new VarianteProductoDTO(
                        variante.getCodigoBarra(),
                        variante.getStock(),
                        variante.getPrecioCompra(),
                        variante.getTalla(),
                        variante.getColor(),
                        variante.getPrecioVenta(),
                        variante.getProducto().getId(),
                        variante.getUrlImagen()
                );

                dto.setId(variante.getId());
                dto.setNombreProducto(variante.getProducto().getNombre());
                dto.setUrlImagen(variante.getUrlImagen()); // Ajusta según tu estructura

                resultado.add(dto);
            }

            return resultado;
        } catch (PersistenciaException ex) {
            throw new NegocioException("Error al obtener variantes filtradas paginadas", ex);
        }
    }

    @Override
    public long contarVariantesPorNombreProducto(String terminoBusqueda) throws NegocioException {
        try {
            return varianteProductoDAO.contarVariantesPorNombreProducto(terminoBusqueda);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al contar variantes", e);
        }
    }

    private VarianteProductoDTO convertirADTO(VarianteProducto variante) {
        VarianteProductoDTO dto = new VarianteProductoDTO();
        dto.setId(variante.getId());
        dto.setCodigoBarra(variante.getCodigoBarra());
        dto.setStock(variante.getStock());
        dto.setPrecioCompra(variante.getPrecioCompra());
        dto.setPrecioVenta(variante.getPrecioVenta());
        dto.setTalla(variante.getTalla());
        dto.setColor(variante.getColor());
        dto.setUrlImagen(variante.getUrlImagen());
        dto.setProductoId(variante.getProducto().getId());
        dto.setNombreProducto(variante.getProducto().getNombre()); // asegúrate que `getNombre()` existe
        return dto;
    }

    @Override
    public List<VarianteProductoDTO> buscarVariantesPorCategoriaYNombreProducto(int idCategoria, String nombre, int pagina, int tamañoPagina) throws NegocioException {
        if (idCategoria <= 0) {
            throw new NegocioException("El ID de la categoría debe ser mayor que cero");
        }
        if (pagina < 1 || tamañoPagina < 1) {
            throw new NegocioException("Parámetros de paginación inválidos");
        }

        try {
            String filtro = (nombre == null || nombre.trim().isEmpty()) ? "" : nombre.trim().toLowerCase();
            List<VarianteProducto> variantes = varianteProductoDAO.buscarVariantesPorCategoriaYNombreProducto(idCategoria, filtro, pagina, tamañoPagina);
            return variantes.stream()
                    .map(this::convertirADTO)
                    .toList();
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al buscar variantes", e);
        }
    }

    @Override
    public long contarVariantesPorCategoriaYNombreProducto(int idCategoria, String nombre) throws NegocioException {
        if (idCategoria <= 0) {
            throw new NegocioException("El ID de la categoría debe ser mayor que cero");
        }
        try {
            String filtro = (nombre == null || nombre.trim().isEmpty()) ? "" : nombre.trim().toLowerCase();
            return varianteProductoDAO.contarVariantesPorCategoriaYNombreProducto(idCategoria, filtro);
        } catch (PersistenciaException e) {
            throw new NegocioException("Error al contar variantes", e);
        }
    }

}
