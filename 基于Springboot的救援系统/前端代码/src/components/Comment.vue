<template>
  <div class="comment-container">
    <div class="comment-header">评论 {{ commentCount }}</div>
    <div class="comment-input-area">
      <el-input type="textarea" placeholder="请输入评论" v-model="content" class="comment-input"></el-input>
      <div class="comment-submit">
        <el-button type="primary" @click="addComment(null)">评论</el-button>
      </div>
    </div>

    <div class="comment-list">
      <div v-for="item in commentList" :key="item.id" class="comment-item">
        <!-- 一级评论 -->
        <div class="comment-main">
          <img :src="item.avatar" alt="" class="comment-avatar" />
          <div class="comment-body">
            <div class="comment-user">
              {{ item.userName }}（{{ item.role }}）
            </div>
            <div class="comment-content">{{ item.content || '该评论已删除' }}</div>
            <div class="comment-meta">
              <span class="comment-time">{{ item.time | timeFormat }}</span>
              <span class="comment-action" @click="handleShowReply(item)" :class="{ 'comment-active': item.showReply }">
                <i class="el-icon-s-comment"></i>回复
              </span>
              <span class="comment-action" @click="delComment(item.id)"
                v-if="item.userId === user.id || item.adminId === user.id" v-show="item.content">
                <i class="el-icon-delete"></i>删除
              </span>
            </div>
            <div v-if="item.showReply" class="comment-reply-box">
              <el-input type="textarea" :placeholder="`请输入对 ${item.userName} 的回复`" v-model="item.replyContent"
                class="reply-input"></el-input>
              <div class="reply-submit">
                <el-button type="primary" @click="addComment(item)">
                  回复
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 二级评论 -->
        <transition name="slide-fade">
          <div v-if="item.children" class="comment-children" :key="'children-' + item.id">
            <div v-for="sub in item.isExpanded
              ? item.children
              : item.children.slice(0, 3)" :key="sub.id" class="comment-child">
              <img :src="sub.avatar" alt="" class="comment-avatar" />
              <div class="comment-body">
                <div class="comment-user">
                  {{ sub.userName }}（{{ sub.role }}）
                  <span v-if="sub.parentUserName !== item.userName">
                    回复：{{ sub.parentUserName }}
                  </span>
                </div>
                <div class="comment-content">{{ sub.content || '该评论已删除' }}</div>
                <div class="comment-meta">
                  <span class="comment-time">{{ sub.time | timeFormat }}</span>
                  <span class="comment-action" @click="handleShowReply(sub)"
                    :class="{ 'comment-active': sub.showReply }">
                    <i class="el-icon-s-comment"></i>回复
                  </span>
                  <span class="comment-action" @click="delComment(sub.id)"
                    v-if="sub.userId === user.id || sub.adminId === user.id" v-show="sub.content">
                    <i class="el-icon-delete"></i>删除
                  </span>
                </div>
                <div v-if="sub.showReply" class="comment-reply-box">
                  <el-input type="textarea" :placeholder="`${user.userName} 回复 ${sub.userName}: 请输入回复`"
                    v-model="sub.replyContent" class="reply-input"></el-input>
                  <div class="reply-submit">
                    <el-button type="primary" @click="addComment(sub)">
                      回复
                    </el-button>
                  </div>
                </div>
              </div>
            </div>

            <!-- 展开/收起按钮 -->
            <div v-if="item.children.length > 3" class="comment-expand" @click="toggleExpand(item)">
              <i :class="item.isExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'
                "></i>
              {{
                item.isExpanded
                  ? "收起"
                  : `展开剩余${item.children.length - 3}条回复（共${item.children.length
                  }条）`
              }}
            </div>
          </div>
        </transition>
      </div>

      <!-- 分页 -->
      <div class="comment-pagination" v-if="total > pageSize">
        <el-pagination small background @current-change="handleCurrentChange" :current-page="pageNum"
          :page-sizes="[5, 10, 20]" :page-size="pageSize" layout="total, prev, pager, next"
          :total="total"></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CommentComponent",
  props: {
    fid: null,
    module: null,
  },
  data() {
    return {
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      commentCount: 0,
      content: "",
      commentList: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
    };
  },
  filters: {
    timeFormat(val) {
      const date = new Date(val);
      const now = new Date();
      const diff = (now - date) / 1000;

      if (diff < 60) return "刚刚";
      if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`;
      if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`;
      return `${date.getFullYear()}年${date.getMonth() + 1}月`;
    },
  },
  created() {
    this.loadComment();
  },
  methods: {
    handleShowReply(comment) {
      this.$set(comment, "showReply", !comment.showReply);
    },
    delComment(commentId) {
      this.$confirm("您确定删除吗？", "确认删除", { type: "warning" })
        .then((response) => {
          this.$request.delete("/comment/delete/" + commentId).then((res) => {
            if (res.code === "200") {
              this.$message.success("操作成功");
              this.loadComment();
            } else {
              this.$message.error(res.msg);
            }
          });
        })
        .catch(() => { });
    },
    loadComment() {
      this.$request
        .get("/comment/selectTree/", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        })
        .then((res) => {
          let list = res.data?.list || [];
          list.forEach((item) => {
            this.$set(item, "isExpanded", false);
            if (item.children) {
              item.children.sort((a, b) => new Date(a.time) - new Date(b.time));
            }
          });
          this.commentList = list;
          this.total = res.data?.total || 0;
        });

      this.$request.get("/comment/selectCount/").then((res) => {
        this.commentCount = res.data || 0;
      });
    },
    addComment(comment) {
      let data = {
        role: this.user.role,
        userId: this.user.id,
        content: this.content,
      };
      if (comment) {
        data.content = comment.replyContent;
        data.pid = comment.id;
      }
      if (!data.content) {
        this.$message.warning("请输入内容!");
        return;
      }
      this.$request.post("/comment/add", data).then((res) => {
        if (res.code === "200") {
          this.$message.success("操作成功");
          this.content = "";
          if (comment) comment.showReply = false;
          this.loadComment();
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum;
      this.loadComment();
    },
    toggleExpand(item) {
      this.$set(item, "isExpanded", !item.isExpanded);
    },
  },
};
</script>

<style scoped>
.comment-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-header {
  font-size: 24px;
  padding-bottom: 15px;
  border-bottom: 2px solid #eee;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.comment-header::before {
  content: "💬";
  font-size: 1.2em;
}

.comment-input-area {
  transition: box-shadow 0.3s;
  border-radius: 8px;
  padding: 12px;
  background: #fff;
}
.comment-input-area:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
/* 按钮悬停动效 */
.el-button--primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
  transition: all 0.2s ease;
}

