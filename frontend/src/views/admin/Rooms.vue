<template>
  <div class="admin-rooms">
    <div class="page-title-section">
      <h2>房间管理</h2>
      <p>管理酒店所有房间信息</p>
    </div>

    <div class="search-card">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form-left">
          <el-form-item label="房间类型">
            <el-select v-model="searchForm.roomType" placeholder="全部" clearable>
              <el-option label="单人间" :value="1" />
              <el-option label="双人间" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="全部" clearable>
              <el-option label="空闲" :value="0" />
              <el-option label="已预订" :value="1" />
              <el-option label="已入住" :value="2" />
              <el-option label="维护" :value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
          </el-form-item>
        </el-form>
        <div class="search-form-right">
          <el-button type="warning" @click="showBatchDialog">批量设置季节系数</el-button>
          <el-button type="info" @click="showStatistics">查看统计</el-button>
        </div>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column prop="roomTypeName" label="房间类型"  />
        <el-table-column prop="basePrice" label="基础价格" >
          <template #default="{ row }"><span class="price">¥{{ row.basePrice }}</span></template>
        </el-table-column>
        <el-table-column prop="seasonCoefficient" label="季节系数"  />
        <el-table-column prop="statusName" label="状态" >
          <template #default="{ row }"><el-tag :type="getStatusType(row.status)" size="small">{{ row.statusName }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }"><el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button></template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="editDialogVisible" title="编辑房间" width="450px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="房间号"><span class="form-value">{{ editForm.roomNumber }}</span></el-form-item>
        <el-form-item label="基础价格"><el-input-number v-model="editForm.basePrice" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="季节系数"><el-input-number v-model="editForm.seasonCoefficient" :min="0" :max="10" :precision="2" :step="0.1" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="空闲" :value="0" /><el-option label="已预订" :value="1" /><el-option label="已入住" :value="2" /><el-option label="维护" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" title="批量设置季节系数" width="450px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="房间类型">
          <el-select v-model="batchForm.roomType" placeholder="全部房间" clearable>
            <el-option label="单人间" :value="1" /><el-option label="双人间" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="季节系数"><el-input-number v-model="batchForm.coefficient" :min="0" :max="10" :precision="2" :step="0.1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBatchUpdate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statisticsDialogVisible" title="房间统计" width="600px">
      <el-table :data="statisticsData" stripe>
        <el-table-column prop="roomType" label="房间类型"><template #default="{ row }">{{ row.roomType === 1 ? '单人间' : '双人间' }}</template></el-table-column>
        <el-table-column prop="status" label="状态"><template #default="{ row }">{{ getStatusName(row.status) }}</template></el-table-column>
        <el-table-column prop="count" label="数量" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminRooms, updateRoom, batchUpdateSeasonCoefficient, getRoomStatistics } from '../../api/admin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const searchForm = reactive({ roomType: null, status: null })
const editDialogVisible = ref(false)
const editForm = reactive({ id: null, roomNumber: '', basePrice: 0, seasonCoefficient: 1, status: 0 })
const submitting = ref(false)
const batchDialogVisible = ref(false)
const batchForm = reactive({ roomType: null, coefficient: 1 })
const statisticsDialogVisible = ref(false)
const statisticsData = ref([])

const getStatusType = (status) => ({ 0: 'success', 1: 'warning', 2: 'danger', 3: 'info' }[status] || 'info')
const getStatusName = (status) => ({ 0: '空闲', 1: '已预订', 2: '已入住', 3: '维护' }[status] || '未知')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdminRooms({ current: pagination.current, size: pagination.size, ...searchForm })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleEdit = (row) => { Object.assign(editForm, row); editDialogVisible.value = true }

const handleSubmitEdit = async () => {
  submitting.value = true
  try {
    await updateRoom(editForm.id, { basePrice: editForm.basePrice, seasonCoefficient: editForm.seasonCoefficient, status: editForm.status })
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const showBatchDialog = () => { batchForm.roomType = null; batchForm.coefficient = 1; batchDialogVisible.value = true }

const handleBatchUpdate = async () => {
  submitting.value = true
  try {
    await batchUpdateSeasonCoefficient({ roomType: batchForm.roomType, coefficient: batchForm.coefficient })
    ElMessage.success('批量更新成功')
    batchDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const showStatistics = async () => { const res = await getRoomStatistics(); statisticsData.value = res.data; statisticsDialogVisible.value = true }

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$text: #2c2c2c;
$text-secondary: #6b6b6b;

.admin-rooms { animation: fadeIn 0.3s ease; }
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
.search-form-right { flex-shrink: 0; display: flex; gap: 8px; }
@media (max-width: 900px) {
  .search-form-wrapper { flex-direction: column; align-items: flex-start; }
  .search-form-right { width: 100%; }
}
.price { color: $primary; }
.form-value { color: $text; font-weight: 500; }
.pagination-container { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
