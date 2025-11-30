# Hướng dẫn Chạy Docker Compose

## Bước 1: Kiểm tra Docker

Đảm bảo Docker và Docker Compose đã được cài đặt:

```bash
docker --version
docker-compose --version
```

## Bước 2: Khởi động Infrastructure

Từ thư mục gốc của project (nơi có file `docker-compose.yml`):

```bash
# Khởi động tất cả services
docker-compose up -d

# Hoặc khởi động và xem logs
docker-compose up
```

### Services sẽ được khởi động:

1. **MySQL** (port 3306)
   - Database: `relatives_help`
   - Username: `root`
   - Password: `rootpassword`

2. **MongoDB** (port 27017)
   - Database: `relatives_help`
   - Username: `admin`
   - Password: `password`

3. **Zookeeper** (port 2181)
   - Dùng cho Kafka coordination

4. **Kafka** (port 9092)
   - Message broker cho event-driven architecture

5. **Redis** (port 6379)
   - Cache layer

6. **Elasticsearch** (port 9200)
   - Search engine

## Bước 3: Kiểm tra Services

### Kiểm tra containers đang chạy:

```bash
docker-compose ps
```

Bạn sẽ thấy tất cả containers với status `Up`:

```
NAME                        STATUS
relatives-help-mysql        Up
relatives-help-mongodb       Up
relatives-help-zookeeper     Up
relatives-help-kafka         Up
relatives-help-redis         Up
relatives-help-elasticsearch Up
```

### Kiểm tra logs:

```bash
# Xem logs của tất cả services
docker-compose logs -f

# Xem logs của một service cụ thể
docker-compose logs -f kafka
docker-compose logs -f mysql
```

### Kiểm tra health:

```bash
# MySQL
docker exec relatives-help-mysql mysqladmin ping -h localhost -u root -prootpassword

# MongoDB
docker exec relatives-help-mongodb mongosh --eval "db.adminCommand('ping')"

# Kafka
docker exec relatives-help-kafka kafka-broker-api-versions --bootstrap-server localhost:9092

# Redis
docker exec relatives-help-redis redis-cli ping

# Elasticsearch
curl http://localhost:9200/_cluster/health
```

## Bước 4: Tạo Database Schema

Sau khi MySQL đã sẵn sàng, chạy migration script:

```bash
# Copy SQL file vào container và execute
docker exec -i relatives-help-mysql mysql -uroot -prootpassword relatives_help < src/main/resources/db/migration/V1__init_schema.sql

# Hoặc connect vào MySQL và chạy thủ công
docker exec -it relatives-help-mysql mysql -uroot -prootpassword
```

Trong MySQL shell:
```sql
USE relatives_help;
SOURCE /path/to/V1__init_schema.sql;
```

## Bước 5: Chạy Application

Sau khi tất cả services đã sẵn sàng:

```bash
cd relativesHelp
./mvnw spring-boot:run
```

Hoặc với Maven:
```bash
mvn clean install
mvn spring-boot:run
```

## Bước 6: Kiểm tra Application

### Test API:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
  }'
```

### Test GraphQL:

Mở browser và truy cập:
- **GraphiQL UI**: http://localhost:8080/graphiql
- **GraphQL Endpoint**: http://localhost:8080/graphql

## Các lệnh hữu ích

### Dừng services:

```bash
# Dừng tất cả services
docker-compose stop

# Dừng và xóa containers
docker-compose down

# Dừng và xóa containers + volumes (xóa data)
docker-compose down -v
```

### Restart một service:

```bash
docker-compose restart kafka
docker-compose restart mysql
```

### Xem resource usage:

```bash
docker stats
```

### Clean up:

```bash
# Xóa tất cả containers, networks, volumes
docker-compose down -v --remove-orphans

# Xóa images không dùng
docker image prune -a
```

## Troubleshooting

### Port đã được sử dụng:

Nếu port đã được sử dụng, bạn có thể:
1. Thay đổi port trong `docker-compose.yml`
2. Hoặc dừng service đang dùng port đó

### Kafka không kết nối được:

```bash
# Kiểm tra Zookeeper đã sẵn sàng chưa
docker-compose logs zookeeper

# Restart Kafka
docker-compose restart kafka
```

### MySQL connection error:

```bash
# Kiểm tra MySQL logs
docker-compose logs mysql

# Kiểm tra MySQL đã sẵn sàng
docker exec relatives-help-mysql mysqladmin ping -h localhost -u root -prootpassword
```

### MongoDB connection error:

```bash
# Kiểm tra MongoDB logs
docker-compose logs mongodb

# Test connection
docker exec relatives-help-mongodb mongosh -u admin -p password
```

## Lưu ý

1. **Lần đầu chạy**: Có thể mất vài phút để tất cả services khởi động hoàn toàn
2. **Data persistence**: Data được lưu trong Docker volumes, sẽ không mất khi restart containers
3. **Resource**: Đảm bảo máy có đủ RAM (tối thiểu 4GB) để chạy tất cả services
4. **Network**: Tất cả services có thể giao tiếp với nhau qua Docker network

## Data Persistence

Data của các database được lưu trong **Docker Volumes** và sẽ không bị mất khi restart containers:

- **MySQL**: `mysql_data` volume → `/var/lib/mysql`
- **MongoDB**: `mongo_data` volume → `/data/db`
- **Elasticsearch**: `elasticsearch_data` volume → `/usr/share/elasticsearch/data`

### Xem volumes:

```bash
docker volume ls
```

### Backup data:

```bash
# Backup MySQL
docker exec relatives-help-mysql mysqldump -u root -prootpassword --all-databases > backup.sql

# Backup MongoDB
docker exec relatives-help-mongodb mongodump --archive --username admin --password password > mongo_backup.archive
```

⚠️ **Lưu ý**: Chạy `docker-compose down -v` sẽ **XÓA TẤT CẢ DATA** trong volumes!

Xem chi tiết trong file `DATA_PERSISTENCE.md`.

## Next Steps

Sau khi infrastructure đã chạy:
1. Chạy application Spring Boot
2. Test GraphQL queries qua GraphiQL
3. Test REST API endpoints
4. Kiểm tra Kafka events trong logs

