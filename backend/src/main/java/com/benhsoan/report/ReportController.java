package com.benhsoan.report;

import java.time.LocalDate;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/reports") @RequiredArgsConstructor
public class ReportController {
 private final JdbcTemplate db;
 private String start(LocalDate value){return value.atStartOfDay().toString();}
 private String end(LocalDate value){return value.plusDays(1).atStartOfDay().toString();}

 @GetMapping("/summary") public Map<String,Object> summary(@RequestParam LocalDate from,@RequestParam LocalDate to){
  Integer visits=db.queryForObject("SELECT COUNT(*) FROM electronic_medical_records WHERE created_at>=? AND created_at<?",Integer.class,start(from),end(to));
  Number revenue=db.queryForObject("SELECT COALESCE(SUM(total_amount),0) FROM invoices WHERE invoice_type='ORIGINAL' AND issued_at>=? AND issued_at<?",Number.class,start(from),end(to));
  return Map.of("visitCount",visits==null?0:visits,"revenue",revenue==null?0:revenue);
 }
 @GetMapping("/visits-timeline") public List<Map<String,Object>> timeline(@RequestParam LocalDate from,@RequestParam LocalDate to){return db.queryForList("SELECT DATE(created_at) reportDate,COUNT(*) visitCount FROM electronic_medical_records WHERE created_at>=? AND created_at<? GROUP BY DATE(created_at) ORDER BY reportDate",start(from),end(to));}
 @GetMapping("/top-medicines") public List<Map<String,Object>> topMedicines(@RequestParam LocalDate from,@RequestParam LocalDate to){return db.queryForList("SELECT m.name,SUM(-t.quantity) dispensedQuantity FROM inventory_transactions t JOIN medicines m ON m.id=t.medicine_id WHERE t.transaction_type='DISPENSE' AND t.created_at>=? AND t.created_at<? GROUP BY m.id,m.name ORDER BY dispensedQuantity DESC LIMIT 10",start(from),end(to));}
 @GetMapping("/audit-logs") public List<Map<String,Object>> audit(@RequestParam(required=false) String user,@RequestParam(required=false) String patient,@RequestParam LocalDate from,@RequestParam LocalDate to){return db.queryForList("SELECT BIN_TO_UUID(a.id) id,u.full_name userName,p.full_name patientName,r.record_code recordCode,a.action,a.accessed_at accessedAt FROM emr_audit_logs a JOIN users u ON u.id=a.user_id JOIN patients p ON p.id=a.patient_id JOIN electronic_medical_records r ON r.id=a.record_id WHERE a.accessed_at>=? AND a.accessed_at<? AND (? IS NULL OR u.full_name LIKE CONCAT('%',?,'%')) AND (? IS NULL OR p.full_name LIKE CONCAT('%',?,'%')) ORDER BY a.accessed_at DESC",start(from),end(to),user,user,patient,patient);}
 @GetMapping("/dashboard") public Map<String,Object> dashboard(){Map<String,Object> result=new LinkedHashMap<>();result.put("totalPatients",db.queryForObject("SELECT COUNT(*) FROM patients WHERE active=TRUE",Integer.class));result.put("totalRecords",db.queryForObject("SELECT COUNT(*) FROM electronic_medical_records",Integer.class));result.put("activeQueue",db.queryForObject("SELECT COUNT(*) FROM appointments WHERE status='CHECKED_IN'",Integer.class));result.put("revenueToday",db.queryForObject("SELECT COALESCE(SUM(total_amount),0) FROM invoices WHERE invoice_type='ORIGINAL' AND DATE(issued_at)=CURDATE()",Number.class));return result;}
 @GetMapping(value="/export",produces="text/csv") public ResponseEntity<byte[]> export(@RequestParam LocalDate from,@RequestParam LocalDate to){List<Map<String,Object>> rows=db.queryForList("SELECT DATE(issued_at) reportDate,COUNT(*) invoiceCount,COALESCE(SUM(total_amount),0) revenue FROM invoices WHERE invoice_type='ORIGINAL' AND issued_at>=? AND issued_at<? GROUP BY DATE(issued_at) ORDER BY reportDate",start(from),end(to));StringBuilder csv=new StringBuilder("Ngày,Số hóa đơn,Doanh thu\n");for(Map<String,Object> row:rows)csv.append(row.get("reportDate")).append(',').append(row.get("invoiceCount")).append(',').append(row.get("revenue")).append('\n');byte[] data=("\uFEFF"+csv).getBytes(StandardCharsets.UTF_8);return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=bao-cao-"+from+"-"+to+".csv").contentType(MediaType.parseMediaType("text/csv;charset=UTF-8")).body(data);}
}
