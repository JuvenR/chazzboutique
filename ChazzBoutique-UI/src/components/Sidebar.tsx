import { useMemo, useRef, useState } from "react";
import { motion } from "framer-motion";
import "./Sidebar.css";

export type MenuKey = "home" | "venta" | "categorias" | "productos" | "reportes";

type Props = {
  active: MenuKey;
  onChange: (k: MenuKey) => void;
};

function Icon({ name, active }: { name: MenuKey; active: boolean }) {
  const common = {
    width: 18,
    height: 18,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: "currentColor",
    strokeWidth: 2,
    strokeLinecap: "round" as const,
    strokeLinejoin: "round" as const,
    className: `sb__icon ${active ? "is-active" : ""}`,
    "aria-hidden": true,
  };

  switch (name) {
    case "home":
      return (
        <svg {...common}>
          <path d="M3 10.5 12 3l9 7.5" />
          <path d="M5 10v10h14V10" />
          <path d="M9 20v-6h6v6" />
        </svg>
      );
    case "venta":
      return (
        <svg {...common}>
          <circle cx="9" cy="20" r="1" />
          <circle cx="17" cy="20" r="1" />
          <path d="M3 4h2l2.4 12.2a2 2 0 0 0 2 1.6h7.9a2 2 0 0 0 2-1.6L22 8H6.2" />
        </svg>
      );
    case "categorias":
      return (
        <svg {...common}>
          <rect x="3" y="3" width="8" height="8" rx="2" />
          <rect x="13" y="3" width="8" height="8" rx="2" />
          <rect x="3" y="13" width="8" height="8" rx="2" />
          <rect x="13" y="13" width="8" height="8" rx="2" />
        </svg>
      );
    case "productos":
      return (
        <svg {...common}>
          <path d="M20.6 13.1 12 21.7a2 2 0 0 1-2.8 0L3 15.5V3h12.5l5.1 5.1a2 2 0 0 1 0 2.8Z" />
          <path d="M7.5 7.5h.01" />
        </svg>
      );
    case "reportes":
      return (
        <svg {...common}>
          <path d="M4 19V5" />
          <path d="M20 19H4" />
          <path d="M8 17v-6" />
          <path d="M12 17V9" />
          <path d="M16 17v-3" />
        </svg>
      );
    default:
      return null;
  }
}

const items: { key: MenuKey; label: string }[] = [
  { key: "home", label: "Home" },
  { key: "venta", label: "Venta" },
  { key: "categorias", label: "Categorías" },
  { key: "productos", label: "Productos" },
  { key: "reportes", label: "Reportes" },
];

export default function Sidebar({ active, onChange }: Props) {
  const [open, setOpen] = useState(false);

  const dims = useMemo(() => ({ collapsed: 92, expanded: 256 }), []);
  const openDelayMs = 120;
  const closeDelayMs = 90;

  const tOpen = useRef<number | null>(null);
  const tClose = useRef<number | null>(null);

  function clearTimers() {
    if (tOpen.current) window.clearTimeout(tOpen.current);
    if (tClose.current) window.clearTimeout(tClose.current);
    tOpen.current = null;
    tClose.current = null;
  }

  function scheduleOpen() {
    if (open) return;
    if (tOpen.current) return;
    if (tClose.current) {
      window.clearTimeout(tClose.current);
      tClose.current = null;
    }
    tOpen.current = window.setTimeout(() => {
      tOpen.current = null;
      setOpen(true);
    }, openDelayMs);
  }

  function scheduleClose() {
    if (!open) {
      if (tOpen.current) {
        window.clearTimeout(tOpen.current);
        tOpen.current = null;
      }
      return;
    }
    if (tClose.current) return;
    if (tOpen.current) {
      window.clearTimeout(tOpen.current);
      tOpen.current = null;
    }
    tClose.current = window.setTimeout(() => {
      tClose.current = null;
      setOpen(false);
    }, closeDelayMs);
  }

  return (
    <>
    
      <div
        className="sb-zone"
        onMouseEnter={scheduleOpen}
        onMouseLeave={scheduleClose}
        aria-hidden="true"
      />

      <motion.aside
        className={`sb ${open ? "is-open" : "is-closed"}`}
        onMouseEnter={scheduleOpen}
        onMouseLeave={scheduleClose}
        onFocusCapture={() => {
          clearTimers();
          setOpen(true);
        }}
        onBlurCapture={(e) => {
          const next = e.relatedTarget as Node | null;
          if (!next || !e.currentTarget.contains(next)) {
            clearTimers();
            setOpen(false);
          }
        }}
        animate={{ width: open ? dims.expanded : dims.collapsed }}
        transition={{ type: "spring", stiffness: 240, damping: 30, mass: 0.9 }}
        style={{ width: dims.collapsed }}
      >
        <div className="sb__brand">
          <img
            className="sb__brandLogo"
            src="/images/chazzLogoBlack.png"
            alt="Chazz Boutique"
            draggable={false}
          />
        </div>

        <nav className="sb__nav" aria-label="Menú">
          {items.map((it) => {
            const isActive = active === it.key;

            return (
              <button
                key={it.key}
                type="button"
                title={it.label}
                onClick={() => onChange(it.key)}
                className={`sb__item ${isActive ? "is-active" : ""}`}
              >
                <span className="sb__iconWrap" aria-hidden="true">
                  <Icon name={it.key} active={isActive} />
                </span>

                <span className={`sb__labelSlot ${open ? "is-open" : "is-closed"}`}>
                  <span className="sb__label">{it.label}</span>
                </span>

                {open && isActive && <span className="sb__activeRail" />}
              </button>
            );
          })}
        </nav>
      </motion.aside>
    </>
  );
}
