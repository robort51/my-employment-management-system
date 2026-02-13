<template>
  <el-card>
    <template #header><span>AI职业规划</span></template>
    <el-alert title="AI将根据你的个人档案自动生成职业规划建议，请先完善个人档案。" type="info" :closable="false" style="margin-bottom:16px" />
    <el-button type="primary" :loading="loading" @click="handleGenerate">生成职业规划</el-button>
    <div v-if="result" class="result-box" style="margin-top:20px">
      <h4>职业规划建议：</h4>
      <div style="white-space:pre-wrap;line-height:1.8">{{ result }}</div>
    </div>
    <el-divider />
    <h4 style="margin-bottom:12px">历史记录</h4>
    <el-table :data="records" stripe size="small">
      <el-table-column prop="createTime" label="生成时间" width="180" />
      <el-table-column prop="planContent" label="规划内容" show-overflow-tooltip />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { aiCareerGenerate, aiCareerRecords } from '../../api'

const loading = ref(false)
const result = ref('')
const records = ref([])

const handleGenerate = async () => {
  loading.value = true
  try {
    const res = await aiCareerGenerate()
    result.value = res.data
    loadRecords()
  } finally {
    loading.value = false
  }
}

const loadRecords = async () => {
  try {
    const res = await aiCareerRecords({ pageNum: 1, pageSize: 10 })
    records.value = res.data.records || []
  } catch (e) {}
}

onMounted(loadRecords)
</script>

<style scoped>
.result-box {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  border-radius: 8px;
  padding: 16px;
}
</style>
