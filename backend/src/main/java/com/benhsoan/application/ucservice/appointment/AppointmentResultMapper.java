package com.benhsoan.application.ucservice.appointment;

import org.springframework.stereotype.Component;

import com.benhsoan.domain.appointment.Appointment;
import com.benhsoan.port.dto.result.AppointmentResult;

@Component
public class AppointmentResultMapper {

    public AppointmentResult toResult(
            Appointment appointment
    ) {

        return new AppointmentResult(

                appointment.getId(),

                appointment.getAppointmentCode(),

                appointment.getPatientId(),

                appointment.getDoctorId(),

                appointment.getStartTime(),

                appointment.getEndTime(),

                appointment.getStatus(),

                appointment.getReason(),

                appointment.getCancelReason(),

                appointment.getCheckedInAt(),

                appointment.getCompletedAt(),

                appointment.getCreatedBy(),

                appointment.getCreatedAt()
        );
    }

}