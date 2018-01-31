<template>
  <!-- 我需支付的佣金 -->
  <div>
    <!--选择框-->
    <el-row>
      <el-col style="width:300px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="所属联盟:">
            <el-select v-model="unionId" clearable placeholder="请选择所属联盟" @change="search">
              <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col style="width:300px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="去向:">
            <el-select v-model="fromMemberId" clearable placeholder="请选择去向" @change="search">
              <el-option v-for="item in options2" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col style="width:100px;">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options3" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col style="width:200px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" @keyup.enter.native="search" v-model="input" clearable class="input-search2 fl">
            <i slot="suffix" @click="search" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我需支付的佣金表格 -->
    <el-table :data="tableData" style="width: 100%" ref="multipleTable" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="55px"></el-table-column>
      <el-table-column prop="opportunity.clientName" label="顾客姓名" min-width="100px">
      </el-table-column>
      <el-table-column prop="opportunity.clientPhone" label="电话" min-width="100px">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>电话: {{ scope.row.opportunity.clientPhone }}</p>
            <div slot="reference" class="name-wrapper">
              {{ scope.row.opportunity.clientPhone }}
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="opportunity.businessMsg" label="业务备注" min-width="100px">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>备注：{{ scope.row.opportunity.businessMsg }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.opportunity.businessMsg }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="toMember.enterpriseName" label="消费去向(盟员)" min-width="135px">
      </el-table-column>
      <el-table-column prop="union.name" label="所属联盟" min-width="100px">
      </el-table-column>
      <el-table-column prop="opportunity.acceptPrice" label="消费金额(元)" min-width="125">
      </el-table-column>
      <el-table-column prop="opportunity.brokerageMoney" label="我的佣金(元)" min-width="120">
      </el-table-column>
      <el-table-column prop="opportunity.acceptStatus" label="交易类型" min-width="100px">
      </el-table-column>
      <el-table-column prop="opportunity.isClose" label="佣金支付状态" width="140" :filters="[{ text: '未支付', value: '未支付' }, { text: '已支付', value: '已支付' }]" :filter-method="filterTag" filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag :type="scope.row.opportunity.isClose === '未支付' ? 'danger' : 'success'">{{scope.row.opportunity.isClose}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="opportunity.createTime" label="交易时间" min-width="120">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>交易时间: {{ scope.row.opportunity.createTime }}</p>
            <div slot="reference" class="name-wrapper">
              {{ scope.row.opportunity.createTime }}
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="" label="操作">
        <template slot-scope="scope">
          <div class="sizeAndColor">
            <el-button v-if="scope.row.opportunity.isClose === '未支付'" @click="pay(scope)">支付</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <div style="position: relative;padding: 5px 0;" v-if="tableData.length>0">
      <el-button @click="payAll" v-bind:disabled="!canPay" style="position: absolute;top:15px;left:0">批量支付</el-button>
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>
    <!-- 弹出框 支付页面 -->
    <div class="model_0">
      <el-dialog title="支付" :visible.sync="visible" width="30%">
        <hr>
        <div class="middle_">
          <img v-bind:src="imgSrc" class="imgSrc">
          <p>￥
            <span>{{ brokerageMoney }}</span>
          </p>
          <p style="margin-top: 5px;">使用微信扫描二维码进行支付</p>
        </div>
        <span slot="footer" class="dialog-footer">
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
import { commissionTypeFilter } from '@/utils/filter.js';
import { commissionIsCloseFilter } from '@/utils/filter.js';
export default {
  name: 'commission-income',
  data() {
    return {
      unionId: '',
      memberId: '',
      options1: [],
      fromMemberId: '',
      options2: [],
      value: 'clientName',
      options3: [
        {
          value: 'clientName',
          label: '顾客姓名'
        },
        {
          value: 'clientPhone',
          label: '顾客电话'
        }
      ],
      input: '',
      tableData: [],
      currentPage: 1,
      brokerageMoney: 0,
      visible: false,
      opportunityId: '',
      imgSrc: '',
      canPay: false,
      totalAll: 0,
      socket: '',
      socketKey: '',
      orderNo: '',
      socketFlag: {
        socketKey: '',
        orderNo: '',
        status: ''
      }
    };
  },
  watch: {
    // 切换联盟，改变来源列表
    unionId: function() {
      this.toMemberId = '';
      this.options2 = [];
      if (this.unionId) {
        $http
          .get(`/unionOpportunity/unionId/${this.unionId}/fromMember`)
          .then(res => {
            if (res.data.data) {
              this.options2 = res.data.data || [];
              res.data.data.forEach((v, i) => {
                this.options2[i].value = v.id;
                this.options2[i].label = v.enterpriseName;
              });
            } else {
              this.options2 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    }
  },
  mounted: function() {
    eventBus.$on('newCommissionPay', () => {
      this.search();
    });
    this.init();
  },
  methods: {
    init() {
      // 获取我参与过的联盟
      $http
        .get(`/unionMain/busUser`)
        .then(res => {
          if (res.data.data) {
            this.options1 = res.data.data || [];
            this.options1.forEach((v, i) => {
              v.value = v.id;
              v.label = v.name;
            });
          } else {
            this.options1 = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
      this.currentPage = 1;
      this.unionId = '';
      this.fromMemberId = '';
      this.value = 'clientName';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(
          `/unionBrokeragePay/opportunity/page?current=${this.currentPage}&unionId=${this.unionId}&fromMemberId=${
            this.fromMemberId
          }&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.opportunity.createTime = timeFilter(v.opportunity.createTime);
              v.opportunity.acceptStatus = commissionTypeFilter(v.opportunity.acceptStatus);
              v.opportunity.isClose = commissionIsCloseFilter(v.opportunity.isClose);
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 带条件搜索
    search() {
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页搜索
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 连接 socket
    mySocket() {
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
        this.socket.on('chatevent', function(data) {
          let msg = eval('(' + data.message + ')');
          console.log(msg, 'commisssionPay');
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
                _this.visible = false;
                _this.socketFlag.socketKey = msg.socketKey;
                _this.socketFlag.status = msg.status;
                _this.socketFlag.orderNo = msg.orderNo;
                _this.init();
              } else if (msg.status == '0') {
                _this.$message({ showClose: true, message: '支付失败', type: 'error', duration: 3000 });
              }
            }
          }
        });
      }
    },
    // 支付
    pay(scope) {
      this.brokerageMoney = scope.row.opportunity.brokerageMoney.toFixed(2);
      this.opportunityId = scope.row.opportunity.id;
      let url = `/unionBrokeragePay/opportunity`;
      let data = [this.opportunityId];
      $http
        .post(url, data)
        .then(res => {
          if (res.data.data) {
            this.imgSrc = res.data.data.payUrl;
            this.socketKey = res.data.data.socketKey;
            this.orderNo = res.data.data.orderNo;
            this.visible = true;
            this.mySocket();
          } else {
            this.imgSrc = '';
            this.socketKey = '';
            this.orderNo = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 批量支付
    payAll() {
      let ids = [];
      this.brokerageMoney = 0;
      this.multipleSelection.forEach((v, i) => {
        this.brokerageMoney += Number(v.opportunity.brokerageMoney);
        ids.push(v.opportunity.id);
      });
      this.brokerageMoney = this.brokerageMoney.toFixed(2);
      let url = `/unionBrokeragePay/opportunity`;
      let data = ids;
      $http
        .post(url, data)
        .then(res => {
          if (res.data.data) {
            this.imgSrc = res.data.data.payUrl;
            this.socketKey = res.data.data.socketKey;
            this.orderNo = res.data.data.orderNo;
            this.visible = true;
            this.mySocket();
          } else {
            this.imgSrc = '';
            this.socketKey = '';
            this.orderNo = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    filterTag(value, row) {
      return row.opportunity.isClose === value;
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
      if (this.multipleSelection.length) {
        this.canPay = true;
        this.multipleSelection.forEach((v, i) => {
          if (v.opportunity.isClose === '已支付') {
            this.canPay = false;
          }
        });
      } else {
        this.canPay = false;
      }
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
