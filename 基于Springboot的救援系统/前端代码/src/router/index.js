import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    console.error('路由跳转出错:', err)
    return err
  })
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/home',  // 重定向到主页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User.vue') },
      { path: 'userPerson', name: 'UserPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/UserPerson.vue') },
      { path: 'volunteer', name: 'Volunteer', meta: { name: '救助员信息' }, component: () => import('../views/manager/Volunteer') },
      { path: 'volApply', name: 'VolApply', meta: { name: '申请救助' }, component: () => import('../views/manager/VolApply.vue') },
      { path: 'userVolApply', name: 'UserVolApply', meta: { name: '救助员申请' }, component: () => import('../views/manager/UserVolApply.vue') },
      { path: 'volPerson', name: 'VolPerson', meta: { name: '个人信息' }, component: () => import('../views/manager/VolPerson.vue') },
      { path: 'help', name: 'Help', meta: { name: '救援信息' }, component: () => import('../views/manager/Help.vue') },
      { path: 'records', name: 'Records', meta: { name: '救援记录' }, component: () => import('../views/manager/Records.vue') },
      { path: 'map', name: 'Map', meta: { name: '地图救援' }, component: () => import('../views/manager/Map.vue') },
      { path: 'knowledge', name: 'Knowledge', meta: { name: '救援科普' }, component: () => import('../views/manager/Knowledge.vue') },
      { path: 'knowledgeDetail', name: 'KnowledgeDetail', meta: { name: '救援科普详情' }, component: () => import('../views/manager/KnowledgeDetail.vue') },
      { path: 'donate', name: 'Donate', meta: { name: '物资捐赠' }, component: () => import('../views/manager/Donate.vue') },
      { path: 'comment', name: 'Comment', meta: { name: '评论' }, component: () => import('../views/manager/Comment.vue') },
      { path: 'userComment', name: 'UserComment', meta: { name: '在线留言' }, component: () => import('../views/manager/UserComment.vue') },
      { path: 'dashboard', name: 'Dashboard', meta: { name: '数据统计' }, component: () => import('../views/manager/Dashboard.vue') },
      { path: 'trainingrecords', name: 'TrainingRecords', meta: { name: '培训记录' }, component: () => import('../views/manager/TrainingRecords.vue') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

/* 注：不需要前台的项目，可以注释掉该路由守卫
路由守卫 */
//
router.beforeEach((to ,from, next) => {
  let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  if (to.path === '/') {
    if (user.role) {
      if (user.role === 'USER') {
        next('/front/home')
      } 
    } else {
      next('/login')
    }
  } else {
    next()
  }
})

export default router