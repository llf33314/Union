<template>
  <div id="UnionCard">
    <div class="first_">
      <el-col style="width: 435px;;">
        <el-input placeholder="请用扫码枪扫码或手动输入联盟卡号" icon="search" v-model="input" :on-icon-click="handleIconClick" @keypress.native="keypress($event)">
        </el-input>
      </el-col>
    </div>
    <!--二维码-->
    <div class="code_" v-show="visible1">
      <div class="model_">
        <p><img v-bind:src="imgSrc" alt=""></p>
        <p>粉丝扫描二维码可查看联盟卡信息</p>
      </div>
    </div>
    <!--有门店消费核销-->
    <div v-show="visible2" class="second_">
      <el-form :model="form" label-position="right" label-width="120px">
        <span style="color: red;margin-left: 48px;margin-bottom: 20px;display: inline-block;">你给予“{{ form.enterpriseName }}”的最低折扣为{{ form.discount }}折</span>
        <el-form-item label="联盟卡号:">
          <span> {{ form.cardNo }} </span>
        </el-form-item>
        <el-form-item label="联盟积分:">
          <span> {{ form.integral }} </span>
        </el-form-item>
        <el-form-item label="选择门店:">
          <el-select v-model="shop" placeholder="请选择">
            <el-option v-for="item in form.shops" :key="item.id" :label="item.name" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <div class="selectUnion" style="margin-bottom: 36px;">
          <el-form-item label="选择联盟:">
            <el-radio-group v-model="form.unionId" style="margin-top:10px;" @change="unionIdChange">
              <el-radio-button v-for="item in form.unions" :key="item.id" :label="item.id">
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
        <el-form-item label="有效期:" v-if="form.validity">
          <span> {{ form.validity }}天 </span>
        </el-form-item>
        <el-form-item label="消费金额:">
          <el-col>
            <el-input v-model="price">
              <template slot="prepend">￥</template>
            </el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="优惠项目" v-show="form.items">
          <el-checkbox-group v-model="item">
            <el-checkbox v-for="item in form.items" :key="item.id" :label="item.id">
              <span> {{ item.name }} </span>
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="项目说明:" v-show="form.illustration">
          <span> {{ form.illustration }}天 </span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="confirm">确定</el-button>
          <el-button @click="back">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 弹出框 核销 -->
    <div class="second_0">
      <el-dialog title="核销" :visible.sync="visible3" size="tiny">
        <hr>
        <el-form :model="form">
          <span style="color: red;margin-left: 40px;margin-bottom: 10px;display: inline-block;">你给予“{{ form.enterpriseName }}”的最低折扣为{{ form.discount }}折</span>
          <el-form-item label="联盟卡号：">
            <span> {{ form.cardNo }} </span>
          </el-form-item>
          <el-form-item label="联盟积分：">
            <span> {{ form.integral }} </span>
          </el-form-item>
          <el-form-item label="消费金额：">
            <span> ￥
              <span class="color_">{{ price | formatPrice }} </span>
            </span>
          </el-form-item>
          <el-form-item label="联盟价格：">
            <span> ￥
              <span class="color_">{{ price * form.discount / 10 | formatPrice }}</span>
            </span>
          </el-form-item>
          <el-form-item label="联盟积分折扣：" v-if="isIntegral">
            <el-switch v-model="isIntegral_" on-text="" off-text="">
            </el-switch>
            <span>最大折扣比率：20%</span>
          </el-form-item>
          <el-form-item label="消耗联盟积分：" v-if="isIntegral_">
            <span> {{ deductionIntegral | formatPrice }} </span>
          </el-form-item>
          <el-form-item label="抵扣金额：" v-if="isIntegral_">
            <span> ￥
              <span class="color_">{{ deductionPrice | formatPrice }} </span>
            </span>
          </el-form-item>
          <el-form-item label="实收金额：" v-if="isIntegral_">
            <span> ￥
              <span class="color_">{{ price1 | formatPrice }} </span>
            </span>
          </el-form-item>
          <div class="payWay" style="margin-bottom: 90px;">
            <el-form-item label="请选择支付方式：">
              <el-radio-group v-model="payType" @change="payTypeChange">
                <el-radio-button label="0">
                  <p>
                    <i class="iconfont" style="font-size: 33px;">&#xe727;</i>
                  </p>
                  <span>现金支付</span>
                </el-radio-button>
                <el-radio-button label="1">
                  <p>
                    <i class="iconfont" style="font-size: 33px;">&#xe60d;</i>
                  </p>
                  <span>扫码支付</span>
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </div>
          <el-form-item label="收取现金：" v-if="payType == 0">
            <el-row>
              <el-col style="width:200px;">
                <el-input v-model="price2">
                  <template slot="prepend">￥</template>
                </el-input>
              </el-col>
              <el-col style="width:240px;margin-left:50px;">
                <span>找零: ￥
                  <span class="color_" v-if="!price2">0.00</span>
                  <span class="color_" v-if="price2-price1 > 0 || price2-price1 === 0">{{ (price2*100 - price1*100)/100 | formatPrice }}</span>
                </span>
              </el-col>
              <el-col style="width:240px;margin-left:50px;position: absolute;top: 40px;left: 72px;">
                <span class="color_" v-if="price2 && price2-price1 < 0">收取金额小于支付金额，请重新输入</span>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submit" :disabled="!canSubmit">确 定</el-button>
          <el-button @click="visible3 = false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 扫码支付 -->
    <div class="codePayment">
      <el-dialog title="付款" :visible.sync="visible5" size="tiny" @close="resetData">
        <hr>
        <img v-bind:src="codeSrc" class="codeImg">
        <p>请使用微信扫描该二维码付款</p>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'unioncard',
  data() {
    return {
      input: '',
      imgSrc: '',
      visible1: true,
      visible2: false,
      form: {
        enterpriseName: '',
        discount: '',
        cardId: '',
        cardNo: '',
        integral: '',
        value1: '',
        shops: [],
        unionId: '',
        validity: '',
        items: [],
        illustration: '',
        exchangeIntegral: ''
      },
      isIntegral: '', // 数据传输
      isIntegral_: '', // 控制显示
      price: '',
      shop: '',
      item: [],
      visible3: false,
      deductionPrice: '',
      deductionIntegral: '',
      price1: '', // 实收金额
      payType: 0,
      price2: '', // 收取现金
      visible5: false,
      codeSrc: '',
      only: '',
      socket: '',
      socketFlag: {
        only: '',
        status: ''
      },
      canSubmit: false
    };
  },
  mounted: function() {
    $http
      .get(`/unionCard/phone`)
      .then(res => {
        if (res.data.data) {
          this.imgSrc = res.data.data;
        } else {
          this.imgSrc = '';
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 切换tab情况输入数据
    eventBus.$on('tabChange1', () => {
      this.input = '';
    });
  },
  filters: {
    formatPrice: function(value) {
      if (!value) {
        return '';
      } else {
        return Number(value).toFixed(2);
      }
    }
  },
  watch: {
    price: function() {
      if (isNaN(this.price)) {
        let that = this;
        setTimeout(function() {
          that.price = '';
        }, 100);
      }
    },
    price2: function() {
      if (isNaN(this.price2)) {
        let that = this;
        setTimeout(function() {
          that.price2 = '';
        }, 100);
      }
      if (this.price2 - this.price1 > 0 || this.price2 - this.price1 === 0) {
        this.canSubmit = true;
      }
    },
    isIntegral_: function() {
      let temData = 0;
      this.isIntegral_ ? (temData = 1) : (temData = 0);
      this.deductionPrice = this.price * this.form.discount / 10 * 0.2 * temData;
      this.deductionIntegral = this.deductionPrice * 100;
      if (this.deductionIntegral > this.form.integral) {
        this.deductionIntegral = this.form.integral;
        this.deductionPrice = this.deductionIntegral / 10;
      }
      this.price1 = this.price * this.form.discount / 10 - this.deductionPrice;
    }
  },
  methods: {
    // 搜索
    handleIconClick(ev) {
      if (this.input) {
        $http
          .get(`unionCard/unionCardInfo?no=${this.input}`)
          .then(res => {
            if (res.data.data) {
              this.form = res.data.data;
              this.isIntegral = this.form.isIntegral;
              this.isIntegral ? (this.isIntegral_ = true) : (this.isIntegral_ = false);
              this.visible1 = false;
              this.visible2 = true;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    keypress(e) {
      if (e.keyCode == 13) {
        this.handleIconClick();
      }
    },
    // 联盟改变
    unionIdChange() {
      if (this.form.unionId) {
        $http
          .get(`/unionCard/unionCardInfo?no=${this.input}&unionId=${this.form.unionId}`)
          .then(res => {
            if (res.data.data) {
              this.form.enterpriseName = res.data.data.enterpriseName;
              this.form.discount = res.data.data.discount;
              this.form.cardNo = res.data.data.cardNo;
              this.form.isIntegral = res.data.data.isIntegral;
              this.form.integral = res.data.data.integral;
              this.form.validity = res.data.data.validity;
              this.isIntegral = this.form.isIntegral;
              this.isIntegral ? (this.isIntegral_ = true) : (this.isIntegral_ = false);
            } else {
              this.form.enterpriseName = '';
              this.form.discount = '';
              this.form.cardNo = '';
              this.form.isIntegral = '';
              this.form.integral = '';
              this.form.validity = '';
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 确认
    confirm() {
      if (this.item.length === 0 && !this.price) {
        this.$message({ showClose: true, message: '请输入金额', type: 'warning', duration: 5000 });
      } else if (this.price) {
        this.visible3 = true;
        let temData = 0;
        this.isIntegral_ ? (temData = 1) : (temData = 0);
        this.deductionPrice = this.price * this.form.discount / 10 * 0.2 * temData;
        this.deductionIntegral = this.deductionPrice * 100;
        if (this.deductionIntegral > this.form.integral) {
          this.deductionIntegral = this.form.integral;
          this.deductionPrice = this.deductionIntegral / 10;
        }
        this.price1 = this.price * this.form.discount / 10 - this.deductionPrice;
      } else {
        this.submit();
      }
    },
    // 提交
    submit() {
      let url = `/unionConsume`;
      let data = {};
      data.cardId = this.form.cardId;
      data.consumeIntegral = this.deductionIntegral;
      data.consumeMoney = this.price - 0;
      data.discount = this.form.discount;
      data.items = this.item;
      data.memberId = this.form.memberId;
      data.payMoney = this.price1;
      data.payType = this.payType - 0;
      data.shopId = this.shop - 0;
      data.unionId = this.form.unionId;
      data.useIntegral = this.isIntegral_;
      data.memberId = this.form.memberId;
      $http
        .post(url, data)
        .then(res => {
          if (res.data.success) {
            this.visible3 = false;
            eventBus.$emit('newTransaction');
            this.$message({ showClose: true, message: '核销成功', type: 'success', duration: 5000 });
            this.init();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 初始化
    init() {
      this.input = '';
      this.visible1 = true;
      this.visible2 = false;
      this.form = {
        enterpriseName: '',
        discount: '',
        cardId: '',
        cardNo: '',
        integral: '',
        value1: '',
        shops: [],
        unionId: '',
        validity: '',
        items: [],
        illustration: '',
        exchangeIntegral: ''
      };
      this.isIntegral = ''; // 数据传输
      this.isIntegral_ = ''; // 控制显示
      this.price = '';
      this.shop = '';
      this.item = [];
      this.visible3 = false;
      this.deductionPrice = '';
      this.deductionIntegral = '';
      this.price1 = ''; // 实收金额
      this.payType = 0;
      this.price2 = ''; // 收取现金
      this.visible5 = false;
      this.codeSrc = '';
    },
    // 付款二维码
    payTypeChange() {
      if (this.payType == 1) {
        this.visible5 = true;
        let url = `/unionConsume/qrCode`;
        let data = {};
        data.cardId = this.form.cardId;
        data.consumeIntegral = this.deductionIntegral;
        data.consumeMoney = this.price - 0;
        data.discount = this.form.discount;
        data.items = this.item;
        data.memberId = this.form.memberId;
        data.payMoney = this.price1;
        data.payType = this.payType - 0;
        data.shopId = this.shop - 0;
        data.unionId = this.form.unionId;
        data.useIntegral = this.isIntegral_;
        data.memberId = this.form.memberId;
        $http
          .post(url, data)
          .then(res => {
            if (res.data.data) {
              this.codeSrc = res.data.data.url;
              this.only = res.data.data.only;
              this.userId = res.data.data.userId;
            } else {
              this.codeSrc = '';
              this.only = '';
              this.userId = '';
            }
          })
          .then(res => {
            var _this = this;
            if (!this.socket) {
              this.socket = io.connect('https://socket.deeptel.com.cn'); // 测试
              // this.socket = io.connect('http://183.47.242.2:8881'); // 堡垒
              var userId = this.userId;
              this.socket.on('connect', function() {
                let jsonObject = { userId: userId, message: '0' };
                _this.socket.emit('auth', jsonObject);
              });
            }
            this.socket.on('chatevent', function(data) {
              let msg = eval('(' + data.message + ')');
              if (!(_this.socketFlag.only == msg.only && _this.socketFlag.status == msg.status)) {
                if (_this.only == msg.only) {
                  if (msg.status == '003') {
                    _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 5000 });
                    _this.visible3 = false;
                    _this.visible5 = false;
                    _this.parent.window.postMessage('openMask()', 'https://deeptel.com.cn/user/toIndex_1.do');
                    _this.eventBus.$emit('newTransaction');
                    _this.init();
                  } else if (msg.status == '004') {
                    _this.$message({ showClose: true, message: '请求超时', type: 'warning', duration: 5000 });
                  } else if (msg.status == '005') {
                    _this.$message({ showClose: true, message: '支付失败', type: 'warning', duration: 5000 });
                  }
                }
              }
              _this.socketFlag.only = msg.only;
              _this.socketFlag.status = msg.status;
            });
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 关闭二维码改变付款方式
    resetData() {
      this.payType = 0;
      parent.window.postMessage('openMask()', 'https://deeptel.com.cn/user/toIndex_1.do');
    },
    // 返回
    back() {
      this.visible1 = true;
      this.visible2 = false;
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.codeImg {
  width: 200px;
  height: 200px;
}

.unionImg {
  width: 80px;
  height: 80px;
}

.first_ {
  height: 100px;
  background: #f8f8f8;
  line-height: 100px;
  margin-bottom: 25px;
  padding: 0 35%;
}

.second_ {
  width: 400px;
  margin: auto;
}

.code_ {
  padding: 30px;
  .model_ {
    p {
      color: #999999;
    }
  }
}

/*点击输出弹出框的样式*/
.second_0 {
  .color_ {
    color: red;
    font-size: 14px;
  }
  .el-form-item {
    margin-bottom: 5px;
  }
  .pay_ {
    margin: 6px 13px;
    font-size: 18px;
    color: #20a0ff;
  }
}
</style>
