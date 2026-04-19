<template>
  <el-tag :type="tagType" :effect="effect">
    {{ statusText }}
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ElTag } from 'element-plus'

interface StatusItem {
  value: string | number
  text: string
  type: 'success' | 'info' | 'warning' | 'danger' | ''
}

const props = defineProps({
  status: {
    type: [String, Number],
    required: true,
  },
  options: {
    type: Array as () => StatusItem[],
    required: true,
  },
  effect: {
    type: String as () => 'dark' | 'light' | 'plain',
    default: 'light',
  },
})

const currentStatus = computed(() => {
  return props.options.find(item => item.value === props.status)
})

const statusText = computed(() => {
  return currentStatus.value?.text || '未知'
})

const tagType = computed(() => {
  return currentStatus.value?.type || 'info'
})
</script>

<style scoped lang="scss">
@use "./style.scss";
</style>
