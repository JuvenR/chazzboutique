/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.juvenr.mqc.chazzboutiqueapi.ventas;


import com.juvenr.mqc.chazzboutiqueapi.ventas.dto.CrearVentaRequest;
import com.juvenr.mqc.chazzboutiqueapi.ventas.dto.VentaResponse;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public VentaResponse crear(@RequestBody @Valid CrearVentaRequest req) {
        return ventaService.crearVenta(req);
    }

    @PostMapping("/cotizar")
    public Object cotizar(@RequestBody @Valid CrearVentaRequest req) {
        // puedes hacer un DTO específico luego
        return ventaService.cotizar(req);
    }
}
