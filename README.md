# Language Center Management System

## 1. Giới Thiệu
Hệ thống được cài đặt nhằm mục tiêu cung cấp một công cụ để quản lý tập trung các hoạt động hàng ngày của trung tâm ngoại ngữ bao gồm: quản lý học viên, giáo viên, khóa học, lớp học, lịch học, điểm danh, kết quả học tập, hoá đơn và thanh toán.
---

## 2. Công Nghệ Sử Dụng
### Backend
- **Java 21**: Ngôn ngữ lập trình chính
- **Hibernate/JPA 6.5.2**: ORM (Object-Relational Mapping) để quản lý dữ liệu
- **Jakarta Persistence API 3.1.0**: Tiêu chuẩn JPA
- **MySQL 9.2.0**: Cơ sở dữ liệu quan hệ
- **Maven**: Build tool

### Frontend
- **Swing**: Framework GUI cho giao diện người dùng desktop

### Bảo Mật & Tiện Ích
- **Lombok 1.18.42**: Hỗ trợ low code
- **bcrypt 0.10.2**: Mã hóa mật khẩu an toàn
- **SLF4J & Logback**: Logging framework
- **Jakarta Persistence**: API chuẩn để làm việc với cơ sở dữ liệu

---

## 3. Cấu Trúc Dự Án

```
Language-Center-Management-System/
├── pom.xml                          # Maven configuration        
├── sql/
│   └── schema_and_data_script.sql   # Cơ sở dữ liệu - schema và dữ liệu mẫu
├── rbac/
│   └── rbac.md                      # Tài liệu phân quyền
└── src/
    └── main/
       ├── java/com/
       │   ├── Main.java                        # Điểm vào ứng dụng
       │   ├── db/
       │   │   └── JpaUtil.java                 # Utility cho JPA/Hibernate
       │   ├── model/                           # Các entity model
       │   │   ├── academic/                    # Entities học tập
       │   │   ├── financial/                   # Entities tài chính
       │   │   ├── operation/                   # Entities vận hành
       │   │   └── user/                        # Entities người dùng
       │   ├── dto/                             # Data Transfer Objects
       │   ├── repository/                      # Data Access Layer
       │   ├── service/                         # Business Logic Layer
       │   ├── security/                        # Quản lý bảo mật & phân quyền
       │   ├── exception/                       # Exception handling
       │   ├── stream/                          # Stream queries
       │   ├── ui/                              # Giao diện người dùng
       │   │   ├── frame/                       # Các cửa sổ chính
       │   │   ├── dialog/                      # Các hộp thoại
       │   │   ├── panel/                       # Các bảng điều khiển
       │   │   ├── table/                       # Bảng dữ liệu
       │   │   ├── chart/                       # Biểu đồ
       │   │   └── util/                        # Tiện ích UI
       │   └── utils/                           # Các hàm tiện ích chung
       └── resources/
           ├── logback.xml                      # Cấu hình logging
           └── META-INF/persistence.xml         # Cấu hình JPA persistence

```

---

## 4. Các Thành Phần Chính

### 4.1. **Model Layer (Entities)**

#### Academic Module (`model/academic/`)
- **Course**: Entity Khóa học
- **Class**: Entity Lớp học
- **Enrollment**: Entity Đăng ký lớp học
- **Schedule**: Entity Lịch học
- **Attendance**:  Entity Điểm danh
- **Result**: Entity Kết quả học tập

#### Financial Module (`model/financial/`)
- **Invoice**: Entity Hoá đơn
- **Payment**: Entity Thanh toán

#### Operation Module (`model/operation/`)
- **Room**: Entity Phòng học

#### User Module (`model/user/`)
- **Student**: Entity Học viên
- **Teacher**: Entity Giáo viên
- **Staff**: Entity Nhân viên
- **UserAccount**: Entity Tài khoản người dùng (cho đăng nhập)

### 4.2. **Repository Layer - Truy Cập Dữ Liệu**

Mỗi entity có một repository tương ứng để quản lý các thao tác CRUD:
- `BaseRepository.java`: Lớp cơ sở cung cấp các phương thức chung
- `StudentRepository.java`, `TeacherRepository.java`, `ClassRepository.java`, v.v.

### 4.3. **DTO Layer - Truyền Dữ Liệu**

Data Transfer Objects được sử dụng để truyền dữ liệu giữa các lớp:
- `StudentDTO`, `TeacherDTO`, `ClassDTO`, `EnrollmentDTO`, v.v.
- Giúp tách biệt model từ giao diện người dùng
- Hỗ trợ xác thực dữ liệu trước khi gửi đến database

