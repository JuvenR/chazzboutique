import { useEffect, useMemo, useRef, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import "../styles/venta.css";
import * as posApi from "../api/pos";
import type { VarianteLookup, VarianteRow, CrearVentaRequest, ProductoLite } from "../api/pos";

type Row = {
  id: string;
  codigoBarras: string;
  nombre: string;
  cantidad: number;
  precio: number;
  colorHex?: string;
};

type Toast = { type: "error" | "ok"; text: string } | null;
type VentaOk = { id: number; total: number; cambio: number };

function money(n: number) {
  return n.toLocaleString("es-MX", { style: "currency", currency: "MXN" });
}

function parseIntSafe(raw: string, fallback: number) {
  const s = raw.trim();
  if (s === "") return fallback;
  const n = Number(s);
  if (!Number.isFinite(n)) return fallback;
  return Math.trunc(n);
}

function onlyDigits(raw: string) {
  return raw.replace(/[^\d]/g, "");
}


const easeOut = [0.16, 1, 0.3, 1] as const;

const fadeUp = {
  hidden: { opacity: 0, y: 10, filter: "blur(6px)" },
  show: { opacity: 1, y: 0, filter: "blur(0px)", transition: { duration: 0.55, ease: easeOut } },
};

const pop = {
  hidden: { opacity: 0, scale: 0.98, y: 10, filter: "blur(8px)" },
  show: { opacity: 1, scale: 1, y: 0, filter: "blur(0px)", transition: { duration: 0.22, ease: easeOut } },
  exit: { opacity: 0, scale: 0.985, y: 8, filter: "blur(8px)", transition: { duration: 0.16, ease: easeOut } },
};

function SuccessModal({
  open,
  venta,
  onOpenTicket,
  onNewSale,
}: {
  open: boolean;
  venta: VentaOk | null;
  onOpenTicket: () => void;
  onNewSale: () => void;
}) {
  return (
    <AnimatePresence>
      {open && venta && (
        <motion.div
          className="modalOverlay"
          role="dialog"
          aria-modal="true"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1, transition: { duration: 0.18 } }}
          exit={{ opacity: 0, transition: { duration: 0.14 } }}
        >
          <motion.div className="modalCard" variants={pop} initial="hidden" animate="show" exit="exit">
            <div className="modalHead">
              <div className="modalBadge ok">OK</div>
              <div>
                <h3 className="modalTitle">Venta registrada</h3>
                <div className="modalSub">Ticket generado correctamente.</div>
              </div>
            </div>

            <div className="modalBody">
              <div className="modalRow">
                <span>ID</span>
                <b>#{venta.id}</b>
              </div>
              <div className="modalRow">
                <span>Total</span>
                <b>{money(venta.total)}</b>
              </div>
              <div className="modalRow">
                <span>Cambio</span>
                <b>{money(venta.cambio)}</b>
              </div>
            </div>

            <div className="modalActions">
              <button className="btn btn-outline" type="button" onClick={onNewSale}>
                Nueva venta
              </button>
              <button className="btn btn-primary" type="button" onClick={onOpenTicket}>
                Abrir ticket
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

function VariantesModal({
  open,
  title,
  loading,
  variantes,
  error,
  onCancel,
  onPick,
}: {
  open: boolean;
  title: string;
  loading: boolean;
  variantes: VarianteRow[];
  error: string | null;
  onCancel: () => void;
  onPick: (v: VarianteRow) => void;
}) {
  const [selected, setSelected] = useState<number>(0);

  useEffect(() => {
    if (open) setSelected(0);
  }, [open]);

  const selectedVar = variantes[selected] ?? null;

  return (
    <AnimatePresence>
      {open && (
        <motion.div
          className="modalOverlay"
          role="dialog"
          aria-modal="true"
          onMouseDown={(e) => {
            if (e.target === e.currentTarget) onCancel();
          }}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1, transition: { duration: 0.18 } }}
          exit={{ opacity: 0, transition: { duration: 0.14 } }}
        >
          <motion.div className="modalCard modalCard--wide" variants={pop} initial="hidden" animate="show" exit="exit">
            <div className="modalHead">
              <div className="modalBadge sel">SEL</div>
              <div>
                <h3 className="modalTitle">Variantes</h3>
                <div className="modalSub">{title}</div>
              </div>
            </div>

            <div className="modalBody">
              {loading && <div className="muted">Cargando variantes...</div>}
              {error && <div className="muted danger">{error}</div>}

              {!loading && !error && variantes.length === 0 && (
                <div className="muted">No hay variantes para este producto.</div>
              )}

              {!loading && !error && variantes.length > 0 && (
                <div className="tablewrap tablewrap--modal" style={{ maxHeight: 420, overflow: "auto" }}>
                  <table className="table table--darkHead">
                    <thead>
                      <tr>
                        <th style={{ textAlign: "center", width: 92 }}>COLOR</th>
                        <th>TALLA</th>
                        <th className="num">PRECIO</th>
                        <th className="num">STOCK</th>
                        <th>CODIGO</th>
                      </tr>
                    </thead>
                    <tbody>
                      {variantes.map((v, i) => {
                        const active = i === selected;
                        return (
                          <tr
                            key={v.codigoBarras}
                            onClick={() => setSelected(i)}
                            onDoubleClick={() => onPick(v)}
                            className={active ? "is-rowActive" : undefined}
                            style={{ cursor: "pointer" }}
                          >
                            <td style={{ textAlign: "center" }}>
                              <span
                                className="swatch swatch--tiny"
                                style={{
                                  background: v.colorHex ?? "#ffffff",
                                }}
                              />
                            </td>
                            <td>{v.talla ?? "—"}</td>
                            <td className="num">{money(v.precioVenta)}</td>
                            <td className="num">{v.stock ?? "—"}</td>
                            <td>{v.codigoBarras}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            <div className="modalActions">
              <button className="btn btn-outline" type="button" onClick={onCancel}>
                Cancelar
              </button>
              <button
                className="btn btn-primary"
                type="button"
                disabled={loading || variantes.length === 0 || !selectedVar}
                onClick={() => selectedVar && onPick(selectedVar)}
              >
                Elegir variante
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

export default function VentaPage() {
  const [modoNombre, setModoNombre] = useState(false);

  const [codigo, setCodigo] = useState("");
  const [nombre, setNombre] = useState("");
  const [precio, setPrecio] = useState<number>(0);
  const [color, setColor] = useState<string>("#ffffff");

  const [cantidadStr, setCantidadStr] = useState<string>("1");
  const [rows, setRows] = useState<Row[]>([]);

  const [descuentoStr, setDescuentoStr] = useState<string>("0");
  const [montoPagoStr, setMontoPagoStr] = useState<string>("0");

  const descuento = useMemo(() => Math.max(0, parseIntSafe(descuentoStr, 0)), [descuentoStr]);
  const montoPago = useMemo(() => Math.max(0, parseIntSafe(montoPagoStr, 0)), [montoPagoStr]);

  const [loadingLookup, setLoadingLookup] = useState(false);
  const [lookupError, setLookupError] = useState<string | null>(null);
  const [variante, setVariante] = useState<VarianteLookup | null>(null);

  const [loadingPay, setLoadingPay] = useState(false);

  const [toast, setToast] = useState<Toast>(null);
  const toastTimer = useRef<number | null>(null);

  const [successOpen, setSuccessOpen] = useState(false);
  const [ventaOk, setVentaOk] = useState<VentaOk | null>(null);
  const [ticketUrl, setTicketUrl] = useState("");

  const [nameQuery, setNameQuery] = useState("");
  const [nameOptions, setNameOptions] = useState<ProductoLite[]>([]);
  const [nameOpen, setNameOpen] = useState(false);
  const [loadingName, setLoadingName] = useState(false);
  const [nameError, setNameError] = useState<string | null>(null);
  const [pickedProducto, setPickedProducto] = useState<ProductoLite | null>(null);

  const [varModalOpen, setVarModalOpen] = useState(false);
  const [varModalLoading, setVarModalLoading] = useState(false);
  const [varModalError, setVarModalError] = useState<string | null>(null);
  const [varModalItems, setVarModalItems] = useState<VarianteRow[]>([]);

  const [qtyDrafts, setQtyDrafts] = useState<Record<string, string>>({});

  const usuarioId = 1;

  const subtotal = useMemo(() => rows.reduce((acc, r) => acc + r.precio * r.cantidad, 0), [rows]);
  const total = useMemo(() => Math.max(0, subtotal - descuento), [subtotal, descuento]);
  const cambio = useMemo(() => Math.max(0, montoPago - total), [montoPago, total]);
  const [descuentoOn, setDescuentoOn] = useState(false);
  const descuentoRef = useRef<HTMLInputElement | null>(null);


  const itemsCount = useMemo(() => {
    return rows.reduce((acc, r) => {
      const draft = qtyDrafts[r.id];
      const qty = Math.max(1, parseIntSafe(draft ?? String(r.cantidad), r.cantidad));
      return acc + qty;
    }, 0);
  }, [rows, qtyDrafts]);

  function showToast(type: "error" | "ok", text: string) {
    setToast({ type, text });
    if (toastTimer.current) window.clearTimeout(toastTimer.current);
    toastTimer.current = window.setTimeout(() => setToast(null), 2300);
  }

  useEffect(() => {
    return () => {
      if (toastTimer.current) window.clearTimeout(toastTimer.current);
    };
  }, []);

  function getCantidadActual() {
    return Math.max(1, parseIntSafe(cantidadStr, 1));
  }

  function getNombreVariante(v: VarianteLookup | null, fallback = ""): string {
    return v?.nombreProducto ?? fallback;
  }

  async function onBuscarCodigo() {
    const c = codigo.trim();
    if (!c) return;

    setLoadingLookup(true);
    setLookupError(null);

    try {
      const v = await posApi.buscarVariantePorCodigo(c);
      setVariante(v);

      const nombreV = getNombreVariante(v, "");
      setNombre(nombreV);
      setPrecio(v.precioVenta);
      setColor(v.colorHex ?? "#ffffff");

      setNameQuery(nombreV);
      setPickedProducto(null);
    } catch (e) {
      setVariante(null);
      setNombre("");
      setPrecio(0);
      setColor("#ffffff");
      const msg = (e as Error).message || "No encontrado";
      showToast("error", msg);
    } finally {
      setLoadingLookup(false);
    }
  }

  useEffect(() => {
    if (!modoNombre) return;

    const q = nameQuery.trim();
    setNameError(null);
    setPickedProducto(null);

    if (q.length < 2) {
      setNameOptions([]);
      setNameOpen(false);
      return;
    }

    setLoadingName(true);
    const t = window.setTimeout(async () => {
      try {
        const items = await posApi.buscarProductosPorNombre(q);
        setNameOptions(items ?? []);
        setNameOpen(true);
      } catch (e) {
        setNameOptions([]);
        setNameOpen(false);
        setNameError((e as Error).message || "Error buscando por nombre");
      } finally {
        setLoadingName(false);
      }
    }, 250);

    return () => window.clearTimeout(t);
  }, [nameQuery, modoNombre]);

  async function openVariantesForProducto(p: ProductoLite) {
    setPickedProducto(p);
    setNameQuery(p.nombreProducto);
    setNameOpen(false);

    setVarModalOpen(true);
    setVarModalLoading(true);
    setVarModalError(null);
    setVarModalItems([]);

    try {
      const vars = await posApi.obtenerVariantesPorProducto(p.id);
      setVarModalItems(vars ?? []);
      if (!vars || vars.length === 0) setVarModalError("Este producto no tiene variantes.");
    } catch (e) {
      setVarModalError((e as Error).message || "Error cargando variantes");
    } finally {
      setVarModalLoading(false);
    }
  }

  async function onPickVariante(v: VarianteRow) {
    setVarModalOpen(false);

    setLoadingLookup(true);
    setLookupError(null);

    try {
      const full = await posApi.buscarVariantePorCodigo(v.codigoBarras);
      setVariante(full);

      const nombreV = getNombreVariante(full, nameQuery ?? "");
      setNombre(nombreV);
      setPrecio(full.precioVenta);
      setColor(full.colorHex ?? "#ffffff");

      setCodigo(full.codigoBarras);

      showToast("ok", "Variante cargada");
    } catch (e) {
      const msg = (e as Error).message || "Error cargando variante";
      setLookupError(msg);
      showToast("error", msg);
    } finally {
      setLoadingLookup(false);
    }
  }

  async function onEnterNombre() {
    if (nameOptions.length === 1) {
      await openVariantesForProducto(nameOptions[0]);
      return;
    }

    if (pickedProducto) {
      await openVariantesForProducto(pickedProducto);
      return;
    }

    setNameOpen(true);
    if (nameOptions.length === 0) showToast("error", "No hay coincidencias para ese nombre.");
  }

  function onAgregar() {
    if (!variante) {
      showToast(
        "error",
        modoNombre
          ? "Primero elige una variante (Enter en nombre / seleccionar opción)."
          : "Primero busca un código válido (Enter) para cargar el producto."
      );
      return;
    }

    const cant = getCantidadActual();
    const nombreSeguro = variante.nombreProducto || nombre || nameQuery || "Producto";

    setRows((prev) => {
      const idx = prev.findIndex((r) => r.codigoBarras === variante.codigoBarras);
      if (idx !== -1) {
        const copy = [...prev];
        copy[idx] = {
          ...copy[idx],
          cantidad: copy[idx].cantidad + cant,
          nombre: copy[idx].nombre || nombreSeguro,
        };
        return copy;
      }

      return [
        ...prev,
        {
          id: crypto.randomUUID(),
          codigoBarras: variante.codigoBarras,
          nombre: nombreSeguro,
          cantidad: cant,
          precio: Math.max(0, variante.precioVenta),
          colorHex: variante.colorHex,
        },
      ];
    });

    setCodigo("");
    setNombre("");
    setPrecio(0);
    setColor("#ffffff");
    setCantidadStr("1");
    setVariante(null);
    setLookupError(null);

    setPickedProducto(null);
    setNameQuery("");
    setNameOptions([]);
    setNameOpen(false);

    showToast("ok", "Producto agregado");
  }

  function onEliminar(id: string) {
    setRows((prev) => prev.filter((r) => r.id !== id));
    setQtyDrafts((prev) => {
      const { [id]: _drop, ...rest } = prev;
      return rest;
    });
  }

  function onVaciar() {
    setRows([]);
    setQtyDrafts({});

    setDescuentoStr("0");
    setMontoPagoStr("0");

    setCodigo("");
    setNombre("");
    setPrecio(0);
    setColor("#ffffff");
    setCantidadStr("1");
    setVariante(null);
    setLookupError(null);

    setPickedProducto(null);
    setNameQuery("");
    setNameOptions([]);
    setNameOpen(false);
    setNameError(null);
    setDescuentoOn(false);

  }

  async function onPagar() {
    if (rows.length === 0) return;

    if (montoPago < total) {
      showToast("error", `Pago insuficiente. Falta: ${money(total - montoPago)}`);
      return;
    }

    setLoadingPay(true);
    try {
      const payload: CrearVentaRequest = {
        usuarioId,
        descuento,
        montoPago,
        detalles: rows.map((r) => ({ codigoBarras: r.codigoBarras, cantidad: r.cantidad })),
      };

      const res = await posApi.crearVenta(payload);
      const url = posApi.ticketPdfUrl(res.id);

      window.open(url, "_blank", "noopener,noreferrer");

      setVentaOk({ id: res.id, total: res.total, cambio: res.cambio });
      setTicketUrl(url);
      setSuccessOpen(true);
    } catch (e) {
      showToast("error", `Error al pagar: ${(e as Error).message}`);
    } finally {
      setLoadingPay(false);
    }
  }

  function handleOpenTicket() {
    if (ticketUrl) window.open(ticketUrl, "_blank", "noopener,noreferrer");
  }

  function handleNewSale() {
    setSuccessOpen(false);
    setVentaOk(null);
    setTicketUrl("");
    onVaciar();
  }

  return (
    <div className="venta">
      <motion.header className="venta__header" variants={fadeUp} initial="hidden" animate="show">
        <div className="venta__hero">
          <div className="venta__heroText">
            <h1>Punto de Venta</h1>
            <p>Registra productos, aplica descuento y cobra en segundos.</p>
          </div>

          <div className="venta__heroMeta">
            <div className="kpi kpi--brand">
              <div className="kpi__label">Total vendido hoy</div>
              <div className="kpi__value">{money(total)}</div>
            </div>
          </div>
        </div>
      </motion.header>

      <section className="venta__top">
        <motion.div
          className="card card--lift"
          initial={{ opacity: 0, y: 10, filter: "blur(8px)" }}
          animate={{ opacity: 1, y: 0, filter: "blur(0px)" }}
          transition={{ duration: 0.45, ease: easeOut }}
        >
          <div className="formgrid">
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

              <div className={`collapse ${modoNombre ? "isClosed" : "isOpen"}`}>
                <div className="muted" style={{ marginTop: 6 }}>
                  Tip: escribe/escanea y presiona <b>Enter</b> para buscar.
                </div>
              </div>

              {lookupError && !modoNombre && (
                <div className="muted danger" style={{ marginTop: 6 }}>
                  {lookupError}
                </div>
              )}
            </div>

            <div className={`field field--withPopover ${modoNombre && nameOpen ? "is-popoverOpen" : ""}`}>
              <label>Nombre</label>

              <input
                className={!modoNombre ? "inputLikeDisabled" : ""}
                value={modoNombre ? nameQuery : nombre}
                readOnly={!modoNombre}
                onChange={(e) => {
                  if (!modoNombre) return;
                  setNameQuery(e.target.value);
                  setNameOpen(true);
                }}
                onFocus={() => {
                  if (modoNombre && nameOptions.length > 0) setNameOpen(true);
                }}
                onBlur={() => window.setTimeout(() => setNameOpen(false), 120)}
                onKeyDown={(e) => {
                  if (!modoNombre) return;
                  if (e.key === "Enter") onEnterNombre();
                  if (e.key === "Escape") setNameOpen(false);
                }}
                placeholder={modoNombre ? "Escribe nombre y presiona Enter" : "Buscar por nombre (habilita toggle)"}
                disabled={loadingPay}
              />

              <div className="toggle toggle--below">
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
                    setCantidadStr("1");
                    setVariante(null);
                    setLookupError(null);

                    setPickedProducto(null);
                    setNameQuery("");
                    setNameOptions([]);
                    setNameOpen(false);
                    setNameError(null);
                  }}
                />
                <label htmlFor="toggleNombre">Habilitar búsqueda por nombre</label>
              </div>

              {modoNombre && (
                <>
                  {nameError && (
                    <div className="muted danger" style={{ marginTop: 6 }}>
                      {nameError}
                    </div>
                  )}

                  <AnimatePresence>
                    {nameOpen && nameOptions.length > 0 && (
                      <motion.div
                        className="popover"
                        initial={{ opacity: 0, y: 8, scale: 0.99, filter: "blur(8px)" }}
                        animate={{ opacity: 1, y: 0, scale: 1, filter: "blur(0px)" }}
                        exit={{ opacity: 0, y: 6, scale: 0.99, filter: "blur(8px)" }}
                        transition={{ duration: 0.16, ease: easeOut }}
                        style={{
                          position: "absolute",
                          left: 0,
                          right: 0,
                          top: "100%",
                          marginTop: 10,
                          padding: 8,
                          maxHeight: 280,
                          overflow: "auto",
                        }}
                      >
                        <div className="popover__title">Resultados</div>

                        {loadingName && <div className="muted" style={{ padding: "10px 12px" }}>Buscando…</div>}

                        {nameOptions.map((p) => (
                          <button
                            key={p.id}
                            type="button"
                            className="rowpick"
                            onMouseDown={(ev) => ev.preventDefault()}
                            onClick={() => openVariantesForProducto(p)}
                          >
                            <span className="rowpick__main">{p.nombreProducto}</span>
                            <span className="rowpick__hint">Enter para seleccionar</span>
                          </button>
                        ))}
                      </motion.div>
                    )}
                  </AnimatePresence>
                </>
              )}
            </div>

            <div className="field">
              <label>Precio</label>
              <input value={money(precio)} readOnly />
            </div>

            <div className="field field--tiny">
              <label>Color</label>
              <div className="swatch" style={{ background: color }} />
            </div>

            <div className="field field--tiny">
              <label>Cantidad</label>
              <div className="stepper stepper--pro">
                <button
                  type="button"
                  onClick={() => {
                    const cur = getCantidadActual();
                    setCantidadStr(String(Math.max(1, cur - 1)));
                  }}
                  disabled={loadingPay}
                >
                  −
                </button>

                <input
                  inputMode="numeric"
                  value={cantidadStr}
                  onFocus={(e) => e.currentTarget.select()}
                  onChange={(e) => {
                    const raw = e.target.value;
                    if (/^\d*$/.test(raw)) setCantidadStr(raw);
                  }}
                  onBlur={() => {
                    const next = Math.max(1, parseIntSafe(cantidadStr, 1));
                    setCantidadStr(String(next));
                  }}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      const next = Math.max(1, parseIntSafe(cantidadStr, 1));
                      setCantidadStr(String(next));
                      (e.currentTarget as HTMLInputElement).blur();
                    }
                  }}
                  disabled={loadingPay}
                />

                <button
                  type="button"
                  onClick={() => {
                    const cur = getCantidadActual();
                    setCantidadStr(String(cur + 1));
                  }}
                  disabled={loadingPay}
                >
                  +
                </button>
              </div>
            </div>

            <div className="field field--actions">
              <label>&nbsp;</label>
              <motion.button
                className="btn btn-primary"
                type="button"
                onClick={onAgregar}
                disabled={loadingPay || !variante}
                title={!variante ? "Carga una variante (código o nombre)" : undefined}
                whileHover={!loadingPay && variante ? { y: -1 } : undefined}
                whileTap={!loadingPay && variante ? { y: 0 } : undefined}
                transition={{ duration: 0.14 }}
              >
                Agregar
              </motion.button>
            </div>
          </div>
        </motion.div>
      </section>

      {/* BODY */}
      <section className="venta__body">
        {/* TABLE */}
        <motion.div className="card tablecard card--lift" variants={fadeUp} initial="hidden" animate="show">
          <div className="tablecard__head">
            <h2>Productos</h2>

            <div className="tableMeta">
              <div className="tableMeta__chip">
                <span>Items</span>
                <b>{itemsCount}</b>
              </div>
              <div className="tableMeta__chip">
                <span>Subtotal</span>
                <b className="brand">{money(subtotal)}</b>
              </div>
            </div>
          </div>

          <div className="tablewrap">
            <table className="table">
              <thead>
                <tr>
                  <th>NOMBRE PRODUCTO</th>
                  <th style={{ width: 92, textAlign: "center" }}>COLOR</th>
                  <th className="num">CANTIDAD</th>
                  <th className="num">PRECIO UNITARIO</th>
                  <th className="num">SUBTOTAL</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {rows.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="empty">
                      Agrega productos para comenzar la venta.
                    </td>
                  </tr>
                ) : (
                  rows.map((r) => {
                    const draft = qtyDrafts[r.id];
                    const value = draft ?? String(r.cantidad);

                    return (
                      <motion.tr
                        key={r.id}
                        className="row"
                        initial={{ opacity: 0, y: 6 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.18, ease: easeOut }}
                        whileHover={{ backgroundColor: "rgba(16,24,40,0.02)" }}
                      >
                        <td className="name">
                          <div className="name__top">
                            <span className="name__title">{r.nombre}</span>
                          </div>
                          <div className="muted" style={{ fontSize: 13, marginTop: 4 }}>
                            {r.codigoBarras}
                          </div>
                        </td>

                        <td style={{ textAlign: "center" }}>
                          {r.colorHex ? (
                            <span className="colorDot" title={r.colorHex} style={{ background: r.colorHex }} />
                          ) : (
                            <span className="muted">—</span>
                          )}
                        </td>

                        <td className="num">
                          <input
                            className="qty"
                            inputMode="numeric"
                            value={value}
                            onFocus={(e) => e.currentTarget.select()}
                            onKeyDown={(e) => {
                              if (e.key === "Enter") (e.currentTarget as HTMLInputElement).blur();
                            }}
                            onChange={(e) => {
                              const raw = e.currentTarget.value;
                              const cleaned = onlyDigits(raw);
                              setQtyDrafts((prev) => ({ ...prev, [r.id]: cleaned }));
                            }}
                            onBlur={() => {
                              const raw = qtyDrafts[r.id] ?? String(r.cantidad);
                              const next = Math.max(1, parseIntSafe(raw, r.cantidad));

                              setRows((prev) => prev.map((x) => (x.id === r.id ? { ...x, cantidad: next } : x)));

                              setQtyDrafts((prev) => {
                                const { [r.id]: _drop, ...rest } = prev;
                                return rest;
                              });
                            }}
                            disabled={loadingPay}
                          />
                        </td>

                        <td className="num">{money(r.precio)}</td>
                        <td className="num">{money(r.precio * r.cantidad)}</td>

                        <td className="actions">
                          <button
                            className="iconbtn"
                            onClick={() => onEliminar(r.id)}
                            title="Eliminar"
                            disabled={loadingPay}
                            type="button"
                          >
                            ✕
                          </button>
                        </td>
                      </motion.tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          <div className="tablecard__foot">
            <div className="totalbar">
              <span>SUBTOTAL:</span>
              <b className="brand">{money(subtotal)}</b>
            </div>
          </div>
        </motion.div>

        <motion.aside className="card paycard card--lift" variants={fadeUp} initial="hidden" animate="show">
          <div className="payhead">
            <h2>Registro del Pago</h2>

          </div>

          <div className="paygrid">
  <label className="labelWithBtn">
    <span>Descuento</span>
    <button
      type="button"
      className="miniAction"
      disabled={rows.length === 0 || loadingPay}
      onClick={() => {
        setDescuentoOn((prev) => {
          const next = !prev;
          if (!next) setDescuentoStr("0");
          else window.setTimeout(() => descuentoRef.current?.focus(), 0);
          return next;
        });
      }}
    >
      {descuentoOn ? "Quitar" : "Poner"}
    </button>
  </label>

  <input
    ref={descuentoRef}
    inputMode="numeric"
    value={descuentoStr}
    onFocus={(e) => e.currentTarget.select()}
    onKeyDown={(e) => {
      if (e.key === "Enter") (e.currentTarget as HTMLInputElement).blur();
    }}
    onChange={(e) => {
      const raw = e.currentTarget.value;
      if (/^\d*$/.test(raw)) setDescuentoStr(raw);
    }}
    onBlur={() => {
      const next = Math.max(0, parseIntSafe(descuentoStr, 0));
      setDescuentoStr(String(next));
    }}
    disabled={rows.length === 0 || loadingPay || !descuentoOn}
  />

  <label>Monto pago</label>
  <input
    inputMode="numeric"
    value={montoPagoStr}
    onFocus={(e) => e.currentTarget.select()}
    onKeyDown={(e) => {
      if (e.key === "Enter") (e.currentTarget as HTMLInputElement).blur();
    }}
    onChange={(e) => {
      const raw = e.currentTarget.value;
      if (/^\d*$/.test(raw)) setMontoPagoStr(raw);
    }}
    onBlur={() => {
      const next = Math.max(0, parseIntSafe(montoPagoStr, 0));
      setMontoPagoStr(String(next));
    }}
    disabled={rows.length === 0 || loadingPay}
  />

  <label>Cambio</label>
  <div className="valueBox">{money(cambio)}</div>

  <div className="payDivider" aria-hidden="true" />

  <label className="totalLabel">Total</label>
  <div className="valueBox valueBox--total">{money(total)}</div>
</div>


          <div className="payactions">
            <button className="btn btn-outline" type="button" onClick={onVaciar} disabled={rows.length === 0 || loadingPay}>
              Borrar productos
            </button>

            <motion.button
              className="btn btn-danger"
              type="button"
              onClick={onPagar}
              disabled={rows.length === 0 || loadingPay}
              whileHover={!loadingPay && rows.length > 0 ? { y: -1 } : undefined}
              whileTap={!loadingPay && rows.length > 0 ? { y: 0 } : undefined}
              transition={{ duration: 0.14 }}
            >
              {loadingPay ? "Procesando..." : "Pagar"}
            </motion.button>
          </div>

          <div className="payhint">Enter confirma campos. Escape cierra listas. Doble click elige variante en modal.</div>
        </motion.aside>
      </section>

      {/* TOAST */}
      <AnimatePresence>
        {toast && (
          <motion.div
            className={`toast ${toast.type}`}
            initial={{ opacity: 0, y: 12, scale: 0.98, filter: "blur(10px)" }}
            animate={{ opacity: 1, y: 0, scale: 1, filter: "blur(0px)" }}
            exit={{ opacity: 0, y: 10, scale: 0.985, filter: "blur(10px)" }}
            transition={{ duration: 0.18, ease: easeOut }}
          >
            {toast.text}
          </motion.div>
        )}
      </AnimatePresence>

      <SuccessModal open={successOpen} venta={ventaOk} onOpenTicket={handleOpenTicket} onNewSale={handleNewSale} />

      <VariantesModal
        open={varModalOpen}
        title={pickedProducto ? pickedProducto.nombreProducto : "Selecciona una variante"}
        loading={varModalLoading}
        variantes={varModalItems}
        error={varModalError}
        onCancel={() => setVarModalOpen(false)}
        onPick={onPickVariante}
      />
    </div>
  );
}
