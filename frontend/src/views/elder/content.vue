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
        <el-table-column prop="contentType" label="类型" width="100" />
        <el-table-column prop="title" label="标题" min-width="240" />
        <el-table-column prop="summary" label="摘要" min-width="260" />
        <el-table-column prop="publishedAt" label="发布时间" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { listPublicContents, type ContentVO } from '@/api/care'

const contents = ref<ContentVO[]>([])
const query = reactive({ type: '' })

const fetchContents = async () => {
  const res = await listPublicContents({ pageNo: 1, pageSize: 100, ...query })
  if (res.success) contents.value = res.data.list
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
