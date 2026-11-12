USE MVC_24162039;
GO
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'admin')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'admin',N'Quản trị viên',N'admin@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',1,'0900000001');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'manager')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'manager',N'Quản lý',N'manager@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',2,'0900000002');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'user')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'user',N'Thành viên',N'user@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',5,'0900000003');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Sách')
INSERT INTO dbo.Category(cate_name) VALUES(N'Sách');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Điện thoại')
INSERT INTO dbo.Category(cate_name) VALUES(N'Điện thoại');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Máy tính')
INSERT INTO dbo.Category(cate_name) VALUES(N'Máy tính');
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Dell XPS 13')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Dell XPS 13', 32990000, N'images/products/dell-xps.png', N'Laptop 13 inch, Intel Core Ultra 7, RAM 16GB, SSD 512GB.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'ASUS ROG Strix G16')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'ASUS ROG Strix G16', 28490000, N'images/products/asus-rog.png', N'Laptop gaming RTX 4060, màn hình 165Hz, tản nhiệt tốt.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'MacBook Air M3')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'MacBook Air M3', 27990000, N'images/products/macbook-air.png', N'Chip Apple M3, màn hình Liquid Retina 13.6 inch, pin 18 giờ.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'iPhone 15 Pro')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'iPhone 15 Pro', 28990000, N'images/products/iphone-15.png', N'Khung titan, chip A17 Pro, camera 48MP, USB-C.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Samsung Galaxy S24')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Samsung Galaxy S24', 22990000, N'images/products/galaxy-s24.png', N'Màn hình AMOLED 120Hz, AI Galaxy, pin 4000mAh.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Google Pixel 8')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Google Pixel 8', 18990000, N'images/products/pixel-8.png', N'Camera AI hàng đầu, chip Tensor G3, cập nhật 7 năm.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Clean Code')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Clean Code', 450000, N'images/products/clean-code.png', N'Sách kinh điển về viết mã sạch của Robert C. Martin.', cate_id FROM dbo.Category WHERE cate_name=N'Sách';
GO
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Java Căn Bản')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Java Căn Bản', 320000, N'images/products/java-co-ban.png', N'Giáo trình Java từ cơ bản đến hướng đối tượng.', cate_id FROM dbo.Category WHERE cate_name=N'Sách';
GO
