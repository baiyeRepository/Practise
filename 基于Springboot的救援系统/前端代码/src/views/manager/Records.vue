<template>
  <!-- 整体页面容器 -->
  <div>
    <!-- 搜索区域，包含输入框和查询、重置按钮 -->
    <div class="search">
      <el-input placeholder="请输入救助员姓名查询" style="width: 200px" v-model="volName"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <!-- 操作区域，只有管理员角色可见，包含批量删除按钮 -->
    <div class="operation" v-if="user.role === 'ADMIN'">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <!-- 表格区域，包含表格和分页组件 -->
    <div class="table">
      <!-- 数据表格，展示记录数据 -->
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <!-- 选择列，只有管理员角色可见 -->
        <el-table-column type="selection" width="55" align="center" v-if="user.role === 'ADMIN'"></el-table-column>
        <!-- 序号列 -->
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <!-- 救助员名称列 -->
        <el-table-column prop="volName" label="救助员名称"></el-table-column>
        <!-- 状态列，根据不同状态显示不同颜色的标签 -->
        <el-table-column prop="status" label="状态">
          <template v-slot="scope">
            <el-tag type="warn" v-if="scope.row.status === '待救援'">待救援</el-tag>
            <el-tag type="info" v-if="scope.row.status === '救援中'">救援中</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已救援'">已救援</el-tag>
          </template>
        </el-table-column>
        <!-- 出勤时间列 -->
        <el-table-column prop="time" label="出勤时间"></el-table-column>
        <!-- 事件详情列，点击按钮可查看详情 -->
        <el-table-column prop="helpId" label="事件详情">
          <template v-slot="scope">
            <el-button @click="view(scope.row.helpId)">查看详情</el-button>
          </template>
        </el-table-column>
        <!-- 备注列 -->
        <el-table-column prop="comment" label="备注"></el-table-column>
        <!-- 操作列，根据不同角色和状态显示不同操作按钮 -->
        <el-table-column label="操作" align="center" width="220">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="changeStatus(scope.row, '救援中')"
              v-if="scope.row.status === '待救援' && user.role === 'VOLUNTEER' && scope.row.volName === user.name">应答</el-button>
            <el-button size="mini" type="success" plain @click="changeStatus(scope.row, '已救援')"
              v-if="scope.row.status === '救援中' && user.role === 'VOLUNTEER'">完成救援</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)"
              v-if="user.role === 'ADMIN'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>
    <!-- 救援信息对话框，显示救援相关信息 -->
    <el-dialog title="救援信息" :visible.sync="fromVisible1" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="120px">
        <!-- 事故名称 -->
        <el-form-item label="事故名称">
          <div>
            {{ help.name }}
          </div>
        </el-form-item>
        <!-- 事故内容 -->
        <el-form-item label="事故内容">
          <div>
            {{ help.content }}
          </div>
        </el-form-item>
        <!-- 现场照片 -->
        <el-form-item label="现场照片">
          <div>
            <img :src="help.img" style="width: 200px" />
          </div>
        </el-form-item>
        <!-- 事故地点 -->
        <el-form-item label="事故地点">
          <div>
            {{ help.location }}
          </div>
        </el-form-item>
        <!-- 求救人名称 -->
        <el-form-item label="求救人名称">
          <div>
            {{ help.userName }}
          </div>
        </el-form-item>
        <!-- 求救电话 -->
        <el-form-item label="求救电话">
          <div>
            {{ help.phone }}
          </div>
        </el-form-item>
        <!-- 事故时间 -->
        <el-form-item label="事故时间">
          <div>
            {{ help.time }}
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 出勤信息对话框，用于新增或编辑出勤信息 -->
    <el-dialog title="出勤信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <!-- 救助员ID -->
        <el-form-item label="救助员ID" prop="volId">
          <el-input v-model="form.volId" placeholder="救助员ID"></el-input>
        </el-form-item>
        <!-- 状态 -->
        <el-form-item label="状态" prop="status">
          <el-input v-model="form.status" placeholder="状态"></el-input>
        </el-form-item>
        <!-- 出勤时间 -->
        <el-form-item label="发布时间" prop="time">
          <el-input v-model="form.time" placeholder="发布时间"></el-input>
        </el-form-item>
        <!-- 事件ID -->
        <el-form-item label="事件ID" prop="helpId">
          <el-input v-model="form.helpId" placeholder="事件ID"></el-input>
        </el-form-item>
        <!-- 备注 -->
        <el-form-item label="备注" prop="comment">
          <el-input v-model="form.comment" placeholder="备注"></el-input>
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
  // 组件名称
  name: "Records",
  // 组件数据
  data() {
    return {
      tableData: [], // 所有的数据
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      volName: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      rules: {},
      ids: [],
      help: {},
      fromVisible1: false,
    };
  },
  // 组件创建时执行的钩子函数
  created() {
    this.load(1);
  },
  methods: {
    // 查看救援详情
    view(helpId) {
      this.$request.get("/help/selectById/" + helpId).then((res) => {
        this.help = res.data || {};
      });
      this.fromVisible1 = true;
    },
    // 更改记录状态
    changeStatus(row, status) {
      this.$confirm("您确认吗？", "确认操作", { type: "info" })
        .then((res) => {
          row.status = status;
          this.$request.put("records/update", row).then((res) => {
            if (res.code === "200") {
              // 根据状态显示不同提示信息
              if (status === '已救援') {
                this.$message.success("救援成功");
              } else {
                this.$message.success("出勤成功");
              }
              this.load(1);
            } else {
              this.$message.error(res.msg); // 弹出错误的信息
            }
          });
        })
        .catch(() => { });
    },
    // 新增数据
    handleAdd() {
      // 新增数据
      this.form = {}; // 新增数据的时候清空数据
      this.fromVisible = true; // 打开弹窗
    },
    // 编辑数据
    handleEdit(row) {
      // 编辑数据
      this.form = JSON.parse(JSON.stringify(row)); // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true; // 打开弹窗
    },
    // 保存数据
    save() {
      // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? "/records/update" : "/records/add",
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
    // 删除单个记录
    del(id) {
      // 单个删除
      this.$confirm("您确定删除吗？", "确认删除", { type: "warning" })
        .then((response) => {
          this.$request.delete("/records/delete/" + id).then((res) => {
            if (res.code === "200") {
              // 表示操作成功
              this.$message.success("操作成功");
              this.load(1);
            } else {
              this.$message.error(res.msg); // 弹出错误的信息
            }
          });
        })
        .catch(() => { });
    },
    // 选择行数据时触发
    handleSelectionChange(rows) {
      // 当前选中的所有的行数据
      this.ids = rows.map((v) => v.id);
    },
    // 批量删除记录
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
            .delete("/records/delete/batch", { data: this.ids })
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
        .catch(() => { });
    },
    // 分页查询记录
    load(pageNum) {
      // 分页查询
      if (pageNum) this.pageNum = pageNum;
      console.log('请求参数:', {
        pageNum: this.pageNum,
        pageSize: this.pageSize,
        volName: this.volName
      });
      this.$request
        .get("/records/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            volName: this.volName,
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
    // 重置查询条件
    reset() {
      this.volName = null;
      this.load(1);
    },
    // 分页组件页码改变时触发
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
  },
};
</script>

<style scoped></style>