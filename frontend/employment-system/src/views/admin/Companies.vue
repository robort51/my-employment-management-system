<template>
  <el-card>
    <template #header><span>企业审核</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="companyName" label="企业名称" width="180" />
      <el-table-column prop="industry" label="行业" width="120" />
      <el-table-column prop="scale" label="规模" width="120" />
      <el-table-column prop="contactPerson" label="联系人" width="100" />
      <el-table-column prop="contactPhone" label="联系电话" width="130" />
      <el-table-column prop="auditStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0:'warning',1:'success',2:'danger' }[row.auditStatus]">
            {{ { 0:'待审核',1:'已通过',2:'未通过' }[row.auditStatus] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.auditStatus === 0" size="small" type="success" @click="handleAudit(row, 1)">通过</el-button>
          <el-button v-if="row.auditStatus === 0" size="small" type="danger" @click="handleAudit(row, 2)">拒绝</el-button>
          <el-button size="small" link type="primary" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />

    <el-dialog v-model="detailVisible" title="企业详情" width="500px">
      <el-descriptions :column="1" border v-if="currentRow">
        <el-descriptions-item label="企业名称">{{ currentRow.companyName }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ currentRow.industry }}</el-descriptions-item>
        <el-descriptions-item label="规模">{{ currentRow.scale }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentRow.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRow.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentRow.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentRow.address }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ currentRow.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCompanyList, auditCompany } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)
const detailVisible = ref(false)
const currentRow = ref(null)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCompanyList({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleAudit = async (row, status) => {
  try {
    await auditCompany(row.id, { auditStatus: status })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

const viewDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

onMounted(loadData)
</script>
