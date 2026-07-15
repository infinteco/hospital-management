import { useEffect, useState } from "react";
import { ApiError, listMedicalRecords } from "../api";
import type { MedicalRecord } from "../types";
import { Card, formatDateTime, Spinner } from "../ui";

/** Lists a single patient's medical records. */
export function MedicalRecordsPanel({
  patientId,
  heading = "My Medical Records",
}: {
  patientId: number;
  heading?: string;
}) {
  const [records, setRecords] = useState<MedicalRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;
    setLoading(true);
    listMedicalRecords(patientId)
      .then((p) => active && setRecords(p.content))
      .catch((err) => active && setError(err instanceof ApiError ? err.message : "Failed to load"))
      .finally(() => active && setLoading(false));
    return () => {
      active = false;
    };
  }, [patientId]);

  if (loading) return <Spinner />;
  if (error) return <p className="rounded-lg bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p>;

  return (
    <div className="space-y-4">
      <h2 className="text-lg font-semibold">{heading}</h2>
      {records.length === 0 ? (
        <Card>
          <p className="text-slate-500">No medical records.</p>
        </Card>
      ) : (
        <div className="grid gap-3">
          {records.map((r) => (
            <Card key={r.id}>
              <div className="flex items-center justify-between">
                <p className="font-medium">{r.diagnosis}</p>
                <span className="text-xs text-slate-400">{formatDateTime(r.createdAt)}</span>
              </div>
              {r.treatment && (
                <p className="mt-1 text-sm text-slate-600">
                  <span className="font-medium text-slate-500">Treatment:</span> {r.treatment}
                </p>
              )}
              {r.notes && (
                <p className="mt-1 text-sm text-slate-600">
                  <span className="font-medium text-slate-500">Notes:</span> {r.notes}
                </p>
              )}
              <p className="mt-2 text-xs text-slate-400">by {r.doctorName}</p>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
