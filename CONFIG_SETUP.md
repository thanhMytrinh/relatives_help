# Hướng dẫn Cấu hình - Configuration Setup Guide

## ⚠️ QUAN TRỌNG: Bảo mật thông tin nhạy cảm

File này hướng dẫn cách cấu hình project một cách an toàn, không commit thông tin nhạy cảm lên Git.

## 📋 Bước 1: Tạo file cấu hình từ template

### Backend Configuration

```bash
cd relativesHelp
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

### Frontend Configuration (nếu cần)

```bash
cd relativesHelp_FE
# Tạo file .env.local nếu cần
touch .env.local
```

## 🔧 Bước 2: Điền thông tin cấu hình

### Backend - `application.yml`

Mở file `relativesHelp/src/main/resources/application.yml` và điền các thông tin sau:

#### 1. Database Configuration

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/relatives_help?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: YOUR_DB_USERNAME        # Thay bằng username của bạn
    password: YOUR_DB_PASSWORD      # Thay bằng password của bạn
```

#### 2. JWT Secret Key

```yaml
jwt:
  secret: YOUR_JWT_SECRET_KEY_SHOULD_BE_AT_LEAST_256_BITS_LONG_FOR_HS256_ALGORITHM
  expiration: 86400000 # 24 hours in milliseconds
```

**Lưu ý:** JWT secret phải có ít nhất 256 bits (32 ký tự) để đảm bảo bảo mật.

#### 3. MinIO Configuration

```yaml
minio:
  endpoint: http://localhost:9000
  access-key: YOUR_MINIO_ACCESS_KEY    # Thay bằng access key của bạn
  secret-key: YOUR_MINIO_SECRET_KEY    # Thay bằng secret key của bạn
  bucket-name: relativeshelp
  secure: false
```

**MinIO Default Credentials (development):**
- Access Key: `admin`
- Secret Key: `admin123`

⚠️ **Thay đổi ngay trong production!**

#### 4. Cloudinary Configuration (nếu sử dụng)

```yaml
cloudinary:
  cloud-name: YOUR_CLOUDINARY_CLOUD_NAME
  api-key: YOUR_CLOUDINARY_API_KEY
  api-secret: YOUR_CLOUDINARY_API_SECRET
```

#### 5. AWS S3 Configuration (nếu sử dụng thay vì MinIO)

```yaml
aws:
  s3:
    bucket-name: family-tree-media
    region: ap-southeast-1
    access-key: ${AWS_ACCESS_KEY:}      # Sử dụng environment variable
    secret-key: ${AWS_SECRET_KEY:}      # Sử dụng environment variable
```

### Frontend - Environment Variables (nếu cần)

Tạo file `.env.local` trong `relativesHelp_FE/`:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_MINIO_ENDPOINT=http://localhost:9000
```

## 🔒 Bước 3: Xác nhận .gitignore

Đảm bảo các file sau đã được ignore:

- ✅ `**/application*.yml` (trừ `.example`)
- ✅ `**/.env*`
- ✅ `**/secrets/`
- ✅ `**/credentials/`

Kiểm tra bằng lệnh:

```bash
git status
```

Nếu thấy `application.yml` trong danh sách, có nghĩa là file chưa được ignore. Kiểm tra lại `.gitignore`.

## 🚀 Bước 4: Sử dụng Environment Variables (Khuyến nghị cho Production)

Thay vì hardcode trong file, sử dụng environment variables:

### Backend

Trong `application.yml`, sử dụng `${VARIABLE_NAME:default_value}`:

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:appuser}
    password: ${DB_PASSWORD:123456}

minio:
  access-key: ${MINIO_ACCESS_KEY:admin}
  secret-key: ${MINIO_SECRET_KEY:admin123}
```

Sau đó set environment variables:

**Linux/Mac:**
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export MINIO_ACCESS_KEY=your_access_key
export MINIO_SECRET_KEY=your_secret_key
```

**Windows (PowerShell):**
```powershell
$env:DB_USERNAME="your_username"
$env:DB_PASSWORD="your_password"
$env:MINIO_ACCESS_KEY="your_access_key"
$env:MINIO_SECRET_KEY="your_secret_key"
```

**Windows (CMD):**
```cmd
set DB_USERNAME=your_username
set DB_PASSWORD=your_password
set MINIO_ACCESS_KEY=your_access_key
set MINIO_SECRET_KEY=your_secret_key
```

### Docker Compose

Có thể tạo file `.env` cho docker-compose (file này cũng nên được ignore):

```env
DB_USERNAME=your_username
DB_PASSWORD=your_password
MINIO_ACCESS_KEY=your_access_key
MINIO_SECRET_KEY=your_secret_key
```

## ✅ Checklist trước khi commit

- [ ] Đã copy `application.yml.example` thành `application.yml`
- [ ] Đã điền đầy đủ thông tin trong `application.yml`
- [ ] Đã kiểm tra `git status` - không thấy file nhạy cảm
- [ ] Đã test ứng dụng chạy được với cấu hình mới
- [ ] Đã đọc và hiểu phần bảo mật

## 🛡️ Best Practices

1. **Không bao giờ commit:**
   - Passwords
   - API keys
   - Secret keys
   - Access tokens
   - Database credentials

2. **Luôn sử dụng:**
   - Template files (`.example`, `.template`)
   - Environment variables cho production
   - Secret management services (AWS Secrets Manager, HashiCorp Vault, etc.)

3. **Rotate secrets định kỳ:**
   - Thay đổi passwords mỗi 3-6 tháng
   - Rotate API keys khi có nghi ngờ bị lộ

4. **Review .gitignore:**
   - Đảm bảo tất cả patterns đúng
   - Test bằng cách tạo file test và kiểm tra `git status`

## 📞 Hỗ trợ

Nếu gặp vấn đề về cấu hình, vui lòng:
1. Kiểm tra lại file `.gitignore`
2. Xem lại template files
3. Đọc README.md chính
4. Tạo issue trên repository

