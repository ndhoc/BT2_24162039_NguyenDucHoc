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
-- Demo accounts are seeded separately by seed.sql.
