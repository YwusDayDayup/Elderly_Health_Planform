<template>
  <el-dialog
    v-model="visible"
    title="我的消息"
    width="600px"
    destroy-on-close
    append-to-body
    class="msg-dialog"
  >
    <div class="msg-container">
      <div class="msg-header common-flex-between-center">
        <el-tabs v-model="activeTab" @tab-change="fetchMessages">
          <el-tab-pane label="全部消息" name="all" />
          <el-tab-pane label="未读消息" name="unread" />
        </el-tabs>
        <el-button type="primary" link @click="handleMarkAllRead">全部标记为已读</el-button>
      </div>

      <el-scrollbar max-height="450px">
        <div v-loading="loading">
          <div
            v-for="msg in msgList"
            :key="msg.id"
            class="common-msg-item common-msg-item--compact"
            :class="{ 'is-unread': msg.readFlag === 0 }"
            @click="handleShowDetail(msg)"
          >
            <div class="msg-content-wrap">
              <div class="common-msg-title">
                <span class="common-msg-unread-dot" v-if="msg.readFlag === 0"></span>
                {{ msg.title }}
              </div>
              <div class="common-msg-time common-msg-time--compact">{{ msg.createTime }}</div>
            </div>
            <el-icon><EpArrowRight /></el-icon>
          </div>
          <el-empty v-if="msgList.length === 0" description="暂无消息" />
        </div>
      </el-scrollbar>
    </div>

    <!-- 详情子对话框 -->
    <el-dialog v-model="detailVisible" title="消息详情" width="450px" append-to-body>
      <div v-if="currentMsg" class="detail-container">
        <div class="common-msg-detail-header">
          <span class="common-msg-detail-type">{{ currentMsg.msgType }}</span>
          <span>{{ currentMsg.createTime }}</span>
        </div>
        <h3 class="common-msg-detail-title common-msg-detail-title--sm">{{ currentMsg.title }}</h3>
        <el-divider />
        <div class="common-msg-detail-content">{{ currentMsg.content }}</div>
      </div>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { EpArrowRight } from 'vue-icons-plus/ep'
import { myMsgList, markRead, markReadAll, type MsgVO } from '@/api/msg'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits(['update:modelValue'])

const visible = ref(props.modelValue)
const activeTab = ref('all')
const loading = ref(false)
const msgList = ref<MsgVO[]>([])
const detailVisible = ref(false)
const currentMsg = ref<MsgVO | null>(null)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    fetchMessages()
  }
})

watch(() => visible.value, (val) => {
  emit('update:modelValue', val)
})

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
</script>

<style scoped lang="scss">
.msg-header {
  margin-bottom: 10px;
  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }
}
</style>
