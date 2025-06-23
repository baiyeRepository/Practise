<template>
  <div class="container">
    <div class="login-form-container">
      <div class="login-title">
        欢迎使用守护之心救援系统
      </div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input size="medium" prefix-icon="el-icon-user" placeholder="请输入用户名" v-model="form.username"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password
            v-model="form.password"></el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-select size="medium" v-model="form.role" style="width: 100%">
            <el-option value="USER" label="用户"></el-option>
            <el-option value="ADMIN" label="管理员"></el-option>
            <el-option value="VOLUNTEER" label="救助员"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <div class="code-input-container">
            <el-input size="medium" style="flex: 1" v-model="form.code" placeholder="请输入验证码"></el-input>
            <div class="code-img-container">
              <img @click="getCode()" :src="codeImg" style="width: 100%; height: 35px" />
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button size="medium" type="primary" style="width: 100%" @click="login">登 录</el-button>
        </el-form-item>
        <div class="register-link-container">
          <div></div>
          <div>
            未注册？请 <a href="/register">注册</a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      codeImg: "",
      uuid: "",
      form: { role: "ADMIN" },
      rules: {
        username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getCode();
  },
  methods: {
    getCode() {
      this.uuid = generateUUID();
      this.codeImg = this.$baseUrl + "/validateCode?key=" + this.uuid;
    },
    login() {
      this.$refs["formRef"].validate((valid) => {
        if (valid) {
          // 验证通过
          this.form.key = this.uuid;
          this.$request.post("/login", this.form).then((res) => {
            if (res.code === "200") {
              localStorage.setItem("xm-user", JSON.stringify(res.data)); // 存储用户数据
              console.log("获取到的用户数据:", res.data); // 打印获取到的用户数据
              this.$router.push("/"); // 跳转主页
              this.$message.success("登录成功");
            } else {
              this.getCode();
              this.$message.error(res.msg);
            }
          });
        }
      });
    }
  }
};
function generateUUID() {
  let d = new Date().getTime();
  if (window.performance && typeof window.performance.now === "function") {
    d += performance.now(); //use high-precision timer if available
  }
  let uuid = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    let r = (d + Math.random() * 16) % 16 | 0;
    d = Math.floor(d / 16);
    return (c === "x" ? r : (r & 0x3) | 0x8).toString(16);
  });
  return uuid;
}
</script>

<style scoped>
/* 容器样式，设置高度、隐藏溢出内容、添加背景图、居中显示 */
.container {
  height: 100vh;
  min-height: 600px;  /* 设置最小高度保证内容可见 */
  overflow: auto;     /* 允许内容滚动 */
  background-image: url("@/assets/imgs/守护之心2.jpg");
  background-size: cover;  /* 关键修改：完全覆盖容器 */
  background-position: center;  /* 居中显示 */
  background-repeat: no-repeat;  /* 禁止重复 */
  background-attachment: fixed;  /* 固定背景 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

/* 登录表单容器样式，设置宽度、内边距、背景颜色和圆角 */
.login-form-container {
  width: 380px;
  padding: 30px;
  background-color: rgba(255, 255, 255, 0.6);
  border-radius: 5px;
}

/* 登录标题样式，设置文本居中、字体大小、底部外边距和颜色 */
.login-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 30px;
  color: #333;
}

/* 验证码输入容器样式，设置为弹性布局 */
.code-input-container {
  display: flex;
}

/* 验证码图片容器样式，设置弹性增长和左边距 */
.code-img-container {
  flex: 1;
  margin-left: 10px;
}

/* 注册链接容器样式，设置为弹性布局 */
.register-link-container {
  display: flex;
  align-items: center;
}

/* 链接颜色样式 */
a {
  color: #2a60c9;
}
</style>