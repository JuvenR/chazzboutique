import "./Sidebar.css";

export type MenuKey = "home" | "venta" | "categorias" | "productos" | "reportes";

type Props = {
  active: MenuKey;
  onChange: (k: MenuKey) => void;
};

const items: { key: MenuKey; label: string }[] = [
  { key: "home", label: "Home" },
  { key: "venta", label: "Venta" },
  { key: "categorias", label: "Categorías" },
  { key: "productos", label: "Productos" },
  { key: "reportes", label: "Reportes" },
];

export default function Sidebar({ active, onChange }: Props) {
  return (
    <aside className="sb">
      <div className="sb__brand">
        <div className="sb__logo">CHAZZ</div>
        <div className="sb__sub">Boutique</div>
      </div>

      <nav className="sb__nav">
        {items.map((it) => (
          <button
            key={it.key}
            className={`sb__item ${active === it.key ? "is-active" : ""}`}
            onClick={() => onChange(it.key)}
            type="button"
          >
            <span className="sb__dot" />
            <span className="sb__label">{it.label}</span>
          </button>
        ))}
      </nav>

      <div className="sb__footer">
        <div className="sb__pill">ChazzBoutique POS</div>
      </div>
    </aside>
  );
}
