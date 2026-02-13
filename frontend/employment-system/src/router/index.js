import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'Login', component: () => import('../views/Login.vue') },
    { path: '/register', name: 'Register', component: () => import('../views/Register.vue') },
    {
      path: '/student',
      component: () => import('../layout/StudentLayout.vue'),
      meta: { role: 'student' },
      children: [
        { path: '', redirect: '/student/jobs' },
        { path: 'profile', name: 'StudentProfile', component: () => import('../views/student/Profile.vue') },
        { path: 'resume', name: 'StudentResume', component: () => import('../views/student/Resume.vue') },
        { path: 'jobs', name: 'StudentJobs', component: () => import('../views/student/Jobs.vue') },
        { path: 'applications', name: 'StudentApplications', component: () => import('../views/student/Applications.vue') },
        { path: 'interviews', name: 'StudentInterviews', component: () => import('../views/student/Interviews.vue') },
        { path: 'ai-resume', name: 'AiResume', component: () => import('../views/student/AiResume.vue') },
        { path: 'ai-interview', name: 'AiInterview', component: () => import('../views/student/AiInterview.vue') },
        { path: 'ai-career', name: 'AiCareer', component: () => import('../views/student/AiCareer.vue') },
        { path: 'notifications', name: 'StudentNotifications', component: () => import('../views/student/Notifications.vue') }
      ]
    },
    {
      path: '/company',
      component: () => import('../layout/CompanyLayout.vue'),
      meta: { role: 'company' },
      children: [
        { path: '', redirect: '/company/positions' },
        { path: 'profile', name: 'CompanyProfile', component: () => import('../views/company/Profile.vue') },
        { path: 'positions', name: 'CompanyPositions', component: () => import('../views/company/Positions.vue') },
        { path: 'applications', name: 'CompanyApplications', component: () => import('../views/company/Applications.vue') },
        { path: 'interviews', name: 'CompanyInterviews', component: () => import('../views/company/Interviews.vue') },
        { path: 'notifications', name: 'CompanyNotifications', component: () => import('../views/company/Notifications.vue') }
      ]
    },
    {
      path: '/admin',
      component: () => import('../layout/AdminLayout.vue'),
      meta: { role: 'admin' },
      children: [
        { path: '', redirect: '/admin/companies' },
        { path: 'companies', name: 'AdminCompanies', component: () => import('../views/admin/Companies.vue') },
        { path: 'positions', name: 'AdminPositions', component: () => import('../views/admin/Positions.vue') },
        { path: 'users', name: 'AdminUsers', component: () => import('../views/admin/Users.vue') }
      ]
    },
    { path: '/', redirect: '/login' },
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.path === '/login' || to.path === '/register') {
    if (token && user) {
      next(`/${user.role}`)
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  // 检查角色权限
  if (to.meta.role && to.meta.role !== user?.role) {
    next(`/${user.role}`)
    return
  }

  next()
})

export default router
