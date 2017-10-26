<template>
  <div>
    <div id="ExpenseRecord">
      <el-tabs v-model="activeName">
        <!-- 本店消费 -->
        <el-tab-pane label="本店消费" name="first">
          <el-row>
            <el-col style="width:297px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item label="所属联盟:">
                  <el-select v-model="unionId1" clearable placeholder="请选择所属联盟">
                    <el-option v-for="item in options11" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col style="width:270px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item label="来源:">
                  <el-select v-model="toMemberId1" clearable placeholder="请选择来源">
                    <el-option v-for="item in options12" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col style="width:100px;">
              <div class="grid-content bg-purple">
                <el-select v-model="value1" clearable placeholder="请选择" class="fl">
                  <el-option v-for="item in options13" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </div>
            </el-col>
            <el-col style="width:180px;margin-right: 20px;">
              <div class="grid-content1 bg-purple">
                <el-input placeholder="请输入关键字" @keyup.enter.native="search1" icon="search" v-model="input1" :on-icon-click="search1" class="input-search2 fl">
                </el-input>
              </div>
            </el-col>
              <el-col :span="3" style="width:235px;">
                <div class="third_">
                  <div class="block">
                    <el-date-picker v-model="timeValue1" type="daterange" placeholder="选择日期范围" @change="search1">
                    </el-date-picker>
                  </div>
                </div>
              </el-col>
            <!-- 导出按钮 -->
            <el-col style="width:50px;">
              <el-button type="primary" @click="output1">导出</el-button>
            </el-col>
          </el-row>
          <!-- 记录表格 -->
          <el-table :data="tableData1" style="width: 100%;">
            <el-table-column prop="memberName" label="来源">
            </el-table-column>
            <el-table-column prop="unionName" label="所属联盟">
            </el-table-column>
            <el-table-column prop="shopName" label="消费门店">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom" effect="Dark">
                  <p> 门店：{{ scope.row.shopName }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.shopName }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="cardNo" label="联盟卡号">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom">
                  <p>卡号：{{ scope.row.cardNo }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.cardNo }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="150px">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom">
                  <p>手机号: {{ scope.row.phone }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.phone }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="consumeMoney" label="消费金额">
            </el-table-column>
            <el-table-column prop="payMoney" label="实收金额">
            </el-table-column>
            <el-table-column prop="items_" label="优惠项目">
            </el-table-column>
            <el-table-column prop="status" label="支付状态">
            </el-table-column>
            <el-table-column prop="createtime" label="创建时间" width="180px">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom">
                  <p>时间: {{ scope.row.createtime }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.createtime }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="handleCurrentChange1" :current-page.sync="currentPage1" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll1" v-if="tableData1.length>0">
          </el-pagination>
        </el-tab-pane>
        <!-- 他店消费 -->
        <el-tab-pane label="他店消费" name="second">
          <el-row>
            <el-col style="width:297px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item label="所属联盟:">
                  <el-select v-model="unionId2" clearable placeholder="请选择所属联盟">
                    <el-option v-for="item in options21" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col style="width:270px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-form-item label="来源:">
                  <el-select v-model="toMemberId2" clearable placeholder="请选择来源">
                    <el-option v-for="item in options22" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
            </el-col>
            <el-col style="width:100px;">
              <div class="grid-content bg-purple">
                <el-select v-model="value2" clearable placeholder="请选择" class="fl" >
                  <el-option v-for="item in options23" :key="item.value" :label="item.label" :value="item.value">
                  </el-option>
                </el-select>
              </div>
            </el-col>
            <el-col style="width:180px;margin-right: 20px;">
              <div class="grid-content1 bg-purple">
                <el-input placeholder="请输入关键字" @keyup.enter.native="search2" icon="search" v-model="input2" :on-icon-click="search2" class="input-search2 fl" >
                </el-input>
              </div>
            </el-col>
            <div class="third_">
              <el-col style="width:235px;">
                <div class="block">
                  <el-date-picker v-model="timeValue2" type="daterange" placeholder="选择日期范围" @change="search2">
                  </el-date-picker>
                </div>
              </el-col>
            </div>
            <!-- 导出按钮 -->
            <el-col style="width:50px;">
              <el-button type="primary" @click="output2">导出</el-button>
            </el-col>
          </el-row>
          <!-- 记录表格 -->
          <el-table :data="tableData2" style="width: 100%">
            <el-table-column prop="memberName" label="来源">
            </el-table-column>
            <el-table-column prop="unionName" label="所属联盟">
            </el-table-column>
            <el-table-column prop="shopName" label="消费门店" min-width="100px">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom" effect="Dark">
                  <p> 门店：{{ scope.row.shopName }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.shopName }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="cardNo" label="联盟卡号">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom" effect="Dark">
                  <p> 卡号：{{ scope.row.cardNo }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.cardNo }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" width="150px">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom">
                  <p>手机号: {{ scope.row.phone }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.phone }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="consumeMoney" label="消费金额">
            </el-table-column>
            <el-table-column prop="payMoney" label="实收金额">
            </el-table-column>
            <el-table-column prop="items_" label="优惠项目">
            </el-table-column>
            <el-table-column prop="status" label="支付状态">
            </el-table-column>
            <el-table-column prop="createtime" label="创建时间" width="180px">
              <template scope="scope">
                <el-popover trigger="hover" placement="bottom" effect="Dark">
                  <p> 创建时间：{{ scope.row.createtime }}</p>
                  <div slot="reference" class="name-wrapper">
                    <span>{{ scope.row.createtime }}</span>
                  </div>
                </el-popover>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @current-change="handleCurrentChange2" :current-page.sync="currentPage2" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll2" v-if="tableData2.length>0">
          </el-pagination>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import $todate from '@/utils/todate.js';
