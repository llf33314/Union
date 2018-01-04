<template>
  <div class="unionList">
    <p>商品列表</p>
    <div class="addUnion" v-if="canEdit">
      <el-button type="primary" @click="addErpGoods">新增商品</el-button>
      <span>进销存商品添加至活动卡，联盟会员可通过活动卡到ERP管理系统进行核销</span>
    </div>
    <!-- ERP项目商品 没有相关数据 -->
    <div id="noUnion" v-if="erpGoodsList.length === 0">
      <img src="~assets/images/noCurrent.png">
      <p>
        还没有相关数据
      </p>
    </div>
    <!-- ERP项目商品 列表数据-->
    <el-table v-if="erpGoodsList.length > 0" :data="erpGoodsList" style="width: 100%" hight="450" v-show="canEdit">
      <el-table-column prop="name" label="商品名称">
      </el-table-column>
      <el-table-column prop="spec" label="规格">
      </el-table-column>
      <el-table-column prop="number" label="数量">
        <template slot-scope="scope">
          <el-input v-model="scope.row.number" placeholder="请输入数量" @keyup.native="check(scope)" @change="erpGoodsListChange"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="handle" label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="small" @click="handleDelete(scope)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-table v-if="erpGoodsList.length > 0" :data="erpGoodsList" style="width: 100%" hight="450" v-show="!canEdit">
      <el-table-column prop="name" label="商品名称">
      </el-table-column>
      <el-table-column prop="spec" label="规格">
      </el-table-column>
      <el-table-column prop="number" label="数量" width="250">
      </el-table-column>
    </el-table>
    <!-- 弹出框 新增ERP商品 -->
    <div class="model_4">
      <el-dialog title="添加商品" :visible.sync="visible" @close="resetData">
        <hr>
        <div>
          <el-row>
            <el-col style="width:220px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-select v-model="shopId" clearable placeholder="请选择门店" @change="search">
                  <el-option v-for="item in options1" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
                </el-select>
              </el-form>
            </el-col>
            <el-col style="width:220px;">
              <el-form :inline="true" class="demo-form-inline">
                <el-select v-model="ProductClass" clearable placeholder="请选择行业" @change="search">
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
              <el-table :data="tableData" style="width: 100%;height: 100%;" ref="multipleTable" @select="handleSelect" @select-all="handleSelectAll" @row-click="handleRowClick">
                <el-table-column type="selection" min-width="55px"></el-table-column>
                <el-table-column prop="name" label="商品名称" min-width="100px">
                </el-table-column>
                <el-table-column prop="spec" label="规格" min-width="100px">
                </el-table-column>
                <el-table-column prop="amount" label="库存" min-width="100px">
                </el-table-column>
              </el-table>
              <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
              </el-pagination>
            </div>
            <div class="rightContent">
              <p>已选择：{{ selectedErpRight.length }}</p>
              <div v-for="(item, index) in selectedErpRight" :key="item.id">
                <el-input-number v-model="item.number" :min="1" size="small" :max="item.amount"></el-input-number>
                <el-button @click="handleDelete2(index)" type="text">删除</el-button>
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
import { numberCheck, projectStatusFilter } from '@/utils/filter.js';
export default {
  name: 'my-erp-goods-list',
  props: ['erpGoodsList'],
  data() {
    return {
      visible: false,
      shopId: '',
      options1: [],
      ProductClass: '',
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
  mounted: function() {
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
      this.erpGoodsList.splice(scope.$index, 1);
      this.erpGoodsListChange();
    },
    erpGoodsListChange() {
      this.$emit('erpGoodsListChange', this.erpGoodsList);
    },
    // 获取门店，显示弹窗
    addErpGoods() {
      $http
        .get(`/api/shop/list`)
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
    // 选择门店，获取分类数据，商品列表
    search() {
      if (this.shopId) {
        // 分类数据
        $http
          .get(`/jxc/api/list/jxcProductClass`)
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
        // 商品列表
        $http
          .get(
            `/jxc/api/list/jxcProduct?current=1&shopId=${this.shopId}&classId=${this.ProductClass}&search=${this.input}`
          )
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
      } else {
        this.options2 = [];
        this.shopId = '';
      }
    },
    // 分页获取商品列表
    handleCurrentChange(val) {
      this.currentPage = val;
      if (this.shopId) {
        $http
          .get(
            `/jxc/api/list/jxcProduct?current=${this.currentPage}&shopId=${this.shopId}&classId=${this
              .ProductClass}&search=${this.input}`
          )
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
          this.$refs.multipleTable.toggleRowSelection(v, false);
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
        this.erpGoodsList.push({ shopId: this.shopId, erpGoodsId: v.id, name: v.name, spec: v.spec, number: v.number });
      });
      this.erpGoodsListChange();
      this.visible = false;
    },
    // 关闭弹窗清空所选数据
    resetData() {
      this.shopId = '';
      this.tableData = [];
      this.selectedErpRight = [];
    }
  }
};
</script>

