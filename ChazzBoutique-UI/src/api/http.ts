const API_BASE =
  import.meta.env.DEV ? "" : (import.meta.env.VITE_API_BASE_URL || "");

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE";

async function readBody(res: Response) {
  const text = await res.text().catch(() => "");
  if (!text) return { text: "", json: null as any };

  try {
    return { text, json: JSON.parse(text) };
  } catch {
    return { text, json: null as any };
  }
}

function errorMessage(status: number, body: { text: string; json: any }) {
  const j = body.json;
  const msg =
    j?.message ||
    j?.error ||
    j?.msg ||
    j?.detail ||
    (typeof j === "string" ? j : "") ||
    body.text;

  if (msg && String(msg).trim()) return String(msg);

  // Fallbacks
  if (status === 404) return "No encontrado";
  if (status === 400) return "Solicitud inválida";
  if (status === 401) return "No autorizado";
  if (status === 403) return "Sin permisos";
  return `HTTP ${status}`;
}

async function request<T>(
  path: string,
  options: {
    method?: HttpMethod;
    body?: unknown;
    headers?: Record<string, string>;
  } = {}
): Promise<T> {
  const url = `${API_BASE}${path}`;

  const res = await fetch(url, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  });

  // 204 No Content
  if (res.status === 204) return undefined as T;

  const body = await readBody(res);

  if (!res.ok) {
    throw new Error(errorMessage(res.status, body));
  }

  return (body.json !== null ? body.json : body.text) as T;
}

export const http = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) => request<T>(path, { method: "POST", body }),
  put: <T>(path: string, body: unknown) => request<T>(path, { method: "PUT", body }),
  del: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};
