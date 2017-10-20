<template>
  <!-- 我的优惠项目 -->
  <div>
    <div class="preferenceItems clearfix">
      <p class="fl">
        <el-button type="primary" @click="dialogVisible = true">新增</el-button>
        <!-- 弹出框 新增 -->
        <el-dialog :visible.sync="dialogVisible" size="tiny" @close="resetData">
          <el-row>
            <el-col :span="12" :offset="2">
              <el-input v-model="serviceName" placeholder="请输入项目名称"></el-input>
            </el-col>
          </el-row>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="save">保 存</el-button>
            <el-button @click="dialogVisible=false">取 消</el-button>
          </span>
        </el-dialog>
        <el-button type="primary" @click="submitAll" v-if="!isUnionOwner">提交审核</el-button>
        <el-button type="warning" style="padding: 10px 15px 10px 32px;">
          <img src="../../assets/images/Videos.png" style="width: 17px;position: absolute;top: 67px;left: 9px;">
          视频教程
        </el-button>
      </p>
      <div class="fr">
        <illustration></illustration>
      </div>
      <div class="footer">
        <el-table :data="tableData" style="width: 100%" ref="multipleTable" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55">
          </el-table-column>
          <el-table-column prop="name" label="项目名称">
          </el-table-column>
          <el-table-column prop="status" label="审核状态" :filters="[{ text: '未提交', value: '未提交' }, { text: '已提交', value: '已提交' }, { text: '已通过', value: '已通过' }, { text: '未通过', value: '未通过' }]" :filter-method="filterTag" filter-placement="bottom-end">
          </el-table-column>
          <el-table-column label="操作" ref="scope">
            <template scope="scope">
              <div class="sizeAndColor">
                <el-button size="small" @click="submit(scope)" v-if="scope.row.status === '未提交' || scope.row.status === '未通过'">
                  提交
                </el-button>
                <el-button size="small" @click="del(scope)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div>
          <div class="sizeAndColor fl">
            <el-button @click="selectAll()">全选</el-button>
            <el-button @click="toggleSelection()">取消选择</el-button>
            <el-button @click="deleteAll()">批量删除</el-button>
          </div>
          <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll">
          </el-pagination>
        </div>
      </div>
      <!-- 弹出框 删除确认 -->
      <div class="model_2">
        <el-dialog title="删除" :visible.sync="visible1" size="tiny">
          <hr>
          <div>
            <img src="../../assets/images/delect01.png"  class="fl">
            <span>是否确认删除“ {{ name }} ”</span>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm1">确定</el-button>
            <el-button @click="visible1=false">取消</el-button>
          </span>
        </el-dialog>
    </div>
      <!-- 弹出框 批量删除确认 -->
      <div class="model_2">
        <el-dialog title="删除" :visible.sync="visible2" size="tiny">
          <hr>
          <div>
            <img src="../../assets/images/delect01.png"  class="fl">
            <span>是否确认批量删除选中项目</span>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm2">确定</el-button>
            <el-button @click="visible2=false">取消</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
import Illustration from './MyDiscountProjectIllustration'
export default {
  name: 'my-discount-project',
  components: {
    Illustration,
  },
  data() {
    return {
      tableData: [],
      currentPage: 1,
      dialogVisible: false,
      serviceName: '',
      totalAll: 0,
      visible1: false,
      visible2: false,
      id: '',
      name: '',
    }
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      $http.get(`/unionPreferentialProject/myProject/memberId/${this.unionMemberId}?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.pageItem.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.status) {
                case 1:
                  v.status = '未提交';
                  break;
                case 2:
                  v.status = '已提交';
                  break;
                case 3:
                  v.status = '已通过';
                  break;
                case 4:
                  v.status = '未通过';
                  break;
              }
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 新增优惠项目保存
    save() {
      let url = `unionPreferentialItem/memberId/${this.unionMemberId}`
      let data = this.serviceName;
      $http.post(url, data)
        .then(res => {
          this.init();
        })
        .then(res => {
          this.dialogVisible = false;
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 提交审核
    submit(scope) {
      let url = `/unionPreferentialItem/batch/status/2/memberId/${this.unionMemberId}`;
      let data = [];
      data.push(scope.row.id);
      $http.put(url, data)
        .then(res => {
          this.init();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 批量提交
    submitAll() {
      let url = `/unionPreferentialItem/batch/status/2/memberId/${this.unionMemberId}`;
      let data = [];
      this.multipleSelection.forEach((v, i) => {
        data.push(v.id);
      });
      $http.put(url, data)
        .then(res => {
          this.init();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 删除
    del(scope) {
      this.name = scope.row.name;
      this.id = scope.row.id;
      this.visible1 = true;

    },
    // 删除确认
    confirm1() {
      this.visible1 = false;
      let url = `unionPreferentialItem/batch/delStatus/1/memberId/${this.unionMemberId}`;
      let data = [];
      data.push(this.id);
      $http.put(url, data)
        .then(res => {
          this.init();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 批量删除
    deleteAll() {
      this.visible2 = true;
    },
    // 批量删除确认
    confirm2() {
      this.visible2 = false;
      let url = `unionPreferentialItem/batch/delStatus/1/memberId/${this.unionMemberId}`;
      let data = [];
      this.multipleSelection.forEach((v, i) => {
        data.push(v.id);
      });
      $http.put(url, data)
        .then(res => {
          this.init();
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 取消选择
    toggleSelection(rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    // 全选
    selectAll() {
      this.tableData.forEach(item => {
        this.$refs.multipleTable.toggleRowSelection(item, true);
      });
    },
    // 分页查询
    handleCurrentChange(val) {
      $http.get(`/unionPreferentialProject/myProject/memberId/${this.unionMemberId}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.pageItem.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.status) {
                case 1:
                  v.status = '未提交';
                  break;
                case 2:
                  v.status = '已提交';
                  break;
                case 3:
                  v.status = '已通过';
                  break;
                case 4:
                  v.status = '未通过';
                  break;
              }
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 状态筛选
    filterTag(value, row) {
      return row.status === value;
    },
    // 勾选状态改变
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    // 弹框取消重置数据
    resetData() {
      this.serviceName = '';
    },
  }
}
</script>


<style lang='less' rel="stylesheet/less" scoped>
.preferenceItems {
  p {
    margin-top: 90px;
  }
  h4 {
    margin-bottom: 14px;
  }
}
</style>
