<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="page-card-header">
          <span>我的消息</span>
          <el-button type="primary" link @click="handleMarkAllRead">全部标记为已读</el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="fetchMessages">
        <el-tab-pane label="全部消息" name="all" />
        <el-tab-pane label="未读消息" name="unread" />
      </el-tabs>

      <div v-loading="loading">
        <div v-for="msg in msgList" :key="msg.id" class="common-msg-item" :class="{ 'is-unread': msg.readFlag === 0 }" @click="handleShowDetail(msg)">
          <div class="msg-content-wrap">
            <div class="common-msg-title">
              <span class="common-msg-unread-dot" v-if="msg.readFlag === 0"></span>
              {{ msg.title }}
            </div>
            <div class="common-msg-time">{{ msg.createTime }}</div>
          </div>
          <el-icon><ArrowRight /></el-icon>
        </div>
        <el-empty v-if="msgList.length === 0" description="暂无消息" />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="消息详情" width="500px">
      <div v-if="currentMsg" class="detail-container">
        <div class="common-msg-detail-header">
          <span class="common-msg-detail-type">{{ currentMsg.msgType }}</span>
          <span>{{ currentMsg.createTime }}</span>
        </div>
        <h3 class="common-msg-detail-title">{{ currentMsg.title }}</h3>
        <el-divider />
        <div class="common-msg-detail-content">{{ currentMsg.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { myMsgList, markRead, markReadAll, type MsgVO } from '@/api/msg'
import { ElMessage } from 'element-plus'

const activeTab = ref('all')
const loading = ref(false)
const msgList = ref<MsgVO[]>([])
const detailVisible = ref(false)
const currentMsg = ref<MsgVO | null>(null)

const fetchMessages = async () => {
  loading.value = true
  try {
    const readFlag = activeTab.value === 'unread' ? 0 : undefined
    const res = await myMsgList(readFlag)
    if (res.success) {
      msgList.value = res.data
    }
  } catch (e) {
    ElMessage.error('加载消息失败')
  } finally {
    loading.value = false
  }
}

const handleShowDetail = async (msg: MsgVO) => {
  currentMsg.value = msg
  detailVisible.value = true
  if (msg.readFlag === 0) {
    try {
      await markRead(msg.id)
      msg.readFlag = 1
    } catch (e) {}
  }
}

const handleMarkAllRead = async () => {
  try {
    await markReadAll()
    ElMessage.success('已全部标记为已读')
    fetchMessages()
  } catch (e) {}
}

onMounted(fetchMessages)
</script>
