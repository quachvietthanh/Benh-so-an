package com.benhsoan.pharmacy;

import java.time.LocalDate;
import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.benhsoan.port.outbound.security.CurrentUserProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

@RestController @RequiredArgsConstructor
public class PharmacyController {
  private final JdbcTemplate db; private final ObjectMapper json; private final CurrentUserProvider currentUser;
  public record MedicineRequest(@NotBlank String name,String category,@NotBlank String unit,@Min(0) int minStock,boolean active){}
  public record BatchRequest(@NotNull UUID medicineId,@NotBlank String lotNumber,@Future LocalDate expiryDate,@Min(1) int quantity,@DecimalMin("0") double unitCost){}
  public record Item(@NotNull UUID medicineId,@Min(1) int quantity,@NotBlank String dosage){}
  public record PrescriptionRequest(@NotNull UUID medicalRecordId,@NotEmpty List<Item> items,String overrideReason){}
  public record AdjustmentRequest(@NotEmpty List<Item> items,@NotBlank String changeReason,String overrideReason){}

  @GetMapping("/pharmacy/medicines") public List<Map<String,Object>> medicines(){return db.queryForList("SELECT BIN_TO_UUID(m.id) id,m.name,m.category,m.unit,m.min_stock minStock,m.active,COALESCE(SUM(CASE WHEN b.expiry_date>=CURDATE() THEN b.quantity ELSE 0 END),0) stock FROM medicines m LEFT JOIN medicine_batches b ON b.medicine_id=m.id GROUP BY m.id ORDER BY m.name");}
  @PostMapping("/pharmacy/medicines") public Map<String,Object> addMedicine(@Valid @RequestBody MedicineRequest r){UUID id=UUID.randomUUID();db.update("INSERT INTO medicines VALUES(UUID_TO_BIN(?),?,?,?,?,?,CURRENT_TIMESTAMP)",id.toString(),r.name(),r.category(),r.unit(),r.minStock(),r.active());return Map.of("id",id,"name",r.name());}
  @PutMapping("/pharmacy/medicines/{id}") public void updateMedicine(@PathVariable UUID id,@Valid @RequestBody MedicineRequest r){db.update("UPDATE medicines SET name=?,category=?,unit=?,min_stock=?,active=? WHERE id=UUID_TO_BIN(?)",r.name(),r.category(),r.unit(),r.minStock(),r.active(),id.toString());}
  @GetMapping("/pharmacy/batches") public List<Map<String,Object>> batches(){return db.queryForList("SELECT BIN_TO_UUID(b.id) id,BIN_TO_UUID(b.medicine_id) medicineId,m.name medicineName,b.lot_number lotNumber,b.expiry_date expiryDate,b.quantity,b.unit_cost unitCost FROM medicine_batches b JOIN medicines m ON m.id=b.medicine_id ORDER BY b.expiry_date");}
  @PostMapping("/pharmacy/batches") @Transactional public void receive(@Valid @RequestBody BatchRequest r){UUID id=UUID.randomUUID();db.update("INSERT INTO medicine_batches VALUES(UUID_TO_BIN(?),UUID_TO_BIN(?),?,?,?,?,CURRENT_TIMESTAMP)",id.toString(),r.medicineId().toString(),r.lotNumber(),r.expiryDate(),r.quantity(),r.unitCost());db.update("INSERT INTO inventory_transactions VALUES(UUID_TO_BIN(UUID()),UUID_TO_BIN(?),UUID_TO_BIN(?),'RECEIPT',?,NULL,CURRENT_TIMESTAMP)",r.medicineId().toString(),id.toString(),r.quantity());}
  @GetMapping("/prescriptions") public List<Map<String,Object>> prescriptions(){return db.queryForList("SELECT BIN_TO_UUID(p.id) id,p.prescription_code prescriptionCode,BIN_TO_UUID(p.medical_record_id) medicalRecordId,pt.full_name patientName,p.items,p.warnings,p.override_reason overrideReason,p.status,p.created_at createdAt,p.dispensed_at dispensedAt FROM prescriptions p JOIN electronic_medical_records mr ON mr.id=p.medical_record_id JOIN patients pt ON pt.id=mr.patient_id ORDER BY p.created_at DESC");}
  @PostMapping("/prescriptions/interactions") public List<Map<String,Object>> interactions(@RequestBody List<UUID> medicineIds){List<Map<String,Object>> result=new ArrayList<>();for(int i=0;i<medicineIds.size();i++)for(int j=i+1;j<medicineIds.size();j++)result.addAll(db.queryForList("SELECT severity,description FROM drug_interactions WHERE (medicine_a_id=UUID_TO_BIN(?) AND medicine_b_id=UUID_TO_BIN(?)) OR (medicine_a_id=UUID_TO_BIN(?) AND medicine_b_id=UUID_TO_BIN(?))",medicineIds.get(i).toString(),medicineIds.get(j).toString(),medicineIds.get(j).toString(),medicineIds.get(i).toString()));return result;}
  @PostMapping("/prescriptions") @Transactional public Map<String,Object> prescribe(@Valid @RequestBody PrescriptionRequest r) throws Exception {List<Map<String,Object>> warnings=new ArrayList<>();for(int i=0;i<r.items().size();i++)for(int j=i+1;j<r.items().size();j++)warnings.addAll(db.queryForList("SELECT severity,description FROM drug_interactions WHERE (medicine_a_id=UUID_TO_BIN(?) AND medicine_b_id=UUID_TO_BIN(?)) OR (medicine_a_id=UUID_TO_BIN(?) AND medicine_b_id=UUID_TO_BIN(?))",r.items().get(i).medicineId().toString(),r.items().get(j).medicineId().toString(),r.items().get(j).medicineId().toString(),r.items().get(i).medicineId().toString()));if(!warnings.isEmpty()&&(r.overrideReason()==null||r.overrideReason().isBlank()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Có tương tác thuốc; phải ghi lý do chuyên môn nếu tiếp tục");UUID id=UUID.randomUUID();String code="DT-"+System.currentTimeMillis();db.update("INSERT INTO prescriptions VALUES(UUID_TO_BIN(?),?,UUID_TO_BIN(?),UUID_TO_BIN(?),CAST(? AS JSON),CAST(? AS JSON),?,'PENDING_DISPENSING',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,NULL)",id.toString(),code,r.medicalRecordId().toString(),currentUser.getCurrentUserId().toString(),json.writeValueAsString(r.items()),json.writeValueAsString(warnings),r.overrideReason());return Map.of("id",id,"prescriptionCode",code,"warnings",warnings);}
  @PutMapping("/prescriptions/{id}") @Transactional public Map<String,Object> adjust(@PathVariable UUID id,@Valid @RequestBody AdjustmentRequest r) throws Exception {Map<String,Object> old=db.queryForMap("SELECT status,items FROM prescriptions WHERE id=UUID_TO_BIN(?)",id.toString());if(!"PENDING_DISPENSING".equals(old.get("status")))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ sửa được đơn đang chờ cấp phát");String next=json.writeValueAsString(r.items());db.update("INSERT INTO prescription_revisions VALUES(UUID_TO_BIN(UUID()),UUID_TO_BIN(?),UUID_TO_BIN(?),CAST(? AS JSON),CAST(? AS JSON),?,CURRENT_TIMESTAMP)",id.toString(),currentUser.getCurrentUserId().toString(),String.valueOf(old.get("items")),next,r.changeReason());db.update("UPDATE prescriptions SET items=CAST(? AS JSON),override_reason=?,updated_at=CURRENT_TIMESTAMP WHERE id=UUID_TO_BIN(?)",next,r.overrideReason(),id.toString());return Map.of("id",id,"status",old.get("status"));}
}
