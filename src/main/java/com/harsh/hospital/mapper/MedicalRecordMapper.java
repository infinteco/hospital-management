package com.harsh.hospital.mapper;

import com.harsh.hospital.domain.MedicalRecord;
import com.harsh.hospital.dto.MedicalRecordDtos.MedicalRecordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicalRecordMapper {

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", source = "patient.fullName")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", source = "doctor.fullName")
    MedicalRecordResponse toResponse(MedicalRecord record);
}
