package com.benhsoan.publiclookup;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.benhsoan.config.SecurityConfig;
import com.benhsoan.infrastructure.authSecurity.JwtAuthenticationFilter;
import com.benhsoan.port.outbound.authSecurity.JwtTokenPort;

@WebMvcTest(PublicAppointmentLookupController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@DisplayName("Public appointment lookup controller tests")
class PublicAppointmentLookupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicAppointmentLookupService service;

    @MockitoBean
    private JwtTokenPort jwtTokenPort;

    @Test
    @DisplayName("Anonymous lookup returns only the minimal JSON contract and disables caching")
    void returnsMinimalNonCachedResponseWithoutAuthentication() throws Exception {
        PublicAppointmentLookupRequest request = new PublicAppointmentLookupRequest(
                "LH-1234567890",
                LocalDate.of(1990, 5, 12));
        given(service.lookup(request)).willReturn(PublicAppointmentLookupResponse.notFound());

        mockMvc.perform(post("/public/appointments/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentCode": "LH-1234567890",
                                  "dateOfBirth": "1990-05-12"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(header().string(
                        HttpHeaders.CACHE_CONTROL,
                        containsString("no-store")))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.matched").value(false))
                .andExpect(jsonPath("$.careState").value(nullValue()))
                .andExpect(jsonPath("$.scheduledAt").value(nullValue()))
                .andExpect(jsonPath("$.patientName").doesNotExist())
                .andExpect(jsonPath("$.dateOfBirth").doesNotExist());

        verify(service).lookup(request);
        verifyNoInteractions(jwtTokenPort);
    }

    @Test
    @DisplayName("A matched lookup serializes its coarse state and scheduled time")
    void serializesMatchedResponse() throws Exception {
        PublicAppointmentLookupRequest request = new PublicAppointmentLookupRequest(
                "LH-1234567890",
                LocalDate.of(1990, 5, 12));
        given(service.lookup(request)).willReturn(new PublicAppointmentLookupResponse(
                true,
                PublicAppointmentLookupResponse.CareState.SCHEDULED,
                Instant.parse("2026-07-27T02:00:00Z")));

        mockMvc.perform(post("/public/appointments/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentCode": "LH-1234567890",
                                  "dateOfBirth": "1990-05-12"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(jsonPath("$.matched").value(true))
                .andExpect(jsonPath("$.careState").value("SCHEDULED"))
                .andExpect(jsonPath("$.scheduledAt").value("2026-07-27T02:00:00Z"));

        verify(service).lookup(request);
        verifyNoInteractions(jwtTokenPort);
    }

    @Test
    @DisplayName("Malformed identifying data is rejected before the service runs")
    void rejectsInvalidPayload() throws Exception {
        mockMvc.perform(post("/public/appointments/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "appointmentCode": "?",
                                  "dateOfBirth": "2999-01-01"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(unauthenticated())
                .andExpect(header().string(
                        HttpHeaders.CACHE_CONTROL,
                        containsString("no-store")))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.errors.appointmentCode").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists());

        verifyNoInteractions(service, jwtTokenPort);
    }

    @Test
    @DisplayName("Only the explicit lookup endpoint is public; unmatched routes require authentication")
    void keepsUnmatchedRoutesProtected() throws Exception {
        mockMvc.perform(get("/private-route-probe"))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());

        verifyNoInteractions(service, jwtTokenPort);
    }
}
