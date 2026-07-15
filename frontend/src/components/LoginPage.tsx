import { useState } from "react";
import { ApiError, login } from "../api";
import { btnPrimary, Field, inputClass } from "../ui";

const DEMO = [
  { label: "Admin", username: "admin", hint: "manage everything" },
  { label: "Doctor", username: "dr.smith", hint: "appointments + records" },
  { label: "Patient", username: "alice", hint: "book + view records" },
];

const FEATURES = [
  "Book & manage appointments in real time",
  "Role-based access — patients see only their own records",
  "Doctors author medical records; admins manage staff",
];

export function LoginPage({ onLoggedIn }: { onLoggedIn: () => void }) {
  const [username, setUsername] = useState("alice");
  const [password, setPassword] = useState("Password123!");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      await login(username, password);
      onLoggedIn();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      {/* Brand / hero panel */}
      <div className="relative hidden flex-col justify-between overflow-hidden bg-gradient-to-br from-teal-600 to-cyan-700 p-12 text-white lg:flex">
        <div className="flex items-center gap-2 text-xl font-bold">
          <span>🏥</span> MediDesk
        </div>
        <div>
          <h1 className="text-4xl font-extrabold leading-tight">
            Care coordination,
            <br />
            made simple.
          </h1>
          <ul className="mt-8 space-y-3">
            {FEATURES.map((f) => (
              <li key={f} className="flex items-start gap-3 text-teal-50">
                <span className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-white/20 text-xs">
                  ✓
                </span>
                {f}
              </li>
            ))}
          </ul>
        </div>
        <p className="text-sm text-teal-100/80">Spring Boot · JWT · Spring Data JPA · MySQL</p>
        <div className="pointer-events-none absolute -right-20 -top-20 h-72 w-72 rounded-full bg-white/10" />
        <div className="pointer-events-none absolute -bottom-24 -left-10 h-80 w-80 rounded-full bg-white/5" />
      </div>

      {/* Form panel */}
      <div className="flex items-center justify-center px-4 py-12">
        <div className="w-full max-w-sm">
          <div className="mb-6 text-center lg:hidden">
            <h1 className="text-2xl font-bold">🏥 MediDesk</h1>
          </div>
          <h2 className="text-2xl font-bold text-slate-900">Welcome back</h2>
          <p className="mt-1 text-sm text-slate-500">Sign in to your dashboard.</p>

          <form onSubmit={submit} className="mt-6 space-y-4">
            <Field label="Username">
              <input className={inputClass} value={username} onChange={(e) => setUsername(e.target.value)} />
            </Field>
            <Field label="Password">
              <input
                className={inputClass}
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </Field>
            {error && <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-600">{error}</p>}
            <button className={`${btnPrimary} w-full`} disabled={loading}>
              {loading ? "Signing in…" : "Sign in"}
            </button>
          </form>

          <div className="mt-6">
            <p className="mb-2 text-center text-xs font-medium uppercase tracking-wide text-slate-400">
              Try a demo account · password Password123!
            </p>
            <div className="grid grid-cols-3 gap-2">
              {DEMO.map((d) => (
                <button
                  key={d.username}
                  onClick={() => {
                    setUsername(d.username);
                    setPassword("Password123!");
                  }}
                  title={d.hint}
                  className="rounded-lg border border-slate-200 bg-white px-2 py-2 text-center transition hover:border-teal-400 hover:bg-teal-50"
                >
                  <div className="text-sm font-semibold text-slate-700">{d.label}</div>
                  <div className="text-[11px] text-slate-400">{d.username}</div>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
