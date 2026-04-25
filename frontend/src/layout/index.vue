<template>
  <el-container class="layout-container">
    <SidebarMenu
      :is-collapse="isCollapse"
      :active-menu="activeMenu"
      :user-role="userStore.userInfo?.role"
    />

    <el-container class="main-container">
      <el-header class="header">
        <div class="header-left common-flex-center">
          <div class="toggle-icon" @click="toggleCollapse">
            <el-icon v-if="isCollapse"><EpExpand /></el-icon>
            <el-icon v-else><EpFold /></el-icon>
          </div>
          <Breadcrumb />
        </div>

        <div class="header-right common-flex-center">
          <div class="header-actions common-flex-center">
            <TopbarNotice @open-msg="msgDialogVisible = true" />
          </div>
          <UserDropdown
            :user-info="userStore.userInfo"
            @logout="handleLogout"
            @profile="profileDialogVisible = true"
            @password="passwordDialogVisible = true"
          />
        </div>
      </el-header>

      <el-main class="main">
        <div class="app-container">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>

    <PasswordDialog v-model="passwordDialogVisible" />
    <MsgDialog v-model="msgDialogVisible" />
    <ProfileDialog v-model="profileDialogVisible" />
  </el-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { EpExpand, EpFold } from 'vue-icons-plus/ep'
import { useUserStore } from '@/stores/user'
import Breadcrumb from '@/components/Breadcrumb'
import PasswordDialog from '@/views/profile/components/Password.vue'
import MsgDialog from '@/views/msg/MsgDialog.vue'
import ProfileDialog from '@/views/profile/ProfileDialog.vue'
import SidebarMenu from './components/SidebarMenu.vue'
import TopbarNotice from './components/TopbarNotice.vue'
import UserDropdown from './components/UserDropdown.vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const passwordDialogVisible = ref(false)
const msgDialogVisible = ref(false)
const profileDialogVisible = ref(false)

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  userStore.fetchMe()
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  width: 100%;
  background: var(--app-bg);
}

.header {
  height: 64px;
  overflow: hidden;
  position: relative;
  background: var(--app-header-bg);
  box-shadow: var(--app-header-shadow);
  padding: 0 20px 0 16px;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--app-border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;

  .header-left {
    gap: 8px;

    .toggle-icon {
      width: 38px;
      height: 38px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      font-size: 18px;
      color: var(--app-text-secondary);
      border-radius: 12px;
      transition: all 0.25s ease;

      &:hover {
        color: var(--app-primary);
        background: var(--app-primary-bg);
        transform: scale(1.05);
      }
    }
  }

  .header-right {
    gap: 12px;
  }

  .header-actions {
    gap: 8px;
  }
}

.main {
  background-color: var(--app-bg);
  padding: 0;
}

.app-container {
  padding: 20px 24px;
  max-width: 100%;
}

.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