### 4.4. **Service Layer - Nghiệp Vụ**

Xử lý logic business của ứng dụng:
- `BaseService.java`: Lớp cơ sở với các phương thức chung
- `StudentServiceImpl.java`: Quản lý học viên
- `TeacherServiceImpl.java`: Quản lý giáo viên
- `CourseServiceImpl.java`: Quản lý khóa học
- `ClassServiceImpl.java`: Quản lý lớp học
- `RoomServiceImpl.java`: Quản lý phòng học
- `EnrollmentServiceImpl.java`: Quản lý đăng ký
- `ResultServiceImpl.java`: Quản lý kết quả học tập của học viên
- `AttendanceServiceImpl.java`: Quản lý điểm danh học viên
- `AuthServiceImpl.java`: Xác thực người dùng
- `PaymentServiceImpl.java`, `InvoiceServiceImpl.java`: Quản lý tài chính

### 4.5. **Security Layer - Bảo Mật**

Quản lý phân quyền và xác thực:
- `CurrentUser.java`: Lưu thông tin người dùng hiện tại
- `SecurityContext.java`: Lưu bối cảnh bảo mật
- `PermissionChecker.java`: Kiểm tra quyền truy cập
- Hỗ trợ 5 vai trò: ADMIN, STAFF_CONSULTANT, STAFF_ACCOUNTANT, TEACHER, STUDENT

### 4.6. **Stream Queries - Truy Vấn Dữ Liệu**

Các lớp truy vấn sử dụng Java Stream API cung cấp cách viết query linh hoạt, ngắn gọn:
- `StudentStreamQueries.java`: Truy vấn học viên
- `ClassStreamQueries.java`: Truy vấn lớp học
- `EnrollmentStreamQueries.java`: Truy vấn đăng ký
- v.v.

### 4.7. **UI Layer - Giao Diện Người Dùng**

Giao diện desktop sử dụng Swing:
- **Frames** (`ui/frame/`): Cửa sổ chính, MainFrame, LoginFrame v.v.
- **Dialogs** (`ui/dialog/`): Hộp thoại cho các tác vụ
- **Panels** (`ui/panel/`): Các bảng điều khiển chức năng
- **Tables** (`ui/table/`): Bảng hiển thị dữ liệu
- **Charts** (`ui/chart/`): Biểu đồ thống kê
- **Utilities** (`ui/util/`): Tiện ích UI

### 4.8. **Exception Handling - Xử Lý Lỗi**

Các lớp xử lý ngoại lệ tập trung:
- `GlobalExceptionHandler.java`: Xử lý ngoại lệ phạm vi global
- `AppException.java`: Ngoại lệ ứng dụng cơ sở
- `BusinessException.java`: Lỗi logic business
- `ValidationException.java`: Lỗi xác thực dữ liệu
- `SystemException.java`: Lỗi hệ thống
- `DataInUseException.java`: Lỗi dữ liệu đang được sử dụng

### 4.9. **Utilities - Hàm Hỗ Trợ**

- `utils/`: Các hàm tiện ích chung như xuất file excel

---

## 5 Hệ Thống Phân Quyền (RBAC)

Ứng dụng hỗ trợ **5 vai trò** với các quyền khác nhau:

### Vai Trò
1. **ADMIN** - Quản trị viên: Toàn quyền
2. **STAFF_CONSULTANT** - Nhân viên tư vấn: Quản lý học viên, khóa học, lớp
3. **STAFF_ACCOUNTANT** - Nhân viên kế toán: Quản lý hoá đơn, thanh toán
4. **TEACHER** - Giáo viên: Quản lý lớp của mình, điểm danh, đánh giá kết quả học tập của học viên
5. **STUDENT** - Học viên: Xem thông tin cá nhân, lớp học, kết quả học, thông tin thanh toán
---

## 6. Kiến Trúc Ứng Dụng

### Luồng Dữ Liệu

```
UI (Swing) 
    |
    V
Service Layer (Business Logic)
    |
    V
Repository Layer (Data Access)
    |
    V
Hibernate ORM
    |
    V
MySQL Database
```

### Cơ Chế Xác Thực & Phân Quyền

```
Login (AuthService)
    |
    V
Tạo CurrentUser Session
    |
    V
PermissionChecker kiểm tra quyền
    |
    V
Cho phép/Từ chối truy cập
```

---

## 7. Cơ Sở Dữ Liệu

