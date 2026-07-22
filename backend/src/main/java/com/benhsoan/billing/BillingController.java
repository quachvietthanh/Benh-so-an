package com.benhsoan.billing;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.benhsoan.port.outbound.security.CurrentUserProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/invoices") @RequiredArgsConstructor
public class BillingController {
 private final JdbcTemplate db; private final ObjectMapper json; private final CurrentUserProvider currentUser;
 public record PaymentRequest(@NotNull UUID prescriptionId,@DecimalMin("0") BigDecimal examFee,@NotBlank String paymentMethod){}
 public record AdjustmentRequest(@NotNull BigDecimal adjustmentAmount,@NotBlank String reason){}

 @GetMapping public List<Map<String,Object>> all(){return db.queryForList("SELECT BIN_TO_UUID(i.id) id,i.invoice_code invoiceCode,BIN_TO_UUID(i.original_invoice_id) originalInvoiceId,i.invoice_type invoiceType,p.full_name patientName,i.line_items lineItems,i.total_amount totalAmount,i.adjustment_reason adjustmentReason,i.issued_at issuedAt FROM invoices i JOIN patients p ON p.id=i.patient_id ORDER BY i.issued_at DESC");}
 @GetMapping("/payable") public List<Map<String,Object>> payable(){return db.queryForList("SELECT BIN_TO_UUID(pr.id) prescriptionId,pr.prescription_code prescriptionCode,pt.full_name patientName,pr.items FROM prescriptions pr JOIN electronic_medical_records mr ON mr.id=pr.medical_record_id JOIN patients pt ON pt.id=mr.patient_id LEFT JOIN payments py ON py.prescription_id=pr.id WHERE pr.status='DISPENSED' AND py.id IS NULL ORDER BY pr.dispensed_at");}

 @PostMapping("/payments") @Transactional public Map<String,Object> pay(@Valid @RequestBody PaymentRequest r) throws Exception {
  Map<String,Object> source;try{source=db.queryForMap("SELECT pr.status,pr.items,BIN_TO_UUID(mr.patient_id) patientId FROM prescriptions pr JOIN electronic_medical_records mr ON mr.id=pr.medical_record_id WHERE pr.id=UUID_TO_BIN(?) FOR UPDATE",r.prescriptionId().toString());}catch(Exception e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy đơn thuốc");}
  if(!"DISPENSED".equals(source.get("status")))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ thu phí khi thuốc đã được cấp phát");
  Integer existed=db.queryForObject("SELECT COUNT(*) FROM payments WHERE prescription_id=UUID_TO_BIN(?)",Integer.class,r.prescriptionId().toString());if(existed!=null&&existed>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Lượt khám đã được thanh toán");
  List<Map<String,Object>> items=json.readValue(String.valueOf(source.get("items")),new TypeReference<>(){});BigDecimal medicineFee=BigDecimal.ZERO;List<Map<String,Object>> lines=new ArrayList<>();
  for(Map<String,Object> item:items){String medicineId=String.valueOf(item.get("medicineId"));int quantity=((Number)item.get("quantity")).intValue();Map<String,Object> med=db.queryForMap("SELECT name,sale_price FROM medicines WHERE id=UUID_TO_BIN(?)",medicineId);BigDecimal price=(BigDecimal)med.get("sale_price");BigDecimal amount=price.multiply(BigDecimal.valueOf(quantity));medicineFee=medicineFee.add(amount);lines.add(Map.of("description",med.get("name"),"quantity",quantity,"unitPrice",price,"amount",amount));}
  BigDecimal examFee=Objects.requireNonNullElse(r.examFee(),BigDecimal.ZERO),total=examFee.add(medicineFee);lines.add(0,Map.of("description","Phí khám bệnh","quantity",1,"unitPrice",examFee,"amount",examFee));UUID paymentId=UUID.randomUUID(),invoiceId=UUID.randomUUID();String paymentCode="TT-"+System.currentTimeMillis(),invoiceCode="HD-"+System.currentTimeMillis(),patientId=String.valueOf(source.get("patientId")),userId=currentUser.getCurrentUserId().toString();
  db.update("INSERT INTO payments VALUES(UUID_TO_BIN(?),?,UUID_TO_BIN(?),UUID_TO_BIN(?),?,?,?,?,UUID_TO_BIN(?),CURRENT_TIMESTAMP)",paymentId.toString(),paymentCode,r.prescriptionId().toString(),patientId,examFee,medicineFee,total,r.paymentMethod(),userId);
  db.update("INSERT INTO invoices VALUES(UUID_TO_BIN(?),?,UUID_TO_BIN(?),UUID_TO_BIN(?),NULL,'ORIGINAL',CAST(? AS JSON),?,NULL,UUID_TO_BIN(?),CURRENT_TIMESTAMP)",invoiceId.toString(),invoiceCode,paymentId.toString(),patientId,json.writeValueAsString(lines),total,userId);
  return Map.of("paymentCode",paymentCode,"invoiceCode",invoiceCode,"totalAmount",total);
 }

 @PostMapping("/{id}/adjustments") @Transactional public Map<String,Object> adjust(@PathVariable UUID id,@Valid @RequestBody AdjustmentRequest r) throws Exception {Map<String,Object> original=db.queryForMap("SELECT BIN_TO_UUID(patient_id) patientId,line_items,total_amount,invoice_type FROM invoices WHERE id=UUID_TO_BIN(?)",id.toString());if(!"ORIGINAL".equals(original.get("invoice_type")))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ điều chỉnh hóa đơn gốc");UUID adjustmentId=UUID.randomUUID();String code="DC-"+System.currentTimeMillis();List<Map<String,Object>> lines=List.of(Map.of("description","Điều chỉnh hóa đơn","quantity",1,"unitPrice",r.adjustmentAmount(),"amount",r.adjustmentAmount()));db.update("INSERT INTO invoices VALUES(UUID_TO_BIN(?),?,NULL,UUID_TO_BIN(?),UUID_TO_BIN(?),'ADJUSTMENT',CAST(? AS JSON),?,?,UUID_TO_BIN(?),CURRENT_TIMESTAMP)",adjustmentId.toString(),code,original.get("patientId"),id.toString(),json.writeValueAsString(lines),r.adjustmentAmount(),r.reason(),currentUser.getCurrentUserId().toString());return Map.of("id",adjustmentId,"invoiceCode",code,"originalInvoiceId",id);}
}
