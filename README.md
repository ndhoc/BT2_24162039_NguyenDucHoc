# Bài tập lập trình Web

Sinh viên: Nguyễn Đức Học – MSSV: 24162039.

Ứng dụng cửa hàng công nghệ dùng JPA (Hibernate) và SQL Server, có quản lý sản phẩm, tài khoản, OTP và video. Bảng [User] liên kết 1–n với Videos; file video được upload bằng Jakarta Servlet Multipart và lưu ngoài WAR.
- Trang chủ /home (hoặc /, /index): giới thiệu cửa hàng, danh mục sản phẩm, 6 sản phẩm nổi bật và 10 video mới nhất.
- Danh mục sản phẩm /products: lọc theo danh mục, tìm kiếm theo tên, hiển thị 8 sản phẩm mẫu kèm ảnh và giá.
- Danh sách video /video: hiển thị tất cả video cho người dùng, phân trang 6 video/trang và ghi rõ tài khoản đăng.
- Chi tiết video /video/detail?id=...: hiển thị thông tin và phát video khi bấm tiêu đề từ /home hoặc /video.
- Upload Multipart /video/upload: nhận MP4, WebM hoặc OGG hợp lệ, tối đa 50 MB; video tự động gắn với tài khoản đăng nhập.
- CRUD video /admin/video/*: admin có thể xem danh sách, thêm, sửa, thay file và xóa video; chọn tài khoản chủ sở hữu.
- Dashboard /dashboard: dành cho admin và manager, thống kê số sản phẩm, danh mục, người dùng, tổng giá trị và sản phẩm mới nhất.
- Đăng nhập /login và đăng ký /register:
  - Admin (roleid 1) và manager (roleid 2) sau khi đăng nhập vào thẳng /dashboard.
  - Thành viên (roleid 5) sau khi đăng nhập vào thẳng /products.
  - Login có tùy chọn Ghi nhớ đăng nhập bằng Cookie: token ngẫu nhiên, HttpOnly, SameSite=Lax, sống 30 phút; bảng RememberToken chỉ lưu SHA-256 của token.
  - Register kiểm tra dữ liệu, xác nhận mật khẩu và trùng username/email/phone; mật khẩu lưu bằng PBKDF2-HMAC-SHA256 có salt.
- Kích hoạt tài khoản bằng OTP /activate: sau khi đăng ký, tài khoản ở trạng thái is_active = 0; mã OTP 6 chữ số được gửi tới email, có hiệu lực 10 phút, dùng một lần, tối đa 5 lần nhập sai. Chỉ bản băm SHA-256 của mã lưu trong bảng OtpToken. Tài khoản chưa kích hoạt không đăng nhập được.
- Quên mật khẩu /forgot-password → /reset-password: nhập email đã đăng ký để nhận mã xác nhận OTP, sau đó nhập mã kèm mật khẩu mới (PBKDF2). Có nút gửi lại mã.
- Đăng xuất /logout: hủy Session, thu hồi token trong RememberToken, xóa Cookie ghi nhớ và JSESSIONID.
- Filter xác thực/phân quyền, CSRF cho POST, UTF-8, JSP xuất dữ liệu bằng c:out.
- JDK 21, Maven 3.9+, Tomcat 11, SQL Server 2019+ bật TCP/IP cổng 1433.
- Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0.
- Hibernate ORM 7.4.6.Final (JPA), Hibernate Validator 8.0.5, Jakarta Validation API 3.1.1, SiteMesh 3.2.3, SQL Server JDBC driver.
- Jakarta Mail 2.1.3 + Angus Mail 2.0.3 để gửi OTP qua SMTP.
src/main/resources/mail.properties chứa thông tin SMTP thật và đã nằm trong .gitignore nên không bị commit. Sao chép file mẫu rồi điền thông tin:
Copy-Item src/main/resources/mail.properties.example src/main/resources/mail.properties
mail.smtp.host=smtp.gmail.com mail.smtp.port=587 mail.smtp.username=your-account@gmail.com mail.smtp.password=your-app-password mail.smtp.from=your-account@gmail.com mail.smtp.starttls=true mail.smtp.auth=true
Có thể ghi đè bằng biến môi trường MAIL_HOST, MAIL_PORT, MAIL_USERNAME, MAIL_PASSWORD, MAIL_FROM, MAIL_STARTTLS, MAIL_AUTH. Chưa cấu hình mail.smtp.host thì ứng dụng chạy chế độ DEV: mã OTP ghi vào <catalina.base>/logs/otp-dev.log.
File src/main/resources/META-INF/persistence.xml:
- Persistence unit jpa-hibernate-mysql dùng provider org.hibernate.jpa.HibernatePersistenceProvider.
- Khai báo 6 entity: lt_web.entity.User, lt_web.entity.Category, lt_web.entity.Product, lt_web.entity.RememberToken, lt_web.entity.OtpToken, lt_web.entity.Video.
- Kết nối SQL Server: databaseName=MVC_24162039, user sa, mật khẩu 123456 (sửa theo máy của bạn).
- hibernate.hbm2ddl.auto=none: Hibernate không tự sửa schema; chạy database/schema.sql để quản lý cột, index và khóa ngoại một cách nhất quán.
- hibernate.show_sql/hibernate.format_sql: in câu lệnh SQL ra log Tomcat để quan sát.
JpaConfig tạo một EntityManagerFactory dùng chung, mở EntityManager cho mỗi thao tác và quản lý transaction (read/write). JpaLifecycleListener đóng factory khi ứng dụng dừng.
Database MVC_24162039 gồm 6 bảng: [User], Category, Product, RememberToken, OtpToken, Videos. Khóa ngoại Videos.user_id → [User].id tạo quan hệ một user có nhiều video và dùng ON DELETE CASCADE.

Chạy database/schema.sql rồi database/seed.sql để tạo bảng và nạp dữ liệu mẫu (3 danh mục, 8 sản phẩm, 3 tài khoản):

```powershell
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/schema.sql
sqlcmd -S tcp:localhost,1433 -U sa -C -b -f 65001 -i database/seed.sql
```

Nếu database chưa có, script sẽ tạo MVC_24162039. Script có kiểm tra tồn tại nên có thể chạy lại; với database cũ, nó bổ sung [User].is_active, OtpToken và Videos mà không xóa dữ liệu hiện có.

Ảnh sản phẩm mẫu nằm trong src/main/webapp/images/products/ (dell-xps.png, iphone-15.png, clean-code.png, …) và được phục vụ trực tiếp từ WAR. Ảnh đại diện danh mục do admin tải lên lưu tại CATALINA_BASE/uploads/category (đổi bằng UPLOAD_DIR hoặc JVM property app.upload.dir).

Video upload được lưu tại CATALINA_BASE/uploads/video. Có thể đổi nơi lưu bằng biến môi trường UPLOAD_VIDEO_DIR hoặc JVM property app.upload.video.dir. Tên file trên máy chủ là UUID, nội dung được kiểm tra bằng chữ ký tệp thay vì tin vào phần mở rộng do trình duyệt gửi lên.

Repository kèm 10 MP4 quay thật minh họa laptop, điện thoại và sách trong src/main/webapp/videos/. Chạy database/seed.sql sẽ thêm các bản ghi video demo cho admin và manager; trình phát ưu tiên file upload ngoài WAR và tự dùng file demo đóng gói khi máy mới chưa có thư mục upload.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.12.1'
mvn clean package
```

Copy target/BT2_24162039_NguyenDucHoc.war vào TOMCAT_HOME/webapps, rồi khởi động Tomcat. Với cổng mặc định 8080, mở [http://localhost:8080/BT2_24162039_NguyenDucHoc/](http://localhost:8080/BT2_24162039_NguyenDucHoc/).
1. File → Import → Maven → Existing Maven Projects → chọn thư mục chứa pom.xml.
2. Chọn JDK 21, thêm Apache Tomcat 11 trong Servers.
3. Maven → Update Project; Run As → Run on Server.
4. Mở /BT2_24162039_NguyenDucHoc/; cấu hình SQL Server như trên.
```
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

## SiteMesh 3, Bootstrap và validation biểu mẫu

### SiteMesh 3 và Bootstrap

- Nâng SiteMesh từ **3.2.3** lên **3.3.0** bằng dependency `org.sitemesh:sitemesh-webfilter`. Bộ lọc được đăng ký trong `WEB-INF/web.xml` và các đường dẫn được cấu hình trong `WEB-INF/sitemesh3.xml`.
- SiteMesh dùng `web.jsp` cho trang công khai và `admin.jsp` cho `/dashboard` và `/admin/*`. Hai decorator dùng chung `layout.jsp` với **Bootstrap 5.3.9** đóng gói trong ứng dụng. API và tài nguyên tĩnh nằm ngoài phạm vi áp dụng layout.
- `WEB-INF/decorators/` chứa các layout. `common/web/` chứa `header.jsp` và `footer.jsp`, `common/admin/` chứa `left.jsp`, còn `views/` chứa nội dung từng trang. Các tệp JSP và phản hồi HTTP dùng UTF-8 để hiển thị đúng tiếng Việt.

### Validation biểu mẫu

Biểu mẫu được kiểm tra ở trình duyệt và trên máy chủ. Lỗi của từng trường hiển thị ngay cạnh trường nhập.

| Biểu mẫu | Nội dung kiểm tra |
| --- | --- |
| Đăng nhập | Kiểm tra tên đăng nhập và mật khẩu bắt buộc, thông tin đăng nhập và trạng thái kích hoạt. |
| Đăng ký | Kiểm tra họ tên, tên đăng nhập, email, số điện thoại, độ dài mật khẩu và xác nhận mật khẩu. Máy chủ kiểm tra trùng tên đăng nhập, email và số điện thoại. |
| Quên và đặt lại mật khẩu | Kiểm tra định dạng email, tài khoản đã đăng ký, mã OTP gồm 6 chữ số và xác nhận mật khẩu mới. |
| Thêm và sửa danh mục | Kiểm tra tên danh mục và ảnh PNG/JPEG tối đa 5 MB, kích thước tối đa 4096 × 4096 pixel. |
| Thêm và sửa video | Kiểm tra tiêu đề, độ dài mô tả, chủ sở hữu và tệp MP4, WebM hoặc OGG tối đa 50 MB. Khi thêm video, tệp là bắt buộc. |

Khi sửa danh mục hoặc video, có thể giữ lại tệp hiện tại. Máy chủ kiểm tra nội dung của tệp mới tải lên.

### Quản lý và xem chi tiết sản phẩm

- Quản trị viên mở **Quản lý sản phẩm** từ dashboard để tìm, thêm, sửa hoặc xóa sản phẩm. Biểu mẫu có tên, giá, danh mục, mô tả và ảnh.
- Ảnh PNG hoặc JPEG được kiểm tra trước khi lưu vào `uploads/product/` của Tomcat, với giới hạn 5 MB và 4096 × 4096 pixel. Khi sửa sản phẩm, có thể giữ ảnh hiện tại.
- Thẻ sản phẩm ở trang chủ và trang danh mục mở `/products/detail?id=...`, hiển thị ảnh, tên, giá, danh mục và mô tả.

### Hồ sơ cá nhân

- Sau khi đăng nhập, mở **Hồ sơ** từ tên tài khoản hoặc menu chân trang để cập nhật họ tên, số điện thoại và ảnh đại diện. Ảnh PNG hoặc JPEG tối đa 5 MB được nhận qua Multipart và tải lên Cloudinary. Bỏ trống trường ảnh để giữ ảnh hiện tại.
- Khi đổi email, hệ thống gửi OTP gồm 6 chữ số đến địa chỉ mới. Email tài khoản được cập nhật sau khi xác nhận mã hợp lệ. Mã có hiệu lực trong 10 phút và hết hiệu lực sau 5 lần nhập sai.
- Chạy lại `database/schema.sql` khi nâng cấp cơ sở dữ liệu đã có. Dùng phần cấu hình SMTP ở trên để gửi OTP qua email. Trong chế độ phát triển, mã được ghi vào `logs/otp-dev.log`.

#### Cấu hình Cloudinary

Khi chạy trong Spring Tools/Eclipse, sao chép [`cloudinary.properties.example`](src/main/resources/cloudinary.properties.example) ngay trong thư mục `src/main/resources` và đặt tên bản sao là `cloudinary.properties`. Điền thông tin từ **Settings → API Keys** trên Cloudinary rồi khởi động lại Tomcat.

```properties
cloudinary.url=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
```

File chứa thông tin thật được Git bỏ qua và Maven loại khỏi WAR. Khi triển khai bằng WAR, đặt cùng file này tại `CATALINA_BASE/conf/cloudinary.properties`. Biến môi trường `CLOUDINARY_URL` là cấu hình dự phòng khi chưa điền URL trong file.
