<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">位置查看</p><h2>老人最近位置与安全区域</h2><span>在授权前提下查看最近位置和是否越界。</span></div></div>
    <el-card shadow="never">
      <el-form :inline="true" class="toolbar-form">
        <el-form-item label="选择绑定老人">
          <el-select v-model="targetUserId" placeholder="请选择老人" style="width: 220px" @change="fetchLocation">
            <el-option v-for="item in activeBindings" :key="item.elderUserId" :label="item.elderName" :value="item.elderUserId" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-descriptions v-if="latestLocation" :column="2" border>
        <el-descriptions-item label="最近地址">{{ latestLocation.address }}</el-descriptions-item>
        <el-descriptions-item label="围栏状态">{{ latestLocation.withinZone === 1 ? '区域内' : '已越界' }}</el-descriptions-item>
        <el-descriptions-item label="坐标">{{ latestLocation.latitude }}, {{ latestLocation.longitude }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ latestLocation.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions v-if="safeZone" :column="2" border style="margin-top: 16px">
        <el-descriptions-item label="安全区域">{{ safeZone.address }}</el-descriptions-item>
        <el-descriptions-item label="半径">{{ safeZone.radiusMeters }} 米</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getLatestLocation, getSafeZone, myFamilyBindings, type FamilyBindingVO, type LocationVO, type SafeZoneVO } from '@/api/care'

const bindings = ref<FamilyBindingVO[]>([])
const targetUserId = ref<number>()
const latestLocation = ref<LocationVO | null>(null)
const safeZone = ref<SafeZoneVO | null>(null)

const activeBindings = computed(() => bindings.value.filter(item => item.status === 'ACTIVE'))

const fetchBindings = async () => {
  const res = await myFamilyBindings({ pageNo: 1, pageSize: 100 })
  if (res.success) {
    bindings.value = res.data.list
    if (!targetUserId.value && activeBindings.value.length) {
      targetUserId.value = activeBindings.value[0]!.elderUserId
      fetchLocation()
    }
  }
}

const fetchLocation = async () => {
  if (!targetUserId.value) return
  const [locRes, zoneRes] = await Promise.all([getLatestLocation(targetUserId.value), getSafeZone(targetUserId.value)])
  if (locRes.success) latestLocation.value = locRes.data
  if (zoneRes.success) safeZone.value = zoneRes.data
}

onMounted(fetchBindings)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
