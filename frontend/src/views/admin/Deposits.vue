<template>
  <div class="admin-deposits">
    <div class="page-title-section">
      <h2>定金管理</h2>
      <p>管理所有定金记录</p>
    </div>

    <div class="search-card">
      <div class="search-form-wrapper">
        <el-form :inline="true" :model="searchForm" class="search-form-left">
          <el-form-item label="预订ID"><el-input v-model="searchForm.reservationId" placeholder="请输入预订ID" clearable /></el-form-item>
          <el-form-item label="操作类型">
            <el-select v-model="searchForm.operateType" placeholder="全部" clearable>
              <el-option label="支付" :value="0" /><el-option label="退款" :value="1" /><el-option label="扣罚" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker v-model="searchForm.startTime" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" :teleported="true" />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker v-model="searchForm.endTime" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" :teleported="true" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="search-form-right">
          <el-button type="success" @click="handleStatistics">收支统计</el-button>
        </div>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="记录ID" width="200" />
        <el-table-column prop="reservationId" label="预订ID" width="200" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }"><span class="price">¥{{ row.amount }}</span></template>
        </el-table-column>
        <el-table-column prop="operateType" label="操作类型" width="100">
          <template #default="{ row }"><el-tag :type="getOperateTypeTag(row.operateType)" size="small">{{ getOperateTypeName(row.operateType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" />
      </el-table>
      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="statisticsDialogVisible" title="定金收支统计" width="500px">
      <el-form :model="statisticsForm" label-width="100px">
        <el-form-item label="开始时间">
          <el-date-picker v-model="statisticsForm.startTime" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="statisticsForm.endTime" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div v-if="statisticsData.length > 0" class="statistics-result">
        <div class="statistics-title">统计结果</div>
        <div v-for="item in statisticsData" :key="item.operateType" class="statistics-item">
          <span class="label">{{ getOperateTypeName(item.operateType) }}</span>
          <span class="value" :class="getOperateTypeClass(item.operateType)">¥{{ item.totalAmount || 0 }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="statisticsDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleQueryStatistics" :loading="statisticsLoading">查询统计</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDepositRecords, getDepositStatistics } from '../../api/admin'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const searchForm = reactive({ reservationId: '', operateType: null, startTime: '', endTime: '' })
const statisticsDialogVisible = ref(false)
const statisticsForm = reactive({ startTime: '', endTime: '' })
const statisticsData = ref([])
const statisticsLoading = ref(false)

const getOperateTypeName = (type) => ({ 0: '支付', 1: '退款', 2: '扣罚' }[type] || '未知')
const getOperateTypeTag = (type) => ({ 0: 'success', 1: 'warning', 2: 'danger' }[type] || 'info')
const getOperateTypeClass = (type) => ({ 0: 'income', 1: 'refund', 2: 'forfeit' }[type] || '')

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size }
    if (searchForm.reservationId) params.reservationId = searchForm.reservationId
    if (searchForm.operateType !== null) params.operateType = searchForm.operateType
    if (searchForm.startTime) params.startTime = searchForm.startTime
    if (searchForm.endTime) params.endTime = searchForm.endTime
    const res = await getDepositRecords(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { 
  searchForm.reservationId = ''
  searchForm.operateType = null
  searchForm.startTime = ''
  searchForm.endTime = ''
  handleSearch() 
}

const handleStatistics = () => {
  statisticsForm.startTime = ''
  statisticsForm.endTime = ''
  statisticsData.value = []
  statisticsDialogVisible.value = true
}

const handleQueryStatistics = async () => {
  if (!statisticsForm.startTime || !statisticsForm.endTime) {
    ElMessage.warning('请选择时间范围')
    return
  }
  statisticsLoading.value = true
  try {
    const res = await getDepositStatistics({ startTime: statisticsForm.startTime, endTime: statisticsForm.endTime })
    statisticsData.value = res.data
  } finally { statisticsLoading.value = false }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.admin-deposits { animation: fadeIn 0.3s ease; }
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
.pagination-container { margin-top: 24px; display: flex; justify-content: flex-end; }

.statistics-result {
  margin-top: 20px;
  padding: 20px;
  background: #f5f3f0;
  border-radius: 8px;
  
  .statistics-title {
    font-size: 12px;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 16px;
    text-align: center;
  }
  
  .statistics-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px dashed $border;
    
    &:last-child { border-bottom: none; }
    
    .label { color: $text-secondary; }
    .value { font-size: 18px; }
    .income { color: #5a9a6e; }
    .refund { color: #d4a24c; }
    .forfeit { color: #c75c5c; }
  }
}
</style>
