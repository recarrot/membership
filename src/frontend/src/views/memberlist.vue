<template>
  <div class="member-container p-4">
    <!-- 顶部操作栏 -->
    <div class="mb-4 flex justify-between items-center">
      <el-button type="primary" @click="addDialogVisible = true">添加会员</el-button>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索会员卡号或姓名"
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
    <el-table :data="members" border stripe>
      <el-table-column prop="cardNumber" label="会员卡号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="joinDate" label="入会时间" width="160">
        <template #default="scope">
          {{ new Date(scope.row.joinDate).toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="consumptionCount" label="消费次数" width="100" />
      <el-table-column prop="totalAmount" label="累计消费" width="120">
        <template #default="scope">
          ¥{{ scope.row.totalAmount.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="balance" label="账户余额" width="120">
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
      <el-table-column label="操作" fixed="right" width="280">
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
      <el-form :model="transactionForm" label-width="100px">
        <el-form-item label="消费金额" required>
          <el-input v-model="transactionForm.amount" type="number" />
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
      width="800px"
    >
      <el-table :data="transactions" border stripe>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'RECHARGE' ? 'success' : 'warning'">
              {{ scope.row.type === 'RECHARGE' ? '充值' : '消费' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="scope">
            ¥{{ scope.row.amount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMemberList,
  createMember,
  recharge,
  consume,
  getTransactions,
  updateMemberStatus
} from '@/api/member'

const members = ref([])
const transactions = ref([])
const addDialogVisible = ref(false)
const transactionDialogVisible = ref(false)
const rechargeDialogVisible = ref(false)
const consumeDialogVisible = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const memberForm = ref({
  cardNumber: '',
  name: '',
  idNumber: '',
  age: null
})

const transactionForm = ref({
  amount: '',
  remark: ''
})

const currentMember = ref(null)

onMounted(async () => {
  await loadMembers()
})

const loadMembers = async () => {
  try {
    const response = await getMemberList({
      page: currentPage.value - 1,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    members.value = response.content
    total.value = response.totalElements
  } catch (error) {
    ElMessage.error('加载会员列表失败')
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
    remark: ''
  }
}

const showConsumeDialog = (member) => {
  currentMember.value = member
  consumeDialogVisible.value = true
  transactionForm.value = {
    amount: '',
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

const handleConsume = async () => {
  try {
    await consume(currentMember.value.id, transactionForm.value)
    ElMessage.success('消费成功')
    consumeDialogVisible.value = false
    await loadMembers()
  } catch (error) {
    ElMessage.error('消费失败：' + error.response?.data?.message || '未知错误')
  }
}

const showTransactions = async (member) => {
  currentMember.value = member
  try {
    const response = await getTransactions(member.id, {
      page: 0,
      size: 50
    })
    transactions.value = response.content
    transactionDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载交易记录失败')
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
</script>

<style scoped>
.member-container {
  padding: 20px;
}
</style>