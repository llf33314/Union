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
          <template scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>企业：{{ scope.row.joinEnterpriseName }}</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.joinEnterpriseName }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="joinDirectorName" label="负责人">
        </el-table-column>
        <el-table-column prop="joinDirectorPhone" label="联系电话">
          <template scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>电话：{{ scope.row.joinDirectorPhone }}</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.joinDirectorPhone }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="joinDirectorEmail" label="邮箱">
          <template scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>邮箱：{{ scope.row.joinDirectorEmail }}</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.joinDirectorEmail }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="joinReason" label="申请/推荐理由">
        </el-table-column>
        <el-table-column prop="joinTime" label="申请/推荐时间">
          <template scope="scope">
            <el-popover trigger="hover" placement="bottom">
              <p>时间：{{ scope.row.joinTime }}</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.joinTime }}</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="" label="推荐人">
        </el-table-column>
        <el-table-column prop="" label="操作" width="160">
          <template scope="scope">
            <div class="sizeAndColor">
              <el-button size="small" @click="handlePass(scope)">通过</el-button>
              <el-button size="small" @click="handleFail(scope)">不通过</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 弹出框 确认通过 -->
    <div class="model_02">
      <el-dialog title="是否确认通过申请" :visible.sync="visible1" size="tiny">
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm1">确定</el-button>
          <el-button @click="visible1=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 确认不通过 -->
    <div class="model_02">
      <el-dialog title="是否确认不通过申请" :visible.sync="visible2" size="tiny">
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm2">确定</el-button>
          <el-button @click="visible2=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <div class="footer">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import $http from '@/utils/http.js';

export default {
  name: 'union-check',
  components: {
    Breadcrumb
  },
  data() {
    return {
      value: '',
      options: [
        {
          value: 'joinEnterpriseName',
          label: '申请企业'
        },
        {
          value: 'joinDirectorPhone',
          label: '联系电话'
        }
      ],
      input: '',
      currentPage: 1,
      tableData: [],
      totalAll: 0,
      joinId: '',
      visible1: false,
      visible2: false
    };
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      if (this.unionMemberId) {
        $http
          .get(`/unionMemberJoin/memberId/${this.unionMemberId}?current=1`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records;
              this.totalAll = res.data.data.total;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
      }
    },
    // 带条件查询
    search() {
      $http
        .get(`/unionMemberJoin/memberId/${this.unionMemberId}?current=1&` + this.value + '=' + this.input)
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
      $http
        .get(`/unionMemberJoin/memberId/${this.unionMemberId}?&current=${val}&` + this.value + '=' + this.input)
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
    // 通过
    handlePass(scope) {
      this.visible1 = true;
      this.joinId = scope.row.joinId;
    },
    // 确认通过
    confirm1() {
      let url = `/unionMemberJoin/${this.joinId}/memberId/${this.unionMemberId}?isOK=1`;
      $http
        .put(url)
        .then(res => {
          if (res.data.success) {
            this.search();
            this.visible1 = false;
            this.$message({ showClose: true, message: '审核通过', type: 'success', duration: 5000 });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 不通过
    handleFail(scope) {
      this.visible2 = true;
      this.joinId = scope.row.joinId;
    },
    // 确认不通过
    confirm2() {
      let url = `/unionMemberJoin/${this.joinId}/memberId/${this.unionMemberId}?isOK=0`;
      $http
        .put(url)
        .then(res => {
          if (res.data.success) {
            this.search();
            this.visible1 = false;
            this.$message({ showClose: true, message: '审核通过', type: 'success', duration: 5000 });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
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
