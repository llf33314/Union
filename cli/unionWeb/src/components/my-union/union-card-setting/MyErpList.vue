<template>
  <div class="unionList">
    <p>ERP项目列表</p>
    <div class="addUnion" v-if="canEdit">
      <el-button type="primary" @click="addErpProject">新增项目</el-button>
      <span>ERP项目添加至活动卡，联盟会员可通过活动卡到ERP管理系统进行核销</span>
    </div>
    <!-- ERP项目 没有相关数据 -->
    <div id="noUnion" v-if="erpTextList.length === 0">
      <img src="~assets/images/noCurrent.png">
      <p>
        还没有相关数据
      </p>
    </div>
    <!-- ERP项目 列表数据-->
    <el-table v-if="erpTextList.length > 0" :data="erpTextList" style="width: 95%" hight="450" v-show="canEdit">
      <el-table-column prop="name" label="项目名称">
      </el-table-column>
      <el-table-column prop="number" label="数量">
        <template slot-scope="scope">
          <el-input v-model="scope.row.number" placeholder="请输入数量" @keyup.native="check(scope)" @change="erpTextListChange"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="handle" label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="small" @click="handleDelete(scope)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-table v-if="erpTextList.length > 0" :data="erpTextList" style="width: 100%" hight="450" v-show="!canEdit">
      <el-table-column prop="name" label="项目名称">
      </el-table-column>
      <el-table-column prop="number" label="数量">
      </el-table-column>
    </el-table>
    <!-- 弹出框 新增ERP项目 -->
    <div class="model_4">
      <el-dialog title="添加ERP项目" :visible.sync="visible" @close="resetData">
        <div>
          <el-row>
            <el-col style="width:220px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-select v-model="erpType" clearable placeholder="请选择分类" @change="erpModelChange">
                  <el-option v-for="item in options1" :key="item.erpType" :label="item.erpName" :value="item.erpType">
                  </el-option>
                </el-select>
              </el-form>
            </el-col>
            <el-col style="width:220px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-select v-model="shopId" clearable placeholder="请选择门店" @change="search">
                  <el-option v-for="item in options2" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
                </el-select>
              </el-form>
            </el-col>
            <el-col style="width:200px;">
              <div class="grid-content1 bg-purple">
                <el-input placeholder="请输入关键字" @keyup.enter.native="search" icon="search" v-model="input" :on-icon-click="search" class="input-search2 fl">
                </el-input>
              </div>
            </el-col>
          </el-row>
          <div class="section_ clearfix">
            <div style="float:left;width: 580px;height: 445px;">
              <el-table :data="tableData" style="width: 100%;" height="446" ref="multipleTable" @select="handleSelect" @select-all="handleSelectAll" @row-click="handleRowClick">
                <el-table-column type="selection" min-width="55px"></el-table-column>
                <el-table-column prop="name" label="ERP项目名称" min-width="100px">
                </el-table-column>
              </el-table>
              <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
              </el-pagination>
            </div>
            <div class="rightContent">
              <p>已选择：{{ selectedErpRight.length }}</p>
              <div class="rightContentBottom">
                <div v-for="(item, index) in selectedErpRight" :key="item.id">
                  <span> {{ item.name }} </span>
                    <el-input-number v-model="item.number" :min="1"></el-input-number>
                    <el-button @click="handleDelete2(index)">删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm">确定</el-button>
          <el-button @click="visible=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { numberCheck } from '@/utils/filter.js';
