<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入用户名查询" style="width: 200px" v-model="userName"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <!--  <el-table-column prop="userId" label="用户ID"></el-table-column> -->
        <el-table-column prop="userName" label="用户名称"></el-table-column>
        <el-table-column prop="status" label="审核状态">
          <template v-slot="scope">
            <el-tag type="info" v-if="scope.row.status === '待审核'">待审核</el-tag>
            <el-tag type="success" v-if="scope.row.status === '通过'">通过</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '拒绝'">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="sex" label="性别"></el-table-column>
        <el-table-column prop="birth" label="生日"></el-table-column>
        <el-table-column prop="cardImg" label="身份证照片">
          <template v-slot="scope">
            <el-image v-if="scope.row.cardImg" style="width: 50px" :src="scope.row.cardImg"
              :preview-src-list="[scope.row.cardImg]"></el-image>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="info" label="个人介绍" ></el-table-column> -->
        <el-table-column prop="info" label="个人介绍" width="300px"></el-table-column>
        <el-table-column prop="time" label="创建时间"></el-table-column>
        <el-table-column prop="cardNo" label="身份证号码"></el-table-column>
        <!-- 修改审核操作列 -->
        <el-table-column label="审核" align="center" width="180">
          <template v-slot="scope">
            <!-- 当状态不是待审核时禁用按钮 -->
            <el-button size="mini" type="success" plain @click="audit(scope.row, '通过')"
              :disabled="scope.row.status !== '待审核'">
              通过
            </el-button>
            <el-button size="mini" type="danger" plain @click="audit(scope.row, '拒绝')"
              :disabled="scope.row.status !== '待审核'">
              拒绝
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="100">
          <template v-slot="scope">
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="救助员申请" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="用户ID"></el-input>
        </el-form-item>
        <el-form-item label="审核状态" prop="status">
          <el-input v-model="form.status" placeholder="审核状态"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-input v-model="form.sex" placeholder="性别"></el-input>
        </el-form-item>
        <el-form-item label="生日" prop="birth">
          <el-input v-model="form.birth" placeholder="生日"></el-input>
        </el-form-item>
        <el-form-item label="身份证照片" prop="cardImg">
          <el-input v-model="form.cardImg" placeholder="身份证照片"></el-input>
        </el-form-item>
        <el-form-item label="个人介绍" prop="info">
          <el-input v-model="form.info" placeholder="个人介绍"></el-input>
        </el-form-item>
        <el-form-item label="创建时间" prop="time">
          <el-input v-model="form.time" placeholder="创建时间"></el-input>
        </el-form-item>
        <el-form-item label="身份证号码" prop="cardNo">
          <el-input v-model="form.cardNo" placeholder="身份证号码"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  userName: "VolApply",
  data() {
    return {
      tableData: [], // 所有的数据
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      userName: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      rules: {},
      ids: [],
      statusIcon: {
        待审核: "el-icon-time",
        通过: "el-icon-success",
        拒绝: "el-icon-error",
      },
    };
  },
  created() {
    this.load(1);
  },
  methods: {
    // 保持原有audit方法不变
    audit(row, status) {
      this.form = JSON.parse(JSON.stringify(row));
      this.form.status = status;

      // 添加确认对话框
      this.$confirm(`确定要${status}该申请吗？`, "审核确认", {
        type: "warning",
      })
        .then(() => {
          this.$request.put("/volApply/audit", this.form).then((res) => {
            if (res.code === "200") {
              this.$message.success(`已${status}申请`);
              this.load(1);
              // 自动禁用已处理的行（通过数据重新加载实现）
            } else {
              this.$message.error(res.msg);
            }
          });
        })
        .catch(() => {
          this.$message.info("已取消操作");
        });
    },
    handleAdd() {
      // 新增数据
      this.form = {}; // 新增数据的时候清空数据
      this.fromVisible = true; // 打开弹窗
    },
    handleEdit(row) {
      // 编辑数据
      this.form = JSON.parse(JSON.stringify(row)); // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true; // 打开弹窗
    },
    save() {
      // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? "/volApply/update" : "/volApply/add",
            method: this.form.id ? "PUT" : "POST",
            data: this.form,
          }).then((res) => {
            if (res.code === "200") {
              // 表示成功保存
              this.$message.success("保存成功");
              this.load(1);
              this.fromVisible = false;
            } else {
              this.$message.error(res.msg); // 弹出错误的信息
            }
          });
        }
      });
    },
    del(id) {
      // 单个删除
      this.$confirm("您确定删除吗？", "确认删除", { type: "warning" })
        .then((response) => {
          this.$request.delete("/volApply/delete/" + id).then((res) => {
            if (res.code === "200") {
              // 表示操作成功
              this.$message.success("操作成功");
              this.load(1);
            } else {
              this.$message.error(res.msg); // 弹出错误的信息
            }
          });
        })
        .catch(() => {});
    },
    handleSelectionChange(rows) {
      // 当前选中的所有的行数据
      this.ids = rows.map((v) => v.id);
    },
    delBatch() {
      // 批量删除
      if (!this.ids.length) {
        this.$message.warning("请选择数据");
        return;
      }
      this.$confirm("您确定批量删除这些数据吗？", "确认删除", {
        type: "warning",
      })
        .then((response) => {
          this.$request
            .delete("/volApply/delete/batch", { data: this.ids })
            .then((res) => {
              if (res.code === "200") {
                // 表示操作成功
                this.$message.success("操作成功");
                this.load(1);
              } else {
                this.$message.error(res.msg); // 弹出错误的信息
              }
            });
        })
        .catch(() => {});
    },
    load(pageNum) {
      // 分页查询
      if (pageNum) this.pageNum = pageNum;
      console.log("查询参数：", {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        userName: this.userName,
      });
      this.$request
        .get("/volApply/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            userName: this.userName,
          },
        })
        .then((res) => {
          if (res.code === "200") {
            this.tableData = res.data?.list;
            this.total = res.data?.total;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
    reset() {
      this.userName = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
  },
};
</script>

<style scoped></style>