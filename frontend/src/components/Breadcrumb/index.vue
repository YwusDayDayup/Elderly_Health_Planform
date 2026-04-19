<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span v-if="item.redirect === 'noRedirect' || index === breadcrumbs.length - 1" class="no-redirect">
          {{ item.meta?.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">
          {{ item.meta?.title }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { compile } from 'path-to-regexp'

type BreadcrumbItem = Partial<RouteRecordRaw> & {
  path: string
  meta?: {
    title?: string
    breadcrumb?: boolean
    activeMenu?: string
  }
  redirect?: string
}

const route = useRoute()
const router = useRouter()
const breadcrumbs = ref<BreadcrumbItem[]>([])

const isDashboard = (route: BreadcrumbItem) => {
  const name = route && route.name
  if (!name) {
    return false
  }
  return name.toString().trim().includes('Home')
}

const getBreadcrumb = () => {
  let matched = route.matched.filter(item => item.meta && item.meta.title) as BreadcrumbItem[]
  const first = matched[0]

  if (first && !isDashboard(first)) {
    matched = [{ path: '/home', meta: { title: '首页' } } as BreadcrumbItem].concat(matched)
  }

  breadcrumbs.value = matched.filter(item => {
    return item.meta && item.meta.title && item.meta.breadcrumb !== false
  })
}

const pathCompile = (path: string) => {
  const { params } = route
  const toPath = compile(path)
  return toPath(params)
}

const handleLink = (item: BreadcrumbItem) => {
  const { redirect, path } = item
  if (typeof redirect === 'string' && redirect) {
    router.push(redirect).catch(err => {
      console.warn(err)
    })
    return
  }
  router.push(pathCompile(path)).catch(err => {
    console.warn(err)
  })
}

watch(
  () => route.path,
  () => {
    getBreadcrumb()
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  line-height: 1;

  :deep(.el-breadcrumb__inner) {
    color: var(--app-text-muted);
    font-weight: 500;
    transition: color 0.2s ease;

    a {
      color: var(--app-text-secondary);
      font-weight: 500;
      transition: color 0.2s ease;

      &:hover {
        color: var(--app-primary);
      }
    }
  }

  :deep(.el-breadcrumb__separator) {
    color: var(--app-text-muted);
    margin: 0 6px;
  }

  .no-redirect {
    color: var(--app-text-primary);
    font-weight: 600;
    cursor: text;
  }
}
</style>
