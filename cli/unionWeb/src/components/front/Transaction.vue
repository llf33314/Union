<template>
  <div id="transaction" class="clearfix">
    <div style="float: left;width: 50%;">
      <!-- 手机号，验证码 -->
      <el-form :label-position="labelPosition" :model="form1" :rules="rules" ref="ruleForm" label-position="right" label-width="120px">
        <el-form-item label="手机号码：" prop="phone">
          <el-col style="width: 250px;margin-right: 20px;">
            <el-input v-model="form1.phone"></el-input>
          </el-col>
          <el-button type="primary" @click="getVerificationCode" :disabled="form1.getVerificationCode || !form1.phone">获取验证码{{ form1.countDownTime}}</el-button>
        </el-form-item>
        <el-form-item label="短信验证码：" prop="code">
          <!--176072-->
          <el-row style="width: 250px;">
            <el-input v-model="form1.code"></el-input>
          </el-row>
        </el-form-item>
        <el-button type="primary" @click="confirmCode('ruleForm')" style="position: relative;top: -58px;left: 390px;" id="affirm">确认</el-button>
      </el-form>
      <!-- 其他办理联盟卡信息 -->
      <el-form :label-position="labelPosition" :model="form2" v-show="visible2" :rules="rules2" ref="ruleForm2" label-position="right" label-width="120px">
        <el-form-item label="关注公众号办理:">
          <el-switch v-model="follow_" on-text="" off-text="" v-if="form2.follow">
          </el-switch>
        </el-form-item>
        <div class="selectUnion">
          <el-form-item label="选择联盟:" prop="unionId">
            <el-radio-group v-model="unionId" style="margin-top:10px;margin-bottom: 20px;">
              <el-radio-button v-for="item in form2.unions" :key="item.id" :label="item.id">
                <div class="dddddd clearfix">
                  <img v-bind:src="item.img" alt="" class="fl unionImg">
                  <div class="fl" style="margin-left: 20px;position: absolute;top: 90px;left: -7px;">
                    <h6 style="margin-bottom: 17px">{{item.name}}</h6>
                  </div>
                  <i></i>
                </div>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="联盟卡类型:" prop="cardType">
          <el-radio-group v-model="form2.cardType">
            <el-radio :label="2">红卡</el-radio>
            <el-radio :label="1">黑卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联盟卡有效期:" v-if="form2.cardType === 2">
          {{ form2.cards.red.termTime }}
        </el-form-item>
        <el-form-item label="价格:" v-if="form2.cardType === 2">
          ￥{{ form2.cards.red.price | formatPrice }}
        </el-form-item>
        <el-form-item label="联盟卡有效期:" v-if="form2.cardType === 1">
          {{ form2.cards.black.termTime }}
        </el-form-item>
        <el-form-item label="价格:" v-if="form2.cardType === 1">
          ￥{{ form2.cards.black.price | formatPrice }}
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm2')">确定</el-button>
          <el-button @click="cancel('ruleForm2')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 是否关注公众号办理 -->
    <div class="fr drop_down" v-if="this.follow_" v-show="visible5">
      <p>扫码二维码关注办理联盟卡</p>
      <div class="middle_">
        <img v-bind:src="codeSrc1" class="codeImg" style="width:240px;height:240px;">
        <div class="code_">
          <el-button @click="downloadCode">下载该二维码</el-button>
        </div>
      </div>
      <span>
        <p>提示：</p>
        <p>1.请确认用户扫码关注成功后，才可出现粉丝列表进行下一步操作。</p>
        <p>2.已关注过公众号的粉丝，扫码成功后将不在新增粉丝列表中呈现，粉丝可在公众号进行联盟卡注册的相关操作。</p>
      </span>
    </div>
    <!-- 今日新增粉丝信息 -->
    <div class="fr drop_down1 step2" v-if="this.follow_" v-show="!visible5">
      <p>今日新增粉丝信息，点击对应头像可与联盟手机号绑定</p>
      <el-radio-group v-model="memberId">
        <el-radio-button :label="wxData_.memberId">
          <div class="dddddd clearfix">
            <img v-bind:src="wxData_.headurl" alt="" class="fl unionImg">
            <div class="fl" style="margin-left: 20px">
              <h6 style="margin-bottom: 17px">{{ wxData_.nickName }}</h6>
              <span>时间：{{ wxData_.time }} </span>
            </div>
            <i></i>
          </div>
        </el-radio-button>
      </el-radio-group>
    </div>

    <!-- 弹出框 提现 -->
    <div class="model_1">
      <el-dialog title="付款" :visible.sync="visible3" size="tiny">
        <hr>
        <div class="model_">
          <p><img v-bind:src="codeSrc2" class="codeImg" style="width:240px;height:240px;"></p>
          <p style="margin-bottom: 50px;">请使用微信扫描该二维码付款</p>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 消费核销成功 -->
    <div class="verSuccess">
      <el-dialog title="办理联盟卡成功" :visible.sync="visible4" size="tiny">
        <hr>
        <div class="clear ddd">
          <div class="fl"><img src="../../assets/images/success01.png" alt=""></div>
          <div class="fl">
            <p>消费核销成功</p>
            <P> {{ countTime }} 秒后返回“联盟卡消费核销”页面... </P>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'transaction',
  data() {
    let phonePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('手机号码内容不能为空，请重新输入'));
      } else if (!value.match(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    return {
      labelPosition: 'right',
      form1: {
        phone: '',
        code: '',
        getVerificationCode: false,
        countDownTime: '',
      },
      rules: {
        code: [
          { required: true, message: '验证码不能为空，请重新输入', trigger: 'blur' }
        ],
        phone: [
          { validator: phonePass, trigger: 'blur' }
        ],
      },
      visible2: false,
      follow_: '',
      form2: {
        follow: false,
        cardType: '',
        red: {
          price: '',
          termTime: '',
        },
        black: {
          price: '',
          termTime: '',
        },
      },
      unionId: '',
      rules2: {
        unionId: [
          { type: 'number', required: true, message: '请选择联盟', trigger: 'change' }
        ],
        cardType: [
          { type: 'number', required: true, message: '请选择联盟卡类型', trigger: 'change' }
        ],
      },
      visible3: false,
      visible4: false,
      countTime: 3,
      codeSrc1: '',
      codeSrc2: '',
      socketurl: '',
      userId1: '',
      socket1: '',
      socketFlag1: {
        only: '',
        status: '',
      },
      wxData: {},
      wxData_: {},
      visible5: true,
      memberId: '',
      only: '',
      userId2: '',
      socket2: '',
      socketFlag2: {
        only: '',
        status: '',
      },
    }
  },
  mounted: function() {
    $http.get(`/unionCard/wxUser/QRcode`)
      .then(res => {
        if (res.data.data) {
          this.codeSrc1 = res.data.data.qrurl;
          this.socketurl = res.data.data.socketurl;
          this.userId1 = res.data.data.userId;
        } else {
          this.codeSrc1 = '';
          this.socketurl = '';
          this.userId1 = '';
        }
      })
      .then(res => {
        var _this = this;
        if (!this.socket1) {
          this.socket1 = io.connect('https://socket.deeptel.com.cn'); // 测试
          // this.socket1 = io.connect('http://183.47.242.2:8881'); // 堡垒
          var userId = this.userId1;
          this.socket1.on('connect', function() {
            let jsonObject = { userId: userId, message: "0" };
            _this.socket1.emit('auth', jsonObject);
          });
        }
        this.socket1.on('chatevent', function(data) {
          let msg = eval('(' + data.message + ')');
          _this.wxData = msg;
        });
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  watch: {
    follow_: function() {
      if (this.follow_) {
        this.visible5 = true;
      }
    },
    wxData: function() {
      this.visible5 = false;
      this.wxData_.headurl = this.wxData.headurl;
      this.wxData_.memberId = this.wxData.memberId;
      this.wxData_.nickName = this.wxData.nickName;
      this.wxData_.time = this.wxData.time;
    },
    unionId: function() {
      this.form2.unionId = this.unionId;
    }
  },
  filters: {
    // 价格格式
    formatPrice: function(value) {
      if (!value) {
        return ''
      } else {
        return (Number(value)).toFixed(2);
      }
    }
  },
  methods: {
    // 获取验证码
    getVerificationCode() {
      $http.get(`/unionCard/phoneCode?phone=${this.form1.phone}`)
        .then(res => {
          if (res.data.data) {
            this.form1.getVerificationCode = true;
            this.form1.countDownTime = 60;
            let timer1 = setInterval(() => {
              this.form1.countDownTime--;
              if (this.form1.countDownTime === 0) {
                clearInterval(timer1);
                this.form1.getVerificationCode = false;
                this.form1.countDownTime = '';
              }
            }, 1000)
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 确认验证码
    confirmCode(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          $http.get(`/unionCard/info?phone=${this.form1.phone}&code=${this.form1.code}`)
            .then(res => {
              if (res.data.data) {
                this.form2.unions = res.data.data.unions;
                this.form2.follow = res.data.data.follow;
                this.form2.cards = res.data.data.cards;
                this.visible2 = true;
              } else {
                this.form2.unions = '';
                this.form2.follow = '';
                this.form2.cards = '';
                this.visible2 = false;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      })
    },
    // 提交
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let url = `/unionCard`;
          let data = {};
          data.cardType = this.form2.cardType;
          data.follow = this.follow_;
          data.memberId = this.memberId - 0;
          data.phone = this.form1.phone;
          data.unionId = this.unionId;
          $http.post(url, data)
            .then(res => {
              if (res.data.success && res.data.data) {
                this.visible3 = true;
                $http.get(`/unionCard/qrCode?phone=${data.phone}&memberId=${data.memberId}&unionId=${data.unionId}&cardType=${data.cardType}`)
                  .then(res => {
                    if (res.data.data) {
                      this.codeSrc2 = res.data.data.url;
                      this.only = res.data.data.only;
                      this.userId2 = res.data.data.userId;
                    } else {
                      this.codeSrc2 = '';
                      this.only = '';
                      this.userId2 = '';
                    };
                  })
                  .then(res => {
                    var _this = this;
                    if (!this.socket2) {
                      this.socket2 = io.connect('https://socket.deeptel.com.cn'); // 测试
                      // this.socket2 = io.connect('http://183.47.242.2:8881'); // 堡垒
                      var userId = this.userId2;
                      this.socket2.on('connect', function() {
                        let jsonObject = { userId: userId, message: "0" };
                        _this.socket2.emit('auth', jsonObject);
                      });
                    }
                    this.socket2.on('chatevent', function(data) {
                      let msg = eval('(' + data.message + ')');
                      if (!(_this.socketFlag2.only == msg.only && _this.socketFlag2.status == msg.status)) {
                        if (_this.only == msg.only) {
                          if (msg.status == '003') {
                            _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 5000 });
                            _this.visible3 = false;
                            _this.visible4 = true;
                            _this.init();
                          } else if (msg.status == '004') {
                            _this.$message({ showClose: true, message: '请求超时', type: 'warning', duration: 5000 });
                          } else if (msg.status == '005') {
                            _this.$message({ showClose: true, message: '支付失败', type: 'warning', duration: 5000 });
                          };
                        };
                      };
                      _this.socketFlag2.only = msg.only;
                      _this.socketFlag2.status = msg.status;
                    });
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              } else if (res.data.success) {
                this.visible4 = true;
                eventBus.$emit('updataExpenseRecord');
                let timer1 = setInterval(() => {
                  if (this.countTime == 0) {
                    clearInterval(timer1);
                    this.init();
                  }
                  this.countTime--;
                }, 1000);
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      })
    },
    // 取消
    cancel(formName) {
      this.$refs[formName].resetFields();
      this.init();
      affirm.style.display = 'block';
    },
    // 初始化
    init() {
      this.form1.phone = '';
      this.form1.code = '';
      this.visible2 = false;
      this.visible3 = false;
      this.visible4 = false;
      this.visible5 = true;
    },
    // 下载二维码
    downloadCode() {
      let alink = document.createElement('a');
      alink.href = this.codeSrc1;
      alink.download = 'wx.jpg';
      alink.click();
    },
  }
}
</script>

<style lang='less' rel="stylesheet/less" scoped>
.unionImg {
  width: 80px;
  height: 80px;
}

.codeImg {
  width: 200px;
  height: 200px;
}
/*右边下拉框的样式*/

.drop_down {
  border: 1px solid #ddd;
  height: 720px;
  overflow: auto;
  width: 420px;
  >p {
    background: #EEF1F6;
    padding: 15px 30px;
    font-size: 12px;
  }
  /*二维码的显示框的样式*/
  .middle_ {
    text-align: center;
    padding: 50px 42px 40px 58px;
    >img {
      display: block;
      margin: 0 40px 30px 40px;
    }
  }
  >span {
    display: block;
    margin: 0px 42px 40px 58px;
    border-top: 1px solid #ddd;
    padding-top: 40px;
    p{
      color:#999999;
    };
  }
}
.drop_down1 {
  border: 1px solid #ddd;
  overflow: auto;
  width: 420px;
  > p {
    background: #EEF1F6;
    padding: 15px 30px;
    font-size: 12px;
  }
}
</style>

