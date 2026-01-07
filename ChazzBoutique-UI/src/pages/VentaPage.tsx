import { useMemo, useState } from "react";
import "../styles/venta.css";
import * as posApi from "../api/pos";
import type { VarianteLookup, CrearVentaRequest } from "../api/pos";

type Row = {
  id: string;

  codigoBarras: string;

  nombre: string;
  cantidad: number;
  precio: number;
  colorHex?: string;
};

function money(n: number) {
  return n.toLocaleString("es-MX", { style: "currency", currency: "MXN" });
}

function num(v: unknown) {
  const n = Number(v);
  return Number.isFinite(n) ? n : 0;
}

export default function VentaPage() {
  const [modoNombre, setModoNombre] = useState(false);

  const [codigo, setCodigo] = useState("");
  const [nombre, setNombre] = useState("");
  const [precio, setPrecio] = useState<number>(0);
  const [color, setColor] = useState<string>("#ffffff");
  const [cantidad, setCantidad] = useState<number>(1);

  const [rows, setRows] = useState<Row[]>([]);

  const [descuento, setDescuento] = useState<number>(0);
  const [montoPago, setMontoPago] = useState<number>(0);

  const [loadingLookup, setLoadingLookup] = useState(false);
  const [lookupError, setLookupError] = useState<string | null>(null);
  const [variante, setVariante] = useState<VarianteLookup | null>(null);

  const [loadingPay, setLoadingPay] = useState(false);

  const usuarioId = 1;

  const subtotal = useMemo(
    () => rows.reduce((acc, r) => acc + r.precio * r.cantidad, 0),
    [rows]
  );

  const total = useMemo(() => Math.max(0, subtotal - descuento), [subtotal, descuento]);
  const cambio = useMemo(() => Math.max(0, montoPago - total), [montoPago, total]);

  
  async function onBuscarCodigo() {
    const c = codigo.trim();
    if (!c) return;

    setLoadingLookup(true);
    setLookupError(null);

    try {
      const v = await posApi.buscarVariantePorCodigo(c);

      setVariante(v);
      setNombre(v.nombreProducto);
      setPrecio(v.precioVenta);
      setColor(v.colorHex ?? "#ffffff");
    } catch (e) {
      setVariante(null);
      setNombre("");
      setPrecio(0);
      setColor("#ffffff");
      setLookupError((e as Error).message || "No encontrado");
    } finally {
      setLoadingLookup(false);
    }
  }

  function onAgregar() {
    if (!modoNombre) {
      if (!variante) {
        alert("Primero busca un código válido (Enter) para cargar el producto.");
        return;
      }

      setRows((prev) => {
        const idx = prev.findIndex((r) => r.codigoBarras === variante.codigoBarras);
        if (idx !== -1) {
          const copy = [...prev];
          copy[idx] = { ...copy[idx], cantidad: copy[idx].cantidad + cantidad };
          return copy;
        }

        return [
          ...prev,
          {
            id: crypto.randomUUID(),
            codigoBarras: variante.codigoBarras,
            nombre: variante.nombreProducto,
            cantidad,
            precio: Math.max(0, variante.precioVenta),
            colorHex: variante.colorHex,
          },
        ];
      });

      setCodigo("");
      setNombre("");
      setPrecio(0);
      setColor("#ffffff");
      setCantidad(1);
      setVariante(null);
      setLookupError(null);
      return;
    }

    alert("Modo nombre: falta el modal de variantes. Ahorita solo está conectado por CÓDIGO.");
  }

  function onEliminar(id: string) {
    setRows((prev) => prev.filter((r) => r.id !== id));
  }

  function onVaciar() {
    setRows([]);
    setDescuento(0);
    setMontoPago(0);

    setCodigo("");
    setNombre("");
    setPrecio(0);
    setColor("#ffffff");
    setCantidad(1);
    setVariante(null);
    setLookupError(null);
  }

 
  async function onPagar() {
    if (rows.length === 0) return;

    if (montoPago < total) {
      alert(`Pago insuficiente. Falta: ${money(total - montoPago)}`);
      return;
    }

    setLoadingPay(true);
    try {
      const payload: CrearVentaRequest = {
        usuarioId,
        descuento,
        montoPago,
        detalles: rows.map((r) => ({
          codigoBarras: r.codigoBarras,
          cantidad: r.cantidad,
        })),
      };

      const res = await posApi.crearVenta(payload);

      alert(
        `Venta registrada ✅\n` +
          `ID: ${res.id}\n` +
          `Total: ${money(res.total)}\n` +
          `Cambio: ${money(res.cambio)}`
      );

      const url = res.ticket?.pdfUrl || posApi.ticketPdfUrl(res.id);
      window.open(url, "_blank");

      onVaciar();
    } catch (e) {
      alert(`Error al pagar: ${(e as Error).message}`);
    } finally {
      setLoadingPay(false);
    }
  }

  return (
    <div className="venta">
      <header className="venta__header">
        <div>
          <h1>Punto de Venta</h1>
          <p>Registra productos, aplica descuento y cobra en segundos.</p>
        </div>
      </header>

      <section className="venta__top">
        <div className="card">
          <div className="formgrid">
            {/* CÓDIGO */}
            <div className="field">
              <label>Código</label>
              <input
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") onBuscarCodigo();
                }}
                placeholder="Escanea / escribe y presiona Enter"
                disabled={modoNombre || loadingLookup || loadingPay}
              />
              {!modoNombre && (
                <div className="muted" style={{ marginTop: 6 }}>
                  Tip: escribe/escanea y presiona <b>Enter</b> para buscar.
                </div>
              )}
              {lookupError && !modoNombre && (
                <div className="muted" style={{ color: "#b03235", marginTop: 6 }}>
                  {lookupError}
                </div>
              )}
              {loadingLookup && !modoNombre && (
                <div className="muted" style={{ marginTop: 6 }}>
                  Buscando…
                </div>
              )}
            </div>

            {/* NOMBRE */}
            <div className="field">
            <div className="labelrow">
                <label>Nombre</label>

                <div className="toggle">
                <input
                    id="toggleNombre"
                    type="checkbox"
                    checked={modoNombre}
                    onChange={(e) => {
                    const v = e.target.checked;
                    setModoNombre(v);

                    setCodigo("");
                    setNombre("");
                    setPrecio(0);
                    setColor("#ffffff");
                    setCantidad(1);
                    setVariante(null);
                    setLookupError(null);
                    }}
                />
                <label htmlFor="toggleNombre">Habilitar</label>
                </div>
            </div>

            <input
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                placeholder="Buscar por nombre (pronto con modal)"
                disabled={!modoNombre || loadingPay}
            />
            </div>


            {/* PRECIO */}
            <div className="field">
              <label>Precio</label>
              <input value={money(precio)} readOnly />
            </div>

            {/* COLOR */}
            <div className="field field--tiny">
              <label>Color</label>
              <div className="swatch" style={{ background: color }} />
            </div>

            {/* CANTIDAD */}
            <div className="field field--tiny">
              <label>Cantidad</label>
              <div className="stepper">
                <button
                  type="button"
                  onClick={() => setCantidad((c) => Math.max(1, c - 1))}
                  disabled={loadingPay}
                >
                  −
                </button>
                <input
                  value={cantidad}
                  onChange={(e) => setCantidad(Math.max(1, Number(e.target.value || 1)))}
                  disabled={loadingPay}
                />
                <button type="button" onClick={() => setCantidad((c) => c + 1)} disabled={loadingPay}>
                  +
                </button>
              </div>
            </div>

            {/* AGREGAR */}
            <div className="field field--actions">
              <label>&nbsp;</label>
              <button
                className="btn btn-primary"
                type="button"
                onClick={onAgregar}
                disabled={loadingPay || (!modoNombre && !variante)}
                title={!modoNombre && !variante ? "Busca primero por código (Enter)" : undefined}
              >
                Agregar
              </button>
            </div>
          </div>
        </div>
      </section>

      <section className="venta__body">
        {/* TABLA */}
        <div className="card tablecard">
          <div className="tablecard__head">
            <h2>Productos</h2>
            <div className="muted">
              Subtotal: <b>{money(subtotal)}</b>
            </div>
          </div>

          <div className="tablewrap">
            <table className="table">
              <thead>
                <tr>
                  <th>NOMBRE PRODUCTO</th>
                  <th className="num">CANTIDAD</th>
                  <th className="num">PRECIO UNITARIO</th>
                  <th className="num">SUBTOTAL</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {rows.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="empty">
                      Agrega productos para comenzar la venta.
                    </td>
                  </tr>
                ) : (
                  rows.map((r) => (
                    <tr key={r.id}>
                      <td className="name">
                        {r.nombre}
                        <div className="muted" style={{ fontSize: 12, marginTop: 4 }}>
                          {r.codigoBarras}
                        </div>
                      </td>

                      <td className="num">
                        <input
                          className="qty"
                          value={r.cantidad}
                          onChange={(e) => {
                            const v = Math.max(1, Number(e.target.value || 1));
                            setRows((prev) => prev.map((x) => (x.id === r.id ? { ...x, cantidad: v } : x)));
                          }}
                          disabled={loadingPay}
                        />
                      </td>

                      <td className="num">{money(r.precio)}</td>
                      <td className="num">{money(r.precio * r.cantidad)}</td>

                      <td className="actions">
                        <button className="iconbtn" onClick={() => onEliminar(r.id)} title="Eliminar" disabled={loadingPay}>
                          🗑️
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          <div className="tablecard__foot">
            <div className="totalbar">
              <span>TOTAL:</span>
              <b>{money(subtotal)}</b>
            </div>
          </div>
        </div>

        {/* COBRO */}
        <aside className="card paycard">
          <h2>Cobro</h2>

          <div className="paygrid">
            <label>Descuento</label>
            <input
              value={descuento}
              onChange={(e) => setDescuento(Math.max(0, num(e.target.value)))}
              type="number"
              min={0}
              disabled={rows.length === 0 || loadingPay}
            />

            <label>Monto pago</label>
            <input
              value={montoPago}
              onChange={(e) => setMontoPago(Math.max(0, num(e.target.value)))}
              type="number"
              min={0}
              disabled={rows.length === 0 || loadingPay}
            />

            <label>Cambio</label>
            <div className="value">{money(cambio)}</div>

            <label className="totalLabel">Total</label>
            <div className="value totalValue">{money(total)}</div>
          </div>

          <button
            className="btn btn-ghost"
            type="button"
            onClick={() => setDescuento((d) => (d > 0 ? 0 : d))}
            disabled={rows.length === 0 || loadingPay}
          >
            Aplicar / Remover descuento
          </button>

          <div className="payactions">
            <button className="btn btn-outline" type="button" onClick={onVaciar} disabled={rows.length === 0 || loadingPay}>
              Borrar productos
            </button>
            <button className="btn btn-danger" type="button" onClick={onPagar} disabled={rows.length === 0 || loadingPay}>
              {loadingPay ? "Procesando..." : "Pagar"}
            </button>
          </div>
        </aside>
      </section>
    </div>
  );
}
