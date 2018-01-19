<template>
  <!-- 佣金支付明细 -->
  <div class="CommissionDetail">
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
      <!-- 导出按钮 -->
      <el-col style="width:289px;">
        <el-button type="primary" @click="output1">导出</el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="union.name" label="所属联盟">
      </el-table-column>
      <el-table-column prop="member.enterpriseName" label="盟员名称">
      </el-table-column>
      <el-table-column prop="contactMoney" label="商机来往金额（元）">
      </el-table-column>
      <el-table-column prop="" label="操作">
        <template slot-scope="scope">
          <div class="sizeAndColor">
            <el-button @click="showDialog(scope)">详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 佣金详情 -->
    <el-dialog title="佣金明细" :visible.sync="dialogTableVisible">
      <hr>
      <div class="middle">
        <p>
          <span style="margin-right: 20px;"> {{ unionName }} </span>
          <span> {{ enterpriseName }} </span>
          <!-- 导出按钮 -->
          <el-button type="primary" @click="output2">导出</el-button>
        </p>
        <el-table :data="gridData" max-height="500">
          <el-table-column prop="createTime" label="时间"></el-table-column>
          <el-table-column prop="clientName" label="顾客姓名"></el-table-column>
          <el-table-column prop="clientPhone" label="电话"></el-table-column>
          <el-table-column prop="brokerageMoney" label="佣金（元）"></el-table-column>
        </el-table>
        <p class="totalAll">合计
          <span>{{ contactMoney }}</span>
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'commission-detail',
  data() {
    return {
      unionId: '',
      options1: [],
      tableData: [],
      currentPage: 1,
      dialogTableVisible: false,
      gridData: [],
      unionName: '',
      enterpriseName: '',
      contactMoney: '',
      totalAll: 0,
      detailId: '',
      detailMemberId: ''
    };
  },
  mounted: function() {
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
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      this.currentPage = 1;
      this.unionId = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(`/unionBrokeragePay/detail/page?current=${this.currentPage}&unionId=${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    // 改变选取联盟
    search() {
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页查询
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 弹出框 佣金详情
    showDialog(scope) {
      this.dialogTableVisible = true;
      this.detailId = scope.row.union.id;
      this.detailMemberId = scope.row.member.id;
      $http
        .get(`/unionBrokeragePay/unionId/${this.detailId}/detail?memberId=${this.detailMemberId}`)
        .then(res => {
          if (res.data.data) {
            this.gridData = res.data.data.opportunityList;
            this.gridData.forEach(v => {
              v.createTime = timeFilter(v.createTime);
            });
            this.unionName = res.data.data.union.name;
            this.enterpriseName = res.data.data.member.enterpriseName;
            this.contactMoney = res.data.data.contactMoney;
          } else {
            this.gridData = [];
            this.unionName = '';
            this.enterpriseName = '';
            this.contactMoney = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    // 导出支付明细
    output1() {
      let url = this.$store.state.baseUrl + `/unionBrokeragePay/detail/export?unionId=${this.unionId}`;
      window.open(url);
    },
    // 导出佣金详情
    output2() {
      let url =
        this.$store.state.baseUrl +
        `/unionBrokeragePay/unionId/${this.detailId}/detail/export?memberId=${this.detailMemberId}`;
      window.open(url);
    }
  }
};
</script>
