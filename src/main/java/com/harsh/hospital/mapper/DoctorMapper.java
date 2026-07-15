package com.harsh.hospital.mapper;

import com.harsh.hospital.domain.Doctor;
import com.harsh.hospital.dto.DoctorDtos.DoctorRequest;
import com.harsh.hospital.dto.DoctorDtos.DoctorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorResponse toResponse(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Doctor toEntity(DoctorRequest request);
}
