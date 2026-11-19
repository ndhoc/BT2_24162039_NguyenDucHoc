USE MVC_24162039;
GO
-- Validate the input encoding before any data is written.
IF UNICODE(SUBSTRING(N'Sách', 2, 1)) <> 225 OR UNICODE(N'Đ') <> 272
    THROW 50001, N'Read seed.sql as UTF-8. For sqlcmd, use -f 65001.', 1;

SET XACT_ABORT ON;
BEGIN TRANSACTION;

DECLARE @CategoryNames TABLE (
    damaged NVARCHAR(255) COLLATE Latin1_General_100_BIN2 PRIMARY KEY,
    corrected NVARCHAR(255) COLLATE Latin1_General_100_BIN2 NOT NULL
);
INSERT INTO @CategoryNames VALUES
    (CONVERT(NVARCHAR(255), 0x5300C300A10063006800), N'Sách'),
    (CONVERT(NVARCHAR(255), 0xC40090006900E100BB0021206E002000740068006F00E100BA00A1006900), N'Điện thoại'),
    (CONVERT(NVARCHAR(255), 0x4D00C300A100790020007400C300AD006E006800), N'Máy tính');

DECLARE @CategoryRepair TABLE (
    damaged_id INT PRIMARY KEY,
    keep_id INT NOT NULL,
    corrected NVARCHAR(255) NOT NULL
);
INSERT INTO @CategoryRepair
SELECT damaged.cate_id,
       COALESCE(corrected.cate_id, MIN(damaged.cate_id) OVER (PARTITION BY names.damaged)),
       names.corrected
FROM dbo.Category AS damaged
JOIN @CategoryNames AS names ON damaged.cate_name COLLATE Latin1_General_100_BIN2 = names.damaged
OUTER APPLY (
    SELECT TOP (1) existing.cate_id
    FROM dbo.Category AS existing
    WHERE existing.cate_name COLLATE Latin1_General_100_BIN2 = names.corrected
    ORDER BY existing.cate_id
) AS corrected
WHERE damaged.icons IS NULL;

UPDATE product
SET cate_id = repair.keep_id
FROM dbo.Product AS product
JOIN @CategoryRepair AS repair ON product.cate_id = repair.damaged_id
WHERE repair.damaged_id <> repair.keep_id;

UPDATE category
SET cate_name = repair.corrected
FROM dbo.Category AS category
JOIN @CategoryRepair AS repair ON category.cate_id = repair.damaged_id
WHERE repair.damaged_id = repair.keep_id;

DELETE category
FROM dbo.Category AS category
JOIN @CategoryRepair AS repair ON category.cate_id = repair.damaged_id
WHERE repair.damaged_id <> repair.keep_id;

DECLARE @CorrectTitle NVARCHAR(255) = N'Java Căn Bản';
DECLARE @DamagedTitle NVARCHAR(255) =
    CONVERT(NVARCHAR(255), 0x4A0061007600610020004300C40092016E0020004200E100BA00A3006E00);
DECLARE @CorrectDescription NVARCHAR(1000) = N'Giáo trình Java từ cơ bản đến hướng đối tượng.';
DECLARE @DamagedDescription NVARCHAR(1000) =
    CONVERT(NVARCHAR(1000), 0x47006900C300A1006F00200074007200C300AC006E00680020004A0061007600610020007400E100BB00AB0020006300C600A10020006200E100BA00A3006E002000C4001820E100BA00BF006E0020006800C600B000E100BB003A206E0067002000C4001820E100BB001820690020007400C600B000E100BB00A3006E0067002E00);
DECLARE @RepairedProducts TABLE (product_id INT PRIMARY KEY);

UPDATE dbo.Product
SET product_name = @CorrectTitle,
    description = CASE WHEN description COLLATE Latin1_General_100_BIN2 = @DamagedDescription
                       THEN @CorrectDescription ELSE description END
