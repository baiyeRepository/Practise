<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <section class="carousel-section">
      <el-carousel 
        :interval="4000" 
        height="300px"
        indicator-position="outside"
      >
        <el-carousel-item 
          v-for="(item, index) in latestKnowledgeList" 
          :key="index"
        >
          <div 
            class="carousel-item-content"
            :style="{ backgroundImage: `url(${backgroundImageUrls})` }"
          >
            <span 
              class="title"
              @click="$router.push('/knowledgeDetail?id=' + item.id)"
            >
              {{ item.title }}
              <i class="el-icon-arrow-right"></i>
            </span>
            <span class="date">{{ item.date }}</span>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 核心内容区 -->
    <div class="core-content">
      <!-- 左侧主内容 -->
      <main class="main-area">
        <!-- 救援科普 -->
        <section class="knowledge-section">
          <h2 class="section-title">急救知识</h2>
          <div class="knowledge-list">
            <div 
              v-for="item in knowledgeList" 
              :key="item.id"
              class="knowledge-item"
              @click="$router.push('/knowledgeDetail?id=' + item.id)"
            >
              <div class="content-wrapper">
                <span class="title">{{ item.title }}</span>
                <span class="date">{{ item.date }}</span>
              </div>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
          <el-pagination
            v-if="total > pageSize"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-size="pageSize"
            layout="prev, pager, next"
            :total="total"
          />
        </section>
      </main>

      <!-- 右侧边栏 -->
      <aside class="side-area">
        <!-- 公告 -->
        <section class="notice-section">
          <h2 class="section-title">公告列表</h2>
          <el-timeline reverse>
            <el-timeline-item
              v-for="item in notices"
              :key="item.id"
              :timestamp="item.time"
            >
              <el-popover 
                placement="right" 
                width="200" 
                trigger="hover"
              >
                <template #reference>
                  <div class="notice-item">
                    <i class="el-icon-caret-right"></i>
                    {{ item.title }}
                  </div>
                </template>
                <p>{{ item.content }}</p>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </section>
      </aside>
    </div>

    <!-- 应急科普 -->
    <section class="emergency-section">
      <h2 class="section-title">应急科普</h2>
      <el-tabs v-model="activeTab">
        <!-- 生活安全 -->
        <el-tab-pane label="生活安全" name="life">
          <div class="safety-grid">
            <div 
              v-for="(item, index) in lifeSafetyList" 
              :key="index"
              class="safety-item"
            >
              <a :href="item.link" target="_blank">
                <img :src="item.imgSrc" :alt="item.title">
                <div class="info-box">
                  {{ item.title }}
                </div>
              </a>
            </div>
          </div>
        </el-tab-pane>

        <!-- 自然灾害 -->
        <el-tab-pane label="自然灾害" name="nature">
          <div class="disaster-grid">
            <div 
              v-for="(item, index) in naturalDisasterList" 
              :key="index"
              class="disaster-item"
            >
              <a :href="item.link" target="_blank">
                <img :src="item.imgSrc" :alt="item.title">
                <div class="info-box">
                  {{ item.title }}
                </div>
              </a>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      activeTab: "life",
      user: JSON.parse(localStorage.getItem("xm-user") || "{}"),
      notices: [],
      knowledgeList: [],
      pageNum: 1,
      pageSize: 5,
      total: 0,
      backgroundImageUrls: require("@/assets/imgs/科普.jpg"),
      lifeSafetyList: [
           {
          link: "https://mp.weixin.qq.com/s/qRcro-1ZmFio0F-9lWP4Yw",
          imgSrc: require("@/assets/imgs/警惕.png"),
          title: "每年超2000例，近期已多起！警惕6种情况→",
        },
        {
          link: "https://mp.weixin.qq.com/s/i7DLcx-M1zvZFQp-O_TPIQ",
          imgSrc: require("@/assets/imgs/脱险.png"),
          title: "全部脱险，赞！",
        },
        {
          link: "https://mp.weixin.qq.com/s/TYzNH031Rny55fC8j44syA",
          imgSrc: require("@/assets/imgs/家庭消防.png"),
          title: "痛心！八旬老人火场遇难！这个习惯真得改→",
        },

      ],
      naturalDisasterList: [
          {
          link: "https://mp.weixin.qq.com/s/PbijPReGfanSeAzGxOiL-w",
          imgSrc: require("@/assets/imgs/大风.png"),
          title: "今年以来最强！具有“极端性”！紧急提醒→",
        },
        {
          link: "https://mp.weixin.qq.com/s/U7ezS0xzj2jvugTqrVjisw",
          imgSrc: require("@/assets/imgs/强对流.png"),
          title: "及时拦截！通报表扬！",
        },
        {
          link: "https://mp.weixin.qq.com/s/RNppzvp-PQoZe_VJpz0zig",
          imgSrc: require("@/assets/imgs/旱涝.png"),
          title: "警惕！特旱与超警洪水齐来？紧急提示→",
        },

      ]
    };
  },
  computed: {
    latestKnowledgeList() {
      return this.knowledgeList.slice(0, 3);
    }
  },
  created() {
    this.$request.get("/notice/selectAll").then((res) => {
      this.notices = res.data || [];
    });
    this.load(1);
  },
  methods: {
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request
        .get("/knowledge/selectPage", {
          params: {
            pageNum: this.pageNum,
            pageSize: this.pageSize,
          },
        })
        .then((res) => {
          if (res.code === "200") {
            this.knowledgeList = res.data?.list;
            this.total = res.data?.total;
          } else {
            this.$message.error(res.msg);
          }
        });
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },
  }
};
</script>

