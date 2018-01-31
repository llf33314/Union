<template>
  <div id="Verification">
    <div class="first_">
      <el-col style="width: 35%;">
        <el-input placeholder="请用扫码枪扫码或手动输入联盟卡号" icon="search" v-model="input" :on-icon-click="handleIconClick" @keyup.enter.native="handleIconClick">
        </el-input>
      </el-col>
    </div>
    <!--二维码-->
    <div class="code_" v-show="visible1&&WeChatImg">
      <div class="model_">
        <p><img v-bind:src="WeChatImg" alt=""></p>
        <p>粉丝扫描二维码可查看联盟卡信息</p>
      </div>
    </div>
    <!--联盟卡信息-->
    <div v-show="visible2" class="UnionCardInformation">
      <div class="clearfix">
        <el-form :model="form" label-position="right" label-width="24%">
          <el-form-item label="联盟卡号:">
            <span> {{ form.fan.number }} </span>
          </el-form-item>
          <el-form-item label="联盟积分:">
            <span> {{ form.integral }} </span>
          </el-form-item>
          <el-form-item label="选择门店:">
            <el-select v-model="shopId" placeholder="请选择">
              <el-option v-for="item in shops" :key="item.id" :label="item.name" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <div class="selectUnion">
            <el-form-item label="选择联盟:" v-show="form.unionList.length>1">
              <el-radio-group v-model="unionId" style="margin-top:10px;" @change="unionIdChange">
                <el-radio-button v-for="item in form.unionList" :key="item.id" :label="item.id">
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
          <el-form-item label="享受折扣:">
            <span style="color: #f10b0b" v-if="form.currentMember.discount == 1"> 无</span>
            <span style="color: #f10b0b" v-if="form.currentMember.discount != 1"> {{ form.currentMember.discount * 10 }} 折</span>
          </el-form-item>
          <el-form-item label="消费金额:">
            <el-col style="width: 100%;">
              <el-input v-model="price" @keyup.native="check()" @keyup.enter.native="confirm">
                <template slot="prepend">￥</template>
              </el-input>
            </el-col>
          </el-form-item>
          <el-form-item label="优惠项目" v-if="form.isProjectAvailable">
            <el-switch v-model="isProjectAvailable_" on-text="" off-text="" :disabled="!unionId">
            </el-switch>
          </el-form-item>
        </el-form>
        <!-- 优惠项目 -->
        <transition name="slide-fade">
          <div v-show="isProjectAvailable_" class="preferenceItems">
            <div style="margin-top: 10px;">
              <span>活动卡名称：</span>
              <el-select style="width: 31%;" v-model="activityCardId" placeholder="请选择" @change="activityCardChange">
                <el-option v-for="item in activityCards" :key="item.activityCard.id" :label="item.activityCard.name" :value="item.activityCard.id">
                </el-option>
              </el-select>
              <span class="validTime">有效时间： {{ activityCardValidity }} </span>
            </div>
            <div class="section_ clearfix">
              <div style="float: left" id="left-table">
                <el-table ref="multipleTable" :data="tableData" style="width:100%;overflow-y: auto;" height="442" @select="handleSelect" @select-all="handleSelectAll" @row-click="handleRowClick" >
                  <el-table-column type="selection" width="40">
                  </el-table-column>
                  <el-table-column prop="item.name" label="项目名称">
                  </el-table-column>
                  <el-table-column prop="availableCount" label="可用数量">
                  </el-table-column>
                </el-table>
              </div>
              <div class="rightContent">
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
      <el-dialog title="消费核销" :visible.sync="visible3" width="30%" @close="resetData1">
        <hr>
        <div>
          <p>消费详情</p>
          <el-form :model="form">
            <el-form-item label="联盟卡号：">
              <span> {{ form.fan.number }} </span>
            </el-form-item>
            <el-form-item label="消费金额：">
              <span> ￥
                <span class="color_">{{ price | formatPrice }} </span>
              </span>
            </el-form-item>
            <el-form-item label="联盟卡折后价格：">
              <span> ￥
                <span class="color_">{{ price * (form.currentMember.discount || 1) | formatPrice }}</span>
              </span>
            </el-form-item>
            <el-form-item label="联盟积分：" v-if="isIntegral && form.currentMember.integralExchangeRatio && form.integral >= 1">
              <span> 共{{ form.integral }}积分，最多可抵扣{{ form.currentMember.integralExchangeRatio * 100 }}% </span>
              <el-switch v-model="isIntegral_" on-text="" off-text="">
              </el-switch>
            </el-form-item>
            <el-form-item label="积分抵扣" v-if="isIntegral_ && form.currentMember.integralExchangeRatio && form.integral >= 1">
              <span> ￥
                <span class="color_">{{ deductionPrice | formatPrice }} </span>
              </span>
              <span> （消耗{{ deductionIntegral | formatPrice }}积分） </span>
            </el-form-item>
            <el-form-item label="实收金额：">
              <span> ￥
                <span class="color_">{{ price1 | formatPrice }} </span>
              </span>
            </el-form-item>
            <el-form-item label="消费赠送积分：">
              <span> ￥
                <span>{{ price1 * form.giveIntegral | formatPrice }}联盟积分 </span>
              </span>
            </el-form-item>
            <div class="discountsProject">
              <el-form-item label="优惠项目：" v-if="activitySelected.length>0">
                <span v-for="(item, index) in activitySelected" :key="item.item.id">
                  {{ index + 1 }} 、 {{ item.item.name }};
                </span>
              </el-form-item>
            </div>
          </el-form>
          <div class="payWay" v-show="price">
            <p>请选择支付方式：</p>
            <div>
              <el-radio-group v-model="payType" @change="payTypeChange">
                <el-radio-button label="1">
                  <p style="margin-bottom: 10px">
                    <i class="iconfont" style="font-size: 33px;">&#xe727;</i>
                  </p>
                  <span >现金支付</span>
                </el-radio-button>
                <el-radio-button label="2">
                  <p style="margin-bottom: 10px">
                    <i class="iconfont" style="font-size: 33px;">&#xe60d;</i>
                  </p>
                  <span>扫码支付</span>
                </el-radio-button>
              </el-radio-group>
              <el-row v-if="payType == 1">
                <el-col style="width:280px;display:flex;align-items:center;">
                  <span>收取现金：</span>
                  <el-input v-model="price2" style="width:200px;" @keyup.enter.native="submit" @keyup.native="check2()">
                    <template slot="prepend">￥</template>
                  </el-input>
                </el-col>
                <el-col style="width:240px;margin-left:50px;padding-top: 2px;">
                  <span style="">找零: ￥
                    <span class="color_" v-if="!price2">0.00</span>
                    <span class="color_" v-if="price2 && price2 - price1 >= 0">{{ (price2 - price1) | formatPrice}}</span>
                  </span>
                </el-col>
                <el-col style="margin-left:70px;">
                  <span class="color-1" v-if="price2 && (price2 - price1 < 0)">收取金额小于支付金额，请重新输入</span>
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
      <el-dialog title="付款" :visible.sync="visible4" width="30%" @close="resetData">
        <hr>
        <img v-bind:src="payUrl">
        <p>￥<span>{{ payPrice }}</span>
        <div>扫描二维码进行支付</div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
