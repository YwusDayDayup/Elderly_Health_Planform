<template>
  <div class="dashboard-container">
    <AdminDashboard v-if="isAdmin" />
    <ElderDashboard v-else-if="userStore.userInfo?.role === 'ELDER'" />
    <StaffDashboard v-else-if="userStore.userInfo?.role === 'STAFF'" />
    <FamilyDashboard v-else />
    <MsgDialog v-model="msgDialogVisible" />
    <ProfileDialog v-model="profileDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import AdminDashboard from './AdminDashboard.vue'
import ElderDashboard from './ElderDashboard.vue'
import StaffDashboard from './StaffDashboard.vue'
import FamilyDashboard from './FamilyDashboard.vue'
import MsgDialog from '@/views/msg/MsgDialog.vue'
import ProfileDialog from '@/views/profile/ProfileDialog.vue'

const userStore = useUserStore()
const isAdmin = computed(() => {
  return userStore.userInfo?.role === 'ADMIN'
})

const msgDialogVisible = ref(false)
const profileDialogVisible = ref(false)
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 0;
}
</style>
