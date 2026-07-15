import { useEffect, useState } from "react";
import {
  ApiError,
  bookAppointment,
  cancelAppointment,
  listAppointments,
  listDoctors,
  listPatients,
} from "../api";
import type { Appointment, Doctor, Me, Patient } from "../types";
import {
  Avatar,
  btnGhost,
  btnPrimary,
  Card,
  Field,
  formatDateTime,
  inputClass,
  Spinner,
  StatusBadge,
} from "../ui";

export function AppointmentsPanel({ me }: { me: Me }) {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);

  const canBook = me.role === "PATIENT" || me.role === "ADMIN";

  const load = async () => {
    setLoading(true);
    setError("");
    try {
      const [appts, docs] = await Promise.all([listAppointments(), listDoctors()]);
      setAppointments(appts.content);
      setDoctors(docs.content);
      if (me.role === "ADMIN") setPatients((await listPatients()).content);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Failed to load appointments");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const onCancel = async (id: number) => {
    try {
      await cancelAppointment(id, "Cancelled from dashboard");
      await load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Cancel failed");
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Appointments</h2>
        {canBook && (
          <button className={btnPrimary} onClick={() => setShowForm((s) => !s)}>
            {showForm ? "Close" : "+ Book appointment"}
          </button>
        )}
      </div>

      {error && <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p>}

      {showForm && canBook && (
        <BookForm
          me={me}
          doctors={doctors}
          patients={patients}
          onBooked={() => {
            setShowForm(false);
            void load();
          }}
        />
      )}

      {loading ? (
        <Spinner />
      ) : appointments.length === 0 ? (
        <Card>
          <p className="text-slate-500">No appointments yet.</p>
        </Card>
      ) : (
        <div className="grid gap-3">
          {appointments.map((a) => (
            <Card key={a.id} className="flex flex-wrap items-center justify-between gap-3">
              <div className="flex items-center gap-3">
                <Avatar name={me.role === "PATIENT" ? a.doctorName : a.patientName} />
                <div>
                  <p className="font-semibold text-slate-800">
                    {me.role === "PATIENT" ? a.doctorName : a.patientName}
                    {me.role === "ADMIN" && (
                      <span className="font-normal text-slate-500"> · {a.doctorName}</span>
                    )}
                  </p>
                  <p className="mt-0.5 text-sm text-slate-500">
                    {formatDateTime(a.startTime)}
                    {a.reason && ` · ${a.reason}`}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <StatusBadge status={a.status} />
                {a.status === "SCHEDULED" && (
                  <button className={btnGhost} onClick={() => onCancel(a.id)}>
                    Cancel
                  </button>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}

function BookForm({
  me,
  doctors,
  patients,
  onBooked,
}: {
  me: Me;
  doctors: Doctor[];
  patients: Patient[];
  onBooked: () => void;
}) {
  const [doctorId, setDoctorId] = useState<number | "">("");
  const [patientId, setPatientId] = useState<number | "">("");
  const [startTime, setStartTime] = useState("");
  const [reason, setReason] = useState("");
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!doctorId || !startTime) return;
    if (me.role === "ADMIN" && !patientId) {
      setError("Select a patient.");
      return;
    }
    setSaving(true);
    setError("");
    try {
      await bookAppointment({
        doctorId: Number(doctorId),
        patientId: me.role === "ADMIN" ? Number(patientId) : undefined,
        startTime,
        reason: reason || undefined,
      });
      onBooked();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Booking failed");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Card>
      <form onSubmit={submit} className="grid gap-4 sm:grid-cols-2">
        {me.role === "ADMIN" && (
          <Field label="Patient">
            <select
              className={inputClass}
              value={patientId}
              onChange={(e) => setPatientId(e.target.value ? Number(e.target.value) : "")}
            >
              <option value="">Select…</option>
              {patients.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.fullName}
                </option>
              ))}
            </select>
          </Field>
        )}
        <Field label="Doctor">
          <select
            className={inputClass}
            value={doctorId}
            onChange={(e) => setDoctorId(e.target.value ? Number(e.target.value) : "")}
          >
            <option value="">Select…</option>
            {doctors.map((d) => (
              <option key={d.id} value={d.id}>
                {d.fullName} — {d.specialization}
              </option>
            ))}
          </select>
        </Field>
        <Field label="Date & time">
          <input
            className={inputClass}
            type="datetime-local"
            value={startTime}
            onChange={(e) => setStartTime(e.target.value)}
          />
        </Field>
        <Field label="Reason (optional)">
          <input className={inputClass} value={reason} onChange={(e) => setReason(e.target.value)} />
        </Field>
        {error && <p className="text-sm text-rose-600 sm:col-span-2">{error}</p>}
        <div className="sm:col-span-2">
          <button className={btnPrimary} disabled={saving}>
            {saving ? "Booking…" : "Confirm booking"}
          </button>
        </div>
      </form>
    </Card>
  );
}
