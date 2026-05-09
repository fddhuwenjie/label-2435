<template>
  <div class="rooms-page">
    <div class="page-header">
      <h1 class="page-title">探索客房</h1>
      <p class="page-subtitle">发现您的完美住所，体验无与伦比的舒适</p>
      <div class="header-decoration">
        <span class="line"></span>
        <span class="diamond">◇</span>
        <span class="line"></span>
      </div>
    </div>

    <div class="search-section">
      <div class="search-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="房间类型">
            <el-select v-model="searchForm.roomType" placeholder="全部类型" clearable>
              <el-option label="豪华单人间" :value="1" />
              <el-option label="尊享双人间" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="入住日期">
            <el-date-picker v-model="searchForm.checkInDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate" />
          </el-form-item>
          <el-form-item label="退房日期">
            <el-date-picker v-model="searchForm.checkOutDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate" />
          </el-form-item>
          <el-form-item class="search-actions">
            <el-button type="primary" @click="handleSearch">搜索可用房间</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="rooms-section">
      <div class="rooms-grid" v-loading="loading">
        <div v-for="room in tableData" :key="room.id" class="room-card" :class="{ unavailable: room.status !== 0 }">
          <div class="room-image">
            <div class="room-type-badge">{{ room.roomTypeName }}</div>
            <div class="room-status" :class="getStatusClass(room.status)">{{ room.statusName }}</div>
            <div class="room-icon">🏨</div>
          </div>
          
          <div class="room-content">
            <div class="room-header">
              <h3 class="room-number">{{ room.roomNumber }}</h3>
              <div class="room-rating">★★★★★</div>
            </div>
            
            <div class="room-features">
              <span>🛏 {{ room.roomType === 1 ? '单人床' : '双人床' }}</span>
              <span>📶 免费WiFi</span>
              <span>☕ 早餐</span>
            </div>
            
            <div class="room-footer">
              <div class="room-price">
                <span class="label">每晚</span>
                <span class="value">¥{{ room.basePrice }}</span>
                <span class="coefficient" v-if="room.seasonCoefficient !== 1">× {{ room.seasonCoefficient }}</span>
              </div>
              <el-button type="primary" :disabled="room.status !== 0" @click="handleBook(room)">
                {{ room.status === 0 ? '立即预订' : '暂不可订' }}
              </el-button>
            </div>
          </div>
        </div>
        
        <div v-if="!loading && tableData.length === 0" class="empty-state">
          <div class="empty-icon">🏨</div>
          <p>暂无符合条件的房间</p>
        </div>
      </div>
      
      <div class="pagination-section" v-if="pagination.total > 0">
        <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size" :total="pagination.total" :page-sizes="[6, 12, 24]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
      </div>
    </div>

    <el-dialog v-model="bookDialogVisible" title="预订确认" width="500px">
      <div class="dialog-content">
        <div class="selected-room">
          <div class="room-preview">{{ selectedRoom?.roomTypeName }}</div>
          <div class="room-info">
            <h4>{{ selectedRoom?.roomNumber }}</h4>
            <p>基础价格: ¥{{ selectedRoom?.basePrice }}/晚</p>
          </div>
        </div>
        
        <el-form :model="bookForm" label-width="100px" class="book-form">
          <el-form-item label="入住日期">
            <el-date-picker v-model="bookForm.checkInDate" type="date" placeholder="选择入住日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate" style="width: 100%" />
          </el-form-item>
          <el-form-item label="退房日期">
            <el-date-picker v-model="bookForm.checkOutDate" type="date" placeholder="选择退房日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate" style="width: 100%" />
          </el-form-item>
        </el-form>
        
        <div v-if="priceInfo" class="price-summary">
          <div class="summary-title">费用明细</div>
          <div class="summary-item"><span>入住天数</span><span>{{ priceInfo.reserveDays }} 晚</span></div>
          <div class="summary-item"><span>房费总计</span><span>¥{{ priceInfo.totalPrice }}</span></div>
          <div class="summary-item highlight"><span>预付定金 (10%)</span><span class="deposit">¥{{ priceInfo.deposit }}</span></div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="info" @click="handleCalculatePrice" :loading="calculating">计算价格</el-button>
        <el-button type="primary" @click="handleConfirmBook" :loading="booking">确认预订</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAvailableRooms, getRooms, calculatePrice, createReservation, payDeposit } from '../../api/user'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 6, total: 0 })
const searchForm = reactive({ roomType: null, checkInDate: '', checkOutDate: '' })
const bookDialogVisible = ref(false)
const selectedRoom = ref(null)
const bookForm = reactive({ checkInDate: '', checkOutDate: '' })
const priceInfo = ref(null)
const calculating = ref(false)
const booking = ref(false)

const getStatusClass = (status) => ({ 0: 'available', 1: 'booked', 2: 'occupied', 3: 'maintenance' }[status] || '')
const disabledDate = (date) => date.getTime() < Date.now() - 24 * 60 * 60 * 1000

const loadData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size, roomType: searchForm.roomType }
    let res
    if (searchForm.checkInDate && searchForm.checkOutDate) {
      params.checkInDate = searchForm.checkInDate
      params.checkOutDate = searchForm.checkOutDate
      res = await getAvailableRooms(params)
    } else {
      res = await getRooms(params)
    }
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally { loading.value = false }
}

