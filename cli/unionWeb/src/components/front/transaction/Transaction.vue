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
              <el-input v-model="form1.code" @keyup.enter.native="confirmCode('ruleForm')"></el-input>
            </el-row>
          </el-form-item>
          <el-button type="primary" @click="confirmCode('ruleForm')" style="position: relative;top: -58px;left: 390px;" id="affirm">确认</el-button>
        </el-form>
      </div>
      <!-- 其他办理联盟卡信息 -->
      <el-form :label-position="labelPosition" :model="form2" v-show="visible2" ref="ruleForm2" label-width="120px">
        <div class="selectUnion">
          <p>选择联盟</p>
          <el-form-item label="选择联盟:">
            <el-radio-group v-model="unionId" style="margin-top:10px;margin-bottom: 20px;" @change="unionIdChange">
              <el-radio-button v-for="item in form2.unionList" :key="item.id" :label="item.id">
                <el-tooltip class="item" effect="dark" :content="item.name" placement="bottom">
                  <div class="dddddd clearfix">
                    <img v-bind:src="item.img" alt="" class="fl unionImg">
                    <i></i>
                  </div>
                </el-tooltip>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
        <div class="selectUnionCard">
          <p>选择联盟卡</p>
          <el-form-item>
            <div class="SwitchAround">
              <i class="forward" style="font-style:normal"> &lt;</i>
              <i class="backward" style="font-style:normal"> &gt;</i>
              <div>
                <el-checkbox-group v-model="activityCheckList">
                  <!-- label 写死？ -->
                  <el-checkbox-button v-if="isDiscountCard" :label="0" disabled>
                    <div class="clearfix SelectunionImg1">
                      <!-- todo 更改样式 -->
                      <img alt="" class="fl SelectunionImg " style="background-image: linear-gradient(90deg, #B1503D 0%, #A52B2C 100%)">
                      <div class="fl" style="margin-left: 20px;position: absolute;top: 62px;left: 33px;">
                        <h6 style="margin-bottom: 17px;color: #333333"> 联盟折扣卡 </h6>
                      </div>
                      <i></i>
                    </div>
                  </el-checkbox-button>
                  <el-checkbox-button v-for="(item,index1) in form2.activityList" :key="item.activity.id" :label="item.activity.id">
                    <div class="clearfix">
                      <img v-bind:src="item.activity.img" alt="" class="fl SelectunionImg" :class="'m'+item.activity.color2+index1">
                      <div class="fl" style="margin-left: 20px;position: absolute;top: 62px;left: 33px;">
                        <h6 style="margin-bottom: 17px;color: #333333">{{item.activity.name}}</h6>
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
            <div>享受折扣： {{ discount * 10 }}折 </div>
          </div>
          <!--活动卡服务-->
          <div class="cardService" v-for="item in form2.activityList" :key="item.activity.id" v-show="activityCheckList.indexOf(item.activity.id)> -1">
            <p> {{ item.activity.name }} </p>
            <div style="margin-left: 82px;cursor: pointer;color: #2a2a2a;">服务项目：
              <span @click="showDetail(item.activity.id)">
                <strong style="cursor: pointer;color: #20A0FF;"> {{ item.itemCount }} </strong>个</span>
            </div>
            <div>联盟卡有效天数：
              <span> {{ item.activity.validityDay }} 天</span>
            </div>
            <div style="margin-left: 112px;">价格：
              <span> ￥{{ (item.activity.price).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <el-form-item>
          <el-button type="primary" @click="submitForm('ruleForm2')">确定</el-button>
          <el-button @click="cancel('ruleForm2')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 公众号信息 -->
    <is-follow></is-follow>
    <!-- 弹出框 付款 -->
    <div class="model_1">
      <el-dialog title="付款" :visible.sync="visible3" size="tiny">
        <hr>
        <div class="model_">
          <p><img v-bind:src="codeSrc" class="codeImg"></p>
          <p style="margin-bottom: 50px;">请使用微信扫描该二维码付款</p>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 查看项目详情 -->
    <el-dialog title="查看项目" :visible.sync="detaiVisible">
      <hr>
      <el-table :data="detailTableData">
        <el-table-column prop="member.enterpriseName" label="企业名称"></el-table-column>
        <el-table-column prop="nameList" label="活动卡项目名称">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p v-for="item in scope.row.itemList" :key="item.id">项目名称：{{ item.name }}, 数量：{{ item.number }}</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.nameList }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
import { cellPhonePass } from '@/utils/validator.js';
import IsFollow from '@/components/front/transaction/IsFollow';
export default {
  name: 'transaction',
  components: {
    IsFollow
  },
  data() {
    return {
      labelPosition: 'right',
      fanId: '',
      form1: {
        phone: '',
        code: '',
        getVerificationCode: false,
        countDownTime: ''
      },
      rules: {
        code: [{ required: true, message: '验证码不能为空，请重新输入', trigger: 'blur' }],
        phone: [{ validator: cellPhonePass, trigger: 'blur' }]
      },
      visible2: false,
      form2: {
        unionList: [],
        activityList: []
      },
      unionId: '',
      activityCheckList: [],
      isDiscountCard: '',
      discount: '',
      visible3: false,
      codeSrc: '',
      socket: '',
      socketKey: '',
      socketFlag: {
        socketKey: '',
        status: ''
      },
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
      if (parseFloat(COUNT) / 3 > 1 && j < 0) {
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
      if (parseFloat(COUNT) / 3 > 1 && parseFloat(COUNT) / 3 > i + 1) {
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
  methods: {
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
                      this.unionId = res.data.data.currentUnion.id;
                      this.form2.activityList = res.data.data.cardActivityApplyVOList;
                      if (this.form2.activityList) {
                        this.form2.activityList.forEach((v, i) => {
                          //todo
                          let color1 = (v.activity.color1 = v.activity.color.split(',')[0]);
                          let color2 = (v.activity.color2 = v.activity.color.split(',')[1]);
                          let mDiv = 'm' + color2 + i;
                          setTimeout(function() {
                            $(
                              '.' + mDiv
                            )[0].style.backgroundImage = `linear-gradient(90deg, #${color1} 0%, #${color2} 100%)`;
                          }, 0);
                        });
                      }
                      this.activityCheckList = [];
                      this.isDiscountCard = res.data.data.isDiscountCard;
                      this.discount = res.data.data.currentMember.discount || 1;
                      this.visible2 = true;
                    } else {
                      this.form2.unionList = [];
                      this.form2.activityList = [];
                      this.isDiscountCard = '';
                      this.discount = 1;
                      this.visible2 = false;
                      this.$message({ showClose: true, message: '您已办理联盟卡', type: 'error', duration: 5000 });
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
    // 更换选择联盟
    unionIdChange() {
      if (this.unionId) {
        $http
          .get(`/unionCard/fanId/${this.fanId}/apply?unionId=${this.unionId}`)
          .then(res => {
            if (res.data.data) {
              this.form2.activityList = res.data.data.cardActivityApplyVOList;
              //底部切换回到原始的位置;
              $('.SwitchAround .el-checkbox-group').css({
                left: 0 + 'px'
              });
              if (this.form2.activityList) {
                this.form2.activityList.forEach((v, i) => {
                  //todo
                  let color1 = (v.activity.color1 = v.activity.color.split(',')[0]);
                  let color2 = (v.activity.color2 = v.activity.color.split(',')[1]);
                  let mDiv = 'm' + color2 + i;
                  setTimeout(function() {
                    $('.' + mDiv)[0].style.backgroundImage = `linear-gradient(90deg, #${color1} 0%, #${color2} 100%)`;
                  }, 0);
                });
              }
              this.activityCheckList = [];
              this.isDiscountCard = res.data.data.isDiscountCard;
              this.discount = res.data.data.currentMember.discount || 1;
              this.visible2 = true;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 显示项目详情
    showDetail(id) {
      $http
        .get(`/unionCardActivity/${id}/unionId/${this.unionId}/apply/itemCount`)
        .then(res => {
          if (res.data.data) {
            this.detailTableData = res.data.data;
            this.detailTableData.forEach(v => {
              v.nameList = [];
              v.itemList.forEach(val => {
                v.nameList.push(val.name);
              });
              v.nameList = v.nameList.join(',');
            });
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
      let url = `/unionCard/fanId/${this.fanId}/unionId/${this.unionId}/apply`;
      let data = [];
      if (!this.isDiscountCard && this.activityCheckList.length < 0) {
        this.$message({ showClose: true, message: '请选择活动卡', type: 'error', duration: 5000 });
        return false;
      } else {
        this.activityCheckList.forEach(v => {
          data.push(v);
        });
      }
      $http
        .post(url, data)
        .then(res => {
          if (res.data.data) {
            this.codeSrc = res.data.data.payUrl;
            this.socketKey = res.data.data.socketKey;
            this.visible3 = true;
            var _this = this;
            var socketUrl = this.$store.state.socketUrl;
            if (!this.socket) {
              this.socket = io.connect(socketUrl);
              var socketKey = this.socketKey;
              this.socket.on('connect', function() {
                let jsonObject = { userId: socketKey, message: '0' };
                _this.socket.emit('auth', jsonObject);
              });
              //重连机制
              let socketindex = 0;
              this.socket.on('reconnecting', function() {
                socketindex += 1;
                if (socketindex > 4) {
                  _this.socket.destroy(); //不在链接
                }
              });
              this.socket.on('chatevent', function(data) {
                let msg = eval('(' + data.message + ')');
                console.log(msg, 'transaction');
                // 避免 socket 重复调用
                if (!(_this.socketFlag.socketKey == msg.socketKey && _this.socketFlag.status == msg.status)) {
                  if (_this.socketKey == msg.socketKey) {
                    if (msg.status == '1') {
                      _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 5000 });
                      _this.socketFlag.socketKey = msg.socketKey;
                      _this.socketFlag.status = msg.status;
                      _this.init();
                    } else if (msg.status == '0') {
                      _this.$message({ showClose: true, message: '支付失败', type: 'warning', duration: 5000 });
                    }
                  }
                }
              });
            }
          } else if (res.data.success) {
            this.$message({ showClose: true, message: '办理成功', type: 'success', duration: 5000 });
            clearInterval(this.timeEnd);
            //灰色倒计时'60s'变为紫蓝色"获取验证码"按钮;
            this.init();
            this.form1.countDownTime = '';
            this.form1.getVerificationCode = false;
          } else {
            this.codeSrc3 = '';
            this.socketKey3 = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
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
</style>

