# Frontend - Bệnh Số Án

React + Vite + Ant Design + React Router.

## Cài đặt

```bash
cd frontend
npm install
cp .env.example .env   # rồi sửa VITE_API_BASE_URL nếu backend chạy khác cổng
npm run dev
```

Mặc định chạy tại http://localhost:5173, proxy `/api` sang backend Spring Boot tại
`http://localhost:8080` (chỉnh trong `vite.config.js` nếu backend team dùng cổng khác).

## Cấu trúc thư mục

```
src/
  api/            # gọi API backend (axios)
  components/     # component dùng chung (layout, route bảo vệ...)
  contexts/       # React context (AuthContext quản lý đăng nhập)
  pages/          # từng trang, chia theo module nghiệp vụ (NCL-01, NCL-02, ...)
  routes/         # khai báo route tập trung
  utils/          # hằng số, hàm dùng chung
```

## Quy ước thêm module mới

Mỗi module nghiệp vụ (vd NCL-02 Quản lý hồ sơ bệnh nhân) làm theo pattern:

1. Tạo `src/api/<module>Api.js` gọi các endpoint tương ứng.
2. Tạo thư mục `src/pages/<Module>/` chứa các trang.
3. Khai báo route trong `src/routes/index.jsx`, bọc trong `<ProtectedRoute allowedRoles={[...]} />`
   nếu chỉ 1 số vai trò được truy cập.
4. Thêm mục menu tương ứng trong `src/components/layout/MainLayout.jsx`.

## Backend API cần có (module NCL-01 - đã code ở frontend)

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | /api/auth/login | Đăng nhập, trả về `{ accessToken, user }` |
| GET | /api/auth/me | Lấy thông tin user hiện tại từ token |
| POST | /api/auth/logout | Đăng xuất |
| GET | /api/accounts | Danh sách tài khoản |
| POST | /api/accounts | Tạo tài khoản mới |
| PATCH | /api/accounts/:id/role | Đổi vai trò |
| PATCH | /api/accounts/:id/status | Khóa/mở khóa tài khoản |

`user` object nên có dạng: `{ id, fullName, username, role, status }` với `role` là một trong
`ADMIN | RECEPTIONIST | DOCTOR | PHARMACIST | CASHIER` (xem `src/utils/constants.js`).
