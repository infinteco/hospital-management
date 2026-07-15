package com.harsh.hospital.mapper;

import com.harsh.hospital.domain.Appointment;
import com.harsh.hospital.dto.AppointmentDtos.AppointmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", source = "patient.fullName")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor.fullName")
    AppointmentResponse toResponse(Appointment appointment);
}
