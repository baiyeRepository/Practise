<template>
  <div style="width: 50%; margin: 20px auto; padding: 20px; box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);" class="card">
    <div style="text-align: center; margin-bottom: 30px; font-size: 20px">
      救助员申请
    </div>
    <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
      <el-form-item label="电话" prop="phone">
        <el-input v-model="form.phone" placeholder="电话"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio label="男"></el-radio>
          <el-radio label="女"></el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="生日" prop="birth">
        <el-date-picker format="yyyy-MM-dd" value-format="yyyy-MM-dd" v-model="form.birth" style="width: 100%"
          :picker-options="pickerOptions">
        </el-date-picker>
        <span v-if="form.cardNo" style="margin-left: 10px; color: #666">
        </span>
      </el-form-item>
      <el-form-item label="身份证号码" prop="cardNo">
        <el-input v-model="form.cardNo" placeholder="身份证号码" @blur="validateIDCard"></el-input>
      </el-form-item>
      <el-form-item label="身份证照片" prop="cardImg">
        <el-upload :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" list-type="picture"
          :on-success="handleCardImgSuccess">
          <el-button type="primary">上传身份证照片</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="个人介绍" prop="info">
        <el-input v-model="form.info" placeholder="个人介绍" type="textarea"></el-input>
      </el-form-item>
      <el-form-item label="审核状态" prop="status" v-if="form.status">
        <div v-if="form.status === '拒绝' && isBlockedToday">
          <strong style="color: red">拒绝</strong>
          <div class="mt-2 text-danger">
            <i class="el-icon-warning-outline"></i> 
            您的申请已被拒绝，
            同一天内不可重复提交
          </div>
        </div>
        <strong style="color: orange" v-else-if="form.status === '待审核'">待审核</strong>
        <strong style="color: #3fa50c" v-else-if="form.status === '通过'">通过</strong>
        <strong style="color: red" v-else>拒绝</strong>
      </el-form-item>
      <div style="text-align: center">
        <el-button @click="submit" size="medium" type="primary" :disabled="isBlockedToday">
          {{ isBlockedToday ? '今日不可提交' : '提交信息' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "UserVolApply",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      form: {},
      rules: {
        phone: [
          { required: true, message: "请输入电话", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" },
        ],
        sex: [{ required: true, message: "请选择性别", trigger: "change" }],
        birth: [{ required: true, message: "请选择生日", trigger: "change" }],
        cardNo: [
          { required: true, message: "请输入身份证号码", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "请输入正确的身份证号码", trigger: "blur" },
          { validator: this.validateIDCard, trigger: 'blur' }
        ],
        cardImg: [{ required: true, message: "请上传身份证照片", trigger: "change" }],
        info: [{ required: true, message: "请输入个人介绍", trigger: "blur" }],
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
    };
  },
  computed: {
    // 判断今天是否被拒绝
    isBlockedToday() {
      if (this.form.status !== '拒绝') return false;
      
      // 获取今天的日期字符串 (YYYY-MM-DD，使用本地时间)
      const today = this.getTodayDate();
      
      // 从后端返回的time字段获取申请日期（使用本地时间）
      const applyDate = this.getDateFromTime(this.form.time);
      
      // 输出日期信息到控制台
      console.log(`当前日期: ${today}`);
      console.log(`申请(拒绝)日期: ${applyDate}`);
      console.log(`是否禁止提交: ${today === applyDate}`);
      
      return today === applyDate;
    }
  },
  watch: {
    'form.cardNo'(newVal) {
      if (newVal && (newVal.length === 15 || newVal.length === 18)) {
        this.autoFillBirthFromID()
      }
    }
  },
  created() {
    this.load();
  },
  methods: {
    // 获取今天的日期字符串 (YYYY-MM-DD，使用本地时间)
    getTodayDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    // 从time字段提取日期部分（使用本地时间）
    getDateFromTime(time) {
      if (!time) return '';
      const date = new Date(time);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },
    
    handleCardImgSuccess(response) {
      this.form.cardImg = response.data;
    },
    
    load() {
      this.$request
        .get("/volApply/selectByUserId/" + this.user.id)
        .then((res) => {
          this.form = res.data || {};
          console.log('加载申请信息:', this.form);
        });
    },
    
    // 身份证校验方法
    validateIDCard(rule, value, callback) {
      if (!value) {
        callback();
        return;
      }

      // 基本格式验证
      if (!/(^\d{15}$)|(^\d{17}(\d|X|x)$)/.test(value)) {
        callback(new Error('身份证格式不正确'));
        return;
      }

      // 校验码验证（简化版）
      if (value.length === 18 && !this.checkIDCard(value)) {
        callback(new Error('身份证校验码不正确'));
        return;
      }

      callback();
    },
    
    // 校验码验证算法
    checkIDCard(id) {
      if (id.length !== 18) return true;
      const arr = id.split('');
      const factor = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
      const parity = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'];
      let sum = 0;

      for (let i = 0; i < 17; i++) {
        sum += parseInt(arr[i]) * factor[i];
      }

      return parity[sum % 11].toUpperCase() === arr[17].toUpperCase();
    },
    
    // 自动填充生日
    autoFillBirthFromID() {
      const idCard = this.form.cardNo;
      let birthStr = '';

      if (idCard.length === 18) {
        birthStr = idCard.substr(6, 8);
      } else if (idCard.length === 15) {
        birthStr = '19' + idCard.substr(6, 6);
      }

      if (birthStr) {
        const year = birthStr.substr(0, 4);
        const month = birthStr.substr(4, 2);
        const day = birthStr.substr(6, 2);

        if (this.isValidDate(year, month, day)) {
          this.form.birth = `${year}-${month}-${day}`;
        }
      }
    },
    
    // 日期有效性验证
    isValidDate(year, month, day) {
      const date = new Date(year, month - 1, day);
      return date.getFullYear() == year &&
        (date.getMonth() + 1) == month &&
        date.getDate() == day;
    },
    
    submit() {
      // 检查今天是否被拒绝
      if (this.isBlockedToday) {
        this.$message({
          type: 'error',
          message: `您的申请已被拒绝，同一天内不可重复提交`,
          duration: 5000 // 延长提示显示时间
        });
        return;
      }
      
      this.$refs.formRef.validate((valid) => {
        if (!valid) return;

        // 验证身份证与生日一致性
        if (this.form.cardNo && this.form.birth) {
          const idBirth = this.getBirthFromID(this.form.cardNo);
          const inputBirth = this.form.birth.replace(/-/g, '');

          if (idBirth !== inputBirth) {
            this.$message.error('身份证号码与出生日期不匹配');
            return;
          }
        }

        // 年龄验证
        const birthDate = new Date(this.form.birth);
        if (isNaN(birthDate.getTime())) {
          this.$message.error("请选择正确的生日日期");
          return;
        }

        const currentDate = new Date();
        let age = currentDate.getFullYear() - birthDate.getFullYear();
        const monthDiff = currentDate.getMonth() - birthDate.getMonth();
        if (monthDiff < 0 || (monthDiff === 0 && currentDate.getDate() < birthDate.getDate())) {
          age--;
        }

        if (age < 18) {
          this.$message.error("您未满18岁，暂不能申请");
          return;
        }

        // 提交数据
        this.$request.post("/volApply/add", this.form).then((res) => {
          if (res.code === "200") {
            this.$message.success("提交成功");
            this.load();
          } else {
            this.$message.error(res.msg || "提交失败");
          }
        }).catch(err => {
          console.error(err);
          this.$message.error("请求失败，请检查网络");
        });
      });
    },
    
    // 从身份证提取生日
    getBirthFromID(idCard) {
      let birthStr = '';
      if (idCard.length === 18) {
        birthStr = idCard.substr(6, 8);
      } else if (idCard.length === 15) {
        birthStr = '19' + idCard.substr(6, 6);
      }
      return birthStr;
    }
  }
};
</script>

<style scoped>
.card {
  margin: 20px auto;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.mt-2 {
  margin-top: 8px;
}
.text-danger {
  color: #f56c6c;
}
.font-weight-bold {
  font-weight: bold;
}
</style>