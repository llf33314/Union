<template>
  <div id="discountCard">
    <header class="unionList">
        <span class="listLeft">
          <i class="iconfont">&#xe6a2;</i>
          <span>{{mainContent.cardName}}</span>
        </span>
    </header>
    <!--专属特权-->
    <nav class="exclusive1">
      <p class="efontSize">专属特权</p>
      <ul>
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
      <div class="privilegedUser">
        <p>特权使用</p>
        <span>在下列{{mainContent.userCount}}个商家消费时可享受折扣</span>
      </div>
      <!--折扣卡列表主页面内容-->
      <div class="cardList" v-for="(items,index1) in cardDetailList">
        <div  class="list">
          <div class="listLeft">
            <p>
              <span>{{items.unionMember.enterpriseName}}</span>
              <span v-if="items.unionMember.enterpriseAddress">{{objectDistance[index1]}}</span>
            </p>
            <div>
              {{items.unionMember.enterpriseAddress}}
            </div>
          </div>
          <h3 class="listRight" v-if="items.unionMember.discount">{{items.unionMember.discount}}  <span class="discount">折</span></h3>
        </div>
      </div>
    </main>
    <footer v-if="mainContent.isTransacted==1">
      <div class="footerHight"></div>
      <span @click="FreeToReceive">免费领取</span>
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
    name: 'discountCard',
    //数据
    data() {
      return {
        mainContent:'',
        cardDetailList:'',
        //距离目标距离
        objectDistance:[],
        busId:"",
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

      };
      //获取当前经纬度和目标经纬度的距离
      this.targetRange();
    },
    //方法
    methods: {
      //获取经纬度离目标的距离
      targetRange() {
        var that_=this;
        var getPosition = function (callback) {
          if ("geolocation" in navigator) {
            var geo_options = {
              enableHighAccuracy: true,
              maximumAge: 0,
              timeout: 500
            };
            navigator.geolocation.getCurrentPosition(function (pos) {
              // 获取到当前位置经纬度
              debugger;
              var lng = pos.coords.longitude;
              var lat = pos.coords.latitude;
              callback(lng, lat);
            }, function (error) {
              //alert(err.message);
              var lng = 0;
              var lat = 0;
              callback(lng, lat);
              switch (error.code) {
                case error.TIMEOUT :
                  alert(" 连接超时，请重试 ");
                  break;
                case error.PERMISSION_DENIED :
                  alert(" 您拒绝了使用位置共享服务，查询已取消 ");
                  break;
                case error.POSITION_UNAVAILABLE :
                  alert(" 亲爱的，非常抱歉，我们暂时无法为您所在的星球提供位置服务 ");
                  break;
              }
            }, geo_options);
          } else {
            var lng = 0;
            var lat = 0;
            callback(lng, lat);
            alert("当前浏览器不支持定位");
          }
        };
        //计算两地之间的距离
        var getDistance = function (lon1, lat1, lon2, lat2) { //单位m
          var result;
          var EARTH_RADIUS = 6378137;
          var rad = Math.PI / 180.0;
          var radLon1 = lon1 * rad;
          var radLat1 = lat1 * rad;
          var radLon2 = lon2 * rad;
          var radLat2 = lat2 * rad;
          var dis = Math.acos(Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radLon1 - radLon2) + Math.sin(radLat1) * Math.sin(radLat2)) * EARTH_RADIUS;
          var distance = Math.round(dis * 10000.0) / 10000.0;
          if (parseFloat(distance) >= parseFloat(1000)) {
            result = parseFloat(distance / 2000).toFixed(1) + '公里';
          } else {
            result = parseFloat(distance).toFixed(0) + '米';
          }
          return result;
        };
        //测试
        getPosition(function(lng,lat){
          console.log('当前经度:'+lng+'<br/>当前纬度'+lat);
          var _Lng = lng;
          var _lat = lat;
          var items=that_.cardDetailList;
          console.log(items);
          for(var i=0;i<items.length;i++){
            if(items[i].unionMember.enterpriseAddress && items[i].unionMember.addressLongitude && items[i].unionMember.addressLatitude){
              console.log(getDistance(_Lng,_lat,items[i].unionMember.addressLongitude,items[i].unionMember.addressLatitude));
              that_.objectDistance.push(getDistance(_Lng,_lat,items[i].unionMember.addressLongitude,items[i].unionMember.addressLatitude));
            }
          }
          console.log(that_.objectDistance);
        });
      },
      //领取按钮
      FreeToReceive(){
        let url1='toDiscountCard';
        $http.post(`/h5Card/79B4DE7C/transaction/${this.busId}/${this.unionId}?&url=${url1}`)
          .then(res => {
            if(res.data.success) {
              if (res.data.data != '' && res.data.data != undefined) {
                if (res.data.data.phone == 0) {
                  $('.box-wrap').show();
                }
              }else {
                //刷新当前页面
                window.location.href;
              }
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      },
      //隐藏联所有的弹出框;
      hide_() {
        $('.box-wrap').hide();
        this.init();
      },
      //初始化数据;
      init(){
        Phone.value='';
        verification.value='';
        this.clearCodeTime();
        clearInterval(this.timeEnd);
        $('.message_').hide();
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
      $('#title_').text('折扣卡');
      this.busId = this.$route.params.bid;
      this.unionId = this.$route.params.uid;
      $http.get(`/h5Card/79B4DE7C/cardDetail/${this.busId}/${this.unionId}`)
        .then(res => {
          if(res.data.data!='' &&res.data.data!=undefined ) {
            this.mainContent = res.data.data;
            this.cardDetailList = this.mainContent.cardDetailListVO;
            console.log(this.cardDetailList);
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
