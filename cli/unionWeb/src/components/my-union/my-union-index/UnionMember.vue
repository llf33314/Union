<template>
  <div id="memberName">
    <el-row class="user_search">
      <el-col style="width: 220px">
        <div class="grid-content bg-purple">
          <el-input placeholder="请输入盟员名称" icon="search" v-model="input" :on-icon-click="search" class="input-search" @keyup.enter.native="search">
          </el-input>
        </div>
      </el-col>
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;padding-left: 20px">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <!-- 盟员列表 表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="盟员名称">
        <template slot-scope="scope">
          <i class="iconfont" v-if="scope.row.isUnionOwner" style="font-size: 25px;color:#FDD43F;position: absolute;top: 4px;left: 9px;">&#xe504;</i>
          <span>{{ scope.row.enterpriseName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="directorName" label="负责人">
      </el-table-column>
      <el-table-column prop="directorPhone" label="联系电话">
      </el-table-column>
      <el-table-column prop="createTime" label="加入时间">
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <div class="sizeAndColor">
            <el-button size="small" @click="detail(scope)">详情</el-button>
            <el-button size="small" v-if="isUnionOwner && !scope.row.isUnionOwner" @click="remove(scope)">移出</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 详情 -->
    <div class="model__10">
      <el-dialog title="详情" :visible.sync="dialogVisible0" size="tiny">
        <hr>
        <div>
          <el-form ref="form" :model="form" label-width="80px">
            <el-form-item label="盟员名称：">
              {{ form.enterpriseName }}
            </el-form-item>
            <el-form-item label="加入时间：">
              {{ form.createTime }}
            </el-form-item>
            <el-form-item label="负责人：">
              {{ form.directorName }}
            </el-form-item>
            <el-form-item label="联系电话：">
              {{ form.directorPhone }}
            </el-form-item>
            <el-form-item label="邮箱：">
              {{ form.directorEmail }}
            </el-form-item>
            <el-form-item label="地址：">
              {{ form.enterpriseAddress }}
            </el-form-item>
          </el-form>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 移出确认 -->
    <el-dialog title="" :visible.sync="visible" size="tiny">
      <div class="model_12">
        <p>是否确认移出“ {{ enterpriseName }} ”</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm">确定</el-button>
        <el-button @click="visible = false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'union-member',
  data() {
    return {
      input: '',
      tableData: [],
      currentPage: 1,
      dialogVisible0: false,
      dialogVisible1: false,
      dialogVisible2: false,
      form: {
        busAddress: '',
        createTime: '',
        directorEmail: '',
        directorName: '',
        directorPhone: '',
        enterpriseName: ''
      },
      current: 1,
      tgtMemberId: '',
      totalAll: 0,
      visible: false,
      enterpriseName: '',
      memberId: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  mounted: function() {
    if (this.unionId) {
      this.init();
    }
  },
  activated: function() {
    if (this.unionId) {
      this.init();
    }
  },
  watch: {
    // 查询盟员列表
    unionId: function() {
      if (this.unionId) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      $http
        .get(`/unionMember/unionId/${this.unionId}/write/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.createTime = timeFilter(v.createTime);
            });
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 带条件查询盟员列表
    search() {
      $http
        .get(`/unionMember/unionId/${this.unionId}/write/page?current=1&memberName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.createTime = timeFilter(v.createTime);
            });
            this.totalAll = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页查询盟员列表
    handleCurrentChange(val) {
      this.current = val;
      $http
        .get(`/unionMember/unionId/${this.unionId}/write/page?current=${val}&memberName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.createTime = timeFilter(v.createTime);
            });
            this.totalAll = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 详情
    detail(scope) {
      $http
        .get(`/unionMember/${scope.row.id}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.form = res.data.data;
            this.form.createTime = timeFilter(this.form.createTime);
            this.dialogVisible0 = true;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 移出
    remove(scope) {
      this.enterpriseName = scope.row.enterpriseName;
      this.memberId = scope.row.id;
      this.visible = true;
    },
    // 确认移出
    confirm() {
      this.visible = false;
      $http
        .post(`/unionMemberOut/unionId/${this.unionId}/applyMemberId/${this.memberId}`)
        .then(res => {
          this.$message({ showClose: true, message: '移出成功，该盟员将在15天过渡期后正式退盟', type: 'success', duration: 5000 });
          this.search();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出盟员列表
    output() {
      let url = this.$store.state.baseUrl + `/unionMember/unionId/${this.unionId}/write/export`;
      window.open(url);
    }
  }
};
</script>


<style  lang='less' rel="stylesheet/less">
/*.el-table_1_column_6{*/

.model__10 {
  .el-dialog__body {
    padding: 0;
    > div {
      margin: 20px 40px 50px;
      > span {
        float: right;
        margin-bottom: 20px;
      }
    }
  }
  .el-dialog--tiny {
    width: 550px;
  }
  .el-form-item__label {
    width: 110px !important;
  }
}
</style>
