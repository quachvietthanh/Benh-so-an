# 🏥 Bệnh số án

> **Hệ thống chuyển đổi số cơ sở khám chữa bệnh** - Quản lý hồ sơ bệnh án điện tử toàn diện

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![React](https://img.shields.io/badge/React-18-61DAFB)](https://reactjs.org/)
[![Vite](https://img.shields.io/badge/Vite-5-646CFF)](https://vitejs.dev/)
[![Ant Design](https://img.shields.io/badge/Ant_Design-5-1677FF)](https://ant.design/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📋 Mục lục

- [Tổng quan](#-tổng-quan)
- [Tính năng](#-tính-năng)
- [Công nghệ](#-công-nghệ)
- [Kiến trúc hệ thống](#-kiến-trúc-hệ-thống)
- [Cấu trúc dự án](#-cấu-trúc-dự-án)
- [Hướng dẫn cài đặt](#-hướng-dẫn-cài-đặt)
  - [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [Database](#database)
- [API Endpoints](#-api-endpoints)
- [Môi trường](#-môi-trường)
- [Team](#-team)

---

## 📖 Tổng quan

**Bệnh số án** là nền tảng quản lý hồ sơ bệnh án điện tử, giúp số hóa quy trình khám chữa bệnh tại các cơ sở y tế. Hệ thống cho phép:

- 📝 Quản lý thông tin bệnh nhân tập trung
- 📋 Lưu trữ và tra cứu hồ sơ bệnh án điện tử
- 🔐 Xác thực & phân quyền người dùng (JWT)
- 🔍 Tìm kiếm, phân trang dữ liệu
- 📊 Dashboard tổng quan

---

## ✨ Tính năng

### 🔐 Authentication & Authorization
- Đăng nhập với JWT Token
- Phân quyền người dùng: `ADMIN`, `DOCTOR`, `NURSE`, `STAFF`
- Bảo vệ API với Spring Security + JWT Filter

### 👤 Quản lý bệnh nhân
- Thêm, sửa, xóa thông tin bệnh nhân
- Tìm kiếm theo mã bệnh nhân, họ tên
- Danh sách phân trang
- Quản lý thông tin chi tiết: ngày sinh, giới tính, SĐT, địa chỉ, mã BHYT, nhóm máu, tiền sử bệnh, dị ứng, ...

### 📄 Quản lý hồ sơ bệnh án
- Tạo hồ sơ bệnh án cho bệnh nhân
- Tra cứu theo bệnh nhân hoặc bác sĩ
- Theo dõi trạng thái: `MỚI`, `ĐANG XỬ LÝ`, `HOÀN THÀNH`, `ĐÃ HỦY`
- Lưu trữ chẩn đoán, triệu chứng, phương pháp điều trị, đơn thuốc

### 🖥️ Giao diện người dùng
- Dashboard tổng quan
- Layout responsive với Sidebar
- Form nhập liệu trực quan (Ant Design)
- Thông báo toast (React Hot Toast)
- Validation form (React Hook Form)

---

## 🛠️ Công nghệ

### Backend

| Công nghệ | Mô tả |
|-----------|-------|
| **Java 21** | OpenJDK Temurin |
| **Spring Boot 3.3.x** | Framework chính |
| **Spring Security** | Xác thực & phân quyền |
| **Spring Data JPA** | ORM - Truy vấn dữ liệu |
| **Spring Validation** | Validation dữ liệu đầu vào |
| **Spring Mail** | Gửi email |
| **JWT (jjwt 0.12.6)** | Xác thực token |
| **Lombok** | Giảm boilerplate code |
| **MapStruct 1.6.2** | Mapping DTO <-> Entity |
| **SpringDoc OpenAPI 2.5.0** | Tài liệu API Swagger |
| **Flyway** | Migration database |
| **PostgreSQL** | Database production |
| **H2 Database** | Database development (in-memory) |

### Frontend

| Công nghệ | Mô tả |
|-----------|-------|
| **React 18** | UI Library |
| **Vite 5** | Build tool |
| **Ant Design 5** | UI Component Library |
| **Axios** | HTTP Client |
| **React Router 6** | Routing |
| **React Hook Form** | Form validation |
| **React Hot Toast** | Notification |
| **Dayjs** | Thư viện xử lý ngày tháng |

---

## 🏗️ Kiến trúc hệ thống

```
┌──────────────────────────────────────────────────────────────────┐
│                    Frontend (React + Vite)                        │
│  Port: 5173                                                       │
│  Thư viện: Ant Design, Axios, React Router, React Hook Form      │
│  Proxy: /api → http://localhost:8080/api/v1                       │
└──────────────────────────┬───────────────────────────────────────┘
                           │ HTTP / REST API (JWT Bearer Token)
┌──────────────────────────┴───────────────────────────────────────┐
│                      Backend (Spring Boot)                        │
│  Port: 8080 | Context-path: /api/v1                               │
│  Java 21 + Maven 3.9+                                             │
│  Modules: Config, Controller, Service, Repository, Security       │
└──────────────────────────┬───────────────────────────────────────┘
                           │ JPA / Hibernate
┌──────────────────────────┴───────────────────────────────────────┐
│                         Database                                  │
│  Dev:  H2 (In-memory)    - jdbc:h2:mem:benhsoandb                │
│  Prod: PostgreSQL 16     - jdbc:postgresql://localhost:5432/...   │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📁 Cấu trúc dự án

# Bệnh Án Số - Project Structure

```text
Bệnh án số/
│
├── backend/
│   │
│   ├── src/
│   │   │
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── benhsoan/
│   │   │   │
│   │   │   │           ├── BenhSoAnApplication.java
│   │   │   │
│   │   │   │           ├── config/
│   │   │   │           │   ├── SecurityConfig.java
│   │   │   │           │   ├── SwaggerConfig.java
│   │   │   │           │   ├── CorsConfig.java
│   │   │   │           │   ├── WebConfig.java
│   │   │   │           │   └── BeanConfig.java
│   │   │   │
│   │   │   │
│   │   │   │           ├── adapter/
│   │   │   │           │
│   │   │   │           │   ├── inbound/
│   │   │   │           │   │
│   │   │   │           │   │   ├── rest/
│   │   │   │           │   │   │   ├── AuthController.java
│   │   │   │           │   │   │   ├── PatientController.java
│   │   │   │           │   │   │   ├── MedicalRecordController.java
│   │   │   │           │   │   │   ├── AppointmentController.java
│   │   │   │           │   │   │   ├── PharmacyController.java
│   │   │   │           │   │   │   ├── InvoiceController.java
│   │   │   │           │   │   │   └── AuditLogController.java
│   │   │   │           │   │
│   │   │   │           │   │   ├── websocket/
│   │   │   │           │   │   ├── scheduler/
│   │   │   │           │   │   └── listener/
│   │   │   │           │
│   │   │   │           │   └── outbound/
│   │   │   │           │       ├── persistence/
│   │   │   │           │       ├── email/
│   │   │   │           │       ├── storage/
│   │   │   │           │       ├── notification/
│   │   │   │           │       └── external/
│   │   │   │
│   │   │   │
│   │   │   │           ├── application/
│   │   │   │           │
│   │   │   │           │   ├── ucservice/
│   │   │   │           │   │
│   │   │   │           │   │   ├── handler/
│   │   │   │           │   │   │   ├── auth/
│   │   │   │           │   │   │   ├── patient/
│   │   │   │           │   │   │   ├── medicalrecord/
│   │   │   │           │   │   │   ├── appointment/
│   │   │   │           │   │   │   ├── pharmacy/
│   │   │   │           │   │   │   ├── invoice/
│   │   │   │           │   │   │   └── auditlog/
│   │   │   │           │   │
│   │   │   │           │   │
│   │   │   │           │   └── queries/
│   │   │   │           │       ├── auth/
│   │   │   │           │       ├── patient/
│   │   │   │           │       ├── medicalrecord/
│   │   │   │           │       ├── appointment/
│   │   │   │           │       ├── pharmacy/
│   │   │   │           │       ├── invoice/
│   │   │   │           │       └── auditlog/
│   │   │   │           │
│   │   │   │           │   ├── mapper/
│   │   │   │           │   ├── assembler/
│   │   │   │           │   └── event/
│   │   │   │           │       ├── publisher/
│   │   │   │           │       └── listener/
│   │   │   │
│   │   │   │
│   │   │   │           ├── port/
│   │   │   │           │
│   │   │   │           │   ├── inbound/
│   │   │   │           │   │   ├── auth/
│   │   │   │           │   │   ├── patient/
│   │   │   │           │   │   ├── medicalrecord/
│   │   │   │           │   │   ├── appointment/
│   │   │   │           │   │   ├── pharmacy/
│   │   │   │           │   │   ├── invoice/
│   │   │   │           │   │   └── auditlog/
│   │   │   │           │
│   │   │   │           │   ├── outbound/
│   │   │   │           │   │   ├── repository/
│   │   │   │           │   │   ├── storage/
│   │   │   │           │   │   ├── notification/
│   │   │   │           │   │   ├── email/
│   │   │   │           │   │   ├── jwt/
│   │   │   │           │   │   ├── cache/
│   │   │   │           │   │   └── external/
│   │   │   │           │
│   │   │   │           │   └── dto/
│   │   │   │           │       ├── request/
│   │   │   │           │       ├── response/
│   │   │   │           │       ├── projection/
│   │   │   │           │       └── event/
│   │   │   │
│   │   │   │
│   │   │   │           ├── domain/
│   │   │   │           │   ├── auth/
│   │   │   │           │   ├── patient/
│   │   │   │           │   ├── medicalrecord/
│   │   │   │           │   ├── appointment/
│   │   │   │           │   ├── pharmacy/
│   │   │   │           │   ├── invoice/
│   │   │   │           │   ├── auditlog/
│   │   │   │           │   ├── user/
│   │   │   │           │   └── shared/
│   │   │   │           │       ├── entity/
│   │   │   │           │       ├── valueobject/
│   │   │   │           │       ├── enums/
│   │   │   │           │       ├── event/
│   │   │   │           │       ├── exception/
│   │   │   │           │       ├── validator/
│   │   │   │           │       └── specification/
│   │   │   │
│   │   │   │
│   │   │   │           ├── persistence/
│   │   │   │           │   ├── config/
│   │   │   │           │   ├── entity/
│   │   │   │           │   ├── repository/
│   │   │   │           │   ├── adapter/
│   │   │   │           │   └── mapper/
│   │   │   │
│   │   │   │
│   │   │   │           ├── infrastructure/
│   │   │   │           │   ├── security/
│   │   │   │           │   │   ├── jwt/
│   │   │   │           │   │   ├── filter/
│   │   │   │           │   │   ├── handler/
│   │   │   │           │   │   ├── permission/
│   │   │   │           │   │   └── service/
│   │   │   │           │   ├── redis/
│   │   │   │           │   ├── kafka/
│   │   │   │           │   ├── mail/
│   │   │   │           │   ├── storage/
│   │   │   │           │   │   ├── cloudinary/
│   │   │   │           │   │   ├── minio/
│   │   │   │           │   │   └── local/
│   │   │   │           │   ├── cache/
│   │   │   │           │   ├── scheduler/
│   │   │   │           │   └── external/
│   │   │   │
│   │   │   │
│   │   │   │           ├── common/
│   │   │   │           │   ├── annotation/
│   │   │   │           │   ├── constant/
│   │   │   │           │   ├── exception/
│   │   │   │           │   ├── response/
│   │   │   │           │   ├── util/
│   │   │   │           │   └── validator/
│   │   │   │
│   │   │   │
│   │   │   │           └── exception/
│   │   │   │               └── GlobalExceptionHandler.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-local.yml
│   │       └── db/
│   │           └── migration/
│   │
│   ├── test/
│   └── pom.xml
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── assets/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── layout/
│   │   │   └── ui/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── routes/
│   │   ├── services/
│   │   ├── pages/
│   │   │   ├── auth/
│   │   │   ├── dashboard/
│   │   │   ├── patient/
│   │   │   ├── medicalrecord/
│   │   │   ├── appointment/
│   │   │   ├── pharmacy/
│   │   │   ├── invoice/
│   │   │   ├── auditlog/
│   │   │   └── admin/
│   │   ├── styles/
│   │   ├── utils/
│   │   ├── App.jsx
│   │   └── main.jsx
│
├── database/
│   ├── init.sql
│   └── seed.sql
│
├── docs/
│   ├── Architecture.md
│   ├── API.md
│   ├── ERD.drawio
│   ├── Sequence/
│   ├── UseCase/
│   └── Database/
│
├── docker/
│   ├── mysql/
│   ├── redis/
│   └── nginx/
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

## Kiến trúc áp dụng

- **Domain-Driven Design (DDD)**
- **Hexagonal Architecture (Ports & Adapters)**
- **Clean Architecture**
- **CQRS (Command Query Responsibility Segregation)**
- **REST API**
- **Event-Driven Architecture (Application Events)**

## 🚀 Hướng dẫn cài đặt

### Yêu cầu hệ thống

| Công cụ | Phiên bản |
|---------|-----------|
| **Java** | 21 (OpenJDK Temurin) |
| **Maven** | 3.9+ |
| **Node.js** | 18+ |
| **npm** | 9+ |
| **PostgreSQL** | 16 (cho production) |

### Backend

```bash
# 1. Di chuyển vào thư mục backend
cd backend

# 2. Build project (bỏ qua test)
mvn clean install -DskipTests

# 3. Chạy ứng dụng (mặc định profile dev)
mvn spring-boot:run
```

> **Lưu ý:** Mặc định chạy với profile `dev` (H2 in-memory). Để chạy với PostgreSQL, set biến môi trường:
> ```bash
> set SPRING_PROFILES_ACTIVE=prod
> set DB_PASSWORD=your_password
> mvn spring-boot:run
> ```

#### 📖 API Docs (Swagger)

Sau khi chạy backend, truy cập:

- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/api/v1/v3/api-docs
- **H2 Console:** http://localhost:8080/api/v1/h2-console (JDBC URL: `jdbc:h2:mem:benhsoandb`)

### Frontend

```bash
# 1. Di chuyển vào thư mục frontend
cd frontend

# 2. Cài đặt dependencies
npm install

# 3. Chạy môi trường development
npm run dev
```

> 🌐 Web: http://localhost:5173
>
> Frontend tự động proxy API `/api` → `http://localhost:8080/api/v1` (xem `vite.config.js`)

### Database

#### Development (mặc định)
- H2 in-memory tự động khởi tạo khi chạy backend
- H2 Console: http://localhost:8080/api/v1/h2-console

#### Production (PostgreSQL)

```bash
# 1. Tạo database
psql -U postgres
CREATE DATABASE benhsoan_db;

# 2. Chạy script init
psql -U postgres -d benhsoan_db -f database/init.sql

# 3. Cấu hình biến môi trường
set SPRING_PROFILES_ACTIVE=prod
set DB_PASSWORD=your_secure_password
```

---

## 📡 API Endpoints

> **Base URL:** `http://localhost:8080/api/v1`
>
> **Auth:** Bearer Token (JWT) - Thêm header `Authorization: Bearer <token>`

### 🔐 Authentication

| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| `POST` | `/auth/login` | Đăng nhập | ❌ |

**Login Request:**
```json
{
  "username": "admin",
  "password": "password"
}
```

**Login Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "username": "admin",
  "fullName": "Quản trị viên",
  "roles": ["ADMIN"]
}
```

### 👤 Patients

| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| `GET` | `/patients` | Danh sách bệnh nhân (phân trang + tìm kiếm) | ✅ |
| `GET` | `/patients/{id}` | Chi tiết bệnh nhân | ✅ |
| `GET` | `/patients/code/{code}` | Tìm theo mã bệnh nhân | ✅ |
| `POST` | `/patients` | Thêm bệnh nhân mới | ✅ |
| `PUT` | `/patients/{id}` | Cập nhật thông tin | ✅ |
| `DELETE` | `/patients/{id}` | Xóa bệnh nhân | ✅ |

**Query params cho `GET /patients`:**
- `page` (int, mặc định: 0)
- `size` (int, mặc định: 10)
- `keyword` (string, tìm kiếm theo tên/mã BN)

### 📋 Medical Records

| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| `GET` | `/medical-records` | Danh sách hồ sơ bệnh án | ✅ |
| `GET` | `/medical-records/{id}` | Chi tiết hồ sơ | ✅ |
| `GET` | `/medical-records/by-patient/{patientId}` | Theo bệnh nhân | ✅ |
| `GET` | `/medical-records/by-doctor/{doctorId}` | Theo bác sĩ | ✅ |
| `POST` | `/medical-records` | Tạo hồ sơ mới | ✅ |
| `PUT` | `/medical-records/{id}` | Cập nhật hồ sơ | ✅ |
| `DELETE` | `/medical-records/{id}` | Xóa hồ sơ | ✅ |

---

## 🌍 Môi trường

### Backend Profiles

| Profile | Database | Flyway | H2 Console | Log Level |
|---------|----------|--------|------------|-----------|
| **dev** (mặc định) | H2 in-memory | ❌ | ✅ | DEBUG |
| **prod** | PostgreSQL | ✅ | ❌ | WARN |

### Cấu hình JWT

| Biến | Giá trị mặc định | Mô tả |
|------|-----------------|-------|
| `app.jwt.secret` | `benhsoan-secret-key-change-in-production-please` | Secret key (⚠️ **Đổi trong production**) |
| `app.jwt.expiration-ms`| `86400000` (24h) | Thời gian hết hạn token |

---

## 👥 Team

Dự án được phát triển bởi **4 thành viên**:

### Backend (2 người)
| Thành viên | Nhiệm vụ |
|------------|----------|
| **Người 1** | Auth, Security, User management |
| **Người 2** | Patient CRUD, Medical Record CRUD |

### Frontend (2 người)
| Thành viên | Nhiệm vụ |
|------------|----------|
| **Người 1** | Login, Dashboard, Layout, Routing |
| **Người 2** | Patient pages, Medical Record pages, API integration |

---

## 📚 Tài liệu

- 📄 [Project Overview](docs/project-overview.md) - Tài liệu chi tiết dự án
- 🗄️ [Database Schema](database/init.sql) - Script khởi tạo cơ sở dữ liệu

---

## 🔗 Repository

**GitHub:** https://github.com/quachvietthanh/Benh-so-an.git

---

## 📄 Giấy phép

Dự án được phân phối dưới giấy phép MIT. Xem file `LICENSE` để biết thêm thông tin.
