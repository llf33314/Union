<template>
  <div class="theLeader">
    <p class="union_set">请选择其中一位盟员，让其成为新的盟主</p>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="enterpriseName" label="盟员名称">
      </el-table-column>
      <el-table-column prop="createTime" label="加入时间">
      </el-table-column>
      <el-table-column label="操作">
        <template scope="scope">
          <div class="sizeAndColor">
            <el-button size="small" @click="onTransfer(scope)">转移</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="提示" :visible.sync="dialogVisible" size="tiny">
      <span>是否将盟主权限转移给 {{ enterpriseName }}， 是否确认？</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onConfirm">确 定</el-button>
        <el-button @click="dialogVisible=false">取 消</el-button>
      </span>
    </el-dialog>
    <div class="footer">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
import $todate from '@/utils/todate.js'
export default {
  name: 'union-setting-transfer',
  data() {
    return {
      tableData: [],
      currentPage: 1,
      dialogVisible: false,
      id: '',
      enterpriseName: '',
      totalAll: 0,
    }
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    $http.get(`/unionMainTransfer/pageMap/memberId/${this.unionMemberId}?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData = res.data.data.records;
          this.totalAll = res.data.data.total;
          this.tableData.forEach((v, i) => {
            v.createTime = $todate.todate(new Date(v.createTime));
          });
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      })
  },
  methods: {
    // 分页查询
    handleCurrentChange(val) {
      $http.get(`/unionMainTransfer/pageMap/memberId/${this.unionMemberId}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.createTime = $todate.todate(new Date(v.createTime));
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 转移
    onTransfer(scope) {
      this.dialogVisible = true;
      this.id = scope.row.memberId;
      this.enterpriseName = scope.row.enterpriseName;
    },
    // 确认
    onConfirm() {
      $http.put(`/unionMainTransfer/memberId/${this.unionMemberId}?tgtMemberId=${this.id}`)
        .then(res => {
          this.dialogVisible = false;
          $http.get(`/unionMainTransfer/pageMap/memberId/${this.unionMemberId}?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData = res.data.data.records;
                this.totalAll = res.data.data.total;
                this.tableData.forEach((v, i) => {
                  v.createTime = $todate.todate(new Date(v.createTime));
                });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            })
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    }
  }
}
</script>
