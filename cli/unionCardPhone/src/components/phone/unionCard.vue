<template><!--佣金明细模块-->
  <div id="unionCard">
    <!--卡号-->
    <p class="position_">
      <img src="../../assets/images/sjUnionCard111.png" alt="">
    <div class="starts_">
      <img src="../../assets/images/sjUnionX1.png" v-for="(items,index) in unionList">
    </div>
    <h1 class="fr" @click="showModel(mainContent.cardNo)">
      <img src="../../assets/images/SJunionCard3.png">
    </h1>
    <h2 class="fl">{{mainContent.cardNo}}</h2>
    </p>
    <!--首页头像信息-->
    <div class="second_ clear">
      <img :src="mainContent.headurl" class="fl">
      <div>
        <div>
          <span class="fr">联盟积分：{{mainContent.integral}}</span>
          <h1>多粉平台联盟卡</h1>
        </div>
        <span>{{mainContent.nickname}}</span>
        <p>凭此卡在联盟内其他商家消费，可享受折扣</p>
      </div>
    </div>
    <!--联盟列表-->
    <ul class="third_ ">
      <li class="unionLi" v-for="(items,index) in unionList">
        <div  @click="showContent1(index,items.unionId,items.memberId,$event)" id="event_">
          <i class="el-icon-arrow-right fr"></i>
          <span>{{items.unionName}}</span>
        </div>
        <ul class="details_   passive">
          <li v-for="(item,index) in UnionCardList" class="li_Li">
            <div>
              <p class="fr">享受折扣：<span style="color:#1FC055;font-size:0.6442rem;">{{item.discount?item.discount:9.5}}折</span></p>
              {{item.enterpriseName}}
              <span style="color:#1FC055;font-size:0.6442rem;" v-if="item.memberId==menId">(本商家)</span>
              <span style="color:#1FC055;font-size:0.6442rem;" v-if="item.isUnionOwner">(盟主)</span>
            </div>
            <div>
              <img src="../../assets/images/SJunionCar03.png" style="width:0.65rem;margin-right: 0.2rem;">
              {{item.directorPhone}}
            </div>
            <p class="">
              <el-tooltip class="item" effect="dark" :content='item.enterpriseAddress' placement="top-start">
                <img src="../../assets/images/SJunionCar04.png" style="width:0.65rem;margin-right: 0.2rem;">
              </el-tooltip>
              <el-tooltip class="item" effect="dark" :content='item.enterpriseAddress' placement="top-start">
                <span title="123" style="font-size:0.6442rem;overflow: hidden;width: 8.12rem;display: inline-block;white-space: nowrap;text-overflow: ellipsis;">
                  {{item.enterpriseAddress}}
                </span>
              </el-tooltip>
              <button class="fr" @click="boxWarp(index)" v-if="List.bind==1&&(List.memberId==List.members[index].memberId)">
                领取联盟卡
              </button>
            </p>
          </li>
        </ul>
      </li>
    </ul>
    <!--绑定手机号的弹框-->
    <div class="box-wrap" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box">
        <ul>
          <li>
            <span style="font-size:0.8536rem;color:#000000">绑定手机号</span>
            <i class="el-icon-close fr" @click="hide_"></i>
          </li>
        </ul>
        <div>
          <form action="">
            <div>
              <span>手机号：</span>
              <input type="text" style="border: none" id="Phone" maxlength="11">
            </div>
            <div>
              <span>验证码：</span>
              <input type="text" style="border: none;width: 3.2rem;" id="verification">
              <div class="fr" style="color:#A8A8A8;background-color: rgb(255, 255, 255);border: none;" @click="getNumber"
                      id="getCode">
                获取验证码
              </div>
              <p id="message_" style="display: none">验证码错误</p>
            </div>
          </form>
          <button class="btn1" @click="bindPhone">绑定手机号</button>
        </div>
      </div>
    </div>
    <!--选择联盟卡的弹框-->
    <div class="box-wrap1" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box">
        <ul>
          <li>
            <span style="font-size:0.8536rem;color:#000000;font-weight: bold">选择联盟卡类型</span>
            <i class="el-icon-close fr" @click="hide_"></i>
          </li>
        </ul>
        <div>
          <div>
            <span class="price_">*卡价格</span>
            <p >￥<span class="price2_" style="font-size:1.74rem;">**</span></p>
          </div>
          <h5 class="select_" v-for="(card,index) in Cards">
            <div  id="black" @click="selectBlackCard()" v-if="card.black">
              <div>黑卡（￥<span class="blackCard">{{card.black.price}}</span>）</div>
              <i class="el-icon-check fr" style="color:#1FC055;opacity:0"></i>
              <h6>{{card.black.illustration?card.black.illustration:'没有黑卡说明'}}</h6>
            </div>
            <div  id="red" @click="selectRedCard()" v-if="card.red">
              <div>红卡（￥<span class="redCard">{{card.red.price}}</span>）</div>
              <i class="el-icon-check fr" style="color:#1FC055;opacity:0;"></i>
              <h6>{{card.red.illustration?card.red.illustration:'没有红卡说明'}}</h6>
            </div>
          </h5>
          <button style="color:#ffffff" @click="goAndGet">确认领取</button>
        </div>
      </div>
    </div>
    <!--点击弹二维码弹框-->
    <div class="box-wrap2" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box">
        <div class="first_" @click="hide_">关闭</div>
        <div class="second_">
          <p>向店员出示此二维码</p>
          <img :src="url1">
          <h6>{{mainContent.cardNo}}</h6>
          <span>多粉平台联盟卡</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  export default {
    name: 'unionCard',
    data() {
      return {
        //验证码等待的时间
        wait:30,
        //统一的id号码
        busId:33,
        //首页加载的数据
        mainContent:[],
        //是否是本商家
        menId:0,
        //是否领取联盟卡
        getUnionCard:'',
        //首页联盟列表
        unionList:[],
        //加载联盟下的盟员和联盟卡信息表的全部数据
        List:[],
        //加载联盟下的盟员和联盟卡信息的具体列表
        UnionCardList:[],
        //领取联盟卡里边的具体信息
        Cards:[],
        //点击联盟卡当前对象的成员Id号码
        memberId:0,
        //点击联盟卡当前对象的联盟Id号码
        unionId:0,
        //联盟卡类型
        cardType:0,
        //选择联盟卡的手机号
        phone:'',
        //二维码图片链接
        url1:'',
        blackCard:{},
        redCard:{},
        isGetCode : 1,
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
            color:'#ffffff'
          })
        } else {
          $('.btn1').css({
            color:'#CCCCCC'
          })
        }
      };
      //手机号失去焦点事件
      Phone.onblur=function(){
        if(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/.test(this.value)){
          message_.innerHTML='';
          getCode.style.color="#1FC055";
          that_.isGetCode = 1;
        }else if (this.value==""){
          $('#message_').show();
          message_.innerHTML="手机号不能为空，请输入手机号";
          getCode.style.color="#A8A8A8";
          that_.isGetCode = 0;
        }
        else{
          $('#message_').show();
          message_.innerHTML="手机号错误，请重新输入";
          getCode.style.color="#A8A8A8";
          that_.isGetCode = 0;
        }
      };
      //手机号获得焦点事件
      Phone.onfocus=function(){

      }
    },
    methods: {
      // 选择红卡        -------------------------1
      selectRedCard(){
        this.selectCard();
        $('.price_').text('红卡价格')
        $('.price2_').text($('.redCard').text());
        $('#red').find('i').css({
          opacity:1
        })
        this.cardType=2;
      },
      //选择黑卡          ------------------------2
      selectBlackCard(){
        this.selectCard();
        $('.price_').text('黑卡价格');
        $('.price2_').text($('.blackCard').text());
        $('#black').find('i').css({
          opacity:1
        })
        this.cardType=1;
      },
      //选择联盟的公共样式 ------------------------0
      selectCard(){
        let check=document.querySelectorAll('.el-icon-check');
        for(var i=0;i<check.length;i++){
          check[i].style.opacity=0;
        }
      },
      //确认领取联盟卡
      goAndGet(){
        let url1='toUnionCard';
        let data1={
          busId:this.busId,
          cardType:this.cardType,
          memberId:this.memberId,
          phone:this.phone,
          unionId:this.unionId
        };
        let data=JSON.stringify(data1);
        $http.post(`cardH5/79B4DE7C?url=${url1}`,data)
          .then(res => {
            if(res.data.success){
              if(res.data.data.length > 0){
                location.href=res.data.data.qrurl;
              }else {
                location.reload();
              }
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      },
      //点击弹出二维码
      showModel(QRCode){
        if(QRCode) {
          $('.box-wrap2').show();
          $http.get(`/cardH5/79B4DE7C/cardNoImgUrl?cardNo=${QRCode}`)
            .then(res => {
              if(res.data.data){
                this.url1=res.data.data;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        }
      },
      //点击按钮弹出领取联盟卡选择框;
      boxWarp(index){
        let Index=index;
        if(this.phone){
          //展示出选择联盟卡的框
          $('.box-wrap1').show();
         if(this.Cards){
           if(this.Cards[0].black){
              $('.price_').text('黑卡价格');
              $('.price2_').text(this.Cards[0].black.price);
              $('.el-icon-check')[0].style.opacity=1;
              this.cardType=1;
           }
           if(this.Cards[0].red){
             $('.price_').text('红卡价格');
             $('.price2_').text(this.Cards[0].red.price);
             $('.el-icon-check')[0].style.opacity=1;
             this.cardType=2;
           }
         }
        }else{
          //展示出绑定手机号的框
          $('.box-wrap').show();
        }
      },
      //隐藏联所有的弹出框;
      hide_(){
        $('.box-wrap').hide();
        $('.box-wrap1').hide();
        $('.box-wrap2').hide();
      },
      //点击div弹出对应的联盟信息
      showContent1(index1,unionid,memberid,$event){
        setTimeout(function () {
          $('.details_').eq(index1).find('li:last').css({
            borderBottom:'none'
          })
        },10);

          //赋值成员和联盟的Id号码--------------
          this.memberId=memberid;
          this.unionId=unionid;
        let isShow = $('.details_')[index1].style.display;
        //箭头全部向右
        var jiantou=document.querySelectorAll('.el-icon-arrow-down');
        for(var i=0;i<jiantou.length;i++){
          jiantou[i].className='el-icon-arrow-right fr';
        }
        //列表数据全部隐藏
        var ddd=document.querySelectorAll('.details_');
        for(var i=0;i<ddd.length;i++){
          ddd[i].style.display='none';
        }
        if(isShow == '' || isShow == 'none'){//当前隐藏了
          this.List=this.unionList[index1].memberInfo;
          //加载联盟下的盟员和联盟卡信息列表
          this.UnionCardList=this.unionList[index1].memberInfo.members;
          //判断ID是否一致
          this.menId=this.List.memberId;
          if(this.List.cards){
            this.Cards=this.List.cards;
            for(var i=0;i<this.Cards.length;i++){
              if(this.Cards[i].black){
                this.blackCard = this.Cards[i].black;
              }
              if(this.Cards[i].red){
                this.redCard = this.Cards[i].red;
              }
            }
          }
          //全部 li 样式 有 下划线 和 下内边距
          var unionLi=document.querySelectorAll('.unionLi>div');
          for(var i=0;i<unionLi.length;i++){
            unionLi[i].style.borderBottom='0.01rem solid #dddddd';
            unionLi[i].style.paddingBottom='1rem';
          }
          //当前列表数据展开、当前箭头向下
          $('.details_')[index1].style.display='block';
          $('.el-icon-arrow-right ').className ='el-icon-arrow-down';
          $('.el-icon-arrow-right ')[index1].className='el-icon-arrow-down fr';
          $('.unionLi>div')[index1].style.borderBottom=0;
          $('.details_')[index1].style.display = 'block';
          $('.unionLi>div')[index1].style.paddingBottom='1rem';
        }else {////当前显示了
          $('.details_')[index1].style.display = 'none';
          $('.unionLi>div')[index1].style.borderBottom = '0.01rem solid #dddddd';
          $('.unionLi>div')[index1].style.paddingBottom = '1rem';
        }
      },
      //点击获得验证码
      getNumber(){
        let that_=this;
        if(this.isGetCode == 1) {
          that_.isGetCode = 0;
          let wait = 60;
          let Number_ = $('#Phone').val();
          //手机页面
          $http.get(`/cardH5/79B4DE7C/code/${Number_}?busId=${this.busId}`)
            .then(res => {
              if (res.data.success) {
                time(getCode);
              }else {
                that_.isGetCode = 1;
              }
              function time(o) {
                if (wait == 0) {
                  that_.isGetCode = 1;
                  o.innerHTML = "获取验证码";
                  o.style.color='#1EC054';
                } else {
                  that_.isGetCode = 0;
                  o.innerHTML = "重新发送(" + wait + ")";
                  o.style.color = "#8a7e7e";
                  wait--;
                  setTimeout(function () {
                      time(o)
                    },
                    1000)
                }
              }
            })
            .catch(err => {
              this.$message({showClose: true, message: err.toString(), type: 'error', duration: 5000});
            });
        }
      },
      //点击绑定手机号
      bindPhone(){
        let url1='toUnionCard';
        let uPhone=$('#Phone').val();
        let pCode=$('#verification').val();
        if($('#verification').val()){
          $http.post(`/cardH5/79B4DE7C/bind/${uPhone}?busId=${this.busId}&url=${url1}&code=${pCode}`)
            .then(res => {
              //刷新当前页面
              if(res.data.success){
                location.reload();
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        }
        else{
          return
        }
      },
    },
    created (){
      //页面的title变换
      $("#title_").text('会员信息');
      //如果有busId值，就赋值
      let Index1=window.location.href.indexOf('=');
      let number=window.location.href.slice(parseInt(Index1)+1);
      this.busId=number;
      //刷新页面渲染联盟卡首页列表数据---------------------------------------------------------1
      let url1='toUnionCard';
      $http.get(`/cardH5/79B4DE7C/index/${this.busId}?url=${url1}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.phone) {
              //如果有phone的值就赋值，方便后续传参数
              this.phone = res.data.data.phone;
            }
            this.mainContent = res.data.data;
            this.unionList = res.data.data.unions;
            this.enterpriseAddress = res.data.data.bind;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
</script>

