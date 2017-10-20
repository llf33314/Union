<template>
  <!-- 优惠项目审核 -->
  <div class="model_projects">
    <el-tabs v-model="activeName">
      <el-tab-pane :label="'未审核（'+tableData2.length+'）'" name="first">
        <div>
          <el-table :data="tableData2">
            <el-table-column prop="enterpriseName" label="盟员名称">
            </el-table-column>
            <el-table-column prop="projectIllustration" label="项目说明">
            </el-table-column>
            <el-table-column prop="" label="操作" width="150">
              <template scope="scope">
                <div class="sizeAndColor">
                  <el-button size="small" @click="showDetail2(scope)">详情</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="footer">
            <el-pagination @current-change="handleCurrentChange2" :current-page.sync="currentPage2" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll2">
            </el-pagination>
          </div>
        </div>
        <!-- 未审核详情 -->
        <el-dialog title="详情" :visible.sync="dialogTableVisible2" size="small">
          <hr>
          <div class="model_setting">
            <p>
              <span>盟员名称：</span>
              <span>{{ visibleData2.enterpriseName }}</span>
            </p>
            <p>
              <span>项目说明：</span>
              <span>{{ visibleData2.illustration }}</span>
            </p>
            <p class="verify">
              <span class="spacil">
                <el-button type="primary" @click="passAll()"  v-bind:disabled="!canPass">通过</el-button>
              </span>
              <el-button @click="noPassAll()">不通过</el-button>
            </p>
            <el-table :data="tableData21" style="width: 100%" @selection-change="handleSelectionChange2">
              <el-table-column type="selection" width="55">
              </el-table-column>
              <el-table-column prop="name" label="项目名称">
              </el-table-column>
              <el-table-column prop="createtime" label="时间">
              </el-table-column>
              <el-table-column prop="status" label="状态"></el-table-column>
              <el-table-column label="操作">
                <template scope="scope">
                  <div>
                    <el-button size="small" @click="pass(scope)">通过</el-button>
                    <span class="butguo">
                      <el-button size="small" @click="noPass(scope)">不通过</el-button>
                    </span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane :label="'通过（'+tableData3.length+'）'" name="second">
        <div>
          <el-table :data="tableData3">
            <el-table-column prop="enterpriseName" label="盟员名称">
            </el-table-column>
            <el-table-column prop="projectIllustration" label="项目说明">
            </el-table-column>
            <el-table-column prop="" label="操作" width="150">
              <template scope="scope">
                <div class="sizeAndColor">
                  <el-button size="small" @click="showDetail3(scope)">详情</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="footer">
            <el-pagination @current-change="handleCurrentChange3" :current-page.sync="currentPage3" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll3">
            </el-pagination>
          </div>
        </div>
        <!-- 通过详情 -->
        <el-dialog title="详情" :visible.sync="dialogTableVisible3" size="small">
          <hr>
          <div class="model_setting">
            <p>
              <span>盟员名称：</span>
              <span>{{ visibleData3.enterpriseName }}</span>
            </p>
            <p>
              <span>项目说明：</span>
              <span>{{ visibleData3.illustration }}</span>
            </p>
            <el-table :data="tableData31" style="width: 100%">
              <el-table-column prop="name" label="项目名称">
              </el-table-column>
              <el-table-column prop="createtime" label="时间">
              </el-table-column>
              <el-table-column prop="status" label="状态">
              </el-table-column>
            </el-table>
          </div>
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane :label="'未通过（'+tableData4.length+'）'" name="third">
        <div class="notSubmitted">
          <el-table :data="tableData4" style="width: 100%">
            <el-table-column prop="enterpriseName" label="盟员名称">
            </el-table-column>
            <el-table-column prop="projectIllustration" label="项目说明">
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template scope="scope">
                <div class="sizeAndColor">
                  <el-button size="small" @click="showDetail4(scope)">详情</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="footer">
            <el-pagination @current-change="handleCurrentChange4" :current-page.sync="currentPage4" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll4">
            </el-pagination>
          </div>
        </div>
        <!-- 未通过详情 -->
        <el-dialog title="详情" :visible.sync="dialogTableVisible4" size="small">
          <hr>
          <div class="model_setting">
            <p>
              <span>盟员名称：</span>
              <span>{{ visibleData4.enterpriseName }}</span>
            </p>
            <p>
              <span>项目说明：</span>
              <span>{{ visibleData4.illustration }}</span>
            </p>
            <el-table :data="tableData41" style="width: 100%">
              <el-table-column prop="name" label="项目名称">
              </el-table-column>
              <el-table-column prop="createtime" label="时间">
              </el-table-column>
              <el-table-column prop="status" label="状态">
              </el-table-column>
            </el-table>
          </div>
        </el-dialog>
      </el-tab-pane>
      <el-tab-pane :label="'未提交（'+tableData1.length+'）'" name="fourth">
        <div>
          <el-table :data="tableData1">
            <el-table-column prop="enterpriseName" label="盟员名称">
            </el-table-column>
            <el-table-column prop="" label="操作">
              <template scope="scope">
                <div class="sizeAndColor">
                  <el-button size="small" @click="moveOut(scope)">移出</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <div class="footer">
            <el-pagination @current-change="handleCurrentChange1" :current-page.sync="currentPage1" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll1">
            </el-pagination>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <div class="model_2">
      <!-- 弹出框 移出确认 -->
      <el-dialog title="提示" :visible.sync="visible" size="tiny">
        <hr>
        <div>
          <img src="../../assets/images/delect01.png"  class="fl">
          <span>是否确认移出“ {{ enterpriseName }} ”</span>
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
import $http from '@/utils/http.js'
import $todate from '@/utils/todate.js'
export default {
  name: 'my-discount-check',
  data() {
    return {
      activeName: 'first',
      tableData1: [],
      dialogTableVisible1: false,
      currentPage1: 1,
      tableData2: [],
      dialogTableVisible2: false,
      visibleData2: {
        enterpriseName: '',
        illustration: '',
      },
      tableData21: [],
      currentPage2: 1,
      tableData3: [],
      dialogTableVisible3: false,
      visibleData3: {
        enterpriseName: '',
        illustration: '',
      },
      tableData31: [],
      currentPage3: 1,
      tableData4: [],
      dialogTableVisible4: false,
      visibleData4: {
        enterpriseName: '',
        illustration: '',
      },
      tableData41: [],
      currentPage4: 1,
      totalAll1: 0,
      totalAll2: 0,
      totalAll3: 0,
      totalAll4: 0,
      visible: false,
      enterpriseName: '',
      id: '',
      canPass: false,
    }
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    // 未提交
    $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/1?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData1 = res.data.data.pageData.records;
          this.totalAll1 = res.data.data.total;
        } else {
          this.tableData1 = [];
          this.totalAll1 = 0;
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 未审核
    $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/2?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData2 = res.data.data.pageData.records;
          this.totalAll2 = res.data.data.total;
        } else {
          this.tableData2 = [];
          this.totalAll2 = 0;
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 已通过
    $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/3?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData3 = res.data.data.pageData.records;
          this.totalAll3 = res.data.data.total;
        } else {
          this.tableData3 = [];
          this.totalAll3 = 0;
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 未通过
    $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/4?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData4 = res.data.data.pageData.records;
          this.totalAll4 = res.data.data.total;
        } else {
          this.tableData4 = [];
          this.totalAll4 = 0;
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 分页查询
    handleCurrentChange1(val) {
      $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/1?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData1 = res.data.data.pageData.records;
            this.totalAll11 = res.data.data.total;
          } else {
            this.tableData1 = [];
            this.totalAll1 = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange2(val) {
      $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/2?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.pageData.records;
            this.totalAll12 = res.data.data.total;
          } else {
            this.tableData2 = [];
            this.totalAll2 = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange3(val) {
      $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/3?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData3 = res.data.data.pageData.records;
            this.totalAll3 = res.data.data.total;
          } else {
            this.tableData3 = [];
            this.totalAll3 = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange4(val) {
      $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/4?current${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData4 = res.data.data.pageData.records;
            this.totalAll4 = res.data.data.total;
          } else {
            this.tableData4 = [];
            this.totalAll4 = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 详情
    showDetail2(scope) {
      this.dialogTableVisible2 = true;
      $http.get(`/unionPreferentialProject/${scope.row.projectId}/memberId/${this.unionMemberId}/status/2`)
        .then(res => {
          if (res.data.data) {
            this.visibleData2.enterpriseName = res.data.data.enterpriseName;
            this.visibleData2.illustration = res.data.data.project.illustration;
            this.tableData21 = res.data.data.itemList;
            this.tableData21.forEach((v, i) => {
              v.createtime = $todate.todate(new Date(v.createtime));
              if (v.status === 2) {
                v.status = '未审核'
              };
            });
          } else {
            this.visibleData2.enterpriseName = '';
            this.visibleData2.illustration = '';
            this.tableData21 = [];
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    showDetail3(scope) {
      this.dialogTableVisible3 = true;
      $http.get(`/unionPreferentialProject/${scope.row.projectId}/memberId/${this.unionMemberId}/status/3`)
        .then(res => {
          if (res.data.data) {
            this.visibleData3.enterpriseName = res.data.data.enterpriseName;
            this.visibleData3.illustration = res.data.data.project.illustration;
            this.tableData31 = res.data.data.itemList;
            this.tableData31.forEach((v, i) => {
              v.createtime = $todate.todate(new Date(v.createtime));
              if (v.status === 3) {
                v.status = '通过'
              };
            });
          } else {
            this.visibleData3.enterpriseName = '';
            this.visibleData3.illustration = '';
            this.tableData31 = [];
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    showDetail4(scope) {
      this.dialogTableVisible4 = true;
      $http.get(`/unionPreferentialProject/${scope.row.projectId}/memberId/${this.unionMemberId}/status/4`)
        .then(res => {
          if (res.data.data) {
            this.visibleData4.enterpriseName = res.data.data.enterpriseName;
            this.visibleData4.illustration = res.data.data.project.illustration;
            this.tableData41 = res.data.data.itemList;
            this.tableData41.forEach((v, i) => {
              v.createtime = $todate.todate(new Date(v.createtime));
              if (v.status === 4) {
                v.status = '不通过'
              };
            });
          } else {
            this.visibleData4.enterpriseName = '';
            this.visibleData4.illustration = '';
            this.tableData41 = [];
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 全选
    handleSelectionChange2(val) {
      this.multipleSelection = val;
      if(this.multipleSelection.length) {
        this.canPass = true;
      } else {
        this.canPass = false;
      };
    },
    // 移出
    moveOut(scope) {
      this.enterpriseName = scope.row.enterpriseName;
      this.memberId = scope.row.memberId;
      this.visible = true;
    },
    // 确认移出
    confirm() {
      $http.put(`/unionMemberOut/memberId/${this.unionMemberId}?tgtMemberId=${this.id}`)
        .then(res => {
          $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/1?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData1 = res.data.data.pageData.records;
                this.totalAll1 = res.data.data.total;
              } else {
                this.tableData1 = [];
                this.totalAll1 = 0;
              };
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 未审核项目审核
    pass(scope) {
      let url = `/unionPreferentialItem/batch/memberId/${this.unionMemberId}?isOK=1`;
      let data = [];
      data.push(scope.row.id);
      $http.put(url, data)
        .then(res => {
          this.dialogTableVisible2 = false;
          $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/2?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData2 = res.data.data.pageData.records;
                this.totalAll2 = res.data.data.total;
              } else {
                this.tableData2 = [];
                this.totalAll2 = 0;
              };
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    noPass(scope) {
      let url = `/unionPreferentialItem/batch/memberId/${this.unionMemberId}?isOK=0`;
      let data = [];
      data.push(scope.row.id);
      $http.put(url, data)
        .then(res => {
          this.dialogTableVisible2 = false;
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    passAll() {
      let url = `/unionPreferentialItem/batch/memberId/${this.unionMemberId}?isOK=1`;
      let data = this.multipleSelection;
      $http.put(url, data)
        .then(res => {
          this.dialogTableVisible2 = false;
          $http.get(`/unionPreferentialProject/page/memberId/${this.unionMemberId}/status/2?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData2 = res.data.data.pageData.records;
                this.totalAll2 = res.data.data.total;
              } else {
                this.tableData2 = [];
                this.totalAll2 = 0;
              };
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    noPassAll() {
      let url = `/unionPreferentialItem/batch/memberId/${this.unionMemberId}?isOK=0`;
      let data = this.multipleSelection;
      $http.put(url, data)
        .then(res => {
          this.dialogTableVisible2 = false;
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
  }
}
</script>

<style lang='less' rel="stylesheet/less" scoped>
/*//模型库样式*/

.model_setting {
  padding: 30px 30px;
  .verify {
    margin: 15px 0;
  }
  p {
    margin-bottom: 10px;
  }
}
</style>
