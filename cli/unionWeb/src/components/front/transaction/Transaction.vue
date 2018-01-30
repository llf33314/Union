<template>
  <div id="transaction">
    <!-- 手机号，验证码 -->
    <nav class="handleCardInformation">
      <p class="cardInformation">办卡信息</p>
      <el-form :label-position="'right'" :model="form1" :rules="rules1" ref="ruleForm" label-width="120px">
        <el-form-item label="办卡手机号：" prop="phone">
          <el-col style="width: 250px;margin-right: 20px;">
            <el-input v-model="form1.phone" placeholder="请输入需要办理联盟卡的手机号" @keyup.native="form1.phone=form1.phone.replace(/[^\d]/g,'')"
              @keyup.enter.native="getVerificationCode" @change="phoneChange"></el-input>
          </el-col>
          <el-button type="primary" @click="getVerificationCode" :disabled="form1.getVerificationCode || !form1.phone">{{form1.countDownTime>0?form1.countDownTime+'s':'获取验证码'}}</el-button>
        </el-form-item>
        <el-form-item label="短信验证码：" prop="code">
          <el-row style="width: 250px;">
            <el-input v-model="form1.code" placeholder="请输入短信验证码" @keyup.enter.native="confirmCode('ruleForm')"></el-input>
          </el-row>
        </el-form-item>
        <el-button type="primary" @click="confirmCode('ruleForm')" style="position: relative;top: -58px;left: 390px;" id="affirm">确认</el-button>
      </el-form>
    </nav>
    <!-- 联盟卡列表 -->
    <main class="unionCardList">
      <p class="cardInformation">联名卡列表</p>
    <div class="unionCardListStatic">
      <el-checkbox-group v-model="checkList1" @change="checkListChange1">
        <el-checkbox-button v-if="unionCardList.discountCardList.length" v-for="item in unionCardList.discountCardList" :key="item.union.id" :label="item.union.id" :disabled="item.disabledFlag">
          <p v-show="item.disabled">已办理</p>
          <p>{{item.union.name}}</p>
          <p>免费</p>
          <!-- 折扣卡详情 -->
          <div>
            <p>{{item.union.name}}</p>
            <p>消费特权：可在下列商家消费时享受折扣</p>
            <ol>
              <li v-for="item1 in item.memberList" :key="item1.id">
                <span>{{item1.enterpriseName}}</span>
                <span v-if="item1.discount">{{(item1.discount*10).toFixed(1)}}折</span>
                <span v-else> 无折扣</span>
              </li>
            </ol>
          </div>
        </el-checkbox-button>
      </el-checkbox-group>
      <el-checkbox-group v-model="checkList2" @change="checkListChange2">
        <el-checkbox-button v-if="unionCardList.activityCardList.length" v-for="item in unionCardList.activityCardList" :key="item.activity.id" :label="item.activity.id" :disabled="item.disabledFlag">
          <p v-show="item.disabled">已办理</p>
          <p>{{item.activity.name}}</p>
          <p>{{item.activity.price}}</p>
          <p>{{item.activity.color}}</p>
          <!-- 活动卡详情 -->
          <p>{{item.activity.name}}</p>
          <p>有效期：购买后{{item.activity.validityDay}}天内</p>
          <p>优惠项目：共{{item.projectItemCount}}项</p>
          <ol>
            <li v-for="item1 in item.cardProjectList" :key="item1.member.id">
              <span>{{item1.member.enterpriseName}}</span>
              <ul>
                <li v-for="item2 in item1.projectItemList" :key="item2.id" :label="item2.id">
                  <span>{{item2.name}}</span>
                  <!-- todo * 样式更换 -->
                  <span>*{{item2.number}}</span>
                </li>
              </ul>
            </li>
          </ol>
        </el-checkbox-button>
      </el-checkbox-group>
    </div>
    </main>
    <footer>
      <div v-if="checkList1.length||checkList2.length">
        <p class="cardInformation">办卡列表</p>
        <p v-if="unionCardList.discountCardList.length" v-for="item in unionCardList.discountCardList" :key="item.union.id" v-show="checkList1.indexOf(item.union.id)> -1">
          <span>{{item.union.name}}</span>
          <span>免费</span>
          <span>
            <el-button @click="del1(item)">删除</el-button>
          </span>
        </p>
        <p v-if="unionCardList.activityCardList.length" v-for="item in unionCardList.activityCardList" :key="item.activity.id" v-show="checkList2.indexOf(item.activity.id)> -1">
          <span>{{item.activity.name}}</span>
          <span>{{item.activity.price}}</span>
          <span>
            <el-button @click="del2(item)">删除</el-button>
          </span>
        </p>
        <p>合计：￥{{payPrice}}</p>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>

  </footer>

    <!-- 弹出框 付款 -->
    <div class="model_1">
      <el-dialog title="付款" :visible.sync="visible2" size="tiny">
        <hr>
        <div class="model_">
          <p>
            <img v-bind:src="codeSrc" class="codeImg">
          </p>
          <p>￥
            <span style="color: red;">{{ payPrice }}</span>
          </p>
          <p style="margin-bottom: 50px;">请使用微信扫描该二维码付款</p>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
