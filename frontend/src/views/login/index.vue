<template>
  <div class="auth-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="deco-circle deco-circle-1"></div>
      <div class="deco-circle deco-circle-2"></div>
      <div class="deco-circle deco-circle-3"></div>
    </div>

    <div class="auth-stage">
      <!-- 左侧品牌展示区 -->
      <section class="brand-panel">
        <div class="brand-header">
          <div class="logo-mark">
            <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
              <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
              <path d="M16 8v16M8 16h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              <circle cx="16" cy="16" r="4" fill="currentColor"/>
            </svg>
          </div>
          <span class="logo-text">慧养家园</span>
        </div>

        <div class="brand-hero">
          <h1 class="hero-title">
            <span class="line-1">温暖相伴</span>
            <span class="line-2">守护银龄</span>
          </h1>
          <p class="hero-desc">为社区养老场景打造全方位服务平台<br/>让关爱触手可及</p>
        </div>

        <div class="brand-features">
          <div class="feature-item" v-for="item in features" :key="item.title">
            <div class="feature-icon">
              <component :is="item.icon" />
            </div>
            <div class="feature-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.desc }}</p>
            </div>
          </div>
        </div>

        <div class="brand-footer">
          <div class="stat-item">
            <span class="stat-num">10K+</span>
            <span class="stat-label">服务老人</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">500+</span>
            <span class="stat-label">合作机构</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-num">98%</span>
            <span class="stat-label">满意度</span>
          </div>
        </div>
      </section>

      <!-- 右侧表单区 -->
      <section class="form-panel">
        <div class="form-card">
          <div class="form-header">
            <div class="form-tabs">
              <button
                :class="['tab-btn', { active: activeTab === 'login' }]"
                @click="activeTab = 'login'"
              >
                登录
              </button>
              <button
                :class="['tab-btn', { active: activeTab === 'register' }]"
                @click="activeTab = 'register'"
              >
                注册
              </button>
            </div>
            <p class="form-subtitle">
              {{ activeTab === 'login' ? '欢迎回来，请输入您的账号信息' : '创建账号，开启智慧养老新体验' }}
            </p>
          </div>

          <!-- 登录表单 -->
          <el-form
            v-show="activeTab === 'login'"
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            label-position="top"
            class="auth-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            </div>

            <el-button type="primary" class="submit-btn" size="large" :loading="loading" @click="handleLogin">
              登录系统
            </el-button>

            <!-- 快速登录按钮 - 仅供调试使用 -->
            <!-- TODO: 项目完成后删除此部分 -->
            <div v-if="showQuickLogin" class="quick-login-wrapper">
              <el-divider>快速登录（调试）</el-divider>
              <div class="quick-login-section">
                <el-button
                  v-for="user in quickLoginUsers"
                  :key="user.username"
                  :type="user.type"
                  size="small"
                  @click="handleQuickLogin(user.username, user.password)"
                  class="quick-login-btn"
                >
                  {{ user.label }}
                </el-button>
              </div>
            </div>

            <!-- 快速登录显示/隐藏按钮 -->
            <!-- TODO: 项目完成后删除此部分 -->
            <div class="quick-login-toggle">
              <el-button
                text
                size="small"
                @click="showQuickLogin = !showQuickLogin"
                class="toggle-btn"
              >
                {{ showQuickLogin ? '隐藏' : '显示' }}快速登录
              </el-button>
            </div>
          </el-form>

          <!-- 注册表单 -->
          <el-form
            v-show="activeTab === 'register'"
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            label-position="top"
            class="auth-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="昵称" prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                size="large"
              >
                <template #prefix>
                  <el-icon><EditPen /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="注册角色" prop="role">
              <el-radio-group v-model="registerForm.role" class="role-radio-group">
                <el-radio-button label="ELDER">老人用户</el-radio-button>
                <el-radio-button label="FAMILY">家属用户</el-radio-button>
                <el-radio-button label="STAFF">服务人员</el-radio-button>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                size="large"
                show-password
              >
                <template #prefix>
                  <el-icon><Check /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-button type="primary" class="submit-btn" size="large" :loading="loading" @click="handleRegister">
              注册账号
            </el-button>
          </el-form>

          <div class="form-footer">
            <p v-if="activeTab === 'login'">
              还没有账号？
              <button type="button" class="footer-link" @click="activeTab = 'register'">立即注册</button>
            </p>
            <p v-else>
              已有账号？
              <button type="button" class="footer-link" @click="activeTab = 'login'">前往登录</button>
            </p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance } from 'element-plus'
