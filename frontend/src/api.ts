import type {
  Appointment,
  AuthResponse,
  Doctor,
  MedicalRecord,
  Me,
  Page,
  Patient,
} from "./types";

const BASE_URL = (import.meta.env.VITE_API_URL ?? "http://localhost:8080").replace(/\/$/, "");
const TOKEN_KEY = "hospital_token";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}
export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}
export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

/** RFC 7807 problem detail, thrown on non-2xx responses. */
export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = { ...(init.headers as Record<string, string>) };
  if (init.body) headers["Content-Type"] = "application/json";
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(`${BASE_URL}${path}`, { ...init, headers });
  if (res.status === 204) return undefined as T;

  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const detail = (data && (data.detail || data.message)) || `Request failed (${res.status})`;
    throw new ApiError(res.status, detail);
  }
  return data as T;
}

// --- auth ---
export async function login(username: string, password: string): Promise<AuthResponse> {
  const auth = await request<AuthResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
  setToken(auth.token);
  return auth;
}

export function me(): Promise<Me> {
  return request<Me>("/api/me");
}

// --- doctors ---
export function listDoctors(): Promise<Page<Doctor>> {
  return request<Page<Doctor>>("/api/doctors?size=100&sort=fullName");
}
export function createDoctor(body: {
  fullName: string;
  specialization: string;
  email?: string;
  phone?: string;
}): Promise<Doctor> {
  return request<Doctor>("/api/doctors", { method: "POST", body: JSON.stringify(body) });
}

// --- patients (admin) ---
export function listPatients(): Promise<Page<Patient>> {
  return request<Page<Patient>>("/api/patients?size=100&sort=fullName");
}

// --- appointments ---
export function listAppointments(): Promise<Page<Appointment>> {
  return request<Page<Appointment>>("/api/appointments?size=100&sort=startTime,desc");
}
export function bookAppointment(body: {
  patientId?: number;
  doctorId: number;
  startTime: string;
  reason?: string;
}): Promise<Appointment> {
  return request<Appointment>("/api/appointments", {
    method: "POST",
    body: JSON.stringify(body),
  });
}
export function cancelAppointment(id: number, reason: string): Promise<Appointment> {
  return request<Appointment>(`/api/appointments/${id}/cancel`, {
    method: "POST",
    body: JSON.stringify({ reason }),
  });
}

// --- medical records ---
export function listMedicalRecords(patientId: number): Promise<Page<MedicalRecord>> {
  return request<Page<MedicalRecord>>(
    `/api/patients/${patientId}/medical-records?size=100&sort=createdAt,desc`,
  );
}
export function createMedicalRecord(body: {
  patientId: number;
  diagnosis: string;
  treatment?: string;
  notes?: string;
}): Promise<MedicalRecord> {
  return request<MedicalRecord>("/api/medical-records", {
    method: "POST",
    body: JSON.stringify(body),
  });
}
