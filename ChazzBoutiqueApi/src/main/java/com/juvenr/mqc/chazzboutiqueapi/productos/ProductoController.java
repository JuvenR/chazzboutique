package com.juvenr.mqc.chazzboutiqueapi.productos;

import com.juvenr.mqc.chazzboutiqueapi.productos.dto.ProductoLiteResponse;
import com.juvenr.mqc.chazzboutiqueapi.variantes.dto.VarianteRowResponse;
import com.mycompany.chazzboutiquenegocio.dtos.ProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;
import java.util.Collections;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoNegocio productoNegocio;
    private final IVarianteProductoNegocio varianteNegocio;

    public ProductoController(IProductoNegocio productoNegocio, IVarianteProductoNegocio varianteNegocio) {
        this.productoNegocio = productoNegocio;
        this.varianteNegocio = varianteNegocio;
    }

    @GetMapping("/buscar")
    public List<ProductoLiteResponse> buscar(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "limit", defaultValue = "15") int limit
    ) {
        String q = (nombre == null) ? "" : nombre.trim();
        if (q.isEmpty()) {
            return Collections.emptyList();
        }

        // normaliza limit (evita 0, negativos, enormes)
        int safeLimit = Math.max(1, Math.min(limit, 50));

        try {
            List<ProductoDTO> productos = productoNegocio.buscarPorNombre(q);

            return productos.stream()
                    .limit(safeLimit) 
                    .map(p -> new ProductoLiteResponse(p.getId(), p.getNombreProducto()))
                    .collect(Collectors.toList());

        } catch (NegocioException e) {
            return Collections.emptyList();
        }
    }

    @GetMapping("/{id}/variantes")
    public List<VarianteRowResponse> variantes(@PathVariable Long id) {
        try {
            List<VarianteProductoDTO> vars = varianteNegocio.obtenerVariantesPorProducto(id);

            return vars.stream().map(v -> {
                VarianteRowResponse r = new VarianteRowResponse();
                r.setVarianteId(v.getId());
                r.setCodigoBarras(v.getCodigoBarra());
                r.setPrecioVenta(v.getPrecioVenta());
                r.setColorHex(v.getColor());
                r.setStock(v.getStock());
                r.setTalla(v.getTalla());
                return r;
            }).collect(Collectors.toList());

        } catch (NegocioException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
