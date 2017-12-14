<template>
  <!-- 售卡佣金分成记录 -->
  <div id="">
    <el-row>
      <el-col :span="4" style="width:210px">
        <div class="grid-content bg-purple1">
          <el-input placeholder="请输入联盟卡号" icon="search" v-model="value1" :on-icon-click="handleIconClick" class="input-search2 fl" @keyup.enter.native="handleIconClick">
          </el-input>
        </div>
      </el-col>
      <el-col :span="4" style="width:210px;margin-left: 50px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" icon="search" v-model="input" :on-icon-click="handleIconClick" class="input-search2 fl" @keyup.enter.native="handleIconClick">
          </el-input>
        </div>
      </el-col>
      <el-col :span="5" style="width:210px;margin-left: 50px;">
        <div class="block">
          <el-date-picker v-model="value2" type="daterange" placeholder="选择日期范围">
          </el-date-picker>
        </div>
      </el-col>
      <!-- 导出按钮 -->
      <el-col :span="2" style="width:80px;margin-left: 50px;">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <!-- 售卡佣金分成记录表格 -->
    <el-table :data="tableData" style="width: 100%;margin-top: 15px;">
      <el-table-column prop="sharingRecord.createTime" label="加入时间">
      </el-table-column>
      <el-table-column prop="fan.number" label="联盟卡号">
      </el-table-column>
      <el-table-column prop="sharingRecord.sellPrice" label="售卡金额（元）">
      </el-table-column>
      <el-table-column prop="sharingRecord.sharingMoney" label="售卡佣金（元）">
      </el-table-column>
      <el-table-column prop="member.enterpriseName" label="售卡出处" width="150">
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'sell-divide-proportion',
  data() {
    return {
      typeOptions: '',
      input: '',
      value1: '',
      value2: '',
      tableData: [],
      currentPage: 1,
      totalAll: 0
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  watch: {
    // typeOptions: function() {
    //   let beginTime, endTime;
    //   if (this.value2[0]) {
    //     beginTime = $todate.todate(this.value2[0]);
    //     endTime = $todate.todate(this.value2[1]);
    //   } else {
    //     beginTime = '';
    //     endTime = '';
    //   }
    //   $http
    //     .get(
    //       `unionBrokerageIncome/card/memberId/${this.unionMemberId}?current=1&cardType=${this.typeOptions}&` +
    //         this.value +
    //         '=' +
    //         this.input +
    //         `&beginDate=${beginTime}&endDate=${endTime}`
    //     )
    //     .then(res => {
    //       if (res.data.data) {
    //         this.tableData = res.data.data.records;
    //         this.tableData.forEach((v, i) => {
    //           switch (v.cardType) {
    //             case 1:
    //               v.cardType = '黑卡';
    //               break;
    //             case 2:
    //               v.cardType = '红卡';
    //               break;
    //           }
    //         });
    //       } else {
    //         this.tableData = [];
    //       }
    //     })
    //     .catch(err => {
    //       this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
    //     });
    // }
  },
  mounted: function() {
    $http
      .get(`/unionCardSharingRecord/unionId/${this.unionId}/page?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData = res.data.data.records;
          this.tableData.forEach((v, i) => {
            v.sharingRecord.createTime = timeFilter(v.sharingRecord.createTime);
          });
          this.totalAll = res.data.data.total;
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 带条件搜索
    handleIconClick(ev) {
      let beginTime, endTime;
      if (this.value2[0]) {
        beginTime = timeFilter(this.value2[0]);
        endTime = timeFilter(this.value2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionCardSharingRecord/unionId/${this.unionId}/page?current=1&cardNumber=${this
            .value1}&beginTime=${beginTime}&endTime=${endTime}`
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.tableData.forEach((v, i) => {
              v.sharingRecord.createTime = timeFilter(v.sharingRecord.createTime);
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页
    handleCurrentChange(val) {
      let beginTime, endTime;
      if (this.value2[0]) {
        beginTime = timeFilter(this.value2[0]);
        endTime = timeFilter(this.value2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionCardSharingRecord/unionId/${this.unionId}/page?current=${val}&cardNumber=${this
            .value1}&beginTime=${beginTime}&endTime=${endTime}`
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.sharingRecord.createTime = timeFilter(v.sharingRecord.createTime);
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // todo
    // 导出售卡分成记录
    output() {
      let beginTime, endTime;
      if (this.value2[0]) {
        beginTime = $todate.todate(this.value2[0]);
        endTime = $todate.todate(this.value2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        this.$store.state.baseUrl +
        `/unionBrokerageIncome/exportCard/memberId/${this.unionMemberId}?&cardType=${this.typeOptions}&` +
        this.value +
        '=' +
        this.input +
        `&beginDate=${beginTime}&endDate=${endTime}`;
      window.open(url);
    }
  }
};
</script>

