<template>
  <div>
    <div id="ExpenseRecord">
      <!-- 搜索栏 -->
      <el-row>
        <el-col style="width:287px;">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="所属联盟:">
              <el-select v-model="unionId" clearable placeholder="请选择所属联盟" @change="search">
                <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col style="width:276px;">
          <el-form :inline="true" class="demo-form-inline">
            <el-form-item label="消费门店:">
              <el-select v-model="shopId" clearable placeholder="请选择来源" @change="search">
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
        <el-col style="width:180px;margin-right: 20px;">
          <div class="grid-content1 bg-purple">
            <el-input placeholder="请输入关键字" @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
            </el-input>
          </div>
        </el-col>
        <el-col :span="3" style="width:235px;">
          <div class="third_">
            <div class="block">
              <el-date-picker v-model="timeValue" type="daterange" placeholder="选择日期范围" @change="search">
              </el-date-picker>
            </div>
          </div>
        </el-col>
        <!-- 导出按钮 -->
        <el-col style="width:50px;">
          <el-button type="primary" @click="output">导出</el-button>
        </el-col>
      </el-row>
      <!-- 记录表格 -->
      <el-table :data="tableData" style="width: 100%;">
        <el-table-column prop="union.name" label="所属联盟">
        </el-table-column>
        <el-table-column prop="shopName" label="消费门店">
        </el-table-column>
        <el-table-column prop="fan.number" label="联盟卡号">
        </el-table-column>
        <el-table-column prop="fan.phone" label="手机号">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <p>手机号: {{ scope.row.fan.phone }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row.fan.phone }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="consume.consumeMoney" label="消费金额">
        </el-table-column>
        <el-table-column prop="consume.payMoney" label="实收金额">
        </el-table-column>
        <el-table-column label="优惠项目">
          <template slot-scope="scope">
            <span @click="showDetail(scope)" style="color: #20a0ff;cursor: pointer"> {{ scope.row.itemList }} </span>
          </template>
        </el-table-column>
        <el-table-column prop="consume.payStatus" label="支付状态">
        </el-table-column>
        <el-table-column prop="consume.createTime" label="消费时间">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <p>消费时间: {{ scope.row.consume.createTime }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row.consume.createTime }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
      <!-- 弹出框 核销详情 -->
      <div class="verificationMessage">
        <el-dialog title="核销详情" :visible.sync="visible">
          <div v-if="noErpList.length > 0">
            <p>非 ERP 项目</p>
            <div v-for="(item, index) in noErpList" :key="item.id">
              {{ index + 1 }} 、{{ item.name }}
            </div>
          </div>
          <div v-if="erpList.length > 0">
            <p> ERP 项目</p>
            <div v-for="(item, index) in erpList" :key="item.id">
              {{ index + 1 }} 、{{ item.name }}
            </div>
          </div>
          <div v-if="erpGoodsList.length > 0">
            <p>商品</p>
            <div v-for="(item, index) in erpGoodsList" :key="item.id">
              {{ index + 1 }} 、{{ item.name }}
            </div>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
import { expenseStatusFilter } from '@/utils/filter.js';
export default {
  name: 'expense-record',
  data() {
    return {
      unionId: '',
      options1: [],
      shopId: '',
      options2: [],
      value: '',
      options3: [{ value: 'cardNo', label: '联盟卡号' }, { value: 'phone', label: '顾客电话' }],
      input: '',
      tableData: [],
      currentPage: 1,
      timeValue: '',
      visible: false,
      noErpList: [],
      erpList: [],
      erpGoodsList: []
    };
  },
  mounted: function() {
    this.getUnionList();
    this.getShopList();
    this.init();
    // 是否有新的消费核销成功
    eventBus.$on('newTransaction', () => {
      this.search();
    });
  },
  watch: {},
  methods: {
    // 获取联盟列表
    getUnionList() {
      $http
        .get(`/unionMain/my`)
        .then(res => {
          if (res.data.data) {
            this.options1 = res.data.data || [];
            this.options1.forEach((v, i) => {
              v.value = v.union.id;
              v.label = v.union.name;
            });
          } else {
            this.options1 = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 获取门店列表
    getShopList() {
      $http
        .get(`/api/shop/list`)
        .then(res => {
          if (res.data.data) {
            this.options2 = res.data.data || [];
            this.options2.forEach((v, i) => {
              v.value = v.id;
              v.label = v.name;
            });
          } else {
            this.options1 = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 消费记录加载
    init() {
      $http
        .get(`/unionConsume/record/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.itemList = [];
              v.nonErpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.erpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.itemList = v.itemList.join(',');
              v.consume.createTime = timeFilter(v.consume.createTime);
              v.consume.payStatus = expenseStatusFilter(v.consume.payStatus);
            });
          } else {
            this.tableData = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 带条件搜索消费记录
    search() {
      let beginTime, endTime;
      if (this.timeValue[0]) {
        beginTime = this.timeValue[0].getTime();
        endTime = this.timeValue[1].getTime();
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        `/unionConsume/record/page?current=1&unionId=${this.unionId}&shopId=${this.shopId}&` +
        this.value +
        '=' +
        this.input +
        `&beginTime=${beginTime}&endTime=${endTime}`;
      $http
        .get(url)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.itemList = [];
              v.nonErpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.erpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.itemList.join(',');
              v.consume.createTime = timeFilter(v.consume.createTime);
              v.consume.payStatus = expenseStatusFilter(v.consume.payStatus);
            });
          } else {
            this.tableData = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页搜索本店消费记录
    handleCurrentChange(val) {
      let beginTime, endTime;
      if (this.timeValue[0]) {
        beginTime = this.timeValue[0].getTime();
        endTime = this.timeValue[1].getTime();
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        `/unionConsume/record/page?current=${val}&unionId=${this.unionId}&shopId=${this.shopId}&` +
        this.value +
        '=' +
        this.input +
        `&beginTime=${beginTime}&endTime=${endTime}`;
      $http
        .get(url)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.itemList = [];
              v.nonErpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.erpTextList.forEach((val, idx) => {
                v.itemList.push(val.name);
              });
              v.itemList.join(',');
              v.consume.createTime = timeFilter(v.consume.createTime);
              v.consume.payStatus = expenseStatusFilter(v.consume.payStatus);
            });
          } else {
            this.tableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出本店消费记录
    output() {
      let url = this.$store.state.baseUrl + `/unionConsume/record/export`;
      window.open(url);
    },
    // 显示核销详情
    showDetail(scope) {
      this.noErpList = scope.row.nonErpTextList;
      this.erpList = scope.row.erpTextList;
      this.erpGoodsList = scope.row.erpGoodsList;
      this.visible = true;
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