import { User, Lock, EditPen, Check } from '@element-plus/icons-vue'
import { login, register as apiRegister } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { getRoleHomePath } from '@/utils/role'

type AuthTab = 'login' | 'register'

// 图标组件
import { h } from 'vue'

const HeartIcon = {
  render: () => h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z' })
  ])
}

const ShieldIcon = {
  render: () => h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10' }),
    h('path', { d: 'm9 12 2 2 4-4' })
  ])
}

const UsersIcon = {
  render: () => h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }),
    h('circle', { cx: 9, cy: 7, r: 4 }),
    h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }),
    h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' })
  ])
}

const ClockIcon = {
  render: () => h('svg', { width: 24, height: 24, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('circle', { cx: 12, cy: 12, r: 10 }),
    h('polyline', { points: '12 6 12 12 16 14' })
  ])
}

const features = [
  { icon: HeartIcon, title: '贴心服务', desc: '家政、医护、生活照料全覆盖' },
  { icon: ShieldIcon, title: '安全保障', desc: '健康数据实时监测与预警' },
  { icon: UsersIcon, title: '家属协同', desc: '多端同步，关怀时刻在线' },
  { icon: ClockIcon, title: '及时响应', desc: '7×24小时服务响应机制' },
]

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref<AuthTab>(route.path === '/register' ? 'register' : 'login')
const loading = ref(false)
const rememberMe = ref(false)

// 快速登录用户列表 - 仅供调试使用
// TODO: 项目完成后删除此部分
const showQuickLogin = ref(false)
const quickLoginUsers = [
  { username: 'admin', password: '123456', label: '管理员', type: 'danger' },
  { username: 'demo_elder_1', password: '123456', label: '老人', type: 'primary' },
  { username: 'demo_staff_1', password: '123456', label: '服务人员', type: 'success' },
  { username: 'demo_family_1', password: '123456', label: '家属', type: 'warning' },
]

const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
  username: '',
  password: '',
})

const registerFormRef = ref<FormInstance>()
const registerForm = reactive({
  username: '',
  nickname: '',
  role: 'ELDER',
  password: '',
  confirmPassword: '',
})

watch(
  () => route.path,
  (path) => {
    activeTab.value = path === '/register' ? 'register' : 'login'
  }
)

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择注册角色', trigger: 'change' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' },
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login(loginForm)
      if (!res.success || !res.data?.token) return

      userStore.setToken(res.data.token)
      await userStore.fetchMe()

      const homePath = getRoleHomePath(userStore.userInfo?.role)
      localStorage.setItem('userRole', userStore.userInfo?.role || 'ELDER')
      ElMessage.success('登录成功')

      await router.replace(homePath)
      if (router.currentRoute.value.path === '/login' || router.currentRoute.value.path === '/register') {
        window.location.href = homePath
      }
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await apiRegister({
        username: registerForm.username,
        nickname: registerForm.nickname,
        role: registerForm.role,
        password: registerForm.password,
      })
      if (res.success) {
        ElMessage.success('注册成功，请登录')
        activeTab.value = 'login'
      }
    } catch (error) {
      console.error(error)
    } finally {
      loading.value = false
    }
  })
}

// 快速登录函数 - 仅供调试使用
// TODO: 项目完成后删除此函数
const handleQuickLogin = async (username: string, password: string) => {
  loginForm.username = username
  loginForm.password = password
  await handleLogin()
}
</script>

