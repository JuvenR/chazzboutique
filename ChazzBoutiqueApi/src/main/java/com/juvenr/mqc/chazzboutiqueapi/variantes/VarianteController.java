package com.juvenr.mqc.chazzboutiqueapi.variantes;

import com.juvenr.mqc.chazzboutiqueapi.variantes.dto.VarianteLookupResponse;
import com.mycompany.chazzboutiquenegocio.dtos.ProductoDTO;
import com.mycompany.chazzboutiquenegocio.dtos.VarianteProductoDTO;
import com.mycompany.chazzboutiquenegocio.excepciones.NegocioException;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IProductoNegocio;
import com.mycompany.chazzboutiquenegocio.interfacesObjetosNegocio.IVarianteProductoNegocio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    private final IVarianteProductoNegocio varianteNegocio;
    private final IProductoNegocio productoNegocio;

    public VarianteController(IVarianteProductoNegocio varianteNegocio, IProductoNegocio productoNegocio) {
        this.varianteNegocio = varianteNegocio;
        this.productoNegocio = productoNegocio;
    }

    @GetMapping("/codigo/{codigo}")
    public VarianteLookupResponse buscarPorCodigo(@PathVariable String codigo) {
        try {
            VarianteProductoDTO v = varianteNegocio.obtenerVariantePorCodigoBarra(codigo);

            if (v == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe variante para el código: " + codigo);
            }

            ProductoDTO p = productoNegocio.buscarPorId(v.getProductoId());
            if (p == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe producto para la variante del código: " + codigo);
            }

            VarianteLookupResponse res = new VarianteLookupResponse();
            res.setVarianteId(v.getId());
            res.setCodigoBarras(v.getCodigoBarra());
            res.setPrecioVenta(v.getPrecioVenta());

            res.setProductoId(v.getProductoId());
            res.setNombreProducto(p.getNombreProducto());

            res.setColorHex(v.getColor());
            res.setStock(v.getStock());
            res.setTalla(v.getTalla());

            return res;

        } catch (NegocioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
