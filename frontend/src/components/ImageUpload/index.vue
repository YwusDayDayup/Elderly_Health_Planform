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
  // 因为 request.ts 拦截器已经处理了 success 判断
  // 如果进入这里说明已经是成功的响应，response 就是后端返回的完整 Result 对象
  if (response && response.success) {
    const resData = response.data
    const newImageUrl = resData.url || resData.avatarUrl
    imageUrl.value = newImageUrl
    emit('update:modelValue', newImageUrl)
    emit('onSuccess', response)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response?.message || '上传失败')
    emit('onError', response)
  }
}

const handleUploadError = (error: Error) => {
  ElMessage.error('上传失败，请重试！')
  emit('onError', error)
}

const httpRequest = async (options: UploadRequestOptions) => {
  try {
    const file = options.file as File
    // 统一 RESTful 上传：通过 bizType 区分头像等业务类型
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