<style scoped>
.home-container {
  max-width: 1280px; /* 页面最大宽度，适应常规屏幕尺寸 */
  margin: 0 auto; /* 水平居中显示 */
  padding: 20px; /* 内容区与容器边缘间距 */
  background: #f5f9ff; /* 新增浅蓝色背景 */
}

/* 轮播图容器样式 */
.carousel-section {
  margin-bottom: 40px; /* 与下方内容间距 */
  border-radius: 8px; /* 圆角设计 */
  overflow: hidden; /* 隐藏超出容器的内容 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 阴影效果增加立体感 */
}

/* 轮播项内容容器 */
.carousel-item-content {
  height: 100%; /* 占满轮播容器高度 */
  display: flex; /* 使用Flex布局 */
  flex-direction: column; /* 垂直排列子元素 */
  justify-content: center; /* 垂直居中 */
  align-items: center; /* 水平居中 */
  color: white; /* 文字颜色 */
  background-size: cover; /* 背景图覆盖整个区域 */
  background-position: center; /* 背景图居中显示 */
  background-repeat: no-repeat; /* 禁止背景图重复 */
  position: relative; /* 相对定位，用于子元素定位 */
  padding: 20px; /* 内边距 */
}

/* 轮播项渐变遮罩层 */
.carousel-item-content::before {
  content: ''; /* 空内容生成伪元素 */
  position: absolute; /* 绝对定位覆盖整个区域 */
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(
    45deg, 
    rgba(0, 0, 0, 0.6) 0%, /* 起始透明度 */
    rgba(0, 0, 0, 0.4) 50%, /* 中间透明度 */
    rgba(0, 0, 0, 0.6) 100% /* 结束透明度 */
  ); /* 45度渐变遮罩 */
  z-index: 1; /* 层级在内容下方 */
}

/* 轮播项标题样式 */
.title {
  font-size: 28px; /* 标题字体大小 */
  font-weight: bold; /* 加粗显示 */
  text-align: center; /* 文字居中 */
  cursor: pointer; /* 鼠标悬停显示手型 */
  position: relative; /* 相对定位 */
  z-index: 2; /* 层级在遮罩层之上 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5); /* 文字阴影效果 */
}

/* 轮播项日期样式 */
.date {
  font-size: 16px; /* 日期字体大小 */
  margin-top: 10px; /* 与标题间距 */
  position: relative; /* 相对定位 */
  z-index: 2; /* 层级在遮罩层之上 */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5); /* 文字阴影效果 */
}

/* 核心内容区布局 */
.core-content {
  display: grid; /* 使用网格布局 */
  grid-template-columns: 3fr 1fr; /* 主内容区3份，侧边栏1份 */
  gap: 30px; /* 列间距 */
  margin-bottom: 40px; /* 与下方内容间距 */
}

/* 知识板块容器 */
.knowledge-section {
   background: #f8f8f8; /* 基础浅灰色 */
  padding: 20px; /* 内边距 */
  border-radius: 8px; /* 圆角设计 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 知识列表布局 */
.knowledge-list {
  display: grid; /* 使用网格布局 */
  gap: 15px; /* 列表项间距 */
  margin-bottom: 20px; /* 与分页组件间距 */
}

/* 知识项样式 */
.knowledge-item {
  display: flex; /* 使用Flex布局 */
  justify-content: space-between; /* 两端对齐 */
  align-items: center; /* 垂直居中 */
  padding: 15px; /* 内边距 */
  border: 1px solid #eee; /* 浅灰色边框 */
  border-radius: 6px; /* 小弧度圆角 */
  cursor: pointer; /* 悬停手型 */
  transition: transform 0.2s; /* 悬停动画过渡 */
}
.content-wrapper .title {
  font-size: 18px;
    font-weight: normal;  
}

/* 知识项悬停效果 */
.knowledge-item:hover {
  transform: translateY(-2px); /* 轻微上浮效果 */
}

/* 公告板块容器 */
.notice-section {
  background: #f8f8f8; /* 基础浅灰色 */
  padding: 20px; /* 内边距 */
  border-radius: 8px; /* 圆角设计 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 公告项样式 */
.notice-item {
  cursor: pointer; /* 悬停手型 */
  padding: 8px 0; /* 垂直内边距 */
  transition: color 0.2s; /* 悬停颜色过渡 */
}

/* 公告项悬停效果 */
.notice-item:hover {
  color: #409EFF; /* 悬停蓝色 */
}

/* 应急科普板块容器 */
.emergency-section {
  background: #f8f8f8; /* 基础浅灰色 */
  padding: 20px; /* 内边距 */
  border-radius: 8px; /* 圆角设计 */
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 安全知识网格布局 */
.safety-grid,
.disaster-grid {
  display: grid; /* 使用网格布局 */
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); /* 响应式列布局 */
  gap: 20px; /* 网格间距 */
}

/* 安全知识图片样式 */
.safety-item img,
.disaster-item img {
  width: 100%; /* 图片宽度撑满容器 */
  height: 280px; /* 固定高度 */
  object-fit: cover; /* 保持比例填充 */
  border-radius: 6px; /* 小弧度圆角 */
    object-position: top center;
}

/* 安全知识标题容器 */
.info-box {
  padding: 10px; /* 内边距 */
  font-size: 14px; /* 较小字体 */
}

/* 标题样式 */
.section-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}
</style>    