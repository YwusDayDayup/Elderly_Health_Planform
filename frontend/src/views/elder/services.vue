<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">服务大厅</p>
        <h2>慧养家园服务预约</h2>
        <span>在线浏览家政、跑腿、基础医护等项目并发起预约。</span>
      </div>
    </div>
    <el-card shadow="never">
      <el-form :inline="true" class="toolbar-form">
        <el-form-item label="服务分类">
          <el-select v-model="query.categoryId" clearable placeholder="全部" style="width: 180px">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="项目名称" clearable /></el-form-item>
        <el-form-item><el-button type="primary" @click="fetchProjects">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="projects">
        <el-table-column prop="name" label="项目" min-width="160" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="description" label="说明" min-width="220" />
        <el-table-column prop="price" label="价格" width="100" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="openOrder(row)">预约</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="page-pagination-right">
        <el-pagination
          v-model:current-page="query.pageNo"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="fetchProjects"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="预约服务" width="560px">
      <el-form :model="orderForm" label-width="90px">
        <el-form-item label="服务项目">{{ currentProject?.name }}</el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="orderForm.serviceTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务地址"><el-input v-model="orderForm.serviceAddress" /></el-form-item>
        <el-form-item label="联系人"><el-input v-model="orderForm.contactName" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="orderForm.contactPhone" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="orderForm.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createServiceOrder, listPublicServiceCategories, listPublicServiceProjects, type ServiceCategoryVO, type ServiceProjectVO } from '@/api/care'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const categories = ref<ServiceCategoryVO[]>([])
const projects = ref<ServiceProjectVO[]>([])
const total = ref(0)
const currentProject = ref<ServiceProjectVO | null>(null)
const dialogVisible = ref(false)
const query = reactive({ categoryId: undefined as number | undefined, keyword: '', pageNo: 1, pageSize: 10 })
const orderForm = reactive({
  serviceTime: '',
  serviceAddress: '',
  contactName: userStore.userInfo?.nickname || '',
  contactPhone: userStore.userInfo?.phone || '',
  remark: '',
})

const fetchCategories = async () => {
  const res = await listPublicServiceCategories()
  if (res.success) categories.value = res.data
}

const fetchProjects = async () => {
  const res = await listPublicServiceProjects(query)
  if (res.success) {
    projects.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchProjects()
}

const openOrder = (row: ServiceProjectVO) => {
  currentProject.value = row
  dialogVisible.value = true
}

const submitOrder = async () => {
  if (!currentProject.value) return
  await createServiceOrder({ serviceProjectId: currentProject.value.id, ...orderForm })
  ElMessage.success('预约已创建，请前往订单中心支付')
  dialogVisible.value = false
}

onMounted(() => {
  fetchCategories()
  fetchProjects()
})
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
