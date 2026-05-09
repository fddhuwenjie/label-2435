<template>
  <div class="admin-reservations">
    <div class="page-title-section">
      <h2>预订管理</h2>
      <p>管理所有客房预订记录</p>
    </div>

    <div class="search-card">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form-left">
          <el-form-item label="用户ID"><el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="待入住" :value="0" /><el-option label="已取消" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已违约" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="开始日期">
            <el-date-picker v-model="searchForm.startTime" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" :teleported="true" />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker v-model="searchForm.endTime" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" :teleported="true" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="search-form-right">
          <el-button type="success" @click="handleExport">导出Excel</el-button>
        </div>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="预订ID" width="200" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="roomTypeName" label="房间类型" width="100" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="checkOutDate" label="退房日期" width="120" />
        <el-table-column prop="reserveDays" label="天数" width="70" />
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }"><span class="price">¥{{ row.totalPrice }}</span></template>
        </el-table-column>
        <el-table-column prop="deposit" label="定金" width="100">
          <template #default="{ row }"><span class="price">¥{{ row.deposit }}</span></template>
        </el-table-column>
        <el-table-column prop="depositStatusName" label="定金状态" width="100">
          <template #default="{ row }"><el-tag :type="getDepositStatusType(row.depositStatus)" size="small">{{ row.depositStatusName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="statusName" label="预订状态" width="100">
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)" size="small">{{ row.statusName }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="reserveTime" label="预订时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div v-if="row.status === 0" class="action-buttons">
              <el-button type="primary" link @click="handleCheckIn(row)">办理入住</el-button>
              <el-button type="warning" link @click="handleBreach(row)">标记违约</el-button>
              <el-button type="danger" link @click="handleForceCancel(row)">强制取消</el-button>
            </div>
            <span v-else class="no-action">-</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="cancelDialogVisible" title="强制取消预订" width="450px">
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="预订ID"><span class="form-value">{{ cancelForm.id }}</span></el-form-item>
        <el-form-item label="房间号"><span class="form-value">{{ cancelForm.roomNumber }}</span></el-form-item>
        <el-form-item label="取消原因" required>
          <el-input v-model="cancelForm.remark" type="textarea" :rows="3" placeholder="请输入取消原因（必填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="cancelling" @click="handleConfirmCancel">确认取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReservations, checkIn, markBreach, forceCancelReservation, exportReservations } from '../../api/admin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const searchForm = reactive({ userId: '', status: null, startTime: '', endTime: '' })
const cancelDialogVisible = ref(false)
const cancelForm = reactive({ id: null, roomNumber: '', remark: '' })
const cancelling = ref(false)

const getStatusType = (status) => ({ 0: 'warning', 1: 'info', 2: 'success', 3: 'danger' }[status] || 'info')
const getDepositStatusType = (status) => ({ 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[status] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size }
    if (searchForm.userId) params.userId = searchForm.userId
    if (searchForm.status !== null) params.status = searchForm.status
    if (searchForm.startTime) params.startTime = searchForm.startTime
    if (searchForm.endTime) params.endTime = searchForm.endTime
    const res = await getReservations(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { 
  searchForm.userId = ''
  searchForm.status = null
  searchForm.startTime = ''
  searchForm.endTime = ''
  handleSearch() 
}

const handleCheckIn = async (row) => {
  await ElMessageBox.confirm('确认办理入住？', '办理入住', { type: 'warning' })
  await checkIn(row.id)
  ElMessage.success('入住办理成功')
  loadData()
}

const handleBreach = async (row) => {
  await ElMessageBox.confirm('确认标记为违约？定金将被没收。', '标记违约', { type: 'warning' })
  await markBreach(row.id)
  ElMessage.success('已标记为违约')
  loadData()
}

const handleForceCancel = (row) => {
  cancelForm.id = row.id
  cancelForm.roomNumber = row.roomNumber
  cancelForm.remark = ''
  cancelDialogVisible.value = true
}

const handleConfirmCancel = async () => {
  if (!cancelForm.remark.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  cancelling.value = true
  try {
    await forceCancelReservation(cancelForm.id, cancelForm.remark)
    ElMessage.success('取消成功')
    cancelDialogVisible.value = false
    loadData()
  } finally { cancelling.value = false }
}

const handleExport = async () => {
  if (!searchForm.startTime || !searchForm.endTime) {
    ElMessage.warning('请先选择时间范围再导出')
    return
  }
  if (searchForm.startTime > searchForm.endTime) {
    ElMessage.warning('开始日期不能晚于结束日期')
    return
  }
  try {
    const res = await exportReservations({ startTime: searchForm.startTime, endTime: searchForm.endTime })
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `预订清单_${searchForm.startTime}_${searchForm.endTime}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$text: #2c2c2c;
$text-secondary: #6b6b6b;

.admin-reservations { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }

.page-title-section {
  margin-bottom: 24px;
  h2 {  font-size: 24px; color: $text; margin-bottom: 4px; }
  p { font-size: 13px; color: $text-secondary; }
}

.search-card, .table-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.search-card :deep(.el-form-item) { margin-bottom: 12px; margin-right: 16px; }
.search-form-wrapper { display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 12px; }
.search-form-left { flex: 1; min-width: 300px; }
.search-form-right { flex-shrink: 0; }
@media (max-width: 900px) {
  .search-form-wrapper { flex-direction: column; align-items: flex-start; }
  .search-form-right { width: 100%; }
}
.price { color: $primary; }
.form-value { color: $text; font-weight: 500; }
.pagination-container { margin-top: 24px; display: flex; justify-content: flex-end; }
.action-buttons { display: flex; gap: 8px; flex-wrap: nowrap; }
.no-action { color: #ccc; }
</style>