import { cellPhonePass } from '@/utils/validator.js';
export default {
  name: 'transaction',
  data() {
    return {
      form1: {
        phone: '',
        code: '',
        getVerificationCode: false,
        countDownTime: ''
      },
      rules1: {
        code: [
          {
            required: true,
            message: '验证码不能为空，请重新输入',
            trigger: 'blur'
          }
        ],
        phone: [
          {
            validator: cellPhonePass,
            trigger: 'blur'
          }
        ]
      },
      unionCardList: {
        discountCardList: [],
        activityCardList: []
      },
      visible1: true,
      fandId: '',
      unionIdList: '',
      activityIdList: '',
      checkList1: [],
      checkList2: [],
      visible2: false,
      codeSrc: '',
      socket: '',
      socketKey: '',
      orderNo: '',
      socketFlag: {
        socketKey: '',
        orderNo: '',
        status: ''
      },
      timeEnd: '',
      payPrice: ''
    };
  },
  mounted: function() {
    // 切换tab清空输入数据 todo
    eventBus.$on('tabChange3', () => {
      if (this.timeEnd) {
        clearInterval(this.timeEnd);
      }
      this.init();
    });
    this.getUnionCardList();
  },
  methods: {
    // 获取联盟卡列表
    getUnionCardList() {
      $http
        .get(`/unionCard/apply`)
        .then(res => {
          if (res.data.data) {
            this.unionCardList = res.data.data;
          } else {
            this.unionCardList = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 手机号更改
    phoneChange() {
      this.visible1 = true;
      this.checkList1 = [];
      this.checkList2 = [];
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
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 确认验证码
    confirmCode(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.checkList1 = [];
          this.checkList2 = [];
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
                this.fanId = res.data.data.fan.id;
                // 判断已办理联盟卡
                this.unionIdList = res.data.data.unionIdList;
                this.activityIdList = res.data.data.activityIdList;
                if (this.unionIdList && this.unionCardList.discountCardList.length) {
                  this.unionIdList.forEach(v => {
                    this.unionCardList.discountCardList.forEach(val => {
                      if (val.union.id === v.id) {
                        val.disabledFlag = true;
                      }
                    });
                  });
                }
                if (this.activityIdList && this.unionCardList.activityCardList.length) {
                  this.activityIdList.forEach(v => {
                    this.unionCardList.activityCardList.forEach(val => {
                      if (val.activity.id === v.id) {
                        val.disabledFlag = true;
                      }
                    });
                  });
                }
                this.visible1 = false;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
            });
        } else {
          return false;
        }
      });
    },
    // 折扣卡更改
    checkListChange1() {},
    // 活动卡更改
    checkListChange2() {
      this.payPrice = 0;
      let activityCheckList_ = [];
      this.checkList2.forEach(v => {
        this.unionCardList.activityCardList.forEach(val => {
          if (val.activity.id === v) {
            activityCheckList_.push(val.activity.price);
          }
        });
      });
      if (activityCheckList_) {
        activityCheckList_.forEach(v => {
          this.payPrice += v - 0;
        });
      }
      this.payPrice = this.payPrice.toFixed(2);
    },
    // 删除折扣卡
    del1(item) {
      let delIndex;
      this.checkList1.forEach((v, i) => {
        if (v === item.union.id) {
          delIndex = i;
        }
      });
      this.checkList1.splice(delIndex, 1);
    },
    // 删除活动卡
    del2(item) {
      let delIndex;
      this.checkList2.forEach((v, i) => {
        if (v === item.activity.id) {
          delIndex = i;
        }
      });
      this.checkList2.splice(delIndex, 1);
    },
    // 提交
    submitForm() {
      let url = `/unionCard/fanId/${this.fanId}/unionId/${this.unionId}/apply`;
      let data = {};
      data.unionIdList = [];
      data.activityIdList = [];
      if (!this.isDiscountCard && this.checkList1.length < 1 && this.checkList2.length < 1) {
        this.$message({ showClose: true, message: '请选择需办理的联盟卡', type: 'error', duration: 3000 });
        return false;
      } else {
        this.checkList1.forEach(v => {
          data.unionIdList.push(v);
        });
        this.checkList2.forEach(v => {
          data.activityIdList.push(v);
        });
      }
      $http
        .post(url, data)
        .then(res => {
          if (res.data.data) {
            this.codeSrc = res.data.data.payUrl;
            this.socketKey = res.data.data.socketKey;
            this.orderNo = res.data.data.orderNo;
            this.visible2 = true;
            var _this = this;
            var socketUrl = this.$store.state.socketUrl;
            if (!this.socket) {
              this.socket = io.connect(socketUrl, {
                reconnect: true
              });
              var socketKey = this.socketKey;
              this.socket.on('connect', function() {
                let jsonObject = {
                  userId: socketKey,
                  message: '0'
                };
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
              this.socket.on('reconnect', function(data) {
                socketindex--;
              });
              this.socket.on('chatevent', function(data) {
                let msg = eval('(' + data.message + ')');
                console.log(msg, 'transaction');
                // 避免 socket 重复调用
                if (
                  !(
                    _this.socketFlag.socketKey == msg.socketKey &&
                    _this.socketFlag.status == msg.status &&
                    _this.socketFlag.orderNo == msg.orderNo
                  )
                ) {
                  if (_this.socketKey == msg.socketKey && _this.orderNo == msg.orderNo) {
                    if (msg.status == '1') {
                      _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 3000 });
                      _this.socketFlag.socketKey = msg.socketKey;
                      _this.socketFlag.status = msg.status;
                      _this.socketFlag.orderNo = msg.orderNo;
                      _this.init();
                    } else if (msg.status == '0') {
                      _this.$message({ showClose: true, message: '支付失败', type: 'warning', duration: 3000 });
                    }
                  }
                }
              });
            }
          } else if (res.data.success) {
            this.$message({ showClose: true, message: '办理成功', type: 'success', duration: 3000 });
            clearInterval(this.timeEnd);
            //灰色倒计时'60s'变为紫蓝色"获取验证码"按钮;
            this.init();
            this.form1.countDownTime = '';
            this.form1.getVerificationCode = false;
          } else {
            this.codeSrc3 = '';
            this.socketKey3 = '';
            this.orderNo = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 初始化
    init() {
      this.form1.phone = '';
      this.form1.code = '';
      this.form1.getVerificationCode = false;
      this.form1.countDownTime = 0;
      this.visible1 = true;
      this.visible2 = false;
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
