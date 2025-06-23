<template>
    <div>
        <!-- 搜索区域 -->
        <div class="search">
            <el-input placeholder="请输入主题查询" style="width: 200px" v-model="trainingTopic"></el-input>
            <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
            <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
        </div>

        <!-- 操作按钮区域 -->
        <div class="operation">
            <el-button type="danger" plain @click="delBatch">批量删除</el-button>
            <el-button type="primary" plain @click="handleAdd">新增</el-button>
        </div>

        <!-- 表格区域 -->
        <div class="table">
            <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>
               <!--  <el-table-column prop="volunteerId" label="救助员ID"></el-table-column> -->
                <el-table-column label="培训日期">
                    <template v-slot="scope">
                        {{ formatDate(scope.row.trainingDate) }}
                    </template>
                </el-table-column>
                <el-table-column prop="trainingTopic" label="培训主题"></el-table-column>
                <el-table-column prop="duration" label="培训时长（小时）"></el-table-column>
                <el-table-column label="操作" align="center" width="220">
                    <template v-slot="scope">
                        <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页区域 -->
            <div class="pagination">
                <el-pagination background @current-change="handleCurrentChange" :current-page="pageNum"
                    :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next" :total="total">
                </el-pagination>
            </div>
        </div>

        <!-- 对话框区域 -->
        <el-dialog title="培训记录信息" :visible.sync="formVisible" width="40%" :close-on-click-modal="false"
            destroy-on-close>
            <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
                <el-form-item label="救助员ID" prop="volunteerId">
                    <el-input v-model="form.volunteerId" placeholder="救助员ID" disabled></el-input>
                </el-form-item>
                <el-form-item label="培训日期" prop="trainingDate">
                    <el-date-picker v-model="form.trainingDate" type="date" placeholder="选择培训日期"
                        :picker-options="pickerOptions"></el-date-picker>
                </el-form-item>
                <el-form-item label="培训主题" prop="trainingTopic">
                    <el-input v-model="form.trainingTopic" placeholder="培训主题"></el-input>
                </el-form-item>
                <el-form-item label="培训时长（小时）" prop="duration">
                    <el-input v-model="form.duration" placeholder="培训时长（小时）"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="formVisible = false">取 消</el-button>
                <el-button type="primary" @click="save">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: "TrainingRecords",
    data() {
        return {
            tableData: [],  // 表格数据
            pageNum: 1,     // 当前页码
            pageSize: 10,   // 每页显示数量
            total: 0,       // 数据总数量
            trainingTopic: null,  // 培训主题搜索关键字
            currentVolunteerId: null, // 当前救助员 ID
            formVisible: false, // 对话框是否显示
            form: {},           // 表单数据
            user: JSON.parse(localStorage.getItem('xm-user') || '{}'), // 用户信息
            rules: {
                trainingTopic: [
                    { required: true, message: '培训主题不能为空', trigger: 'blur' }
                ],
                duration: [
                    { pattern: /^\d+$/, message: '请输入数字', trigger: 'blur' }
                ]
            },          // 表单验证规则
            ids: [],            // 选中的数据 ID 列表
            username: null,     // 用户名
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now(); // 禁用今天之后的日期
                }
            }
        };
    },
    created() {
        this.username = this.user.username; // 从 localStorage 中获取用户名
        this.getVolunteerIdByUsername(this.username)
            .then(volunteerId => {
                this.currentVolunteerId = volunteerId;
                this.load(1); // 页面加载时加载第一页数据
            })
            .catch(error => {
                console.error('获取救助员 ID 失败:', error);
                const errorMessage = error.message || '获取救助员 ID 失败，请稍后重试';
                this.$message.error(errorMessage);
            });
    },
    methods: {
        // 加载数据方法
        load(pageNum) {
            const url = '/trainingrecords/page';
            const params = {
                pageNum,
                pageSize: this.pageSize,
                trainingTopic: this.trainingTopic
            };
            console.log('请求 URL:', url);
            console.log('请求参数:', params);
            this.$request.get(url, { params })
                .then(res => {
                    console.log('完整响应内容:', res);
                    if (res && res.code === '200') {
                        console.log('响应数据 data 内容:', res.data);
                        this.tableData = res.data.list || [];
                        this.total = res.data.total || 0;
                        this.pageNum = pageNum;
                    } else {
                        const errorMsg = res && res.msg ? res.msg : '显示数据未知错误';
                        this.$message.error(errorMsg);
                    }
                })
                .catch(error => {
                    console.error('加载数据失败:', error);
                    const errorMessage = error.message || '加载数据失败，请稍后重试';
                    this.$message.error(errorMessage);
                });
        },
        // 处理表格选中项变化
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.id);
        },
        // 处理新增操作
        handleAdd() {
            this.form = {};
            this.getVolunteerIdByUsername(this.username)
                .then(volunteerId => {
                    this.form.volunteerId = volunteerId;
                    this.formVisible = true;
                })
                .catch(error => {
                    console.error('获取救助员 ID 失败:', error);
                    const errorMessage = error.message || '获取救助员 ID 失败，请稍后重试';
                    this.$message.error(errorMessage);
                });
        },
        // 处理编辑操作
        handleEdit(row) {
            this.form = { ...row };
            this.formVisible = true;
        },
        // 保存表单数据
        save() {
            this.$refs.formRef.validate(valid => {
                if (valid) {
                    let url;
                    let method;
                    if (this.form.id) {
                        url = '/trainingrecords/update';
                        method = 'put';
                    } else {
                        url = '/trainingrecords/add';
                        method = 'post';
                    }
                    console.log('请求 URL:', url);
                    console.log('请求数据:', this.form);
                    this.$request[method](url, this.form)
                        .then(res => {
                            if (res && res.code === '200') {
                                this.$message.success('保存成功');
                                this.load(1);
                                this.formVisible = false;
                            } else {
                                const errorMsg = res && res.msg ? res.msg : '未知错误';
                                this.$message.error(errorMsg);
                            }
                        })
                        .catch(error => {
                            console.error('保存数据失败:', error);
                            const errorMessage = error.message || '保存数据失败，请稍后重试';
                            this.$message.error(errorMessage);
                        });
                }
            });
        },
        // 删除单条数据
        del(id) {
            this.$confirm('您确定删除吗？', '确认删除', { type: 'warning' })
                .then(() => {
                    const url = `/trainingrecords/delete/${id}`;
                    console.log('请求 URL:', url);
                    this.$request.delete(url)
                        .then(res => {
                            if (res && res.code === '200') {
                                this.$message.success('删除成功');
                                this.load(1);
                            } else {
                                const errorMsg = res && res.msg ? res.msg : '未知错误';
                                this.$message.error(errorMsg);
                            }
                        })
                        .catch(error => {
                            console.error('删除数据失败:', error);
                            const errorMessage = error.message || '删除数据失败，请稍后重试';
                            this.$message.error(errorMessage);
                        });
                });
        },
        // 批量删除数据
        delBatch() {
            if (this.ids.length === 0) {
                this.$message.warning('请选择要删除的记录');
                return;
            }
            this.$confirm('您确定批量删除吗？', '确认批量删除', { type: 'warning' })
                .then(() => {
                    const url = '/trainingrecords/delete-batch';
                    console.log('请求 URL:', url);
                    console.log('请求数据:', this.ids);
                    this.$request.post(url, this.ids)
                        .then(res => {
                            if (res && res.code === '200') {
                                this.$message.success('批量删除成功');
                                this.load(1);
                            } else {
                                const errorMsg = res && res.msg ? res.msg : '未知错误';
                                this.$message.error(errorMsg);
                            }
                        })
                        .catch(error => {
                            console.error('批量删除数据失败:', error);
                            const errorMessage = error.message || '批量删除数据失败，请稍后重试';
                            this.$message.error(errorMessage);
                        });
                });
        },
        // 重置搜索条件
        reset() {
            this.volunteerId = null;
            this.load(1);
        },
        // 处理分页页码变化
        handleCurrentChange(pageNum) {
            this.load(pageNum);
        },
        // 根据用户名获取救助员 ID
        getVolunteerIdByUsername(username) {
            return new Promise((resolve, reject) => {
                const url = `/trainingrecords/getIdByUsername/${username}`;
                this.$request.get(url)
                    .then(res => {
                        if (res && res.code === '200') {
                            resolve(res.data);
                        } else {
                            reject(new Error(res && res.msg ? res.msg : '获取用户名未知错误'));
                        }
                    })
                    .catch(error => {
                        reject(error);
                    });
            });
        },
        // 格式化日期
        formatDate(dateStr) {
            if (dateStr) {
                return new Date(dateStr).toLocaleDateString();
            }
            return '';
        }
    }
};
</script>

<style scoped>
/* 可以添加一些自定义样式 */
.search {
    margin-bottom: 20px;
}

.operation {
    margin-bottom: 20px;
}

.table {
    margin-bottom: 20px;
}

.pagination {
    margin-top: 20px;
}
</style>