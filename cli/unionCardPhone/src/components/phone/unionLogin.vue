<template>
  <div id="unionLogin">
    <div class="box" >
      <p class="binDing">登录/注册</p>
      <div class="boxContent">
        <!--//输入手机号-->
        <div>
          <span>手机号：</span>
          <input style="width: 75%" type="text" id="Phone" maxlength="11" v-model="phoneCode">
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
</template>

<script>
  import $http from '@/utils/http.js'
  export default {
    name: 'unionLogin',
    //数据
    data() {
      return {
        //验证码
        phoneCode:'',
        //输入框input
        verificationCode:'',
        //验证码等待的时间
        wait:60,
        //判断能不能按按钮
        isGetCode : 0,
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
    methods: {
      //点击获得验证码
      getNumber(){

      },
      //点击绑定手机号
      bindPhone(){

      },

    },//监听数据的变化
    watch: {
      phoneCode(){
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
    created () {
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>

</style>
