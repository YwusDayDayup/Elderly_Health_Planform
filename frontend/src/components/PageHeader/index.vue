<template>
  <div class="page-header common-flex-between-center">
    <div class="left-content common-flex-center">
      <el-button v-if="showBack" :icon="ArrowLeft" link @click="goBack">返回</el-button>
      <span class="title">{{ title || route.meta?.title || '页面标题' }}</span>
      <slot name="breadcrumb"></slot>
    </div>
    <div class="right-content">
      <slot name="content"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

defineProps({
  title: {
    type: String,
    default: '',
  },
  showBack: {
    type: Boolean,
    default: false,
  },
})

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/') // 或者跳转到首页
  }
}
</script>

<style lang="scss" scoped>
.page-header {
  padding: 10px 0;
  border-bottom: 1px solid var(--app-border-color);
  margin-bottom: 20px;

  .left-content {
    .title {
      font-size: 18px;
      font-weight: bold;
      margin-left: 10px;
      color: var(--app-text-primary);
    }
  }
  .right-content {
    //
  }
}
</style>
