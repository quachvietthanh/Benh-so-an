package com.benhsoan.adapter.inbound.rest.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.benhsoan.adapter.inbound.rest.mapper.AppointmentRestMapper;
import com.benhsoan.adapter.inbound.rest.request.appointment.CancelAppointmentRequest;
import com.benhsoan.adapter.inbound.rest.request.appointment.CreateAppointmentRequest;
import com.benhsoan.adapter.inbound.rest.response.appointment.AppointmentResponse;
import com.benhsoan.port.dto.result.AppointmentResult;
import com.benhsoan.port.inbound.appointment.CancelAppointmentUseCase;
import com.benhsoan.port.inbound.appointment.CreateAppointmentUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    private final AppointmentRestMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create( @Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResult result =
                createAppointmentUseCase.create(
                        mapper.toCommand(request)
                );
        return mapper.toResponse(result);

    }

    @PatchMapping("/cancel/{id}")
    public AppointmentResponse cancel( 
        @PathVariable UUID id,
        @Valid
        @RequestBody
        CancelAppointmentRequest request ) {
            
            AppointmentResult result =
            cancelAppointmentUseCase.cancel(
                    id,
                    mapper.toCommand(request)
            );
        return mapper.toResponse(result);
    }

}