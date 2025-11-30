# Data Persistence trong Docker Compose

## Tổng quan

Khi sử dụng Docker Compose, data của các database được lưu trữ trong **Docker Volumes**. Volumes là cơ chế được Docker khuyến nghị để lưu trữ data bền vững (persistent data).

## Cấu trúc Volumes trong Project

Trong file `docker-compose.yml`, chúng ta có 3 volumes được định nghĩa:

```yaml
volumes:
  mysql_data:
  mongo_data:
  elasticsearch_data:
```

## Chi tiết từng Database

### 1. MySQL Data

```yaml
mysql:
  volumes:
    - mysql_data:/var/lib/mysql
```

- **Volume name**: `mysql_data`
- **Mount point trong container**: `/var/lib/mysql` (thư mục mặc định của MySQL)
- **Vị trí lưu trữ thực tế**: Docker quản lý tự động

**Data được lưu:**
- Tất cả databases và tables
- User accounts và permissions
- Indexes và constraints
- Transaction logs

### 2. MongoDB Data

```yaml
mongodb:
  volumes:
    - mongo_data:/data/db
```

- **Volume name**: `mongo_data`
- **Mount point trong container**: `/data/db` (thư mục mặc định của MongoDB)
- **Vị trí lưu trữ thực tế**: Docker quản lý tự động

**Data được lưu:**
- Tất cả collections và documents
- Indexes
- GridFS files (nếu có)
- Replica set configuration (nếu có)

### 3. Elasticsearch Data

```yaml
elasticsearch:
  volumes:
    - elasticsearch_data:/usr/share/elasticsearch/data
```

- **Volume name**: `elasticsearch_data`
- **Mount point trong container**: `/usr/share/elasticsearch/data`
- **Vị trí lưu trữ thực tế**: Docker quản lý tự động

**Data được lưu:**
- Indices và documents
- Cluster state
- Index metadata

## Vị trí lưu trữ thực tế

### Trên Windows

Docker Desktop trên Windows sử dụng WSL2 (Windows Subsystem for Linux 2). Volumes được lưu tại:

```
\\wsl$\docker-desktop-data\data\docker\volumes\
```

Hoặc trong WSL2:
```bash
# Vào WSL2 terminal
cd /var/lib/docker/volumes/
```

### Trên Linux/Mac

```bash
/var/lib/docker/volumes/
```

### Xem vị trí cụ thể của volume

```bash
# Xem danh sách volumes
docker volume ls

# Xem thông tin chi tiết volume
docker volume inspect relatives-help_mysql_data
docker volume inspect relatives-help_mongo_data
docker volume inspect relatives-help_elasticsearch_data
```

Output sẽ cho biết `Mountpoint` - đây là nơi data thực sự được lưu.

## Tính chất của Volumes

### ✅ Ưu điểm

1. **Persistent**: Data không bị mất khi container bị xóa
2. **Tách biệt**: Data tách biệt với container lifecycle
3. **Shareable**: Có thể chia sẻ giữa nhiều containers
4. **Backup dễ dàng**: Có thể backup/restore volumes
5. **Performance**: Tốt hơn bind mounts cho database

### 🔄 Lifecycle

- **Tạo volume**: Khi chạy `docker-compose up` lần đầu
- **Giữ data**: Khi restart containers (`docker-compose restart`)
- **Giữ data**: Khi stop containers (`docker-compose stop`)
- **Xóa data**: Khi chạy `docker-compose down -v` (flag `-v` xóa volumes)

## Các lệnh quản lý Volumes

### 1. Xem danh sách volumes

```bash
docker volume ls
```

Output:
```
DRIVER    VOLUME NAME
local     relatives-help_elasticsearch_data
local     relatives-help_mongo_data
local     relatives-help_mysql_data
```

### 2. Xem thông tin chi tiết

```bash
docker volume inspect relatives-help_mysql_data
```

### 3. Backup Volume

#### Backup MySQL:

```bash
# Backup
docker exec relatives-help-mysql mysqldump -u root -prootpassword --all-databases > backup.sql

# Hoặc backup volume trực tiếp
docker run --rm -v relatives-help_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```

