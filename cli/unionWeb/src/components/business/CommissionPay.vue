<template>
  <!-- 我需支付的佣金 -->
  <div>
    <!--选择框-->
    <el-row>
      <el-col style="width:300px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="所属联盟:">
            <el-select v-model="unionId" clearable placeholder="请选择所属联盟">
              <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col style="width:300px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="去向:">
            <el-select v-model="fromMemberId" clearable placeholder="请选择去向">
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
          <el-input placeholder="请输入关键字" @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我需支付的佣金表格 -->
    <el-table :data="tableData" style="width: 100%" ref="multipleTable" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="55px"></el-table-column>
      <el-table-column prop="clientName" label="顾客姓名" min-width="100px">
      </el-table-column>
      <el-table-column prop="clientPhone" label="电话" min-width="100px">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>手机号: {{ scope.row.clientPhone }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.clientPhone }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="businessMsg" label="业务备注" min-width="100px">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>{{ scope.row.businessMsg }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.businessMsg }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="fromEnterpriseName" label="消费去向(盟员)" min-width="135px">
      </el-table-column>
      <el-table-column prop="unionName" label="所属联盟" min-width="100px">
      </el-table-column>
      <el-table-column prop="acceptPrice" label="消费金额(元)" min-width="125">
      </el-table-column>
      <el-table-column prop="brokeragePrice" label="我的佣金(元)" min-width="120">
      </el-table-column>
      <el-table-column prop="opportunityType" label="交易类型" min-width="100px">
      </el-table-column>
      <el-table-column prop="isClose" label="佣金结算状态" width="140" :filters="[{ text: '未结算', value: '未结算' }, { text: '已结算', value: '已结算' }]" :filter-method="filterTag" filter-placement="bottom-end">
        <template scope="scope">
          <el-tag :type="scope.row.isClose === '未结算' ? 'danger' : 'success'">{{scope.row.isClose}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastModifyTime" label="交易时间" min-width="120">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>{{ scope.row.lastModifyTime }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.lastModifyTime }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template scope="scope">
          <div class="sizeAndColor">
            <el-button v-if="scope.row.isClose === '未结算'" @click="pay(scope)">支付</el-button>
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
      <el-dialog title="支付" :visible.sync="dialogVisible" size="tiny">
        <hr>
        <div class="middle_">
          <img v-bind:src="imgSrc" class="imgSrc">
          <p>￥
            <span>{{ brokeragePrice }}</span>
          </p>
          <p>扫描二维码进行支付</p>
        </div>
        <span slot="footer" class="dialog-footer">
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import $todate from '@/utils/todate.js';
export default {
  name: 'commission-income',
  data() {
    return {
      unionId: '',
      memberId: '',
      options1: [],
      fromMemberId: '',
      options2: [],
      value: '',
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
      brokeragePrice: 0,
      dialogVisible: false,
      opportunityId: '',
      imgSrc: '',
      canPay: false,
      totalAll: 0,
      only: '',
      userId: '',
      socket: '',
      socketFlag: {
        only: '',
        status: ''
      }
    };
  },
  computed: {
    initUnionId() {
      return this.$store.state.unionId;
    }
  },
  watch: {
    initUnionId: function() {
      this.init();
    },
    unionId: function() {
      this.fromMemberId = '';
      this.options2 = [];
      // 通过对应的unionId获取对应的memberId
      $http
        .get(`/unionMember/listMap`)
        .then(res => {
          if (res.data.data) {
            res.data.data.forEach((v, i) => {
              if (v.unionMain.id === this.unionId) {
                this.memberId = v.unionMember.id;
                $http
                  .get(`/unionMember/listMap/memberId/${this.memberId}`)
                  .then(res => {
                    if (res.data.data) {
                      this.options2 = res.data.data;
                      res.data.data.forEach((v, i) => {
                        this.options2[i].value = v.memberId;
                        this.options2[i].label = v.enterpriseName;
                      });
                    } else {
                      this.options2 = [];
                    }
                  })
                  .catch(err => {
                    this.$message({
                      showClose: true,
                      message: err.toString(),
                      type: 'error',
                      duration: 5000
                    });
                  });
              }
              this.search();
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    fromMemberId: function() {
      this.search();
    }
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      if (this.initUnionId) {
        // 获取联盟列表
        $http
          .get(`/unionMember/listMap`)
          .then(res => {
            if (res.data.data && res.data.data.length > 0) {
              this.options1 = res.data.data;
              res.data.data.forEach((v, i) => {
                this.options1[i].value = v.unionMain.id;
                this.options1[i].label = v.unionMain.name;
              });
            } else {
              this.options1 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
        $http
          .get(`/unionOpportunity/expense?current=1`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records;
              this.totalAll = res.data.data.total;
              this.tableData.forEach((v, i) => {
                v.lastModifyTime = $todate.todate(new Date(v.lastModifyTime));
                switch (v.isClose) {
                  case 0:
                    v.isClose = '未结算';
                    break;
                  case 1:
                    v.isClose = '已结算';
                    break;
                }
                switch (v.opportunityType) {
                  case 1:
                    v.opportunityType = '线上';
                    break;
                  case 2:
                    v.opportunityType = '线下';
                    break;
                }
              });
            } else {
              this.tableData = [];
              this.totalAll = 0;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 带条件搜索
    search() {
      $http
        .get(
          `/unionOpportunity/expense?current=1&unionId=${this.unionId}&fromMemberId=${this.fromMemberId}&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.lastModifyTime = $todate.todate(new Date(v.lastModifyTime));
              switch (v.isClose) {
                case 0:
                  v.isClose = '未结算';
                  break;
                case 1:
                  v.isClose = '已结算';
                  break;
              }
              switch (v.opportunityType) {
                case 1:
                  v.opportunityType = '线上';
                  break;
                case 2:
                  v.opportunityType = '线下';
                  break;
              }
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页搜索
    handleCurrentChange(val) {
      $http
        .get(
          `/unionOpportunity/expense?current=${val}&unionId=${this.unionId}&fromMemberId=${this.fromMemberId}&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.lastModifyTime = $todate.todate(new Date(v.lastModifyTime));
              switch (v.isClose) {
                case 0:
                  v.isClose = '未结算';
                  break;
                case 1:
                  v.isClose = '已结算';
                  break;
              }
              switch (v.opportunityType) {
                case 1:
                  v.opportunityType = '线上';
                  break;
                case 2:
                  v.opportunityType = '线下';
                  break;
              }
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 支付
    pay(scope) {
      this.brokeragePrice = scope.row.brokeragePrice.toFixed(2);
      this.opportunityId = scope.row.opportunityId;
      this.dialogVisible = true;
      $http
        .get(`/unionOpportunity/qrCode/${this.opportunityId}`)
        .then(res => {
          if (res.data.data) {
            this.imgSrc = res.data.data.url;
            this.only = res.data.data.only;
            this.userId = res.data.data.userId;
          } else {
            this.imgSrc = '';
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
                  _this.visible4 = true;
                  _this.search();
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
    },
    // 批量支付
    payAll() {
      let ids = '';
      this.brokeragePrice = 0;
      this.multipleSelection.forEach((v, i) => {
        this.brokeragePrice += Number(v.brokeragePrice);
        ids += v.opportunityId + '%2C';
      });
      this.dialogVisible = true;
      $http
        .get(`/unionOpportunity/qrCode/${ids}`)
        .then(res => {
          if (res.data.data) {
            this.imgSrc = res.data.data.url;
            this.only = res.data.data.only;
            this.userId = res.data.data.userId;
          } else {
            this.imgSrc = '';
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
                  _this.$message({
                    showClose: true,
                    message: '支付成功',
                    type: 'success',
                    duration: 5000
                  });
                  _this.visible3 = false;
                  _this.visible4 = true;
                  _this.search();
                } else if (msg.status == '004') {
                  _this.$message({
                    showClose: true,
                    message: '请求超时',
                    type: 'warning',
                    duration: 5000
                  });
                } else if (msg.status == '005') {
                  _this.$message({
                    showClose: true,
                    message: '支付失败',
                    type: 'warning',
                    duration: 5000
                  });
                }
              }
            }
            _this.socketFlag.only = msg.only;
            _this.socketFlag.status = msg.status;
          });
        })
        .catch(err => {
          this.$message({
            showClose: true,
            message: err.toString(),
            type: 'error',
            duration: 5000
          });
        });
    },
    filterTag(value, row) {
      return row.isClose === value;
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
      if (this.multipleSelection.length) {
        this.canPay = true;
        this.multipleSelection.forEach((v, i) => {
          if (v.isClose === '已结算') {
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
.imgSrc {
  width: 200px;
  height: 200px;
}
</style>

