<template>
  <div>
    <div id="ExpenseRecord">
      <!-- 搜索栏 -->
      <div class="searchBar">
        <el-row>
          <el-col style="width:26%;max-width: 238px">
            <el-form :inline="true" class="demo-form-inline">
              <el-form-item label="所属联盟:">
                <el-select v-model="unionId" clearable placeholder="请选择所属联盟" @change="search">
                  <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col style="width:26%;max-width: 238px">
            <el-form :inline="true" class="demo-form-inline">
              <el-form-item label="消费门店:">
                <el-select v-model="shopId" clearable placeholder="请选择来源" @change="search">
                  <el-option v-for="item in options2" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </el-col>
          <el-col style="width:9%;max-width: 100px">
            <div class="grid-content bg-purple">
              <el-select v-model="value" clearable placeholder="请选择" class="fl">
                <el-option v-for="item in options3" :key="item.value" :label="item.label" :value="item.value">
                </el-option>
              </el-select>
            </div>
          </el-col>
          <el-col style="width:15%;max-width: 200px;margin-right: 20px;">
            <div class="grid-content1 bg-purple">
              <el-input placeholder="请输入关键字" @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
              </el-input>
            </div>
          </el-col>
          <el-col :span="3" style="width:16%;">
            <div class="third_">
              <div class="block">
                <el-date-picker v-model="timeValue" type="daterange" placeholder="选择日期范围" @change="search">
                </el-date-picker>
              </div>
            </div>
          </el-col>
          <!-- 导出按钮 -->
          <el-col style="width:1%;height: 38px;line-height: 33px;">
            <span class="btn1">
              <el-button type="primary" @click="output" >导出</el-button>
            </span>
        </el-col>
        </el-row>
      </div>
      <!-- 记录表格 -->
      <el-table :data="tableData" style="width: 100%;">
        <el-table-column prop="union.name" label="所属联盟">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>所属联盟: {{ scope.row.union.name }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row.union.name }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="consume.shopName" label="消费门店">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>消费门店: {{ scope.row.consume.shopName }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row.consume.shopName }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="fan.number" label="联盟卡号">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>联盟卡号: {{ scope.row.fan.number }}</p>
              <div slot="reference" class="name-wrapper">
                {{ scope.row.fan.number }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="fan.phone" label="手机号">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
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
            <el-popover trigger="hover" placement="bottom">
              <p>{{ scope.row.itemList }}</p>
              <div slot="reference" class="name-wrapper">
                <span @click="showDetail(scope)" style="color: #20a0ff;cursor: pointer">{{ scope.row.itemList }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="consume.payStatus" label="支付状态">
        </el-table-column> -->
        <el-table-column prop="consume.createTime" label="消费时间">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom">
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
          <div v-if="nonErpTextList.length > 0">
            <p>非 ERP 项目</p>
            <div v-for="(item, index) in nonErpTextList" :key="item.id">
              {{ index + 1 }} 、{{ item.name }}
            </div>
          </div>
          <div v-if="erpTextList.length > 0">
            <p> ERP 项目</p>
            <div v-for="(item, index) in erpTextList" :key="item.id">
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
import { timeFilter, expenseStatusFilter } from '@/utils/filter.js';
export default {
  name: 'expense-record',
  data() {
    return {
      unionId: '',
      options1: [],
      shopId: '',
      options2: [],
      value: 'cardNumber',
      options3: [{ value: 'cardNumber', label: '联盟卡号' }, { value: 'phone', label: '顾客电话' }],
      input: '',
      tableData: [],
      currentPage: 1,
      timeValue: '',
      visible: false,
      nonErpTextList: [],
      erpTextList: [],
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
    // 获取我参与过的联盟
    getUnionList() {
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
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
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
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    // 消费记录加载
    init() {
      this.currentPage = 1;
      this.unionId = '';
      this.shopId = '';
      this.value = 'cardNumber';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      let beginTime, endTime;
      if (this.timeValue[0]) {
        beginTime = this.timeValue[0].getTime();
        endTime = this.timeValue[1].getTime();
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        `/unionConsume/record/page?current=${this.currentPage}&unionId=${this.unionId}&shopId=${this.shopId}&` +
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
              if (v.nonErpTextList || v.erpTextList || v.erpGoodsList) {
                v.itemList = [];
                v.nonErpTextList.forEach((val, idx) => {
                  v.itemList.push(val.name);
                });
                v.erpTextList.forEach((val, idx) => {
                  v.itemList.push(val.name);
                });
                v.erpGoodsList.forEach((val, idx) => {
                  v.itemList.push(val.name);
                });
                v.itemList = v.itemList.join(',');
              }
              v.consume.createTime = timeFilter(v.consume.createTime);
              v.consume.payStatus = expenseStatusFilter(v.consume.payStatus);
            });
          } else {
            this.tableData = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    // 带条件搜索消费记录
    search() {
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页搜索消费记录
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 导出消费记录
    output() {
      let beginTime, endTime;
      if (this.timeValue[0]) {
        beginTime = this.timeValue[0].getTime();
        endTime = this.timeValue[1].getTime();
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        this.$store.state.baseUrl +
        `/unionConsume/record/export?unionId=${this.unionId}&shopId=${this.shopId}&` +
        this.value +
        '=' +
        this.input +
        `&beginTime=${beginTime}&endTime=${endTime}`;
      window.open(url);
    },
    // 显示核销详情
    showDetail(scope) {
      this.nonErpTextList = scope.row.nonErpTextList;
      this.erpTextList = scope.row.erpTextList;
      this.erpGoodsList = scope.row.erpGoodsList;
      this.visible = true;
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
