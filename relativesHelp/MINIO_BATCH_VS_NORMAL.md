# So sánh Batch Operations vs Upload/Delete thông thường

## 📊 Tổng quan

| Tiêu chí | Upload/Delete thông thường | Batch Operations |
|----------|---------------------------|------------------|
| **Số lượng file** | 1 file mỗi request | Nhiều files trong 1 request |
| **HTTP Requests** | 1 request = 1 file | 1 request = nhiều files |
| **Error Handling** | Fail toàn bộ nếu lỗi | Tiếp tục với các file khác |
| **Response Time** | Nhanh cho 1 file | Chậm hơn nhưng hiệu quả hơn cho nhiều files |
| **Network Overhead** | Cao (nhiều requests) | Thấp (1 request) |
| **Transaction** | Atomic (all or nothing) | Partial success possible |
| **Use Case** | Upload/delete đơn lẻ | Upload/delete hàng loạt |

---

## 🔄 Upload Operations

### Upload thông thường (Single Upload)

**API Endpoint:**
```
POST /api/v1/minio/upload
POST /api/v1/minio/upload/{fileId}
```

**Đặc điểm:**
- ✅ **Ưu điểm:**
  - Đơn giản, dễ sử dụng
  - Error handling rõ ràng (fail ngay nếu có lỗi)
  - Phù hợp cho upload 1-2 files
  - Response nhanh
  - Dễ debug khi có lỗi
  
- ❌ **Nhược điểm:**
  - Phải gọi nhiều API calls cho nhiều files
  - Network overhead cao
  - Chậm khi upload nhiều files
  - Không tối ưu cho bulk operations

**Ví dụ:**
```bash
# Upload 1 file
curl -X POST "http://localhost:8080/api/v1/minio/upload?folderPath=family/123" \
  -F "file=@image1.jpg"

# Upload 3 files = 3 API calls
curl -X POST "http://localhost:8080/api/v1/minio/upload?folderPath=family/123" \
  -F "file=@image1.jpg"
curl -X POST "http://localhost:8080/api/v1/minio/upload?folderPath=family/123" \
  -F "file=@image2.jpg"
curl -X POST "http://localhost:8080/api/v1/minio/upload?folderPath=family/123" \
  -F "file=@image3.jpg"
```

**Implementation:**
```java
public String uploadFile(MultipartFile file, String folderPath) {
    // Upload 1 file, throw exception nếu fail
    minioClient.putObject(...);
    return fileUrl;
}
```

---

### Batch Upload

**API Endpoint:**
```
POST /api/v1/minio/batch/upload
```

**Đặc điểm:**
- ✅ **Ưu điểm:**
  - 1 API call cho nhiều files
  - Giảm network overhead
  - Hiệu quả cho bulk operations
  - Tiếp tục upload các file khác nếu 1 file fail
  - Response tổng hợp (số lượng thành công/thất bại)
  
- ❌ **Nhược điểm:**
  - Phức tạp hơn (cần xử lý partial success)
  - Response time lâu hơn (phải đợi tất cả files)
  - Khó debug khi có lỗi ở file cụ thể
  - Có thể timeout nếu upload quá nhiều files lớn

**Ví dụ:**
```bash
# Upload nhiều files trong 1 request
curl -X POST "http://localhost:8080/api/v1/minio/batch/upload?folderPath=family/123" \
  -F "files=@image1.jpg" \
  -F "files=@image2.jpg" \
  -F "files=@image3.jpg" \
  -F "files=@image4.jpg" \
  -F "files=@image5.jpg"
```

**Response:**
```json
{
  "success": true,
  "data": {
    "uploaded": 5,
    "total": 5,
    "urls": [
      "http://localhost:9000/relativeshelp/family/123/uuid1.jpg",
      "http://localhost:9000/relativeshelp/family/123/uuid2.jpg",
      ...
    ]
  }
}
```

**Implementation:**
```java
public List<String> batchUploadFiles(List<MultipartFile> files, String folderPath) {
    List<String> urls = new ArrayList<>();
    for (MultipartFile file : files) {
        try {
            String url = uploadFile(file, folderPath); // Gọi upload thông thường
            urls.add(url);
        } catch (Exception e) {
            // Log error nhưng tiếp tục với file khác
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
        }
    }
    return urls;
}
```

---

## 🗑️ Delete Operations

### Delete thông thường (Single Delete)

**API Endpoint:**
```
DELETE /api/v1/minio/files/{fileId}
```

**Đặc điểm:**
- ✅ **Ưu điểm:**
  - Đơn giản, rõ ràng
  - Error handling tốt
  - Phù hợp cho delete 1-2 files
  - Response nhanh
  
- ❌ **Nhược điểm:**
  - Nhiều API calls cho nhiều files
  - Network overhead cao
  - Chậm khi delete nhiều files

**Ví dụ:**
```bash
# Delete 1 file
curl -X DELETE "http://localhost:8080/api/v1/minio/files/file-123?folderPath=family/456"

# Delete 3 files = 3 API calls
curl -X DELETE "http://localhost:8080/api/v1/minio/files/file-1?folderPath=family/456"
curl -X DELETE "http://localhost:8080/api/v1/minio/files/file-2?folderPath=family/456"
curl -X DELETE "http://localhost:8080/api/v1/minio/files/file-3?folderPath=family/456"
```

