import { useEffect, useState } from "react";
import { clearToken, getToken, me as fetchMe } from "./api";
import { AppointmentsPanel } from "./components/AppointmentsPanel";
import { DoctorsPanel } from "./components/DoctorsPanel";
import { LoginPage } from "./components/LoginPage";
import { MedicalRecordsPanel } from "./components/MedicalRecordsPanel";
import { NewRecordPanel } from "./components/NewRecordPanel";
import { PatientsPanel } from "./components/PatientsPanel";
import type { Me, Role } from "./types";
import { Avatar, cn, Spinner } from "./ui";

const TABS: Record<Role, string[]> = {
  PATIENT: ["Appointments", "My Records", "Doctors"],
  DOCTOR: ["Appointments", "New Record"],
  ADMIN: ["Appointments", "Patients", "Doctors"],
};

const ROLE_BADGE: Record<Role, string> = {
  ADMIN: "bg-purple-100 text-purple-700",
  DOCTOR: "bg-sky-100 text-sky-700",
  PATIENT: "bg-teal-100 text-teal-700",
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
        <Spinner />
      </div>
    );
  }

  if (!user) return <LoginPage onLoggedIn={loadMe} />;

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-2">
            <span className="text-xl">🏥</span>
            <span className="text-lg font-bold text-slate-900">
              Medi<span className="text-teal-600">Desk</span>
            </span>
          </div>
          <div className="flex items-center gap-3">
            <div className="hidden text-right sm:block">
              <div className="text-sm font-medium text-slate-800">{user.username}</div>
              <span className={cn("rounded-full px-2 py-0.5 text-[11px] font-semibold", ROLE_BADGE[user.role])}>
                {user.role.toLowerCase()}
              </span>
            </div>
            <Avatar name={user.username} className="h-9 w-9" />
            <button
              onClick={logout}
              className="rounded-lg border border-slate-300 px-3 py-1.5 text-sm font-medium text-slate-700 transition hover:bg-slate-50"
            >
              Sign out
            </button>
          </div>
        </div>
      </header>

      <div className="mx-auto max-w-5xl px-4 py-6">
        <nav className="mb-6 flex gap-2 overflow-x-auto">
          {TABS[user.role].map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={cn(
                "shrink-0 rounded-full px-4 py-2 text-sm font-semibold transition",
                tab === t
                  ? "bg-teal-600 text-white shadow-sm"
                  : "bg-white text-slate-600 ring-1 ring-slate-200 hover:bg-slate-50",
              )}
            >
              {t}
            </button>
          ))}
        </nav>

        <div className="animate-fade-up" key={tab}>
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
    </div>
  );
}
