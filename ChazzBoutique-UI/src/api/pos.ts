import { http } from "./http";

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

export function buscarVariantePorCodigo(codigo: string) {
  return http.get<VarianteLookup>(`/api/variantes/codigo/${encodeURIComponent(codigo)}`);
}

export function crearVenta(payload: CrearVentaRequest) {
  return http.post<VentaResponse>(`/api/ventas`, payload);
}

export function ticketPdfUrl(ventaId: number) {
  return `/api/ventas/${ventaId}/ticket.pdf`;
}
