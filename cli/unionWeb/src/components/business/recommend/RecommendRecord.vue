<template>
  <!-- 推荐记录 -->
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
      <el-col style="width:100px;">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options2" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col style="width:200px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" v-model="input" @keyup.enter.native="search" clearable class="input-search2 fl">
            <i slot="suffix" @click="search" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我要推荐表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="opportunity.clientName" label="顾客姓名">
      </el-table-column>
      <el-table-column prop="opportunity.clientPhone" label="电话">
      </el-table-column>
      <el-table-column prop="opportunity.businessMsg" label="业务备注">
        <template slot-scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>业务备注：{{ scope.row.opportunity.businessMsg }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.opportunity.businessMsg }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="toMember.enterpriseName" label="推荐商家">
      </el-table-column>
      <el-table-column prop="union.name" label="所属联盟">
      </el-table-column>
      <el-table-column prop="opportunity.acceptStatus" label="状态" :filters="[{ text: '未处理', value: '未处理' }, { text: '已完成', value: '已完成' }, { text: '已拒绝', value: '已拒绝' }]" :filter-method="filterTag" filter-placement="bottom-end" width="150">
        <template slot-scope="scope">
          <el-tag :type="scope.row.opportunity.acceptStatus === '未处理' ? 'warning' : (scope.row.opportunity.acceptStatus === '已完成' ? 'success' : 'danger')">{{scope.row.opportunity.acceptStatus}}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>
<script>
import $http from '@/utils/http.js';
import { bussinessStatusChange } from '@/utils/filter.js';
export default {
  name: 'recommend-record',
  data() {
    return {
      unionId: '',
      options1: [],
      value: 'clientName',
      options2: [
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
  mounted: function() {
    this.init();
    // 监听是否有新的推荐
    eventBus.$on('newBusinessRecommend', () => {
      this.search();
    });
  },
  methods: {
    init() {
      // 获取我的当前有效的联盟
      $http
        .get(`/unionMain/busUser/valid`)
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
      // 我推荐的商机数据
      this.currentPage = 1;
      this.unionId = '';
      this.value = 'clientName';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(
          `/unionOpportunity/fromMe/page?current=${this.currentPage}&unionId=${this.unionId}&` +
            this.value +
            '=' +
            this.input
        )
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.opportunity.acceptStatus = bussinessStatusChange(v.opportunity.acceptStatus);
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
    filterTag(value, row) {
      return row.opportunity.acceptStatus === value;
    }
  }
};
</script>
