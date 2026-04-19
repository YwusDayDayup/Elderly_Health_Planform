<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">服务管理</p>
        <h2>服务分类、项目与服务人员配置</h2>
        <span>统一维护老人服务目录，并为项目指定默认服务人员。</span>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧：服务分类（导航区） -->
      <el-col :xs="24" :md="7" :lg="6">
        <div class="zone-category">
          <div class="zone-category__header">
            <el-icon><FolderOpened /></el-icon>
            <span>服务分类</span>
            <el-button type="primary" size="small" @click="openCategoryDialog()">新增</el-button>
          </div>
          <div class="zone-category__body">
            <el-scrollbar>
              <div
                v-for="item in categories"
                :key="item.id"
                class="category-item"
                :class="{ 'is-active': selectedCategoryId === item.id }"
                @click="selectCategory(item)"
              >
                <div class="category-item__info">
                  <span class="category-item__name">{{ item.name }}</span>
                  <el-tag size="small" :type="item.status === 1 ? 'success' : 'info'">{{ item.status === 1 ? '启用' : '停用' }}</el-tag>
                </div>
                <div class="category-item__actions">
                  <el-button link type="primary" size="small" @click.stop="openCategoryDialog(item)">编辑</el-button>
                  <el-button link type="primary" size="small" @click.stop="showCategoryDetail(item)">详情</el-button>
                  <el-button link type="danger" size="small" @click.stop="handleDeleteCategory(item.id)">删除</el-button>
                </div>
              </div>
              <el-empty v-if="categories.length === 0" description="暂无分类" />
            </el-scrollbar>
          </div>
        </div>
      </el-col>

      <!-- 右侧：服务项目（内容区） -->
      <el-col :xs="24" :md="17" :lg="18">
        <div class="zone-project">
          <div class="zone-project__header">
            <el-icon><Collection /></el-icon>
            <span>服务项目</span>
            <el-button type="primary" size="small" :disabled="!selectedCategoryId" @click="openProjectDialog()">新增项目</el-button>
          </div>
          <div class="zone-project__toolbar">
            <el-form :inline="true">
              <el-form-item label="关键字">
                <el-input v-model="projectQuery.keyword" placeholder="项目名称" clearable @keyup.enter="fetchProjects" />
              </el-form-item>
              <el-form-item label="分类">
                <el-select v-model="projectQuery.categoryId" clearable placeholder="全部" style="width: 140px">
                  <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="fetchProjects">查询</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div class="zone-project__body">
            <el-table :data="projects">
              <el-table-column prop="name" label="项目名称" min-width="160" />
              <el-table-column prop="categoryName" label="所属分类" width="120" />
              <el-table-column prop="price" label="价格" width="100" />
              <el-table-column prop="defaultStaffName" label="默认服务人员" width="130" />
              <el-table-column prop="serviceDurationMinutes" label="时长(分钟)" width="100" />
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="{ row }">
                  <el-button link type="primary" @click="showProjectDetail(row)">详情</el-button>
                  <el-button link type="primary" @click="openProjectDialog(row)">编辑</el-button>
                  <el-button link type="danger" @click="handleDeleteProject(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="categoryDialogVisible" :title="categoryForm.id ? '编辑分类' : '新增分类'" width="480px">
      <el-form :model="categoryForm" label-width="90px">
        <el-form-item label="分类名称"><el-input v-model="categoryForm.name" /></el-form-item>
        <el-form-item label="分类编码"><el-input v-model="categoryForm.code" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="categoryForm.sortNo" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="说明"><el-input v-model="categoryForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategory">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="projectDialogVisible" :title="projectForm.id ? '编辑项目' : '新增项目'" width="560px">
      <el-form :model="projectForm" label-width="100px">
        <el-form-item label="项目名称"><el-input v-model="projectForm.name" /></el-form-item>
        <el-form-item label="服务分类">
          <el-select v-model="projectForm.categoryId" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="projectForm.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="计费单位"><el-input v-model="projectForm.unit" /></el-form-item>
        <el-form-item label="时长(分钟)"><el-input-number v-model="projectForm.serviceDurationMinutes" :min="15" :step="15" /></el-form-item>
        <el-form-item label="默认人员">
          <el-select v-model="projectForm.defaultStaffUserId" clearable style="width: 100%">
            <el-option v-for="item in staffOptions" :key="item.id" :label="item.nickname || item.username" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="projectForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="项目说明"><el-input v-model="projectForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProject">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" :title="detailMode === 'category' ? '分类详情' : '项目详情'" width="620px">
      <el-descriptions v-if="detailMode === 'category' && currentCategory" :column="2" border>
        <el-descriptions-item label="分类名称">{{ currentCategory.name }}</el-descriptions-item>
        <el-descriptions-item label="分类编码">{{ currentCategory.code }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentCategory.status === 1 ? '启用' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ currentCategory.sortNo }}</el-descriptions-item>
        <el-descriptions-item label="说明" :span="2">{{ currentCategory.description || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions v-else-if="detailMode === 'project' && currentProject" :column="2" border>
        <el-descriptions-item label="项目名称">{{ currentProject.name }}</el-descriptions-item>
        <el-descriptions-item label="服务分类">{{ currentProject.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="价格">{{ currentProject.price }} / {{ currentProject.unit }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentProject.status === 1 ? '启用' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="默认人员">{{ currentProject.defaultStaffName || '待分配' }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ currentProject.serviceDurationMinutes }} 分钟</el-descriptions-item>
        <el-descriptions-item label="项目说明" :span="2">{{ currentProject.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  createAdminServiceCategory,
  createAdminServiceProject,
  deleteAdminServiceCategory,
  deleteAdminServiceProject,
  listAdminServiceCategories,
  listAdminServiceProjects,
  updateAdminServiceCategory,
  updateAdminServiceProject,
  type ServiceCategoryReq,
  type ServiceCategoryVO,
  type ServiceProjectReq,
  type ServiceProjectVO,
} from '@/api/care'
import { adminUserPage, type AdminUserVO } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderOpened, Collection } from '@element-plus/icons-vue'

const categories = ref<ServiceCategoryVO[]>([])
const projects = ref<ServiceProjectVO[]>([])
const staffOptions = ref<AdminUserVO[]>([])
const selectedCategoryId = ref<number | null>(null)

const projectQuery = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
})

const categoryDialogVisible = ref(false)
const detailVisible = ref(false)
const detailMode = ref<'category' | 'project'>('category')
const currentCategory = ref<ServiceCategoryVO | null>(null)
const currentProject = ref<ServiceProjectVO | null>(null)
const categoryForm = reactive<ServiceCategoryReq & { id?: number }>({
  name: '',
  code: '',
  description: '',
  sortNo: 0,
  status: 1,
})

const projectDialogVisible = ref(false)
const projectForm = reactive<ServiceProjectReq & { id?: number }>({
  categoryId: 0,
  name: '',
  description: '',
  price: 0,
  unit: '次',
  status: 1,
  defaultStaffUserId: undefined,
  serviceDurationMinutes: 60,
})

const fetchCategories = async () => {
  const res = await listAdminServiceCategories()
  if (res.success) categories.value = res.data
}

const fetchProjects = async () => {
  const res = await listAdminServiceProjects({ pageNo: 1, pageSize: 100, ...projectQuery })
  if (res.success) projects.value = res.data.list
}

const selectCategory = (item: ServiceCategoryVO) => {
  selectedCategoryId.value = item.id ?? null
  projectQuery.categoryId = item.id
  fetchProjects()
}

const fetchStaffUsers = async () => {
  const res = await adminUserPage({ pageNo: 1, pageSize: 100, role: 'STAFF' })
  if (res.success) staffOptions.value = res.data.list
}

const openCategoryDialog = (row?: ServiceCategoryVO) => {
  Object.assign(categoryForm, row || { name: '', code: '', description: '', sortNo: 0, status: 1, id: undefined })
  categoryDialogVisible.value = true
}

const openProjectDialog = (row?: ServiceProjectVO) => {
  Object.assign(projectForm, row || {
    categoryId: selectedCategoryId.value || categories.value[0]?.id || 0,
    name: '',
    description: '',
    price: 0,
    unit: '次',
    status: 1,
    defaultStaffUserId: undefined,
    serviceDurationMinutes: 60,
    id: undefined,
  })
  projectDialogVisible.value = true
}

const submitCategory = async () => {
  if (categoryForm.id) {
    await updateAdminServiceCategory(categoryForm.id, categoryForm)
  } else {
    await createAdminServiceCategory(categoryForm)
  }
  ElMessage.success('保存成功')
  categoryDialogVisible.value = false
  fetchCategories()
}

const handleDeleteCategory = (id: number) => {
  ElMessageBox.confirm('确认删除该服务分类吗？', '提示').then(async () => {
    await deleteAdminServiceCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  })
}

const submitProject = async () => {
  if (projectForm.id) {
    await updateAdminServiceProject(projectForm.id, projectForm)
  } else {
    await createAdminServiceProject(projectForm)
  }
  ElMessage.success('保存成功')
  projectDialogVisible.value = false
  fetchProjects()
}

const handleDeleteProject = (id: number) => {
  ElMessageBox.confirm('确认删除该服务项目吗？', '提示').then(async () => {
    await deleteAdminServiceProject(id)
    ElMessage.success('删除成功')
    fetchProjects()
  })
}

const showCategoryDetail = (row: ServiceCategoryVO) => {
  detailMode.value = 'category'
  currentCategory.value = row
  detailVisible.value = true
}

const showProjectDetail = (row: ServiceProjectVO) => {
  detailMode.value = 'project'
  currentProject.value = row
  detailVisible.value = true
}

onMounted(async () => {
  await fetchCategories()
  fetchProjects()
  fetchStaffUsers()
  if (categories.value.length > 0) {
    selectCategory(categories.value[0]!)
  }
})
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }

// 分类导航区
.zone-category {
  background: var(--app-surface);
  border: 1px solid var(--app-border-color);
  border-radius: var(--app-card-radius);
  height: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;

  &__header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 14px 16px;
    border-bottom: 1px solid var(--app-border-color);
    background: var(--app-surface-muted);
    color: var(--app-text-primary);
    font-weight: 700;
    font-size: 15px;

    .el-icon { color: var(--app-primary); }

    > .el-button { margin-left: auto; }
  }

  &__body {
    flex: 1;
    overflow: hidden;
    padding: 8px;

    .el-scrollbar { height: 100%; }
  }
}

// 分类列表项
.category-item {
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
  margin-bottom: 4px;

  &:hover {
    background: var(--app-hover-bg);
  }

  &.is-active {
    background: var(--app-primary-bg);
    border-color: var(--app-primary);

    .category-item__name { color: var(--app-primary); }
  }

  &__info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 6px;
  }

  &__name {
    font-size: 14px;
    font-weight: 600;
    color: var(--app-text-primary);
  }

  &__actions {
    display: flex;
    gap: 4px;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover &__actions { opacity: 1; }
}

// 项目内容区
.zone-project {
  background: var(--app-surface);
  border: 1px solid var(--app-border-color);
  border-radius: var(--app-card-radius);
  height: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;

  &__header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 14px 16px;
    border-bottom: 1px solid var(--app-border-color);
    background: var(--app-surface-muted);
    color: var(--app-text-primary);
    font-weight: 700;
    font-size: 15px;

    .el-icon { color: var(--app-primary); }

    > .el-button { margin-left: auto; }
  }

  &__toolbar {
    padding: 14px 16px;
    border-bottom: 1px solid var(--app-border-color);
  }

  &__body {
    flex: 1;
    padding: 16px;
    overflow: auto;
  }
}
</style>
