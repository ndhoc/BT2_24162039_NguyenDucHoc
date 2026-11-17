# Bài tập 02 – Phần 4: Video, Multipart, Servlet/JSP/JPA

**Sinh viên:** Nguyễn Đức Học – **MSSV:** 24162039.

Ứng dụng cửa hàng công nghệ dùng **JPA (Hibernate)** và SQL Server, có quản lý sản phẩm, tài khoản, OTP và video. Bảng `[User]` liên kết **1–n** với `Videos`; file video được upload bằng Jakarta Servlet Multipart và lưu ngoài WAR.

## Chức năng

- **Trang chủ `/home`** (hoặc `/`, `/index`): giới thiệu cửa hàng, danh mục sản phẩm, 6 sản phẩm nổi bật và **10 video mới nhất**.
- **Danh mục sản phẩm `/products`**: lọc theo danh mục, tìm kiếm theo tên, hiển thị 8 sản phẩm mẫu kèm ảnh và giá.
- **Danh sách video `/video`**: hiển thị tất cả video cho người dùng, phân trang **6 video/trang** và ghi rõ tài khoản đăng.
- **Chi tiết video `/video/detail?id=...`**: hiển thị thông tin và phát video khi bấm tiêu đề từ `/home` hoặc `/video`.
- **Upload Multipart `/video/upload`**: nhận MP4, WebM hoặc OGG hợp lệ, tối đa 50 MB; video tự động gắn với tài khoản đăng nhập.
- **CRUD video `/admin/video/*`**: admin có thể xem danh sách, thêm, sửa, thay file và xóa video; chọn tài khoản chủ sở hữu.
- **Dashboard `/dashboard`**: dành cho admin và manager, thống kê số sản phẩm, danh mục, người dùng, tổng giá trị và sản phẩm mới nhất.
- **Đăng nhập `/login`** và **đăng ký `/register`**:
  - Admin (roleid 1) và manager (roleid 2) sau khi đăng nhập vào thẳng `/dashboard`.
  - Thành viên (roleid 5) sau khi đăng nhập vào thẳng `/products`.
  - Login có tùy chọn **Ghi nhớ đăng nhập bằng Cookie**: token ngẫu nhiên, HttpOnly, SameSite=Lax, sống 30 phút; bảng `RememberToken` chỉ lưu SHA-256 của token.
  - Register kiểm tra dữ liệu, xác nhận mật khẩu và trùng username/email/phone; mật khẩu lưu bằng PBKDF2-HMAC-SHA256 có salt.
- **Kích hoạt tài khoản bằng OTP `/activate`**: sau khi đăng ký, tài khoản ở trạng thái `is_active = 0`; mã OTP 6 chữ số được gửi tới email, có hiệu lực 10 phút, dùng một lần, tối đa 5 lần nhập sai. Chỉ bản băm SHA-256 của mã lưu trong bảng `OtpToken`. Tài khoản chưa kích hoạt không đăng nhập được.
- **Quên mật khẩu `/forgot-password` → `/reset-password`**: nhập email đã đăng ký để nhận mã xác nhận OTP, sau đó nhập mã kèm mật khẩu mới (PBKDF2). Có nút gửi lại mã.
- **Đăng xuất `/logout`**: hủy Session, thu hồi token trong `RememberToken`, xóa Cookie ghi nhớ và `JSESSIONID`.
- Filter xác thực/phân quyền, CSRF cho POST, UTF-8, JSP xuất dữ liệu bằng `c:out`.

## Công nghệ

- JDK **21**, Maven **3.9+**, Tomcat **11**, SQL Server **2019+** bật TCP/IP cổng 1433.
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0.
- Hibernate ORM **7.4.6.Final** (JPA), Hibernate Validator 8.0.5, Jakarta Validation API 3.1.1, SiteMesh 3.2.3, SQL Server JDBC driver.
- Jakarta Mail 2.1.3 + Angus Mail 2.0.3 để gửi OTP qua SMTP.

## Cấu hình gửi OTP qua email

`src/main/resources/mail.properties` chứa thông tin SMTP thật và **đã nằm trong `.gitignore`** nên không bị commit. Sao chép file mẫu rồi điền thông tin:

