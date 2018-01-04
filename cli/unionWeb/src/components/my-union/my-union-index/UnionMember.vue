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
          <i class="iconfont" v-if="scope.row.member.isUnionOwner" style="font-size: 25px;color:#FDD43F;position: absolute;top: 4px;left: 9px;">&#xe504;</i>
          <span>{{ scope.row.member.enterpriseName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="member.directorName" label="负责人">
      </el-table-column>
      <el-table-column prop="member.directorPhone" label="联系电话">
      </el-table-column>
      <el-table-column prop="member.createTime" label="加入时间">
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template slot-scope="scope">
          <div class="sizeAndColor">
            <el-button size="small" @click="detail(scope)">详情</el-button>
            <el-button size="small" v-if="(isUnionOwner && !scope.row.member.isUnionOwner) && !scope.row.memberOut.type" @click="remove(scope)">移出</el-button>
            <el-button size="small" v-if="isUnionOwner && scope.row.memberOut.type === 2" @click="withdraw(scope)">撤回移出</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 详情 -->
    <div class="model__10">
      <el-dialog title="详情" :visible.sync="visible" size="tiny">
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
    <!-- 弹出框 确认移出 -->
    <div class="model_13">
      <el-dialog title="" :visible.sync="visible1" size="tiny">
          <p>是否确认移出“ {{ enterpriseName }} ”</p>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm1">确定</el-button>
          <el-button @click="visible1=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 确认撤回移出 -->
    <el-dialog title="" :visible.sync="visible2" size="tiny">
      <div class="model_12">
        <p>是否确认撤回移出“ {{ enterpriseName }} ”</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm2">确定</el-button>
        <el-button @click="visible2=false">取消</el-button>
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
      visible1: false,
      visible2: false,
      enterpriseName: '',
      memberId: '',
      outId: ''
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
      this.currentPage = 1;
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(`/unionMember/unionId/${this.unionId}/index/page?current=${this.currentPage}&memberName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.member.createTime = timeFilter(v.member.createTime);
              if (!v.memberOut) {
                v.memberOut = {};
                v.memberOut.type = '';
              }
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
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页查询盟员列表
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 详情
    detail(scope) {
      $http
        .get(`/unionMember/${scope.row.member.id}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.form = res.data.data;
            this.form.createTime = timeFilter(this.form.createTime);
            this.visible = true;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 移出
    remove(scope) {
      this.enterpriseName = scope.row.member.enterpriseName;
      this.memberId = scope.row.member.id;
      this.visible1 = true;
    },
    // 确认移出
    confirm1() {
      $http
        .post(`/unionMemberOut/unionId/${this.unionId}/applyMemberId/${this.memberId}`)
        .then(res => {
          if (res.data.success) {
            this.visible1 = false;
            this.$message({ showClose: true, message: '移出成功，该盟员将在15天过渡期后正式退盟', type: 'success', duration: 5000 });
            this.search();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 撤回移出
    withdraw(scope) {
      this.enterpriseName = scope.row.member.enterpriseName;
      this.outId = scope.row.memberOut.id;
      this.visible2 = true;
    },
    // 确认撤回移出
    confirm2() {
      $http
        .del(`/unionMemberOut/${this.outId}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.success) {
            this.visible2 = false;
            this.$message({ showClose: true, message: '撤回移出成功', type: 'success', duration: 5000 });
            this.search();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出盟员列表
    output() {
      let url =
        this.$store.state.baseUrl + `/unionMember/unionId/${this.unionId}/index/export?memberName=${this.input}`;
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
    margin-top: 15px;
    > div {
      margin: 20px 40px 50px;
      > span {
        float: right;
        margin-bottom: 20px;
      }
    }
    .el-form-item {
      margin-bottom: 17px !important;
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