const handleSearch = () => { pagination.current = 1; loadData() }
const handleReset = () => { searchForm.roomType = null; searchForm.checkInDate = ''; searchForm.checkOutDate = ''; handleSearch() }

const handleBook = (row) => {
  selectedRoom.value = row
  bookForm.checkInDate = searchForm.checkInDate || ''
  bookForm.checkOutDate = searchForm.checkOutDate || ''
  priceInfo.value = null
  bookDialogVisible.value = true
}

const handleCalculatePrice = async () => {
  if (!bookForm.checkInDate || !bookForm.checkOutDate) { ElMessage.warning('请选择入住和退房日期'); return }
  calculating.value = true
  try {
    const res = await calculatePrice({ roomId: selectedRoom.value.id, checkInDate: bookForm.checkInDate, checkOutDate: bookForm.checkOutDate })
    priceInfo.value = res.data
  } finally { calculating.value = false }
}

const handleConfirmBook = async () => {
  if (!bookForm.checkInDate || !bookForm.checkOutDate) { ElMessage.warning('请选择入住和退房日期'); return }
  booking.value = true
  try {
    const res = await createReservation({ roomId: selectedRoom.value.id, checkInDate: bookForm.checkInDate, checkOutDate: bookForm.checkOutDate })
    const deposit = priceInfo.value?.deposit || res.data.deposit
    try {
      await ElMessageBox.confirm(`预订成功！定金为 ¥${deposit}，是否立即支付？`, '支付定金', { confirmButtonText: '立即支付', cancelButtonText: '稍后支付', type: 'success' })
      await payDeposit(res.data.id)
      ElMessage.success('定金支付成功')
    } catch (e) { /* 用户取消支付，忽略 */ }
    bookDialogVisible.value = false
    loadData()
  } catch (e) { /* 预订失败，保持弹窗打开 */ } finally { booking.value = false }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
$primary: #8b7355;
$primary-light: #a89078;
$accent: #c4a77d;
$bg: #faf9f7;
$bg-warm: #f5f3f0;
$text: #2c2c2c;
$text-secondary: #6b6b6b;
$border: #e8e5e1;

.rooms-page { min-height: 100%; }

.page-header {
  text-align: center;
  padding: 40px 20px 32px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.page-title {
  
  font-size: 32px;
  font-weight: 500;
  color: $text;
  margin-bottom: 8px;
}

.page-subtitle {
  font-size: 14px;
  color: $text-secondary;
}

.header-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 20px;
  
  .line { width: 60px; height: 1px; background: $border; }
  .diamond { color: $primary; font-size: 10px; }
}

.search-section { margin-bottom: 24px; }

.search-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  
  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 0;
    
    .el-form-item__label {
      color: $text-secondary;
      font-size: 12px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }
  
  .search-actions { margin-left: auto; }
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
  min-height: 200px;
}

.room-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0,0,0,0.08);
  }
  
  &.unavailable {
    opacity: 0.7;
    .room-image { filter: grayscale(30%); }
  }
}

.room-image {
  height: 160px;
  background: linear-gradient(135deg, $bg-warm 0%, #ebe7e2 100%);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .room-icon { font-size: 48px; opacity: 0.3; }
}

.room-type-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: $primary;
  color: #fff;
  padding: 6px 12px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
  border-radius: 4px;
}

.room-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 5px 10px;
  font-size: 11px;
  font-weight: 500;
  border-radius: 4px;
  
  &.available { background: rgba(#5a9a6e, 0.1); color: #5a9a6e; }
  &.booked { background: rgba(#d4a24c, 0.1); color: #d4a24c; }
  &.occupied { background: rgba(#c75c5c, 0.1); color: #c75c5c; }
  &.maintenance { background: rgba(#999, 0.1); color: #999; }
}

.room-content { padding: 20px; }

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  
  .room-number {
    
    font-size: 20px;
    color: $text;
  }
  
  .room-rating {
    color: $accent;
    font-size: 11px;
    letter-spacing: 2px;
  }
}

.room-features {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid $border;
  
  span {
    font-size: 12px;
    color: $text-secondary;
  }
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-price {
  .label { font-size: 12px; color: $text-secondary; margin-right: 6px; }
  .value {  font-size: 22px; color: $primary; }
  .coefficient { font-size: 12px; color: $text-secondary; margin-left: 4px; }
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  
  .empty-icon { font-size: 48px; margin-bottom: 16px; opacity: 0.4; }
  p { color: $text-secondary; }
}

.pagination-section {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.dialog-content {
  .selected-room {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid $border;
  }
  
  .room-preview {
    width: 100px;
    height: 70px;
    background: $bg-warm;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    color: $primary;
    font-weight: 500;
  }
  
  .room-info {
    h4 {  font-size: 18px; color: $text; margin-bottom: 4px; }
    p { font-size: 13px; color: $text-secondary; }
  }
}

.book-form { 
  margin-bottom: 24px;
  
  :deep(.el-form-item) {
    .el-input, .el-date-editor {
      width: 100%;
    }
  }
}

.price-summary {
  background: $bg-warm;
  border-radius: 8px;
  padding: 20px;
  
  .summary-title {
    font-size: 12px;
    color: $text-secondary;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 16px;
    text-align: center;
  }
  
  .summary-item {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 10px;
    
    &.highlight {
      padding-top: 12px;
      border-top: 1px dashed $border;
      color: $text;
      font-weight: 500;
      margin-bottom: 0;
      
      .deposit { color: $primary; font-size: 18px;  }
    }
  }
}
</style>
