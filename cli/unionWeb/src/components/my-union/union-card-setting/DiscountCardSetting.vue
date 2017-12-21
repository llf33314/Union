<template>
  <div id="DiscountCardSetting">
    <el-row class="user_search">
      <el-col style="width: 240px;">
        <div class="grid-content bg-purple">
          <el-input placeholder="请输入盟员名称" icon="search" v-model="input" :on-icon-click="search" class="input-search" @keyup.enter.native="search">
          </el-input>
        </div>
      </el-col>
      <el-col style="float: right;width: 90px;">
        <el-button @click="visible=true" type="primary">折扣设置</el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="盟员名称">
        <template slot-scope="scope">
          <i class="iconfont" v-if="scope.row.isUnionOwner" style="font-size: 25px;color:#FDD43F;position: absolute;top: 4px;left: 9px;">&#xe504;</i>
          <span>{{ scope.row.enterpriseName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="discount" label="粉丝享受折扣">
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 设置折扣 -->
    <el-dialog title="设置折扣" :visible.sync="visible" size="tiny" @close="resetData">
      <hr>
      <div>当前折扣： {{ discount }} </div>
      <p>设置折扣：
        <el-input v-model="discountInput"
                  placeholder="请输入0~10的折扣"
                  @keyup.native="check" style="width:200px"></el-input>
      </p>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="discountConfirm">确定</el-button>
        <el-button @click="visible=false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { numberCheck } from '@/utils/filter.js';
export default {
  name: 'DiscountCardSetting',
  data() {
    return {
      input: '',
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      visible: false,
      memberId: '',
      discount: '',
      discountInput: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      $http
        .get(`/unionMember/unionId/${this.unionId}/write/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
      $http
        .get(`/unionMember/unionId/${this.unionId}/busUser`)
        .then(res => {
          if (res.data.data) {
            this.memberId = res.data.data.id;
            this.discount = res.data.data.discount;
          } else {
            this.memberId = '';
            this.discount = '';
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
            this.tableData = res.data.data.records;
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
    // 分页查询盟员列表
    handleCurrentChange(val) {
      $http
        .get(`/unionMember/unionId/${this.unionId}/write/page?current=${val}&memberName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
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
    // 校验折扣输入为数字类型
    check() {
      this.discountInput = numberCheck(this.discountInput);
    },
    // 确认折扣
    discountConfirm() {
      this.discountInput = Number(this.discountInput).toFixed(2);
      if (this.discountInput > 10) {
        this.$message({ showClose: true, message: '最大折扣为10，请重新输入', type: 'error', duration: 5000 });
      } else if (this.discountInput < 0) {
        this.$message({ showClose: true, message: '最小折扣为0，请重新输入', type: 'error', duration: 5000 });
      } else {
        let url = `/unionMember/${this.memberId}/unionId/${this.unionId}/discount`;
        let data = this.discountInput;
        $http
          .put(url, data)
          .then(res => {
            if (res.data.success) {
              this.visible = false;
              this.init();
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 关闭弹窗重置数据
    resetData() {
      this.discountInput = '';
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
