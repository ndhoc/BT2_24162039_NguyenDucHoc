# Bài tập 02 – Servlet, JSP/JSTL, JPA (Hibernate), MVC ba tầng

**Sinh viên:** Nguyễn Đức Học – **MSSV:** 24162039.

Ứng dụng cửa hàng công nghệ: giữ nguyên chức năng của Bài tập 01 nhưng tầng truy cập dữ liệu dùng **JPA (Hibernate)**, bổ sung trang chủ, trang danh mục sản phẩm cho người dùng và dashboard cho quản trị.

## Chức năng

- **Trang chủ `/`**: giới thiệu cửa hàng, danh mục sản phẩm và 6 sản phẩm nổi bật.
- **Danh mục sản phẩm `/products`**: lọc theo danh mục, tìm kiếm theo tên, hiển thị 8 sản phẩm mẫu kèm ảnh và giá.
- **Dashboard `/dashboard`**: dành cho admin và manager, thống kê số sản phẩm, danh mục, người dùng, tổng giá trị và sản phẩm mới nhất.
- **Đăng nhập `/login`** và **đăng ký `/register`**:
  - Admin (roleid 1) và manager (roleid 2) sau khi đăng nhập vào thẳng `/dashboard`.
  - Thành viên (roleid 5) sau khi đăng nhập vào thẳng `/products`.
  - Login có tùy chọn **Ghi nhớ đăng nhập bằng Cookie**: token ngẫu nhiên, HttpOnly, SameSite=Lax, sống 30 phút; bảng `RememberToken` chỉ lưu SHA-256 của token.
  - Register kiểm tra dữ liệu, xác nhận mật khẩu và trùng username/email/phone; mật khẩu lưu bằng PBKDF2-HMAC-SHA256 có salt.
- **Quản lý danh mục** (chỉ admin): danh sách, tìm kiếm, thêm/sửa/xóa, tải và hiển thị ảnh PNG/JPEG.
- **Logout**: hủy Session, thu hồi token trong `RememberToken`, xóa Cookie ghi nhớ và `JSESSIONID`.
- Filter xác thực/phân quyền, CSRF cho POST, UTF-8, JSP xuất dữ liệu bằng `c:out`.

## Công nghệ

- JDK **21**, Maven **3.9+**, Tomcat **11**, SQL Server **2019+** bật TCP/IP cổng 1433.
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0.
- Hibernate ORM **7.4.6.Final** (JPA), Hibernate Validator 8.0.5, Jakarta Validation API 3.1.1, SiteMesh 3.2.3, SQL Server JDBC driver.

## Cấu hình JPA

File `src/main/resources/META-INF/persistence.xml`:

- Persistence unit `jpa-hibernate-mysql` dùng provider `org.hibernate.jpa.HibernatePersistenceProvider`.
- Khai báo 4 entity: `lt_web.model.User`, `lt_web.model.Category`, `lt_web.model.Product`, `lt_web.model.RememberToken`.
- Kết nối SQL Server: `databaseName=MVC_24162039`, user `sa`, mật khẩu `123456` (sửa theo máy của bạn).
- `hibernate.hbm2ddl.auto=update`: Hibernate tự tạo/cập nhật bảng khi khởi động lần đầu.
- `hibernate.show_sql`/`hibernate.format_sql`: in câu lệnh SQL ra log Tomcat để quan sát.

`JpaUtil` tạo một `EntityManagerFactory` dùng chung, mở `EntityManager` cho mỗi thao tác và quản lý transaction (`read`/`write`). `JpaLifecycleListener` đóng factory khi ứng dụng dừng.

## Cơ sở dữ liệu và ảnh sản phẩm

Database `MVC_24162039` gồm 4 bảng: `[User]`, `Category`, `Product`, `RememberToken`.

Chạy `database/schema.sql` rồi `database/seed.sql` để tạo bảng và nạp dữ liệu mẫu (3 danh mục, 8 sản phẩm, 3 tài khoản):

```powershell
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/schema.sql
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/seed.sql
```

Nếu database chưa có, tạo trước bằng `CREATE DATABASE MVC_24162039;`. Script có kiểm tra tồn tại nên chạy lại không nhân đôi dữ liệu.

Ảnh sản phẩm mẫu nằm trong `src/main/webapp/images/products/` (`dell-xps.png`, `iphone-15.png`, `clean-code.png`, …) và được phục vụ trực tiếp từ WAR. Ảnh đại diện danh mục do admin tải lên lưu tại `CATALINA_BASE/uploads/category` (đổi bằng **UPLOAD_DIR** hoặc JVM property **app.upload.dir**).

## Build và chạy

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
mvn clean package
```

Copy `target/BT2_24162039_NguyenDucHoc.war` vào `TOMCAT_HOME/webapps`, rồi khởi động Tomcat. Với cổng mặc định 8080, mở **http://localhost:8080/BT2_24162039_NguyenDucHoc/**.

### Import vào Spring Tools/Eclipse

1. File → Import → Maven → Existing Maven Projects → chọn thư mục chứa `pom.xml`.
2. Chọn JDK 21, thêm Apache Tomcat 11 trong Servers.
3. Maven → Update Project; Run As → Run on Server.
4. Mở `/BT2_24162039_NguyenDucHoc/`; cấu hình SQL Server như trên.

## Tài khoản mẫu

| Tài khoản | Mật khẩu | Vai trò | Sau đăng nhập |
| --- | --- | --- | --- |
| admin | 123456 | Quản trị viên | `/dashboard` + quản lý danh mục |
| manager | 123456 | Quản lý | `/dashboard` |
| user | 123456 | Thành viên | `/products` |

## Cấu trúc MVC ba tầng

```text
src/main/java/lt_web/
  controllers/       Servlet: Index, Product, Dashboard, Login, Register, Category, ...
  filters/           UTF-8, xác thực, phân quyền, CSRF
  model/             Entity JPA: User, Category, Product, RememberToken
  service/           Interface nghiệp vụ
  service/impl/      Validation và xử lý nghiệp vụ
  dao/               Interface truy cập dữ liệu
  dao/impl/          DAO dùng EntityManager (JPQL)
  utils/             JpaUtil, Cookie, hash mật khẩu, lưu ảnh
src/main/resources/META-INF/persistence.xml
src/main/webapp/views/          index.jsp, products.jsp, dashboard.jsp, login/register, admin/
src/main/webapp/images/products/ Ảnh sản phẩm mẫu
database/                       Schema và dữ liệu mẫu
```

## Kiểm tra thủ công

1. Mở `/` xem trang chủ và sản phẩm nổi bật; mở `/products` lọc theo danh mục và tìm kiếm.
2. Đăng nhập `admin / 123456` → vào `/dashboard`; từ dashboard mở **Quản lý danh mục** để CRUD danh mục.
3. Đăng nhập `manager / 123456` → vào `/dashboard` nhưng không thấy nút quản lý danh mục.
4. Đăng nhập `user / 123456` → vào `/products`; truy cập `/dashboard` bị chặn 403.
5. Đăng ký tài khoản mới, kiểm tra báo lỗi trùng thông tin và mật khẩu xác nhận.
6. Đăng xuất để kết thúc Session và xóa Cookie ghi nhớ.