<style scoped lang="scss">
.auth-container {
  --white-50: #FAFAFA;
  --white-100: #F5F5F5;
  --white-200: #EEEEEE;
  --white-300: #E0E0E0;
  --white-400: #BDBDBD;
  --white-500: #9E9E9E;
  --white-600: #757575;
  --white-700: #616161;
  --white-800: #424242;
  --white-900: #212121;

  --primary: #E85A3C;
  --primary-light: #FF7A5C;
  --primary-dark: #C44A2E;
  --primary-bg: #FEF2EF;

  --green-dark: #1B4332;
  --green-mid: #2D6A4F;
  --green-light: #40916C;

  --coral: #FF8A6A;
  --gold: #D4A853;

  min-height: 100dvh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #FAFAFA 0%, #F5F5F5 50%, #FAFAFA 100%);
  box-sizing: border-box;
  overflow: hidden;
  font-family: 'Source Han Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  position: relative;
}

// 背景装饰
.bg-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
  filter: blur(80px);
}

.deco-circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  left: -200px;
  background: radial-gradient(circle, rgba(232, 90, 60, 0.08) 0%, transparent 70%);
}

.deco-circle-2 {
  width: 500px;
  height: 500px;
  bottom: -150px;
  right: -150px;
  background: radial-gradient(circle, rgba(27, 67, 50, 0.06) 0%, transparent 70%);
}

.deco-circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(212, 168, 83, 0.05) 0%, transparent 70%);
}

.auth-stage {
  width: min(1200px, 100%);
  min-height: min(800px, calc(100dvh - 48px));
  display: grid;
  grid-template-columns: 1.1fr 480px;
  background: #FFFFFF;
  border-radius: 32px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.02),
    0 12px 24px rgba(0, 0, 0, 0.04),
    0 24px 48px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

// 左侧品牌区
.brand-panel {
  display: flex;
  flex-direction: column;
  padding: 48px 56px;
  background: linear-gradient(180deg, #FFFFFF 0%, #FAFAFA 100%);
  position: relative;
  overflow: hidden;
}

.brand-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-mark {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: 14px;
  color: #FFFFFF;
  box-shadow: 0 8px 20px rgba(232, 90, 60, 0.25);
}

.logo-text {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--white-800);
  letter-spacing: 0.05em;
}

.brand-hero {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px 0;
}

