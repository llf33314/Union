<template>
  <div id="transaction" class="clearfix">
    <div class="uninCardContent">
      <div>
        <p>办理联盟卡</p>
        <!-- 手机号，验证码 -->
        <el-form :label-position="labelPosition" :model="form1" :rules="rules" ref="ruleForm" label-width="120px">
          <el-form-item label="手机号码：" prop="phone">
            <el-col style="width: 250px;margin-right: 20px;">
              <el-input v-model="form1.phone" @keyup.native="form1.phone=form1.phone.replace(/[^\d]/g,'')" @keyup.enter.native="getVerificationCode"></el-input>
            </el-col>
            <el-button type="primary" @click="getVerificationCode" :disabled="form1.getVerificationCode || !form1.phone">{{ form1.countDownTime>0?form1.countDownTime+'s':'获取验证码' }}</el-button>
          </el-form-item>
          <el-form-item label="短信验证码：" prop="code">
            <el-row style="width: 250px;">
              <el-input v-model="form1.code"></el-input>
            </el-row>
          </el-form-item>
          <el-button type="primary" @click="confirmCode('ruleForm')" style="position: relative;top: -58px;left: 390px;" id="affirm">确认</el-button>
          <el-form-item label="关注公众号办理:" v-if="form1.follow">
            <el-switch v-model="follow_" on-text="" off-text="">
            </el-switch>
          </el-form-item>
        </el-form>
      </div>
      <!-- 其他办理联盟卡信息 -->
      <el-form :label-position="labelPosition" :model="form2" v-show="visible2" :rules="rules2" ref="ruleForm2" label-width="120px">
        <div class="selectUnion">
          <p>选择联盟</p>
          <el-form-item label="选择联盟:" prop="unionId">
            <el-radio-group v-model="form2.unionId" style="margin-top:10px;margin-bottom: 20px;">
              <el-radio-button v-for="item in form2.unionList" :key="item.id" :label="item.id">
                <div class="dddddd clearfix">
                  <img v-bind:src="item.img" alt="" class="fl unionImg">
                  <div class="fl" style="margin-left: 20px;position: absolute;top: 90px;left: -15px;">
                    <h6 style="margin-bottom: 17px">{{item.name}}</h6>
                  </div>
                  <i></i>
                </div>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
        <div class="selectUnionCard">
          <p>选择联盟卡</p>
          <el-form-item label="" prop="activityCheckList">
            <div class="SwitchAround">
              <button class="forward">
              </button>
              <button class="backward"> > </button>
              <div>
                <el-checkbox-group v-model="form2.activityCheckList">
                  <el-checkbox-button v-if="isDiscountCard" :label="form2.currentMember.id" disabled>
                    <div class="clearfix">
                      <!-- todo 更改样式 -->
                      <img alt="" class="fl SelectunionImg">
                      <div class="fl" style="margin-left: 20px;position: absolute;top: 125px;left: -7px;">
                        <h6 style="margin-bottom: 17px;color: #333333"> 联盟折扣卡 </h6>
                      </div>
                      <i></i>
                    </div>
                  </el-checkbox-button>
                  <el-checkbox-button v-for="item in form2.activityList" :key="item.id" :label="item.id">
                    <div class="clearfix">
                      <img v-bind:src="item.img" alt="" class="fl SelectunionImg">
                      <div class="fl" style="margin-left: 20px;position: absolute;top: 125px;left: -7px;">
                        <h6 style="margin-bottom: 17px;color: #333333">{{item.name}}</h6>
                      </div>
                      <i></i>
                    </div>
                  </el-checkbox-button>
                </el-checkbox-group>
              </div>
            </div>
          </el-form-item>
        </div>
        <!-- 活动卡详情 -->
        <div class="ActivityCardDetails">
          <!--联盟卡折扣-->
          <div class="UnionDiscountCard" v-if="isDiscountCard">
            <p>联盟折扣卡</p>
            <div>享受折扣： {{ discount }}折 </div>
          </div>
          <!--活动卡服务-->
          <div class="cardService" v-for="item in form2.activityList" :key="item.id" v-show="form2.activityCheckList.indexOf(item.id) > -1">
            <p> {{ item.name }} </p>
            <div style="margin-left: 82px;">服务项目：
              <span @click="showDetail(item.id)"> {{ item.itemCount }} 个</span>
            </div>
            <div>联盟卡有效天数：
              <span> {{ item.validityDay }} 天</span>
            </div>
            <div style="margin-left: 112px;">价格：
              <span> ￥{{ item.price }}</span>
            </div>
          </div>
        </div>
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
    <!-- 弹出框 付款 -->
    <div class="model_1">
      <el-dialog title="付款" :visible.sync="visible3" size="tiny">
        <hr>
        <div class="model_">
          <p><img v-bind:src="codeSrc2" class="codeImg"></p>
          <p style="margin-bottom: 50px;">请使用微信扫描该二维码付款</p>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 查看项目详情 -->
    <el-dialog title="查看项目" :visible.sync="detaiVisible">
      <hr>
      <el-table :data="detailTableData">
        <el-table-column prop="member.enterpriseName" label="企业名称"></el-table-column>
        <el-table-column prop="itemList.name" label="活动卡项目名称"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
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
      fanId: '',
      form1: {
        phone: '',
        code: '',
        getVerificationCode: false,
        countDownTime: '',
        follow: ''
      },
      follow_: '',
      rules: {
        code: [{ required: true, message: '验证码不能为空，请重新输入', trigger: 'blur' }],
        phone: [{ validator: phonePass, trigger: 'blur' }]
      },
      visible2: false,
      form2: {
        unionList: [],
        unionId: '',
        activityList: [],
        activityCheckList: []
      },
      isDiscountCard: '',
      discount: '',
      rules2: {
        unionId: [{ type: 'number', required: true, message: '请选择联盟', trigger: 'change' }],
        activityCheckList: [{ type: 'array', required: true, message: '请选择联盟卡', trigger: 'change' }]
      },
      visible3: false,
      codeSrc1: '',
      codeSrc2: '',
      socket1: '',
      socketKey1: '',
      socketFlag1: {
        socketKey: '',
        status: ''
      },
      wxData: {},
      wxData_: {},
      visible5: true,
      memberId: '',
      socket2: '',
      socketKey2: '',
      socketFlag2: {
        socketKey: '',
        status: ''
      },
      wxUser: false,
      timeEnd: '',
      detaiVisible: false,
      detailTableData: []
    };
  },
  mounted: function() {
    // 切换tab清空输入数据
    eventBus.$on('tabChange3', () => {
      if (this.timeEnd) {
        clearInterval(this.timeEnd);
      }
      this.init();
    });
    const LIWIDTH = 200; //宽度
    const OFFSET = 13; //起始的距离
    var moved = 0; //左移的卡片个数
    var this_ = this;
    var i = 0; //左移次数
    var j = 0; //右移次数
    $('.forward').click(() => {
      var COUNT = this_.form2.activityList.length;
      if (parseFloat(COUNT) / 4 > 1 && j < 0) {
        i--;
        j++;
        moved -= 4;
        var offesetLength = -moved * (LIWIDTH + OFFSET);
        $('.SwitchAround .el-checkbox-group').css({
          left: offesetLength + 'px'
        });
      }
    });
    $('.backward').click(() => {
      var COUNT = this_.form2.activityList.length;
      if (parseFloat(COUNT) / 4 > 1 && parseFloat(COUNT) / 4 > i + 1) {
        i++;
        j--;
        moved += 4;
        var offesetLength = -moved * (LIWIDTH + OFFSET);
        $('.SwitchAround .el-checkbox-group').css({
          left: offesetLength + 'px'
        });
      }
    });
  },
  watch: {
    follow_: function() {
      if (this.follow_) {
        this.visible5 = true;
      } else {
        this.memberId = '';
      }
    },
    wxData: function() {
      this.visible5 = false;
      this.wxData_.headurl = this.wxData.headurl;
      this.wxData_.memberId = this.wxData.memberId;
      this.wxData_.nickName = this.wxData.nickName;
      this.wxData_.time = this.wxData.time;
    }
  },
  methods: {
    // 能否关注公众号 然后获取二维码
    canFollow() {
      $http
        .get(`/api/user/wxPublicUser`)
        .then(res => {
          if (res.data.data) {
            this.form1.follow = res.data.data;
          } else {
            this.form1.follow = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
      var _this = this;
      if (this.form1.follow && !this.wxUser) {
        this.wxUser = true;
        $http
          .get(`/api/user/wxPublicUserQRCode/${this.form1.follow}`)
          .then(res => {
            if (res.data.data) {
              this.codeSrc1 = res.data.data.qrCodeUrl;
              this.socketKey1 = res.data.data.qrCodeSocketKey;
            } else {
              this.codeSrc1 = '';
              this.socketKey1 = '';
            }
          })
          .then(res => {
            if (!this.socket1) {
              var _this = this;
              var socketUrl1 = this.$store.state.socketUrl;
              this.socket1 = io.connect(socketUrl1);
              var socketKey1 = this.socketKey1;
              this.socket1.on('connect', function() {
                let jsonObject = { userId: socketKey1, message: '0' };
                _this.socket1.emit('auth', jsonObject);
              });
            }
            this.socket1.on('chatevent', function(data) {
              if (_this.visible5) {
                let msg = eval('(' + data.message + ')');
                _this.wxData = msg;
              }
            });
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 获取验证码
    getVerificationCode() {
      $http
        .get(`/api/sms/2?phone=${this.form1.phone}`)
        .then(res => {
          if (res.data.success) {
            this.form1.getVerificationCode = true;
            this.form1.countDownTime = 60;
            this.timeEnd = setInterval(() => {
              if (this.form1.countDownTime === 0) {
                this.form1.getVerificationCode = false;
                this.form1.countDownTime = '';
                clearInterval(this.timeEnd);
                return;
              }
              this.form1.countDownTime--;
            }, 1000);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 确认验证码
    confirmCode(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionCard/apply/phone`;
          let data = {
            phone: this.form1.phone,
            code: this.form1.code
          };
          $http
            .post(url, data)
            .then(res => {
              if (res.data.data) {
                clearInterval(this.timeEnd);
                //灰色倒计时'60s'变为紫蓝色"获取验证码"按钮;
                this.form1.countDownTime = '';
                this.form1.getVerificationCode = false;
                this.fanId = res.data.data.id;
                $http
                  .get(`/unionCard/fanId/${this.fanId}/apply`)
                  .then(res => {
                    if (res.data.data) {
                      this.form2 = res.data.data;
                      this.form2.unionList = res.data.data.unionList;
                      this.form2.unionId = res.data.data.currentUnion.id;
                      this.form2.activityList = res.data.data.activityList;
                      this.isDiscountCard = res.data.data.isDiscountCard;
                      if (this.isDiscountCard) {
                        this.activityCheckList.push(this.form2.unionId);
                      }
                      this.discount = res.data.data.currentMember.discount;
                      this.visible2 = true;
                    } else {
                      this.form2.unionList = [];
                      this.form2.unionId = '';
                      this.form2.activityList = [];
                      this.isDiscountCard = '';
                      this.discount = '';
                      this.visible2 = false;
                    }
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      });
    },
    // 获取活动卡详情
    getActivityDetal() {
      this.form2.activityList.forEach((v, i) => {
        $http
          .get(`/unionCardActivity/${v.id}/unionId/${this.form2.unionId}/apply`)
          .then(res => {
            if (res.data.data) {
              v.itemCount = res.data.data.itemCount;
              v.validityDay = res.data.data.activity.validityDay;
              v.price = res.data.data.activity.price;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      });
    },
    // 显示项目详情
    showDetail(id) {
      $http
        .get(`/unionCardActivity/${id}/unionId/${this.form2.unionId}/apply/itemCount`)
        .then(res => {
          if (res.data.data) {
            this.detailTableData = res.data.data;
            this.detaiVisible = true;
          } else {
            this.detailTableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 提交
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionCard/fanId/${this.fanId}/unionId/${this.form2.unionId}/apply`;
          let data = {};
          data.cardType = this.form2.cardType;
          data.follow = this.follow_;
          if (this.follow_) {
            if (this.memberId) {
              data.memberId = this.memberId - 0;
            } else {
              this.$message({ showClose: true, message: '请选择粉丝', type: 'warning', duration: 5000 });
              return false;
            }
          } else {
            data.memberId = '';
          }
          data.phone = this.form1.phone;
          data.unionId = this.form2.unionId;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success && res.data.data.qrurl) {
                this.visible3 = true;
                $http
                  .get(
                    `/unionCard/qrCode?phone=${data.phone}&memberId=${data.memberId}&unionId=${data.unionId}&cardType=${data.cardType}`
                  )
                  .then(res => {
                    if (res.data.data) {
                      this.codeSrc2 = res.data.data.url;
                      this.userId2 = res.data.data.userId;
                    } else {
                      this.codeSrc2 = '';
                      this.only = '';
                      this.userId2 = '';
                    }
                  })
                  .then(res => {
                    var _this = this;
                    if (!this.socket2) {
                      this.socket2 = io.connect('https://socket.deeptel.com.cn'); // 测试
                      // this.socket2 = io.connect('http://183.47.242.2:8881'); // 堡垒
                      var userId = this.userId2;
                      this.socket2.on('connect', function() {
                        let jsonObject = { userId: userId, message: '0' };
                        _this.socket2.emit('auth', jsonObject);
                      });
                    }
                    this.socket2.on('chatevent', function(data) {
                      let msg = eval('(' + data.message + ')');
                      // 避免 socket 重复调用
                      if (!(_this.socketFlag.socketKey == msg.socketKey && _this.socketFlag.status == msg.status)) {
                        if (_this.socketKey == msg.socketKey) {
                          if (msg.status == '1') {
                            _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 5000 });
                            _this.socketFlag.socketKey = msg.socketKey;
                            _this.socketFlag.status = msg.status;
                            _this.visible1 = false;
                            _this.$router.push({ path: '/my-union' });
                          } else if (msg.status == '0') {
                            _this.$message({ showClose: true, message: '支付失败', type: 'warning', duration: 5000 });
                          }
                        }
                      }
                    });
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              } else if (res.data.success) {
                this.$message({ showClose: true, message: '办理成功', type: 'success', duration: 5000 });
                clearInterval(this.timeEnd);
                //灰色倒计时'60s'变为紫蓝色"获取验证码"按钮;
                this.init();
                this.form1.countDownTime = '';
                this.form1.getVerificationCode = false;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      });
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
      this.form1.getVerificationCode = false;
      this.form1.countDownTime = 0;
      this.visible2 = false;
      this.visible3 = false;
      this.visible5 = true;
    },
    // 下载二维码
    downloadCode() {
      let alink = document.createElement('a');
      alink.href = this.codeSrc1;
      alink.download = 'wx.jpg';
      alink.click();
    }
  }
};
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
  margin-top: 30px;
  border: 1px solid #ddd;
  height: 720px;
  overflow: auto;
  width: 420px;
  > p {
    background: #eef1f6;
    padding: 15px 30px;
    font-size: 12px;
  }
  /*二维码的显示框的样式*/
  .middle_ {
    text-align: center;
    padding: 50px 42px 40px 58px;
    > img {
      display: block;
      margin: 0 40px 30px 40px;
    }
  }
  > span {
    display: block;
    margin: 0px 42px 40px 58px;
    border-top: 1px solid #ddd;
    padding-top: 40px;
    p {
      color: #999999;
    }
  }
}
.drop_down1 {
  margin-top: 30px;
  border: 1px solid #ddd;
  overflow: auto;
  width: 420px;
  > p {
    background: #eef1f6;
    padding: 15px 30px;
    font-size: 12px;
  }
}
</style>

