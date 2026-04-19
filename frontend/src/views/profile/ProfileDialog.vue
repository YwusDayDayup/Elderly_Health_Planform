<template>
  <el-dialog
    v-model="visible"
    title="个人中心"
    width="800px"
    destroy-on-close
    append-to-body
  >
    <div class="profile-container">
      <el-row :gutter="40">
        <el-col :span="6">
          <div class="avatar-section">
            <ImageUpload
              v-model="userStore.userInfo.avatarUrl"
              upload-url="/api/me/files"
              biz-type="avatar"
              :file-size="2"
              @on-success="handleAvatarSuccess"
            />
            <div class="user-info">
              <div class="nickname">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</div>
              <div class="username">@{{ userStore.userInfo.username }}</div>
            </div>
            <div class="roles">
              <el-tag v-for="role in userStore.userInfo.roles" :key="role" size="small" type="info">
                {{ formatRole(role) }}
              </el-tag>
            </div>
          </div>
        </el-col>
        <el-col :span="18">
          <div class="form-section">
            <div class="section-title">基本信息</div>
            <UserInfo />
          </div>
        </el-col>
      </el-row>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import ImageUpload from '@/components/ImageUpload/index.vue'
import UserInfo from './components/UserInfo.vue'
import { formatRole } from '@/utils/role'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const userStore = useUserStore()
const visible = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(() => visible.value, (val) => {
  emit('update:modelValue', val)
})

const handleAvatarSuccess = (res: any) => {
  userStore.setUserInfo({ avatarUrl: res.data.url })
}

</script>

<style scoped lang="scss">
.profile-container {
  padding: 10px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px 0;

  .user-info {
    text-align: center;
    .nickname {
      font-size: 18px;
      font-weight: 600;
      color: var(--app-text-primary);
    }
    .username {
      font-size: 13px;
      color: var(--app-text-muted);
      margin-top: 4px;
    }
  }

  .roles {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    justify-content: center;
  }
}

.form-section {
  .section-title {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--app-border-color);
    color: var(--app-text-primary);
  }
}
</style>
