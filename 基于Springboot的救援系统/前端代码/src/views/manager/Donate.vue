<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入物资类型查询" style="width: 200px" v-model="type"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <!-- 用户显示部分：发起捐赠按钮 -->
      <el-button type="primary" plain @click="handleAdd" v-if="user.role !== ''">发起捐赠</el-button>
      <!-- 管理员显示部分：批量删除按钮 -->
      <el-button type="danger" plain @click="delBatch" v-if="user.role === 'ADMIN'">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <!-- 管理员显示部分：表格选择列 -->
        <el-table-column type="selection" width="55" align="center" v-if="user.role === 'ADMIN'"></el-table-column>
        <!-- 管理员显示部分：序号列 -->
        <el-table-column prop="id" label="序号" width="70" align="center" sortable
          v-if="user.role === 'ADMIN'"></el-table-column>
        <el-table-column prop="name" label="物资名称"></el-table-column>
        <el-table-column prop="descr" label="物资描述"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="img" label="物资图片">
          <template v-slot="scope">
            <el-image v-if="scope.row.img" style="width: 50px" :src="scope.row.img"
              :preview-src-list="[scope.row.img]"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID"></el-table-column>
        <el-table-column prop="time" label="提交时间"></el-table-column>
        <el-table-column prop="status" label="审核状态">
          <template v-slot="scope">
            <el-tag type="info" v-if="scope.row.status === '待审核'">待审核</el-tag>
            <el-tag type="success" v-if="scope.row.status === '通过'">已通过</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '拒绝'">暂不需要</el-tag>
          </template>
        </el-table-column>
        <!-- 管理员显示部分：使用状态列 -->
        <el-table-column prop="used" label="使用状态" v-if="user.role === 'ADMIN'">
          <template v-slot="scope">
            <el-button :type="scope.row.used === '未使用' ? 'success' : 'warning'" plain
              @click="toggleUsedStatus(scope.row)" :disabled="scope.row.status !== '通过'">
              {{ scope.row.used }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="审核理由"></el-table-column>
        <el-table-column label="操作" align="center" width="180">
          <template v-slot="scope">
            <!-- 管理员显示部分：已审核提示 -->
            <span v-if="user.role === 'ADMIN' && (scope.row.audited || scope.row.status === '通过')">已审核</span>
            <!-- 管理员显示部分：审核按钮 -->
            <el-button size="mini" type="primary" plain @click="handleAudit(scope.row)"
              v-if="user.role === 'ADMIN' && !scope.row.audited && scope.row.status !== '通过'">审核</el-button>
            <!-- 用户显示部分：编辑按钮 -->
            <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)"
              v-if="user.role === 'USER' && scope.row.status === '待审核'">编辑</el-button>
            <!-- 用户显示部分：删除按钮 -->
            <el-button size="mini" type="danger" plain @click="del(scope.row.id)"
              v-if="user.role === 'USER' && (scope.row.status === '待审核' || scope.row.status === '拒绝')">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next" :total="total">
        </el-pagination>
      </div>
    </div>

    <!-- 管理员显示部分：物资捐赠审核对话框 -->
    <el-dialog title="物资捐赠审核" :visible.sync="fromVisible1" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="审核状态" prop="status">
          <el-select v-model="form.status" style="width: 100%" @change="checkStatus">
            <el-option :value="item" v-for="item in ['待审核', '通过', '拒绝']" :key="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="审核理由" prop="reason" :disabled="form.status === '待审核'">
          <el-input v-model="form.reason" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible1 = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
    <!-- 用户显示部分：物资捐赠对话框 -->
    <el-dialog title="物资捐赠" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="物资名称" prop="name">
          <el-input v-model="form.name" placeholder="物资名称"></el-input>
        </el-form-item>
        <el-form-item label="物资描述" prop="descr">
          <el-input v-model="form.descr" placeholder="物资描述" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option :value="item" v-for="item in ['食品', '饮用水', '医疗用品', '其他']" :key="item"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="物资图片" prop="img">
          <el-upload :action="$baseUrl + '/files/upload'" :headers="{ token: user.token }" list-type="picture"
            :on-success="handleImgSuccess">
            <el-button type="primary">上传</el-button>
          </el-upload>
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
  name: "Donate",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      type: null,
      fromVisible: false,
      form: {
        used: '未使用',
        audited: false
      },
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: [],
      fromVisible1: false
    };
  },
  created() {
    this.load(1);
  },
  methods: {
    toggleUsedStatus(row) {
      if (row.status !== '通过') {
        this.$message.warning('该物资未通过审核，无法修改使用状态');
        return;
      }
      if (row.used === '已使用') {
        this.$message.warning('该物资已使用，状态不可更改');
        return;
      }
      const newStatus = row.used === '未使用' ? '已使用' : '未使用';
      this.$request({
        url: '/donate/update',
        method: 'PUT',
        data: {
          id: row.id,
          used: newStatus
        }
      }).then(res => {
        if (res.code === '200') {
          row.used = newStatus;
          this.$message.success('使用状态更新成功');
        } else {
          this.$message.error(res.msg);
          row.used = row.used === '未使用' ? '已使用' : '未使用';
        }
      }).catch(() => {
        row.used = row.used === '未使用' ? '已使用' : '未使用';
      });
    },
    handleAudit(row) {
      this.fromVisible1 = true;
      this.form = JSON.parse(JSON.stringify(row));
    },
    handleAdd() {
      this.form = {
        used: '未使用',
        audited: false
      };
      this.fromVisible = true;
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row));
      this.fromVisible = true;
    },
    checkStatus() {
      if (this.form.status === '待审核') {
        this.form.reason = '';
      }
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          if ((this.form.status === '通过' || this.form.status === '拒绝') &&!this.form.reason) {
            this.$message.warning('审核理由不能为空');
            return;
          }
          if (this.form.status === '待审核' && this.form.reason) {
            this.$message.warning('请通过或者拒绝本次捐赠');
            return;
          }
          if (this.form.status === '通过') {
            this.form.used = '未使用';
            this.form.audited = true;
          }
          this.form.role = this.user.role;
          this.$request({
            url: this.form.id ? '/donate/update' : '/donate/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('操作成功');
              this.load(1);
              this.fromVisible = false;
              this.fromVisible1 = false;
            } else {
              this.$message.error(res.msg);
            }
          });
        }
      });
    },
    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/donate/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => { });
    },
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id);
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(response => {
        this.$request.delete('/donate/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => { });
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request.get('/donate/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          type: this.type
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list;
          this.total = res.data?.total;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    reset() {
      this.type = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
    handleImgSuccess(response, file, fileList) {
      this.form.img = response.data;
    }
  }
};
</script>
<style scoped></style>    