OUTPUT inserted.product_id INTO @RepairedProducts
WHERE image = N'images/products/java-co-ban.png'
  AND product_name COLLATE Latin1_General_100_BIN2 = @DamagedTitle;

DELETE repaired
FROM dbo.Product AS repaired
JOIN @RepairedProducts AS changed ON repaired.product_id = changed.product_id
WHERE EXISTS (
    SELECT 1
    FROM dbo.Product AS existing
    LEFT JOIN @RepairedProducts AS other_changed ON existing.product_id = other_changed.product_id
    WHERE existing.product_id <> repaired.product_id
      AND (other_changed.product_id IS NULL OR existing.product_id < repaired.product_id)
      AND existing.product_name COLLATE Latin1_General_100_BIN2 = repaired.product_name
      AND existing.cate_id = repaired.cate_id
      AND existing.price = repaired.price
      AND existing.image COLLATE Latin1_General_100_BIN2 = repaired.image
      AND (existing.description COLLATE Latin1_General_100_BIN2 = repaired.description
           OR (existing.description IS NULL AND repaired.description IS NULL))
);

-- Nạp dữ liệu mẫu.
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'admin')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'admin',N'Quản trị viên',N'admin@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',1,'0900000001');
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'manager')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'manager',N'Quản lý',N'manager@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',2,'0900000002');
IF NOT EXISTS(SELECT 1 FROM dbo.[User] WHERE username=N'user')
INSERT INTO dbo.[User](username,fullname,email,password,roleid,phone) VALUES(N'user',N'Thành viên',N'user@example.com',N'pbkdf2$120000$MjQxNjIwMzktZGVtby12MQ==$oBGo4z+RJxFI/WMLd+fRep+OqJBlMVS4/O5PSejeag8=',5,'0900000003');
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Sách')
INSERT INTO dbo.Category(cate_name) VALUES(N'Sách');
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Điện thoại')
INSERT INTO dbo.Category(cate_name) VALUES(N'Điện thoại');
IF NOT EXISTS(SELECT 1 FROM dbo.Category WHERE cate_name=N'Máy tính')
INSERT INTO dbo.Category(cate_name) VALUES(N'Máy tính');
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Dell XPS 13')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Dell XPS 13', 32990000, N'images/products/dell-xps.png', N'Laptop 13 inch, Intel Core Ultra 7, RAM 16GB, SSD 512GB.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'ASUS ROG Strix G16')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'ASUS ROG Strix G16', 28490000, N'images/products/asus-rog.png', N'Laptop gaming RTX 4060, màn hình 165Hz, tản nhiệt tốt.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'MacBook Air M3')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'MacBook Air M3', 27990000, N'images/products/macbook-air.png', N'Chip Apple M3, màn hình Liquid Retina 13.6 inch, pin 18 giờ.', cate_id FROM dbo.Category WHERE cate_name=N'Máy tính';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'iPhone 15 Pro')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'iPhone 15 Pro', 28990000, N'images/products/iphone-15.png', N'Khung titan, chip A17 Pro, camera 48MP, USB-C.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Samsung Galaxy S24')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Samsung Galaxy S24', 22990000, N'images/products/galaxy-s24.png', N'Màn hình AMOLED 120Hz, AI Galaxy, pin 4000mAh.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Google Pixel 8')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Google Pixel 8', 18990000, N'images/products/pixel-8.png', N'Camera AI hàng đầu, chip Tensor G3, cập nhật 7 năm.', cate_id FROM dbo.Category WHERE cate_name=N'Điện thoại';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Clean Code')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Clean Code', 450000, N'images/products/clean-code.png', N'Sách kinh điển về viết mã sạch của Robert C. Martin.', cate_id FROM dbo.Category WHERE cate_name=N'Sách';
IF NOT EXISTS(SELECT 1 FROM dbo.Product WHERE product_name=N'Java Căn Bản')
INSERT INTO dbo.Product(product_name, price, image, description, cate_id)
SELECT N'Java Căn Bản', 320000, N'images/products/java-co-ban.png', N'Giáo trình Java từ cơ bản đến hướng đối tượng.', cate_id FROM dbo.Category WHERE cate_name=N'Sách';

