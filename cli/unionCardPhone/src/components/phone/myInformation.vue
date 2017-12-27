<template>
  <div id="myInformaiton">
    <header class="cardBag">
        <img class="headpPortrait" :src="myContent.heardImg" v-if="myContent.heardImg">
        <img class="headpPortrait" src="../../assets/images/header1.png" alt="" v-else="">
        <p class="name_ mb20">{{myContent.nickName?myContent.nickName:'没有数据'}}</p>
        <p class="mb30">{{myContent.cardNo?'凭此二维码到各联盟消费':'卡包空空如也'}}</p>
        <img class="QRCode mb30" :src="myContent.cardImg" alt="" v-if="myContent.cardNo">
        <img class="QRCode mb30" src="../../assets/images/noContentQR.png" alt="" v-else="">
        <p class="UnionNumber"> {{myContent.cardNo?myContent.cardNo:'**** ****** ****'}}</p>
        <div v-if="myContent.cardNo" class="flex">
          <p>
            <i class="iconfont">&#xe66f;</i>
            <span>联盟积分({{myContent.integral}})</span>
          </p>
          <p @click="consumeRecord">
            <i class="iconfont">&#xe647;</i>
            <span>消费记录({{myContent.consumeCount}})</span>
          </p>
        </div>
        <div v-else="" class="btn1" @click="CollectionCard">领取联盟卡</div>
    </header>
    <!--主页面内容-->
    <main class="mainContent" v-if="myContent.cardNo">
      <div class="unionCard">
        <p >
          <span></span>
          <strong>我已拥有的联盟卡</strong>
          <span></span>
        </p>
        <div v-for="(items,index1) in cardList1">
          <div class="unionList" @click="lookListConent(index1)" :class="'m'+items.color2+index1">
            <span class="listLeft">
              <i class="iconfont">&#xe6a2;</i>
              <div>
                <p>{{items.cardName}}</p>
              </div>
            </span>
            <i class="outOfDate iconfont" v-if="items.isOverdue">&#xe644;</i>
          </div>
          <!--专属特权-->
          <div class="exclusive">
            <p class="efontSize">
              <span>专属特权</span>
              <span class="fr" v-if="items.cardType==2" >使用期限：{{items.validityStr}}</span>
            </p>

            <ul>
              <li class="discounts" v-if="items.cardType==2">
                <i class="iconfont">&#xe698;</i>
                <p>{{items.itemCount}}项优惠</p>
              </li>
              <li class="consumed">
                <i class="iconfont">&#xe600;</i>
                <p>消费折扣</p>
              </li>
              <li class="unionPoint">
                <i class="iconfont">&#xe61a;</i>
                <p>联盟积分</p>
              </li>
            </ul>
            <div class="viewDetails">
              <span @click="viewDetails(index1)">查看详情</span>
            </div>
          </div>
        </div>
      </div>
    </main>
    <FooterVue></FooterVue>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  import FooterVue from "@/components/phone/FooterVue"

  export default {
    name: 'myInformation',
    components:{
      FooterVue
    },
    //数据
    data() {
      return {
        busId:'',
//        二维码
        url1:'',
        //我的详情的所有数据
        myContent:'',
        //底部卡列表数据
        cardList1:''
      }
    },
    //页面加载后调用的函数
    mounted(){

    },
    //方法
    methods: {
      consumeRecord(){
        this.$router.push({path:`/toConsumeRecord/${this.busId}`})
      },
      viewDetails(index1){
        if(this.cardList1[index1].cardType==1){
          this.$router.push({path:`/toDiscountCard/${this.busId}/${this.cardList1[index1].unionId}`})
        }else{
          this.$router.push({path:`/toActivityCard/${this.busId}/${this.cardList1[index1].activityId}/${this.cardList1[index1].unionId}`})
        }
      },
      CollectionCard(){
        this.$router.push({path:`/toUnionCard/${this.busId}`})
      },
      //点击对应的列表数据
      lookListConent(index1){
        let isShow = $('.exclusive')[index1].style.display;
        if(isShow=="" || isShow=="none") {
          $('.exclusive').slideUp(200)
          $('.exclusive').eq(index1).slideDown(200)
        }
        else{
          $('.exclusive').eq(index1).slideUp(200)
        }
      },
    },
    //页面创造之前调用的函数
    created (){
      $('#title_').text('我的');
      this.busId = this.$route.params.bid;
      setTimeout(function(){
        $('.footerLeft li').css({
          color:"#ddd"
        })
        $('.footerRight li').css({
          color:"#44bf97"
        })
      },0);
      //todo
      $http.get(`/h5Card/79B4DE7C/myCardDetail/${this.busId}?url=toMyInformation`)
        .then(res => {
          if(res.data.success) {
            if (res.data.data) {
              this.myContent = res.data.data;
              this.cardList1 = this.myContent.cardList;
              this.cardList1.forEach((v, i) => {
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
<style lang='less' rel="stylesheet/less" scoped>

</style>
