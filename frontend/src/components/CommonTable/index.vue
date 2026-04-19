<template>
  <el-table
    v-loading="loading"
    :data="tableData"
    style="width: 100%"
    v-bind="$attrs"
  >
    <template v-for="column in columns" :key="column.prop">
      <el-table-column
        v-if="!column.slot"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :fixed="column.fixed"
      />
      <el-table-column v-else :prop="column.prop" :label="column.label" :width="column.width" :fixed="column.fixed">
        <template #default="scope">
          <slot :name="column.slot" :row="scope.row" :column="scope.column" :$index="scope.$index"></slot>
        </template>
      </el-table-column>
    </template>
  </el-table>

  <el-pagination
    v-if="showPagination"
    v-model:current-page="pagination.pageNo"
    v-model:page-size="pagination.pageSize"
    :page-sizes="[10, 20, 50, 100]"
    :small="false"
    :disabled="loading"
    :background="true"
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    style="margin-top: 20px; justify-content: flex-end;"
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElTable, ElTableColumn, ElPagination } from 'element-plus'

interface ColumnItem {
  prop: string
  label: string
  width?: string | number
  fixed?: boolean | 'left' | 'right'
  slot?: string // 用于自定义列内容的插槽名称
}

interface PageResult<T> {
  pageNo: number
  pageSize: number
  total: number
  records: T[]
}

const props = defineProps({
  columns: {
    type: Array as () => ColumnItem[],
    required: true,
  },
  requestApi: {
    type: Function as unknown as () => (params: any) => Promise<PageResult<any>>, // 异步请求函数
    required: true,
  },
  queryForm: {
    type: Object,
    default: () => ({}),
  },
  showPagination: {
    type: Boolean,
    default: true,
  },
})

const tableData = ref<any[]>([])
const loading = ref(false)
const pagination = reactive({
  pageNo: 1,
  pageSize: 10,
  total: 0,
})

const fetchTableData = async () => {
  loading.value = true
  try {
    const params = {
      pageNo: pagination.pageNo,
      pageSize: pagination.pageSize,
      ...props.queryForm,
    }
    const res = await props.requestApi(params)
    tableData.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('Failed to fetch table data:', error)
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchTableData()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNo = val
  fetchTableData()
}

watch(() => props.queryForm, () => {
  pagination.pageNo = 1 // 查询条件变化时重置页码
  fetchTableData()
}, { deep: true })

onMounted(() => {
  fetchTableData()
})

defineExpose({
  fetchTableData,
})
</script>

<style scoped lang="scss">
@use "./style.scss";
</style>
