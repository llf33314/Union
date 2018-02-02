<template>
  <div id="activityCard">
    <header class="unionList">
      <span class="listLeft">
          <i class="iconfont">&#xe616;</i>
          <span>
              <p>{{mainContent.cardName}}</p>
              <span v-if="mainContent.isTransacted==0">使用期限：{{mainContent.validityStr}}</span>
              <span v-if="mainContent.isTransacted==1">使用期限：{{mainContent.validityDay}}天</span>
          </span>
      </span>
      <i class="outOfDate iconfont" v-if="mainContent.isOverdue==1">&#xe644;</i>
    </header>
    <!--专属特权-->
    <nav class="exclusive">
      <p class="efontSize">专属特权</p>
      <ul>
        <li class="discounts">
          <i class="iconfont">&#xe698;</i>
          <p>{{mainContent.itemCount}}项优惠</p>
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
    </nav>
    <main>
      <!--特权使用-->
      <div class="privileged">
        <div class="privilegedUser" @click="UserDeclare">
          <p>特权使用</p>
          <i class="iconfont iName1">&#xe604;</i>
          <i class="iconfont iName2" style="display: none">&#xe603;</i>
          <span>凭此卡可享受下列优惠及折扣</span>
        </div>
        <div class="declare">
          <i class="left iconfont">&#xe606;</i>
          <i class="right iconfont">&#xe605;</i>
          <span class="content">
            {{mainContent.activityIllustration}}
          </span>
        </div>
      </div>
      <!--折扣卡列表主页面内容-->
      <div class="cardList"  v-for="(items,index1) in cardDetailList" >
        <div  class="list">    <!-- @click="lookListConent"  -->
          <div class="listLeft">
            <p>
              <span>{{items.unionMember.enterpriseName}}</span>
              <span>500m</span>
            </p>
            <div>
              {{items.unionMember.enterpriseAddress}}
            </div>
          </div>
          <h3 class="listRight" v-if="items.unionMember.discount">{{items.unionMember.discount}} <span class="discount">折</span></h3>
        </div>
        <div class="listConent" v-if="items.unionCardProjectItems">
          <i class="left iconfont">&#xe606;</i>
          <i class="right iconfont">&#xe605;</i>
          <div class="discounts">优惠项目：</div>
          <p class="iconContent">
            <i class="iconfont">&#xe607;</i>
             洗车打蜡套餐，数量5       <!--  todo  -->
          </p>
        </div>
      </div>
    </main>
    <footer v-if="mainContent.isTransacted==1">
      <div class="footerHight"></div>
      <span class="money">￥{{mainContent.cardPrice}}</span>
      <span @click="Purchase">立即购买</span>
    </footer>
    <!--绑定手机号的弹框-->
    <div class="box-wrap" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box">
        <i class="iconfont close " @click="hide_">&#xe61b;</i>
        <p class="binDing">绑定手机号</p>
        <div class="boxContent">
          <!--//输入手机号-->
          <div>
            <span>手机号：</span>
            <input style="width: 75%" type="text" id="Phone" maxlength="11" v-model="phoneNumber">
          </div>
          <!--输入验证码-->
          <div>
            <span>验证码：</span>
            <input type="text" id="verification" maxlength="8" v-model="verificationCode">
            <div class="fr" @click="getNumber" id="getCode">
              获取验证码
            </div>
          </div>
          <span class="message_">验证码错误</span>
          <p class="btn1" @click="bindPhone">立即绑定</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'

  export default {
    name: 'activityCard',
    //数据
    data() {
      return {
        //内容数据
        mainContent:'',
        //联盟卡详情页-列表信息
        cardDetailList:'',
        busId:'',
        activityId:'',
        unionId:'',
        //验证码
        phoneNumber:'',
        verificationCode:'',
        //验证码等待的时间
        wait:60,
        //判断能不能按按钮
        isGetCode : 0,
        timeEnd :'',
      }
    },
    mounted(){
      let that_=this;
      //验证码获得焦点的时候
      verification.onfocus=function(){

      };
      //验证码失去焦点的时候
      verification.onblur=function(){
        if($('#verification').val()){
          $('.btn1').css({
            background:'#00ad7b'
          })
        } else {
          $('.btn1').css({
            background:'#64c4a8'
          })
        }
      };
      //手机号失去焦点事件
      Phone.onblur=function(){
        if(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/.test(this.value)){
          $(".message_")[0].innerHTML='';
          getCode.style.background="#00ad7b";
          that_.isGetCode = 1;
        }else if (this.value==""){
          $('.message_').show();
          $(".message_")[0].innerHTML="手机号不能为空，请输入手机号";
          getCode.style.background="#64c4a8";
          that_.isGetCode = 0;
        }
        else{
          $('.message_').show();
          $(".message_")[0].innerHTML="手机号错误，请重新输入";
          getCode.style.background="#64c4a8";
          that_.isGetCode = 0;
        }
      };
      //手机号获得焦点事件
      Phone.onfocus=function(){

      }
    },
    //方法
    methods: {
      //隐藏联所有的弹出框;
      hide_() {
        $('.box-wrap').hide();
        this.init();
      },
      //初始化数据;
      init(){
        Phone.value='';
        verification.value='';
        clearInterval(this.timeEnd);
        $('.message_').hide();
        this.clearCodeTime();
      },
      //清除定时器
      clearCodeTime(){
        this.wait=60;
        this.isGetCode = 1;
        getCode.innerHTML = "获取验证码";
      },
      //点击获得验证码
      getNumber(){
        let that_=this;
        let type=4;
        let number1=this.phoneNumber;
        if(this.isGetCode == 1) {
          $http.get(`/api/sms/${type}?phone=${number1}`)
            .then(res => {
              if (res.data.success) {
                time(getCode);
              }else {
                that_.isGetCode = 1;
              }
              function time(o) {
                that_.timeEnd = setInterval(() => {
                  that_.isGetCode = 0;
                  o.innerHTML = that_.wait + "s";
                  if (that_.wait === 0) {
                    clearInterval(that_.timeEnd);
                    that_.clearCodeTime();
                    return;
                  }
                  that_.wait--;
                }, 1000);
              }
            })
            .catch(err => {
              this.$message({showClose: true, message: err.toString(), type: 'error', duration: 5000});
            });
        }
      },
      //点击绑定手机号
      bindPhone(){
        let phone1=this.phoneNumber;
        let code1=this.verificationCode;
        $http.post(`/h5Card/79B4DE7C/${this.busId}/bind?phone=${phone1}&url=toDiscountCard&code=${code1}`)
          .then(res => {
            if(res.data.success){
              //刷新当前页面
              history.go(0)
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      },
      //点击权限使用
      UserDeclare(){
        let isShow = $('.declare')[0].style.display;
        if(isShow=="" || isShow=="none") {
          $('.declare').slideDown(100)
          $('.iName2').show()
          $('.iName1').hide()
        }
        else{
          $('.declare').slideUp(100)
          $('.iName1').show()
          $('.iName2').hide()
        }
      },
      //立即购买
      Purchase(){
        let url1='toActivityCard';
        $http.post(`/h5Card/79B4DE7C/transaction/${this.busId}/${this.unionId}?activityId=${this.activityId}&url=${url1}`)
          .then(res => {
            if(res.data.success){
              if(res.data.data!='' &&res.data.data!=undefined ){
                if(res.data.data.phone==0){
                  $('.box-wrap').show();
                }
                else{
                  window.location = res.data.data.payUrl;
                }
              }
              else{
                //刷新当前页面
                window.location.href;
              }
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      },
    },
    //监听数据的变化
    watch: {
      phoneNumber(){
        if(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/.test(Phone.value)){
          $(".message_")[0].innerHTML='';
          getCode.style.background="#64c4a8";
          this.isGetCode = 1;
        }
        if(Phone.value.length<11){
          getCode.style.background="#64c4a8";
          this.isGetCode = 0;
        }else{
          getCode.style.background="#00ad7b ";
        }
      },
      verificationCode(){
        if(verification.value.length>5){
          $('.btn1').css({
            background:'#00ad7b'
          })
        }else{
          $('.btn1').css({
            background:'#64c4a8'
          })
        }
      }
    },
    //页面创造之前调用的函数
    created (){
      let that_=this;
      $('#title_').text('活动卡');
      this.busId = this.$route.params.bid;
      this.activityId = this.$route.params.aid;
      this.unionId = this.$route.params.uid;
      $http.get(`/h5Card/79B4DE7C/cardDetail/${this.busId}/${this.unionId}?activityId=${this.activityId}`)
        .then(res => {
          if(res.data.data!='' &&res.data.data!=undefined ) {
            this.mainContent = res.data.data;
            this.cardDetailList = this.mainContent.cardDetailListVO;
            $('.unionList')[0].style.backgroundImage =
              `linear-gradient(90deg, #${that_.mainContent.color1} 0%, #${that_.mainContent.color2} 100%)`
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
