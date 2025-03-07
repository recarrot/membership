import request from '@/utils/request'

// 获取会员列表
export function getMemberList(params) {
  return request({
    url: '/api/members',
    method: 'get',
    params
  })
}

// 创建会员
export function createMember(data) {
  return request({
    url: '/api/members',
    method: 'post',
    data
  })
}

// 充值
export function recharge(memberId, data) {
  return request({
    url: `/api/members/${memberId}/recharge`,
    method: 'post',
    data
  })
}

// 消费
export function consume(memberId, data) {
  return request({
    url: `/api/members/${memberId}/consume`,
    method: 'post',
    data
  })
}

// 获取交易记录
export function getTransactions(memberId, params) {
  return request({
    url: `/api/members/${memberId}/transactions`,
    method: 'get',
    params
  })
}

// 更新会员状态
export function updateMemberStatus(memberId, isActive) {
  return request({
    url: `/api/members/${memberId}/status`,
    method: 'put',
    params: { isActive }
  })
}

export function getMemberStatistics() {
  return request({
    url: '/api/members/statistics',
    method: 'get'
  })
}

// 更新会员手机号
export function updateMemberPhoneNumber(memberId, phoneNumber) {
  return request({
    url: `/api/members/${memberId}/updatePhoneNumber`,
    method: 'put',
    params: { phoneNumber }
  })
}

// 更新会员身份证号
export function updateMemberIdNumber(memberId, idNumber) {
  return request({
    url: `/api/members/${memberId}/updateIdNumber`,
    method: 'put',
    params: { idNumber }
  })
}