export default {
  name: 'my-erp-list',
  props: ['erpTextList'],
  data() {
    return {
      visible: false,
      erpType: '',
      options1: [],
      shopId: '',
      options2: [],
      input: '',
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      selectedErpRight: [],
      projectData: {},
      canEdit: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    activityId() {
      return this.$route.params.id;
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      $http
        .get(`/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.projectData = res.data.data;
            if (this.projectData.project) {
              this.projectData.project.status = projectStatusFilter(this.projectData.project.status);
            } else {
              this.projectData.project = {};
              this.projectData.project.status = '';
            }
            if (
              this.projectData.activityStatus === 2 &&
              (this.projectData.project.status === '未提交' ||
                this.projectData.project.status === '不通过' ||
                !this.projectData.project.status)
            ) {
              this.canEdit = true;
            } else {
              this.canEdit = false;
            }
            setTimeout(() => {
              this.loading = false;
            }, 500);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleDelete(scope) {
      this.erpTextList.splice(scope.$index, 1);
      this.erpTextListChange();
    },
    erpTextListChange() {
      this.$emit('erpTextListChange', this.erpTextList);
    },
    // 获取行业，显示弹窗
    addErpProject() {
      $http
        .get(`/api/erp/list/erpType`)
        .then(res => {
          if (res.data.data) {
            this.options1 = res.data.data || [];
          } else {
            this.options1 = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
      this.visible = true;
    },
    // 选择行业，获取门店数据
    erpModelChange() {
      if (this.erpType) {
        $http
          .get(`/api/shop/list`)
          .then(res => {
            if (res.data.data) {
              this.options2 = res.data.data || [];
            } else {
              this.options2 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      } else {
        this.options2 = [];
        this.shopId = '';
      }
    },
    // 选择门店，获取项目列表
    search() {
      if (this.shopId) {
        $http
          .get(`/api/erp/list/server/${this.erpType}?current=1&shopId=${this.shopId}&search=${this.input}`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records || [];
              this.totalAll = res.data.data.total;
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
    // 分页获取项目列表
    handleCurrentChange(val) {
      if (this.shopId) {
        $http
          .get(`/api/erp/list/server/${this.erpType}?current=${val}&shopId=${this.shopId}&search=${this.input}`)
          .then(res => {
            if (res.data.data) {
              this.tableData = res.data.data.records || [];
              this.$nextTick(() => {
                this.checkToggle();
              });
            } else {
              this.tableData = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 点击行
    handleRowClick(row) {
      this.$refs.multipleTable.toggleRowSelection(row);
      this.handleSelect(null, row, false);
    },
    // 单选
    handleSelect(selection, row, isAll) {
      let pass = this.selectedErpRight.some(item => {
        return item.id === row.id;
      });
      if (!pass) {
        this.selectedErpRight.push(row);
      } else {
        this.selectedErpRight.forEach((item, index) => {
          if (item.id === row.id && !isAll) {
            this.selectedErpRight.splice(index, 1);
          }
        });
      }
    },
    // 全选
    handleSelectAll(selection) {
      if (selection.length) {
        this.tableData.forEach(item => {
          this.handleSelect(null, item, true);
        });
      } else {
        this.tableData.forEach(item => {
          this.handleSelect(null, item, false);
        });
      }
    },
    // 已选择右边项目勾选左边项目
    checkToggle() {
      this.tableData.forEach((v, i) => {
        this.selectedErpRight.forEach(val => {
          if (v.id === val.id) {
            this.$refs.multipleTable.toggleRowSelection(this.tableData[i]);
          }
        });
      });
    },
    // 右侧删除按钮
    handleDelete2(index) {
      let deletRow = this.selectedErpRight.splice(index, 1);
      this.tableData.forEach((v, i) => {
        if (v.id === deletRow[0].id) {
          this.$refs.multipleTable.toggleRowSelection(this.tableData[i], false);
        }
      });
    },
    // 校验折扣输入为数字类型
    check(scope) {
      scope.row.number = numberCheck(scope.row.number);
    },
    // 确定所选ERP项目
    confirm() {
      this.selectedErpRight.forEach(v => {
        this.erpTextList.push({
          erpType: this.erpType,
          shopId: this.shopId,
          erpTextId: v.id,
          name: v.name,
          number: v.number
        });
      });
      this.erpTextListChange();
      this.visible = false;
    },
    // 关闭弹窗清空所选数据
    resetData() {
      this.erpType = '';
      this.tableData = [];
      this.selectedErpRight = [];
    }
  }
};
</script>