.hero-title {
  margin: 0;
  font-size: clamp(2.4rem, 4vw, 3.6rem);
  font-weight: 800;
  line-height: 1.2;
  letter-spacing: -0.02em;
  color: var(--white-900);

  .line-1 {
    display: block;
    background: linear-gradient(135deg, var(--white-900) 0%, var(--white-700) 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .line-2 {
    display: block;
    background: linear-gradient(135deg, var(--primary) 0%, var(--coral) 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.hero-desc {
  margin: 20px 0 0;
  font-size: 1.05rem;
  line-height: 1.9;
  color: var(--white-600);
  max-width: 420px;
}

.brand-features {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  padding: 24px 0;
  border-top: 1px solid var(--white-200);
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 16px;
  background: var(--white-50);
  border: 1px solid var(--white-200);
  border-radius: 16px;
  transition: all 0.3s ease;

  &:hover {
    background: #FFFFFF;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
    transform: translateY(-2px);
  }
}

.feature-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-bg);
  border-radius: 10px;
  color: var(--primary);
  flex-shrink: 0;
}

.feature-content {
  h3 {
    margin: 0;
    font-size: 0.95rem;
    font-weight: 700;
    color: var(--white-800);
  }

  p {
    margin: 4px 0 0;
    font-size: 0.82rem;
    color: var(--white-500);
    line-height: 1.5;
  }
}

.brand-footer {
  display: flex;
  align-items: center;
  gap: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--white-200);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-num {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 0.78rem;
  color: var(--white-500);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.stat-divider {
  width: 1px;
  height: 36px;
  background: var(--white-300);
}

// 右侧表单区
.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: linear-gradient(180deg, #F8F9FA 0%, #FFFFFF 100%);
  border-left: 1px solid var(--white-200);
}

.form-card {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 32px;
}

.form-tabs {
  display: flex;
  gap: 8px;
  padding: 6px;
  background: var(--white-100);
  border-radius: 14px;
  margin-bottom: 20px;
}

.tab-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--white-600);
  cursor: pointer;
  transition: all 0.3s ease;

  &.active {
    background: #FFFFFF;
    color: var(--white-900);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  &:hover:not(.active) {
    color: var(--white-800);
  }
}

.form-subtitle {
  margin: 0;
  font-size: 0.92rem;
  color: var(--white-600);
  line-height: 1.6;
}

.auth-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-form-item__label) {
    color: var(--white-800);
    font-weight: 600;
    font-size: 0.88rem;
    padding: 0 0 8px;
  }

  :deep(.el-input__wrapper) {
    padding: 4px 16px;
    border-radius: 12px;
    background: #FFFFFF;
    box-shadow: 0 0 0 1px var(--white-300) inset;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 0 0 1px var(--white-400) inset;
    }

    &.is-focus {
      box-shadow: 0 0 0 2px var(--primary) inset;
    }
  }

  :deep(.el-input__inner) {
    height: 44px;
    font-size: 0.95rem;

    &::placeholder {
      color: var(--white-400);
    }
  }

  :deep(.el-input__prefix) {
    color: var(--white-500);
  }
}

.form-options {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 24px;

  :deep(.el-checkbox__label) {
    color: var(--white-600);
    font-size: 0.88rem;
  }
}

.submit-btn {
  width: 100%;
  height: 52px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  box-shadow: 0 8px 24px rgba(232, 90, 60, 0.3);
  font-size: 1rem;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: 0.04em;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(232, 90, 60, 0.35);
  }

  &:active {
    transform: translateY(0);
  }
}

// 快速登录区域样式 - 仅供调试使用
// TODO: 项目完成后删除此部分
.quick-login-wrapper {
  margin-top: 8px;
}

.quick-login-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-top: 12px;

  .quick-login-btn {
    width: 100%;
  }
}

.quick-login-toggle {
  margin-top: 16px;
  text-align: center;

  .toggle-btn {
    font-size: 12px;
    color: var(--white-500);
    padding: 4px 8px;

    &:hover {
      color: var(--primary);
    }
  }
}

:deep(.el-divider__text) {
  font-size: 12px;
  color: var(--white-500);
  background: linear-gradient(180deg, #F8F9FA 0%, #FFFFFF 100%);
}

.form-footer {
  margin-top: 28px;
  text-align: center;
  color: var(--white-600);
  font-size: 0.88rem;

  p {
    margin: 0;
  }
}

.footer-link {
  padding: 0;
  border: none;
  background: transparent;
  color: var(--primary);
  font: inherit;
  font-weight: 700;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

// 响应式
@media (max-width: 1024px) {
  .auth-stage {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .brand-panel {
    padding: 40px 32px;
  }

  .brand-features {
    grid-template-columns: repeat(2, 1fr);
  }

  .brand-footer {
    justify-content: center;
  }

  .form-panel {
    border-left: none;
    border-top: 1px solid var(--white-200);
    padding: 40px 32px;
  }
}

@media (max-width: 640px) {
  .auth-container {
    padding: 16px;
  }

  .brand-panel {
    padding: 32px 24px;
  }

  .brand-hero {
    padding: 24px 0;
  }

  .hero-title {
    font-size: 2.2rem;
  }

  .brand-features {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .feature-item {
    padding: 14px;
  }

  .brand-footer {
    flex-wrap: wrap;
    gap: 16px;
  }

  .stat-divider {
    display: none;
  }

  .form-panel {
    padding: 32px 24px;
  }
}
</style>
