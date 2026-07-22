param(
    [Parameter(Mandatory = $true)]
    [string]$TemplatePath,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Expand-TestText([string]$Value) {
    if ($null -eq $Value) { return '' }
    return $Value -replace '\\n', [Environment]::NewLine
}

function Escape-Xml([object]$Value) {
    if ($null -eq $Value) { return '' }
    $escaped = [System.Security.SecurityElement]::Escape([string]$Value)
    if ($null -eq $escaped) { return '' }
    return $escaped
}

function New-TextCell([string]$Ref, [int]$Style, [object]$Value) {
    $text = Escape-Xml $Value
    return ('<c r="{0}" s="{1}" t="inlineStr"><is><t xml:space="preserve">{2}</t></is></c>' -f $Ref, $Style, $text)
}

function New-NumberCell([string]$Ref, [int]$Style, [int]$Value) {
    return ('<c r="{0}" s="{1}"><v>{2}</v></c>' -f $Ref, $Style, $Value)
}

function Get-ZipText([System.IO.Compression.ZipArchive]$Zip, [string]$Name) {
    $entry = $Zip.GetEntry($Name)
    if ($null -eq $entry) { throw "Missing XLSX entry: $Name" }
    $reader = [System.IO.StreamReader]::new($entry.Open(), [System.Text.Encoding]::UTF8)
    try { return $reader.ReadToEnd() }
    finally { $reader.Dispose() }
}

function Set-ZipText([System.IO.Compression.ZipArchive]$Zip, [string]$Name, [string]$Content) {
    $old = $Zip.GetEntry($Name)
    if ($null -ne $old) { $old.Delete() }
    $entry = $Zip.CreateEntry($Name, [System.IO.Compression.CompressionLevel]::Optimal)
    $encoding = [System.Text.UTF8Encoding]::new($false)
    $writer = [System.IO.StreamWriter]::new($entry.Open(), $encoding)
    try { $writer.Write($Content) }
    finally { $writer.Dispose() }
}

function Convert-XmlToString([xml]$Document) {
    $settings = [System.Xml.XmlWriterSettings]::new()
    $settings.Encoding = [System.Text.UTF8Encoding]::new($false)
    $settings.Indent = $false
    $settings.OmitXmlDeclaration = $false
    $buffer = [System.IO.MemoryStream]::new()
    $writer = [System.Xml.XmlWriter]::Create($buffer, $settings)
    try {
        $Document.Save($writer)
        $writer.Flush()
        return [System.Text.Encoding]::UTF8.GetString($buffer.ToArray())
    }
    finally {
        $writer.Dispose()
        $buffer.Dispose()
    }
}

$caseSource = @'
ACC-UI-001§NCL-01-CN-003§NCL-01-CN-003-CV-05§Phân quyền giao diện§UI§Người chưa đăng nhập truy cập trực tiếp trang quản lý tài khoản§P0§Đã xóa token và user khỏi localStorage§URL /users§1. Mở trình duyệt ở trạng thái chưa đăng nhập.\n2. Truy cập /users.§Chuyển đến /login; không hiển thị danh sách hoặc dữ liệu tài khoản.§AppRoutes.jsx:21-25,65.
ACC-UI-002§NCL-01-CN-003§NCL-01-CN-003-CV-05§Phân quyền giao diện§UI/E2E§Người dùng không phải Admin truy cập trang quản lý tài khoản§P0§Đăng nhập lần lượt bằng DOCTOR, NURSE, RECEPTIONIST, PHARMACIST§Các tài khoản hợp lệ của 4 vai trò§1. Đăng nhập từng vai trò.\n2. Kiểm tra menu.\n3. Nhập trực tiếp /users.§Menu quản lý tài khoản bị ẩn; URL trực tiếp hiển thị thông báo không có quyền; không lộ dữ liệu.§AppRoutes.jsx:65; mockDataService.js:35.
ACC-UI-003§NCL-01-CN-002§NCL-01-CN-002-CV-05§Danh sách tài khoản§UI/Integration§Admin mở danh sách và dữ liệu phải đến từ backend§P0§Admin đã đăng nhập; backend có ít nhất 2 user§ADMIN_TOKEN§1. Mở /users.\n2. Đối chiếu các dòng với GET /api/v1/users.§Hiển thị dữ liệu thật gồm username, họ tên, email, phone, role, trạng thái; không hiển thị password; dữ liệu thay đổi theo backend.§BLOCKED G-01/G-05: route hiện render UsersPage dùng mock và response thiếu active. AppRoutes.jsx:65; UsersPage.jsx:3-8.
ACC-UI-004§NCL-01-CN-002§NCL-01-CN-002-CV-05§Danh sách tài khoản§UI§Loading, tải lại, danh sách rỗng và lỗi tải danh sách§P1§Có thể mock lần lượt pending, 200 [], 500§GET /api/v1/users§1. Mở trang khi request đang pending.\n2. Bấm Tải lại.\n3. Trả [] rồi trả 500 ở hai lần chạy.§Có spinner và chống gửi lặp; tải lại gọi đúng một request; [] cho empty state; 500 hiện lỗi dễ hiểu và trang không crash.§BLOCKED G-01/G-03. UserList.jsx:16-34,107-130 là dead code.
ACC-UI-005§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo tài khoản§UI§Nút Tạo tài khoản mở form đầy đủ và validation tại trường§P0§Admin ở trang /users§Form: username, password, fullName, email, phone, roleName§1. Bấm Tạo tài khoản.\n2. Để trống các trường bắt buộc.\n3. Nhập email sai.\n4. Gửi form.§Form mở đúng trường; lỗi gắn đúng trường; không gửi API khi client validation thất bại; giữ dữ liệu hợp lệ đã nhập.§BLOCKED G-02: nút hiện không có handler/form. UsersPage.jsx:21.
ACC-UI-006§NCL-01-CN-002§NCL-01-CN-002-CV-05§CRUD tài khoản§UI§Các thao tác sửa, xóa, kích hoạt/vô hiệu hóa được nối đúng user§P0§Admin; danh sách có user active và inactive§Hai USER_ID khác nhau§1. Mở menu thao tác từng dòng.\n2. Chọn Sửa, Xóa, Vô hiệu hóa/Kích hoạt.\n3. Quan sát request và tên user trong confirm.§Mỗi thao tác dùng đúng id; có confirm cho thao tác phá hủy; nút có loading; thành công cập nhật đúng dòng, lỗi giữ trạng thái cũ.§BLOCKED G-02/G-03/G-16: trang đang dùng mock; chưa có handler CRUD.
ACC-UI-007§NCL-01-CN-003§NCL-01-CN-003-CV-05§Chống giả mạo quyền§Security/E2E§Sửa localStorage thành admin nhưng dùng token non-admin§P0§Đăng nhập DOCTOR, sau đó sửa user.roles thành ['admin']§DOCTOR_TOKEN không đổi§1. Sửa localStorage.\n2. Reload /users.\n3. Theo dõi request API.§Backend trả 403; UI không hiển thị dữ liệu và báo không có quyền. Quyền phía client không thể vượt quyền server.§KNOWN FAIL G-10: trang mock có thể lộ dữ liệu dù API sẽ chặn. AuthContext.jsx:15-18; SecurityConfig.java:73-76.
ACC-UI-008§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo trùng/thiếu vai trò§UI/E2E§Hiển thị đúng lỗi tạo trùng và thiếu vai trò theo task.xlsx§P0§Admin; đã có username/email của user A§Lần 1 trùng username; lần 2 trùng email; lần 3 bỏ role§1. Mở form tạo.\n2. Gửi lần lượt 3 bộ dữ liệu.\n3. Đếm dòng sau mỗi lần.§Trùng username/email hiển thị lỗi server và không thêm dòng; thiếu role hiển thị validation tại trường và không gửi request.§BLOCKED G-02; bao phủ trực tiếp NCL-01-CN-002-CV-05.
ACC-SEC-001§NCL-01-CN-003§NCL-01-CN-003-CV-05§Phân quyền API§API/Security§Admin được gọi tất cả API quản lý tài khoản§P0§ADMIN_TOKEN hợp lệ§POST/GET /users; GET/PUT/DELETE /users/{id}; PATCH activate/deactivate§Gọi từng endpoint với dữ liệu hợp lệ.§Không bị 401/403; status thành công thực tế: POST/GET/PUT/PATCH 200, DELETE 204.§UserController.java:50-123; base URL /api/v1.
ACC-SEC-002§NCL-01-CN-003§NCL-01-CN-003-CV-05§Phân quyền API§API/Security§Không có token bị chặn trên mọi API user§P0§Không có Authorization header§Toàn bộ 7 endpoint user§Gọi từng endpoint không token, kể cả payload/id sai.§Trả 401 trước validation; body có status, error, message, path, timestamp; không thay đổi dữ liệu.§SecurityConfig.java:204-224.
ACC-SEC-003§NCL-01-CN-003§NCL-01-CN-003-CV-05§Phân quyền API§API/Security§Các role không phải Admin bị chặn trên mọi API user§P0§Token hợp lệ cho 4 role non-admin§DOCTOR/NURSE/RECEPTIONIST/PHARMACIST§Parameterize role x endpoint và gọi toàn bộ API.§Mọi tổ hợp trả 403 với thông báo không có quyền; không thay đổi dữ liệu.§SecurityConfig.java:73-76; UserController.java:50-123.
ACC-SEC-004§NCL-01-CN-003§NCL-01-CN-003-CV-05§Xác thực API§API/Security§Token sai chữ ký, hết hạn hoặc Bearer rỗng§P0§Chuẩn bị 3 header không hợp lệ§Bearer invalid; expired JWT; Bearer + khoảng trắng§Gọi GET /api/v1/users với từng header.§Trả 401; không trả danh sách.§JwtAuthenticationFilter.java:67-107.
ACC-SEC-005§NCL-01-CN-003§NCL-01-CN-003-CV-05§Hiệu lực đổi vai trò§Security/E2E§Hạ role Admin xuống Doctor phải mất quyền ngay§P0§Admin A và Admin B đã đăng nhập; giữ token cũ của B§PUT user B roleName=DOCTOR§1. A đổi role B.\n2. Dùng token cũ B gọi GET /users.\n3. Đăng nhập lại B và gọi lại.§Token cũ không còn quyền Admin hoặc bị thu hồi; token mới mang role DOCTOR và GET /users trả 403.§KNOWN FAIL G-09: JWT tin role claim tới khi hết hạn. JwtAuthenticationFilter.java:67-107.
ACC-GET-001§NCL-01-CN-002§NCL-01-CN-002-CV-05§Danh sách API§API/Contract§Lấy danh sách gồm user active và inactive§P0§DB có ít nhất 1 active và 1 inactive§GET /api/v1/users với ADMIN_TOKEN§Gọi API và kiểm tra từng phần tử, không giả định thứ tự.§200; mảng chứa cả active/inactive; có id, username, fullName, email, phone, role, active; không có password/hash.§KNOWN FAIL G-05: UserResponse bỏ active. Backend hiện trả mảng, không phân trang.
ACC-GET-002§NCL-01-CN-002§NCL-01-CN-002-CV-05§Danh sách API§API§Danh sách không có dữ liệu§P2§Môi trường test không có user ngoài account phục vụ auth; dùng fixture cô lập§GET /api/v1/users§Gọi API ở fixture rỗng.§200 và body []; không trả null/404.§GetAllUsersService.java:26-43.
ACC-GET-003§NCL-01-CN-002§NCL-01-CN-002-CV-05§Chi tiết tài khoản§API§Lấy user theo UUID tồn tại§P1§Có USER_ID_EXISTING§GET /api/v1/users/{id}§Gọi API và đối chiếu DB.§200; đúng user; không lộ password/hash; role đúng; trạng thái phải đủ để UI hiển thị.§KNOWN FAIL một phần G-05: response thiếu active. UserController.java:69-78.
ACC-GET-004§NCL-01-CN-002§NCL-01-CN-002-CV-05§Chi tiết tài khoản§API/Negative§UUID hợp lệ nhưng user không tồn tại§P1§UUID_NOT_FOUND chưa có trong DB§GET /api/v1/users/{UUID_NOT_FOUND}§Gọi API với ADMIN_TOKEN.§404; message User not found.; không có dữ liệu user.§UserNotFoundException.java.
ACC-GET-005§NCL-01-CN-002§NCL-01-CN-002-CV-05§Chi tiết tài khoản§API/Negative§ID sai định dạng§P1§Admin hợp lệ§GET /api/v1/users/not-a-uuid§Gọi API.§400; message Invalid parameter: id; không trả 500.§GlobalExceptionHandler.java:74-88.
ACC-GET-006§NCL-01-CN-002§NCL-01-CN-002-CV-05§Hợp đồng danh sách§API/Integration§Frontend và backend thống nhất kiểu dữ liệu danh sách§P0§Nối UserList với backend thật§GET /api/v1/users§1. Gọi qua userApi.\n2. Render bảng.\n3. Đổi trang nếu có pagination.§UI đọc đúng body; hoặc backend trả Page thống nhất. Không được đọc content/totalElements từ một mảng.§KNOWN FAIL G-03/G-04: UserList kỳ vọng Page nhưng backend trả List.
ACC-CRT-001§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo tài khoản§API/Integration§Tạo tài khoản hợp lệ đủ trường§P0§Username/email chưa tồn tại; role DOCTOR tồn tại§doctor_tc01, Doctor@123, Bác sĩ TC01, doctor.tc01@example.com, 0901000001, DOCTOR§1. POST /api/v1/users.\n2. GET theo id trả về.\n3. Đăng nhập bằng mật khẩu raw.§200; lưu đúng dữ liệu; account active=true; đăng nhập được; response không chứa password/hash.§CreateUserService.java:35-78. active phải xác minh DB vì G-05.
ACC-CRT-002§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo tài khoản§API/Positive§Tạo tài khoản không có phone§P1§Username/email mới§phone null hoặc bỏ field§POST payload hợp lệ không phone.§200; phone null; các trường khác đúng; user active.§CreateUserRequest cho phép phone null.
ACC-CRT-003§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo tài khoản§API/Data-driven§Tạo cho từng role hệ thống§P1§5 role seed tồn tại§ADMIN, DOCTOR, NURSE, RECEPTIONIST, PHARMACIST§Lặp POST với username/email riêng cho từng role.§Mỗi request 200; response.role và role_id lưu đúng; không dùng STAFF/manager.§V2__seed_roles.sql:14-54; KNOWN GAP G-13 về vocabulary frontend.
ACC-CRT-004§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo trùng§API/Negative§Tạo trùng username§P0§Đã có user duplicate_user§Payload mới dùng duplicate_user nhưng email khác§1. Đếm user.\n2. POST.\n3. Đếm lại.§409; message Username already exists.; số user không đổi; không ghi dữ liệu một phần.§Bao phủ trực tiếp expected của task.xlsx.
ACC-CRT-005§NCL-01-CN-002§NCL-01-CN-002-CV-05§Tạo trùng§API/Negative§Tạo trùng email§P0§Đã có duplicate@example.com§Username mới, email duplicate@example.com§POST và kiểm tra dữ liệu sau lỗi.§409; message Email already exists.; không tạo user.§CreateUserService.java:39-41.
ACC-CRT-006§NCL-01-CN-002§NCL-01-CN-002-CV-05§Thiếu vai trò§API/Validation§Bỏ field roleName hoặc gửi null§P0§Các trường khác hợp lệ§Hai biến thể: không có roleName; roleName=null§Gửi lần lượt hai payload.§400; message Validation failed.; errors.roleName tồn tại; không tạo user.§Bao phủ trực tiếp thiếu vai trò trong task.xlsx.
ACC-CRT-007§NCL-01-CN-002§NCL-01-CN-002-CV-05§Thiếu vai trò§API/Validation§roleName rỗng hoặc chỉ khoảng trắng§P0§Các trường khác hợp lệ§roleName='' và roleName='   '§Gửi hai payload.§400; errors.roleName; không gọi lookup role/lưu user.§CreateUserRequest.java:22-23.
ACC-CRT-008§NCL-01-CN-002§NCL-01-CN-002-CV-05§Vai trò không hợp lệ§API/Negative§Role không tồn tại§P1§Tên role chưa có trong DB§roleName=MANAGER§POST payload.§404; message Role not found.; không tạo user.§RoleNotFoundException.java; frontend mock có manager nhưng backend không seed.
ACC-CRT-009§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation tạo§API/Validation§Username null, rỗng hoặc chỉ khoảng trắng§P1§Các trường khác hợp lệ§3 biến thể username§POST từng payload.§400; errors.username; không tạo user.§CreateUserRequest.java:8-9.
ACC-CRT-010§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation tạo§API/Validation§Password null, rỗng hoặc chỉ khoảng trắng§P1§Các trường khác hợp lệ§3 biến thể password§POST từng payload.§400; errors.password; không tạo user.§CreateUserRequest.java:11-12.
ACC-CRT-011§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation tạo§API/Validation§Full name null, rỗng hoặc chỉ khoảng trắng§P1§Các trường khác hợp lệ§3 biến thể fullName§POST từng payload.§400; errors.fullName; không tạo user.§CreateUserRequest.java:14-15.
ACC-CRT-012§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation tạo§API/Validation§Email null, rỗng hoặc chỉ khoảng trắng§P1§Các trường khác hợp lệ§3 biến thể email§POST từng payload.§400; thông báo xác định email bắt buộc; không tạo user; error contract nhất quán ở cả biến thể.§KNOWN GAP G-11: @Email không có @NotBlank, null/rỗng rơi xuống domain với body khác validation.
ACC-CRT-013§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation tạo§API/Validation§Email sai định dạng§P1§Các trường khác hợp lệ§invalid-email§POST payload.§400; errors.email; không tạo user.§CreateUserRequest.java:17-18.
ACC-CRT-014§NCL-01-CN-002§NCL-01-CN-002-CV-05§Biên dữ liệu tạo§API/Boundary§Các trường đúng giới hạn DB§P2§Chuẩn bị chuỗi đúng độ dài§username 50; fullName 100; email 100 hợp lệ; phone 20§POST từng fixture biên hoặc một payload kết hợp.§200; dữ liệu lưu không bị cắt; đọc lại giống input.§V1__create_auth_tables.sql:43-63.
ACC-CRT-015§NCL-01-CN-002§NCL-01-CN-002-CV-05§Biên dữ liệu tạo§API/Boundary§Các trường vượt giới hạn§P1§Chuỗi dài hơn giới hạn 1 ký tự§username 51; fullName 101; email 101; phone 21§Parameterize từng field, POST và kiểm tra không có row mới.§400 với lỗi đúng field; không trả 500; không cắt âm thầm.§KNOWN FAIL G-11: POST thiếu @Size và hiện có thể trả generic 500.
ACC-UPD-001§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Integration§Cập nhật fullName, email, phone và role hợp lệ§P0§USER_ID_EXISTING; email mới chưa dùng§fullName mới, email mới, phone mới, roleName=NURSE§1. PUT /users/{id}.\n2. GET lại.\n3. Kiểm tra username/password/status.§200; 4 field đổi đúng; username, password, active, createdAt giữ nguyên.§UpdateUserService.java:31-68.
ACC-UPD-002§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Positive§Giữ nguyên email của chính user§P1§User tồn tại§email hiện tại, thay fullName§PUT payload.§200; không báo trùng email; dữ liệu khác cập nhật.§UpdateUserService.java:38-41.
ACC-UPD-003§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Negative§Đổi sang email của user khác§P0§Có user A và B§PUT A với email của B§Gửi request rồi GET lại A.§409 Email already exists.; không cập nhật một phần fullName/phone/role.§UpdateUserService transactional.
ACC-UPD-004§NCL-01-CN-002§NCL-01-CN-002-CV-05§Validation cập nhật§API/Data-driven§Payload update vi phạm validation§P1§User tồn tại§fullName blank/>100; email blank/sai/>100; phone>20; roleName blank§Gửi riêng từng biến thể.§400; errors đúng field; dữ liệu cũ không đổi.§UpdateUserRequest.java:9-22.
ACC-UPD-005§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API§Xóa số điện thoại§P2§User có phone§phone=null và phone='' ở hai lượt§PUT rồi GET lại.§200; phone được xóa theo contract đã chốt; không ảnh hưởng field khác.§Backend hiện cho phép cả null/rỗng.
ACC-UPD-006§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Negative§Đổi sang role không tồn tại§P1§User tồn tại§roleName=MANAGER§PUT và GET lại.§404 Role not found.; profile và role cũ không đổi.§UpdateUserService.java:43-44.
ACC-UPD-007§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Negative§Update UUID hợp lệ không tồn tại§P1§UUID_NOT_FOUND§PUT /users/{UUID_NOT_FOUND}§Gửi payload hợp lệ.§404 User not found.; không tạo mới.§UserController.java:79-93.
ACC-UPD-008§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật tài khoản§API/Negative§Update với ID sai định dạng§P1§Admin hợp lệ§PUT /users/not-a-uuid§Gửi payload hợp lệ.§400 Invalid parameter: id; không thay đổi dữ liệu.§GlobalExceptionHandler type mismatch.
ACC-UPD-009§NCL-01-CN-002§NCL-01-CN-002-CV-05§Bảo vệ field bất biến§API/Security§Gửi thêm username/password trong body update§P1§User tồn tại; lưu hash cũ§JSON có username/password ngoài 4 field hợp lệ§PUT rồi GET/login.§200 hoặc 400 theo policy JSON; tuyệt đối không đổi username/password ngoài endpoint chuyên dụng.§Jackson hiện bỏ field lạ; xác nhận username/password giữ nguyên.
ACC-UPD-010§NCL-01-CN-002§NCL-01-CN-002-CV-05§Cập nhật user inactive§API§Cập nhật profile user đang inactive không tự kích hoạt§P1§USER_INACTIVE_ID§Payload update hợp lệ§PUT; kiểm tra DB/trạng thái; thử login.§200; profile đổi; active vẫn false; login vẫn bị chặn.§UpdateUserService giữ active.
ACC-STS-001§NCL-01-CN-004§NCL-01-CN-004-CV-04§Vô hiệu hóa§API/E2E§Admin vô hiệu hóa user active§P0§USER_ACTIVE_ID không phải admin hiện tại§PATCH /api/v1/users/{id}/deactivate§1. Gọi PATCH.\n2. GET/list reload.\n3. Kiểm tra DB.§200; active=false; response/list thể hiện trạng thái; UI đổi tag và nút thành Kích hoạt.§KNOWN FAIL G-03/G-05: frontend gọi endpoint cũ và response không có active.
ACC-STS-002§NCL-01-CN-004§NCL-01-CN-004-CV-04§Vô hiệu hóa§API/Idempotence§Vô hiệu hóa user đã inactive lần hai§P2§USER_INACTIVE_ID§PATCH deactivate hai lần§Gọi lần hai và đọc trạng thái.§200; vẫn inactive; không tạo lỗi/dữ liệu phụ.§User.deactivate idempotent.
ACC-STS-003§NCL-01-CN-004§NCL-01-CN-004-CV-04§Đăng nhập tài khoản bị khóa§API/E2E§User inactive đăng nhập bằng mật khẩu đúng§P0§Đã deactivate user; chưa có phiên mới§POST /api/v1/auth/login§Gửi username/password đúng.§403; message Account has been disabled.; không trả accessToken và không tạo session mới.§Bao phủ trực tiếp expected task.xlsx. LoginService.java:67-68.
ACC-STS-004§NCL-01-CN-004§NCL-01-CN-004-CV-04§Refresh tài khoản bị khóa§API/Security§User inactive dùng refresh token/session§P0§User có token trước khi bị deactivate§POST /api/v1/auth/refresh§Deactivate user rồi gọi refresh.§403; không cấp token mới.§RefreshTokenService.java:72-77.
ACC-STS-005§NCL-01-CN-004§NCL-01-CN-004-CV-04§Kích hoạt lại§E2E§Admin kích hoạt user rồi user đăng nhập lại§P0§USER_INACTIVE_ID và mật khẩu đúng§PATCH /users/{id}/activate§1. Admin activate.\n2. Reload danh sách.\n3. User login.§200 activate; active=true; UI hiển thị Hoạt động; login 200 và role đúng.§KNOWN GAP G-05 về response trạng thái.
ACC-STS-006§NCL-01-CN-004§NCL-01-CN-004-CV-04§Kích hoạt§API/Idempotence§Kích hoạt user đang active lần hai§P2§USER_ACTIVE_ID§PATCH activate hai lần§Gọi lần hai.§200; vẫn active; không tác dụng phụ.§User.activate idempotent.
ACC-STS-007§NCL-01-CN-004§NCL-01-CN-004-CV-04§Trạng thái lỗi ID§API/Negative§Activate/deactivate user không tồn tại hoặc ID malformed§P1§Admin hợp lệ§UUID_NOT_FOUND và not-a-uuid§Parameterize 2 endpoint x 2 loại id.§UUID không tồn tại trả 404 User not found.; malformed trả 400; không 500.§ActivateUserService/DeactivateUserService.
ACC-STS-008§NCL-01-CN-004§NCL-01-CN-004-CV-04§Nút khóa/mở§UI§Confirm hủy, thành công và lỗi khi đổi trạng thái§P0§Danh sách có active/inactive; có thể mock 200/500§Một user cụ thể§1. Mở confirm và Hủy.\n2. Xác nhận với 200.\n3. Lặp với 500.§Hủy không gọi API; success khóa nút khi pending, toast và cập nhật đúng dòng; lỗi giữ trạng thái và hiển thị message server.§BLOCKED G-01/G-03/G-16; UserList không được route.
ACC-STS-009§NCL-01-CN-004§NCL-01-CN-004-CV-04§Bảo vệ quản trị§API/Security§Không cho tự vô hiệu hóa hoặc vô hiệu hóa Admin cuối cùng§P0§Chỉ còn một active ADMIN; token của chính admin§Hai biến thể: self và last-admin§Gọi PATCH deactivate rồi thử truy cập/login.§API từ chối bằng 4xx nghiệp vụ; admin vẫn active; hệ thống không mất quyền quản trị.§KNOWN FAIL G-07: service hiện cho phép. Yêu cầu self-lock có trong docs/manual-test-cases-account-status.md.
ACC-STS-010§NCL-01-CN-004§NCL-01-CN-004-CV-04§Thu hồi token khi khóa§Security/E2E§Access token cũ bị vô hiệu ngay sau deactivate§P0§Admin B đã đăng nhập và giữ access token; Admin A thao tác§B_TOKEN_OLD§1. A deactivate B.\n2. B dùng token cũ gọi tài nguyên được bảo vệ.\n3. Chờ chưa tới hạn JWT và gọi lại.§Token cũ bị từ chối ngay (401/403 theo policy); không còn quyền đến hết 30 phút.§KNOWN FAIL G-08: JwtAuthenticationFilter không tra active/session.
ACC-DEL-001§NCL-01-CN-002§NCL-01-CN-002-CV-05§Xóa tài khoản§UI§Hủy hộp thoại xác nhận xóa§P1§Admin; có user có thể xóa§USER_ID_EXISTING§Bấm Xóa rồi Hủy.§Không gọi DELETE; dòng và dữ liệu giữ nguyên.§BLOCKED G-02: active page chưa có delete.
ACC-DEL-002§NCL-01-CN-002§NCL-01-CN-002-CV-05§Xóa tài khoản§API/E2E§Xóa user chưa có tham chiếu§P0§User test không có FK nghiệp vụ§DELETE /api/v1/users/{id}§1. DELETE.\n2. GET id.\n3. Reload list.\n4. Tạo lại username/email.§204 body rỗng; GET sau trả 404; list bỏ dòng; username/email được tái sử dụng theo hard-delete hiện tại.§DeleteUserService.java:21-28.
ACC-DEL-003§NCL-01-CN-002§NCL-01-CN-002-CV-05§Xóa tài khoản§API/Negative§Xóa user không tồn tại hoặc ID malformed§P1§Admin hợp lệ§UUID_NOT_FOUND; not-a-uuid§Gọi DELETE cho hai biến thể.§404 User not found. với UUID hợp lệ; 400 Invalid parameter: id với malformed.§GlobalExceptionHandler.
ACC-DEL-004§NCL-01-CN-002§NCL-01-CN-002-CV-05§Bảo vệ quản trị§API/Security§Không cho tự xóa hoặc xóa Admin cuối cùng§P0§Chỉ còn một ADMIN active§Self id và last-admin id§Gọi DELETE và kiểm tra account.§API từ chối 4xx nghiệp vụ; account còn tồn tại; quyền quản trị vẫn hoạt động.§KNOWN FAIL G-07: DeleteUserService không có guard.
ACC-DEL-005§NCL-01-CN-002§NCL-01-CN-002-CV-05§Ràng buộc xóa§API/Negative§Xóa user đang được patient/visit/log tham chiếu§P1§User có ít nhất một FK không cascade§USER_REFERENCED_ID§Gọi DELETE và kiểm tra dữ liệu liên quan.§409 Conflict hoặc lỗi nghiệp vụ rõ ràng; user và dữ liệu liên quan không mất; không trả generic 500.§KNOWN FAIL G-12: lỗi FK rơi vào handler 500.
ACC-DEL-006§NCL-01-CN-003§NCL-01-CN-003-CV-05§Thu hồi token khi xóa§Security/E2E§Token cũ không dùng được sau khi user bị hard-delete§P0§Admin B có token; A xóa B; B chưa có FK chặn xóa§B_TOKEN_OLD§1. A xóa B.\n2. B dùng token cũ gọi endpoint được bảo vệ.§Token cũ bị từ chối ngay; principal của user đã xóa không được chấp nhận.§KNOWN FAIL G-08: filter chỉ kiểm chữ ký/claims.
'@

$cases = [System.Collections.Generic.List[object]]::new()
foreach ($rawLine in ($caseSource -split '[\r\n]+')) {
    $line = $rawLine.Trim()
    if ([string]::IsNullOrWhiteSpace($line)) { continue }
    $parts = $line -split '§'
    if ($parts.Count -ne 12) { throw "Invalid testcase row with $($parts.Count) fields: $line" }
    $cases.Add([pscustomobject]@{
        Id = $parts[0]
        Story = $parts[1]
        Task = $parts[2]
        Category = Expand-TestText $parts[3]
        Layer = Expand-TestText $parts[4]
        Scenario = Expand-TestText $parts[5]
        Priority = $parts[6]
        Preconditions = Expand-TestText $parts[7]
        Data = Expand-TestText $parts[8]
        Steps = Expand-TestText $parts[9]
        Expected = Expand-TestText $parts[10]
        Notes = Expand-TestText $parts[11]
    })
}

$duplicateIds = $cases | Group-Object Id | Where-Object Count -gt 1
if ($duplicateIds) { throw 'Duplicate testcase IDs: ' + (($duplicateIds.Name) -join ', ') }

$gapSource = @'
G-01§High§Route /users hiển thị dữ liệu mock§Không thể kiểm thử danh sách thật; dữ liệu có thể lệch backend.§frontend/src/routes/AppRoutes.jsx:65; frontend/src/pages/UsersPage.jsx:3-8§ACC-UI-003/004; ACC-GET-006§Route component dùng API thật và bỏ mock khỏi màn hình quản trị.
G-02§Critical§Nút Tạo tài khoản và Quản lý không có handler/form; không có edit/delete§Các flow CRUD trong task.xlsx bị chặn hoàn toàn ở UI.§frontend/src/pages/UsersPage.jsx:10-21§ACC-UI-005/006/008; ACC-DEL-001§Cài form + userApi create/update/delete + confirm/loading/error.
G-03§Critical§Frontend gọi /admin/users và PUT /status; backend dùng /users và PATCH activate/deactivate§List/status luôn lỗi 404/405 khi nối thật.§frontend/src/api/userApi.js:10-31; UserController.java:35,106-124§ACC-UI-004/006; ACC-GET-006; ACC-STS-001/008§Thống nhất một contract và thêm contract test.
G-04§High§Frontend mong Page.content/totalElements nhưng backend trả List§Bảng nhận undefined và không render/paginate.§frontend/src/pages/UserList.jsx:20-25; UserController.java:61-67§ACC-GET-006§Bỏ pagination client hiện tại hoặc backend trả Page thống nhất.
G-05§Critical§UserResponse bỏ active; không có locked/lastLoginAt trong khi UI sử dụng§Không thể hiển thị/xác nhận trạng thái sau activate/deactivate.§UserResult.java:17-19; UserRestMapper.java:39-48; UserList.jsx:80-99§ACC-UI-003; ACC-GET-001/003; ACC-STS-001/005§Bổ sung active vào response; thống nhất mô hình 2 trạng thái hoặc locked riêng.
G-06§Medium§Backend chỉ có active nhưng UI/tài liệu dùng locked và disabled như trạng thái khác nhau§Expected Result dễ mâu thuẫn, nhãn/nút sai.§User.java:35; UserList.jsx:83-92§Các case STS§Chốt domain state machine và thuật ngữ trước khi automation.
G-07§Critical§Không chặn self/last-admin deactivate, delete hoặc hạ role§Có thể làm mất toàn bộ quyền quản trị.§DeactivateUserService.java:29-41; DeleteUserService.java:21-28; UpdateUserService.java:31-68§ACC-STS-009; ACC-DEL-004; ACC-SEC-005§Thêm policy guard và test transaction/security.
G-08§Critical§JWT filter không tra user.active, user tồn tại hoặc session revoked§User bị khóa/xóa vẫn dùng token cũ tới 30 phút.§JwtAuthenticationFilter.java:67-107; application.properties:74-75§ACC-STS-010; ACC-DEL-006§Kiểm tra active/session mỗi request hoặc dùng token version/revocation cache.
G-09§Critical§Đổi role không vô hiệu access token cũ§User bị hạ quyền vẫn giữ ROLE_ADMIN từ claim.§JwtAuthenticationFilter.java:67-107; UpdateUserService.java:43-64§ACC-SEC-005§Thu hồi phiên/token khi đổi role và kiểm tra version.
G-10§High§Frontend tin roles trong localStorage; trang mock không cần API§Sửa localStorage có thể mở và xem dữ liệu mock quản trị.§frontend/src/context/AuthContext.jsx:15-18; AppRoutes.jsx:21-34§ACC-UI-007§Dùng server as source of truth; không chứa dữ liệu nhạy cảm trong mock production.
G-11§High§Create DTO thiếu @NotBlank email và @Size theo DB§Error body không nhất quán; dữ liệu quá dài có thể trả 500.§CreateUserRequest.java:6-25; V1__create_auth_tables.sql:43-63§ACC-CRT-012/014/015§Đồng bộ validation REST/domain/DB và map DataIntegrityViolationException.
G-12§High§Hard-delete vướng nhiều FK không cascade và không có exception mapping§Xóa user đã hoạt động có thể trả generic 500.§V1__create_auth_tables.sql:90-110; V4__create_patient_tables.sql:37-125§ACC-DEL-005§Chốt soft-delete/409 policy và map constraint cụ thể.
G-13§Medium§Role vocabulary không đồng bộ: STAFF/manager so với RECEPTIONIST/PHARMACIST§Form và test data có thể gửi role không tồn tại.§frontend/src/utils/constants.js:23; V2__seed_roles.sql:14-54§ACC-CRT-003/008§Dùng role catalog từ backend và một enum thống nhất.
G-14§Medium§Frontend chưa có test runner/script/test file§Không thể tự động hóa component/integration trong CI.§frontend/package.json:7-22§Toàn bộ ACC-UI§Thêm Vitest + Testing Library và test axios mock/route guard.
G-15§Medium§Tài liệu manual hiện dùng endpoint/port/policy cũ§Người chạy test dễ gọi sai URL hoặc assert sai token behavior.§docs/manual-test-cases-account-status.md:39,60-76§Các case STS/SEC§Cập nhật tài liệu theo workbook và contract đã chốt.
G-16§Medium§UserList không có loading riêng khi đổi trạng thái§Double click có thể gửi request lặp và toast sai.§frontend/src/pages/UserList.jsx:38-51§ACC-UI-006; ACC-STS-008§Theo dõi pending theo user id và disable action.
'@

$gaps = [System.Collections.Generic.List[object]]::new()
foreach ($rawLine in ($gapSource -split '[\r\n]+')) {
    $line = $rawLine.Trim()
    if ([string]::IsNullOrWhiteSpace($line)) { continue }
    $parts = $line -split '§'
    if ($parts.Count -ne 7) { throw "Invalid gap row: $line" }
    $gaps.Add([pscustomobject]@{
        Id=$parts[0]; Severity=$parts[1]; Finding=$parts[2]; Impact=$parts[3]
        Evidence=$parts[4]; Cases=$parts[5]; Recommendation=$parts[6]
    })
}

$traceRows = @(
    @('NCL-01-CN-002','NCL-01-CN-002-CV-03','Xây dựng chức năng quản lý tài khoản','Tạo, sửa và vô hiệu hóa tài khoản','ACC-GET-*; ACC-CRT-*; ACC-UPD-*; ACC-DEL-*; ACC-STS-001/002','UserController + user services','G-05/G-07/G-11/G-12','Đã thiết kế test; có blocker'),
    @('NCL-01-CN-002','NCL-01-CN-002-CV-04','Xây dựng giao diện quản lý tài khoản','Danh sách và biểu mẫu tài khoản','ACC-UI-003 đến ACC-UI-008','UsersPage/UserList/userApi','G-01 đến G-05/G-14/G-16','Bị chặn bởi frontend hiện tại'),
    @('NCL-01-CN-002','NCL-01-CN-002-CV-05','Kiểm thử quản lý tài khoản','Kiểm thử tạo trùng và thiếu vai trò','ACC-CRT-004 đến ACC-CRT-008; ACC-UI-008','CreateUserRequest/CreateUserService','G-02/G-11','Bao phủ trực tiếp'),
    @('NCL-01-CN-003','NCL-01-CN-003-CV-03','Xây dựng lớp kiểm soát truy cập','Kiểm tra quyền phía máy chủ','ACC-SEC-001 đến ACC-SEC-005','SecurityConfig/UserController/JWT filter','G-08/G-09','Đã thiết kế test'),
    @('NCL-01-CN-003','NCL-01-CN-003-CV-04','Ràng buộc giao diện theo quyền','Ẩn hoặc chặn chức năng ngoài quyền','ACC-UI-001/002/007','AppRoutes/AuthContext','G-01/G-10','Có case expected fail'),
    @('NCL-01-CN-003','NCL-01-CN-003-CV-05','Kiểm thử phân quyền','Truy cập trái phép bị chặn','ACC-SEC-*; ACC-UI-001/002/007; ACC-DEL-006','SecurityConfig/JwtAuthenticationFilter','G-08/G-09/G-10','Bao phủ trực tiếp'),
    @('NCL-01-CN-004','NCL-01-CN-004-CV-02','Xây dựng chức năng khóa và mở','Đổi trạng thái tài khoản','ACC-STS-001/002/005/006/007/009/010','Activate/DeactivateUserService','G-05/G-07/G-08','Đã thiết kế test; có lỗi bảo mật'),
    @('NCL-01-CN-004','NCL-01-CN-004-CV-03','Thêm nút khóa và mở trên giao diện','Bổ sung thao tác vào danh sách','ACC-UI-006; ACC-STS-008','UserList/userApi/AppRoutes','G-01/G-03/G-05/G-16','Bị chặn bởi contract/UI'),
    @('NCL-01-CN-004','NCL-01-CN-004-CV-04','Kiểm thử khóa tài khoản','Tài khoản bị khóa không đăng nhập được','ACC-STS-003/004/005/010','LoginService/RefreshTokenService/JWT filter','G-08','Bao phủ trực tiếp')
)

function New-TestCasesSheet([System.Collections.Generic.List[object]]$Items) {
    $columns = @('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O')
    $headers = @('STT','Test Case ID','Story ID','Task ID','Hạng mục','Lớp kiểm thử','Kịch bản','Ưu tiên','Tiền điều kiện','Dữ liệu kiểm thử','Các bước thực hiện','Kết quả mong đợi','Kết quả thực tế','Trạng thái','Ghi chú / Code ref')
    $lastRow = 4 + $Items.Count
    $sb = [System.Text.StringBuilder]::new()
    [void]$sb.Append('<?xml version="1.0" encoding="UTF-8" standalone="yes"?>')
    [void]$sb.Append('<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><dimension ref="A1:O' + $lastRow + '"/>')
    [void]$sb.Append('<sheetViews><sheetView tabSelected="1" workbookViewId="0"><pane ySplit="4" topLeftCell="A5" activePane="bottomLeft" state="frozen"/><selection pane="bottomLeft" activeCell="A5" sqref="A5"/></sheetView></sheetViews>')
    [void]$sb.Append('<sheetFormatPr defaultRowHeight="60"/><cols>')
    $widths = @(6,18,18,25,20,15,40,10,38,38,50,55,34,14,48)
    for ($i=0; $i -lt $widths.Count; $i++) { [void]$sb.Append(('<col min="{0}" max="{0}" width="{1}" customWidth="1"/>' -f ($i+1), $widths[$i])) }
    [void]$sb.Append('</cols><sheetData>')
    [void]$sb.Append('<row r="1" ht="30" customHeight="1">' + (New-TextCell 'A1' 1 'BỘ TEST CASE QUẢN LÝ TÀI KHOẢN') + '</row>')
    [void]$sb.Append('<row r="2" ht="32" customHeight="1">' + (New-TextCell 'A2' 8 ('Căn cứ task.xlsx: NCL-01-CN-002-CV-05, NCL-01-CN-003-CV-05, NCL-01-CN-004-CV-04. Tổng số: ' + $Items.Count + ' test case.')) + '</row>')
    [void]$sb.Append('<row r="3" ht="32" customHeight="1">' + (New-TextCell 'A3' 8 'Baseline: working tree tại 2026-07-22, commit 6ff9d62. Mặc định chưa thực thi; gap dự kiến fail/block được ghi ở cột Ghi chú.') + '</row>')
    [void]$sb.Append('<row r="4" ht="55" customHeight="1">')
    for ($i=0; $i -lt $headers.Count; $i++) { [void]$sb.Append((New-TextCell ($columns[$i] + '4') $(if($i -eq 0){1}else{2}) $headers[$i])) }
    [void]$sb.Append('</row>')
    $index = 0
    foreach ($item in $Items) {
        $index++
        $r = 4 + $index
        [void]$sb.Append(('<row r="{0}" ht="82" customHeight="1">' -f $r))
        [void]$sb.Append((New-NumberCell ('A'+$r) 3 $index))
        $values = @($item.Id,$item.Story,$item.Task,$item.Category,$item.Layer,$item.Scenario,$item.Priority,$item.Preconditions,$item.Data,$item.Steps,$item.Expected,'','Chưa chạy',$item.Notes)
        for ($j=0; $j -lt $values.Count; $j++) {
            $colIndex = $j + 1
            $style = 4
            if ($colIndex -eq 7 -and $item.Priority -eq 'P0') { $style = 5 }
            elseif ($colIndex -eq 13) { $style = 7 }
            elseif ($colIndex -eq 14 -and ($item.Notes -match 'FAIL|BLOCKED|G-')) { $style = 8 }
            [void]$sb.Append((New-TextCell ($columns[$colIndex] + $r) $style $values[$j]))
        }
        [void]$sb.Append('</row>')
    }
    [void]$sb.Append('</sheetData>')
    [void]$sb.Append(('<autoFilter ref="A4:O{0}"/><mergeCells count="3"><mergeCell ref="A1:O1"/><mergeCell ref="A2:O2"/><mergeCell ref="A3:O3"/></mergeCells>' -f $lastRow))
    [void]$sb.Append(('<dataValidations count="1"><dataValidation type="list" allowBlank="0" sqref="N5:N{0}"><formula1>&quot;Chưa chạy,Đạt,Không đạt,Bị chặn&quot;</formula1></dataValidation></dataValidations>' -f $lastRow))
    [void]$sb.Append('<pageMargins left="0.3" right="0.3" top="0.5" bottom="0.5" header="0.2" footer="0.2"/></worksheet>')
    return $sb.ToString()
}

function New-TraceSheet([object[]]$Rows) {
    $cols = @('A','B','C','D','E','F','G','H')
    $headers = @('Story ID','Task ID','Task','Expected Result từ task.xlsx','Test case bao phủ','Code liên quan','Known gaps','Kết luận coverage')
    $variables = @(
        @('ADMIN_TOKEN','JWT hợp lệ của ADMIN','Header Authorization','Không dùng token seed trong tài liệu nếu chưa xác minh'),
        @('NON_ADMIN_TOKEN','JWT của DOCTOR/NURSE/RECEPTIONIST/PHARMACIST','Kiểm thử 403','Mỗi role một lần'),
        @('USER_ACTIVE_ID','UUID user active không phải admin thao tác','Status/update/delete','Tạo fixture riêng'),
        @('USER_INACTIVE_ID','UUID user đã deactivate','Status/login/update','Không dùng chung với test song song'),
        @('UUID_NOT_FOUND','UUID hợp lệ không có trong DB','Negative 404','Ví dụ 99999999-9999-9999-9999-999999999999'),
        @('B_TOKEN_OLD','Access token cấp trước đổi role/deactivate/delete','Stale-token security','Phải gọi lại trước khi JWT hết hạn'),
        @('API_BASE','http://localhost:8080/api/v1','Mọi API test','Frontend dev: http://localhost:5173')
    )
    $variableTitleRow = 7 + $Rows.Count
    $variableHeaderRow = $variableTitleRow + 1
    $lastRow = $variableHeaderRow + $variables.Count
    $sb = [System.Text.StringBuilder]::new()
    [void]$sb.Append('<?xml version="1.0" encoding="UTF-8" standalone="yes"?><worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">')
    [void]$sb.Append('<dimension ref="A1:H' + $lastRow + '"/><sheetViews><sheetView workbookViewId="0"><pane ySplit="5" topLeftCell="A6" activePane="bottomLeft" state="frozen"/><selection pane="bottomLeft" activeCell="A6" sqref="A6"/></sheetView></sheetViews><sheetFormatPr defaultRowHeight="55"/>')
    [void]$sb.Append('<cols><col min="1" max="1" width="20" customWidth="1"/><col min="2" max="2" width="27" customWidth="1"/><col min="3" max="4" width="34" customWidth="1"/><col min="5" max="5" width="40" customWidth="1"/><col min="6" max="7" width="38" customWidth="1"/><col min="8" max="8" width="27" customWidth="1"/></cols><sheetData>')
    [void]$sb.Append('<row r="1" ht="30" customHeight="1">' + (New-TextCell 'A1' 1 'TRUY VẾT TASK.XLSX ↔ TEST CASE ↔ CODE') + '</row>')
    [void]$sb.Append('<row r="2" ht="30" customHeight="1">' + (New-TextCell 'A2' 8 'Sheet Trang_tính1 được giữ nguyên từ file gốc. Các dòng dưới đây lấy đúng Story/Task liên quan quản lý tài khoản.') + '</row>')
    [void]$sb.Append('<row r="3" ht="30" customHeight="1">' + (New-TextCell 'A3' 8 'Ưu tiên chạy: P0 duplicate/missing role → authorization → deactivate/login/token → CRUD contract.') + '</row>')
    [void]$sb.Append('<row r="5" ht="52" customHeight="1">')
    for ($i=0; $i -lt $headers.Count; $i++) { [void]$sb.Append((New-TextCell ($cols[$i]+'5') $(if($i -eq 0){1}else{2}) $headers[$i])) }
    [void]$sb.Append('</row>')
    $rowNum = 5
    foreach ($row in $Rows) {
        $rowNum++
        [void]$sb.Append(('<row r="{0}" ht="70" customHeight="1">' -f $rowNum))
        for ($i=0; $i -lt 8; $i++) { [void]$sb.Append((New-TextCell ($cols[$i]+$rowNum) $(if($i -eq 6 -and $row[$i]){8}else{4}) $row[$i])) }
        [void]$sb.Append('</row>')
    }
    [void]$sb.Append(('<row r="{0}" ht="28" customHeight="1">{1}</row>' -f $variableTitleRow, (New-TextCell ('A'+$variableTitleRow) 1 'BIẾN DỮ LIỆU KIỂM THỬ')))
    $varHeaders = @('Biến','Ý nghĩa','Dùng cho','Lưu ý')
    [void]$sb.Append(('<row r="{0}" ht="42" customHeight="1">' -f $variableHeaderRow))
    for ($i=0; $i -lt 4; $i++) { [void]$sb.Append((New-TextCell ($cols[$i]+$variableHeaderRow) $(if($i -eq 0){1}else{2}) $varHeaders[$i])) }
    [void]$sb.Append('</row>')
    $vr = $variableHeaderRow
    foreach ($variable in $variables) {
        $vr++
        [void]$sb.Append(('<row r="{0}" ht="48" customHeight="1">' -f $vr))
        for ($i=0; $i -lt 4; $i++) { [void]$sb.Append((New-TextCell ($cols[$i]+$vr) 4 $variable[$i])) }
        [void]$sb.Append('</row>')
    }
    [void]$sb.Append('</sheetData><autoFilter ref="A5:H' + (5+$Rows.Count) + '"/><mergeCells count="4"><mergeCell ref="A1:H1"/><mergeCell ref="A2:H2"/><mergeCell ref="A3:H3"/><mergeCell ref="A' + $variableTitleRow + ':H' + $variableTitleRow + '"/></mergeCells><pageMargins left="0.3" right="0.3" top="0.5" bottom="0.5" header="0.2" footer="0.2"/></worksheet>')
    return $sb.ToString()
}

function New-GapsSheet([System.Collections.Generic.List[object]]$Items) {
    $cols = @('A','B','C','D','E','F','G')
    $headers = @('Gap ID','Mức độ','Phát hiện từ code','Tác động kiểm thử/nghiệp vụ','Bằng chứng code','Test case ảnh hưởng','Khuyến nghị')
    $lastRow = 4 + $Items.Count
    $sb = [System.Text.StringBuilder]::new()
    [void]$sb.Append('<?xml version="1.0" encoding="UTF-8" standalone="yes"?><worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><dimension ref="A1:G' + $lastRow + '"/>')
    [void]$sb.Append('<sheetViews><sheetView workbookViewId="0"><pane ySplit="4" topLeftCell="A5" activePane="bottomLeft" state="frozen"/><selection pane="bottomLeft" activeCell="A5" sqref="A5"/></sheetView></sheetViews><sheetFormatPr defaultRowHeight="65"/>')
    [void]$sb.Append('<cols><col min="1" max="1" width="10" customWidth="1"/><col min="2" max="2" width="12" customWidth="1"/><col min="3" max="4" width="43" customWidth="1"/><col min="5" max="5" width="52" customWidth="1"/><col min="6" max="6" width="34" customWidth="1"/><col min="7" max="7" width="45" customWidth="1"/></cols><sheetData>')
    [void]$sb.Append('<row r="1" ht="30" customHeight="1">' + (New-TextCell 'A1' 1 'CODE GAPS / BLOCKERS ẢNH HƯỞNG BỘ TEST') + '</row>')
    [void]$sb.Append('<row r="2" ht="38" customHeight="1">' + (New-TextCell 'A2' 8 'Các mục này là kết quả static review backend + frontend. Đây không phải kết quả chạy test; dùng để dự báo FAIL/BLOCKED và ưu tiên sửa.') + '</row>')
    [void]$sb.Append('<row r="4" ht="55" customHeight="1">')
    for ($i=0; $i -lt $headers.Count; $i++) { [void]$sb.Append((New-TextCell ($cols[$i]+'4') $(if($i -eq 0){1}else{2}) $headers[$i])) }
    [void]$sb.Append('</row>')
    $index = 0
    foreach ($item in $Items) {
        $index++; $r=4+$index
        [void]$sb.Append(('<row r="{0}" ht="78" customHeight="1">' -f $r))
        $values=@($item.Id,$item.Severity,$item.Finding,$item.Impact,$item.Evidence,$item.Cases,$item.Recommendation)
        for ($i=0; $i -lt 7; $i++) {
            $style=4
            if ($i -eq 1 -and $item.Severity -eq 'Critical') { $style=5 }
            elseif ($i -eq 1) { $style=8 }
            [void]$sb.Append((New-TextCell ($cols[$i]+$r) $style $values[$i]))
        }
        [void]$sb.Append('</row>')
    }
    [void]$sb.Append('</sheetData><autoFilter ref="A4:G' + $lastRow + '"/><mergeCells count="2"><mergeCell ref="A1:G1"/><mergeCell ref="A2:G2"/></mergeCells><pageMargins left="0.3" right="0.3" top="0.5" bottom="0.5" header="0.2" footer="0.2"/></worksheet>')
    return $sb.ToString()
}

$resolvedTemplate = [System.IO.Path]::GetFullPath($TemplatePath)
$resolvedOutput = [System.IO.Path]::GetFullPath($OutputPath)
if (-not (Test-Path -LiteralPath $resolvedTemplate -PathType Leaf)) { throw "Template not found: $resolvedTemplate" }
$outputDirectory = Split-Path -Parent $resolvedOutput
if (-not (Test-Path -LiteralPath $outputDirectory -PathType Container)) { New-Item -ItemType Directory -Path $outputDirectory | Out-Null }
Copy-Item -LiteralPath $resolvedTemplate -Destination $resolvedOutput -Force

$stream = [System.IO.File]::Open($resolvedOutput, [System.IO.FileMode]::Open, [System.IO.FileAccess]::ReadWrite, [System.IO.FileShare]::None)
$zip = [System.IO.Compression.ZipArchive]::new($stream, [System.IO.Compression.ZipArchiveMode]::Update, $false)
try {
    [xml]$workbook = Get-ZipText $zip 'xl/workbook.xml'
    $mainNs = 'http://schemas.openxmlformats.org/spreadsheetml/2006/main'
    $officeRelNs = 'http://schemas.openxmlformats.org/officeDocument/2006/relationships'
    $ns = [System.Xml.XmlNamespaceManager]::new($workbook.NameTable)
    $ns.AddNamespace('x', $mainNs)
    $sheetsNode = $workbook.SelectSingleNode('//x:sheets', $ns)
    $viewNode = $workbook.SelectSingleNode('//x:workbookView', $ns)
    $viewNode.SetAttribute('activeTab','1')
    $sheetDefs = @(
        @('Test_Cases','2','rId5'),
        @('Traceability','3','rId6'),
        @('Code_Gaps','4','rId7')
    )
    foreach ($def in $sheetDefs) {
        $node = $workbook.CreateElement('sheet', $mainNs)
        $node.SetAttribute('name', $def[0])
        $node.SetAttribute('sheetId', $def[1])
        $attribute = $workbook.CreateAttribute('r','id',$officeRelNs)
        $attribute.Value = $def[2]
        [void]$node.Attributes.Append($attribute)
        [void]$sheetsNode.AppendChild($node)
    }
    Set-ZipText $zip 'xl/workbook.xml' (Convert-XmlToString $workbook)

    [xml]$rels = Get-ZipText $zip 'xl/_rels/workbook.xml.rels'
    $relsNs = 'http://schemas.openxmlformats.org/package/2006/relationships'
    for ($i=2; $i -le 4; $i++) {
        $rel = $rels.CreateElement('Relationship', $relsNs)
        $rel.SetAttribute('Id', ('rId' + ($i+3)))
        $rel.SetAttribute('Type', 'http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet')
        $rel.SetAttribute('Target', ('worksheets/sheet' + $i + '.xml'))
        [void]$rels.DocumentElement.AppendChild($rel)
    }
    Set-ZipText $zip 'xl/_rels/workbook.xml.rels' (Convert-XmlToString $rels)

    [xml]$types = Get-ZipText $zip '[Content_Types].xml'
    $typesNs = 'http://schemas.openxmlformats.org/package/2006/content-types'
    for ($i=2; $i -le 4; $i++) {
        $override = $types.CreateElement('Override', $typesNs)
        $override.SetAttribute('PartName', ('/xl/worksheets/sheet' + $i + '.xml'))
        $override.SetAttribute('ContentType', 'application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml')
        [void]$types.DocumentElement.AppendChild($override)
    }
    Set-ZipText $zip '[Content_Types].xml' (Convert-XmlToString $types)

    [xml]$app = Get-ZipText $zip 'docProps/app.xml'
    $appNs = [System.Xml.XmlNamespaceManager]::new($app.NameTable)
    $appNs.AddNamespace('ep','http://schemas.openxmlformats.org/officeDocument/2006/extended-properties')
    $appNs.AddNamespace('vt','http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes')
    $countNode = $app.SelectSingleNode('//ep:HeadingPairs/vt:vector/vt:variant[2]/vt:i4', $appNs)
    $countNode.InnerText = '4'
    $titlesVector = $app.SelectSingleNode('//ep:TitlesOfParts/vt:vector', $appNs)
    $titlesVector.SetAttribute('size','4')
    $titlesVector.RemoveAll()
    $titlesVector.SetAttribute('size','4')
    $titlesVector.SetAttribute('baseType','lpstr')
    foreach ($title in @('Trang_tính1','Test_Cases','Traceability','Code_Gaps')) {
        $titleNode = $app.CreateElement('vt','lpstr','http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes')
        $titleNode.InnerText = $title
        [void]$titlesVector.AppendChild($titleNode)
    }
    Set-ZipText $zip 'docProps/app.xml' (Convert-XmlToString $app)

    $sheet1 = Get-ZipText $zip 'xl/worksheets/sheet1.xml'
    $sheet1 = $sheet1 -replace 'tabSelected="1"', 'tabSelected="0"'
    Set-ZipText $zip 'xl/worksheets/sheet1.xml' $sheet1
    Set-ZipText $zip 'xl/worksheets/sheet2.xml' (New-TestCasesSheet $cases)
    Set-ZipText $zip 'xl/worksheets/sheet3.xml' (New-TraceSheet $traceRows)
    Set-ZipText $zip 'xl/worksheets/sheet4.xml' (New-GapsSheet $gaps)
}
finally {
    $zip.Dispose()
    $stream.Dispose()
}

$verify = [System.IO.Compression.ZipFile]::OpenRead($resolvedOutput)
try {
    foreach ($required in @('xl/workbook.xml','xl/worksheets/sheet1.xml','xl/worksheets/sheet2.xml','xl/worksheets/sheet3.xml','xl/worksheets/sheet4.xml')) {
        [xml](Get-ZipText $verify $required) | Out-Null
    }
    [xml]$verifiedWorkbook = Get-ZipText $verify 'xl/workbook.xml'
    $sheetNames = @($verifiedWorkbook.SelectNodes("//*[local-name()='sheet']") | ForEach-Object { $_.name })
    if (($sheetNames -join '|') -ne 'Trang_tính1|Test_Cases|Traceability|Code_Gaps') { throw 'Unexpected worksheet list: ' + ($sheetNames -join ', ') }
}
finally { $verify.Dispose() }

[pscustomobject]@{
    Output = $resolvedOutput
    TestCases = $cases.Count
    CodeGaps = $gaps.Count
    Sheets = 4
    Size = (Get-Item -LiteralPath $resolvedOutput).Length
}
