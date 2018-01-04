<template>
  <div id="Verification">
    <div class="first_">
      <el-col style="width: 540px;">
        <el-input placeholder="请用扫码枪扫码或手动输入联盟卡号" icon="search" v-model="input" :on-icon-click="handleIconClick" @keypress.native="keypress($event)">
        </el-input>
      </el-col>
    </div>
    <!--二维码-->
    <div class="code_" v-show="visible1">
      <div class="model_">
        <p><img v-bind:src="$store.state.baseUrl + '/unionCard/qr/h5Card'" alt=""></p>
        <p>粉丝扫描二维码可查看联盟卡信息</p>
      </div>
    </div>
    <!--联盟卡信息-->
    <div v-show="visible2" class="UnionCardInformation">
      <div class="clearfix">
        <el-form :model="form" label-position="right" label-width="120px">
          <el-form-item label="联盟卡号:">
            <span> {{ form.fan.number }} </span>
          </el-form-item>
          <el-form-item label="联盟积分:">
            <span> {{ form.integral }} </span>
          </el-form-item>
          <el-form-item label="选择门店:">
            <el-select v-model="shopId" placeholder="请选择">
              <el-option v-for="item in form.shops" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <div class="selectUnion" style="margin-bottom: 36px;">
            <el-form-item label="选择联盟:">
              <el-radio-group v-model="form.unionId" style="margin-top:10px;" @change="unionIdChange">
                <el-radio-button v-for="item in form.unionList" :key="item.id" :label="item.id">
                  <div class="dddddd clearfix">
                    <img v-bind:src="item.img" alt="" class="fl unionImg">
                    <div class="fl isShow">
                      <h6 >{{item.name}}</h6>
                    </div>
                    <i></i>
                  </div>
                </el-radio-button>
              </el-radio-group>
            </el-form-item>
          </div>
          <el-form-item label="享受折扣:">
            <span style="color: #f10b0b"> {{ form.currentMember.discount * 10 }} 折</span>
          </el-form-item>
          <el-form-item label="消费金额:">
            <el-col style="width: 220px;">
              <el-input v-model="price">
                <template slot="prepend">￥</template>
              </el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="优惠项目" v-show="form.isProjectAvailable">
            <el-switch v-model="isProjectAvailable_" on-text="" off-text="" @change="isProjectAvailable_Change" :disabled="!form.unionId">
            </el-switch>
          </el-form-item>
        </el-form>
        <!-- 优惠项目 -->
        <transition name="slide-fade">
          <div v-show="isProjectAvailable_" class="preferenceItems">
            <div style="margin-top: 10px;">
              <span>活动卡名称：</span>
              <el-select v-model="activityId" placeholder="请选择" @change="activityCardChange" style="width: 180px">
                <el-option v-for="item in activityCards" :key="item.activity.id" :label="item.activity.name" :value="item.activity.id">
                </el-option>
              </el-select>
              <span style="margin-left: 15px;">有效时间： {{ activityCardValidity }} </span>
            </div>
            <div class="section_ clearfix">
              <div style="float: left">
                <el-table ref="multipleTable" :data="tableData" style="width:382px;overflow-y: auto" height="442" @selection-change="handleSelectionChange">
                  <el-table-column type="selection" width="50">
                  </el-table-column>
                  <el-table-column prop="item.name" label="项目名称">
                  </el-table-column>
                  <el-table-column prop="availableCount" label="可用数量">
                  </el-table-column>
                </el-table>
              </div>
              <div class="rightContent" style="width: 295px;">
                <p>已选择：{{ activitySelected.length }}</p>
                <div>
                  <div v-for="(item, index) in activitySelected" :key="item.item.id">
                    <span> {{ item.item.name }} </span>
                    <el-button type="text" @click="cancelActivity(index)">取消</el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </transition>
      </div>
      <p class="footer_">
        <el-button type="primary" @click="confirm">确定</el-button>
        <el-button @click="back">返回</el-button>
      </p>
    </div>
    <!-- 弹出框 核销 -->
    <div class="second_0">
      <el-dialog title="消费核销" :visible.sync="visible3" size="tiny">
        <hr>
        <div>
          <p>消费详情</p>
          <el-form :model="form">
            <el-form-item label="联盟卡号：">
              <span> {{ form.fan.number }} </span>
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
                <span class="color_">{{ price * form.currentMember.discount / 10 | formatPrice }}</span>
              </span>
            </el-form-item>
            <el-form-item label="联盟积分折扣：" v-if="isIntegral && form.integral > 0">
              <el-switch v-model="isIntegral_" on-text="" off-text="">
              </el-switch>
              <span>积分抵扣率：{{form.currentMember.integralExchangeRatio}}%</span>
            </el-form-item>
            <el-form-item label="消耗联盟积分：" v-if="isIntegral_ && form.integral > 0">
              <span> {{ deductionIntegral | formatPrice }} </span>
            </el-form-item>
            <el-form-item label="抵扣金额：" v-if="isIntegral_ && form.integral > 0">
              <span> ￥
                <span class="color_">{{ deductionPrice | formatPrice }} </span>
              </span>
            </el-form-item>
            <el-form-item label="实收金额：" v-if="isIntegral_ && form.integral > 0">
              <span> ￥
                <span class="color_">{{ price1 | formatPrice }} </span>
              </span>
            </el-form-item>
            <div class="discountsProject">
              <el-form-item label="优惠项目：">
                <span v-for="(item, index) in activitySelected" :key="item.item.id">
                  {{ index + 1 }} 、 {{ item.item.name }};
                </span>
              </el-form-item>
            </div>
          </el-form>
          <div class="payWay">
            <p>请选择支付方式：</p>
            <div>
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
              <el-row v-if="payType == 0">
                <el-col style="width:280px;">
                  <span>收取现金：</span>
                  <el-input v-model="price2" style="width:200px;">
                    <template slot="prepend">￥</template>
                  </el-input>
                </el-col>
                <el-col style="width:240px;margin-left:50px;padding-top: 2px;">
                  <span style="">找零: ￥
                    <span class="color_" v-if="!price2">0.00</span>
                    <span class="color_" v-if="(price2*100 - price1*100)/100 > 0 || Number(price2).toFixed(2) - Number(price1).toFixed(2) == 0">{{ (price2*100 - price1*100)/100 | formatPrice }}</span>
                  </span>
                </el-col>
                <el-col style="width:240px;margin-left:50px;position: absolute;top: 40px;left: 72px;">
                  <span class="color_" v-if="price2 && (price2*100 - price1*100)/100 < 0">收取金额小于支付金额，请重新输入</span>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submit">确 定</el-button>
          <el-button @click="visible3=false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 扫码支付 -->
    <div class="codePayment">
      <el-dialog title="付款" :visible.sync="visible4" size="tiny" @close="resetData">
        <hr>
        <img v-bind:src="payUrl">
        <p>请使用微信/支付宝扫描该二维码付款</p>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'verification',
  data() {
    return {
      num1: 1,
      input: '',
      imgSrc: '',
      visible1: true,
      visible2: false,
      form: {
        fan: {},
        integral: '',
        exchangeIntegral: '',
        currentUnion: {},
        unionList: [],
        unionId: '',
        currentUnion: {},
        currentMember: {},
        isProjectAvailable: ''
      },
      isProjectAvailable_: false,
      shopId: '',
      price: '',
      isIntegral: '', // 数据传输
      isIntegral_: '', // 控制显示
      deductionPrice: '', // 抵扣金额
      deductionIntegral: '', // 抵扣积分
      tableData: [],
      activityCards: [],
      activityId: '',
      activitySelected: [],
      activityCardValidity: '',
      visible3: false,
      payType: '',
      price1: '',
      price2: '',
      visible4: false,
      payUrl: '',
      socket: '',
      socketKey: '',
      socketFlag: {
        socketKey: '',
        status: ''
      }
    };
  },
  mounted: function() {
    eventBus.$on('tabChange1', () => {
      this.input = '';
      this.visible1 = true;
      this.visible2 = false;
    });
  },
  filters: {
    formatPrice: function(value) {
      if (!value) {
        return Number(0).toFixed(2);
      } else {
        return Number(value).toFixed(2);
      }
    }
  },
  watch: {
    // 输入金额格式化
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
      if ((this.price2 * 100 - this.price1 * 100) / 100 > 0 || (this.price2 * 100 - this.price1 * 100) / 100 === 0) {
        //        this.canSubmit = true;
      }
    },
    // 是否开启联盟积分
    isIntegral_: function() {
      let temData = 0;
      this.isIntegral_ ? (temData = 1) : (temData = 0);
      this.deductionPrice = this.price * this.form.discount / 10 * this.form.exchangeIntegral / 100 * temData;
      this.deductionIntegral = this.deductionPrice * 100 / this.form.exchangeIntegral;
      if (this.deductionIntegral > this.form.integral) {
        this.deductionIntegral = this.form.integral;
        this.deductionPrice = this.deductionIntegral / 100 * this.form.exchangeIntegral;
      }
      this.price1 = ((this.price * this.form.currentMember.discount - this.deductionPrice * 10) / 10).toFixed(2);
    }
  },
  methods: {
    // 搜索
    handleIconClick(ev) {
      if (this.input) {
        $http
          .get(`/unionCardFan/search?numberOrPhone=${this.input}`)
          .then(res => {
            if (res.data.data) {
              this.form = res.data.data;
              if (this.form.unionList.length) {
                this.form.unionId = this.form.unionList.id;
              }
              this.isIntegral = this.form.currentUnion.isIntegral;
              this.isIntegral ? (this.isIntegral_ = true) : (this.isIntegral_ = false);
              this.visible1 = false;
              this.visible2 = true;
            } else {
              this.$message({ showClose: true, message: '不存在该联盟卡', type: 'error', duration: 5000 });
            }
          })
          .then(res => {
            // 获取门店信息
            $http
              .get(`/api/shop/list`)
              .then(res => {
                if (res.data.data) {
                  this.form.shops = res.data.data;
                } else {
                  this.form.shops = [];
                }
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
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
          .get(`/unionCardFan/search?numberOrPhone=${this.input}&unionId=${this.form.unionId}`)
          .then(res => {
            if (res.data.data) {
              this.form.currentMember.enterpriseName = res.data.data.currentMember.enterpriseName;
              this.form.currentMember.discount = res.data.data.currentMember.discount;
              this.form.fan.number = res.data.data.fan.number;
              this.form.currentUnion.isIntegral = res.data.data.currentUnion.isIntegral;
              this.form.integral = res.data.data.integral;
              this.form.currentUnion.validity = res.data.data.currentUnion.validity;
              this.isIntegral = this.form.isIntegral;
              this.isIntegral ? (this.isIntegral_ = true) : (this.isIntegral_ = false);
            } else {
              this.form.currentMember.enterpriseName = '';
              this.form.currentMember.discount = '';
              this.form.fan.number = '';
              this.form.currentUnion.isIntegral = '';
              this.form.integral = '';
              this.form.currentUnion.validity = '';
              this.isIntegral = '';
              this.isIntegral ? (this.isIntegral_ = true) : (this.isIntegral_ = false);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 是否选择优惠项目
    isProjectAvailable_Change(isProjectAvailable_) {
      if (isProjectAvailable_) {
        // 获取活动卡名称列表
        $('.UnionCardInformation form').css({
          transition: 'all .3s ease',
          transform: 'translate(-420px)'
        });
        $http
          .get(`/unionCardActivity/unionId/${this.form.unionId}/consume?fanId=${this.form.fan.id}`)
          .then(res => {
            if (res.data.data) {
              this.activityCards = res.data.data || [];
            } else {
              this.activityCards = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      } else {
        $('.UnionCardInformation form').css({
          transition: 'all .3s .2s cubic-bezier(1.0, 0.5, 0.8, 1.0)',
          transform: 'translate(0px)'
        });
      }
    },
    // 活动卡切换
    activityCardChange(item) {
      this.activityCardValidity = timeFilter(item.activityCard.validity);
      // 获取活动卡优惠项目
      if (item.activity.id) {
        $http
          .get(`/unionCardProjectItem/activityId/${item.activity.id}/unionId/${this.form.unionId}/consume`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data || [];
            } else {
              this.tableData = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 勾选优惠项目
    handleSelectionChange(val) {
      this.multipleSelection = val;
      this.activitySelected = this.multipleSelection;
    },
    // 取消优惠项目
    cancelActivity(index) {
      this.activitySelected.splice(index, 1);
    },
    // 确认
    confirm() {
      if (this.activitySelected.length === 0 && !this.price) {
        this.$message({ showClose: true, message: '请输入金额', type: 'error', duration: 5000 });
      } else {
        this.visible3 = true;
        let temData = 0;
        this.isIntegral_ ? (temData = 1) : (temData = 0);
        this.deductionPrice =
          this.price * this.form.currentMember.discount / 10 * this.form.integralPercent / 100 * temData;
        this.deductionIntegral = this.deductionPrice * 100 / this.form.exchangeIntegral;
        if (this.deductionIntegral > this.form.integral) {
          this.deductionIntegral = this.form.integral;
          this.deductionPrice = this.deductionIntegral / 100 * this.form.exchangeIntegral;
        }
        this.price1 = ((this.price * this.form.currentMember.discount - this.deductionPrice * 10) / 10).toFixed(2);
      }
    },
    // 提交
    submit() {
      let url = `/unionConsume/unionId/${this.form.unionId}/fanId/${this.form.fan.id}`;
      let data = {};
      data.unionId = this.form.unionId - 0;
      data.fanId = this.form.fan.id - 0;
      data.shopId = this.shopId - 0;
      data.isUserIntegral = this.isIntegral_ && this.form.integral - 0;
      (data.consume = {}), (data.consume.consumeMoney = this.price - 0);
      data.consume.payMoney = this.price1 - 0;
      data.consume.payType = this.payType - 0;
      data.textList = [];
      this.activitySelected.forEach(v => {
        data.textList.push({ id: v.item.id });
      });
      $http
        .post(url, data)
        .then(res => {
          if (res.data.success) {
            eventBus.$emit('newTransaction');
            eventBus.$emit('unionUpdata');
            this.init();
            this.$message({ showClose: true, message: '核销成功', type: 'success', duration: 5000 });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 付款二维码
    payTypeChange() {
      if (this.payType == 1) {
        let url = `/unionConsume/unionId/${this.form.unionId}/fanId/${this.form.fan.id}`;
        let data = {};
        data.unionId = this.form.unionId - 0;
        data.fanId = this.form.fan.id - 0;
        data.shopId = this.shopId - 0;
        data.isUserIntegral = this.isIntegral_ && this.form.integral - 0;
        (data.consume = {}), (data.consume.consumeMoney = this.price - 0);
        data.consume.payMoney = this.price1 - 0;
        data.consume.payType = this.payType - 0;
        data.textList = [];
        this.activitySelected.forEach(v => {
          data.textList.push({ id: v.item.id });
        });
        $http
          .post(url, data)
          .then(res => {
            if (res.data.data) {
              this.payUrl = res.data.data.url;
              this.socketKey = res.data.data.socketKey;
            } else {
              this.payUrl = '';
              this.socketKey = '';
            }
          })
          .then(res => {
            this.visible4 = true;
          })
          .then(res => {
            var _this = this;
            var socketUrl = this.$store.state.socketUrl;
            if (!this.socket) {
              this.socket = io.connect(socketUrl);
              var socketKey = this.socketKey;
              this.socket.on('connect', function() {
                let jsonObject = { userId: socketKey, message: '0' };
                _this.socket.emit('auth', jsonObject);
              });
            }
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
                    _this.$message({ showClose: true, message: '支付失败', type: 'error', duration: 5000 });
                  }
                }
              }
            });
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 关闭二维码改变付款方式
    resetData() {
      parent.window.postMessage('openMask()', 'https://deeptel.com.cn/user/toIndex_1.do');
      this.payType = 0;
    },
    // 返回
    back() {
      this.visible1 = true;
      this.visible2 = false;
    },
    // 初始化
    init() {
      this.visible4 = false;
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
        exchangeIntegral: '',
        integralPercent: ''
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
      this.payUrl = '';
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.unionImg {
  width: 80px;
  height: 80px;
}

.first_ {
  height: 100px;
  background: #f8f8f8;
  line-height: 100px;
  margin-bottom: 25px;
  padding: 0 34.7%;
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
    color: #ff4949;
    font-size: 20px;
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

.slide-fade-enter-active {
  transition: all 0.3s ease;
}
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter,
.slide-fade-leave-to {
  transform: translateX(100px);
  opacity: 0;
}
</style>
