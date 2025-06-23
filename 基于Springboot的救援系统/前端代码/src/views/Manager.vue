<template>
  <div class="manager-container">
    <!--  头部  -->
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/logo.png" />
        <div class="title">守护之心救援系统</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <!-- 根据 isSidebarVisible 的值切换按钮图标 -->
        <el-button @click="toggleSidebar" :icon="isSidebarVisible ? 'el-icon-arrow-left' : 'el-icon-arrow-right'"></el-button>
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div>{{ user.name || '管理员' }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="$router.push('/userVolApply')"
              v-if="user.role === 'USER'">救助员申请</el-dropdown-item>
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!--  主体  -->
    <div class="manager-main">
      <!--  侧边栏  -->
      <div class="manager-main-left" :class="{ 'sidebar-expanded': isSidebarVisible }">
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">系统首页</span>
          </el-menu-item>
          <el-menu-item index="/dashboard" v-if="user.role === 'ADMIN'">
            <i class="el-icon-s-marketing"></i>
            <span slot="title">数据统计</span>
          </el-menu-item>
          <el-submenu index="info">
            <template slot="title">
              <i class="el-icon-menu"></i><span>信息管理</span>
            </template>
            <el-menu-item index="/help">救援信息</el-menu-item>
            <el-menu-item index="/records" v-if="user.role !== 'USER'">救援记录</el-menu-item>
            <el-menu-item index="/map" v-if="user.role === 'VOLUNTEER'">地图救援</el-menu-item>
            <el-menu-item index="/notice" v-if="user.role === 'ADMIN'">公告信息</el-menu-item>
            <el-menu-item index="/knowledge" v-if="user.role === 'VOLUNTEER'">救援科普</el-menu-item>
            <el-menu-item index="/volApply" v-if="user.role === 'ADMIN'">救助员申请</el-menu-item>
            <el-menu-item index="/donate">物资捐赠</el-menu-item>
            <el-menu-item index="/comment" v-if="user.role === 'ADMIN'">留言信息</el-menu-item>
            <el-menu-item index="/userComment" v-if="user.role !== 'VOLUNTEER'">在线留言</el-menu-item>
            <el-menu-item index="/trainingrecords" v-if="user.role === 'VOLUNTEER'">培训记录</el-menu-item>
          </el-submenu>

          <el-submenu index="user" v-if="user.role === 'ADMIN'">
            <template slot="title">
              <i class="el-icon-menu"></i><span>用户管理</span>
            </template>
            <el-menu-item index="/admin">管理员信息</el-menu-item>
            <el-menu-item index="/user">普通用户信息</el-menu-item>
            <el-menu-item index="/volunteer">救助员信息</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!--  数据表格  -->
      <div class="manager-main-right" :class="{ 'centered':!isSidebarVisible }">
        <router-view @update:user="updateUser" />
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Manager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      // 控制侧边栏显示状态的变量，初始值为 false 表示默认隐藏
      isSidebarVisible: false
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {
    updateUser() {
      // 重新获取下用户的最新信息
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')
    },
    goToPerson() {
      console.log('正在跳转到个人信息，用户角色: ' + this.user.role); // 输出用户角色
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      } else if (this.user.role === 'USER') {
        this.$router.push('/userPerson')
      } else {
        this.$router.push('/volPerson')
      }
    },
    logout() {
      localStorage.removeItem('xm-user')
      this.$router.push('/login')
    },
    // 切换侧边栏显示状态的方法
    toggleSidebar() {
      this.isSidebarVisible = !this.isSidebarVisible;
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/manager.css";

.manager-main {
  display: flex;
  height: calc(100vh - 60px); /* 减去头部高度 */
}

.manager-main-left {
  width: 200px;
  height: 100%;
  position: relative;
  left: -200px;
  transition: left 0.3s ease;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  z-index: 1000;
}

.sidebar-expanded {
  left: 0;
}

.manager-main-right {
  flex: 1;
  padding: 20px;
  transition: margin-left 0.3s ease;
}

.centered {
  margin: 0 auto;
  max-width: 80%; /* 可根据需要调整最大宽度 */
}
</style>