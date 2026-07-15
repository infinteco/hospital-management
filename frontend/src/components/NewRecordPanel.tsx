import { useEffect, useMemo, useState } from "react";
import { ApiError, createMedicalRecord, listAppointments } from "../api";
import type { Appointment } from "../types";
import { btnPrimary, Card, Field, inputClass, Spinner } from "../ui";

/** Doctor view: author a medical record for one of their patients. */
export function NewRecordPanel() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [loading, setLoading] = useState(true);
  const [patientId, setPatientId] = useState<number | "">("");
  const [diagnosis, setDiagnosis] = useState("");
  const [treatment, setTreatment] = useState("");
  const [notes, setNotes] = useState("");
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    listAppointments()
      .then((p) => setAppointments(p.content))
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load"))
      .finally(() => setLoading(false));
  }, []);

  // Unique patients drawn from the doctor's appointments.
  const patients = useMemo(() => {
    const map = new Map<number, string>();
    appointments.forEach((a) => map.set(a.patientId, a.patientName));
    return [...map.entries()].map(([id, name]) => ({ id, name }));
  }, [appointments]);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!patientId || !diagnosis) return;
    setSaving(true);
    setError("");
    setSaved(false);
    try {
      await createMedicalRecord({
        patientId: Number(patientId),
        diagnosis,
        treatment: treatment || undefined,
        notes: notes || undefined,
      });
      setSaved(true);
      setDiagnosis("");
      setTreatment("");
      setNotes("");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Save failed");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold">New Medical Record</h2>
      <Card>
        {patients.length === 0 ? (
          <p className="text-slate-500">
            No patients yet — records can be authored for patients you have appointments with.
          </p>
        ) : (
          <form onSubmit={submit} className="grid gap-4">
            <Field label="Patient">
              <select
                className={inputClass}
                value={patientId}
                onChange={(e) => setPatientId(e.target.value ? Number(e.target.value) : "")}
              >
                <option value="">Select…</option>
                {patients.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </Field>
            <Field label="Diagnosis">
              <input className={inputClass} value={diagnosis} onChange={(e) => setDiagnosis(e.target.value)} required />
            </Field>
            <Field label="Treatment (optional)">
              <input className={inputClass} value={treatment} onChange={(e) => setTreatment(e.target.value)} />
            </Field>
            <Field label="Notes (optional)">
              <textarea className={inputClass} rows={3} value={notes} onChange={(e) => setNotes(e.target.value)} />
            </Field>
            {error && <p className="text-sm text-rose-600">{error}</p>}
            {saved && <p className="text-sm text-emerald-600">Record saved ✓</p>}
            <div>
              <button className={btnPrimary} disabled={saving}>
                {saving ? "Saving…" : "Save record"}
              </button>
            </div>
          </form>
        )}
      </Card>
    </div>
  );
}
