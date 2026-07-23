package com.benhsoan.adapter.inbound.rest.mapper;

import org.springframework.stereotype.Component;

import com.benhsoan.adapter.inbound.rest.request.appointment.CancelAppointmentRequest;
import com.benhsoan.adapter.inbound.rest.request.appointment.CreateAppointmentRequest;
import com.benhsoan.adapter.inbound.rest.response.appointment.AppointmentResponse;
import com.benhsoan.port.dto.command.appointment.CancelAppointmentCommand;
import com.benhsoan.port.dto.command.appointment.CreateAppointmentCommand;
import com.benhsoan.port.dto.result.AppointmentResult;

@Component
public class AppointmentRestMapper {

    public CreateAppointmentCommand toCommand(
            CreateAppointmentRequest request
    ) {

        return CreateAppointmentCommand.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .reason(request.reason())
                .build();

    }

    public AppointmentResponse toResponse(
            AppointmentResult result
    ) {

        return AppointmentResponse.builder()
                .id(result.id())
                .appointmentCode(result.appointmentCode())
                .patientId(result.patientId())
                .doctorId(result.doctorId())
                .startTime(result.startTime())
                .endTime(result.endTime())
                .status(result.status())
                .reason(result.reason())
                .cancelReason(result.cancelReason())
                .checkedInAt(result.checkedInAt())
                .completedAt(result.completedAt())
                .createdAt(result.createdAt())
                .build();

    }
    public CancelAppointmentCommand toCommand( CancelAppointmentRequest request) {
        return CancelAppointmentCommand.builder()
            .cancelReason(request.cancelReason())
            .build();

        }
}