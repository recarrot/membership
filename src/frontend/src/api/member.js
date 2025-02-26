import request from '@/utils/request'

export function getMemberList(params) {
  return request({
    url: '/api/members',
    method: 'get',
    params
  })
}

export function createMember(data) {
  return request({
    url: '/api/members',
    method: 'post',
    data
  })
}

export function recharge(memberId, data) {
  return request({
    url: `/api/members/${memberId}/recharge`,
    method: 'post',
    data
  })
}

export function consume(memberId, data) {
  return request({
    url: `/api/members/${memberId}/consume`,
    method: 'post',
    data
  })
}

export function getTransactions(memberId, params) {
  return request({
    url: `/api/members/${memberId}/transactions`,
    method: 'get',
    params
  })
}

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

export function updateMemberPhoneNumber(memberId, phoneNumber) {
  return request({
    url: `/api/members/${memberId}/updatePhoneNumber`,
    method: 'put',
    params: { phoneNumber }
  })
}

export function updateMemberIdNumber(memberId, idNumber) {
  return request({
    url: `/api/members/${memberId}/updateIdNumber`,
    method: 'put',
    params: { idNumber }
  })
}

