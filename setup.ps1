# ============================================================
# setup.ps1 —— 安装依赖 + 初始化数据库
# 用法: .\setup.ps1
# ============================================================

param(
    [string]$MysqlUser     = "root",
    [string]$MysqlPassword = "123456",
    [string]$MysqlHost     = "127.0.0.1",
    [string]$MysqlPort     = "3306",
    [string]$DbName        = "154laoren"
)

$ErrorActionPreference = "Stop"
$RootDir = $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " 智慧养老健康平台 - 环境初始化脚本"
Write-Host "========================================`n" -ForegroundColor Cyan

# ---------- 1. 设置 JDK 21 ----------
Write-Host "[1/3] 检查 Java 环境 ..." -ForegroundColor Yellow
if (Test-Path "F:\JAVA\Jdk21") {
    $env:JAVA_HOME = "F:\JAVA\Jdk21"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "  ✓ 使用 JDK 21: $env:JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "  ! 未找到 F:\JAVA\Jdk21，使用系统默认 Java（需 21+）" -ForegroundColor DarkYellow
}
java -version 2>&1 | Select-Object -First 1

# ---------- 2. 安装后端依赖 ----------
Write-Host "`n[2/3] 安装依赖 ..." -ForegroundColor Yellow

Write-Host "  → 后端 Maven 依赖"
Push-Location "$RootDir\backend"
mvn dependency:resolve -q
if ($LASTEXITCODE -ne 0) { throw "后端依赖安装失败" }
Pop-Location
Write-Host "  ✓ 后端依赖完成" -ForegroundColor Green

Write-Host "  → 前端 npm 依赖"
Push-Location "$RootDir\frontend"
npm install
if ($LASTEXITCODE -ne 0) { throw "前端依赖安装失败" }
Pop-Location
Write-Host "  ✓ 前端依赖完成" -ForegroundColor Green

# ---------- 3. 初始化数据库 ----------
Write-Host "`n[3/3] 初始化数据库 ..." -ForegroundColor Yellow

$mysqlExe = "mysql"
# 尝试找到 MySQL 可执行文件
$candidates = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
)
foreach ($c in $candidates) {
    if (Test-Path $c) { $mysqlExe = $c; break }
}

$createDb = "CREATE DATABASE IF NOT EXISTS ``$DbName`` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
$createDb | & $mysqlExe -u$MysqlUser -p$MysqlPassword -h$MysqlHost -P$MysqlPort 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) { throw "数据库创建失败，请检查 MySQL 连接参数" }
Write-Host "  ✓ 数据库 [$DbName] 已就绪" -ForegroundColor Green
Write-Host "  ✓ 表结构将在首次启动后端时由 Flyway 自动创建" -ForegroundColor Green

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " ✅ 初始化完成！" -ForegroundColor Green
Write-Host ""
Write-Host " 启动后端: cd backend ; java -jar target\example-0.0.1-SNAPSHOT.jar"
Write-Host " 启动前端: cd frontend ; npm run dev"
Write-Host "========================================`n" -ForegroundColor Cyan
