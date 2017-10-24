<template>
  <!-- 佣金支付明细 -->
  <div class="CommissionDetail">
    <el-row>
      <el-col style="width:300px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="所属联盟:">
            <el-select v-model="memberId" clearable placeholder="请选择所属联盟" @change="change">
              <el-option v-for="item in options1" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </el-col>
      <!-- 导出按钮 -->
      <el-col style="width:300px;">
        <el-button type="primary" @click="output2">导出</el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="tgtUnionName" label="所属联盟">
      </el-table-column>
      <el-table-column prop="tgtMemberEnterpriseName" label="盟员名称">
      </el-table-column>
      <el-table-column prop="contactMoney" label="商机来往金额（元）">
      </el-table-column>
      <el-table-column label="操作">
        <template scope="scope">
          <div class="sizeAndColor">
            <el-button @click="showDialog(scope)">详情</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 佣金明细 -->
    <el-dialog title="佣金明细" :visible.sync="dialogTableVisible">
      <hr>
      <div class="middle">
        <p>
          <span style="margin-right: 20px;"> {{ unionName }} </span>
          <span> {{ enterpriseName }} </span>
          <!-- 导出按钮 -->
          <el-button type="primary" @click="output1">导出</el-button>
        </p>
        <el-table :data="gridData">
          <el-table-column prop="lastModifyTime" label="时间"></el-table-column>
          <el-table-column prop="clientName" label="顾客姓名"></el-table-column>
          <el-table-column prop="clientPhone" label="电话"></el-table-column>
          <el-table-column prop="brokeragePrice" label="佣金（元）"></el-table-column>
        </el-table>
        <p class="totalAll">合计 {{ contactMoneySum }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'commission-detail',
  data() {
    return {
      memberId: '',
      options1: [],
      tableData: [],
      currentPage: 1,
      dialogTableVisible: false,
      gridData: [],
      unionName: '',
      enterpriseName: '',
      contactMoneySum: '',
      totalAll: 0,
      tgtMemberId: ''
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
                this.options1[i].value = v.unionMember.id;
                this.options1[i].label = v.unionMain.name;
              });
            } else {
              this.options1 = [];
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
        $http
          .get(`/unionOpportunity/contact/page?current=1`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records;
              this.totalAll = res.data.data.total;
            } else {
              this.tableData = [];
              this.totalAll = 0;
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
    },
    // 改变选取联盟
    change() {
      $http
        .get(`/unionOpportunity/contact/page?current=1&memberId=${this.memberId}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
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
    },
    // 分页查询
    handleCurrentChange(val) {
      $http
        .get(`/unionOpportunity/contact/page?current=${val}&memberId=${this.memberId}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
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
    },
    // 弹出框 佣金详情
    showDialog(scope) {
      this.dialogTableVisible = true;
      this.tgtMemberId = scope.row.tgtMemberId;
      $http
        .get(`/unionOpportunity/contact/detail?tgtMemberId=${this.tgtMemberId}&memberId=${this.memberId}`)
        .then(res => {
          if (res.data.data) {
            this.gridData = res.data.data.contactList;
            this.unionName = res.data.data.tgtUnionName;
            this.enterpriseName = res.data.data.tgtMemberEnterpriseName;
            this.contactMoneySum = res.data.data.contactMoneySum;
          } else {
            this.gridData = [];
            this.unionName = '';
            this.enterpriseName = '';
            this.contactMoneySum = '';
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
    },
    // 导出佣金明细详情
    output1() {
      let url =
        this.$store.state.baseUrl +
        `/unionOpportunity/contact/exportDetail?tgtMemberId=${this.tgtMemberId}&memberId=${this.memberId}`;
      window.open(url);
    },
    // 导出支付明细
    output2() {
      let url = this.$store.state.baseUrl + `/unionOpportunity/contact/export?memberId=${this.memberId}`;
      window.open(url);
    }
  }
};
</script>
