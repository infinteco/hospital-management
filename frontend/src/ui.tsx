import type { ReactNode } from "react";
import type { AppointmentStatus } from "./types";

/** Join truthy class names. */
export function cn(...parts: (string | false | null | undefined)[]): string {
  return parts.filter(Boolean).join(" ");
}

export function formatDateTime(iso: string | null): string {
  if (!iso) return "—";
  const d = new Date(iso.length <= 16 ? `${iso}:00` : iso);
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" });
}

export function Spinner({ label = "Loading…" }: { label?: string }) {
  return (
    <div className="flex items-center gap-2 py-10 text-slate-500">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-teal-600" />
      {label}
    </div>
  );
}

export function StatusBadge({ status }: { status: AppointmentStatus }) {
  const styles: Record<AppointmentStatus, string> = {
    SCHEDULED: "bg-teal-100 text-teal-700",
    COMPLETED: "bg-slate-200 text-slate-700",
    CANCELLED: "bg-rose-100 text-rose-700",
  };
  return (
    <span className={cn("rounded-full px-2.5 py-0.5 text-xs font-semibold", styles[status])}>
      {status.charAt(0) + status.slice(1).toLowerCase()}
    </span>
  );
}

/** Colored initials avatar derived from a name. */
export function Avatar({ name, className }: { name: string; className?: string }) {
  const clean = name.replace(/^Dr\.?\s*/i, "").trim();
  const initials = clean
    .split(/\s+/)
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() ?? "")
    .join("");
  const palette = [
    "bg-teal-100 text-teal-700",
    "bg-sky-100 text-sky-700",
    "bg-indigo-100 text-indigo-700",
    "bg-amber-100 text-amber-700",
    "bg-rose-100 text-rose-700",
    "bg-emerald-100 text-emerald-700",
  ];
  let h = 0;
  for (let i = 0; i < clean.length; i++) h = (h * 31 + clean.charCodeAt(i)) & 0xffff;
  return (
    <span
      className={cn(
        "flex shrink-0 items-center justify-center rounded-full text-sm font-bold",
        palette[h % palette.length],
        className ?? "h-10 w-10",
      )}
    >
      {initials || "?"}
    </span>
  );
}

export function Card({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={cn("rounded-2xl border border-slate-200 bg-white p-5 shadow-sm", className)}>
      {children}
    </div>
  );
}

export function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="block text-sm">
      <span className="mb-1 block font-medium text-slate-600">{label}</span>
      {children}
    </label>
  );
}

export const inputClass =
  "w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-slate-800 outline-none transition focus:border-teal-500 focus:ring-2 focus:ring-teal-500/30";

export const btnPrimary =
  "rounded-lg bg-teal-600 px-4 py-2 font-semibold text-white shadow-sm transition hover:bg-teal-700 disabled:opacity-50";

export const btnGhost =
  "rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50";
