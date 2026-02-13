<template>
  <el-card>
    <template #header><span>收到的简历</span></template>
    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="studentName" label="学生姓名" width="120" />
      <el-table-column prop="jobTitle" label="应聘职位" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="{ 0:'info',1:'warning',2:'success',3:'danger' }[row.status]">
            {{ { 0:'待查看',1:'已查看',2:'通过',3:'不合适' }[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="投递时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="viewResume(row)">查看简历</el-button>
          <el-button v-if="row.status < 2" size="small" type="success" @click="handleStatus(row, 2)">通过</el-button>
          <el-button v-if="row.status < 2" size="small" type="danger" @click="handleStatus(row, 3)">不合适</el-button>
          <el-button v-if="row.status === 2" size="small" type="warning" @click="openInterview(row)">发面试邀请</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top:16px;justify-content:center" background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="currentPage" @current-change="loadData" />

    <!-- 简历弹窗 -->
    <el-dialog v-model="resumeVisible" title="学生简历" width="600px">
      <div style="white-space:pre-wrap;line-height:1.8">{{ resumeContent }}</div>
    </el-dialog>

    <!-- 面试邀请弹窗 -->
    <el-dialog v-model="interviewVisible" title="发送面试邀请" width="500px">
      <el-form :model="interviewForm" label-width="90px">
        <el-form-item label="面试时间">
          <el-date-picker v-model="interviewForm.interviewTime" type="datetime" placeholder="选择面试时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="面试地点">
          <el-input v-model="interviewForm.location" placeholder="如: 公司3楼会议室 / 腾讯会议" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="interviewForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="sendingInterview" @click="handleSendInterview">发送</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReceivedApplications, handleApplication, sendInterview } from '../../api'

const list = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)
const resumeVisible = ref(false)
const resumeContent = ref('')
const interviewVisible = ref(false)
const sendingInterview = ref(false)
const currentApp = ref(null)

const interviewForm = reactive({ interviewTime: '', location: '', remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await getReceivedApplications({ pageNum: currentPage.value, pageSize: 10 })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const viewResume = (row) => {
  resumeContent.value = row.resumeContent || '该学生尚未填写简历'
  resumeVisible.value = true
}

const handleStatus = async (row, status) => {
  try {
    await handleApplication(row.id, { status })
    ElMessage.success('操作成功')
    loadData()
  } catch (e) {}
}

const openInterview = (row) => {
  currentApp.value = row
  Object.assign(interviewForm, { interviewTime: '', location: '', remark: '' })
  interviewVisible.value = true
}

const handleSendInterview = async () => {
  if (!interviewForm.interviewTime || !interviewForm.location) {
    return ElMessage.warning('请填写面试时间和地点')
  }
  sendingInterview.value = true
  try {
    await sendInterview({
      applicationId: currentApp.value.id,
      interviewTime: interviewForm.interviewTime,
      location: interviewForm.location,
      remark: interviewForm.remark
    })
    ElMessage.success('面试邀请已发送')
    interviewVisible.value = false
  } finally {
    sendingInterview.value = false
  }
}

onMounted(loadData)
</script>
