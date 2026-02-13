<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #304156">
      <div class="logo">企业端</div>
      <el-menu :default-active="route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/company/positions"><el-icon><Briefcase /></el-icon><span>职位管理</span></el-menu-item>
        <el-menu-item index="/company/applications"><el-icon><Document /></el-icon><span>收到的简历</span></el-menu-item>
        <el-menu-item index="/company/interviews"><el-icon><ChatDotRound /></el-icon><span>面试管理</span></el-menu-item>
        <el-menu-item index="/company/profile"><el-icon><OfficeBuilding /></el-icon><span>企业档案</span></el-menu-item>
        <el-menu-item index="/company/notifications">
          <el-icon><Bell /></el-icon>
          <span>通知中心</span>
          <el-badge v-if="unreadCount > 0" :value="unreadCount" class="badge" />
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;display:flex;align-items:center;justify-content:flex-end;box-shadow:0 1px 4px rgba(0,21,41,.08)">
        <span style="margin-right:16px">{{ userStore.username }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUnreadCount } from '../api'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

onMounted(async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data
  } catch (e) {}
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background-color: #263445;
}
.badge { margin-left: 8px; }
</style>
