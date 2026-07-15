import { useState } from "react";
import { ApiError, login } from "../api";
import { btnPrimary, Field, inputClass } from "../ui";

const DEMO = [
  { label: "Admin", username: "admin" },
  { label: "Doctor", username: "dr.smith" },
  { label: "Patient", username: "alice" },
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
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-sm">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold">🏥 Hospital Management</h1>
          <p className="mt-1 text-sm text-slate-500">Sign in to your dashboard</p>
        </div>
        <form onSubmit={submit} className="space-y-4 rounded-xl border border-slate-200 bg-white p-6 shadow-sm">
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
          {error && <p className="text-sm text-rose-600">{error}</p>}
          <button className={`${btnPrimary} w-full`} disabled={loading}>
            {loading ? "Signing in…" : "Sign in"}
          </button>
        </form>
        <div className="mt-4 text-center text-xs text-slate-500">
          <p className="mb-2">Demo accounts (password <code>Password123!</code>):</p>
          <div className="flex justify-center gap-2">
            {DEMO.map((d) => (
              <button
                key={d.username}
                onClick={() => {
                  setUsername(d.username);
                  setPassword("Password123!");
                }}
                className="rounded-full border border-slate-300 px-3 py-1 hover:bg-slate-100"
              >
                {d.label}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
