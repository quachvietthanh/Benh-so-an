INSERT INTO medicines(id,name,category,unit,min_stock,active,created_at) VALUES
(UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000001'),'Aspirin 81mg','Tim mạch','viên',20,TRUE,CURRENT_TIMESTAMP),
(UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000002'),'Ibuprofen 400mg','Giảm đau - kháng viêm','viên',20,TRUE,CURRENT_TIMESTAMP),
(UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000003'),'Paracetamol 500mg','Giảm đau - hạ sốt','viên',30,TRUE,CURRENT_TIMESTAMP),
(UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000004'),'Metformin 500mg','Tiểu đường','viên',30,TRUE,CURRENT_TIMESTAMP);

INSERT INTO drug_interactions(id,medicine_a_id,medicine_b_id,severity,description) VALUES
(UUID_TO_BIN('bbbbbbbb-0000-0000-0000-000000000001'),UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000001'),UUID_TO_BIN('aaaaaaaa-0000-0000-0000-000000000002'),'HIGH','Dùng Aspirin cùng Ibuprofen làm tăng nguy cơ xuất huyết tiêu hóa và có thể giảm tác dụng bảo vệ tim mạch của Aspirin.');