export default {
  name: 'expense-record',
  data() {
    return {
      activeName: 'first',
      // 本店
      unionId1: '',
      toMemberId1: '',
      options11: [],
      toMemberId1: '',
      options12: [],
      value1: '',
      options13: [{ value: 'cardNo', label: '联盟卡号' }, { value: 'phone', label: '顾客电话' }],
      input1: '',
      tableData1: [],
      currentPage1: 1,
      timeValue1: '',
      // 他店
      unionId2: '',
      toMemberId2: '',
      options21: [],
      toMemberId2: '',
      options22: [],
      value2: '',
      options23: [{ value: 'cardNo', label: '联盟卡号' }, { value: 'phone', label: '顾客电话' }],
      input2: '',
      tableData2: [],
      currentPage2: 1,
      timeValue2: '',
      totalAll1: 0,
      totalAll2: 0,
      memberId1: '',
      memberId2: ''
    };
  },
  mounted: function() {
    // 获取联盟列表
    $http
      .get(`/unionMember/listMap`)
      .then(res => {
        if (res.data.data && res.data.data.length > 0) {
          this.options11 = res.data.data;
          this.options21 = res.data.data;
          res.data.data.forEach((v, i) => {
            this.options11[i].value = v.unionMain.id;
            this.options11[i].label = v.unionMain.name;
            this.options21[i].value = v.unionMain.id;
            this.options21[i].label = v.unionMain.name;
          });
        } else {
          this.options11 = [];
          this.options21 = [];
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 本店
    this.init1();
    // 他店
    this.init2();
    // 是否有新的消费核销成功
    eventBus.$on('newTransaction', () => {
      this.init1();
      this.init2();
    });
  },
  // 获取商家列表
  watch: {
    // 联盟更改
    unionId1: function() {
      this.toMemberId1 = '';
      this.options12 = [];
      // 通过对应的unionId获取对应的memberId
      $http
        .get(`/unionMember/listMap`)
        .then(res => {
          if (res.data.data) {
            res.data.data.forEach((v, i) => {
              if (v.unionMain.id === this.unionId1) {
                this.memberId1 = v.unionMember.id;
                $http
                  .get(`/unionMember/listMap/memberId/${this.memberId1}`)
                  .then(res => {
                    if (res.data.data) {
                      this.options12 = res.data.data;
                      this.options12.forEach((v, i) => {
                        v.value = v.memberId;
                        v.label = v.enterpriseName;
                      });
                    } else {
                      this.options12 = [];
                    }
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              }
            });
          }
          this.search1();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    unionId2: function() {
      this.toMemberId2 = '';
      this.options22 = [];
      // 通过对应的unionId获取对应的memberId
      $http
        .get(`/unionMember/listMap`)
        .then(res => {
          if (res.data.data) {
            res.data.data.forEach((v, i) => {
              if (v.unionMain.id === this.unionId2) {
                this.memberId2 = v.unionMember.id;
                $http
                  .get(`/unionMember/listMap/memberId/${this.memberId2}`)
                  .then(res => {
                    if (res.data.data) {
                      this.options22 = res.data.data;
                      this.options22.forEach((v, i) => {
                        v.value = v.memberId;
                        v.label = v.enterpriseName;
                      });
                    } else {
                      this.options22 = [];
                    }
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              }
            });
          }
          this.search2();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 来源更改
    toMemberId1: function() {
      this.search1();
    },
    toMemberId2: function() {
      this.search2();
    },
    // 日期更改
    timeValue1: function() {
      this.search1();
    },
    timeValue2: function() {
      this.search2();
    }
  },
  methods: {
    // 本店消费记录加载
    init1() {
      $http
        .get(`/unionConsume/my?current=${this.currentPage1}`)
        .then(res => {
          if (res.data.data) {
            this.tableData1 = res.data.data.records;
            this.totalAll1 = res.data.data.total;
            this.tableData1.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData1 = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 他店消费记录加载
    init2() {
      $http
        .get(`/unionConsume/other?current=${this.currentPage2}`)
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records;
            this.totalAll2 = res.data.data.total;
            this.tableData2.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData2 = [];
            this.totalAll2 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 带条件搜索本店消费记录
    search1() {
      let beginTime, endTime;
      if (this.timeValue1[0]) {
        beginTime = $todate.todate(this.timeValue1[0]);
        endTime = $todate.todate(this.timeValue1[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionConsume/my?current=${this.currentPage1}&unionId=${this.unionId1}&memberId=${this.toMemberId1}&` +
            this.value1 +
            '=' +
            this.input1 +
            '&beginTime=' +
            beginTime +
            '&endTime=' +
            endTime
        )
        .then(res => {
          if (res.data.data) {
            this.tableData1 = res.data.data.records;
            this.totalAll1 = res.data.data.total;
            this.tableData1.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData1 = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 带条件搜索他店消费记录
    search2() {
      let beginTime, endTime;
      if (this.timeValue2[0]) {
        beginTime = $todate.todate(this.timeValue2[0]);
        endTime = $todate.todate(this.timeValue2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionConsume/other?current=${this.currentPage2}&unionId=${this.unionId2}&memberId=${this.toMemberId2}&` +
            this.value2 +
            '=' +
            this.input2 +
            '&beginTime=' +
            beginTime +
            '&endTime=' +
            endTime
        )
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records;
            this.totalAll2 = res.data.data.total;
            this.tableData2.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData2 = [];
            this.totalAll2 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页搜索本店消费记录
    handleCurrentChange1(val) {
      let beginTime, endTime;
      if (this.timeValue1[0]) {
        beginTime = $todate.todate(this.timeValue1[0]);
        endTime = $todate.todate(this.timeValue1[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionConsume/my?current=${val}&unionId=${this.unionId1}&memberId=${this.toMemberId1}&` +
            this.value1 +
            '=' +
            this.input1 +
            '&beginTime=' +
            beginTime +
            '&endTime=' +
            endTime
        )
        .then(res => {
          if (res.data.data) {
            this.tableData1 = res.data.data.records;
            this.totalAll1 = res.data.data.total;
            this.tableData1.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData1 = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页搜索他店消费记录
    handleCurrentChange2(val) {
      let beginTime, endTime;
      if (this.timeValue2[0]) {
        beginTime = $todate.todate(this.timeValue2[0]);
        endTime = $todate.todate(this.timeValue2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      $http
        .get(
          `/unionConsume/other?current=${val}&unionId=${this.unionId1}&memberId=${this.toMemberId1}&` +
            this.value1 +
            '=' +
            this.input1 +
            '&beginTime=' +
            beginTime +
            '&endTime=' +
            endTime
        )
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records;
            this.totalAll2 = res.data.data.total;
            this.tableData2.forEach((v, i) => {
              v.items_ = [];
              v.items.forEach((val, idx) => {
                v.items_.push(val.name);
                v.items_.join(',');
              });
              v.createtime = $todate.todate(new Date(v.createtime));
              switch (v.status) {
                case 1:
                  v.status = '未支付';
                  break;
                case 2:
                  v.status = '已支付';
                  break;
                case 3:
                  v.status = '已退款';
                  break;
              }
            });
          } else {
            this.tableData2 = [];
            this.totalAll2 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出本店消费记录
    output1() {
      let beginTime, endTime;
      if (this.timeValue1[0]) {
        beginTime = $todate.todate(this.timeValue1[0]);
        endTime = $todate.todate(this.timeValue1[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        this.$store.state.baseUrl +
        `/unionConsume/consumeFromDetail?unionId=${this.unionId1}&memberId=${this.toMemberId1}&` +
        this.value1 +
        '=' +
        this.input1 +
        `beginTime=${beginTime}&endTime=${endTime}`;
      window.open(url);
    },
    // 导出他店消费记录
    output2() {
      let beginTime, endTime;
      if (this.timeValue2[0]) {
        beginTime = $todate.todate(this.timeValue2[0]);
        endTime = $todate.todate(this.timeValue2[1]);
      } else {
        beginTime = '';
        endTime = '';
      }
      let url =
        this.$store.state.baseUrl +
        `/unionConsume/consumeToDetail?unionId=${this.unionId2}&memberId=${this.toMemberId2}&` +
        this.value2 +
        '=' +
        this.input2 +
        `beginTime=${beginTime}&endTime=${endTime}`;
      window.open(url);
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
/*消费审核记录样式表------------------------------------*/

#ExpenseRecord {
  .el-col-5 {
    width: 288px;
  }
  .third_ {
    .el-col-3 {
      margin-left: 20px;
    }
  }
  .el-col-3 {
    width: 13.5%;
  }
}
</style>
