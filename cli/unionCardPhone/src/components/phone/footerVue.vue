<template>
  <div class="footer_">
    <div class="footerHight"></div>
    <footer class="footer_">
      <ul class="footerLeft" @click="toUnionCard">
        <li><i class="iconfont">&#xe66c;</i></li>
        <li>首页</li>
      </ul>
      <ul class="footerRight" @click="toMyInformation">
        <li><i class="iconfont">&#xe668;</i></li>
        <li>我的</li>
      </ul>
    </footer>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  export default {
    name: 'footerVue',
    //数据
    data() {
      return {
        busId:33,//todo
      }
    },
    //方法
    methods: {
      toUnionCard(){
        this.$router.push({path:`/toUnionCard/${this.busId}`})
        setTimeout(function () {
          $('.footerRight li').css({
            color:"#ddd"
          })
          $('.footerLeft li').css({
            color:"#44bf97"
          },0)
        })
      },
      toMyInformation(){
        this.$router.push({path:`/toMyInformation/${this.busId}`});
        setTimeout(function(){
          $('.footerLeft li').css({
            color:"#ddd"
          })
          $('.footerRight li').css({
            color:"#44bf97"
          })
        },0)

      }
    },
    //页面创造之前调用的函数
    created (){

    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>

</style>
