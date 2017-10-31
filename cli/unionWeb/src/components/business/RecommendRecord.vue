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
          <el-input placeholder="请输入关键字" icon="search" v-model="input" @keyup.enter.native="search" :on-icon-click="search" class="input-search2 fl">
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我要推荐表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="clientName" label="顾客姓名">
      </el-table-column>
      <el-table-column prop="clientPhone" label="电话">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>{{ scope.row.clientPhone }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.clientPhone }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="businessMsg" label="业务备注">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>{{ scope.row.businessMsg }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.businessMsg }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="toEnterpriseName" label="推荐商家">
      </el-table-column>
      <el-table-column prop="unionName" label="所属联盟">
      </el-table-column>
      <el-table-column prop="isAccept" label="状态" :filters="[{ text: '未处理', value: '未处理' }, { text: '已完成', value: '已完成' }, { text: '已拒绝', value: '已拒绝' }]" :filter-method="filterTag" filter-placement="bottom-end" width="150">
        <template scope="scope">
          <el-tag :type="scope.row.isAccept === '未处理' ? 'warning' : (scope.row.isAccept === '已完成' ? 'success' : 'danger')">{{scope.row.isAccept}}</el-tag>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>
<script>
import $http from '@/utils/http.js';
export default {
  name: 'recommend-record',
  data() {
    return {
      unionId: '',
      options1: [],
      value: '',
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
    // 监听是否有新的推荐
    eventBus.$on('newBusinessRecommend', () => {
      $http
        .get(`/unionOpportunity/fromMe?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.isAccept) {
                case 1:
                  v.isAccept = '未处理';
                  break;
                case 2:
                  v.isAccept = '已完成';
                  break;
                case 3:
                  v.isAccept = '已拒绝';
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
    });
  },
  methods: {
    init() {
      if (this.initUnionId) {
        // 我创建及加入的所有联盟
        $http
          .get(`/unionMain/list/myUnion`)
          .then(res => {
            if (res.data.data && res.data.data.length > 0) {
              this.options1 = res.data.data;
              this.options1.forEach((v, i) => {
                v.value = v.id;
                v.label = v.name;
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
        // 我推荐的商机数据
        $http
          .get(`/unionOpportunity/fromMe?current=1`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records;
              this.totalAll = res.data.data.total;
              this.tableData.forEach((v, i) => {
                switch (v.isAccept) {
                  case 1:
                    v.isAccept = '未处理';
                    break;
                  case 2:
                    v.isAccept = '已完成';
                    break;
                  case 3:
                    v.isAccept = '已拒绝';
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
        .get(`/unionOpportunity/fromMe?current=1&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.isAccept) {
                case 1:
                  v.isAccept = '未处理';
                  break;
                case 2:
                  v.isAccept = '已完成';
                  break;
                case 3:
                  v.isAccept = '已拒绝';
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
    },
    // 分页搜索
    handleCurrentChange(val) {
      $http
        .get(`/unionOpportunity/fromMe?current=${val}&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.isAccept) {
                case 1:
                  v.isAccept = '未处理';
                  break;
                case 2:
                  v.isAccept = '已完成';
                  break;
                case 3:
                  v.isAccept = '已拒绝';
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
    },
    filterTag(value, row) {
      return row.isAccept === value;
    }
  }
};
</script>
