<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">系统监控</p>
        <h2>老人档案、统计概览与系统操作日志</h2>
        <span>查看老人档案基础信息，以及支付、短信、地图模拟调用结果和后台操作状态。</span>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="11">
        <div class="admin-zone" style="min-height: 320px">
          <div class="admin-zone__header">
            <el-icon class="zone-icon"><User /></el-icon>
            <span class="zone-title">老人档案</span>
            <span class="zone-meta">{{ elderProfiles.length }} 条</span>
          </div>
          <div class="admin-zone__body admin-zone__body--flush">
            <el-table :data="elderProfiles">
              <el-table-column prop="nickname" label="账号" width="110" />
              <el-table-column prop="realName" label="姓名" width="100" />
              <el-table-column prop="emergencyContactName" label="紧急联系人" width="110" />
              <el-table-column prop="address" label="住址" min-width="140" />
              <el-table-column label="操作" width="80">
                <template #default="{ row }">
                  <el-button link type="primary" @click="showElderDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :lg="13">
        <div class="admin-zone" style="min-height: 320px">
          <div class="admin-zone__header">
            <el-icon class="zone-icon"><DArrowRight /></el-icon>
            <span class="zone-title">外部接口调用日志</span>
            <span class="zone-meta">{{ logs.length }} 条</span>
          </div>
          <div class="admin-zone__body admin-zone__body--flush">
            <el-table :data="logs">
              <el-table-column label="提供方" width="100">
                <template #default="{ row }">
                  {{ providerLabel(row.providerType) }}
                </template>
              </el-table-column>
              <el-table-column label="动作" width="120">
                <template #default="{ row }">
                  {{ actionLabel(row.action) }}
                </template>
              </el-table-column>
              <el-table-column prop="bizType" label="业务类型" width="110" />
              <el-table-column label="结果" width="80">
                <template #default="{ row }">
                  <el-tag :type="row.success === 1 ? 'success' : 'danger'">{{ row.success === 1 ? '成功' : '失败' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="时间" width="160" />
              <el-table-column label="操作" width="80">
                <template #default="{ row }">
                  <el-button link type="primary" @click="showLogDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="admin-zone" style="margin-top: 16px">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><Document /></el-icon>
        <span class="zone-title">操作日志</span>
        <span class="zone-meta">{{ opLogs.length }} 条</span>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table :data="opLogs">
          <el-table-column prop="username" label="操作人" width="120" />
          <el-table-column prop="action" label="操作行为" min-width="200" />
          <el-table-column prop="method" label="请求方式" width="100" />
          <el-table-column prop="path" label="请求路径" min-width="160" />
          <el-table-column label="结果" width="80">
            <template #default="{ row }">
              <el-tag :type="row.success === 1 ? 'success' : 'danger'">{{ row.success === 1 ? '成功' : '失败' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="costMs" label="耗时(ms)" width="100" />
          <el-table-column prop="createTime" label="时间" width="170" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button link type="primary" @click="showOpDetail(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog v-model="elderDetailVisible" title="老人档案详情" width="720px">
      <el-descriptions v-if="currentElder" :column="2" border>
        <el-descriptions-item label="账号">{{ currentElder.nickname }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentElder.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentElder.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证">{{ currentElder.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ currentElder.emergencyContactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人电话">{{ currentElder.emergencyContactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="健康授权">{{ currentElder.familyHealthAuthorized === 1 ? '已授权' : '未授权' }}</el-descriptions-item>
        <el-descriptions-item label="定位授权">{{ currentElder.familyLocationAuthorized === 1 ? '已授权' : '未授权' }}</el-descriptions-item>
        <el-descriptions-item label="住址" :span="2">{{ currentElder.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="健康备注" :span="2">{{ currentElder.healthNotes || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="logDetailVisible" title="外部调用详情" width="760px">
      <el-descriptions v-if="currentLog" :column="2" border>
        <el-descriptions-item label="提供方">{{ providerLabel(currentLog.providerType) }}</el-descriptions-item>
        <el-descriptions-item label="动作">{{ actionLabel(currentLog.action) }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ currentLog.bizType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="业务编号">{{ currentLog.bizId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="执行结果">{{ currentLog.success === 1 ? '成功' : '失败' }}</el-descriptions-item>
        <el-descriptions-item label="调用时间">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求报文" :span="2">{{ currentLog.reqJson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="返回报文" :span="2">{{ currentLog.respJson || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="opDetailVisible" title="操作日志详情" width="760px">
      <el-descriptions v-if="currentOpLog" :column="2" border>
        <el-descriptions-item label="操作人">{{ currentOpLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作行为">{{ currentOpLog.action }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ currentOpLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求路径">{{ currentOpLog.path }}</el-descriptions-item>
        <el-descriptions-item label="执行结果">{{ currentOpLog.success === 1 ? '成功' : '失败' }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentOpLog.costMs || 0 }} ms</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ currentOpLog.errorMsg || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { User, DArrowRight, Document } from '@element-plus/icons-vue'
import { listAdminElderProfiles, listAdminExternalCallLogs, listAdminOpLogs, type ElderProfileVO, type ExternalCallLogVO, type OpLogVO } from '@/api/care'

const elderProfiles = ref<ElderProfileVO[]>([])
const logs = ref<ExternalCallLogVO[]>([])
const opLogs = ref<OpLogVO[]>([])
const elderDetailVisible = ref(false)
const logDetailVisible = ref(false)
const opDetailVisible = ref(false)
const currentElder = ref<ElderProfileVO | null>(null)
const currentLog = ref<ExternalCallLogVO | null>(null)
const currentOpLog = ref<OpLogVO | null>(null)

const fetchData = async () => {
  const [elderRes, logRes, opLogRes] = await Promise.all([
    listAdminElderProfiles({ pageNo: 1, pageSize: 100 }),
    listAdminExternalCallLogs({ pageNo: 1, pageSize: 100 }),
    listAdminOpLogs({ pageNo: 1, pageSize: 100 }),
  ])
  if (elderRes.success) elderProfiles.value = elderRes.data.list
  if (logRes.success) logs.value = logRes.data.list
  if (opLogRes.success) opLogs.value = opLogRes.data.list
}

const showElderDetail = (row: ElderProfileVO) => {
  currentElder.value = row
  elderDetailVisible.value = true
}

const showLogDetail = (row: ExternalCallLogVO) => {
  currentLog.value = row
  logDetailVisible.value = true
}

const showOpDetail = (row: OpLogVO) => {
  currentOpLog.value = row
  opDetailVisible.value = true
}

const providerLabel = (type: string) => {
  const map: Record<string, string> = {
    PAYMENT: '支付模拟',
    SMS: '短信模拟',
    MAP: '地图模拟',
  }
  return map[type] || type
}

const actionLabel = (action: string) => {
  const map: Record<string, string> = {
    PAY: '发起支付',
    REFUND: '退款处理',
    SEND_ALERT: '发送预警',
    REVERSE_GEOCODE: '地址解析',
    GEOFENCE_CHECK: '围栏判断',
  }
  return map[action] || action
}

onMounted(fetchData)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
