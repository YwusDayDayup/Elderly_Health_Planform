<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :fullscreen="fullscreen"
    destroy-on-close
    center
    v-bind="$attrs"
  >
    <template #header="{ close, titleId, titleClass }">
      <div class="common-dialog-header common-flex-between-center">
        <span :id="titleId" :class="titleClass">{{ title }}</span>
        <div class="common-dialog-header-actions common-flex-center">
          <el-button v-if="showFullscreen" link @click="toggleFullscreen">
            <el-icon><FullScreen /></el-icon>
          </el-button>
          <el-button link @click="close">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
      </div>
    </template>

    <slot></slot>

    <template v-if="showFooter" #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { FullScreen, Close } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: '提示',
  },
  width: {
    type: String,
    default: '50%',
  },
  showFooter: {
    type: Boolean,
    default: true,
  },
  showFullscreen: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const visible = ref(props.modelValue)
const fullscreen = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const toggleFullscreen = () => {
  fullscreen.value = !fullscreen.value
}

const handleCancel = () => {
  visible.value = false
  emit('cancel')
}

const handleConfirm = () => {
  // 在这里可以做一些表单校验等逻辑
  emit('confirm')
}
</script>

<style lang="scss" scoped>
.common-dialog-header {
  padding-right: 16px; // 与 Element Plus 默认标题的右边距保持一致

  .common-dialog-header-actions {
    .el-button {
      font-size: 16px;
      margin-left: 8px;
      color: var(--el-text-color-regular);
      &:hover {
        color: var(--el-color-primary);
      }
    }
  }
}

.dialog-footer {
  text-align: right;
}
</style>
