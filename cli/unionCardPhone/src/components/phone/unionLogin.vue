<template><!--佣金明细模块-->
  <div id="unionLogin">
    <img src="../../assets/images/sjlm01.png" style="width:100%;height:8.26rem;">
    <div class="login_">
      <span>手机号</span>
      <input type="text" style="border:none;width: 16.12rem;" placeholder="请输入手机号" maxlength="11" id="Phone">
    </div>
    <div class="login_">
      <div class="fr btn0" id="getCode" @click="getNumber">获取验证码</div>
      <span>验证码</span>
      <input type="number" style="border:none" placeholder="请输入验证码" v-model="inputNumber" maxlength="8" id="codeNumber">
      <p id="message_" style="display: none">验证码错误</p>
    </div>
    <button  @click="tologin_" class="btn1" disabled="true">登录</button>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  export default {
    name: 'toDetailList',
    //数据
    data() {
      return {
        //验证码输入框的值
        inputNumber:'',
        //id号码
        busId:'',
        //验证码可以点击
        isGetCode : 1,
      }
    },
    //页面加载后调用的函数
    mounted(){
      let that_=this;
      Phone.onblur=function(){
        if(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/.test(this.value)){
          message_.innerHTML='';
          getCode.style.color="#1FC055";
          that_.isGetCode = 1;
        }else if (this.value==""){
          $('#message_').show();
          message_.innerHTML="手机号不能为空，请输入手机号";
          setTimeout(()=>{
            message_.innerHTML="";
          },3000)
          getCode.style.color="#A8A8A8";
          that_.isGetCode = 0;
        }
        else{
          $('#message_').show();
          message_.innerHTML="手机号错误，请重新输入";
          setTimeout(()=>{
            message_.innerHTML="";
          },3000)
          getCode.style.color="#A8A8A8";
          that_.isGetCode = 0;
        }
      }
    },
    //方法
    methods: {
      //点击登录
      tologin_(){
        let Index1=window.location.href.indexOf('=');
        let number=window.location.href.slice(parseInt(Index1)+1);
        this.busId=number;
        var uPhone=$('#Phone').val();
        var pCode=$('#codeNumber').val();
        if(this.busId) {
          //登录页面
          $http.post(`/cardH5/79B4DE7C/login/${uPhone}?code=${pCode}&busId=${this.busId}`)
            .then(res => {
              //跳转到主页面中去
              if (res.data.success) {
                //跳转到主页面
                location.href = '#/toUnionCard?busId=' + number;
              }
            })
            .catch(err => {
              this.$message({showClose: true, message: err.toString(), type: 'error', duration: 5000});
            });
        }
      },
      //点击获得验证码
      getNumber(){
        let that_=this;
        if(that_.isGetCode == 1) {
          that_.isGetCode = 0;
          let Index1 = window.location.href.indexOf('=');
          let number = window.location.href.slice(parseInt(Index1) + 1);
          this.busId = number;
          let wait = 60;
          let Number_ = $('#Phone').val();
          //手机页面
          $http.get(`/cardH5/79B4DE7C/login/${Number_}?busId=${this.busId}`)
            .then(res => {
              if (res.data.success) {
                time(getCode);
              } else {
                that_.isGetCode = 1;
              }

              function time(o) {
                if (wait == 0) {
                  that_.isGetCode = 1;
                  o.innerHTML = "获取验证码";
                  o.style.backgroundColor = '#ffffff';
                  o.style.color = "#98989F";
                  Phone.value = '';
                } else {
                  that_.isGetCode = 0;
                  o.innerHTML = "重新发送(" + wait + ")";
                  o.style.color = "#ffffff";
                  o.style.backgroundColor = '#ddd';
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
    },
    //监听数据的变化
    watch: {
      inputNumber(){
        if($('#codeNumber').val().length>=5){
          document.querySelector('.btn1').removeAttribute("disabled");
          document.querySelector('.btn1').style.backgroundColor='#1FC055';
        }else{
          document.querySelector('.btn1').setAttribute("disabled",true);
          document.querySelector('.btn1').style.backgroundColor='#58b473';
        }
      }
    },
    //页面创造之前调用的函数
    created (){
      //页面的title变换
      $("#title_").text('联盟卡');
      console.log();
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>
  .login_>input::-webkit-input-placeholder { /* WebKit browsers */
    color:    #cccccc!important;
    font-size:0.8054rem;
  }
  .check-wrap-div>input:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
    color:    #cccccc!important;
    font-size:0.8054rem;
  }
  .check-wrap-div>input::-moz-placeholder { /* Mozilla Firefox 19+ */
    color:    #cccccc!important;
    font-size:0.8054rem;
  }
  .check-wrap-div>input:-ms-input-placeholder { /* Internet Explorer 10+ */
    color:    #cccccc!important;
    font-size:0.8054rem;
  }
</style>
