<template>
  <el-card>
    <template #header><span>AI模拟面试</span></template>
    <!-- 开始面试 -->
    <div v-if="step === 'start'">
      <el-form label-width="100px" style="max-width:500px">
        <el-form-item label="目标职位">
          <el-input v-model="jobTitle" placeholder="如: Java后端开发工程师" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleStart">生成面试题</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 答题阶段 -->
    <div v-if="step === 'answering'">
      <el-alert :title="`面试职位: ${jobTitle}`" type="info" :closable="false" style="margin-bottom:16px" />
      <div v-for="(q, i) in questions" :key="i" style="margin-bottom:20px">
        <p style="font-weight:bold;margin-bottom:8px">{{ i + 1 }}. {{ q }}</p>
        <el-input v-model="answers[i]" type="textarea" :rows="3" placeholder="请输入你的回答" />
      </div>
      <el-button type="success" :loading="submitting" @click="handleSubmit">提交答案</el-button>
      <el-button @click="step = 'start'">重新开始</el-button>
    </div>
    <!-- 结果展示 -->
    <div v-if="step === 'result'" class="result-box">
      <h4>AI评价：</h4>
      <div style="white-space:pre-wrap;line-height:1.8">{{ feedback }}</div>
      <p style="margin-top:12px;font-size:18px;font-weight:bold">综合评分：<el-tag type="success" size="large">{{ score }} 分</el-tag></p>
      <el-button type="primary" style="margin-top:16px" @click="step = 'start'; jobTitle = ''">再来一次</el-button>
    </div>
    <el-divider />
    <h4 style="margin-bottom:12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="jobTitle" label="职位" width="200" />
      <el-table-column prop="score" label="评分" width="80" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button size="small" link type="primary" @click="viewDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="面试详情" width="600px">
      <div v-if="detail">
        <h4>问答内容：</h4>
        <div style="white-space:pre-wrap;line-height:1.8;margin-bottom:16px">{{ detail.qaContent }}</div>
        <h4>AI评价：</h4>
        <div style="white-space:pre-wrap;line-height:1.8">{{ detail.aiFeedback }}</div>
        <p style="margin-top:12px;font-weight:bold">评分：{{ detail.score }} 分</p>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { aiInterviewStart, aiInterviewSubmit, aiInterviewRecords, aiInterviewDetail } from '../../api'

const step = ref('start')
const jobTitle = ref('')
const loading = ref(false)
const submitting = ref(false)
const questions = ref([])
const answers = ref([])
const feedback = ref('')
const score = ref(0)
const recordId = ref(0)
const records = ref([])
const dialogVisible = ref(false)
const detail = ref(null)

const handleStart = async () => {
  if (!jobTitle.value.trim()) return ElMessage.warning('请输入目标职位')
  loading.value = true
  try {
    const res = await aiInterviewStart({ jobTitle: jobTitle.value })
    const data = res.data
    recordId.value = data.id
    // 从qaContent解析题目（每行一个问题）
    const lines = (data.qaContent || '').split('\n').filter(l => l.trim())
    questions.value = lines
    answers.value = new Array(lines.length).fill('')
    step.value = 'answering'
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (answers.value.some(a => !a.trim())) return ElMessage.warning('请回答所有问题')
  submitting.value = true
  try {
    const res = await aiInterviewSubmit({ recordId: recordId.value, answers: answers.value })
    feedback.value = res.data.feedback || res.data.aiFeedback || ''
    score.value = res.data.score || 0
    step.value = 'result'
    loadRecords()
  } finally {
    submitting.value = false
  }
}

const viewDetail = async (row) => {
  try {
    const res = await aiInterviewDetail(row.id)
    detail.value = res.data
    dialogVisible.value = true
  } catch (e) {}
}

const loadRecords = async () => {
  try {
    const res = await aiInterviewRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

onMounted(loadRecords)
</script>

<style scoped>
.result-box {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 8px;
  padding: 16px;
}
</style>