```powershell
Copy-Item src/main/resources/mail.properties.example src/main/resources/mail.properties
```

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.smtp.username=your-account@gmail.com
mail.smtp.password=your-app-password
mail.smtp.from=your-account@gmail.com
mail.smtp.starttls=true
mail.smtp.auth=true
```

Có thể ghi đè bằng biến môi trường `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_STARTTLS`, `MAIL_AUTH`. Chưa cấu hình `mail.smtp.host` thì ứng dụng chạy **chế độ DEV**: mã OTP ghi vào `<catalina.base>/logs/otp-dev.log`.

## Cấu hình JPA

File `src/main/resources/META-INF/persistence.xml`:

- Persistence unit `jpa-hibernate-mysql` dùng provider `org.hibernate.jpa.HibernatePersistenceProvider`.
- Khai báo 6 entity: `lt_web.entity.User`, `lt_web.entity.Category`, `lt_web.entity.Product`, `lt_web.entity.RememberToken`, `lt_web.entity.OtpToken`, `lt_web.entity.Video`.
- Kết nối SQL Server: `databaseName=MVC_24162039`, user `sa`, mật khẩu `123456` (sửa theo máy của bạn).
- `hibernate.hbm2ddl.auto=none`: Hibernate không tự sửa schema; chạy `database/schema.sql` để quản lý cột, index và khóa ngoại một cách nhất quán.
- `hibernate.show_sql`/`hibernate.format_sql`: in câu lệnh SQL ra log Tomcat để quan sát.

`JpaConfig` tạo một `EntityManagerFactory` dùng chung, mở `EntityManager` cho mỗi thao tác và quản lý transaction (`read`/`write`). `JpaLifecycleListener` đóng factory khi ứng dụng dừng.

## Cơ sở dữ liệu và file upload

Database `MVC_24162039` gồm 6 bảng: `[User]`, `Category`, `Product`, `RememberToken`, `OtpToken`, `Videos`. Khóa ngoại `Videos.user_id → [User].id` tạo quan hệ một user có nhiều video và dùng `ON DELETE CASCADE`.

Chạy `database/schema.sql` rồi `database/seed.sql` để tạo bảng và nạp dữ liệu mẫu (3 danh mục, 8 sản phẩm, 3 tài khoản):

```powershell
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/schema.sql
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/seed.sql
```

Nếu database chưa có, script sẽ tạo `MVC_24162039`. Script có kiểm tra tồn tại nên có thể chạy lại; với database cũ, nó bổ sung `[User].is_active`, `OtpToken` và `Videos` mà không xóa dữ liệu hiện có.

Ảnh sản phẩm mẫu nằm trong `src/main/webapp/images/products/` (`dell-xps.png`, `iphone-15.png`, `clean-code.png`, …) và được phục vụ trực tiếp từ WAR. Ảnh đại diện danh mục do admin tải lên lưu tại `CATALINA_BASE/uploads/category` (đổi bằng **UPLOAD_DIR** hoặc JVM property **app.upload.dir**).

Video upload được lưu tại `CATALINA_BASE/uploads/video`. Có thể đổi nơi lưu bằng biến môi trường `UPLOAD_VIDEO_DIR` hoặc JVM property `app.upload.video.dir`. Tên file trên máy chủ là UUID, nội dung được kiểm tra bằng chữ ký tệp thay vì tin vào phần mở rộng do trình duyệt gửi lên.

Repository kèm 10 MP4 quay thật minh họa laptop, điện thoại và sách trong `src/main/webapp/videos/`. Chạy `database/seed.sql` sẽ thêm các bản ghi video demo cho `admin` và `manager`; trình phát ưu tiên file upload ngoài WAR và tự dùng file demo đóng gói khi máy mới chưa có thư mục upload.

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
  controllers/       Servlet: Index, Product, Dashboard, Login, Register, Activate,
                     Video, VideoDetail, VideoUpload, VideoFile, AdminVideo, ...
  filters/           UTF-8, xác thực, phân quyền, CSRF
  entity/            Entity JPA: User, Category, Product, RememberToken, OtpToken, Video
  service/           Interface nghiệp vụ (UserService, VideoService, ...)
  service/impl/      Validation và xử lý nghiệp vụ, phát hành/xác thực OTP
  repository/        Interface repository + JpaConfig/DataAccessException
  repository/impl/   Repository dùng EntityManager (JPQL)
  utils/             Cookie, hash mật khẩu, OTP, gửi mail, lưu ảnh/video
src/main/resources/
  META-INF/persistence.xml   Cấu hình JPA
  mail.properties.example    Mẫu cấu hình SMTP (mail.properties thật bị gitignore)
src/main/webapp/views/          index.jsp, products.jsp, video-list/detail/upload.jsp,
                                login/register/activate, dashboard.jsp, admin/
src/main/webapp/js/              otp-input.js: ô nhập OTP 6 chữ số
src/main/webapp/images/products/ Ảnh sản phẩm mẫu
database/                       Schema và dữ liệu mẫu
```

## Kiểm tra thủ công

1. Chạy `database/schema.sql`, mở `/home` và kiểm tra khu vực **10 video mới nhất**.
2. Đăng nhập `user / 123456`, mở `/video/upload`, upload MP4/WebM/OGG rồi kiểm tra video xuất hiện ở `/video`.
3. Upload ít nhất 7 video cho cùng một tài khoản và kiểm tra `/video` hiển thị 6 video ở trang 1, phần còn lại ở trang 2.
4. Bấm tiêu đề video trên `/home` và `/video`; trang chi tiết phải phát được video và hỗ trợ tua.
5. Đăng nhập `admin / 123456` → `/admin/video/list`; thử đầy đủ thêm, xem, sửa/thay file và xóa video.
6. Từ dashboard mở **Quản lý danh mục** để kiểm tra CRUD danh mục cũ vẫn hoạt động.
7. Đăng nhập `manager / 123456` → vào `/dashboard` nhưng không truy cập được `/admin/video/list`.
8. Đăng ký tài khoản mới → kích hoạt OTP → upload video và xác nhận video thuộc đúng tài khoản đó.
9. Đăng xuất để kết thúc Session; `/video` vẫn xem được công khai, còn `/video/upload` phải chuyển về trang đăng nhập.
