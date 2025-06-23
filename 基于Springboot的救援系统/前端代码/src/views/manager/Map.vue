<template>
  <div style="width: 100%; height: 700px" id="container"></div>
</template>
<script>
export default {
  name: "Map",
  mounted() {
    let map = new BMapGL.Map('container'); // 创建Map实例
    map.centerAndZoom(new BMapGL.Point(117.292507, 31.86926), 13); // 初始化地图,设置中心点坐标和地图级别
    map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
    this.$request.get('/help/selectAll').then(res=>{
      res.data.filter(v=>v.status==='待处理'||v.status==='进行中').forEach(v=>{
        let point = new BMapGL.Point(v.longitude, v.latitude)
        let marker = new BMapGL.Marker(point);  // 创建标注
        map.addOverlay(marker);
        let opts = {
          width : 300,     // 信息窗口宽度
          height: 100,     // 信息窗口高度
          title : '救援信息' , // 信息窗口标题
        }
        let infoWindow = new BMapGL.InfoWindow(
            "<b>名称</b>：" + v.name + "<br/> " +
            "<b>内容</b>：" + v.content + "<br/>" +
            "<b>状态</b>：" + v.status + "<br/>"
            ,
            opts);  // 创建信息窗口对象
        marker.addEventListener("click", function(){
          map.openInfoWindow(infoWindow,point) //开启信息窗口
        })
      })
    })
  }
}
</script>

<style scoped>

</style>