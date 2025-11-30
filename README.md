# RelativesHelp - Hệ thống Quản lý Gia phả & Quan hệ

Hệ thống quản lý gia phả toàn diện với khả năng quản lý cây gia phả, sự kiện gia đình, và media.

## 📁 Cấu trúc Project

```
relativesHelp/
├── relativesHelp/          # Backend (Spring Boot)
│   ├── src/
│   ├── pom.xml
│   └── README.md
├── relativesHelp_FE/       # Frontend (React + Vite)
│   ├── src/
│   ├── package.json
│   └── README.md
├── .gitignore              # Git ignore cho toàn bộ project
└── README.md               # File này
```

## 🚀 Bắt đầu nhanh

### Yêu cầu hệ thống

- **Java 17+**
- **Node.js 18+** và npm/yarn
- **Docker & Docker Compose** (cho infrastructure)
- **Maven 3.6+** (hoặc sử dụng mvnw wrapper)

### 1. Clone repository

```bash
git clone <repository-url>
cd relativesHelp
```

### 2. Cấu hình Backend

1. Copy file template cấu hình:
   ```bash
   cd relativesHelp
   cp src/main/resources/application.yml.example src/main/resources/application.yml
   ```

2. Chỉnh sửa `application.yml` với thông tin của bạn:
   - Database credentials
   - JWT secret key
   - MinIO credentials
   - Cloudinary API keys (nếu sử dụng)
   - AWS credentials (nếu sử dụng S3)

3. Khởi động infrastructure:
   ```bash
   cd relativesHelp
   docker-compose up -d
   ```

4. Chạy backend:
   ```bash
   ./mvnw spring-boot:run
   ```

Backend sẽ chạy tại: http://localhost:8080

### 3. Cấu hình Frontend

1. Cài đặt dependencies:
   ```bash
   cd relativesHelp_FE
   npm install
   ```

2. Chạy development server:
   ```bash
   npm run dev
   ```

Frontend sẽ chạy tại: http://localhost:5173

## 📝 Cấu hình

### Backend Configuration

File cấu hình chính: `relativesHelp/src/main/resources/application.yml`

**⚠️ LƯU Ý QUAN TRỌNG:**
- File `application.yml` chứa thông tin nhạy cảm (passwords, API keys)
- **KHÔNG** commit file này lên Git
- Sử dụng `application.yml.example` làm template
- Trong production, sử dụng environment variables hoặc secret management

### Frontend Configuration

File cấu hình: `relativesHelp_FE/vite.config.js`

API endpoint được cấu hình trong: `relativesHelp_FE/src/services/apiClient.js`

## 🔐 Bảo mật

### Files nhạy cảm được ignore:

- `**/application*.yml` (trừ `.example` và `.template`)
- `**/.env*`
- `**/secrets/`
- `**/credentials/`

### Best Practices:

1. **Không commit secrets**: Luôn sử dụng environment variables hoặc secret management
2. **Sử dụng template files**: Copy từ `.example` files và điền thông tin của bạn
3. **Review .gitignore**: Đảm bảo tất cả files nhạy cảm đã được ignore
4. **Rotate secrets**: Thay đổi passwords và API keys định kỳ

## 🛠️ Development

### Backend

```bash
cd relativesHelp
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend

```bash
cd relativesHelp_FE
npm install
npm run dev
```

### Build Production

**Backend:**
```bash
cd relativesHelp
./mvnw clean package
java -jar target/relativesHelp-*.jar
```

**Frontend:**
```bash
cd relativesHelp_FE
npm run build
# Output trong thư mục dist/
```

## 📚 Tài liệu

- [Backend README](relativesHelp/README.md) - Chi tiết về backend architecture
- [Frontend README](relativesHelp_FE/FRONTEND_SETUP.md) - Hướng dẫn setup frontend
- [Docker Setup](relativesHelp/DOCKER_SETUP.md) - Hướng dẫn Docker
- [GraphQL Guide](relativesHelp/GRAPHQL_GUIDE.md) - Hướng dẫn GraphQL API

## 🧪 Testing

### Backend Tests
```bash
cd relativesHelp
./mvnw test
```

### Frontend Tests
```bash
cd relativesHelp_FE
npm test
```

## 📦 Infrastructure Services

Docker Compose sẽ khởi động các services sau:

- **MySQL** (port 3307) - Database chính
- **MongoDB** (port 27017) - Document store
- **Kafka** (port 9092) - Message broker
- **Zookeeper** (port 2181) - Kafka coordination
- **Redis** (port 6379) - Cache
- **Elasticsearch** (port 9200) - Search engine
- **MinIO** (port 9000) - Object storage

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

[Thêm license của bạn ở đây]

## 👥 Authors

[Thêm tên tác giả ở đây]

## 🙏 Acknowledgments

[Thêm acknowledgments nếu có]

