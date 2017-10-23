<template>
  <div id="apply_for">
    <Breadcrumb :header-name="'入盟审核'"></Breadcrumb>
    <div class="apply_for">
      <span class="apply_table">入盟申请表</span>
      <el-col style="width:125px">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col style="width:245px">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" icon="search" v-model="input" :on-icon-click="search"
                    @keyup.enter.native="search">
          </el-input>
        </div>
      </el-col>
    </div>
    <div class="apply_select">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="joinEnterpriseName" label="申请企业">
        </el-table-column>
        <el-table-column prop="joinDirectorName" label="负责人">
        </el-table-column>
        <el-table-column prop="joinDirectorPhone" label="联系电话">
        </el-table-column>
        <el-table-column prop="joinDirectorEmail" label="邮箱">
        </el-table-column>
        <el-table-column prop="joinReason" label="申请/推荐理由">
        </el-table-column>
        <el-table-column prop="joinTime" label="申请/推荐时间">
        </el-table-column>
        <el-table-column prop="" label="推荐人">
        </el-table-column>
        <el-table-column prop="" label="操作" width="160">
          <template scope="scope">
            <div class="sizeAndColor">
              <el-button size="small" type="danger" @click="handlePass(scope.$index, scope.row)">通过</el-button>
              <el-button size="small" @click="handleFail(scope.$index, scope.row)">不通过</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="footer">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage"
                     :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb'
import $http from '@/utils/http.js'

export default {
  name: 'union-check',
  components: {
    Breadcrumb
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    $http.get(`/unionMemberJoin/memberId/${this.unionMemberId}?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData = res.data.data.records;
          this.totalAll = res.data.data.total;
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
      });
  },
  data() {
    return {
      value: '',
      options: [{
        value: 'joinEnterpriseName',
        label: '申请企业'
      }, {
        value: 'joinDirectorPhone',
        label: '联系电话'
      }],
      input: '',
      currentPage: 1,
      tableData: [],
      totalAll: 0,
    }
  },
  methods: {
    // 带条件查询
    search() {
      $http.get(`/unionMemberJoin/memberId/${this.unionMemberId}?current=1&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页查询
    handleCurrentChange(val) {
      $http.get(`/unionMemberJoin/memberId/${this.unionMemberId}?&current=${val}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handlePass(index, row) {
      let url = `/unionMemberJoin/${row.joinId}/memberId/${this.unionMemberId}?isOK=1`;
      $http.put(url)
        .then(res => {
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleFail(index, row) {
      let url = `/unionMemberJoin/${row.joinId}/memberId/${this.unionMemberId}?isOK=0`;
      $http.put(url)
        .then(res => {
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
  }
}
</script>
<style lang='less' rel="stylesheet/less" scoped>
.apply_for {
  padding: 10px 75px 0 55px;
  .apply_table {
    display: block;
    margin: 40px 0 30px 0;
    font-size: 20px;
    font-weight: bold;
  }
}

.apply_select {
  padding: 35px 75px 0 55px;
  margin-top: 40px;
  margin-bottom: 20px;
}

.footer {
  padding-right: 75px;
}
</style>
