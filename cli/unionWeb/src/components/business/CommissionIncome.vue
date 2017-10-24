<template>
  <!-- 我的佣金收入 -->
  <div>
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
          <el-form-item label="来源:">
            <el-select v-model="toMemberId" clearable placeholder="请选择来源">
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
          <el-input placeholder="请输入关键字"  @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我的佣金收入表格 -->
    <el-table :data="tableData" style="width: 100%">
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
      <el-table-column prop="toEnterpriseName" label="消费来源(盟员)" min-width="135px">
      </el-table-column>
      <el-table-column prop="unionName" label="所属联盟" min-width="100px">
      </el-table-column>
      <el-table-column prop="acceptPrice" label="消费金额(元)" min-width="125">
      </el-table-column>
      <el-table-column prop="brokeragePrice" label="我的佣金(元)" min-width="120">
      </el-table-column>
      <el-table-column prop="opportunityType" label="交易类型" min-width="100px">
      </el-table-column>
      <el-table-column prop="isClose" label="佣金结算状态" min-width="140px" :filters="[{ text: '未结算', value: '未结算' }, { text: '已结算', value: '已结算' }]"
                       :filter-method="filterTag" filter-placement="bottom-end">
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
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
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
      toMemberId: '',
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
      totalAll: 0
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
      this.toMemberId = '';
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
          this.$message({
            showClose: true,
            message: err.toString(),
            type: 'error',
            duration: 5000
          });
        });
    },
    toMemberId: function() {
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
            this.$message({
              showClose: true,
              message: err.toString(),
              type: 'error',
              duration: 5000
            });
          });
        $http
          .get(`/unionOpportunity/income?current=1`)
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
            this.$message({
              showClose: true,
              message: err.toString(),
              type: 'error',
              duration: 5000
            });
          });
      }
    },
    // 带条件搜索
    search() {
      $http
        .get(
          `/unionOpportunity/income?current=1&unionId=${this.unionId}&toMemberId=${this.toMemberId}&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
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
    // 分页搜索
    handleCurrentChange(val) {
      $http
        .get(
          `/unionOpportunity/income?current=${val}&unionId=${this.unionId}&toMemberId=${this.toMemberId}&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
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
    filterTag(value, row) {
      return row.isClose === value;
    }
  }
};
</script>
