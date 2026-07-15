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
  return d.toLocaleString(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

export function Spinner({ label = "Loading…" }: { label?: string }) {
  return (
    <div className="flex items-center gap-2 py-8 text-slate-500">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-indigo-600" />
      {label}
    </div>
  );
}

export function StatusBadge({ status }: { status: AppointmentStatus }) {
  const styles: Record<AppointmentStatus, string> = {
    SCHEDULED: "bg-emerald-100 text-emerald-700",
    COMPLETED: "bg-slate-200 text-slate-700",
    CANCELLED: "bg-rose-100 text-rose-700",
  };
  return (
    <span className={cn("rounded-full px-2.5 py-0.5 text-xs font-medium", styles[status])}>
      {status.toLowerCase()}
    </span>
  );
}

export function Card({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <div className={cn("rounded-xl border border-slate-200 bg-white p-5 shadow-sm", className)}>
      {children}
    </div>
  );
}

export function Field({
  label,
  children,
}: {
  label: string;
  children: ReactNode;
}) {
  return (
    <label className="block text-sm">
      <span className="mb-1 block font-medium text-slate-600">{label}</span>
      {children}
    </label>
  );
}

export const inputClass =
  "w-full rounded-lg border border-slate-300 px-3 py-2 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500";

export const btnPrimary =
  "rounded-lg bg-indigo-600 px-4 py-2 font-medium text-white transition hover:bg-indigo-700 disabled:opacity-50";

export const btnGhost =
  "rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-100";
