<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">社区互动</p>
        <h2>论坛帖子与互动内容审核</h2>
        <span>查看演示生成的社区互动数据，支持详情查看与删除审核场景展示。</span>
      </div>
    </div>

    <div class="admin-zone">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><ChatDotSquare /></el-icon>
        <span class="zone-title">帖子列表</span>
        <span class="zone-meta">{{ posts.length }} 条帖子</span>
      </div>
      <div class="admin-zone__toolbar">
        <el-form :inline="true">
          <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="帖子标题" clearable /></el-form-item>
          <el-form-item label="分类">
            <el-select v-model="query.type" clearable placeholder="全部" style="width: 160px">
              <el-option label="论坛交流" value="论坛交流" />
              <el-option label="活动分享" value="活动分享" />
              <el-option label="健康打卡" value="健康打卡" />
              <el-option label="服务反馈" value="服务反馈" />
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
              <el-option label="已通过" value="APPROVED" />
              <el-option label="待审核" value="PENDING" />
              <el-option label="已驳回" value="REJECTED" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="fetchPosts">查询</el-button></el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table :data="posts">
          <el-table-column prop="authorName" label="发帖人" width="120" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column label="审核状态" width="100">
            <template #default="{ row }">
              <el-tag :type="auditStatusType(row.auditStatus)">{{ auditStatusLabel(row.auditStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="帖子状态" width="100">
            <template #default="{ row }">
              <el-tag :type="postStatusType(row.postStatus)">{{ postStatusLabel(row.postStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="互动数据" width="140">
            <template #default="{ row }">
              <span>{{ row.commentCount }}评 / {{ row.likeCount }}赞</span>
            </template>
          </el-table-column>
          <el-table-column prop="lastAction" label="最近操作" width="120" />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="page-pagination-right">
          <el-pagination
            v-model:current-page="query.pageNo"
            v-model:page-size="query.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="fetchPosts"
          />
        </div>
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="帖子详情" width="640px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="发帖人">{{ currentPost?.authorName }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentPost?.category }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">{{ currentPost?.auditStatus }}</el-descriptions-item>
        <el-descriptions-item label="帖子状态">{{ currentPost?.postStatus }}</el-descriptions-item>
        <el-descriptions-item label="最近操作">{{ currentPost?.lastAction }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentPost?.lastActionTime }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentPost?.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentPost?.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ChatDotSquare } from '@element-plus/icons-vue'
import { listAdminCommunityPosts, type CommunityPostVO } from '@/api/care'
import { ElMessage, ElMessageBox } from 'element-plus'

const posts = ref<CommunityPostVO[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentPost = ref<CommunityPostVO | null>(null)
const query = reactive({ 
  keyword: '', 
  type: '', 
  status: '',
  pageNo: 1,
  pageSize: 10,
})

const fetchPosts = async () => {
  const res = await listAdminCommunityPosts(query)
  if (res.success) {
    posts.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = () => {
  query.pageNo = 1
  fetchPosts()
}

const showDetail = (row: CommunityPostVO) => {
  currentPost.value = row
  detailVisible.value = true
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该社区帖子吗？', '提示').then(() => {
    posts.value = posts.value.filter(item => item.id !== id)
    ElMessage.success('帖子已删除')
  })
}

const auditStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    APPROVED: '已通过',
    PENDING: '待审核',
    REJECTED: '已驳回',
  }
  return map[status] || status
}

const auditStatusType = (status: string) => {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

const postStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    PUBLISHED: '已发布',
    HIDDEN: '已隐藏',
    DELETED: '已删除',
  }
  return map[status] || status
}

const postStatusType = (status: string) => {
  if (status === 'PUBLISHED') return 'success'
  if (status === 'DELETED') return 'danger'
  return 'info'
}

onMounted(fetchPosts)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
