import { useEffect, useState } from "react";
import { ApiError, createDoctor, listDoctors } from "../api";
import type { Doctor, Me } from "../types";
import { Avatar, btnGhost, btnPrimary, Card, Field, inputClass, Spinner } from "../ui";

export function DoctorsPanel({ me }: { me: Me }) {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      setDoctors((await listDoctors()).content);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to load doctors");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Doctors</h2>
        {me.role === "ADMIN" && (
          <button className={btnPrimary} onClick={() => setShowForm((s) => !s)}>
            {showForm ? "Close" : "+ Add doctor"}
          </button>
        )}
      </div>

      {error && <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p>}

      {showForm && me.role === "ADMIN" && (
        <NewDoctorForm
          onCreated={() => {
            setShowForm(false);
            void load();
          }}
        />
      )}

      {loading ? (
        <Spinner />
      ) : (
        <div className="grid gap-3 sm:grid-cols-2">
          {doctors.map((d) => (
            <Card key={d.id} className="flex items-center gap-4">
              <Avatar name={d.fullName} className="h-12 w-12 text-base" />
              <div className="min-w-0">
                <p className="truncate font-semibold text-slate-800">{d.fullName}</p>
                <p className="text-sm font-medium text-teal-600">{d.specialization}</p>
                {d.email && <p className="mt-0.5 truncate text-sm text-slate-400">{d.email}</p>}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

function NewDoctorForm({ onCreated }: { onCreated: () => void }) {
  const [fullName, setFullName] = useState("");
  const [specialization, setSpecialization] = useState("");
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await createDoctor({ fullName, specialization, email: email || undefined });
      onCreated();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Create failed");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card>
      <form onSubmit={submit} className="grid gap-4 sm:grid-cols-3">
        <Field label="Full name">
          <input className={inputClass} value={fullName} onChange={(e) => setFullName(e.target.value)} required />
        </Field>
        <Field label="Specialization">
          <input
            className={inputClass}
            value={specialization}
            onChange={(e) => setSpecialization(e.target.value)}
            required
          />
        </Field>
        <Field label="Email (optional)">
          <input className={inputClass} type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </Field>
        {error && <p className="text-sm text-rose-600 sm:col-span-3">{error}</p>}
        <div className="sm:col-span-3 flex gap-2">
          <button className={btnPrimary} disabled={saving}>
            {saving ? "Saving…" : "Create doctor"}
          </button>
          <button type="button" className={btnGhost} onClick={onCreated}>
            Cancel
          </button>
        </div>
      </form>
    </Card>
  );
}
