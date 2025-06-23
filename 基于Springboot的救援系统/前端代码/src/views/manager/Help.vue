<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入救援事项查询" style="width: 200px" v-model="name"></el-input>
      <el-select style="width: 200px; margin-left: 10px" v-model="status">
        <el-option value="待处理"></el-option>
        <el-option value="待出勤"></el-option>
        <el-option value="进行中"></el-option>
        <el-option value="已解决"></el-option>
      </el-select>
    
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation" v-if="user.role === 'USER' || user.role === 'ADMIN'">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'USER'">我要救援</el-button>
      <el-button type="danger" plain @click="delBatch" v-if="user.role === 'ADMIN'">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="救援事项"></el-table-column>
        <el-table-column prop="content" label="救援内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="救援类型"></el-table-column>
        <el-table-column prop="img" label="现场图片">
          <template v-slot="scope">
            <el-image v-if="scope.row.img" style="width: 100px" :src="scope.row.img"
              :preview-src-list="[scope.row.img]"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="事故地点"></el-table-column>
        <el-table-column prop="longitude" label="经度"></el-table-column>
        <el-table-column prop="latitude" label="纬度"></el-table-column>
        <el-table-column prop="time" label="发布时间"></el-table-column>
        <el-table-column prop="userName" label="求助者名称"></el-table-column>
        <el-table-column prop="phone" label="联系方式"></el-table-column>
        <el-table-column prop="status" label="处理状态">
          <template v-slot="scope">
            <el-tag type="warning" v-if="scope.row.status === '待处理'">待处理</el-tag>
            <el-tag type="info" v-if="scope.row.status === '待出勤'">待出勤</el-tag>
            <!-- 新增此行 -->
            <el-tag type="info" v-if="scope.row.status === '进行中'">进行中</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已解决'">已解决</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="recordId" label="出勤记录" width="200">
          <template v-slot="scope">
            <div v-if="scope.row.recordId">
              <div>出勤人员：{{ scope.row.records?.volName }}</div>
              <div v-if="
                scope.row.status === '进行中' || scope.row.status === '已解决'
              ">
                <div>出勤时间：{{ scope.row.records?.time }}</div>
                <div>联系电话：{{ scope.row.records?.volPhone }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220">
          <template v-slot="scope">
            <el-button size="mini" type="primary" plain @click="handleVol(scope.row)"
              v-if="user.role === 'ADMIN' && scope.row.status === '待处理'">出勤</el-button>
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)"
              v-if="user.role === 'USER' && scope.row.status === '待处理'">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)" v-if="
              user.role === 'ADMIN' ||
              (user.role === 'USER' &&
                (scope.row.status === '待处理' ||
                  scope.row.status === '已解决'))
            ">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="救援信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="救援事项" prop="name">
          <el-input v-model="form.name" placeholder="救援事项"></el-input>
        </el-form-item>
        <el-form-item label="救援内容" prop="content">
          <el-input type="textarea" v-model="form.content" placeholder="救援内容"></el-input>
        </el-form-item>
        <el-form-item label="救援类型" prop="type">
          <el-select style="width: 100%" v-model="form.type">
            <el-option v-for="item in [
              '火灾',
              '落水',
              '燃气中毒',
              '意外伤害',
              '交通事故',
              '其他',
            ]" :key="item" :value="item" :label="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="现场图片" prop="img">
          <el-upload :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" list-type="picture"
            :on-success="handleImgSuccess">
            <el-button type="primary">上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="事故地点" prop="location">
          <el-input v-model="form.location" placeholder="事故地点"></el-input>
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <div>
            <el-input v-model="form.longitude" placeholder="经度" style="width: 85%"></el-input>
            <a href="https://api.map.baidu.com/lbsapi/getpoint/index.html" target="_blank"
              style="color: #409eff; margin-left: 5px">选择坐标</a>
          </div>
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input v-model="form.latitude" placeholder="纬度"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="form.phone" placeholder="联系方式"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="出勤信息" :visible.sync="fromVisible1" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="救助员" prop="volId">
          <el-select style="width: 100%" v-model="form.volId">
            <el-option v-for="item in freeVols" :key="item.id" :value="item.id"
              :label="item.name + (item.free === '是' ? '（空闲）' : '（忙碌）')"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input type="textarea" v-model="form.comment" placeholder="备注"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="saveRecord">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: "Help",
  data() {
    return {
      tableData: [], // 所有的数据
      pageNum: 1, // 当前的页码
      pageSize: 10, // 每页显示的个数
      total: 0,
      name: null,
      status: null,
      time: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      rules: {
        name: [
          { required: true, message: "救援事项不能为空", trigger: "blur" },
        ],
        content: [
          { required: true, message: "救援内容不能为空", trigger: "blur" },
        ],
        type: [
          { required: true, message: "请选择救援类型", trigger: "change" },
        ],
        phone: [
          { required: true, message: "联系方式不能为空", trigger: "blur" },
          {
            pattern: /^\d{11}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
        comment: [
          { required: true, message: "请填写备注", trigger: "blur" },
        ],
        volId: [
          {
            validator: (rule, value, callback) => {
              const selectedVol = this.vols.find(vol => vol.id === value);
              if (selectedVol && selectedVol.free !== '是') {
                callback(new Error('该救助员正忙碌，无法分配任务'));
              } else {
                callback();
              }
            },
            trigger: 'change'
          }
        ]
      },
      ids: [],
      fromVisible1: false,
      vols: [],
      freeVols: [], // 新增空闲救助员数组
    };
  },
  created() {
    this.load(1);
  },
  methods: {
    saveRecord() {
      this.$refs.formRef.validate((valid) => {
        if (!valid) return;
        this.$request.post("/records/add", this.form).then((res) => {
          if (res.code === "200") {
            // 表示成功保存
            this.$message.success("出勤成功");
            this.load(1);
            this.fromVisible1 = false;
          } else {
            this.$message.error(res.msg); // 弹出错误的信息
          }
        });
      });
    },
    handleVol(row) {
      this.form = { helpId: row.id };
      // 获取发布救助任务的用户id
      const requesterId = row.userId;
      // 获取求助者名称
      const requesterName = row.userName;
      // 获取所有救助员并过滤出空闲的救助员
      this.$request
        .get("/volunteer/selectAll")
        .then((res) => {
          this.vols = res.data || [];
          // 过滤出空闲的救助员，并排除发布救助任务的用户以及名称和求助者相同的人员
          this.freeVols = this.vols.filter((vol) => {
            return (
              vol.id !== requesterId &&
              vol.name !== requesterName
            );
          });
        });
      this.fromVisible1 = true;
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
            url: this.form.id ? "/help/update" : "/help/add",
            method: this.form.id ? "PUT" : "POST",
            data: this.form,
          }).then((res) => {
            if (res.code === "200") {
              // 表示成功保存
              this.$message.success("请求成功");
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
          this.$request.delete("/help/delete/" + id).then((res) => {
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
            .delete("/help/delete/batch", { data: this.ids })
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
    load(pageNum) {
      // 分页查询
      if (pageNum) this.pageNum = pageNum;
      // 处理时间参数，添加默认时间 00:00:00
      let time = this.time ? `${this.time} 00:00:00` : null;
      this.$request
        .get("/help/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
            name: this.name,
            status: this.status,
            timeParam: this.time,
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
      this.name = null;
      this.status = null;
      this.time = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    handleImgSuccess(response, file, fileList) {
      this.form.img = response.data;
    },
  },
};
</script>

<style scoped></style>