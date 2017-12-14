<template>
  <div id="myBusiness">
    <h4> 商机消息 </h4>
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
          <el-input placeholder="请输入关键字" @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
          </el-input>
        </div>
      </el-col>
    </el-row>
    <!-- 我的商机表格 -->
    <div class="businesTable">
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
        <el-table-column prop="fromMember.enterpriseName" label="商机来源">
        </el-table-column>
        <el-table-column prop="union.name" label="所属联盟">
        </el-table-column>
        <el-table-column prop="opportunity.acceptStatus" label="状态" :filters="[{ text: '未处理', value: '未处理' }, { text: '已完成', value: '已完成' }, { text: '已拒绝', value: '已拒绝' }]" :filter-method="filterTag" filter-placement="bottom-end" width="150">
          <template slot-scope="scope">
            <el-tag :type="scope.row.opportunity.acceptStatus === '未处理' ? 'warning' : (scope.row.opportunity.acceptStatus === '已完成' ? 'success' : 'danger')">{{scope.row.opportunity.acceptStatus}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="" label="操作" min-width="120" ref="test">
          <template slot-scope="scope">
            <div class="sizeAndColor">
              <el-button @click="showDialog(scope)" size="small">详情</el-button>
              <el-button v-if="scope.row.isAccept === '未处理'" @click="agree(scope)" size="small">接受</el-button>
              <el-button v-if="scope.row.isAccept === '未处理'" @click="disagree(scope)" size="small">拒绝</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 弹出框 商机推荐详情 -->
    <div class="model_01">
      <el-dialog title="商机推荐详情" :visible.sync="dialogVisible" size="tiny" @click="dialogVisible = false">
        <hr>
        <div class="model_detail">
          <p>顾客详情：{{ detailData.opportunity.clientName }}</p>
          <p>电话：{{ detailData.opportunity.clientPhone }}</p>
          <p>商机来源：{{ detailData.fromMember.enterpriseName }}</p>
          <p>所属联盟：{{ detailData.union.name }}</p>
          <p>状态：{{ detailData.opportunity.acceptStatus }}</p>
          <p>业务备注：{{ detailData.opportunity.businessMsg }}</p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 商机受理价格 -->
    <div class="model_01">
      <el-dialog title="商机受理价格" :visible.sync="dialogVisible1" size="tiny" @close="resetData">
        <hr>
        <div class="model_detail">
          <span>商机受理价格</span>
          <el-row style="width:250px;margin-top:10px;">
            <el-input v-model="acceptancePrice" placeholder="请输入受理价格"></el-input>
          </el-row>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm">确定</el-button>
          <el-button @click="dialogVisible1=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 确认拒绝 -->
    <div class="model_02">
      <el-dialog title="是否确认拒绝商机" :visible.sync="dialogVisible2" size="tiny">
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm1">确定</el-button>
          <el-button @click="dialogVisible2 = false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { bussinessStatusChange } from '@/utils/filter.js';
export default {
  name: 'mybusiness',
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
      dialogVisible: false,
      dialogVisible1: false,
      dialogVisible2: false,
      detailData: {
        opportunity: {
          clientName: '',
          clientPhone: '',
          businessMsg: '',
          unionName: '',
          acceptStatus: '',
          businessMsg: ''
        },
        fromMember: {
          enterpriseName: ''
        },
        union: {
          name: ''
        }
      },
      acceptancePrice: '',
      opportunityId: '',
      //请求到的所有表格列表总数目;
      totalAll: 0,
      putUnionId: ''
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
  },
  methods: {
    init() {
      if (this.initUnionId) {
        // 我创建及加入的所有联盟
        $http
          .get(`/unionMain/my`)
          .then(res => {
            if (res.data.data) {
              this.options1 = res.data.data;
              this.options1.forEach((v, i) => {
                v.value = v.union.id;
                v.label = v.union.name;
              });
            } else {
              this.options1 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
        // 推荐给我的商机
        $http
          .get(`/unionOpportunity/toMe/page?current=1`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records;
              this.totalAll = res.data.data.total;
              this.tableData.forEach((v, i) => {
                bussinessStatusChange(v.opportunity.acceptStatus);
              });
            } else {
              this.tableData = [];
              this.totalAll = 0;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 带条件搜索
    search() {
      $http
        .get(`/unionOpportunity/toMe/page?current=1&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              bussinessStatusChange(v.opportunity.acceptStatus);
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页搜索
    handleCurrentChange(val) {
      $http
        .get(`/unionOpportunity/toMe/page?current=${val}&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.tableData.forEach((v, i) => {
              bussinessStatusChange(v.opportunity.acceptStatus);
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 弹出框 商机详情
    showDialog(scope) {
      this.dialogVisible = true;
      this.detailData = scope.row;
    },
    // 接受
    agree(scope) {
      this.dialogVisible1 = true;
      this.opportunityId = scope.row.opportunity.id;
      this.putUnionId = scope.row.union.id;
    },
    // 接受确认
    confirm() {
      $http
        .put(
          `/unionOpportunity/${this.opportunityId}/unionId/${this.putUnionId}?isAccept=1&acceptPrice=${this
            .acceptancePrice}`
        )
        .then(res => {
          if (res.data.success) {
            eventBus.$emit('newCommissionPay');
            this.dialogVisible1 = false;
            this.search();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 拒绝
    disagree(scope) {
      this.dialogVisible2 = true;
      this.opportunityId = scope.row.opportunity.id;
      this.putUnionId = scope.row.union.id;
    },
    // 接受拒绝
    confirm1() {
      $http
        .put(`/unionOpportunity/${this.opportunityId}/unionId/${this.putUnionId}?isAccept=0`)
        .then(res => {
          this.dialogVisible2 = false;
          if (res.data.success) {
            this.dialogVisible1 = false;
            this.search();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    filterTag(value, row) {
      return row.isAccept === value;
    },
    // 关闭弹窗重置数据
    resetData() {
      this.acceptancePrice = '';
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less">
.model_01 {
  .el-dialog__body {
    padding: 0;
  }
  .el-dialog--tiny {
    width: 660px !important;
  }
  .model_detail {
    margin: 19px 0 10px 30px;
    p {
      margin-bottom: 15px;
    }
  }
}

.model_02 {
  .el-dialog--tiny {
    width: 660px !important;
  }
  .el-dialog__footer {
    padding: 70px 20px 15px !important;
  }
}
</style>
