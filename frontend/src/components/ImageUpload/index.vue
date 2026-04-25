<template>
  <el-upload
    class="image-uploader"
    :action="uploadUrl"
    :show-file-list="false"
    :http-request="httpRequest"
    :on-success="handleUploadSuccess"
    :before-upload="beforeUpload"
    :on-error="handleUploadError"
  >
    <img v-if="imageUrl" :src="imageUrl" class="uploaded-image" />
    <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadRequestOptions } from 'element-plus'
import { uploadFile } from '@/api/file'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
  uploadUrl: {
    type: String,
    required: true,
  },
  bizType: {
    type: String,
    default: '',
  },
  fileSize: {
    type: Number,
    default: 2, // MB
  },
  fileTypes: {
    type: Array as () => string[],
    default: () => ['image/jpeg', 'image/png', 'image/gif'],
  },
})

const emit = defineEmits(['update:modelValue', 'onSuccess', 'onError'])

const imageUrl = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  imageUrl.value = newVal
})

const handleUploadSuccess = (response: any) => {
  // 拦截器已经验证过 success，能进入这里说明上传成功
  if (response?.data?.url) {
    imageUrl.value = response.data.url
    emit('update:modelValue', response.data.url)
    emit('onSuccess', response)
    ElMessage.success('上传成功')
  }
}

const handleUploadError = (error: any) => {
  // 拦截器已经显示了错误消息，这里不再重复显示
  emit('onError', error)
}

const httpRequest = async (options: UploadRequestOptions) => {
  try {
    const file = options.file as File
    const res = await uploadFile(file, props.bizType || undefined)
    options.onSuccess?.(res as any)
  } catch (e) {
    options.onError?.(e as any)
  }
}

const beforeUpload = (rawFile: File) => {
  const isCorrectType = props.fileTypes.includes(rawFile.type)
  const isLtFileSize = rawFile.size / 1024 / 1024 < props.fileSize

  if (!isCorrectType) {
    ElMessage.error(`上传文件只能是 ${props.fileTypes.map(type => type.split('/')[1]).join('/')} 格式!`)
  }
  if (!isLtFileSize) {
    ElMessage.error(`上传文件大小不能超过 ${props.fileSize}MB!`)
  }
  return isCorrectType && isLtFileSize
}
</script>

<style scoped lang="scss">
@use "./style.scss";
</style>
