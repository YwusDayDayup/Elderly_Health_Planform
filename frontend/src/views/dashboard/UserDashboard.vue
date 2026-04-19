<template>
  <div class="user-dashboard">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="never" class="welcome-card">
          <div class="welcome-content">
            <el-avatar :size="64" :src="userStore.userInfo?.avatarUrl">
              {{ (userStore.userInfo?.nickname || userStore.userInfo?.username)?.charAt(0) }}
            </el-avatar>
            <div class="welcome-text">
              <h2>下午好，{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}！</h2>
              <p>欢迎回到管理系统，祝您今天工作愉快。</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="8">
        <el-card shadow="hover" header="我的概览">
          <div class="overview-list">
            <div class="overview-item">
              <span class="label">未读消息</span>
              <el-badge :value="12" class="item" type="danger" />
            </div>
            <el-divider />
            <div class="overview-item">
              <span class="label">已上传文件</span>
              <span class="value">25 个</span>
            </div>
            <el-divider />
            <div class="overview-item">
              <span class="label">当前角色</span>
              <el-tag size="small">普通用户</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="hover" header="快捷入口">
          <el-row :gutter="20">
            <el-col :span="6" v-for="nav in quickNavs" :key="nav.title">
              <div class="quick-nav-item" @click="nav.onClick ? nav.onClick() : router.push(nav.path)">
                <el-icon :size="24" :color="nav.color"><component :is="nav.icon" /></el-icon>
                <span>{{ nav.title }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { EpMessage, EpFiles, EpUser, EpSetting } from 'vue-icons-plus/ep'

const userStore = useUserStore()
const router = useRouter()

const props = defineProps<{
  msgDialogVisible: boolean
  profileDialogVisible: boolean
}>()

const emit = defineEmits(['update:msg-dialog-visible', 'update:profile-dialog-visible'])

const localMsgDialogVisible = computed({
  get: () => props.msgDialogVisible,
  set: (val) => emit('update:msg-dialog-visible', val)
})

const localProfileDialogVisible = computed({
  get: () => props.profileDialogVisible,
  set: (val) => emit('update:profile-dialog-visible', val)
})

const quickNavs = [
  { title: '查看消息', icon: EpMessage, onClick: () => localMsgDialogVisible.value = true, color: '#409eff' },
  { title: '我的文件', icon: EpFiles, path: '/profile', color: '#67c23a' },
  { title: '个人信息', icon: EpUser, onClick: () => localProfileDialogVisible.value = true, color: '#e6a23c' },
  { title: '账号设置', icon: EpSetting, onClick: () => localProfileDialogVisible.value = true, color: '#f56c6c' }
]
</script>

<style scoped lang="scss">
.welcome-card {
  border: none;
  background: linear-gradient(to right, var(--app-welcome-bg-start), var(--app-welcome-bg-end));
  .welcome-content {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px;
    .welcome-text {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: var(--app-text-primary);
      }
      p {
        margin: 0;
        color: var(--app-text-muted);
        font-size: 14px;
      }
    }
  }
}

.mt-20 {
  margin-top: 20px;
}

.overview-list {
  .overview-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 5px 0;
    .label {
      color: var(--app-text-secondary);
      font-size: 14px;
    }
    .value {
      font-weight: bold;
      color: var(--app-text-primary);
    }
  }
  .el-divider {
    margin: 12px 0;
  }
}

.quick-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s;
  &:hover {
    background-color: var(--app-hover-bg);
    transform: translateY(-2px);
  }
  span {
    font-size: 14px;
    color: var(--app-text-secondary);
  }
}
</style>
