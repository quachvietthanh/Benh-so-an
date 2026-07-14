# 🏥 Bệnh số án

> **Hệ thống chuyển đổi số cơ sở khám chữa bệnh**

## 📋 Tổng quan

Dự án **Bệnh số án** là nền tảng quản lý hồ sơ bệnh án điện tử, giúp số hóa quy trình khám chữa bệnh tại các cơ sở y tế.

### 👥 Team
- **4 thành viên**: 2 Backend (Spring Boot) + 2 Frontend (React)

### 🛠️ Công nghệ

| Layer | Công nghệ |
|-------|-----------|
| **Backend** | Java 21, Spring Boot 3, Spring Security, JWT, JPA/Hibernate |
| **Frontend** | React 18, Vite, Ant Design 5, Axios, React Router 6 |
| **Database** | PostgreSQL (Prod) / H2 (Dev) |
| **API Docs** | Swagger / OpenAPI 3 |

## 🚀 Bắt đầu nhanh

### Backend
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```
📖 API Docs: http://localhost:8080/api/v1/swagger-ui/index.html

### Frontend
```bash
cd frontend
npm install
npm run dev
```
🌐 Web: http://localhost:5173

## 📁 Cấu trúc dự án

```
Bệnh số án/
├── backend/         # Spring Boot API
├── frontend/        # React Web App
├── database/        # SQL scripts
└── docs/            # Tài liệu
```

## 📚 Tài liệu chi tiết
Xem [docs/project-overview.md](docs/project-overview.md)

## 🔗 Repository
https://github.com/quachvietthanh/Benh-so-an.git
# Benh-so-an
