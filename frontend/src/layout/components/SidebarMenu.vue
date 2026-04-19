<template>
  <el-aside :width="isCollapse ? '72px' : '240px'" class="aside">
    <div class="logo">
      <div class="logo-mark">
        <svg width="22" height="22" viewBox="0 0 32 32" fill="none">
          <circle cx="16" cy="16" r="14" stroke="currentColor" stroke-width="2"/>
          <path d="M16 8v16M8 16h16" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <circle cx="16" cy="16" r="4" fill="currentColor"/>
        </svg>
      </div>
      <div v-if="!isCollapse" class="logo-copy">
        <strong class="logo-title">社区服务</strong>
        <span class="logo-subtitle">Community Care</span>
      </div>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        :background-color="'transparent'"
        :text-color="'rgba(255,255,255,0.65)'"
        :active-text-color="'#FFFFFF'"
      >
        <el-menu-item v-for="menu in filteredMenus" :key="menu.path" :index="menu.path">
          <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
          <template #title>{{ menu.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  EpDataAnalysis,
  EpUser,
  EpSetting,
  EpReading,
  EpTickets,
  EpBell,
  EpConnection,
  EpVan,
  EpDocument,
  EpOpportunity,
  EpLocation,
  EpAvatar,
  EpMonitor,
  EpChatDotRound,
  EpUserFilled,
  EpDocumentCopy
} from 'vue-icons-plus/ep'

interface MenuRoute {
  path: string
  title: string
  icon?: any
  roles?: string[]
}

const props = defineProps<{
  isCollapse: boolean
  activeMenu: string
  userRole?: string
}>()

const menuRoutes: MenuRoute[] = [
  {
    path: '/dashboard',
    title: '工作台',
    icon: EpDataAnalysis
  },
  {
    path: '/admin/user',
    title: '用户管理',
    icon: EpUser,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/service',
    title: '服务管理',
    icon: EpSetting,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/content',
    title: '内容管理',
    icon: EpReading,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/community',
    title: '社区互动',
    icon: EpDocument,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/orders',
    title: '订单调度',
    icon: EpTickets,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/health',
    title: '健康预警',
    icon: EpBell,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/bindings',
    title: '家属绑定',
    icon: EpConnection,
    roles: ['ADMIN'],
  },
  {
    path: '/admin/system',
    title: '系统监控',
    icon: EpMonitor,
    roles: ['ADMIN'],
  },
  {
    path: '/elder/services',
    title: '服务大厅',
    icon: EpVan,
    roles: ['ELDER'],
  },
  {
    path: '/elder/orders',
    title: '我的订单',
    icon: EpTickets,
    roles: ['ELDER'],
  },
  {
    path: '/elder/health',
    title: '健康管理',
    icon: EpOpportunity,
    roles: ['ELDER'],
  },
  {
    path: '/elder/content',
    title: '资讯活动',
    icon: EpDocument,
    roles: ['ELDER'],
  },
  {
    path: '/elder/family',
    title: '家属授权',
    icon: EpConnection,
    roles: ['ELDER'],
  },
  {
    path: '/elder/location',
    title: '位置安全',
    icon: EpLocation,
    roles: ['ELDER'],
  },
  {
    path: '/staff/orders',
    title: '接单中心',
    icon: EpTickets,
    roles: ['STAFF'],
  },
  {
    path: '/staff/center',
    title: '个人中心',
    icon: EpUserFilled,
    roles: ['STAFF'],
  },
  {
    path: '/staff/profile',
    title: '服务档案',
    icon: EpAvatar,
    roles: ['STAFF'],
  },
  {
    path: '/staff/messages',
    title: '消息通知',
    icon: EpChatDotRound,
    roles: ['STAFF'],
  },
  {
    path: '/staff/schedule',
    title: '日程管理',
    icon: EpDocumentCopy,
    roles: ['STAFF'],
  },
  {
    path: '/family/bindings',
    title: '绑定管理',
    icon: EpConnection,
    roles: ['FAMILY'],
  },
  {
    path: '/family/health',
    title: '健康监测',
    icon: EpOpportunity,
    roles: ['FAMILY'],
  },
  {
    path: '/family/orders',
    title: '服务同步',
    icon: EpTickets,
    roles: ['FAMILY'],
  },
  {
    path: '/family/location',
    title: '位置查看',
    icon: EpLocation,
    roles: ['FAMILY'],
  },
]

const filteredMenus = computed(() => {
  return menuRoutes.filter(menu => {
    if (!menu.roles) return true
    return menu.roles.includes(props.userRole || '')
  })
})
</script>

<style scoped lang="scss">
.aside {
  background: var(--app-sidebar-bg);
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 8px 0 32px rgba(27, 67, 50, 0.15);
  padding: 16px 12px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;

  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 0 8px;
    margin-bottom: 20px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.06);
    backdrop-filter: blur(8px);
    color: #fff;
    flex-shrink: 0;

    .logo-mark {
      width: 40px;
      height: 40px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 12px;
      background: linear-gradient(135deg, var(--app-primary) 0%, var(--app-primary-light) 100%);
      color: #fff;
      flex: none;
      box-shadow: 0 4px 12px rgba(232, 90, 60, 0.35);
    }

    .logo-copy {
      min-width: 0;
      display: flex;
      flex-direction: column;
      gap: 3px;
    }

    .logo-title {
      color: #f5f7f5;
      font-size: 16px;
      font-weight: 700;
      line-height: 1.1;
      letter-spacing: 0.02em;
    }

    .logo-subtitle {
      color: rgba(255, 255, 255, 0.5);
      font-size: 11px;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }
  }

  .el-menu-vertical {
    border: none;
    width: 100% !important;
    background: transparent;
    flex: 1;

    &:not(.el-menu--collapse) {
      width: 216px;
    }

    :deep(.el-menu-item) {
      position: relative;
      height: 48px;
      margin-bottom: 4px;
      border-radius: 12px;
      color: rgba(255, 255, 255, 0.65) !important;
      transition: all 0.25s ease;
      display: flex;
      align-items: center;

      .el-icon {
        font-size: 18px;
        margin-right: 12px;
      }

      &.is-active {
        background: var(--app-sidebar-active-bg) !important;
        color: #FFFFFF !important;
        font-weight: 600;

        &::before {
          content: "";
          position: absolute;
          left: 0;
          top: 50%;
          width: 4px;
          height: 24px;
          border-radius: 0 4px 4px 0;
          background: linear-gradient(180deg, var(--app-primary) 0%, var(--app-primary-light) 100%);
          transform: translateY(-50%);
          box-shadow: 0 2px 8px rgba(232, 90, 60, 0.4);
        }
      }

      &:hover:not(.is-active) {
        background: rgba(255, 255, 255, 0.08) !important;
        color: #FFFFFF !important;
        transform: translateX(2px);
      }
    }
  }
}
</style>
