<template><!--佣金明细模块-->
  <div id="toLogin">
    <h1>商家联盟佣金平台</h1>
    <div class="check-wrap">
      <div class="check-wrap-div">
        <div style="width: 30px;display: inline-block">
          <img src="../../assets/images/phone.png" alt="">
        </div>
        <input type="text" placeholder="请输入账号" id="username" name="username" style="width: 50%">
      </div>
      <div class="check-wrap-div">
        <div style="width: 30px;display: inline-block">
          <img src="../../assets/images/cher.png" alt="">
        </div>
        <input type="password" placeholder="请输入密码" id="userpwd" name="userpwd" style="width: 50%">
        <!--   <input class="checkCode" type="button" id="btn" value="发送验证码"/> -->
      </div>

      <div class="login-btn" @click="accountClick">
        登录
      </div>
    </div>
    <div class="check-phone passive">
      <div class="check-wrap-div">
        <div style="width: 1.6rem;display: inline-block">
          <img src="../../assets/images/shouji01.png" alt="">
        </div>
        <input type="text" placeholder="请输入手机号" id="userPhone" name="username" style="width: 50%;color:#AEDCFF;">
        <button style="color: #ffffff;font-size: 0.65rem;
        margin-left: 1rem;background-color:transparent;
        border: none"
                @click="getNumber" id="getCode" disabled="true">
          获取验证码
        </button>
      </div>
      <div class="check-wrap-div">
        <div style="width: 1.6rem;display: inline-block">
          <img src="../../assets/images/shouji02.png" alt="">
        </div>
        <input type="password" placeholder="请输入验证号码" id="userCode" name="userpwd" style="width: 50%">
        <!--   <input class="checkCode" type="button" id="btn" value="发送验证码"/> -->
      </div>
      <p id="message_" style="display: none;color:#E21918;">验证码错误</p>
      <div class="login-btn" @click="phoneClick">
        登录
      </div>
    </div>
    <div class="User_phone">
      使用手机号登录
    </div>
    <div class="User_name passive">
      使用账号登录
    </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  import { Message } from 'element-ui';

  export default {
    name: 'toDetailList',
    data() {
      return {
        toLogin: 'ceshi',
//        verificationCode:'',
        isGetCode : 1,

      }
    },
    mounted(){
      userPhone.onblur=function(){
        if(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/.test(this.value)){
          that_.isGetCode = 1;
        }else if (this.value==""){
          $('#message_').show();
          message_.innerHTML="手机号不能为空，请输入手机号";
          setTimeout(()=>{
            message_.innerHTML="";
          },3000)
          that_.isGetCode = 0;
        }
        else{
          $('#message_').show();
          message_.innerHTML="手机号错误，请重新输入"
          setTimeout(()=>{
            message_.innerHTML="";
          },3000)
          that_.isGetCode = 0;
        }
      },
      $('.User_phone').on('click',function(){
        $('.check-wrap').hide();
        $(this).hide();
        $('.check-phone').show();
        $('.User_name').show();
      });
      $('.User_name').on('click',function(){
        $('.check-phone').hide();
        $(this).hide();
        $('.check-wrap').show();
        $('.User_phone').show();
      })
    },
    //页面加载后调用的函数
    methods: {
      //手机登录页面---------------------1
      //点击获得验证码
      getNumber(){
        let that_=this;
        if(that_.isGetCode == 1) {
          that_.isGetCode = 0;
          let wait = 60;
          let Number_ = $('#userPhone').val();
          $http.get(`/unionH5Brokerage/79B4DE7C/phone/${Number_}`)
            .then(res => {
              //获得验证码成功
              if(res.data.success){
                time(getCode);
              }else {
                that_.isGetCode = 1;
              }
              function time(o) {
                if (wait == 0) {
                  that_.isGetCode = 1;
                  o.removeAttribute("disabled");
                  o.innerHTML = "获取验证码";
                  o.style.color = '#ffffff';
                } else {
                  that_.isGetCode = 0;
                  o.setAttribute("disabled", true);
                  o.innerHTML = "重新发送(" + wait + ")";
                  o.style.color = "#8a7e7e";
                  o.style.fontSize = "0.60rem!important;";
                  wait--;
                  setTimeout(function () {
                      time(o)
                    },
                    1000)
                }
              }
            })
            .catch(err => {
              this.$message({showClose: true, message: err.toString(), type: 'error', duration: 0});
            });
        }
      },
      //账号登录页面----------------------2
      accountClick(){
        var uname=$('#username').val();
        var pwd=$('#userpwd').val();
        var that_=this;
        //登录页面
          var options = {
            url: "/unionH5Brokerage/loginSign",
            data: {username: uname, userpwd: pwd},
            dataType: "json",
            type: "post",
            success: function (res) {
              if (res.data != null && res.data.sign != undefined) {
                var ajaxTimeOut = $.ajax({
                  url: res.data.url,
                  timeout: 10000,
                  type: 'post',
                  data: {
                    login_name: uname,
                    password: pwd,
                    sign: JSON.stringify(res.data.sign)
                  },
                  dataType: "jsonp",
                  jsonp: "callback",
                  jsonpCallback: "successCallback",
                  success: function (res) {
                    if(res.code == 0){
                      setTimeout(function () {
                        location.href = '#/Index';
                      }, 1000);
                    }else {
                      that_.$message({showClose: true, message: res.msg, type: 'error', duration: 3000});
                    }
                  },
                  error: function (XMLHttpRequest, status) {
                    that_.$message({showClose: true, message: '登录失败', type: 'error', duration: 3000});
                  }
                });
              } else {
                that_.$message({showClose: true, message: '登录失败', type: 'error', duration: 3000});
              }
            }
            }
          $.ajax(options);
          },
      //手机登录页面
      phoneClick(){
        var uPhone=$('#userPhone').val();
        var pCode=$('#userCode').val();
        //登录页面
        $http.post(`/unionH5Brokerage/login?phone=${uPhone}&code=${pCode}`)
          .then(res => {
            //跳转到主页面中去
            if(res.data && res.data != undefined && res.data.success) {
              //跳转到主页面
              location.href = '#/Index';
            }else{
              this.$message({
                showClose: true, message: res, type: 'error', duration: 3000
              });
            }
          })
          .catch(err => {
            this.$message({showClose: true, message: err.toString(), type: 'error', duration: 3000});
          });
      },
    },
    //页面创造之前调用函数
    created (){
      //页面的title变换
      $("#title_").text('商家联盟佣金平台');
      //图片中的颜色切换（白和灰切换）
      this.$emit('getValue',this.toLogin);
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>
  .check-wrap-div>input::-webkit-input-placeholder { /* WebKit browsers */
    color:    #AEDCFF!important;
  }
  .check-wrap-div>input:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
    color:    #AEDCFF!important;
  }
  .check-wrap-div>input::-moz-placeholder { /* Mozilla Firefox 19+ */
    color:    #AEDCFF!important;
  }
  .check-wrap-div>input:-ms-input-placeholder { /* Internet Explorer 10+ */
    color:    #AEDCFF!important;
  }
</style>
