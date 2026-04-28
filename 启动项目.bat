@echo off
chcp 65001 >nul
echo ========================================
echo 智慧养老健康平台 - 启动脚本
echo ========================================
echo.

echo [1/4] 检查环境...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请先安装 JDK 17+
    pause
    exit /b 1
)

where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Node.js，请先安装 Node.js 18+
    pause
    exit /b 1
)

echo [✓] Java 和 Node.js 已安装
echo.

echo [2/4] 启动后端服务...
start "后端服务" cmd /k "java -jar release\huiyang-home-1.0.0.jar"
echo [✓] 后端服务启动中...
echo     访问地址: http://localhost:8080
echo     接口文档: http://localhost:8080/swagger-ui/index.html
echo.

echo [3/4] 等待后端启动...
timeout /t 15 /nobreak >nul
echo [✓] 后端服务已就绪
echo.

echo [4/4] 启动前端服务...
cd frontend
start "前端服务" cmd /k "npm run dev"
cd ..
echo [✓] 前端服务启动中...
echo     访问地址: http://localhost:5173
echo.

echo ========================================
echo 启动完成！
echo ========================================
echo.
echo 前端地址: http://localhost:5173
echo 后端地址: http://localhost:8080
echo 接口文档: http://localhost:8080/swagger-ui/index.html
echo.
echo 默认账号: admin / 123456
echo.
echo 按任意键打开前端页面...
pause >nul
start http://localhost:5173