#### Backup MongoDB:

```bash
# Backup
docker exec relatives-help-mongodb mongodump --out /backup --username admin --password password

# Hoặc backup volume
docker run --rm -v relatives-help_mongo_data:/data -v $(pwd):/backup alpine tar czf /backup/mongo_backup.tar.gz /data
```

### 4. Restore Volume

#### Restore MySQL:

```bash
# Restore từ SQL dump
docker exec -i relatives-help-mysql mysql -u root -prootpassword < backup.sql

# Hoặc restore từ volume backup
docker run --rm -v relatives-help_mysql_data:/data -v $(pwd):/backup alpine tar xzf /backup/mysql_backup.tar.gz -C /
```

### 5. Xóa Volume (⚠️ Cẩn thận - mất data)

```bash
# Xóa một volume cụ thể
docker volume rm relatives-help_mysql_data

# Xóa tất cả volumes không dùng
docker volume prune
```

## Backup Strategy

### 1. Backup định kỳ

Tạo script backup tự động:

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="./backups"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup MySQL
docker exec relatives-help-mysql mysqldump -u root -prootpassword --all-databases > $BACKUP_DIR/mysql_$DATE.sql

# Backup MongoDB
docker exec relatives-help-mongodb mongodump --archive --username admin --password password > $BACKUP_DIR/mongo_$DATE.archive

echo "Backup completed: $DATE"
```

### 2. Sử dụng named volumes với path cụ thể (Advanced)

Nếu muốn lưu data ở vị trí cụ thể, có thể sử dụng bind mount:

```yaml
mysql:
  volumes:
    - ./data/mysql:/var/lib/mysql  # Lưu vào thư mục ./data/mysql
```

⚠️ **Lưu ý**: Bind mounts có thể có vấn đề về permissions và performance.

## Kiểm tra Data

### MySQL

```bash
# Vào MySQL container
docker exec -it relatives-help-mysql mysql -u root -prootpassword

# Xem databases
SHOW DATABASES;

# Xem tables
USE relatives_help;
SHOW TABLES;
```

### MongoDB

```bash
# Vào MongoDB container
docker exec -it relatives-help-mongodb mongosh -u admin -p password

# Xem databases
show dbs

# Xem collections
use relatives_help
show collections
```

### Elasticsearch

```bash
# Kiểm tra indices
curl http://localhost:9200/_cat/indices?v
```

## Migration Data

### Copy data giữa environments

```bash
# Export từ production
docker exec relatives-help-mysql mysqldump -u root -prootpassword relatives_help > prod_data.sql

# Import vào development
docker exec -i relatives-help-mysql mysql -u root -prootpassword relatives_help < prod_data.sql
```

## Troubleshooting

### Volume đầy

```bash
# Xem dung lượng volumes
docker system df -v

# Dọn dẹp
docker system prune -a --volumes
```

### Permission issues

```bash
# Sửa permissions (Linux/Mac)
sudo chown -R 999:999 /var/lib/docker/volumes/relatives-help_mysql_data/_data
```

### Data không persist

Kiểm tra:
1. Volume đã được mount chưa: `docker inspect relatives-help-mysql`
2. Container có đang chạy không: `docker-compose ps`
3. Volume có tồn tại không: `docker volume ls`

## Best Practices

1. **Backup định kỳ**: Setup backup tự động hàng ngày/tuần
2. **Version control**: Không commit volumes vào git
3. **Documentation**: Ghi lại cấu trúc data và backup procedures
4. **Testing**: Test restore process định kỳ
5. **Monitoring**: Monitor dung lượng volumes

## Tóm tắt

- ✅ Data được lưu trong Docker Volumes (bền vững)
- ✅ Volumes tồn tại độc lập với containers
- ✅ Data không mất khi restart/stop containers
- ⚠️ Data sẽ mất nếu chạy `docker-compose down -v`
- 📦 Volumes được Docker quản lý tự động
- 🔄 Có thể backup/restore volumes dễ dàng

