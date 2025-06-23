<template>
  <div>
    <el-card style="width: 50%">
      <el-form :model="volunteer" label-width="100px" style="padding-right: 50px">
        <div style="margin: 15px; text-align: center">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="volunteer.avatar" :src="volunteer.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>
        <el-form-item label="用户名" prop="volunteername">
          <el-input v-model="volunteer.username" placeholder="用户名" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="volunteer.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="volunteer.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="volunteer.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="birth">
          <el-date-picker format="yyyy-MM-dd" value-format="yyyy-MM-dd"
                          v-model="volunteer.birth" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="volunteer.sex">
            <el-radio label="男"></el-radio>
            <el-radio label="女"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="介绍" prop="info">
          <el-input type="textarea" v-model="volunteer.info" placeholder="介绍"></el-input>
        </el-form-item>
        <el-form-item label="证件照片" prop="cardImg">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: volunteer.token }"
              list-type="picture"
              :on-success="handleCardImgSuccess"
          >
            <el-button type="primary">上传证件照片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="身份证号码" prop="cardNo">
          <el-input v-model="volunteer.cardNo" placeholder="身份证号码"></el-input>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button type="primary" @click="update">保 存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "VolPerson",
  data() {
    return {
      volunteer: JSON.parse(localStorage.getItem('xm-user') || '{}')
    }
  },
  created() {

  },
  methods: {
    handleCardImgSuccess(response) {
      this.volunteer.cardImg = response.data
    },
    update() {
      // 保存当前的用户信息到数据库
      this.$request.put('/volunteer/update', this.volunteer).then(res => {
        if (res.code === '200') {
          // 成功更新
          this.$message.success('保存成功')

          // 更新浏览器缓存里的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.volunteer))

          // 触发父级的数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把volunteer的头像属性换成上传的图片的链接
      this.$set(this.volunteer, 'avatar', response.data)
    },
  }
}
</script>

<style scoped>
/deep/.el-form-item__label {
  font-weight: bold;
}
/deep/.el-upload {
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 50%;
}
/deep/.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border-radius: 50%;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
}
</style>