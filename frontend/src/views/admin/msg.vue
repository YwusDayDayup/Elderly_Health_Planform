<template>
  <div class="app-container">
    <div class="admin-zone">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><Bell /></el-icon>
        <span class="zone-title">通知管理</span>
        <el-button type="primary" size="small" @click="openSendDialog">发布通知</el-button>
      </div>
      <div class="admin-zone__toolbar">
        <el-form :inline="true">
          <el-form-item>
            <el-button type="primary" @click="handleSearch">刷新</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table v-loading="loading" :data="msgList">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="接收者" min-width="150">
            <template #default="{ row }">
              <template v-if="row.receiverId">
                <el-tag size="small">用户({{ row.receiverId }})</el-tag>
              </template>
              <template v-else>
                <el-tag type="success" size="small">全体用户</el-tag>
              </template>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              {{ msgTypeLabel(row.msgType) }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="通知标题" min-width="150" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.readFlag === 1 ? 'info' : 'warning'">
                {{ row.readFlag === 1 ? '已阅' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发送时间" width="180" />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="page-pagination-right">
          <el-pagination
            v-model:current-page="queryParams.pageNo"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="fetchMessages"
          />
        </div>
      </div>
    </div>

    <!-- 发布通知对话框 -->
    <el-dialog v-model="dialogVisible" title="发布通知" width="500px">
      <el-form :model="sendForm" label-width="80px">
        <el-form-item label="发送范围">
          <el-radio-group v-model="sendForm.scope">
            <el-radio label="all">全体用户</el-radio>
            <el-radio label="user">特定用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="接收者" v-if="sendForm.scope === 'user'">
          <el-select
            v-model="sendForm.receiverId"
            filterable
            remote
            reserve-keyword
            placeholder="输入昵称或用户名搜索"
            :remote-method="searchUsers"
            :loading="userSearchLoading"
            style="width: 100%"
          >
            <el-option
              v-for="user in searchUserOptions"
              :key="user.id"
              :label="`${user.nickname || user.username} (${user.username})`"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="通知类型">
          <el-select v-model="sendForm.msgType" placeholder="选择通知类型" style="width: 100%">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="普通通知" value="NOTICE" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="sendForm.title" placeholder="输入通知标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="sendForm.content" type="textarea" rows="4" placeholder="输入通知内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSend">确定发送</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="通知详情" width="640px">
      <el-descriptions v-if="currentMsg" :column="2" border>
        <el-descriptions-item label="通知类型">{{ msgTypeLabel(currentMsg.msgType) }}</el-descriptions-item>
        <el-descriptions-item label="阅读状态">{{ currentMsg.readFlag === 1 ? '已阅' : '未读' }}</el-descriptions-item>
        <el-descriptions-item label="接收者">
          {{ currentMsg.receiverId ? `用户(${currentMsg.receiverId})` : '全体用户' }}
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ currentMsg.createTime }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentMsg.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentMsg.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { adminMsgPage, adminBroadcast, adminSend, adminMsgDelete, type AdminMsgVO } from '@/api/msg'
import { adminUserPage } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const msgList = ref<AdminMsgVO[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentMsg = ref<AdminMsgVO | null>(null)

// 用户搜索相关
const userSearchLoading = ref(false)
const searchUserOptions = ref<any[]>([])

const searchUsers = async (query: string) => {
  if (query) {
    userSearchLoading.value = true
    try {
      const res = await adminUserPage({
        pageNo: 1,
        pageSize: 20,
        keyword: query
      })
      if (res.success) {
        searchUserOptions.value = res.data.list
      }
    } catch (e) {
    } finally {
      userSearchLoading.value = false
    }
  } else {
    searchUserOptions.value = []
  }
}

const queryParams = reactive({ pageNo: 1, pageSize: 10 })

const fetchMessages = async () => {
  loading.value = true
  try {
    const res = await adminMsgPage(queryParams)
    if (res.success) {
      msgList.value = res.data.list
      total.value = res.data.total
    }
  } catch (e) {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  fetchMessages()
}

// 发送消息
const dialogVisible = ref(false)
const submitLoading = ref(false)
const sendForm = reactive<any>({
  scope: 'all',
  receiverId: undefined,
  msgType: 'SYSTEM',
  title: '',
  content: ''
})

const openSendDialog = () => {
  dialogVisible.value = true
  Object.assign(sendForm, {
    scope: 'all',
    receiverId: undefined,
    msgType: 'SYSTEM',
    title: '',
    content: ''
  })
}

const handleSend = async () => {
  if (sendForm.scope === 'user' && !sendForm.receiverId) {
    return ElMessage.warning('请填写接收者ID')
  }
  if (!sendForm.title || !sendForm.content) {
    return ElMessage.warning('请填写完整内容')
  }
  submitLoading.value = true
  try {
    const res = sendForm.scope === 'all'
      ? await adminBroadcast({
          msgType: sendForm.msgType,
          title: sendForm.title,
          content: sendForm.content
        })
      : await adminSend({
          receiverId: sendForm.receiverId,
          msgType: sendForm.msgType,
          title: sendForm.title,
          content: sendForm.content
        })
    if (res.success) {
      ElMessage.success('发送成功')
      dialogVisible.value = false
      fetchMessages()
    }
  } catch (e) {
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除该消息吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await adminMsgDelete(id)
      ElMessage.success('删除成功')
      fetchMessages()
    } catch (e) {}
  })
}

const showDetail = (row: AdminMsgVO) => {
  currentMsg.value = row
  detailVisible.value = true
}

const msgTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    SYSTEM: '系统通知',
    NOTICE: '普通通知',
    ALERT: '预警通知',
  }
  return map[type] || type
}

onMounted(fetchMessages)
</script>
