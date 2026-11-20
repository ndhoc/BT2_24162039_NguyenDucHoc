-- SQL Server 2019+; rerunning preserves existing coursework data.
IF DB_ID(N'MVC_24162039') IS NULL CREATE DATABASE MVC_24162039;
GO
USE MVC_24162039;
GO
IF OBJECT_ID(N'dbo.User', N'U') IS NULL
CREATE TABLE dbo.[User] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    email NVARCHAR(255) NOT NULL UNIQUE,
    username NVARCHAR(50) NOT NULL UNIQUE,
    fullname NVARCHAR(100) NOT NULL,
    password NVARCHAR(255) NOT NULL,
    avatar NVARCHAR(255) NULL,
    roleid INT NOT NULL DEFAULT 5 CHECK (roleid IN (1,2,5)),
    phone VARCHAR(15) NOT NULL UNIQUE,
    createddate DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    is_active BIT NOT NULL DEFAULT 1
);
GO
IF COL_LENGTH(N'dbo.[User]', N'is_active') IS NULL
ALTER TABLE dbo.[User] ADD is_active BIT NOT NULL CONSTRAINT DF_User_is_active DEFAULT 1;
GO
IF COL_LENGTH(N'dbo.[User]', N'avatar_public_id') IS NULL
ALTER TABLE dbo.[User] ADD avatar_public_id NVARCHAR(255) NULL;
GO
IF OBJECT_ID(N'dbo.ProfileEmailChange', N'U') IS NULL
CREATE TABLE dbo.ProfileEmailChange (
    user_id INT PRIMARY KEY REFERENCES dbo.[User](id) ON DELETE CASCADE,
    new_email NVARCHAR(255) NOT NULL,
    code_hash CHAR(64) NOT NULL,
    expires_at DATETIMEOFFSET NOT NULL,
    attempts INT NOT NULL DEFAULT 0
);
GO
IF OBJECT_ID(N'dbo.Category', N'U') IS NULL
CREATE TABLE dbo.Category (
    cate_id INT IDENTITY(1,1) PRIMARY KEY,
    cate_name NVARCHAR(255) NOT NULL,
    icons NVARCHAR(255) NULL
);
GO
IF OBJECT_ID(N'dbo.Product', N'U') IS NULL
CREATE TABLE dbo.Product (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    product_name NVARCHAR(255) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    image NVARCHAR(255) NULL,
    description NVARCHAR(1000) NULL,
    cate_id INT NOT NULL REFERENCES dbo.Category(cate_id)
);
GO
IF OBJECT_ID(N'dbo.RememberToken', N'U') IS NULL
CREATE TABLE dbo.RememberToken (
    token_hash CHAR(64) PRIMARY KEY,
    user_id INT NOT NULL REFERENCES dbo.[User](id) ON DELETE CASCADE,
    expires_at DATETIME2 NOT NULL
);
GO
IF OBJECT_ID(N'dbo.OtpToken', N'U') IS NULL
CREATE TABLE dbo.OtpToken (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL REFERENCES dbo.[User](id) ON DELETE CASCADE,
    purpose VARCHAR(20) NOT NULL CHECK (purpose IN ('ACTIVATION','RESET')),
    code_hash CHAR(64) NOT NULL,
    expires_at DATETIME2 NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT UQ_OtpToken_user_purpose UNIQUE (user_id, purpose)
);
GO
IF OBJECT_ID(N'dbo.Videos', N'U') IS NULL
CREATE TABLE dbo.Videos (
    video_id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(1000) NULL,
    file_name VARCHAR(255) NOT NULL,
    user_id INT NOT NULL REFERENCES dbo.[User](id) ON DELETE CASCADE,
    created_at DATETIMEOFFSET NOT NULL DEFAULT SYSUTCDATETIME()
);
GO

-- Nâng cấp bảng do Hibernate hoặc phiên bản bài cũ đã tạo trước đó.
-- NVARCHAR giữ đúng tiêu đề/mô tả tiếng Việt; DATETIMEOFFSET khớp ánh xạ Date của Hibernate.
IF OBJECT_ID(N'dbo.Videos', N'U') IS NOT NULL
BEGIN
    UPDATE dbo.Videos SET created_at = SYSUTCDATETIME() WHERE created_at IS NULL;
    ALTER TABLE dbo.Videos ALTER COLUMN title NVARCHAR(255) NOT NULL;
    ALTER TABLE dbo.Videos ALTER COLUMN description NVARCHAR(1000) NULL;
    ALTER TABLE dbo.Videos ALTER COLUMN file_name VARCHAR(255) NOT NULL;
    ALTER TABLE dbo.Videos ALTER COLUMN created_at DATETIMEOFFSET NOT NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.default_constraints dc
    JOIN sys.columns c ON c.object_id = dc.parent_object_id AND c.column_id = dc.parent_column_id
    WHERE dc.parent_object_id = OBJECT_ID(N'dbo.Videos') AND c.name = N'created_at'
)
ALTER TABLE dbo.Videos ADD CONSTRAINT DF_Videos_created_at DEFAULT SYSUTCDATETIME() FOR created_at;
GO

-- Chuẩn hóa khóa ngoại kể cả khi bảng đã được Hibernate tạo với NO ACTION.
DECLARE @VideoUserFk SYSNAME;
SELECT TOP (1) @VideoUserFk = fk.name
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc ON fkc.constraint_object_id = fk.object_id
JOIN sys.columns pc ON pc.object_id = fkc.parent_object_id AND pc.column_id = fkc.parent_column_id
WHERE fk.parent_object_id = OBJECT_ID(N'dbo.Videos')
  AND fk.referenced_object_id = OBJECT_ID(N'dbo.[User]')
  AND pc.name = N'user_id';

IF @VideoUserFk IS NOT NULL
BEGIN
    DECLARE @DropVideoUserFkSql NVARCHAR(1000);
    SET @DropVideoUserFkSql = N'ALTER TABLE dbo.Videos DROP CONSTRAINT ' + QUOTENAME(@VideoUserFk);
    EXEC sys.sp_executesql @DropVideoUserFkSql;
END

ALTER TABLE dbo.Videos WITH CHECK
ADD CONSTRAINT FK_Videos_User FOREIGN KEY (user_id) REFERENCES dbo.[User](id) ON DELETE CASCADE;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Videos_user_created' AND object_id = OBJECT_ID(N'dbo.Videos'))
CREATE INDEX IX_Videos_user_created ON dbo.Videos(user_id, created_at DESC);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Videos_created' AND object_id = OBJECT_ID(N'dbo.Videos'))
CREATE INDEX IX_Videos_created ON dbo.Videos(created_at DESC, video_id DESC);
GO
-- Demo accounts are seeded separately by seed.sql.
