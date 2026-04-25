import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// NProgress 配置
NProgress.configure({ showSpinner: false, speed: 500 })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '注册', public: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'msg',
        name: 'MyMsg',
        component: () => import('@/views/msg/index.vue'),
        meta: { title: '我的消息' }
      },
      {
        path: 'admin/user',
        name: 'AdminUser',
        component: () => import('@/views/admin/user.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'admin/service',
        name: 'AdminService',
        component: () => import('@/views/admin/service.vue'),
        meta: { title: '服务管理' }
      },
      {
        path: 'admin/content',
        name: 'AdminContent',
        component: () => import('@/views/admin/content.vue'),
        meta: { title: '内容管理' }
      },
      {
        path: 'admin/community',
        name: 'AdminCommunity',
        component: () => import('@/views/admin/community.vue'),
        meta: { title: '社区互动' }
      },
      {
        path: 'admin/orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/orders.vue'),
        meta: { title: '订单调度' }
      },
      {
        path: 'admin/health',
        name: 'AdminHealth',
        component: () => import('@/views/admin/health.vue'),
        meta: { title: '健康预警' }
      },
      {
        path: 'admin/bindings',
        name: 'AdminBindings',
        component: () => import('@/views/admin/bindings.vue'),
        meta: { title: '家属绑定' }
      },
      {
        path: 'admin/msg',
        name: 'AdminMsg',
        component: () => import('@/views/admin/msg.vue'),
        meta: { title: '消息管理' }
      },
      {
        path: 'admin/system',
        name: 'AdminSystem',
        component: () => import('@/views/admin/system.vue'),
        meta: { title: '系统监控' }
      },
      {
        path: 'elder/services',
        name: 'ElderServices',
        component: () => import('@/views/elder/services.vue'),
        meta: { title: '服务大厅' }
      },
      {
        path: 'elder/orders',
        name: 'ElderOrders',
        component: () => import('@/views/elder/orders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'elder/health',
        name: 'ElderHealth',
        component: () => import('@/views/elder/health.vue'),
        meta: { title: '健康管理' }
      },
      {
        path: 'elder/content',
        name: 'ElderContent',
        component: () => import('@/views/elder/content.vue'),
        meta: { title: '资讯活动' }
      },
      {
        path: 'elder/family',
        name: 'ElderFamily',
        component: () => import('@/views/elder/family.vue'),
        meta: { title: '家属授权' }
      },
      {
        path: 'elder/location',
        name: 'ElderLocation',
        component: () => import('@/views/elder/location.vue'),
        meta: { title: '位置安全' }
      },
      {
        path: 'staff/orders',
        name: 'StaffOrders',
        component: () => import('@/views/staff/orders.vue'),
        meta: { title: '接单中心' }
      },
      {
        path: 'staff/center',
        name: 'StaffCenter',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'staff/messages',
        name: 'StaffMessages',
        component: () => import('@/views/msg/index.vue'),
        meta: { title: '消息通知' }
      },
      {
        path: 'staff/profile',
        name: 'StaffProfile',
        component: () => import('@/views/staff/profile.vue'),
        meta: { title: '服务档案' }
      },
      {
        path: 'staff/schedule',
        name: 'StaffSchedule',
        component: () => import('@/views/staff/schedule.vue'),
        meta: { title: '日程管理' }
      },
      {
        path: 'family/bindings',
        name: 'FamilyBindings',
        component: () => import('@/views/family/bindings.vue'),
        meta: { title: '绑定管理' }
      },
      {
        path: 'family/health',
        name: 'FamilyHealth',
        component: () => import('@/views/family/health.vue'),
        meta: { title: '健康监测' }
      },
      {
        path: 'family/orders',
        name: 'FamilyOrders',
        component: () => import('@/views/family/orders.vue'),
        meta: { title: '服务同步' }
      },
      {
        path: 'family/location',
        name: 'FamilyLocation',
        component: () => import('@/views/family/location.vue'),
        meta: { title: '位置查看' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  NProgress.start()
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    next('/login')
  } else {
    next()
  }
})

router.afterEach((to) => {
  NProgress.done()
  if (to.meta.title) {
    document.title = `${to.meta.title} - 慧养家园`
  }
})

export default router