import { timeFilter, numberCheck } from '@/utils/filter.js';

export default {
  name: 'verification',
  data() {
    return {
      input: '',
      imgSrc: '',
      visible1: true,
      visible2: false,
      form: {
        fan: {},
        integral: '',
        exchangeIntegral: '',
        giveIntegral: '',
        currentUnion: {},
        unionList: [],
        currentUnion: {},
        currentMember: {},
        isProjectAvailable: ''
      },
      unionId: '',
      isProjectAvailable_: false,
      shops: [],
      shopId: '',
      price: '',
      isIntegral: '', // 数据传输
      isIntegral_: false, // 控制显示
      deductionPrice: '', // 抵扣金额
      deductionIntegral: '', // 抵扣积分
      tableData: [],
      activityCards: [],
      activityCardId: '',
      activityId: '',
      activitySelected: [],
      activityCardValidity: '',
      visible3: false,
      payType: 1,
      price1: '',
      price2: '',
      visible4: false,
      payUrl: '',
      socket: '',
      socketKey: '',
      orderNo: '',
      socketFlag: {
        socketKey: '',
        status: '',
        orderNo: ''
      },
      payPrice: '',
      WeChatImg: ''
    };
  },
  mounted: function() {
    $http
      .get(`/unionCard/qr/applet`)
      .then(res => {
        if (res.data.data) {
          this.WeChatImg = res.data.data;
        } else {
          this.WeChatImg = '';
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
      });
    eventBus.$on('tabChange1', () => {
      this.input = '';
      this.visible1 = true;
      this.visible2 = false;
      this.isProjectAvailable_ = false;
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
    // 是否开启联盟积分
    isIntegral_: function() {
      let temData = 0;
      this.isIntegral_ ? (temData = 1) : (temData = 0);
      this.deductionPrice =
        this.price * this.form.currentMember.discount * this.form.currentMember.integralExchangeRatio * temData;
      this.deductionIntegral = parseInt(this.deductionPrice * this.form.exchangeIntegral);
      if (this.deductionIntegral > this.form.integral) {
        this.deductionIntegral = parseInt(this.form.integral);
        this.deductionPrice = this.deductionIntegral / this.form.exchangeIntegral;
      }
      if (this.deductionPrice > this.price * this.form.currentMember.discount) {
        this.deductionPrice = this.price * this.form.currentMember.discount;
        this.deductionIntegral = this.deductionPrice * this.form.exchangeIntegral;
      }
      this.price1 = this.price * this.form.currentMember.discount - this.deductionPrice;
    },
    // 是否开启优惠项目
    isProjectAvailable_: function() {
      this.isProjectAvailable_Change();
    }
  },
  methods: {
    // 校验输入为数字类型
    check() {
      this.price = numberCheck(this.price);
    },
    check2() {
      this.price2 = numberCheck(this.price2);
    },
    // 搜索
    handleIconClick(ev) {
      this.shopId = '';
      this.unionId = '';
      if (this.input) {
        $http
          .get(`/unionCardFan/search?numberOrPhone=${this.input}`)
          .then(res => {
            if (res.data.data) {
              this.visible1 = false;
              this.visible2 = true;
              this.form = res.data.data;
              if (this.form.unionList.length) {
                this.unionId = this.form.unionList[0].id;
              }
              this.isIntegral = this.form.currentUnion.isIntegral;
            } else {
              this.visible1 = true;
              this.visible2 = false;
            }
          })
          .then(res => {
            // 获取门店信息
            $http
              .get(`/api/shop/list`)
              .then(res => {
                if (res.data.data) {
                  this.shops = res.data.data;
                } else {
                  this.shops = [];
                }
              })
              .catch(err => {
                this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
              });
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    },
    // 联盟改变
    unionIdChange() {
      this.form.currentMember.enterpriseName = '';
      this.form.currentMember.discount = '';
      this.form.currentMember.integralExchangeRatio = '';
      this.form.fan.number = '';
      this.form.currentUnion.isIntegral = '';
      this.form.integral = '';
      this.form.isProjectAvailable = '';
      this.price = '';
      this.isProjectAvailable_ = false;
      this.isIntegral = '';

      if (this.unionId) {
        $http
          .get(`/unionCardFan/search?numberOrPhone=${this.input}&unionId=${this.unionId}`)
          .then(res => {
            if (res.data.data) {
              this.form.currentMember.enterpriseName = res.data.data.currentMember.enterpriseName;
              this.form.currentMember.discount = res.data.data.currentMember.discount || 1;
              this.form.currentMember.integralExchangeRatio = res.data.data.currentMember.integralExchangeRatio || 0;
              this.form.fan.number = res.data.data.fan.number;
              this.form.currentUnion.isIntegral = res.data.data.currentUnion.isIntegral;
              this.form.integral = res.data.data.integral;
              this.form.isProjectAvailable = res.data.data.isProjectAvailable;
              this.isIntegral = this.form.currentUnion.isIntegral;

              this.isProjectAvailable_ = false;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    },
    // 是否选择优惠项目
    isProjectAvailable_Change() {
      this.activityCards = [];
      this.activityCardId = '';
      this.activityCardValidity = '';
      if (this.isProjectAvailable_) {
        // 获取活动卡名称列表
        $('.UnionCardInformation form').css({
          transition: 'all .3s ease',
          transform: 'translate(-81%)'
        });
        $http
          .get(`/unionCardActivity/unionId/${this.unionId}/consume?fanId=${this.form.fan.id}`)
          .then(res => {
            if (res.data.data) {
              this.activityCards = res.data.data || [];
            } else {
              this.activityCards = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      } else {
        $('.UnionCardInformation form').css({
          transition: 'all .3s .2s cubic-bezier(1.0, 0.5, 0.8, 1.0)',
          transform: 'translate(0px)'
        });
      }
    },
    // 活动卡切换
    activityCardChange() {
      this.tableData = [];
      this.$refs.multipleTable.clearSelection();
      this.activitySelected = [];
      if (this.activityCardId) {
        let item = this.activityCards.find(item => {
          return item.activityCard.id === this.activityCardId;
        });
        this.activityId = item.activity.id;
        this.activityCardValidity = timeFilter(item.activityCard.validity);
        // 获取活动卡优惠项目
        $http
          .get(`/unionCardProjectItem/activityId/${this.activityId}/unionId/${this.unionId}/consume`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data || [];
            } else {
              this.tableData = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    },
    // 点击行
    handleRowClick(row) {
      this.$refs.multipleTable.toggleRowSelection(row);
      this.handleSelect(null, row, false);
    },
    // 单选
    handleSelect(selection, row, isAll) {
      let pass = this.activitySelected.some(item => {
        return item.item.id === row.item.id;
      });
      if (!pass) {
        this.activitySelected.push(row);
      } else {
        this.activitySelected.forEach((item, index) => {
          if (item.item.id === row.item.id && !isAll) {
            this.activitySelected.splice(index, 1);
          }
        });
      }
    },
    // 全选
    handleSelectAll(selection) {
      if (selection.length) {
        this.tableData.forEach(item => {
          this.handleSelect(null, item, true);
        });
      } else {
        this.tableData.forEach(item => {
          this.handleSelect(null, item, false);
        });
      }
    },
    // 右侧删除按钮
    cancelActivity(index) {
      let deletRow = this.activitySelected.splice(index, 1);
      this.tableData.forEach((v, i) => {
        if (v.item.id === deletRow[0].item.id) {
          this.$refs.multipleTable.toggleRowSelection(v, false);
        }
      });
    },
    // 确认
    confirm() {
      if (this.activitySelected.length === 0 && !this.price) {
        this.$message({ showClose: true, message: '请输入金额', type: 'error', duration: 3000 });
      } else if (this.price > 50000) {
        this.$message({ showClose: true, message: '单次消费金额最大为5万元', type: 'error', duration: 3000 });
      } else {
        this.visible3 = true;
        let temData = 0;
        this.isIntegral_ ? (temData = 1) : (temData = 0);
        this.deductionPrice =
          this.price * this.form.currentMember.discount * this.form.currentMember.integralExchangeRatio * temData;
        this.deductionIntegral = parseInt(this.deductionPrice * this.form.exchangeIntegral);
        if (this.deductionIntegral > this.form.integral) {
          this.deductionIntegral = parseInt(this.form.integral);
          this.deductionPrice = this.deductionIntegral / this.form.exchangeIntegral;
        }
        if (this.deductionPrice > this.price * this.form.currentMember.discount) {
          this.deductionPrice = this.price * this.form.currentMember.discount;
          this.deductionIntegral = this.deductionPrice * this.form.exchangeIntegral;
        }
        this.price1 = this.price * this.form.currentMember.discount - this.deductionPrice;
      }
    },
    // 提交
    submit() {
      let url = `/unionConsume/unionId/${this.unionId}/fanId/${this.form.fan.id}`;
      let data = {};
      data.unionId = this.unionId - 0;
      data.fanId = this.form.fan.id - 0;
      if (this.shopId) {
        data.shopId = this.shopId - 0;
      } else {
        data.shopId = '';
      }
      data.isUseIntegral = this.isIntegral_ - 0;
      data.consume = {};
      data.consume.consumeMoney = this.price - 0;
      data.consume.payMoney = this.price1 - 0;
      data.consume.payType = this.payType - 0;
      data.activityId = this.activityId - 0;
      data.textList = [];
      this.activitySelected.forEach(v => {
        data.textList.push({ id: v.item.id });
      });
      $http
        .post(url, data)
        .then(res => {
          if (res.data.success) {
            if (data.textList.length > 0 && data.consume.payMoney > 0) {
              this.$message({ showClose: true, message: '收款与核销成功', type: 'success', duration: 3000 });
            } else if (data.consume.payMoney > 0) {
              this.$message({ showClose: true, message: '收款成功', type: 'success', duration: 3000 });
            } else {
              this.$message({ showClose: true, message: '核销成功', type: 'success', duration: 3000 });
            }
            this.init();
            eventBus.$emit('newTransaction');
            eventBus.$emit('unionUpdata');
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 付款二维码
    payTypeChange() {
      if (this.payType == 2) {
        let url = `/unionConsume/unionId/${this.unionId}/fanId/${this.form.fan.id}`;
        let data = {};
        data.unionId = this.unionId - 0;
        data.fanId = this.form.fan.id - 0;
        if (this.shopId) {
          data.shopId = this.shopId - 0;
        } else {
          data.shopId = '';
        }
        data.isUseIntegral = this.isIntegral_ - 0;
        data.consume = {};
        data.consume.consumeMoney = this.price - 0;
        data.consume.payMoney = this.price1 - 0;
        data.consume.payType = this.payType - 0;
        data.activityId = this.activityId - 0;
        data.textList = [];
        this.activitySelected.forEach(v => {
          data.textList.push({ id: v.item.id });
        });
        $http
          .post(url, data)
          .then(res => {
            if (res.data.data) {
              this.payUrl = res.data.data.payUrl;
              this.socketKey = res.data.data.socketKey;
              this.orderNo = res.data.data.orderNo;
              this.visible4 = true;
              this.payPrice = (this.price1 - 0).toFixed(2);
            } else {
              this.payUrl = '';
              this.socketKey = '';
              this.orderNo = '';
            }
          })
          .then(res => {
            var _this = this;
            var socketUrl = this.$store.state.socketUrl;
            if (!this.socket) {
              this.socket = io.connect(socketUrl, { reconnect: true });
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
              this.socket.on('reconnect', function(data) {
                socketindex--;
              });
              this.socket.on('chatevent', function(data_) {
                let msg = eval('(' + data_.message + ')');
                console.log(msg, 'verification');
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
                      if (data.textList.length > 0 && data.consume.payMoney > 0) {
                        _this.$message({ showClose: true, message: '收款与核销成功', type: 'success', duration: 3000 });
                      } else if (data.consume.payMoney > 0) {
                        _this.$message({ showClose: true, message: '收款成功', type: 'success', duration: 3000 });
                      } else {
                        _this.$message({ showClose: true, message: '核销成功', type: 'success', duration: 3000 });
                      }
                      _this.socketFlag.socketKey = msg.socketKey;
                      _this.socketFlag.status = msg.status;
                      _this.socketFlag.orderNo = msg.orderNo;
                      eventBus.$emit('newTransaction');
                      eventBus.$emit('unionUpdata');
                      _this.init();
                      setTimeout(() => {
                        setTimeout(() => {
                          parent.window.postMessage('closeMask()', '*');
                        }, 0);
                      }, 0);
                    } else if (msg.status == '0') {
                      _this.$message({ showClose: true, message: '支付失败', type: 'error', duration: 3000 });
                    }
                  }
                }
              });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    },
    // 关闭二维码改变付款方式
    resetData() {
      this.payType = 1;
      setTimeout(() => {
        parent.window.postMessage('openMask()', '*');
      }, 0);
    },
    resetData1() {
      this.isIntegral_ = false;
      this.price2 = '';
    },
    // 返回
    back() {
      this.init();
    },
    // 初始化
    init() {
      this.input = '';
      this.visible1 = true;
      this.visible2 = false;
      this.visible3 = false;
      this.visible4 = false;
      this.shopId = '';
      this.unionId = '';
      this.isProjectAvailable_ = false;
      this.price = '';
      this.isIntegral_ = false;
      this.price2 = '';
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
  display: flex;
  justify-content: center;
}

.code_ {
  padding: 30px;
  .model_ {
    p {
      color: #999999;
      img {
        width: 240px;
      }
    }
  }
}

/*点击输出弹出框的样式*/
.second_0 {
  .color_ {
    color: #ff4949;
    font-size: 20px;
    position: relative;
    top: 3px;
  }
  .color-1 {
    color: #ff4949;
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
