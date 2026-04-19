# ============================================================
# setup_and_push.ps1
# 安装前后端依赖，并将项目推送到 GitHub
# 用法: .\setup_and_push.ps1 [-RepoName 仓库名]  默认: Elderly_Health
# ============================================================

param(
    [string]$RepoName = "Elderly_Health"
)

$ErrorActionPreference = "Stop"

$GithubUser = "YwusDayDayup"
$RemoteUrl  = "git@github.com:${GithubUser}/${RepoName}.git"
$RootDir    = $PSScriptRoot

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " 项目根目录: $RootDir"
Write-Host " 推送目标  : $RemoteUrl"
Write-Host "========================================" -ForegroundColor Cyan

# ---------- 1. 安装后端依赖 ----------
Write-Host "`n[1/4] 安装后端依赖 (mvn dependency:resolve) ..." -ForegroundColor Yellow

# 优先使用 JDK 21
if (Test-Path "F:\JAVA\Jdk21") {
    $env:JAVA_HOME = "F:\JAVA\Jdk21"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
}

Push-Location "$RootDir\backend"
mvn dependency:resolve -q
if ($LASTEXITCODE -ne 0) { throw "后端依赖安装失败" }
Pop-Location
Write-Host "  ✓ 后端依赖安装完成" -ForegroundColor Green

# ---------- 2. 安装前端依赖 ----------
Write-Host "`n[2/4] 安装前端依赖 (npm install) ..." -ForegroundColor Yellow
Push-Location "$RootDir\frontend"
npm install
if ($LASTEXITCODE -ne 0) { throw "前端依赖安装失败" }
Pop-Location
Write-Host "  ✓ 前端依赖安装完成" -ForegroundColor Green

# ---------- 3. 初始化 Git 仓库 ----------
Write-Host "`n[3/4] 初始化 Git 仓库并提交代码 ..." -ForegroundColor Yellow
Push-Location $RootDir

if (-not (Test-Path ".git")) {
    git init
    Write-Host "  ✓ git init 完成" -ForegroundColor Green
}

# 设置远程
$existingRemote = git remote get-url origin 2>$null
if ($existingRemote) {
    git remote set-url origin $RemoteUrl
    Write-Host "  ✓ 已更新 remote origin -> $RemoteUrl" -ForegroundColor Green
} else {
    git remote add origin $RemoteUrl
    Write-Host "  ✓ 已添加 remote origin -> $RemoteUrl" -ForegroundColor Green
}

git add .
$status = git status --porcelain
if ($status) {
    git commit -m "chore: initial commit"
} else {
    Write-Host "  (nothing to commit, skipping)" -ForegroundColor Gray
}

# ---------- 4. 推送到 GitHub ----------
Write-Host "`n[4/4] 推送到 GitHub ..." -ForegroundColor Yellow
git push -u origin main
if ($LASTEXITCODE -ne 0) {
    git push -u origin master
}

Pop-Location

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " ✅ 全部完成！" -ForegroundColor Green
Write-Host " 仓库地址: https://github.com/${GithubUser}/${RepoName}" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
