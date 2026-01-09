import { useMemo, useState } from "react";
import Sidebar, { type MenuKey } from "./components/Sidebar";
import VentaPage from "./pages/VentaPage";

export default function App() {
  const [active, setActive] = useState<MenuKey>("venta");

  const content = useMemo(() => {
    if (active === "venta") return <VentaPage />;

    return (
      <div className="page-placeholder">
        <h1>{active.toUpperCase()}</h1>
        <p>Panel pendiente.</p>
      </div>
    );
  }, [active]);

  return (
    <div className="app-shell">
      <Sidebar active={active} onChange={setActive} />
      <main className="app-content">{content}</main>
    </div>
  );
}
