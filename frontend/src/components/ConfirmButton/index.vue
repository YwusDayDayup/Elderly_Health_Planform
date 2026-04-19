<template>
  <el-button v-bind="$attrs" @click="handleClick">
    <slot></slot>
  </el-button>
</template>

<script setup lang="ts">
import { ElMessageBox, ElMessage } from 'element-plus'
import { useAttrs } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '确认操作',
  },
  content: {
    type: String,
    default: '此操作将永久删除该文件, 是否继续?',
  },
  confirmButtonText: {
    type: String,
    default: '确定',
  },
  cancelButtonText: {
    type: String,
    default: '取消',
  },
  type: {
    type: String,
    default: 'warning',
  },
})

const emit = defineEmits(['confirm', 'cancel'])

useAttrs()

const handleClick = () => {
  ElMessageBox.confirm(props.content, props.title, {
    confirmButtonText: props.confirmButtonText,
    cancelButtonText: props.cancelButtonText,
    type: props.type as any,
  })
    .then(() => {
      emit('confirm')
      ElMessage({
        type: 'success',
        message: '操作成功',
      })
    })
    .catch(() => {
      emit('cancel')
      ElMessage({
        type: 'info',
        message: '操作已取消',
      })
    })
}
</script>
