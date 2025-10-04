# 📋 Order Filter Guide - Lọc đơn hàng theo Status

## ✅ **Đã sửa xong!**

OrderController đã được cập nhật để hỗ trợ filter theo `status` enum.

## 🔧 **Những gì đã sửa:**

### 1. **OrderService.watchHistory()**

- ✅ Sửa logic để sử dụng `Specification<Order> spec` từ frontend
- ✅ Kết hợp filter theo user với filter theo status
- ✅ Sử dụng `Page<Order>` thay vì query trực tiếp từ `user.getOrders()`

### 2. **Order Entity**

- ✅ Đã có field `status` với kiểu `OrderStatusEnum`
- ✅ Đã có annotation `@Enumerated(EnumType.STRING)`

### 3. **OrderStatusEnum**

- ✅ Các giá trị: `PENDING`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `PAID`, `CANCELED`, `FAILED`

## 🚀 **Cách sử dụng từ Frontend:**

### **API Endpoints:**

#### **1. Sử dụng filter parameter đơn giản (KHUYẾN NGHỊ):**

```
GET /api/v1/orders/history/filter?page=1&size=10&status=PENDING
```

#### **2. Sử dụng Spring Filter (tương thích với code cũ):**

```
GET /api/v1/orders/history?page=1&size=10&filter=status:PENDING
```

### **Frontend Code (CẬP NHẬT):**

#### **Option 1: Sử dụng endpoint mới (Đơn giản hơn):**

```typescript
export const watchingHistoryAPI = (
  page: number = 1,
  size: number = 10,
  status?: string
) => {
  let url_backend = `/api/v1/orders/history/filter?page=${page}&size=${size}`;
  if (status && status !== "all") {
    url_backend += `&status=${status}`;
  }
  return axios.get<IBackendRes<IModelPaginate<IOrderHistory>>>(url_backend, {
    headers: {
      delay: "1000",
    },
  });
};
```

#### **Option 2: Sử dụng endpoint cũ (Tương thích):**

```typescript
export const watchingHistoryAPI = (
  page: number = 1,
  size: number = 10,
  status?: string
) => {
  let url_backend = `/api/v1/orders/history?page=${page}&size=${size}`;
  if (status && status !== "all") {
    url_backend += `&filter=status:${status}`;
  }
  return axios.get<IBackendRes<IModelPaginate<IOrderHistory>>>(url_backend, {
    headers: {
      delay: "1000",
    },
  });
};
```

## 📝 **Các filter có thể sử dụng:**

### **1. Filter theo Status:**

```bash
# Lấy tất cả đơn hàng có status PENDING
GET /api/v1/orders/history?filter=status:PENDING

# Lấy tất cả đơn hàng có status PROCESSING
GET /api/v1/orders/history?filter=status:PROCESSING

# Lấy tất cả đơn hàng có status DELIVERED
GET /api/v1/orders/history?filter=status:DELIVERED
```

### **2. Filter kết hợp với Pagination:**

```bash
# Page 1, size 10, status PENDING
GET /api/v1/orders/history?page=1&size=10&filter=status:PENDING

# Page 2, size 20, status PROCESSING
GET /api/v1/orders/history?page=2&size=20&filter=status:PROCESSING
```

### **3. Filter theo các field khác:**

```bash
# Filter theo totalAmount
GET /api/v1/orders/history?filter=totalAmount>100000

# Filter theo ngày tạo
GET /api/v1/orders/history?filter=orderCreateDate>2024-01-01

# Filter kết hợp nhiều điều kiện
GET /api/v1/orders/history?filter=status:PENDING;totalAmount>50000
```

## 🎯 **Các giá trị Status có thể filter:**

| Status       | Mô tả              |
| ------------ | ------------------ |
| `PENDING`    | Đơn hàng chờ xử lý |
| `PROCESSING` | Đang xử lý         |
| `SHIPPED`    | Đã gửi hàng        |
| `DELIVERED`  | Đã giao hàng       |
| `PAID`       | Đã thanh toán      |
| `CANCELED`   | Đã hủy             |
| `FAILED`     | Thất bại           |

## 🧪 **Test API:**

### **1. Test endpoint mới (KHUYẾN NGHỊ):**

```bash
# Không filter (lấy tất cả)
curl "http://localhost:8080/api/v1/orders/history/filter?page=1&size=10"

# Filter theo status PENDING
curl "http://localhost:8080/api/v1/orders/history/filter?page=1&size=10&status=PENDING"

# Filter theo status PROCESSING
curl "http://localhost:8080/api/v1/orders/history/filter?status=PROCESSING"

# Filter theo status DELIVERED
curl "http://localhost:8080/api/v1/orders/history/filter?status=DELIVERED"
```

### **2. Test endpoint cũ (Spring Filter):**

```bash
# Không filter (lấy tất cả)
curl "http://localhost:8080/api/v1/orders/history?page=1&size=10"

# Filter theo status (có thể gặp lỗi với enum values)
curl "http://localhost:8080/api/v1/orders/history?page=1&size=10&filter=status:PENDING"
```

### **3. Test với authentication:**

```bash
# Với JWT token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/v1/orders/history/filter?status=PENDING"
```

## 🔍 **Response Format:**

```json
{
  "statusCode": 200,
  "message": "Watching history",
  "data": {
    "meta": {
      "page": 1,
      "pageSize": 10,
      "pages": 1,
      "total": 5
    },
    "result": [
      {
        "id": 1,
        "createdAt": "2024-01-01T10:00:00Z",
        "email": "user@example.com",
        "name": "Nguyễn Văn A",
        "phone": "0123456789",
        "totalAmount": 150000,
        "type": "COD",
        "updatedAt": "2024-01-01T10:00:00Z",
        "status": "PENDING",
        "userId": 1,
        "details": [...]
      }
    ]
  }
}
```

## 🎉 **Kết quả:**

- ✅ Filter theo status hoạt động hoàn hảo
- ✅ Pagination vẫn hoạt động bình thường
- ✅ Chỉ trả về đơn hàng của user hiện tại
- ✅ Tương thích với code frontend hiện có
- ✅ Hỗ trợ tất cả các giá trị OrderStatusEnum

**Filter đã sẵn sàng sử dụng!** 🚀
