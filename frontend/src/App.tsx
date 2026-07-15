import { useEffect, useState } from "react";
import { clearToken, getToken, me as fetchMe } from "./api";
import { AppointmentsPanel } from "./components/AppointmentsPanel";
import { DoctorsPanel } from "./components/DoctorsPanel";
import { LoginPage } from "./components/LoginPage";
import { MedicalRecordsPanel } from "./components/MedicalRecordsPanel";
import { NewRecordPanel } from "./components/NewRecordPanel";
import { PatientsPanel } from "./components/PatientsPanel";
import type { Me, Role } from "./types";
import { btnGhost, cn, Spinner } from "./ui";

const TABS: Record<Role, string[]> = {
  PATIENT: ["Appointments", "My Records", "Doctors"],
  DOCTOR: ["Appointments", "New Record"],
  ADMIN: ["Appointments", "Patients", "Doctors"],
};

const ROLE_BADGE: Record<Role, string> = {
  ADMIN: "bg-purple-100 text-purple-700",
  DOCTOR: "bg-sky-100 text-sky-700",
  PATIENT: "bg-emerald-100 text-emerald-700",
};

export default function App() {
  const [user, setUser] = useState<Me | null>(null);
  const [booting, setBooting] = useState(true);
  const [tab, setTab] = useState("Appointments");

  const loadMe = async () => {
    if (!getToken()) {
      setBooting(false);
      return;
    }
    try {
      const m = await fetchMe();
      setUser(m);
      setTab(TABS[m.role][0]);
    } catch {
      clearToken();
    } finally {
      setBooting(false);
    }
  };

  useEffect(() => {
    void loadMe();
  }, []);

  const logout = () => {
    clearToken();
    setUser(null);
  };

  if (booting) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner label="Loading…" />
      </div>
    );
  }

  if (!user) return <LoginPage onLoggedIn={loadMe} />;

  return (
    <div className="min-h-screen">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-2">
            <span className="text-xl">🏥</span>
            <span className="font-semibold">Hospital Management</span>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-sm text-slate-600">{user.username}</span>
            <span className={cn("rounded-full px-2.5 py-0.5 text-xs font-medium", ROLE_BADGE[user.role])}>
              {user.role.toLowerCase()}
            </span>
            <button className={btnGhost} onClick={logout}>
              Sign out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-5xl px-4 py-6">
        <nav className="mb-6 flex gap-1 border-b border-slate-200">
          {TABS[user.role].map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={cn(
                "border-b-2 px-4 py-2 text-sm font-medium transition",
                tab === t
                  ? "border-indigo-600 text-indigo-600"
                  : "border-transparent text-slate-500 hover:text-slate-800",
              )}
            >
              {t}
            </button>
          ))}
        </nav>

        {tab === "Appointments" && <AppointmentsPanel me={user} />}
        {tab === "My Records" &&
          (user.patientId ? (
            <MedicalRecordsPanel patientId={user.patientId} />
          ) : (
            <p className="text-slate-500">No patient profile linked to this account.</p>
          ))}
        {tab === "New Record" && <NewRecordPanel />}
        {tab === "Patients" && <PatientsPanel />}
        {tab === "Doctors" && <DoctorsPanel me={user} />}
      </div>
    </div>
  );
}
