<template>
  <el-form label-width="80px" label-position="top">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="用户名">
          <el-input v-model="userStore.userInfo.username" disabled></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="昵称">
          <el-input v-model="userStore.userInfo.nickname"></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="邮箱">
          <el-input v-model="userStore.userInfo.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="手机号">
          <el-input v-model="userStore.userInfo.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="性别">
          <el-radio-group v-model="userStore.userInfo.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="userStore.userInfo.birthday"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择出生日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="地区">
          <el-input v-model="userStore.userInfo.region" placeholder="请输入地区"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="城市">
          <el-input v-model="userStore.userInfo.city" placeholder="请输入城市"></el-input>
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="个性签名">
      <el-input v-model="userStore.userInfo.signature" placeholder="请输入个性签名" maxlength="255" show-word-limit></el-input>
    </el-form-item>

    <el-form-item label="个人简介">
      <el-input
        v-model="userStore.userInfo.bio"
        type="textarea"
        :rows="3"
        maxlength="255"
        show-word-limit
        placeholder="请输入个人简介"
      ></el-input>
    </el-form-item>

    <div class="form-footer">
      <el-button type="primary" :loading="loading" @click="handleUpdate">保存修改</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)

const normalizeOptional = (value: unknown) => {
  if (value === null || value === undefined) {
    return null
  }
  if (typeof value === 'string' && value.trim() === '') {
    return null
  }
  return value
}

const handleUpdate = async () => {
  loading.value = true
  try {
    const res = await updateProfile({
      nickname: userStore.userInfo.nickname,
      avatarUrl: normalizeOptional(userStore.userInfo.avatarUrl) as string | null,
      email: normalizeOptional(userStore.userInfo.email) as string | null,
      phone: normalizeOptional(userStore.userInfo.phone) as string | null,
      gender: userStore.userInfo.gender,
      birthday: normalizeOptional(userStore.userInfo.birthday) as string | null,
      bio: normalizeOptional(userStore.userInfo.bio) as string | null,
      signature: normalizeOptional(userStore.userInfo.signature) as string | null,
      region: normalizeOptional(userStore.userInfo.region) as string | null,
      city: normalizeOptional(userStore.userInfo.city) as string | null
    })
    if (res.success) {
      ElMessage.success('个人信息更新成功')
    }
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.form-footer {
  margin-top: 20px;
  text-align: right;
}
</style>
