<template>
  <div class="theLeader">
    <p class="union_set">请选择其中一位盟员，让其成为新的盟主</p>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="member.enterpriseName" label="盟员名称">
      </el-table-column>
      <el-table-column prop="member.createTime" label="加入时间">
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <div class="sizeAndColor">
            <span class="repeal repeal_">
              <el-button v-if="!scope.row.unionTransfer" size="small" @click="onTransfer(scope)" :disabled="!canTransferFlag">转移</el-button>
            </span>
            <span class="repeal">
              <el-button v-if="scope.row.unionTransfer" size="small" @click="onRevoke(scope)">撤销</el-button>
            </span>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <!-- 弹出框 确认转移 -->
    <el-dialog title="提示" :visible.sync="dialogVisible1" size="tiny">
      <hr>
      <span>将盟主权限转移给 {{ enterpriseName }}， 是否确认？</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onConfirm1">确 定</el-button>
        <el-button @click="dialogVisible1=false">取 消</el-button>
      </span>
    </el-dialog>
    <!-- 弹出框 确认撤销 -->
    <el-dialog title="提示" :visible.sync="dialogVisible2" size="tiny">
      <hr>
      <span>撤销将盟主权限转移给 {{ enterpriseName }}， 是否确认？</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onConfirm2">确 定</el-button>
        <el-button @click="dialogVisible2=false">取 消</el-button>
      </span>
    </el-dialog>
    <div class="footer">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'union-setting-transfer',
  data() {
    return {
      tableData: [],
      currentPage: 1,
      dialogVisible1: false,
      dialogVisible2: false,
      id: '',
      enterpriseName: '',
      totalAll: 0,
      canTransferFlag: true,
      transferId: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('unionSettingTabChange', () => {
      this.init();
    });
  },
  methods: {
    init() {
      this.currentPage = 1;
      this.getTableData();
    },
    getTableData() {
      $http.get(`/unionMainTransfer/unionId/${this.unionId}/page?current=${this.currentPage}`);
      then(res => {
        if (res.data.data) {
          this.tableData = res.data.data.records || [];
          // 判断canTransferFlag
          this.tableData.forEach((v, i) => {
            if (v.unionTransfer) {
              thisthis.canTransferFlag = false;
            }
            v.member.createTime = timeFilter(v.member.createTime);
          });
          this.totalAll = res.data.data.total;
        }
      }).catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    },
    // 分页查询
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 转移
    onTransfer(scope) {
      this.dialogVisible1 = true;
      this.id = scope.row.member.id;
      this.enterpriseName = scope.row.member.enterpriseName;
    },
    // 确认转移
    onConfirm1() {
      $http
        .post(`/unionMainTransfer/unionId/${this.unionId}toMemberId=${this.id}`)
        .then(res => {
          if (res.data.success) {
            eventBus.$emit('unionUpdata');
            this.dialogVisible1 = false;
            this.canTransferFlag = false;
            this.init();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 撤销
    onRevoke(scope) {
      this.dialogVisible2 = true;
      this.transferId = scope.row.unionTransfer.id;
      this.enterpriseName = scope.row.member.enterpriseName;
    },
    // 确认撤销
    onConfirm2() {
      $http
        .del(`/unionMainTransfer/${this.transferId}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.success) {
            eventBus.$emit('unionUpdata');
            this.dialogVisible2 = false;
            this.canTransferFlag = true;
            this.init();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>
