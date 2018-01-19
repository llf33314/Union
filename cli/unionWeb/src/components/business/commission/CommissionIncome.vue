<template>
  <!-- 我的佣金收入 -->
  <div>
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
      <el-col style="width:289px;">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="来源:">
            <el-select v-model="toMemberId" clearable placeholder="请选择来源" @change="search">
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
    <!-- 我的佣金收入表格 -->
    <el-table :data="tableData" style="width: 100%">
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
      <el-table-column prop="toMember.enterpriseName" label="消费来源(盟员)" min-width="135px">
      </el-table-column>
      <el-table-column prop="union.name" label="所属联盟" min-width="100px">
      </el-table-column>
      <el-table-column prop="opportunity.acceptPrice" label="消费金额(元)" min-width="125">
      </el-table-column>
      <el-table-column prop="opportunity.brokerageMoney" label="我的佣金(元)" min-width="120">
      </el-table-column>
      <el-table-column prop="opportunity.acceptStatus" label="交易类型" min-width="100px">
      </el-table-column>
      <el-table-column prop="isClose" label="佣金结算状态" min-width="140px" :filters="[{ text: '未支付', value: '未支付' }, { text: '已支付', value: '已支付' }]" :filter-method="filterTag" filter-placement="bottom-end">
        <template slot-scope="scope">
          <el-tag :type="scope.row.opportunity.isClose === '未支付' ? 'danger' : 'success'">{{scope.row.opportunity.isClose}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="opportunity.createTime" label="交易时间" min-width="150">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>交易时间: {{ scope.row.opportunity.createTime }}</p>
            <div slot="reference" class="name-wrapper">
              {{ scope.row.opportunity.createTime }}
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
      toMemberId: '',
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
      totalAll: 0
    };
  },
  watch: {
    // 切换联盟，改变来源列表
    unionId: function() {
      this.toMemberId = '';
      this.options2 = [];
      if (this.unionId) {
        $http
          .get(`/unionOpportunity/unionId/${this.unionId}/toMember`)
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
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      }
    }
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
      this.toMemberId = '';
      this.value = 'clientName';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(
          `/unionBrokerageIncome/opportunity/page?current=${this.currentPage}&unionId=${this.unionId}&toMemberId=${this
            .toMemberId}&` +
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
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
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
    filterTag(value, row) {
      return row.opportunity.isClose === value;
    }
  }
};
</script>
