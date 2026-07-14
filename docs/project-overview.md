# Tổng quan dự án - Bệnh số án

## Giới thiệu
Hệ thống **Bệnh số án** là nền tảng chuyển đổi số cơ sở khám chữa bệnh, giúp quản lý hồ sơ bệnh nhân, hồ sơ bệnh án điện tử, và quy trình khám chữa bệnh một cách hiệu quả.

## Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────┐
│                     Frontend (React + Vite)              │
│  Port: 5173                                              │
│  Thư viện: Ant Design, Axios, React Router, React Hook Form │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP / REST API
                       │ (JWT Authentication)
┌──────────────────────┴──────────────────────────────────┐
│                   Backend (Spring Boot)                  │
│  Port: 8080                                              │
│  Context-path: /api/v1                                   │
│  Java 21 + Maven 3.9+                                    │
└──────────────────────┬──────────────────────────────────┘
                       │ JPA / Hibernate
┌──────────────────────┴──────────────────────────────────┐
│                   Database                               │
│  Dev: H2 (In-memory)                                     │
│  Prod: PostgreSQL                                        │
└─────────────────────────────────────────────────────────┘
```

## Công nghệ sử dụng

### Backend
- **Java 21** - OpenJDK Temurin
- **Spring Boot 3.3.x** - Framework chính
- **Spring Security** - Xác thực & phân quyền
- **Spring Data JPA** - ORM
- **JWT (jjwt)** - Xác thực token
- **Lombok** - Giảm boilerplate code
- **MapStruct** - Mapping DTO <-> Entity
- **Swagger/OpenAPI** - Tài liệu API
- **PostgreSQL** - Database production
- **H2** - Database development
- **Flyway** - Migration

### Frontend
- **React 18** - UI Library
- **Vite** - Build tool
- **Ant Design 5** - UI Component Library
- **Axios** - HTTP Client
- **React Router 6** - Routing
- **React Hook Form** - Form validation
- **React Hot Toast** - Notification

## Cấu trúc thư mục

```
Bệnh số án/
├── backend/                        # Spring Boot Backend
│   ├── src/main/java/com/benhsoan/
│   │   ├── config/                 # Cấu hình (CORS, Security, Swagger)
│   │   ├── controller/             # REST Controllers
│   │   ├── dto/                    # Data Transfer Objects
│   │   ├── exception/              # Exception handling
│   │   ├── model/entity/           # JPA Entities
│   │   ├── repository/             # Data repositories
│   │   ├── security/               # JWT, Authentication
│   │   └── service/                # Business logic
│   └── src/main/resources/         # Properties, config files
│
├── frontend/                       # React Frontend
│   └── src/
│       ├── api/                    # API calls
│       ├── components/             # Reusable components
│       │   ├── common/             # Shared components
│       │   └── layout/             # Layout components
│       ├── context/                # React contexts
│       ├── hooks/                  # Custom hooks
│       ├── pages/                  # Page components
│       ├── routes/                 # Route config
│       └── utils/                  # Utilities
│
├── database/                       # Database scripts
│   └── init.sql                    # Schema init script
│
├── docs/                           # Documentation
└── README.md
```

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - Đăng nhập

### Patients
- `GET /api/v1/patients` - Danh sách bệnh nhân (phân trang + tìm kiếm)
- `GET /api/v1/patients/{id}` - Chi tiết bệnh nhân
- `GET /api/v1/patients/code/{code}` - Tìm theo mã BN
- `POST /api/v1/patients` - Thêm bệnh nhân
- `PUT /api/v1/patients/{id}` - Cập nhật
- `DELETE /api/v1/patients/{id}` - Xóa

### Medical Records
- `GET /api/v1/medical-records` - Danh sách hồ sơ
- `GET /api/v1/medical-records/{id}` - Chi tiết
- `GET /api/v1/medical-records/by-patient/{patientId}` - Theo bệnh nhân
- `GET /api/v1/medical-records/by-doctor/{doctorId}` - Theo bác sĩ
- `POST /api/v1/medical-records` - Tạo mới
- `PUT /api/v1/medical-records/{id}` - Cập nhật
- `DELETE /api/v1/medical-records/{id}` - Xóa

## Phân công team

### Backend (2 người)
1. **Người 1**: Auth, Security, User management
2. **Người 2**: Patient CRUD, Medical Record CRUD

### Frontend (2 người)
1. **Người 1**: Login, Dashboard, Layout, Routing
2. **Người 2**: Patient pages, Medical Record pages, API integration

## Hướng dẫn chạy

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
Truy cập: http://localhost:8080/api/v1/swagger-ui/index.html

### Frontend
```bash
cd frontend
npm install
npm run dev
```
Truy cập: http://localhost:5173
