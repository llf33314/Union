<template>
  <div id="unionCard">
    <main v-if="mainContent.length>0">
      <div class="unionList" :class="'m'+items.color2+index1"
           v-for="(items,index1) in mainContent"
          @click="CardDetails(index1)"
      >
        <span class="listLeft">
          <i class="iconfont">{{items.cardType==1?"&#xe6a2;":"&#xe616;"}}</i>
          <span>{{items.cardName}}</span>
        </span>
        <div class="listRight" >
          {{items.cardType==1?"免费领取":"购买"}}
        </div>
      </div>
      <span class="useRaiders" @click="useRaiders">使用攻略</span>
    </main>
    <noData v-else></noData>
    <FooterVue></FooterVue>
    <div class="box-wrap2" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box" >
        <p class="unionHeader">联盟卡使用说明：</p>
        <div class="boxContent2">
          <p>
            <span>1、</span>
            <span>联盟卡分为折扣卡和活动卡2种类型，折扣卡可以免费领取，而活动卡则需要购买</span>
          </p>
          <p>
            <span>2、</span>
            <span>粉丝已领取的折扣卡 或 已购买的活动卡，将存储在【我的】里面，可在里面查看详情</span>
          </p>
          <p>
            <span>3、</span>
            <span>折扣卡的使用：成功领取折扣卡后，粉丝到联盟内的商家进行消费时，只需要向商家展示 本人的联盟卡“二维码”，待商家扫码成功后即可享受对应折扣优惠</span>
          </p>
          <p>
            <span>4、</span>
            <span>活动卡的使用：粉丝购买活动卡后，可在有限的使用期限内，到联盟内的商家使用活动卡的 优惠项目，并且享受折扣优惠</span>
          </p>
        </div>
        <p class="btn1" @click="hide_">我知道了</p>
      </div>
    </div>

  </div>
</template>

<script>
  import FooterVue from '@/components/phone/FooterVue';
  import noData from '@/components/phone/noData';
  import $http from '@/utils/http.js'
  export default {
    name: 'unionCard',
    components:{
      FooterVue,
      noData
    },
    data() {
      return {
        //首页内容数据
        mainContent:'',
        //统一的id号码
        busId:'',//todo
      }
    },
    methods: {
      //隐藏联所有的弹出框;
      hide_() {
        $('.box-wrap2').hide();
      },
      // 点击弹出攻略
      useRaiders(){
        $(".box-wrap2").show();
      },
      toUnionCard(){
        this.$router.push({path:`/toUnionCard/${this.busId}`})
      },
      toMyInformation(){
        this.$router.push({path:`/toMyInformation/${this.busId}`})
      },
      //点击跳转到对应的路由
      CardDetails(index){
        if(this.mainContent[index].cardType==1){
          this.$router.push({path:`/toDiscountCard/${this.busId}/${this.mainContent[index].unionId}`})
        }else{
          this.$router.push({path:`/toActivityCard/${this.busId}/${this.mainContent[index].activityId}/${this.mainContent[index].unionId}`})
        }
      }
    },
    created () {
      $('#title_').text('联盟卡列表');
      this.busId = this.$route.params.bid;
      setTimeout(function() {
        $('.footerLeft li').css({
          color: "#44bf97"
        })
        $('.footerRight li').css({
          color: "#ddd"
        })
      },0);
      $http.get(`/h5Card/79B4DE7C/index/${this.busId}`)
        .then(res => {
          if (res.data.success) {
            if (res.data.data) {
              this.mainContent = res.data.data.records;
              console.log(this.mainContent);
              this.mainContent.forEach((v, i) => {
                let color2 = v.color2;
                let color1 = v.color1;
                let mDiv = 'm' + color2 + i;
                setTimeout(function () {
                  $("." + mDiv)[0].style.backgroundImage = `linear-gradient(90deg, #${color1} 0%, #${color2} 100%)`;
                }, 0)
              })
            }
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
</script>

<style>

</style>
