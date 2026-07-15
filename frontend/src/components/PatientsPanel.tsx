import { useEffect, useState } from "react";
import { ApiError, listPatients } from "../api";
import type { Patient } from "../types";
import { btnGhost, Card, Spinner } from "../ui";
import { MedicalRecordsPanel } from "./MedicalRecordsPanel";

/** Admin view: list patients and drill into any patient's records. */
export function PatientsPanel() {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [openId, setOpenId] = useState<number | null>(null);

  useEffect(() => {
    listPatients()
      .then((p) => setPatients(p.content))
      .catch((err) => setError(err instanceof ApiError ? err.message : "Failed to load"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner />;
  if (error) return <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p>;

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold">Patients</h2>
      <div className="grid gap-3">
        {patients.map((p) => (
          <Card key={p.id}>
            <div className="flex flex-wrap items-center justify-between gap-2">
              <div>
                <p className="font-medium">{p.fullName}</p>
                <p className="text-sm text-slate-500">
                  {[p.gender, p.dateOfBirth, p.email].filter(Boolean).join(" · ") || "—"}
                </p>
              </div>
              <button className={btnGhost} onClick={() => setOpenId(openId === p.id ? null : p.id)}>
                {openId === p.id ? "Hide records" : "View records"}
              </button>
            </div>
            {openId === p.id && (
              <div className="mt-4 border-t border-slate-100 pt-4">
                <MedicalRecordsPanel patientId={p.id} heading={`${p.fullName}'s records`} />
              </div>
            )}
          </Card>
        ))}
      </div>
    </div>
  );
}
