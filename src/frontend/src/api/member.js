import request from '@/utils/request'

export function getMemberList(params) {
  return request({
    url: '/members',
    method: 'get',
    params
  })
}

export function createMember(data) {
  return request({
    url: '/members',
    method: 'post',
    data
  })
}

export function recharge(memberId, data) {
  return request({
    url: `/members/${memberId}/recharge`,
    method: 'post',
    data
  })
}

export function consume(memberId, data) {
  return request({
    url: `/members/${memberId}/consume`,
    method: 'post',
    data
  })
}

export function getTransactions(memberId, params) {
  return request({
    url: `/members/${memberId}/transactions`,
    method: 'get',
    params
  })
}

export function updateMemberStatus(memberId, isActive) {
  return request({
    url: `/members/${memberId}/status`,
    method: 'put',
    params: { isActive }
  })
}

export function getMemberStatistics() {
  return request({
    url: '/members/statistics',
    method: 'get'
  })
}

export function updateMemberPhoneNumber(memberId, phoneNumber) {
  return request({
    url: `/members/${memberId}/updatePhoneNumber`,
    method: 'put',
    params: { phoneNumber }
  })
}