.comment-input textarea {
  border-radius: 4px;
  transition: border-color 0.3s ease;
}

.comment-input textarea:focus {
  border-color: #409eff;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.3);
}

.comment-submit {
  text-align: right;
  margin-top: 10px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.comment-main {
  display: flex;
}
/* 一级评论头像 */
.comment-main .comment-avatar {
  width: 56px;
  height: 56px;
  border: 2px solid #e1f3ff;
}

/* 二级评论头像 */
.comment-child .comment-avatar {
  width: 42px;
  height: 42px;
  border-width: 1px;
}

/* 层级缩进线 */
.comment-children {
  position: relative;
  padding-left: 40px;
  margin-left: 30px;
  border-left: 2px solid #e8e8e8;
}
.comment-children::before {
  content: "";
  position: absolute;
  left: -2px;
  top: 20px;
  bottom: 20px;
  width: 2px;
  background: linear-gradient(to bottom, transparent 10%, #e8e8e8 50%, transparent 90%);
}
.comment-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.comment-body {
  flex: 1;
  padding-left: 15px;
}

.comment-user {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #555;
  margin-bottom: 10px;
}

.comment-meta {
  font-size: 13px;
  color: #999;
}

.comment-action {
  margin: 0 10px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.comment-action:hover {
  color: #409eff;
}

.comment-reply-box {
  margin-top: 10px;
}

.reply-input textarea {
  border-radius: 4px;
  transition: border-color 0.3s ease;
}

.reply-input textarea:focus {
  border-color: #409eff;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.3);
}

.reply-submit {
  text-align: right;
  margin-top: 10px;
}

.comment-children {
  padding-left: 65px;
  border-left: 2px solid #ebeef5;
  margin-left: 15px;
}

.comment-child {
  display: flex;
  margin-bottom: 15px;
}

.comment-expand {
  margin-left: 50px;
  cursor: pointer;
  color: #409eff;
  font-size: 13px;
  margin-bottom: 10px;
  transition: color 0.3s ease;
}

.comment-expand:hover {
  color: #66b1ff;
}

.comment-pagination {
  margin-top: 20px;
  text-align: center;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
.comment-user {
  position: relative;
  padding-left: 8px;
}
.comment-user::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: #409eff;
  border-radius: 2px;
}

/* 不同角色颜色标记 */
.comment-user:has([role="admin"])::before { background: #f56c6c; }
.comment-user:has([role="guest"])::before { background: #67c23a; }
</style>    