-- 10 video quay thật minh họa các mặt hàng của shop. File nằm trong
-- src/main/webapp/videos và vẫn phát được khi chưa có thư mục upload ngoài WAR.
;WITH DemoVideos(username, title, description, file_name, sort_order) AS (
    SELECT * FROM (VALUES
        (N'admin', N'Dell XPS 13 - Laptop mỏng nhẹ cho công việc', N'Video quay thật minh họa thiết kế laptop hiện đại, mỏng nhẹ, phù hợp với dòng Dell XPS 13 đang bán tại shop.', '30ddedae-9520-4581-b2cf-c6fe04eec84e.mp4', 1),
        (N'manager', N'ASUS ROG Strix G16 - Trải nghiệm laptop hiệu năng cao', N'Video quay thật trải nghiệm làm việc trên laptop, minh họa cho ASUS ROG Strix G16 hiệu năng cao tại shop.', 'e5c042ff-8e06-4e49-adb1-c7c3a32ba6c8.mp4', 2),
        (N'admin', N'MacBook Air M3 - Thiết kế mỏng nhẹ hiện đại', N'Video quay thật một mẫu laptop tối giản và mỏng nhẹ, minh họa cho MacBook Air M3.', 'dfcd7a25-4bd2-4a2c-9df5-f7bb90177b52.mp4', 3),
        (N'manager', N'Top laptop cho công việc và học tập', N'Video quay thật giới thiệu laptop phục vụ công việc, học tập và làm việc tại nhà.', '0847280b-024c-4cd3-9090-f28fe2267f18.mp4', 4),
        (N'admin', N'iPhone 15 Pro - Khám phá camera smartphone', N'Video quay thật cận cảnh camera smartphone, minh họa tính năng chụp ảnh của iPhone 15 Pro.', '1f06fa5b-f205-41bf-8587-23f4652f9b55.mp4', 5),
        (N'manager', N'Samsung Galaxy S24 - Thiết kế điện thoại hiện đại', N'Video quay thật cận cảnh thiết kế và cụm camera điện thoại, minh họa cho Samsung Galaxy S24.', '53e7e46c-74da-4c6c-904e-df1a31b7e645.mp4', 6),
        (N'admin', N'Google Pixel 8 - Hỗ trợ công việc online', N'Video quay thật sử dụng smartphone và laptop để vận hành công việc online, minh họa cho Google Pixel 8.', '8398f3e6-dedc-4233-8957-14091c735e8d.mp4', 7),
        (N'manager', N'Clean Code - Sách dành cho lập trình viên', N'Video quay thật hoạt động đọc sách và học cùng laptop, phù hợp giới thiệu sách Clean Code.', '99d3148a-881d-4057-ac3d-da0c2ebd5d56.mp4', 8),
        (N'admin', N'Java Căn Bản - Bắt đầu học lập trình', N'Video quay thật đọc và ghi chú sách, phù hợp cho người bắt đầu học Java Căn Bản.', '2bb79fb9-64de-499b-b873-42e00094f8c2.mp4', 9),
        (N'manager', N'Combo laptop và sách lập trình', N'Video quay thật kết hợp đọc sách và sử dụng laptop dành cho khách hàng học lập trình.', '778556a1-9d1d-4d92-8daf-aeb605db0949.mp4', 10)
    ) AS source(username, title, description, file_name, sort_order)
)
INSERT INTO dbo.Videos(title, description, file_name, user_id, created_at)
SELECT demo.title, demo.description, demo.file_name, account.id,
       DATEADD(MINUTE, -demo.sort_order, SYSUTCDATETIME())
FROM DemoVideos demo
JOIN dbo.[User] account ON account.username = demo.username
WHERE NOT EXISTS (SELECT 1 FROM dbo.Videos existing WHERE existing.file_name = demo.file_name);

COMMIT TRANSACTION;
GO
