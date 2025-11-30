# Hệ thống Quản lý Gia phả & Quan hệ (Family Tree Management System)

## Tổng quan

Hệ thống quản lý gia phả được xây dựng với kiến trúc **modular monolithic** (có thể dễ dàng tách thành microservices sau này), sử dụng:
- **Spring Boot 3.5.7** với Java 17
- **MySQL** cho dữ liệu quan hệ
- **MongoDB** cho documents và media
- **Kafka** cho event-driven architecture (xử lý bất đồng bộ)
- **Elasticsearch** cho tìm kiếm
- **GraphQL** cho API linh hoạt
- **Redis** cho caching
- **JWT** cho authentication

## Kiến trúc

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                          │
│         (Web App / Mobile App - React/React Native)      │
└────────────────────┬─────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐ │
│  │   User   │  │  Family │  │  Event   │  │  Media   │ │
│  │  Service │  │  Service│  │  Service │  │  Service │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘ │
│       │             │              │             │        │
│       └─────────────┴──────────────┴─────────────┘        │
│                     │                                      │
│              ┌──────▼──────┐                              │
│              │   KAFKA     │                              │
│              │  PRODUCERS  │                              │
│              └──────┬──────┘                              │
└─────────────────────┼─────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                    KAFKA MESSAGE BUS                     │
│  Topics: user.registered, family.person.created, etc.   │
└────┬─────────────────────────────────────────────────────┘
     │
     ├──────────────┬──────────────┬──────────────┐
     ▼              ▼              ▼              ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│Notification│ │Analytics │ │  Media   │ │  Other   │
│  Consumer │ │ Consumer │ │ Consumer │ │ Consumers │
└───────────┘  └──────────┘  └──────────┘  └──────────┘
```

## Event-Driven Architecture với Kafka

Hệ thống sử dụng **Kafka** để xử lý các tác vụ bất đồng bộ, không chặn luồng chính:

### Luồng xử lý:

1. **User Registration:**
   - User đăng ký → Lưu vào DB → Trả về response ngay
   - Publish `user.registered` event → Kafka
   - Consumers xử lý: Welcome email, Analytics, etc.

2. **Tạo Person:**
   - Tạo person → Lưu vào DB → Trả về response ngay
   - Publish `family.person.created` event → Kafka
   - Consumers: Notify family members, Update analytics, Create default avatar

3. **Tạo Event:**
   - Tạo event → Lưu vào DB → Trả về response ngay
   - Publish `event.created` event → Kafka
   - Consumer: Schedule reminders (chạy bất đồng bộ)

4. **Event Reminders:**
   - Scheduled job kiểm tra reminders mỗi phút
   - Publish `event.reminder.due` → Kafka
   - Consumer: Gửi email/push notification

## Cài đặt và Chạy

### Yêu cầu:
- Java 17+
- Maven 3.6+
- Docker & Docker Compose

### Bước 1: Khởi động Infrastructure

```bash
docker-compose up -d
```

Services sẽ được khởi động:
- MySQL (port 3306)
- MongoDB (port 27017)
- Kafka + Zookeeper (port 9092)
- Redis (port 6379)
- Elasticsearch (port 9200)

### Bước 2: Chạy Application

```bash
cd relativesHelp
./mvnw spring-boot:run
```

Hoặc với Maven:
```bash
mvn clean install
mvn spring-boot:run
```

### Bước 3: Kiểm tra

- API: http://localhost:8080
- GraphQL: http://localhost:8080/graphql
- GraphiQL: http://localhost:8080/graphiql

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Đăng ký
- `POST /api/v1/auth/login` - Đăng nhập

### User
- `GET /api/v1/users/me` - Lấy thông tin user hiện tại
- `GET /api/v1/users/{id}` - Lấy thông tin user theo ID

### Family Tree
- `POST /api/v1/family-trees` - Tạo gia phả
- `GET /api/v1/family-trees/{id}` - Lấy thông tin gia phả
- `POST /api/v1/family-trees/{id}/persons` - Thêm thành viên
- `GET /api/v1/family-trees/{id}/persons` - Danh sách thành viên
- `POST /api/v1/family-trees/{id}/relationships` - Tạo quan hệ

### Events
- `POST /api/v1/events` - Tạo sự kiện
- `GET /api/v1/events/upcoming` - Sự kiện sắp tới

### Media
- `POST /api/v1/media/upload` - Upload media
- `GET /api/v1/media/{id}` - Lấy media

## Kafka Topics

- `user.registered` - Khi user đăng ký
- `family.person.created` - Khi tạo person mới
- `family.relationship.created` - Khi tạo relationship
- `event.created` - Khi tạo event
- `event.reminder.due` - Khi đến thời gian nhắc nhở
- `media.uploaded` - Khi upload media

## Database Schema

Xem file `src/main/resources/db/migration/V1__init_schema.sql` để biết chi tiết schema.

## Cấu hình

Cấu hình trong `application.yml`:
- Database connections
- Kafka brokers
- JWT secret
- AWS S3 (optional)

## Lưu ý

1. **Kafka là bất đồng bộ**: Các operations như gửi email, notification không chặn response
2. **Event-driven**: Services giao tiếp qua events, không gọi trực tiếp
3. **Scalable**: Có thể scale từng consumer group độc lập
4. **Resilient**: Kafka đảm bảo message delivery, có retry mechanism

## Phát triển tiếp

- [ ] Thêm GraphQL resolvers
- [ ] Tích hợp Elasticsearch search
- [ ] Thêm unit tests
- [ ] Thêm integration tests
- [ ] Setup CI/CD
- [ ] Monitoring với Prometheus/Grafana

