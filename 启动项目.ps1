# 智慧养老健康平台 - PowerShell 启动脚本

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "智慧养老健康平台 - 启动脚本" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查环境
Write-Host "[1/4] 检查环境..." -ForegroundColor Yellow

$javaVersion = & java -version 2>&1 | Select-String "version"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] 未找到 Java，请先安装 JDK 17+" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

$nodeVersion = & node --version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[错误] 未找到 Node.js，请先安装 Node.js 18+" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "[✓] Java 和 Node.js 已安装" -ForegroundColor Green
Write-Host ""

# 启动后端
Write-Host "[2/4] 启动后端服务..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "java -jar release\huiyang-home-1.0.0.jar" -WindowStyle Normal
Write-Host "[✓] 后端服务启动中..." -ForegroundColor Green
Write-Host "    访问地址: http://localhost:8080" -ForegroundColor Gray
Write-Host "    接口文档: http://localhost:8080/swagger-ui/index.html" -ForegroundColor Gray
Write-Host ""

# 等待后端启动
Write-Host "[3/4] 等待后端启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 15
Write-Host "[✓] 后端服务已就绪" -ForegroundColor Green
Write-Host ""

# 启动前端
Write-Host "[4/4] 启动前端服务..." -ForegroundColor Yellow
Set-Location frontend
Start-Process powershell -ArgumentList "-NoExit", "-Command", "npm run dev" -WindowStyle Normal
Set-Location ..
Write-Host "[✓] 前端服务启动中..." -ForegroundColor Green
Write-Host "    访问地址: http://localhost:5173" -ForegroundColor Gray
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "启动完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "前端地址: " -NoNewline
Write-Host "http://localhost:5173" -ForegroundColor Blue
Write-Host "后端地址: " -NoNewline
Write-Host "http://localhost:8080" -ForegroundColor Blue
Write-Host "接口文档: " -NoNewline
Write-Host "http://localhost:8080/swagger-ui/index.html" -ForegroundColor Blue
Write-Host ""
Write-Host "默认账号: " -NoNewline
Write-Host "admin / 123456" -ForegroundColor Yellow
Write-Host ""

$openBrowser = Read-Host "是否打开浏览器访问前端？(Y/n)"
if ($openBrowser -ne "n" -and $openBrowser -ne "N") {
    Start-Process "http://localhost:5173"
}
