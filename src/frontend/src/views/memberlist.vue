<template>
  <div class="member-container p-4">
    <!-- 顶部操作栏 -->

    <div class="mb-4 flex justify-between items-center">
      <el-button type="primary" @click="addDialogVisible = true">添加会员</el-button>

      <!-- 时间筛选按钮 -->
      <div class="time-filter-container">
        <el-button
            :type="activeTimeFilter === 'all' ? 'primary' : 'default'"
            @click="toggleTimeFilter('all')">
          全部时间
        </el-button>
        <el-button
            :type="activeTimeFilter === 'month' ? 'primary' : 'default'"
            @click="toggleTimeFilter('month')">
          一个月内
        </el-button>
        <el-button
            :type="activeTimeFilter === 'quarter' ? 'primary' : 'default'"
            @click="toggleTimeFilter('quarter')">
          一个季度内
        </el-button>
        <el-button
            :type="activeTimeFilter === 'year' ? 'primary' : 'default'"
            @click="toggleTimeFilter('year')">
          一年内
        </el-button>
      </div>

      <!-- 搜索输入框 -->
      <el-input
          v-model="searchKeyword"
          placeholder="搜索会员卡号或姓名或手机号"
          class="w-64"
          clearable
          @clear="loadMembers"
          @keyup.enter="loadMembers"
      >
        <template #append>
          <el-button @click="loadMembers">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 会员列表表格 -->
    <el-table :data="members" border stripe  v-loading="loading">
      <el-table-column prop="cardNumber" label="会员卡号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="phoneNumber" label="手机号" width="120" />
      <el-table-column prop="idNumber" label="身份证号" width="180" />
      <el-table-column prop="joinDate" label="入会时间" width="90">
        <template #default="scope">
          {{ new Date(scope.row.joinDate).toLocaleDateString() }}
        </template>
      </el-table-column>
      <!-- 筛选后的消费次数 -->
      <el-table-column label="筛选期内消费次数" width="75">
        <template #default="scope">
          {{ scope.row.filteredConsumptionCount || 0 }}次
        </template>
      </el-table-column>

      <!-- 筛选后的抵扣前消费总额 -->
      <el-table-column label="筛选期内抵扣前消费" width="90">
        <template #default="scope">
          ¥{{ scope.row.filteredAmountBeforeDeduction?.toFixed(2) || '0.00' }}
        </template>
      </el-table-column>

      <!-- 筛选后的抵扣额 -->
      <el-table-column label="筛选期内抵扣金额" width="90">
        <template #default="scope">
          ¥{{ scope.row.filteredDeductionAmount?.toFixed(2) || '0.00' }}
        </template>
      </el-table-column>

      <!-- 历史总数据，可以保留或根据需要调整 -->
      <el-table-column label="历史消费次数" width="75" >
      <template #default="scope">
        {{ scope.row.consumptionCount || 0 }}次
      </template>
      </el-table-column>
      <el-table-column prop="totalAmountBeforeDeduction" label="历史抵扣前累计消费" width="90">
        <template #default="scope">
          ¥{{ scope.row.totalAmountBeforeDeduction?.toFixed(2) || scope.row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="历史累计抵扣金额" width="90">
        <template #default="scope">
          ¥{{ scope.row.totalAmount?.toFixed(2) || '0.00' }}
        </template>
      </el-table-column>
      <el-table-column prop="balance" label="账户余额" width="90">
        <template #default="scope">
          ¥{{ scope.row.balance.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="isActive" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isActive ? 'success' : 'danger'">
            {{ scope.row.isActive ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="400">
        <template #default="scope">
          <el-button size="small" @click="showRechargeDialog(scope.row)">充值</el-button>
          <el-button size="small" type="success" @click="showConsumeDialog(scope.row)">消费</el-button>
          <el-button size="small" type="primary" @click="showTransactions(scope.row)">记录</el-button>
          <el-button
              size="small"
              :type="scope.row.isActive ? 'danger' : 'success'"
              @click="toggleMemberStatus(scope.row)"
          >
            {{ scope.row.isActive ? '停用' : '启用' }}
          </el-button>
          <el-button size="small" @click="showChangePhoneNumberDialog(scope.row)">修改手机号</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="mt-4 flex justify-end">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加会员对话框 -->
    <el-dialog
        v-model="addDialogVisible"
        title="添加会员"
        width="500px"
    >
      <el-form :model="memberForm" label-width="100px">
        <el-form-item label="会员卡号" required>
          <el-input v-model="memberForm.cardNumber" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="memberForm.name" />
        </el-form-item>
        <el-form-item label="身份证号" required>
          <el-input v-model="memberForm.idNumber" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="memberForm.phoneNumber" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="memberForm.age" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddMember">确定</el-button>
      </template>
    </el-dialog>

    <!-- 充值对话框 -->
    <el-dialog
        v-model="rechargeDialogVisible"
        title="会员充值"
        width="500px"
    >
      <el-form :model="transactionForm" label-width="100px">
        <el-form-item label="充值金额" required>
          <el-input v-model="transactionForm.amount" type="number" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="transactionForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge">确定</el-button>
      </template>
    </el-dialog>

    <!-- 消费对话框 -->
    <el-dialog
        v-model="consumeDialogVisible"
        title="会员消费"
        width="500px"
    >
      <el-form :model="transactionForm" label-width="120px">
        <el-form-item label="消费金额" required>
          <el-input v-model="transactionForm.amount" type="number" placeholder="填写折扣前的消费金额" />
        </el-form-item>
        <el-form-item label="线路类型" required>
          <el-radio-group
              v-model="transactionForm.consumptionType"
              @change="handleConsumptionTypeChange"
          >
            <el-radio label="LONGLINE">飞机/火车线</el-radio>
            <el-radio label="SHORTLINE">汽车线</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="抵扣金额" required>
          <el-input v-model="transactionForm.deductionAmount" type="number" />
          <div class="text-xs text-gray-500 mt-1">抵扣金额从余额扣除</div>
        </el-form-item>
        <el-form-item label="出行方向" required>
          <el-input v-model="transactionForm.destination" type="textarea" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="transactionForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="consumeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConsume">确定</el-button>
      </template>
    </el-dialog>

    <!-- 交易记录对话框 -->
    <el-dialog
        v-model="transactionDialogVisible"
        title="交易记录"
        width="900px"
    >
      <!-- 时间筛选 -->
      <div style="margin-bottom: 20px;">
        <el-date-picker
            v-model="selectedTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleTimeRangeChange"
        />
        <el-button
            style="margin-left: 10px;"
            @click="clearTimeRange"
        >
          清除
        </el-button>
      </div>
      <el-table :data="filteredTransactions" border stripe v-loading="transactionsLoading">
        <el-table-column prop="type" label="类型" width="90">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'RECHARGE' ? 'success' : 'warning'">
              {{ scope.row.type === 'RECHARGE' ? '充值' : '消费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="消费金额" width="110">
          <template #default="scope">
            <template v-if="scope.row.type === 'CONSUMPTION'">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column label="抵扣金额" width="110">
          <template #default="scope">
            <template v-if="scope.row.type === 'CONSUMPTION' && scope.row.deductionAmount !== null">
              ¥{{ scope.row.deductionAmount.toFixed(2) }}
            </template>
            <template v-else-if="scope.row.type === 'CONSUMPTION' && scope.row.deductionAmount === null">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column label="充值金额" width="110">
          <template #default="scope">
            <template v-if="scope.row.type === 'RECHARGE'">
              ¥{{ scope.row.amount.toFixed(2) }}
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="线路类型" prop="consumptionType" width="100">
          <template #default="scope">
            <template v-if="getTagText(scope.row.consumptionType)">
              <el-tag>
                {{ getTagText(scope.row.consumptionType) }}
              </el-tag>
            </template>
            <template v-else>
              <!-- 空白内容 -->
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="destination" label="出行方向" />
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <!-- 分页控件 -->
      <div class="mt-4 flex justify-end">
        <el-pagination
            v-model:current-page="transactionCurrentPage"
            v-model:page-size="transactionPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="transactionTotal"
            @size-change="handleTransactionSizeChange"
            @current-change="handleTransactionPageChange"
        />
      </div>
    </el-dialog>

    <!-- 修改手机号 -->
    <el-dialog
        v-model="changePhoneNumberDialogVisible"
        title="修改手机号"
        width="500px"
    >
      <el-form :model="memberForm" label-width="100px">
        <el-form-item label="手机号" required>
          <el-input v-model="memberForm.phoneNumber" type="text" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePhoneNumberDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePhoneNumber">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'  // 确保导入axios
import { ElMessage } from 'element-plus'
import {
  getMemberList,
  createMember,
  recharge,
  consume,
  getTransactions,
  updateMemberStatus,
  updateMemberPhoneNumber
} from '@/api/member'

const members = ref([])
const transactions = ref([])
const addDialogVisible = ref(false)
const transactionDialogVisible = ref(false)
const rechargeDialogVisible = ref(false)
const consumeDialogVisible = ref(false)
const changePhoneNumberDialogVisible = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const selectedTimeRange = ref([]) // 时间范围

const transactionCurrentPage = ref(1)
const transactionPageSize = ref(50)
const transactionTotal = ref(0)
const transactionsLoading = ref(false)

const memberForm = ref({
  cardNumber: '',
  name: '',
  idNumber: '',
  phoneNumber: '',
  age: null
})

const transactionForm = ref({
  amount: '',
  consumptionType: '', // 初始值为空
  destination: '',
  remark: '',
  deductionAmount: ''
})

const loading = ref(false)

// 将activeTimeFilter定义在setup内
const activeTimeFilter = ref('all')

const currentMember = ref(null)

// 切换时间筛选函数
const toggleTimeFilter = (filterType) => {
  activeTimeFilter.value = filterType
  loadMembers()
}

// 定义getTagText函数
const getTagText = (consumptionType) => {
  switch (consumptionType) {
    case 'LONGLINE':
      return '飞机/火车线';
    case 'SHORTLINE':
      return '汽车线';
    case 'EMPTY':
      return '';
    default:
      return 'ERROR';
  }
}

onMounted(async () => {
  await loadMembers()
})

// 计算时间范围
const getTimeRange = (filterType) => {
  const now = new Date()
  let startDate = null
  const endDate = now.toISOString().split('T')[0] // 当前日期作为结束日期

  switch (filterType) {
    case 'month':
      startDate = new Date(now)
      startDate.setMonth(now.getMonth() - 1)
      break
    case 'quarter':
      startDate = new Date(now)
      startDate.setMonth(now.getMonth() - 3)
      break
    case 'year':
      startDate = new Date(now)
      startDate.setFullYear(now.getFullYear() - 1)
      break
    default:
      startDate = null // 全部时间不设限制
      return { startDate: null, endDate: null }
  }

  return {
    startDate: startDate ? startDate.toISOString().split('T')[0] : null,
    endDate
  }
}

// 加载筛选后的交易数据
const loadFilteredTransactions = async (membersList, startDate, endDate) => {
  try {
    // 对每个会员获取符合时间条件的交易记录
    const promises = membersList.map(async (member) => {
      // 只有在选择了时间筛选时才进行交易查询
      if (activeTimeFilter.value === 'all') {
        return {
          ...member,
          filteredConsumptionCount: member.consumptionCount,
          filteredAmountBeforeDeduction: member.totalAmountBeforeDeduction,
          filteredDeductionAmount: member.totalAmount
        }
      }

      // 使用你已有的API接口
      const params = {
        size: 1000, // 获取足够多的记录以便统计
        page: 0
      }

      if (startDate && endDate) {
        params.startDate = startDate
        params.endDate = endDate
      }

      // 调用你现有的getMemberTransactions API
      const response = await getTransactions(member.id, params)
      const transactions = response.content

      // 计算筛选后的统计数据
      let filteredConsumptionCount = 0
      let filteredAmountBeforeDeduction = 0
      let filteredDeductionAmount = 0

      if (transactions && transactions.length > 0) {
        // 只统计消费类型的交易
        const consumptionTransactions = transactions.filter(t => t.type === 'CONSUMPTION')
        filteredConsumptionCount = consumptionTransactions.length

        // 计算筛选期内的总额和抵扣金额
        consumptionTransactions.forEach(transaction => {
          // 消费金额（抵扣前）
          filteredAmountBeforeDeduction += Number(transaction.amount) || 0

          // 抵扣金额
          filteredDeductionAmount += Number(transaction.deductionAmount) || 0
        })
      }

      // 更新会员对象添加筛选后的数据
      return {
        ...member,
        filteredConsumptionCount,
        filteredAmountBeforeDeduction,
        filteredDeductionAmount
      }
    })

    // 等待所有请求完成，更新会员列表
    members.value = await Promise.all(promises)
  } catch (error) {
    ElMessage.error('加载交易记录失败')
    console.error(error)
  }
}

// 加载会员列表
const loadMembers = async () => {
  loading.value = true
  try {
    // 获取会员基本列表
    const response = await getMemberList({
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchKeyword.value
    })

    const { startDate, endDate } = getTimeRange(activeTimeFilter.value)
    members.value = response.content
    total.value = response.totalElements

    // 如果选择了时间筛选，则获取相应时间段的交易数据
    if (members.value.length > 0) {
      await loadFilteredTransactions(members.value, startDate, endDate)
    }
  } catch (error) {
    ElMessage.error('加载会员列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAddMember = async () => {
  try {
    await createMember(memberForm.value)
    ElMessage.success('添加会员成功')
    addDialogVisible.value = false
    await loadMembers()
    memberForm.value = {
      cardNumber: '',
      name: '',
      idNumber: '',
      phoneNumber: '',
      age: null
    }
  } catch (error) {
    ElMessage.error('添加会员失败：' + error.response?.data?.message || '未知错误')
  }
}

const showRechargeDialog = (member) => {
  currentMember.value = member
  rechargeDialogVisible.value = true
  transactionForm.value = {
    amount: '',
    destination: '',
    remark: ''
  }
}

const showChangePhoneNumberDialog = (member) => {
  currentMember.value = member;
  memberForm.value.phoneNumber = member.phoneNumber; // 填充当前手机号
  changePhoneNumberDialogVisible.value = true;
};

const showConsumeDialog = (member) => {
  currentMember.value = member
  consumeDialogVisible.value = true
  // 默认选择长线
  const defaultConsumptionType = 'LONGLINE'
  const defaultDeductionAmount = defaultConsumptionType === 'LONGLINE' ? '200' : '20'

  transactionForm.value = {
    amount: '',
    deductionAmount: defaultDeductionAmount,
    consumptionType: defaultConsumptionType,
    destination: '',
    remark: ''
  }
}

const handleRecharge = async () => {
  try {
    await recharge(currentMember.value.id, transactionForm.value)
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    await loadMembers()
  } catch (error) {
    ElMessage.error('充值失败：' + error.response?.data?.message || '未知错误')
  }
}

const handleUpdatePhoneNumber = async () => {
  const newPhoneNumber = memberForm.value.phoneNumber.trim();
  if (!newPhoneNumber) {
    ElMessage.error("手机号不能为空");
    return;
  }

  try {
    await updateMemberPhoneNumber(currentMember.value.id, newPhoneNumber);
    ElMessage.success("手机号更新成功");
    changePhoneNumberDialogVisible.value = false;
    await loadMembers();
  } catch (error) {
    ElMessage.error('更新失败：' + error.response?.data?.message || '未知错误');
  }
};

// 监听消费类型变化，自动设置抵扣金额默认值
const handleConsumptionTypeChange = (type) => {
  if (type === 'LONGLINE') {
    transactionForm.value.deductionAmount = '200'
  } else if (type === 'SHORTLINE') {
    transactionForm.value.deductionAmount = '20'
  }
}

const handleConsume = async () => {
  if (!transactionForm.value.amount){
    ElMessage.error('请输入金额')
    return
  }
  else if (transactionForm.value.deductionAmount === undefined || transactionForm.value.deductionAmount === '') {
    ElMessage.error('请输入抵扣金额')
    return
  }
  else if (!transactionForm.value.consumptionType) {
    ElMessage.error('请选择线路类型')
    return
  }
  else if (!transactionForm.value.destination){
    ElMessage.error('请输入出行方向')
    return
  }

  // 验证金额和抵扣金额
  const amount = parseFloat(transactionForm.value.amount)
  const deductionAmount = parseFloat(transactionForm.value.deductionAmount)

  if (isNaN(amount) || amount <= 0) {
    ElMessage.error('消费金额必须大于0')
    return
  }

  if (isNaN(deductionAmount) || deductionAmount < 0) {
    ElMessage.error('抵扣金额不能为负数')
    return
  }

  if (deductionAmount > amount) {
    ElMessage.error('抵扣金额不能大于消费金额')
    return
  }

  if (deductionAmount > currentMember.value.balance) {
    ElMessage.error('余额不足以支付抵扣金额')
    return
  }

  try {
    await consume(currentMember.value.id, transactionForm.value)
    ElMessage.success('消费成功')
    consumeDialogVisible.value = false
    await loadMembers()
  } catch (error) {
    ElMessage.error('消费失败：' + (error.response?.data?.message || '未知错误'))
  }
}

const showTransactions = async (member) => {
  currentMember.value = member
  transactionCurrentPage.value = 1
  transactionPageSize.value = 50
  selectedTimeRange.value = []
  await loadTransactions()
  transactionDialogVisible.value = true
}

// 加载交易记录
const loadTransactions = async () => {
  if (!currentMember.value) return

  transactionsLoading.value = true
  try {
    const params = {
      page: transactionCurrentPage.value - 1, // 后端分页从0开始
      size: transactionPageSize.value
    }

    // 如果有时间筛选，添加时间参数
    if (selectedTimeRange.value && selectedTimeRange.value.length === 2) {
      params.startDate = selectedTimeRange.value[0]
      params.endDate = selectedTimeRange.value[1]
    }

    const response = await getTransactions(currentMember.value.id, params)
    transactions.value = response.content
    transactionTotal.value = response.totalElements
    transactionsLoading.value = false
  } catch (error) {
    ElMessage.error('加载交易记录失败')
    transactionsLoading.value = false
  }
}

const toggleMemberStatus = async (member) => {
  try {
    await updateMemberStatus(member.id, !member.isActive)
    ElMessage.success(member.isActive ? '会员已停用' : '会员已启用')
    await loadMembers()
  } catch (error) {
    ElMessage.error('操作失败：' + error.response?.data?.message || '未知错误')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadMembers()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadMembers()
}

// 处理交易记录分页大小变化
const handleTransactionSizeChange = (newSize) => {
  transactionPageSize.value = newSize
  loadTransactions()
}

// 处理交易记录页码变化
const handleTransactionPageChange = (newPage) => {
  transactionCurrentPage.value = newPage
  loadTransactions()
}

// 时间范围改变时重新加载数据
const handleTimeRangeChange = () => {
  transactionCurrentPage.value = 1
  loadTransactions()
}

// 清除时间范围并重新加载数据
const clearTimeRange = () => {
  selectedTimeRange.value = []
  transactionCurrentPage.value = 1
  loadTransactions()
}

// 计算属性：根据时间范围筛选交易记录
const filteredTransactions = computed(() => {
  return transactions.value
})
</script>

<style scoped>
.member-container {
  padding: 20px;
}
</style>