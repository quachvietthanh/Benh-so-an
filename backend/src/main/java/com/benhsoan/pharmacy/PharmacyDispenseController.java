package com.benhsoan.pharmacy;

import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RestController @RequiredArgsConstructor
public class PharmacyDispenseController {
  private final JdbcTemplate db; private final ObjectMapper json;

  @PostMapping("/pharmacy/prescriptions/{id}/dispense") @Transactional
  public Map<String,Object> dispense(@PathVariable UUID id) throws Exception {
    Map<String,Object> prescription=db.queryForMap("SELECT status,items FROM prescriptions WHERE id=UUID_TO_BIN(?) FOR UPDATE",id.toString());
    if(!"PENDING_DISPENSING".equals(prescription.get("status"))) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Đơn không ở trạng thái chờ cấp phát");
    PharmacyController.Item[] items=json.readValue(String.valueOf(prescription.get("items")),PharmacyController.Item[].class);
    for(var item:items){int remaining=item.quantity();List<Map<String,Object>> batches=db.queryForList("SELECT BIN_TO_UUID(id) id,quantity FROM medicine_batches WHERE medicine_id=UUID_TO_BIN(?) AND quantity>0 AND expiry_date>=CURDATE() ORDER BY expiry_date,created_at FOR UPDATE",item.medicineId().toString());int available=batches.stream().mapToInt(b->((Number)b.get("quantity")).intValue()).sum();if(available<remaining)throw new ResponseStatusException(HttpStatus.CONFLICT,"Tồn kho không đủ để cấp đơn");for(Map<String,Object> batch:batches){if(remaining==0)break;int take=Math.min(remaining,((Number)batch.get("quantity")).intValue());db.update("UPDATE medicine_batches SET quantity=quantity-? WHERE id=UUID_TO_BIN(?)",take,batch.get("id"));db.update("INSERT INTO inventory_transactions VALUES(UUID_TO_BIN(UUID()),UUID_TO_BIN(?),UUID_TO_BIN(?),'DISPENSE',?,UUID_TO_BIN(?),CURRENT_TIMESTAMP)",item.medicineId().toString(),batch.get("id"),-take,id.toString());remaining-=take;}}
    db.update("UPDATE prescriptions SET status='DISPENSED',dispensed_at=CURRENT_TIMESTAMP,updated_at=CURRENT_TIMESTAMP WHERE id=UUID_TO_BIN(?)",id.toString());
    return Map.of("id",id,"status","DISPENSED");
  }
}
