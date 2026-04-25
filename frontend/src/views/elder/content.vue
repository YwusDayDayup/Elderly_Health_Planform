<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">资讯活动</p><h2>社区公告与健康知识</h2><span>查看社区新闻、政策解读、活动通知和健康建议。</span></div></div>
    <el-card shadow="never">
      <el-form :inline="true" class="toolbar-form">
        <el-form-item label="类型">
          <el-select v-model="query.type" clearable placeholder="全部" style="width: 180px">
            <el-option label="公告" value="NOTICE" />
            <el-option label="新闻" value="NEWS" />
            <el-option label="政策" value="POLICY" />
            <el-option label="健康知识" value="HEALTH" />
            <el-option label="活动" value="ACTIVITY" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchContents">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="contents">
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            {{ contentTypeLabel(row.contentType) }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="summary" label="摘要" min-width="260" />
        <el-table-column prop="publishedAt" label="发布时间" width="180" />
      </el-table>
      <div class="page-pagination-right">
        <el-pagination
          v-model:current-page="query.pageNo"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="fetchContents"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { listPublicContents, type ContentVO } from '@/api/care'

const contents = ref<ContentVO[]>([])
const total = ref(0)
const query = reactive({ type: '', pageNo: 1, pageSize: 10 })

const fetchContents = async () => {
  const res = await listPublicContents(query)
  if (res.success) {
    contents.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchContents()
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
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
