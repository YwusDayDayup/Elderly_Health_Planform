<template>
  <el-popover
    placement="bottom-end"
    :width="340"
    trigger="click"
    @show="handleNoticeShow"
    popper-class="notice-popover"
  >
    <template #reference>
      <div class="notice-trigger common-flex-center topbar-action-btn">
        <el-badge :value="unreadCountVal" :hidden="unreadCountVal === 0" :max="99" class="badge-wrap">
          <div class="bell-wrapper">
            <Bell class="bell-icon" />
          </div>
        </el-badge>
      </div>
    </template>
    <div class="notice-container">
      <div class="notice-header">
        <div class="notice-title">
          <span class="title-dot"></span>
          <span>通知中心</span>
          <span class="notice-count">({{ unreadCountVal }})</span>
        </div>
        <el-button
          v-if="unreadCountVal > 0"
          type="primary"
          link
          size="small"
          class="mark-all-btn"
          @click="handleMarkAll"
        >
          全部已读
        </el-button>
      </div>
      <el-scrollbar max-height="360px" class="notice-scroll">
        <div v-loading="noticeLoading" class="notice-list">
          <div
            v-for="item in recentNotices"
            :key="item.id"
            class="notice-item"
            :class="{ unread: item.readFlag === 0 }"
            @click="goToDetail(item)"
          >
            <div class="notice-icon-wrap">
              <Bell class="notice-icon" />
            </div>
            <div class="notice-content">
              <div class="notice-item-title">{{ item.title }}</div>
              <div class="notice-item-time">{{ item.createTime }}</div>
            </div>
            <el-button
              v-if="item.readFlag === 0"
              type="primary"
              link
              size="small"
              class="read-btn"
              @click.stop="handleMarkRead(item)"
            >
              已读
            </el-button>
          </div>
          <el-empty
            v-if="recentNotices.length === 0"
            description="暂无新通知"
            :image-size="50"
            class="notice-empty"
          />
        </div>
      </el-scrollbar>
      <div class="notice-footer">
        <el-button type="primary" link @click="emit('open-msg')">查看全部消息</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { unreadCount, myMsgList, markRead, markReadAll } from '@/api/msg'

interface MsgItem {
  id: number
  title: string
  createTime: string
  readFlag: number
}

const emit = defineEmits<{
  'open-msg': []
}>()

const unreadCountVal = ref(0)
const noticeLoading = ref(false)
const recentNotices = ref<MsgItem[]>([])
let unreadTimer: number | null = null

const fetchUnread = async () => {
  try {
    const res = await unreadCount()
    if (res && res.success) {
      unreadCountVal.value = res.data.count
    }
  } catch (e) {}
}

const handleNoticeShow = async () => {
  noticeLoading.value = true
  try {
    const res = await myMsgList(0)
    if (res.success) {
      recentNotices.value = res.data.slice(0, 5)
    }
  } catch (e) {
  } finally {
    noticeLoading.value = false
  }
}

const handleMarkRead = async (item: MsgItem) => {
  try {
    const res = await markRead(item.id)
    if (res.success) {
      fetchUnread()
      handleNoticeShow()
    }
  } catch (e) {}
}

const handleMarkAll = async () => {
  try {
    const res = await markReadAll()
    if (res.success) {
      ElMessage.success('已全部标记为已读')
      fetchUnread()
      recentNotices.value = []
    }
  } catch (e) {}
}

const goToDetail = (item: MsgItem) => {
  handleMarkRead(item)
  emit('open-msg')
}

onMounted(() => {
  fetchUnread()
  unreadTimer = window.setInterval(fetchUnread, 60000)
})

onUnmounted(() => {
  if (unreadTimer !== null) {
    window.clearInterval(unreadTimer)
  }
})
</script>

<style scoped lang="scss">
.notice-trigger {
  .badge-wrap {
    :deep(.el-badge__content) {
      background: var(--app-primary);
      border: none;
    }
  }
}

.bell-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: var(--app-surface);
  border: 1px solid var(--app-border-color);
  transition: all 0.25s ease;
}

.bell-icon {
  color: var(--app-text-secondary);
  transition: color 0.2s ease;
}

.notice-trigger:hover {
  .bell-wrapper {
    border-color: var(--app-primary);
    background: var(--app-primary-bg);

    .bell-icon {
      color: var(--app-primary);
    }
  }
}

.notice-container {
  .notice-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 14px;
    border-bottom: 1px solid var(--app-border-color);
    margin-bottom: 12px;
  }

  .notice-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 700;
    font-size: 15px;
    color: var(--app-text-primary);
  }

  .title-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: var(--app-primary);
    box-shadow: 0 2px 8px rgba(232, 90, 60, 0.4);
  }

  .notice-count {
    font-weight: 400;
    color: var(--app-text-muted);
    font-size: 13px;
  }

  .mark-all-btn {
    font-size: 12px;
    color: var(--app-primary);

    &:hover {
      text-decoration: underline;
    }
  }
}

.notice-scroll {
  margin: 0 -12px;
  padding: 0 12px;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    background: var(--app-hover-bg);
  }

  &.unread {
    background: var(--app-primary-bg);

    &:hover {
      background: var(--app-primary-bg-hover);
    }
  }
}

.notice-icon-wrap {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: var(--app-surface-muted);
  flex-shrink: 0;

  .notice-icon {
    color: var(--app-text-secondary);
    font-size: 16px;
  }

  .unread & {
    background: rgba(232, 90, 60, 0.1);

    .notice-icon {
      color: var(--app-primary);
    }
  }
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-item-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--app-text-primary);
  margin-bottom: 4px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  .unread & {
    color: var(--app-primary);
  }
}

.notice-item-time {
  font-size: 11px;
  color: var(--app-text-muted);
}

.read-btn {
  font-size: 11px;
  color: var(--app-primary);
  flex-shrink: 0;
  padding: 4px 8px;
  align-self: center;
}

.notice-empty {
  padding: 24px 0;
}

.notice-footer {
  padding-top: 12px;
  margin-top: 8px;
  border-top: 1px solid var(--app-border-color);
  text-align: center;

  .el-button {
    color: var(--app-primary);
    font-weight: 600;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>

<style lang="scss">
// Popover global styling
.notice-popover {
  padding: 16px !important;
  border-radius: 20px !important;
  border: 1px solid var(--app-border-color) !important;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1) !important;
  background: var(--app-surface) !important;
}
</style>
