<template>
  <div class="app-container">
    <div class="page-head">
      <p class="page-kicker">用户管理</p>
      <h2>平台账号与权限维护</h2>
      <span>统一处理账号信息、角色状态、创建与编辑操作。</span>
    </div>

    <div class="admin-zone">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><User /></el-icon>
        <span class="zone-title">用户列表</span>
        <el-button type="primary" size="small" @click="handleCreate">新增用户</el-button>
      </div>
      <div class="admin-zone__toolbar">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="关键字">
            <el-input v-model="queryParams.keyword" placeholder="用户名/昵称" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="queryParams.role" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="老人用户" value="ELDER" />
              <el-option label="服务人员" value="STAFF" />
              <el-option label="家属用户" value="FAMILY" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table v-loading="loading" :data="userList">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="头像" width="90">
            <template #default="{ row }">
              <el-avatar :size="36" :src="row.avatarUrl">{{ row.username.charAt(0) }}</el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="nickname" label="昵称" />
          <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column label="性别" width="80">
            <template #default="{ row }">
              <span>{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="region" label="地区" width="110" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
              <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="page-pagination-right">
          <el-pagination
            v-model:current-page="queryParams.pageNo"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="fetchUsers"
          />
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期">
          <el-date-picker
            v-model="editForm.birthday"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择出生日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="editForm.region" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="editForm.city" />
        </el-form-item>
        <el-form-item label="签名">
          <el-input v-model="editForm.signature" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="老人用户" value="ELDER" />
            <el-option label="服务人员" value="STAFF" />
            <el-option label="家属用户" value="FAMILY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="账号详情" width="680px">
      <el-descriptions v-if="detailForm" :column="2" border>
        <el-descriptions-item label="用户名">{{ detailForm.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel(detailForm.role) }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailForm.nickname || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detailForm.status === 1 ? '启用' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailForm.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailForm.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ genderLabel(detailForm.gender) }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailForm.birthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{ detailForm.region || '-' }}</el-descriptions-item>
        <el-descriptions-item label="城市">{{ detailForm.city || '-' }}</el-descriptions-item>
        <el-descriptions-item label="签名" :span="2">{{ detailForm.signature || '-' }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ detailForm.bio || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增对话框 -->
    <el-dialog v-model="createVisible" title="新增用户" width="500px">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="createForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="createForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="createForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="createForm.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthday">
          <el-date-picker
            v-model="createForm.birthday"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择出生日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-input v-model="createForm.region" placeholder="请输入地区" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="createForm.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="签名" prop="signature">
          <el-input v-model="createForm.signature" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="简介" prop="bio">
          <el-input v-model="createForm.bio" type="textarea" :rows="3" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="createForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="createForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="老人用户" value="ELDER" />
            <el-option label="服务人员" value="STAFF" />
            <el-option label="家属用户" value="FAMILY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { User } from '@element-plus/icons-vue'
import {
  adminUserPage,
  adminUserDetail,
  adminUserUpdate,
  adminUserDelete,
  adminUserCreate,
  type AdminUserVO,
  type AdminUserDetailVO,
  type AdminUpdateReq,
  type AdminCreateReq
} from '@/api/user'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'

const loading = ref(false)
const userList = ref<AdminUserVO[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detailForm = ref<AdminUserDetailVO | null>(null)

const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  keyword: '',
  status: undefined as number | undefined,
  role: undefined as string | undefined
})

const normalizeOptional = (value: unknown) => {
  if (value === null || value === undefined) {
    return null
  }
  if (typeof value === 'string' && value.trim() === '') {
    return null
  }
  return value
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await adminUserPage(queryParams)
    if (res.success) {
      userList.value = res.data.list
      total.value = res.data.total
    }
  } catch (err: any) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNo = 1
  fetchUsers()
}

const resetSearch = () => {
  queryParams.keyword = ''
  queryParams.status = undefined
  queryParams.role = undefined
  handleSearch()
}

const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  fetchUsers()
}

// 编辑逻辑
const editVisible = ref(false)
const submitLoading = ref(false)
const editForm = reactive<AdminUpdateReq & { id: number; username?: string }>({
  id: 0,
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  birthday: null,
  bio: '',
  signature: '',
  region: '',
  city: '',
  status: 1,
  role: 'ELDER',
  username: ''
})

const handleEdit = async (row: AdminUserVO) => {
  try {
    const res = await adminUserDetail(row.id)
    if (res.success) {
      Object.assign(editForm, res.data)
      editVisible.value = true
    }
  } catch (err: any) {
    ElMessage.error('获取详情失败')
  }
}

const handleDetail = async (row: AdminUserVO) => {
  try {
    const res = await adminUserDetail(row.id)
    if (res.success) {
      detailForm.value = res.data
      detailVisible.value = true
    }
  } catch (err: any) {
    ElMessage.error('获取详情失败')
  }
}

const submitEdit = async () => {
  submitLoading.value = true
  try {
    const { id, ...data } = editForm
    const payload: AdminUpdateReq = {
      ...data,
      avatarUrl: normalizeOptional(data.avatarUrl) as string | null,
      email: normalizeOptional(data.email) as string | null,
      phone: normalizeOptional(data.phone) as string | null,
      birthday: normalizeOptional(data.birthday) as string | null,
      bio: normalizeOptional(data.bio) as string | null,
      signature: normalizeOptional(data.signature) as string | null,
      region: normalizeOptional(data.region) as string | null,
      city: normalizeOptional(data.city) as string | null
    }
    const res = await adminUserUpdate(id, payload)
    if (res.success) {
      ElMessage.success('更新成功')
      editVisible.value = false
      fetchUsers()
    }
  } catch (err: any) {
    ElMessage.error('更新失败')
  } finally {
    submitLoading.value = false
  }
}

// 新增逻辑
const createVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<AdminCreateReq>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  gender: 0,
  birthday: null,
  bio: '',
  signature: '',
  region: '',
  city: '',
  status: 1,
  role: 'ELDER'
})

const createRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const handleCreate = () => {
  Object.assign(createForm, {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    gender: 0,
    birthday: null,
    bio: '',
    signature: '',
    region: '',
    city: '',
    status: 1,
    role: 'ELDER'
  })
  createVisible.value = true
}

const submitCreate = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const payload: AdminCreateReq = {
          ...createForm,
          email: normalizeOptional(createForm.email) as string | null,
          phone: normalizeOptional(createForm.phone) as string | null,
          birthday: normalizeOptional(createForm.birthday) as string | null,
          bio: normalizeOptional(createForm.bio) as string | null,
          signature: normalizeOptional(createForm.signature) as string | null,
          region: normalizeOptional(createForm.region) as string | null,
          city: normalizeOptional(createForm.city) as string | null
        }
        const res = await adminUserCreate(payload)
        if (res.success) {
          ElMessage.success('创建成功')
          createVisible.value = false
          fetchUsers()
        }
      } catch (err: any) {
        ElMessage.error(err.message || '创建失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await adminUserDelete(id)
      if (res.success) {
        ElMessage.success('删除成功')
        fetchUsers()
      }
    } catch (err: any) {
      ElMessage.error('删除失败')
    }
  })
}

const roleLabel = (role?: string) => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    ELDER: '老人用户',
    STAFF: '服务人员',
    FAMILY: '家属用户',
  }
  return role ? map[role] || role : '-'
}

const genderLabel = (gender?: number | null) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '未知'
}

onMounted(fetchUsers)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss
</style>
