<template>
  <div style="width: 50%">
    <div style="margin-bottom: 10px">
      <el-button @click="$router.back()"><<返回</el-button>
    </div>
    <div class="card">
      <div style="font-size: 25px;margin-left: 20px;text-align: center">{{knowledge.title}}</div>
      <div style="text-align: center;color:#888;margin-bottom: 30px">
        <span>发布日期:{{knowledge.date}}</span>
        <span style="margin-left: 20px">阅读量:{{knowledge.readCount}}</span>
      </div>
      <div class="w-e-text">
        <div v-html="knowledge.content"></div>
      </div>
    </div>
  </div>
</template>

<script>
import E from 'wangeditor'
export default {
  name: "KnowledgeDetail",
  data() {
    return {
      id: this.$route.query.id,
      knowledge: {}
    }
  },
  created() {
    this.$request.put('/knowledge/updateCount/' + this.id).then(res => {
      this.load()
    })
  },
  methods: {
    load() {
      this.$request.get('knowledge/selectById/' + this.id).then(res => {
        this.knowledge = res.data || []
      })
    }
  }
}
</script>

<style scoped>
/* 添加文章内容字体样式 */
.w-e-text {
  font-size: 20px;
}
</style>