### Vị Trí
- Tệp schema: `sql/schema_and_data_script.sql`
- Cơ sở dữ liệu: MySQL
- Chứa: Schema định nghĩa bảng và dữ liệu mẫu

### Bảng Chính
- **user_account**: Tài khoản người dùng
- **student**: Thông tin học viên
- **teacher**: Thông tin giáo viên
- **staff**: Thông tin nhân viên
- **course**: Khóa học
- **class**: Lớp học
- **enrollment**: Đăng ký lớp
- **room**: Phòng học
- **schedule**: Lịch biểu
- **attendance**: Điểm danh
- **result**: Kết quả học tập
- **invoice**: Hoá đơn
- **payment**: Thanh toán

### Xóa Mềm (Soft Delete)
- Các bảng: `user_account`, `student`, `teacher`, `staff`, `invoice`, `payment`
- Sử dụng cột `is_deleted` thay vì xóa vật lý
- Chỉ ADMIN có quyền xóa

---

## 8. Hướng Dẫn Cài Đặt & Chạy

### Yêu Cầu Hệ Thống
- Java 21 trở lên
- MySQL 8.0+
- Maven 4.0.0+

### Cài Đặt

1. **Clone hoặc tải dự án**
   ```bash
   cd Language-Center-Management-System
   ```

2. **Tạo cơ sở dữ liệu**
   - Mở MySQL Workbench hoặc terminal MySQL
   - Chạy script: `sql/schema_and_data_script.sql`

3. **Cấu hình kết nối cơ sở dữ liệu**
   - Sửa `src/main/resources/META-INF/persistence.xml`
   - Cập nhật URL, username, password MySQL

4. **Build dự án**
   ```bash
   mvn clean install
   ```

5. **Chạy ứng dụng**
   ```bash
   mvn exec:java -Dexec.mainClass="com.Main"
   ```

### Cấu Hình Logging
- File cấu hình: `src/main/resources/logback.xml`
- Logs được ghi vào thư mục: `logs/`
- Định dạng: `[NGÀY GIỜ] [MỨC] [LỚP]`

---

## 9. Các Tính Năng Chính

### 1. **Quản Lý Học Viên**
- Thêm, sửa, xóa mềm thông tin học viên
- Xem lịch sử học tập (kết quả học, điểm danh)
- Xem hoá đơn và thanh toán

### 2. **Quản Lý Giáo Viên**
- Quản lý thông tin giáo viên
- Gán lớp học
- Điểm danh và cập nhật kết quả

### 3. **Quản Lý Khóa Học & Lớp Học**
- Tạo khóa học với nhiều cấp độ
- Tạo lớp học từ khóa học
- Quản lý lịch biểu lớp

### 4. **Quản Lý Điểm Danh**
- Ghi nhân điểm danh học viên
- Báo cáo học viên vắng mặt

### 5. **Quản Lý Kết Quả**
- Nhập điểm học viên
- Báo cáo kết quả học tập

### 6. **Quản Lý Tài Chính**
- Tạo hoá đơn thanh toán
- Ghi nhận thanh toán
- Báo cáo doanh thu

### 7. **Quản Lý Tài Khoản & Nhân Viên**
- Tạo tài khoản người dùng
- Quản lý vai trò phân quyền
- Quản lý thông tin nhân viên

---

## 8. Các Tính Năng Kỹ Thuật

### ORM & Hibernate
- Sử dụng Jakarta Persistence API (JPA)
- Hibernate 6.5.2 cho tự động sinh SQL
- Hỗ trợ relationship (OneToMany, ManyToOne, ManyToMany)

### Bảo Mật
- Mã hóa mật khẩu bằng bcrypt
- Session quản lý người dùng hiện tại
- Kiểm tra quyền truy cập trước mỗi tác vụ

### Xử Lý Lỗi
- Custom exceptions cho các tình huống khác nhau
- Global exception handler đảm bảo xử lý lỗi tập trung
- Logging chi tiết cho mỗi thao tác

### Performance
- Stream queries hỗ trợ low code
- DTOs để giảm dữ liệu truyền tải

### 9. Thành viên nhóm
- Trần Triều Dương - 23110200
- Võ Lê Khánh Duy - 23110196

## 10. Tài Liệu Bổ Sung

- **RBAC Details**: Xem [rbac/rbac.md](rbac/rbac.md) để xem chi tiết phân quyền
- **Database**: Xem [sql/schema_and_data_script.sql](sql/schema_and_data_script.sql) cho schema

