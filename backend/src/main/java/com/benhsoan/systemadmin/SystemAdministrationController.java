package com.benhsoan.systemadmin;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/system") @RequiredArgsConstructor
public class SystemAdministrationController {
 private final JdbcTemplate db; private final ObjectMapper json;
 public record ServiceRequest(@NotBlank String serviceCode,@NotBlank String name,@NotNull @DecimalMin("0") BigDecimal price,@NotNull LocalDate effectiveFrom,boolean active){}
 public record ClinicRequest(@NotBlank String clinicName,String address,String phone,@NotNull LocalTime openingTime,@NotNull LocalTime closingTime,@NotEmpty List<String> examinationRooms){}
 @GetMapping("/services") public List<Map<String,Object>> services(){return db.queryForList("SELECT BIN_TO_UUID(id) id,service_code serviceCode,name,price,effective_from effectiveFrom,active FROM clinic_services ORDER BY service_code,effective_from DESC");}
 @PostMapping("/services") public Map<String,Object> createService(@Valid @RequestBody ServiceRequest r){UUID id=UUID.randomUUID();db.update("INSERT INTO clinic_services VALUES(UUID_TO_BIN(?),?,?,?,?,?,CURRENT_TIMESTAMP)",id.toString(),r.serviceCode(),r.name(),r.price(),r.effectiveFrom(),r.active());return Map.of("id",id);}
 @PutMapping("/services/{id}") public void updateService(@PathVariable UUID id,@Valid @RequestBody ServiceRequest r){db.update("UPDATE clinic_services SET service_code=?,name=?,price=?,effective_from=?,active=? WHERE id=UUID_TO_BIN(?)",r.serviceCode(),r.name(),r.price(),r.effectiveFrom(),r.active(),id.toString());}
 @GetMapping("/clinic") public Map<String,Object> clinic() throws Exception {Map<String,Object> row=db.queryForMap("SELECT clinic_name clinicName,address,phone,opening_time openingTime,closing_time closingTime,examination_rooms examinationRooms,updated_at updatedAt FROM clinic_configuration WHERE id=1");row.put("examinationRooms",json.readValue(String.valueOf(row.get("examinationRooms")),new TypeReference<List<String>>(){}));return row;}
 @PutMapping("/clinic") public void updateClinic(@Valid @RequestBody ClinicRequest r) throws Exception {if(!r.closingTime().isAfter(r.openingTime()))throw new IllegalArgumentException("Giờ đóng cửa phải sau giờ mở cửa");db.update("UPDATE clinic_configuration SET clinic_name=?,address=?,phone=?,opening_time=?,closing_time=?,examination_rooms=CAST(? AS JSON),updated_at=CURRENT_TIMESTAMP WHERE id=1",r.clinicName(),r.address(),r.phone(),r.openingTime(),r.closingTime(),json.writeValueAsString(r.examinationRooms()));}
}
