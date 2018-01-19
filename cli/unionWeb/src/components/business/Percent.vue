<template>
  <div>
    <h4>商机佣金比例设置</h4>
    <div>
      <el-form :inline="true" :model="formInline1" class="demo-form-inline">
        <el-form-item label="选择联盟：" v-show="options.length>1">
          <el-select v-model="unionId" placeholder="请选择" @change="search">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="ratioSetting">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="member.enterpriseName" label="盟员名称" width="200">
        </el-table-column>
        <el-table-column prop="" label="我给TA佣金" align="center">
          <template slot-scope="scope">
            {{ scope.row.ratioFromMe }} %
          </template>
        </el-table-column>
        <el-table-column prop="ratioToMe" label="TA给我佣金" align="center">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="bottom" >
              <p>"{{ scope.row.member.enterpriseName }}"给我的佣金：{{ scope.row.ratioToMe }}%</p>
              <div slot="reference" class="name-wrapper">
                <span>{{ scope.row.ratioToMe }}%</span>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
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
            <p>"{{ toEnterpriseName }}" 给我的佣金为：{{ ratioToMe }} % </p>
            <p>我给TA佣金 ：&nbsp;&nbsp;<input type="text" v-model="ratioFromMe" @keyup.enter.native="submit" id="pushMoney">
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
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>

  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'percent',
  data() {
    return {
      dialogVisible: false,
      formInline1: {
        region: '',
        state: ''
      },
      unionId: '',
      options: [],
      tableData: [],
      currentPage: 1,
      toEnterpriseName: '',
      ratioFromMe: '',
      ratioToMe: '',
      toMemberId: '',
      totalAll: 0
    };
  },
  mounted: function() {
    this.init();
    window.addEventListener('keyup', this.keyUp);
  },
  methods: {
    init() {
      // 获取我的当前有效的联盟
      $http
        .get(`/unionMain/busUser/valid`)
        .then(res => {
          if (res.data.data) {
            this.options = res.data.data || [];
            this.options.forEach((v, i) => {
              v.value = v.id;
              v.label = v.name;
            });
            // 给unionId 赋初始值
            this.unionId = this.options[0].value;
          } else {
            this.options = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    getTableData() {
      $http
        .get(`/unionOpportunityRatio/unionId/${this.unionId}/page?current=${this.currentPage}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            console.log(this.tableData);
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              v.ratioFromMe = v.ratioFromMe * 100 || 0;
              v.ratioToMe = v.ratioToMe * 100 || 0;
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    search() {
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页查询
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 设置比例
    setPercent(scope) {
      this.dialogVisible = true;
      this.toEnterpriseName = scope.row.member.enterpriseName;
      this.ratioToMe = scope.row.ratioToMe;
      this.ratioFromMe = scope.row.ratioFromMe;
      this.toMemberId = scope.row.member.id;
    },
    // 保存设置
    submit() {
      $http
        .put(
          `/unionOpportunityRatio/unionId/${this.unionId}/toMemberId/${this.toMemberId}?ratio=${this.ratioFromMe / 100}`
        )
        .then(res => {
          if (res.data.success) {
            this.$message({ showClose: true, message: '设置成功', type: 'success', duration: 3000 });
            this.search();
            this.dialogVisible = false;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    //点击键盘事件
    keyUp(event) {
      if (event.keyCode == 13) {
        //       如果获得焦点
        if ($('#pushMoney').is(':focus')) {
          this.submit();
        }
      }
    },
    // 关闭弹窗重置数据
    resetData() {
      this.ratioFromMe = '';
    }
  }
};
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
