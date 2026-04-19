#!/usr/bin/env bash
# ============================================================
# setup_and_push.sh
# 安装前后端依赖，并将项目推送到 GitHub
# 用法: bash setup_and_push.sh [仓库名]  默认: Elderly_Health
# ============================================================

set -e

REPO_NAME="${1:-Elderly_Health}"
GITHUB_USER="YwusDayDayup"
REMOTE_URL="git@github.com:${GITHUB_USER}/${REPO_NAME}.git"

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========================================"
echo " 项目根目录: $ROOT_DIR"
echo " 推送目标  : $REMOTE_URL"
echo "========================================"

# ---------- 1. 安装后端依赖（下载到本地 Maven 仓库）----------
echo ""
echo "[1/4] 安装后端依赖 (mvn dependency:resolve) ..."

# 优先使用 JDK 21
if [ -d "F:/JAVA/Jdk21" ]; then
  export JAVA_HOME="F:/JAVA/Jdk21"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

cd "$ROOT_DIR/backend"
mvn dependency:resolve -q
echo "  ✓ 后端依赖安装完成"

# ---------- 2. 安装前端依赖 ----------
echo ""
echo "[2/4] 安装前端依赖 (npm install) ..."
cd "$ROOT_DIR/frontend"
npm install
echo "  ✓ 前端依赖安装完成"

# ---------- 3. 初始化 Git 仓库 ----------
echo ""
echo "[3/4] 初始化 Git 仓库并提交代码 ..."
cd "$ROOT_DIR"

if [ ! -d ".git" ]; then
  git init
  echo "  ✓ git init 完成"
fi

# 设置远程（已存在则更新）
if git remote get-url origin &>/dev/null; then
  git remote set-url origin "$REMOTE_URL"
  echo "  ✓ 已更新 remote origin -> $REMOTE_URL"
else
  git remote add origin "$REMOTE_URL"
  echo "  ✓ 已添加 remote origin -> $REMOTE_URL"
fi

git add .
git commit -m "chore: initial commit" || echo "  (nothing to commit, skipping)"

# ---------- 4. 推送到 GitHub ----------
echo ""
echo "[4/4] 推送到 GitHub ..."
git push -u origin main 2>/dev/null || git push -u origin master

echo ""
echo "========================================"
echo " ✅ 全部完成！"
echo " 仓库地址: https://github.com/${GITHUB_USER}/${REPO_NAME}"
echo "========================================"
