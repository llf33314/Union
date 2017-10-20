<template>
  <div>
    <h4>商机佣金比例设置</h4>
    <div>
      <el-form :inline="true" :model="formInline1" class="demo-form-inline">
        <el-form-item label="选择联盟：">
          <el-select v-model="value" placeholder="请选择">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="ratioSetting">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="toEnterpriseName" label="盟员名称">
        </el-table-column>
        <el-table-column prop="ratioFromMe" label="我给TA佣金">
        </el-table-column>
        <el-table-column prop="ratioToMe" label="TA给我佣金">
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template scope="scope">
            <div class="sizeAndColor">
              <el-button @click="setPercent(scope)">设置佣金比例</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 弹出框 商机佣金比例设置 -->
      <div class="model_03">
        <el-dialog title="商机佣金比例设置" :visible.sync="dialogVisible" size="tiny" @close="resetData">
          <div class="model_03_detail">
            <p>"{{ toEnterpriseName }}" 给我的佣金为：{{ ratioToMe }}</p>
            <p>我给TA佣金 ：&nbsp;&nbsp;<input type="text" v-model="ratioFromMe">
              <span> %</span>
            </p>
            <div style="width:340px;text-align:right;color: #999999">对方推荐商机成功后获得的佣金百分比</div>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submit">确 定</el-button>
            <el-button @click="dialogVisible=false">取 消</el-button>
          </span>
        </el-dialog>
      </div>
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10"
                     layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'percent',
  data() {
    return {
      dialogVisible: false,
      formInline1: {
        region: '',
        state: ''
      },
      value: 0,
      options: [],
      tableData: [],
      currentPage: 1,
      toEnterpriseName: '',
      ratioFromMe: '',
      ratioToMe: '',
      toMemberId: '',
      totalAll: 0,
    }
  },
  computed: {
    initUnionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    if (this.initUnionId) {
      // 获取联盟列表
      $http.get(`/unionMember/listMap`)
        .then(res => {
          if (res.data.data && res.data.data.length > 0) {
            this.options = res.data.data;
            res.data.data.forEach((v, i) => {
              this.options[i].value = v.unionMember.id;
              this.options[i].label = v.unionMain.name;
            });
            this.value = this.options[0].value;
            $http.get(`/unionOpportunityRatio/pageMap/memberId/${this.value}?current=1`)
              .then(res => {
                if (res.data.data) {
                  this.tableData = res.data.data.records;
                  this.totalAll = res.data.data.total;
                  this.tableData.forEach((v, i) => {
                    v.ratioFromMe = (v.ratioFromMe || 0) + '%';
                    v.ratioToMe = (v.ratioToMe || 0) + '%';
                  });
                } else {
                  this.tableData = [];
                  this.totalAll = 0;
                };
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
          } else {
            this.options = [];
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    };
  },
  watch: {
    value: function() {
      $http.get(`/unionOpportunityRatio/pageMap/memberId/${this.value}?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.ratioFromMe = (v.ratioFromMe || 0) + '%';
              v.ratioToMe = (v.ratioToMe || 0) + '%';
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    }
  },
  methods: {
    // 设置比例
    setPercent(scope) {
      this.dialogVisible = true;
      this.toEnterpriseName = scope.row.toEnterpriseName;
      this.ratioToMe = scope.row.ratioToMe;
      this.ratioFromMe = scope.row.ratioFromMe.slice(0, -1);
      this.toMemberId = scope.row.toMemberId;
    },
    // 保存设置
    submit() {
      $http.put(`/unionOpportunityRatio/memberId/${this.value}?toMemberId=${this.toMemberId}&ratio=${this.ratioFromMe}`)
        .then(res => {
            if(res.data.success){
              $http.get(`/unionOpportunityRatio/pageMap/memberId/${this.value}?current=${this.currentPage}`)
                .then(res => {
                  if (res.data.data) {
                    this.tableData = res.data.data.records;
                    this.totalAll = res.data.data.total;
                    this.tableData.forEach((v, i) => {
                      v.ratioFromMe = (v.ratioFromMe || 0) + '%';
                      v.ratioToMe = (v.ratioToMe || 0) + '%';
                    });
                  } else {
                    this.tableData = [];
                    this.totalAll = 0;
                  };
                })
                .then(res => {
                  this.dialogVisible = false;
                })
                .catch(err => {
                  this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                })
            }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 分页查询
    handleCurrentChange(val) {
      $http.get(`/unionOpportunityRatio/pageMap/memberId/${this.value}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.ratioFromMe = (v.ratioFromMe || 0) + '%';
              v.ratioToMe = (v.ratioToMe || 0) + '%';
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 关闭弹窗重置数据
    resetData() {
      this.ratioFromMe = '';
    },
  }
}
</script>

<style lang='less' rel="stylesheet/less">
.model_03 {
  .el-dialog__body {
    padding: 0;
    margin: 30px 0 30px 30px;
    .el-dialog--tiny {
      width: 45%;
    }
    p {
      margin-bottom: 20px;
      input {
        width: 220px;
        height: 32px;
        border-radius: 5px;
      }
    }
  }
}
</style>

