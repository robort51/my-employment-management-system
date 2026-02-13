<template>
  <el-card>
    <template #header><span>用户管理</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="{ admin:'',student:'success',company:'warning' }[row.role]">
            {{ { admin:'管理员',student:'学生',company:'企业' }[row.role] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.role !== 'admin'" size="small" :type="row.status === 1 ? 'danger' : 'success'" @click="handleToggle(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, toggleUserStatus } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleToggle = async (row) => {
  try {
    await toggleUserStatus(row.id)
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

onMounted(loadData)
</script>