**Implementation:**
```java
public void deleteFile(String objectName) {
    // Delete 1 file, throw exception nếu fail
    minioClient.removeObject(...);
}
```

---

### Batch Delete

**API Endpoint:**
```
DELETE /api/v1/minio/batch/delete
```

**Đặc điểm:**
- ✅ **Ưu điểm:**
  - 1 API call cho nhiều files
  - Giảm network overhead
  - Hiệu quả cho bulk delete
  - Tiếp tục delete các file khác nếu 1 file fail
  
- ❌ **Nhược điểm:**
  - Phức tạp hơn
  - Response time lâu hơn
  - Khó biết file nào delete thành công/thất bại

**Ví dụ:**
```bash
# Delete nhiều files trong 1 request
curl -X DELETE "http://localhost:8080/api/v1/minio/batch/delete" \
  -H "Content-Type: application/json" \
  -d '[
    "family/123/file1.jpg",
    "family/123/file2.jpg",
    "family/123/file3.jpg"
  ]'
```

**Response:**
```json
{
  "success": true,
  "data": {
    "deleted": 3
  }
}
```

**Implementation:**
```java
public void batchDeleteFiles(List<String> objectNames) {
    for (String objectName : objectNames) {
        try {
            deleteFile(objectName); // Gọi delete thông thường
        } catch (Exception e) {
            // Log error nhưng tiếp tục với file khác
            log.error("Failed to delete file: {}", objectName, e);
        }
    }
}
```

---

## 📈 Performance Comparison

### Scenario: Upload 10 files (mỗi file 1MB)

**Upload thông thường:**
```
- 10 HTTP requests
- Network overhead: ~10KB × 10 = 100KB
- Total time: ~10 seconds (1s/file)
- Success rate: All or nothing
```

**Batch Upload:**
```
- 1 HTTP request
- Network overhead: ~10KB
- Total time: ~8 seconds (parallel processing)
- Success rate: Partial (có thể 8/10 thành công)
```

**Kết luận:** Batch nhanh hơn ~20% và tiết kiệm network ~90%

---

## 🎯 Khi nào dùng cái nào?

### Dùng Upload/Delete thông thường khi:
- ✅ Upload/delete 1-2 files
- ✅ Cần error handling chặt chẽ (all or nothing)
- ✅ Cần biết chính xác file nào fail
- ✅ Upload file lớn (cần progress tracking)
- ✅ Cần transaction atomic
- ✅ Real-time upload (user upload từng file)

### Dùng Batch Operations khi:
- ✅ Upload/delete nhiều files cùng lúc (3+ files)
- ✅ Import/export data
- ✅ Bulk operations
- ✅ Background jobs
- ✅ Migration data
- ✅ Cleanup operations
- ✅ Album upload (nhiều ảnh cùng lúc)

---

## 🔧 Cải thiện Batch Operations (Future Enhancement)

### 1. **Parallel Processing**
```java
public List<String> batchUploadFilesParallel(List<MultipartFile> files, String folderPath) {
    return files.parallelStream()
        .map(file -> {
            try {
                return uploadFile(file, folderPath);
            } catch (Exception e) {
                log.error("Failed to upload: {}", file.getOriginalFilename(), e);
                return null;
            }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
}
```

### 2. **Progress Tracking**
```java
public class BatchUploadProgress {
    private int total;
    private int completed;
    private int failed;
    private List<String> urls;
    private List<String> errors;
}
```

### 3. **Transaction Support**
```java
public List<String> batchUploadWithTransaction(List<MultipartFile> files) {
    // Rollback tất cả nếu có lỗi
    try {
        return files.stream()
            .map(file -> uploadFile(file, folderPath))
            .collect(Collectors.toList());
    } catch (Exception e) {
        // Rollback: delete tất cả files đã upload
        rollbackUploadedFiles();
        throw e;
    }
}
```

### 4. **Chunked Upload cho file lớn**
```java
public void uploadLargeFile(MultipartFile file, String folderPath) {
    // Chia file thành chunks và upload từng chunk
    // Sau đó merge lại
}
```

---

## 📝 Best Practices

### Upload thông thường:
1. Validate file trước khi upload
2. Set proper content-type
3. Handle errors gracefully
4. Log upload activities

### Batch Operations:
1. Validate tất cả files trước khi upload
2. Set timeout phù hợp
3. Implement retry mechanism
4. Track progress cho user
5. Handle partial success
6. Log chi tiết từng file

---

## 🚀 Kết luận

**Upload/Delete thông thường:**
- Phù hợp cho: Single file operations, real-time upload
- Ưu điểm: Đơn giản, reliable, dễ debug
- Nhược điểm: Không hiệu quả cho bulk operations

**Batch Operations:**
- Phù hợp cho: Bulk operations, import/export, background jobs
- Ưu điểm: Hiệu quả, giảm network overhead
- Nhược điểm: Phức tạp hơn, cần xử lý partial success

**Khuyến nghị:** 
- Dùng upload/delete thông thường cho user-facing operations
- Dùng batch operations cho admin tools, bulk imports, background jobs

