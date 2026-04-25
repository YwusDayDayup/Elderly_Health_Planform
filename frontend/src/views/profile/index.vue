<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="never" class="profile-card">
          <div class="avatar-section">
            <ImageUpload
              v-model="userStore.userInfo.avatarUrl"
              upload-url="/api/me/files"
              biz-type="avatar"
              :file-size="2"
              @on-success="handleAvatarSuccess"
            />
            <div class="user-meta">
              <h3>{{ userStore.userInfo.nickname || userStore.userInfo.username }}</h3>
              <p>{{ userStore.userInfo.username }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card shadow="never">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <UserInfo class="tab-content" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/user'
import ImageUpload from '@/components/ImageUpload/index.vue'
import UserInfo from './components/UserInfo.vue'

const userStore = useUserStore()
const activeTab = ref('info')

const handleAvatarSuccess = (res: any) => {
  // 上传成功后，更新 store 中的头像 URL
  if (res && res.data && res.data.url) {
    userStore.setUserInfo({ avatarUrl: res.data.url })
    // 立即保存到后端
    updateProfile({
      avatarUrl: res.data.url
    }).then((updateRes) => {
      if (updateRes.success) {
        // 重新获取用户信息以确保同步
        userStore.fetchMe()
      }
    })
  }
}
</script>

<style scoped lang="scss">
.profile-card {
  text-align: center;
  padding: 20px 0;

  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .user-meta {
    h3 {
      margin: 10px 0 5px;
      font-size: 18px;
    }
    p {
      color: var(--app-text-muted);
      font-size: 14px;
    }
  }
}

.tab-content {
  padding-top: 20px;
}
</style>
