import request from '../utils/request'

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

// 查询房间列表
export function getRooms(params) {
  return request({
    url: '/user/rooms',
    method: 'get',
    params
  })
}

// 查询可用房间
export function getAvailableRooms(params) {
  return request({
    url: '/user/rooms/available',
    method: 'get',
    params
  })
}

// 计算价格
export function calculatePrice(params) {
  return request({
    url: '/user/price/calculate',
    method: 'get',
    params
  })
}

// 创建预订
export function createReservation(data) {
  return request({
    url: '/user/reservations',
    method: 'post',
    data
  })
}

// 支付定金
export function payDeposit(id) {
  return request({
    url: `/user/reservations/${id}/pay`,
    method: 'post'
  })
}

// 取消预订
export function cancelReservation(id) {
  return request({
    url: `/user/reservations/${id}/cancel`,
    method: 'post'
  })
}

// 查询我的预订
export function getMyReservations(params) {
  return request({
    url: '/user/reservations',
    method: 'get',
    params
  })
}
