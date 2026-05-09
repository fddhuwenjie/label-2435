import request from '../utils/request'

// ==================== 用户管理 ====================
export function getUsers(params) {
  return request({ url: '/admin/users', method: 'get', params })
}

export function createUser(data, role) {
  return request({ url: '/admin/users', method: 'post', data, params: { role } })
}

export function updateUser(id, data) {
  return request({ url: `/admin/users/${id}`, method: 'put', data })
}

export function deleteUser(id) {
  return request({ url: `/admin/users/${id}`, method: 'delete' })
}

// ==================== 房间管理 ====================
export function getAdminRooms(params) {
  return request({ url: '/admin/rooms', method: 'get', params })
}

export function updateRoom(id, data) {
  return request({ url: `/admin/rooms/${id}`, method: 'put', data })
}

export function batchUpdateSeasonCoefficient(params) {
  return request({ url: '/admin/rooms/season-coefficient', method: 'put', params })
}

export function getRoomStatistics() {
  return request({ url: '/admin/rooms/statistics', method: 'get' })
}

// ==================== 预订管理 ====================
export function getReservations(params) {
  return request({ url: '/admin/reservations', method: 'get', params })
}

export function forceCancelReservation(id, remark) {
  return request({ url: `/admin/reservations/${id}/force-cancel`, method: 'post', params: { remark } })
}

export function checkIn(id) {
  return request({ url: `/admin/reservations/${id}/check-in`, method: 'post' })
}

export function markBreach(id) {
  return request({ url: `/admin/reservations/${id}/breach`, method: 'post' })
}

// ==================== 定金管理 ====================
export function getDepositRecords(params) {
  return request({ url: '/admin/deposits', method: 'get', params })
}

export function getDepositStatistics(params) {
  return request({ url: '/admin/deposits/statistics', method: 'get', params })
}

// ==================== 导出功能 ====================
export function exportReservations(params) {
  return request({
    url: '/admin/export/reservations',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function exportDeposits(params) {
  return request({
    url: '/admin/export/deposits',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
