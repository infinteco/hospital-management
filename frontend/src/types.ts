export type Role = "ADMIN" | "DOCTOR" | "PATIENT";

export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  role: Role;
}

export interface Me {
  username: string;
  role: Role;
  patientId: number | null;
  doctorId: number | null;
}

export interface Doctor {
  id: number;
  fullName: string;
  specialization: string;
  email: string | null;
  phone: string | null;
}

export interface Patient {
  id: number;
  fullName: string;
  email: string | null;
  phone: string | null;
  dateOfBirth: string | null;
  gender: string | null;
}

export type AppointmentStatus = "SCHEDULED" | "COMPLETED" | "CANCELLED";

export interface Appointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  startTime: string;
  status: AppointmentStatus;
  reason: string | null;
  createdAt: string;
  cancelledAt: string | null;
  cancelReason: string | null;
}

export interface MedicalRecord {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  diagnosis: string;
  treatment: string | null;
  notes: string | null;
  createdAt: string;
}

/** Spring Data pageable response. */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
