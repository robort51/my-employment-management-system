<template>
  <el-card>
    <template #header><span>AI简历润色</span></template>

    <el-alert
      :title="hasResumeText ? '已检测到简历识别文本，可直接开始润色。' : '未检测到简历识别文本，请先到「我的简历」上传图片。'"
      :type="hasResumeText ? 'success' : 'warning'"
      :closable="false"
      style="margin-bottom: 16px"
    />

    <el-button type="primary" :loading="loading" :disabled="!hasResumeText" @click="handlePolish">
      一键润色已上传简历
    </el-button>

    <el-divider v-if="result" />
    <div v-if="result" class="result-box">
      <h4>润色结果：</h4>
      <div style="white-space: pre-wrap; line-height: 1.8">{{ result }}</div>
    </div>

    <el-divider />
    <h4 style="margin-bottom: 12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="时间" width="180" />
      <el-table-column prop="originalContent" label="原始内容" show-overflow-tooltip />
      <el-table-column prop="polishedContent" label="润色结果" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { aiResumePolishByResume, aiResumeRecords, getResume } from '../../api'

const result = ref('')
const loading = ref(false)
const records = ref([])
const resumeText = ref('')

const hasResumeText = computed(() => !!resumeText.value.trim())

const handlePolish = async () => {
  loading.value = true
  try {
    const res = await aiResumePolishByResume()
    result.value = res.data?.polishedContent || ''
    loadRecords()
  } finally {
    loading.value = false
  }
}

const loadResume = async () => {
  try {
    const res = await getResume()
    resumeText.value = res.data?.ocrText || res.data?.content || ''
  } catch (e) {
    resumeText.value = ''
  }
}

const loadRecords = async () => {
  try {
    const res = await aiResumeRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

onMounted(async () => {
  await loadResume()
  await loadRecords()
  if (!hasResumeText.value) {
    ElMessage.info('请先在「我的简历」上传简历图片')
  }
})
</script>

<style scoped>
.result-box {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 8px;
  padding: 16px;
}
</style>

