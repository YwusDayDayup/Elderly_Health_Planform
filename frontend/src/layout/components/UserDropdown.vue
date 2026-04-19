<template>
  <el-dropdown @command="handleCommand">
    <div class="avatar-wrapper common-flex-center">
      <el-avatar :size="32" class="user-avatar">
        {{ (userInfo?.nickname || userInfo?.username)?.charAt(0) }}
      </el-avatar>
      <div class="user-info-text">
        <span class="nickname">{{ userInfo?.nickname || userInfo?.username }}</span>
        <div class="roles-wrap common-flex-center">
          <el-tag size="small" type="info" class="role-tag">{{ formatRole(userInfo?.role) }}</el-tag>
        </div>
      </div>
      <el-icon class="dropdown-arrow"><EpArrowDown /></el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu class="modern-dropdown">
        <el-dropdown-item command="profile">
          <el-icon><EpUser /></el-icon>
          个人中心
        </el-dropdown-item>
        <el-dropdown-item command="password">
          <el-icon><EpLock /></el-icon>
          修改密码
        </el-dropdown-item>
        <el-dropdown-item command="logout" divided>
          <el-icon><EpSwitchButton /></el-icon>
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { EpArrowDown, EpUser, EpLock, EpSwitchButton } from 'vue-icons-plus/ep'
import { formatRole } from '@/utils/role'

interface UserInfo {
  username?: string
  nickname?: string
  avatarUrl?: string
  role?: string
}

defineProps<{
  userInfo?: UserInfo
}>()

const emit = defineEmits<{
  profile: []
  password: []
  logout: []
}>()

const handleCommand = (command: string | number | object) => {
  if (command === 'logout') {
    emit('logout')
  } else if (command === 'profile') {
    emit('profile')
  } else if (command === 'password') {
    emit('password')
  }
}
</script>

<style scoped lang="scss">
.avatar-wrapper {
  cursor: pointer;
  gap: 10px;
  padding: 6px 12px 6px 6px;
  border-radius: 16px;
  background: var(--app-surface);
  border: 1px solid var(--app-border-color);
  transition: all 0.25s ease;

  &:hover {
    background: var(--app-hover-bg);
    border-color: var(--app-primary);
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(232, 90, 60, 0.1);
  }
}

.user-avatar {
  background: linear-gradient(135deg, var(--app-primary) 0%, var(--app-primary-light) 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.user-info-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;

  .nickname {
    font-size: 14px;
    color: var(--app-text-primary);
    font-weight: 600;
  }

  .roles-wrap {
    gap: 4px;
    margin-top: 3px;

    .role-tag {
      height: 18px;
      padding: 0 8px;
      font-size: 10px;
      font-weight: 600;
      border-radius: 999px;
      color: var(--app-primary);
      background: var(--app-primary-bg);
      border-color: transparent;
    }
  }
}

.dropdown-arrow {
  color: var(--app-text-muted);
  margin-left: 4px;
  transition: transform 0.2s ease;

  .avatar-wrapper:hover & {
    transform: rotate(180deg);
    color: var(--app-primary);
  }
}
</style>

<style lang="scss">
// Global dropdown menu styling
.modern-dropdown {
  padding: 8px !important;
  border-radius: 16px !important;
  border: 1px solid var(--app-border-color) !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08) !important;
  background: var(--app-surface) !important;

  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 14px !important;
    border-radius: 10px !important;
    font-size: 14px !important;
    color: var(--app-text-primary) !important;
    transition: all 0.2s ease;

    .el-icon {
      color: var(--app-text-secondary);
      font-size: 16px;
    }

    &:hover {
      background: var(--app-primary-bg) !important;
      color: var(--app-primary) !important;

      .el-icon {
        color: var(--app-primary);
      }
    }

    &.is-divided {
      margin-top: 4px;
      border-top: 1px solid var(--app-border-color);
    }
  }
}
</style>
