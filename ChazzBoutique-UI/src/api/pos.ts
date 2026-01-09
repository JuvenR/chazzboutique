import { http } from "./http";

const API_BASE =
  import.meta.env.DEV ? "" : (import.meta.env.VITE_API_BASE_URL || "");

// ===== Tipos =====

export type VarianteLookup = {
  varianteId: number;
  codigoBarras: string;
  precioVenta: number;
  productoId: number;
  nombreProducto: string;
  colorHex: string;
  stock: number;
  talla?: string;
};

export type VarianteRow = {
  varianteId: number;
  codigoBarras: string;
  precioVenta: number;
  colorHex?: string;
  stock: number;
  talla?: string;
};

export type CrearVentaRequest = {
  usuarioId: number;
  montoPago: number;
  descuento: number;
  detalles: { codigoBarras: string; cantidad: number }[];
};

export type VentaResponse = {
  id: number;
  fecha: string;
  estado: string;
  usuarioId: number;
  subtotal: number;
  descuento: number;
  total: number;
  montoPago: number;
  cambio: number;
  ticket?: { pdfUrl: string };
};

export type ProductoLite = { id: number; nombreProducto: string };


export function buscarVariantePorCodigo(codigo: string) {
  return http.get<VarianteLookup>(
    `/api/variantes/codigo/${encodeURIComponent(codigo)}`
  );
}

export function crearVenta(payload: CrearVentaRequest) {
  return http.post<VentaResponse>(`/api/ventas`, payload);
}

export function ticketPdfUrl(ventaId: number) {
  const base = import.meta.env.DEV
    ? "http://localhost:8080"
    : (import.meta.env.VITE_API_BASE_URL || "");

  return `${base}/api/ventas/${ventaId}/ticket.pdf`;
}


export function buscarProductosPorNombre(nombre: string, limit = 15) {
  const q = nombre.trim();
  if (!q) return Promise.resolve([] as ProductoLite[]);

  return http.get<ProductoLite[]>(
    `/api/productos/buscar?nombre=${encodeURIComponent(q)}&limit=${limit}`
  );
}


export function obtenerVariantesPorProducto(productoId: number) {
  return http.get<VarianteRow[]>(
    `/api/productos/${productoId}/variantes`
  );
}
