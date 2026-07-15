package com.harsh.hospital.mapper;

import com.harsh.hospital.domain.Patient;
import com.harsh.hospital.dto.PatientDtos.PatientRequest;
import com.harsh.hospital.dto.PatientDtos.PatientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientResponse toResponse(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Patient toEntity(PatientRequest request);
}
