#!/usr/bin/env bash
# ============================================================
# setup.sh —— 安装依赖 + 初始化数据库
# 用法: bash setup.sh
# ============================================================

set -e

MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-123456}"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
DB_NAME="${DB_NAME:-154laoren}"

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========================================"
echo " 智慧养老健康平台 - 环境初始化脚本"
echo "========================================"

# ---------- 1. Java 环境 ----------
echo ""
echo "[1/3] 检查 Java 环境 ..."
java -version 2>&1 | head -1
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VER" -lt 21 ] 2>/dev/null; then
  echo "  ✗ 需要 Java 21+，当前版本: $JAVA_VER" && exit 1
fi
echo "  ✓ Java 版本满足要求"

# ---------- 2. 安装依赖 ----------
echo ""
echo "[2/3] 安装依赖 ..."

echo "  → 后端 Maven 依赖"
cd "$ROOT_DIR/backend"
mvn dependency:resolve -q
echo "  ✓ 后端依赖完成"

echo "  → 前端 npm 依赖"
cd "$ROOT_DIR/frontend"
npm install
echo "  ✓ 前端依赖完成"

# ---------- 3. 初始化数据库 ----------
echo ""
echo "[3/3] 初始化数据库 ..."

mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -h"$MYSQL_HOST" -P"$MYSQL_PORT" \
  -e "CREATE DATABASE IF NOT EXISTS \`$DB_NAME\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null

echo "  ✓ 数据库 [$DB_NAME] 已就绪"
echo "  ✓ 表结构将在首次启动后端时由 Flyway 自动创建"

echo ""
echo "========================================"
echo " ✅ 初始化完成！"
echo ""
echo " 启动后端: cd backend && java -jar target/example-0.0.1-SNAPSHOT.jar"
echo " 启动前端: cd frontend && npm run dev"
echo "========================================"
