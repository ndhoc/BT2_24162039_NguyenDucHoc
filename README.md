# Bài tập 02 – Servlet, JSP/JSTL, JPA (Hibernate), MVC ba tầng

**Sinh viên:** Nguyễn Đức Học – **MSSV:** 24162039.

Ứng dụng giữ nguyên chức năng của Bài tập 01 nhưng tầng truy cập dữ liệu đã được chuyển từ JDBC sang **JPA (Hibernate)**: entity ánh xạ bảng, `EntityManager` cho mọi thao tác, `persistence.xml` cấu hình kết nối SQL Server.

## Chức năng

- Login tại `/login`: xác thực bằng JPA và lưu tài khoản trong Session; phiên hết hạn sau 30 phút không hoạt động.
- Tùy chọn **Ghi nhớ đăng nhập bằng Cookie**: Cookie sống 30 phút và tự phục hồi Session khi quay lại. Cookie chứa token ngẫu nhiên, HttpOnly, SameSite=Lax; bảng `RememberToken` chỉ lưu SHA-256 của token (entity `RememberToken`).
- Register tại `/register`: kiểm tra dữ liệu, xác nhận mật khẩu và trùng username/email/phone; thành viên mới luôn có roleid=5. Mật khẩu lưu bằng PBKDF2-HMAC-SHA256 có salt.
- Điều hướng `/waiting`: roleid 1 → `/admin/home`, 2 → `/manager/home`, 5 → `/home`.
- Logout: hủy Session, thu hồi token trong bảng `RememberToken`, xóa Cookie ghi nhớ và `JSESSIONID`.
- **CRUD Category bằng JPA**: danh sách, tìm kiếm, thêm/sửa/xóa, tải và hiển thị ảnh PNG/JPEG; sửa không chọn ảnh sẽ giữ ảnh cũ; thay ảnh/xóa danh mục sẽ dọn ảnh cũ. Xóa có trang xác nhận và POST.
- Filter xác thực/phân quyền, CSRF cho POST, UTF-8, JSP xuất dữ liệu bằng `c:out`.

## Công nghệ

- JDK **21**, Maven **3.9+**, Tomcat **11**, SQL Server **2019+** bật TCP/IP cổng 1433.
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0.
- Hibernate ORM **7.4.6.Final** (JPA), Hibernate Validator 8.0.5, Jakarta Validation API 3.1.1, SiteMesh 3.2.3, SQL Server JDBC driver.

## Cấu hình JPA

File `src/main/resources/META-INF/persistence.xml`:

- Persistence unit `jpa-hibernate-mysql` dùng provider `org.hibernate.jpa.HibernatePersistenceProvider`.
- Khai báo 3 entity: `lt_web.model.User`, `lt_web.model.Category`, `lt_web.model.RememberToken`.
- Kết nối SQL Server: `databaseName=MVC_24162039`, user `sa`, mật khẩu `123456` (sửa theo máy của bạn).
- `hibernate.hbm2ddl.auto=update`: Hibernate tự tạo/cập nhật bảng khi khởi động lần đầu.
- `hibernate.show_sql`/`hibernate.format_sql`: in câu lệnh SQL ra log Tomcat để quan sát.

`JpaUtil` tạo một `EntityManagerFactory` dùng chung, mở `EntityManager` cho mỗi thao tác và quản lý transaction (`read`/`write`). `JpaLifecycleListener` đóng factory khi ứng dụng dừng.

## Tạo cơ sở dữ liệu

Có hai cách, chọn một:

1. **Để Hibernate tự tạo bảng**: chỉ cần database `MVC_24162039` tồn tại; `hbm2ddl.auto=update` sẽ tạo bảng từ entity.
2. **Chạy script** `database/schema.sql` rồi `database/seed.sql` trong SSMS, DBeaver hoặc sqlcmd để có sẵn dữ liệu mẫu.

```powershell
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/schema.sql
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/seed.sql
```

Nếu database chưa có, tạo trước bằng `CREATE DATABASE MVC_24162039;`. `trustServerCertificate=true` dùng cho SQL Server local với chứng chỉ tự ký.

## Build và chạy

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
mvn clean package
```

Copy `target/BT2_24162039_NguyenDucHoc.war` vào `TOMCAT_HOME/webapps`, rồi khởi động Tomcat. Với cổng mặc định 8080, mở **http://localhost:8080/BT2_24162039_NguyenDucHoc/**.

Ảnh mặc định lưu tại `CATALINA_BASE/uploads/category`, ngoài WAR; có thể đổi bằng **UPLOAD_DIR** hoặc JVM property **app.upload.dir**.

### Import vào Spring Tools/Eclipse

1. File → Import → Maven → Existing Maven Projects → chọn thư mục chứa `pom.xml`.
2. Chọn JDK 21, thêm Apache Tomcat 11 trong Servers.
3. Maven → Update Project; Run As → Run on Server.
4. Mở `/BT2_24162039_NguyenDucHoc/`; cấu hình SQL Server như trên.

## Tài khoản mẫu

| Tài khoản | Mật khẩu | Vai trò |
| --- | --- | --- |
| admin | 123456 | Quản trị viên, được CRUD Category |
| manager | 123456 | Quản lý |
| user | 123456 | Thành viên |

(Dữ liệu mẫu nằm trong `database/seed.sql`.)

## Cấu trúc MVC ba tầng

```text
src/main/java/lt_web/
  controllers/       Servlet nhận request và chọn JSP
  filters/           UTF-8, xác thực, phân quyền và CSRF
  model/             Entity JPA: User, Category, RememberToken
  service/           Interface nghiệp vụ
  service/impl/      Validation và xử lý nghiệp vụ
  dao/               Interface truy cập dữ liệu
  dao/impl/          DAO dùng EntityManager (JPQL)
  utils/             JpaUtil, Cookie, hash mật khẩu, lưu ảnh
src/main/resources/META-INF/persistence.xml
src/main/webapp/views/     JSP/JSTL
database/                  Schema và dữ liệu mẫu
```

## Kiểm tra thủ công

1. Dùng `admin / 123456` để đăng nhập; thử cả khi bỏ chọn và chọn **Ghi nhớ đăng nhập bằng Cookie**.
2. Đăng ký tài khoản mới, kiểm tra báo lỗi trùng thông tin và mật khẩu xác nhận.
3. Đăng nhập bằng tài khoản mới; thành viên không có quyền truy cập trang quản trị.
4. Dùng admin để thêm, tìm kiếm, sửa và xóa danh mục, thử tải/thay ảnh.
5. Đăng xuất để kết thúc Session và xóa Cookie ghi nhớ.
