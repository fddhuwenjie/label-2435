<template>
  <div class="reservations-page">
    <div class="page-header">
      <h1 class="page-title">我的预订</h1>
      <p class="page-subtitle">查看和管理您的所有预订记录</p>
      <div class="header-decoration"><span class="line"></span><span class="diamond">◇</span><span class="line"></span></div>
    </div>

    <div class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="预订状态">
          <el-select v-model="searchForm.status" placeholder="全部状态" clearable>
            <el-option label="待入住" :value="0" />
            <el-option label="已取消" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已违约" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="roomTypeName" label="房间类型" width="100" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="checkOutDate" label="退房日期" width="120" />
        <el-table-column prop="reserveDays" label="天数" width="80" />
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.depositStatus === 0 && row.status === 0" type="primary" size="small" @click="handlePay(row)">支付定金</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyReservations, payDeposit, cancelReservation } from '../../api/user'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })
const searchForm = reactive({ status: null })

const getStatusType = (status) => ({ 0: 'warning', 1: 'info', 2: 'success', 3: 'danger' }[status] || 'info')
const getDepositStatusType = (status) => ({ 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }[status] || 'info')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyReservations({ current: pagination.current, size: pagination.size, status: searchForm.status })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }

const handlePay = async (row) => {
  await ElMessageBox.confirm(`确认支付定金 ¥${row.deposit}？`, '支付定金', { type: 'warning' })
  await payDeposit(row.id)
  ElMessage.success('支付成功')
  loadData()
}

const handleCancel = async (row) => {
  let message = '确认取消此预订？'
  if (row.depositStatus === 1) {
    const hours = (new Date() - new Date(row.reserveTime)) / (1000 * 60 * 60)
    message = hours <= 6 ? '预订时间未超过6小时，取消后定金将全额退还。确认取消？' : '预订时间已超过6小时，取消后定金将不予退还。确认取消？'
  }
  await ElMessageBox.confirm(message, '取消预订', { type: 'warning' })
  await cancelReservation(row.id)
  ElMessage.success('取消成功')
  loadData()
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$bg: #faf9f7;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.reservations-page { min-height: 100%; }

.page-header {
  text-align: center;
  padding: 40px 20px 32px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  
  .page-title {  font-size: 32px; font-weight: 500; color: $text; margin-bottom: 8px; }
  .page-subtitle { font-size: 14px; color: $text-secondary; }
  .header-decoration { display: flex; align-items: center; justify-content: center; gap: 12px; margin-top: 20px;
    .line { width: 60px; height: 1px; background: $border; }
    .diamond { color: $primary; font-size: 10px; }
  }
}

.search-card, .table-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.search-card :deep(.el-form-item) { margin-bottom: 0; margin-right: 16px; }
.price { color: $primary; }
.pagination-container { margin-top: 24px; display: flex; justify-content: flex-end; }
</style>
