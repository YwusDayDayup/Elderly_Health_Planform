<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">内容管理</p>
        <h2>社区公告、政策资讯与健康知识维护</h2>
        <span>发布面向老人、家属和服务人员的内容入口。</span>
      </div>
    </div>

    <div class="admin-zone">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><Document /></el-icon>
        <span class="zone-title">内容列表</span>
        <el-button type="primary" size="small" @click="openDialog()">新增内容</el-button>
      </div>
      <div class="admin-zone__toolbar">
        <el-form :inline="true">
          <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="标题" clearable /></el-form-item>
          <el-form-item label="类型">
            <el-select v-model="query.type" clearable placeholder="全部" style="width: 160px">
              <el-option label="公告" value="NOTICE" />
              <el-option label="新闻" value="NEWS" />
              <el-option label="政策" value="POLICY" />
              <el-option label="健康知识" value="HEALTH" />
              <el-option label="活动" value="ACTIVITY" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="fetchContents">查询</el-button></el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table :data="contents">
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              {{ contentTypeLabel(row.contentType) }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '已停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="publishedAt" label="发布时间" width="180" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
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
            @current-change="fetchContents"
          />
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑内容' : '新增内容'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="内容类型">
          <el-select v-model="form.contentType" style="width: 100%">
            <el-option label="公告" value="NOTICE" />
            <el-option label="新闻" value="NEWS" />
            <el-option label="政策" value="POLICY" />
            <el-option label="健康知识" value="HEALTH" />
            <el-option label="活动" value="ACTIVITY" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="正文"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitContent">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="内容详情" width="720px">
      <el-descriptions v-if="currentContent" :column="2" border>
        <el-descriptions-item label="内容类型">{{ contentTypeLabel(currentContent.contentType) }}</el-descriptions-item>
        <el-descriptions-item label="发布状态">{{ currentContent.status === 1 ? '已发布' : '已停用' }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentContent.title }}</el-descriptions-item>
        <el-descriptions-item label="作者">{{ currentContent.authorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ currentContent.publishedAt || '-' }}</el-descriptions-item>
        <el-descriptions-item label="摘要" :span="2">{{ currentContent.summary || '-' }}</el-descriptions-item>
        <el-descriptions-item label="正文" :span="2">{{ currentContent.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Document } from '@element-plus/icons-vue'
import {
  createAdminContent,
  deleteAdminContent,
  listAdminContents,
  updateAdminContent,
  type ContentReq,
  type ContentVO,
} from '@/api/care'
import { ElMessage, ElMessageBox } from 'element-plus'

const contents = ref<ContentVO[]>([])
const total = ref(0)
const query = reactive({ keyword: '', type: '', pageNo: 1, pageSize: 10 })
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentContent = ref<ContentVO | null>(null)
const form = reactive<ContentReq & { id?: number }>({
  contentType: 'NOTICE',
  title: '',
  summary: '',
  content: '',
  status: 1,
})

const fetchContents = async () => {
  const res = await listAdminContents(query)
  if (res.success) {
    contents.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchContents()
}

const openDialog = (row?: ContentVO) => {
  Object.assign(form, row || { contentType: 'NOTICE', title: '', summary: '', content: '', status: 1, id: undefined })
  dialogVisible.value = true
}

const submitContent = async () => {
  if (form.id) {
    await updateAdminContent(form.id, form)
  } else {
    await createAdminContent(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchContents()
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该内容吗？', '提示').then(async () => {
    await deleteAdminContent(id)
    ElMessage.success('删除成功')
    fetchContents()
  })
}

const showDetail = (row: ContentVO) => {
  currentContent.value = row
  detailVisible.value = true
}

const contentTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    NOTICE: '公告',
    NEWS: '新闻',
    POLICY: '政策',
    HEALTH: '健康知识',
    ACTIVITY: '活动',
  }
  return map[type] || type
}

onMounted(fetchContents)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
