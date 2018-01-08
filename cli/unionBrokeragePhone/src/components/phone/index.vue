<template>
  <div id="Index0" class="main">
    <div class="Orders">
      <div class="main-title">
        <p>{{dataList[0]}}</p>
        <span>可提现</span>
      </div>
      <div class="flex main-wrap">
        <div class="flex-1">
            <i class="fr tubiao"></i>
            <strong>未支付</strong>
            <p>{{dataList[2]}}</p>
        </div>
        <div class="flex-1">
              <strong>所得佣金总和</strong>
              <p>{{dataList[1]}}</p>
        </div>
      </div>
      <router-link :to="{ path:'/toExtract',name:'toExtract'}">
          <div class="main_list clear">
              <img src="../../assets/images/widthDraw-1.png" class="fl">
              <div class="fl">
                <h2>我要提现</h2>
                <p>提现佣金至微信</p>
              </div>
          </div>
      </router-link>
      <router-link :to="{ path:'/toPayList',name:'toPayList'}">
        <div class="main_list clear">
            <img src="../../assets/images/widthDraw-3.png" class="fl">
            <div class="fl">
              <h2>我需支付</h2>
              <p>支付佣金给其他商家</p>
            </div>
        </div>
      </router-link>
      <router-link :to="{ path: '/toUnPayList', name: 'toUnPayList'}">
      <div class="main_list clear">
          <img src="../../assets/images/widthDraw-4.png" class="fl">
          <div class="fl">
            <h2>我未收佣金</h2>
            <p>其他商家未支付给我的佣金</p>
          </div>
      </div>
    </router-link>
    </div>
    <div class="supportIcon">
      <img src="../../assets/images/supprot-black.png" alt="" >
    </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  export default {
    name: 'index',
    data() {
      return {
//        底部颜色切换
//         toLogin: 'ceshi1',
        dataList:[],
        canReflect:'',
        unpaid:'',
        nonPayment:'',
      }
    },
    methods: {

    },
//    页面创建之前执行的函数
    created (){
      $("#title_").text('商家联盟佣金平台');
      //图片底部的颜色切换（白和灰切换）
      // this.$emit('getValue',this.toLogin);
      //可提现的数据
      $http.get("/h5Brokerage/index")
        .then(res => {
          if(res.data.data) {
            for (var key in res.data.data) {
              this.dataList.push(res.data.data[key].toFixed(2));
            }
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    }
  }
</script>
<style lang='less' rel="stylesheet/less">

</style>
