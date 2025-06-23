<template>
  <div>
    <div style="display: flex;grid-gap: 15px;margin-bottom: 10px">
      <div class="card item">
        <div style="flex: 1;text-align: center">
          <img src="@/assets/imgs/救灾.png" style="width: 60px;height: 60px">
        </div>
        <div style="flex: 1">
          <div style="margin-bottom: 5px">救灾总数</div>
          <div style="font-weight: bold;font-size:20px">{{count.helpCount}}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1;text-align: center">
          <img src="@/assets/imgs/消防.png" style="width: 60px;height: 60px">
        </div>
        <div style="flex: 1">
          <div style="margin-bottom: 5px">救助员总数</div>
          <div style="font-weight: bold;font-size:20px">{{count.volCount}}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1;text-align: center">
          <img src="@/assets/imgs/物资.png" style="width: 60px;height: 60px">
        </div>
        <div style="flex: 1">
          <div style="margin-bottom: 5px">物资总数</div>
          <div style="font-weight: bold;font-size:20px">{{count.donateCount}}</div>
        </div>
      </div>
      <div class="card item">
        <div style="flex: 1;text-align: center">
          <img src="@/assets/imgs/留言.png" style="width: 60px;height: 60px">
        </div>
        <div style="flex: 1">
          <div style="margin-bottom: 5px">留言总数</div>
          <div style="font-weight: bold;font-size:20px">{{count.commentCount}}</div>
        </div>
      </div>
    </div>
    <div style="display: flex;grid-gap: 15px">
      <div style="flex:1" class="card">
        <div id="pie" style="height: 400px"></div>
      </div>
      <div style="flex:1" class="card">
        <div id="bar" style="height: 400px"></div>
      </div>
    </div>
  </div>
</template>
<script>
import * as echarts from 'echarts'
const pieOption = {
  title: {
    text: '物资捐赠分类统计',
    subtext: '比例图',
    left: 'center'
  },
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      type: 'pie',
      center: ['50%', '60%'],
      radius: '50%',
      data: [],
      label: {
        show: true,
        formatter(param) {
          return param.name + ' (' + param.percent + '%)';
        }
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}
const barOption = {
  title: {
    text: '分类救援统计',
    subtext: '柱状图',
    left: 'center'
  },
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    left: 'left'
  },
  xAxis: {
    type: 'category',
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [],
      type: 'bar',
      smooth: true,
      itemStyle: {
        normal: {
          color: function(params) { // 根据索引返回对应的颜色
            let colorList = ['#ffaa2e','#32C5E9','#fa4c4c','#08b448','#FFDB5C','#ff9f7f','#fb7293','#E062AE','#E690D1','#e7bcf3']
            return colorList[params.dataIndex];
          }
        }
      },
    }
  ]
}
export default {
  name: "DashBoard",
  data(){
    return{
      count:{
        helpCount:0,
        volCount:0,
        donateCount:0,
        commentCount:0
      }
    }
  },
  mounted(){
    this.load()
    let pieDom = document.getElementById('pie');
    let pieChart = echarts.init(pieDom);
    this.$request.get('/pieData').then(res=>{
      pieOption.series[0].data = res.data || []
      pieChart.setOption(pieOption)
    })
    let barDom = document.getElementById('bar');
    let barChart = echarts.init(barDom);
    this.$request.get('/barData').then(res => {
      barOption.xAxis.data = res.data?.map(v => v.name) || []
      barOption.series[0].data = res.data?.map(v => v.value) || []
      barChart.setOption(barOption)
    })
  },
  methods:{
    load(){
      this.$request.get('/count').then(res=>{
        this.count=res.data||{}
      })
    }
  }
}
</script>

<style scoped>
.item{
  flex:1;
  display:flex;
  align-items: center;
}
</style>