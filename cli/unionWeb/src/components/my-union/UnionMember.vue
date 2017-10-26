<template>
  <div id="memberName">
    <el-row class="user_search">
      <el-col :xs="4" :sm="4" :md="4" :lg="4">
        <div class="grid-content bg-purple">
          <el-input placeholder="请输入盟员名称" icon="search" v-model="input"
                    :on-icon-click="search" class="input-search" @keyup.enter.native="search" >
          </el-input>
        </div>
      </el-col>
      <!-- 导出按钮 -->
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;padding-left: 20px">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData"  style="width: 100%">
      <el-table-column label="盟员名称" >
          <template scope="scope">
            <i class="iconfont" v-if="scope.row.isUnionOwner" style="font-size: 25px;color:#FDD43F;position: absolute;top: 4px;left: 9px;">&#xe504;</i>
            <span>{{ scope.row.enterpriseName }}</span>
          </template>
      </el-table-column>
      <el-table-column prop="createTime" label="加入时间">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>时间：{{ scope.row.createTime }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.createTime }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="discountFromMe" label="我给TA折扣（折）">
      </el-table-column>
      <el-table-column prop="discountToMe" label="TA给我折扣（折）">
      </el-table-column>
      <el-table-column prop="cardDividePercent" label="售卡分成比例">
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template scope="scope">
          <div class="sizeAndColor">
            <el-button size="small" @click="detail(scope)">详情</el-button>
            <el-button size="small" @click="discount(scope)">折扣</el-button>
            <el-button size="small" v-if="isUnionOwner && !scope.row.isUnionOwner && scope.row.status == 2" @click="remove(scope)">移出</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 详情 -->
    <div class="model__10">
      <el-dialog title="详情" :visible.sync="dialogVisible0" size="tiny">
        <hr>
        <div>
          <el-form ref="form" :model="form" label-width="80px">
            <el-form-item label="盟员名称：">
              {{ form.enterpriseName }}
            </el-form-item>
            <el-form-item label="加入时间：">
              {{ form.createTime }}
            </el-form-item>
            <el-form-item label="负责人：">
              {{ form.directorName }}
            </el-form-item>
            <el-form-item label="联系电话：">
              {{ form.directorPhone }}
            </el-form-item>
            <el-form-item label="邮箱：">
              {{ form.directorEmail }}
            </el-form-item>
            <el-form-item label="地址：">
              {{ form.enterpriseAddress }}
            </el-form-item>
          </el-form>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 当前折扣 -->
    <div class="model__10">
      <el-dialog title="设置折扣" :visible.sync="dialogVisible1" size="tiny" @close="resetData1">
        <hr>
        <div>
          <el-form ref="form1" :model="form1" label-width="80px">
            <el-form-item label="当前折扣：">
              {{ form1.discountFromMe }}
            </el-form-item>
            <el-form-item label="设置折扣：">
              <el-col :span="8">
                <el-input v-model="form1.discount" placeholder="请输入内容">
                </el-input>
              </el-col>
            </el-form-item>
          </el-form>
          <span>
            <el-button type="primary" @click="discountConfirm1">确 定</el-button>
            <el-button @click="dialogVisible1=false">取 消</el-button>
          </span>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 他给我的折扣 -->
    <div class="model__10">
      <el-dialog title="设置折扣" :visible.sync="dialogVisible2" size="tiny" @close="resetData2">
        <hr>
        <div>
          <el-form ref="form1" :model="form2" label-width="80px">
            <el-form-item label="他给我的折扣：">
              {{ form2.discountToMe }}
            </el-form-item>
            <el-form-item label="我给他的折扣：">
              <el-col :span="8">
                <el-input v-model="form2.discountFromMe" placeholder="请输入内容">
                </el-input>
              </el-col>
            </el-form-item>
          </el-form>
          <span>
            <el-button type="primary" @click="discountConfirm2">确 定</el-button>
            <el-button @click="dialogVisible2 = false">取 消</el-button>
          </span>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 移出确认 -->
    <el-dialog title="" :visible.sync="visible" size="tiny">
      <div class="model_12">
        <p>是否确认移出“ {{ enterpriseName }} ”</p>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm">确定</el-button>
        <el-button @click="visible = false">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import $todate from '@/utils/todate.js';
export default {
  name: 'union-member',
  data() {
    return {
      input: '',
      tableData: [],
      currentPage: 1,
      dialogVisible0: false,
      dialogVisible1: false,
      dialogVisible2: false,
      form: {
        busAddress: '',
        createTime: '',
        directorEmail: '',
        directorName: '',
        directorPhone: '',
        enterpriseName: ''
      },
      form1: {
        discountFromMe: '',
        discount: ''
      },
      form2: {
        discountFromMe: '',
        discountToMe: ''
      },
      current: 1,
      tgtMemberId: '',
      totalAll: 0,
      visible: false,
      enterpriseName: '',
      memberId: ''
    };
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  mounted: function() {
    if (this.unionMemberId) {
      this.init();
    }
  },
  watch: {
    // 查询盟员列表
    unionMemberId: function() {
      if (this.unionMemberId) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      $http
        .get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              if (v.discountFromMe) {
                v.discountFromMe = v.discountFromMe.toFixed(1);
              }
              if (v.discountToMe) {
                v.discountToMe = v.discountToMe.toFixed(1);
              }
              if (v.cardDividePercent) {
                v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
              }
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
    // 带条件查询盟员列表
    search() {
      $http
        .get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=1&enterpriseName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              if (v.discountFromMe) {
                v.discountFromMe = v.discountFromMe.toFixed(1);
              }
              if (v.discountToMe) {
                v.discountToMe = v.discountToMe.toFixed(1);
              }
              if (v.cardDividePercent) {
                v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页查询盟员列表
    handleCurrentChange(val) {
      this.current = val;
      $http
        .get(`/unionMember/pageMap/memberId/${this.unionMemberId}/?current=${val}&enterpriseName=${this.input}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              if (v.discountFromMe) {
                v.discountFromMe = v.discountFromMe.toFixed(1);
              }
              if (v.discountToMe) {
                v.discountToMe = v.discountToMe.toFixed(1);
              }
              if (v.cardDividePercent) {
                v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 详情
    detail(scope) {
      $http
        .get(`/unionMember/${scope.row.memberId}`)
        .then(res => {
          if (res.data.data) {
            this.form = res.data.data;
            this.form.createTime = $todate.todate(new Date(this.form.createtime));
            this.dialogVisible0 = true;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 移出
    remove(scope) {
      this.enterpriseName = scope.row.enterpriseName;
      this.memberId = scope.row.memberId;
      this.visible = true;
    },
    // 确认移出
    confirm() {
      this.visible = false;
      $http
        .put(`/unionMemberOut/memberId/${this.unionMemberId}?tgtMemberId=${this.memberId}`)
        .then(res => {
            this.$message({ showClose: true, message: '移出成功，该盟员将在15天过渡期后正式退盟', type: 'success', duration: 5000 });
            $http
              .get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=${this.current}`)
              .then(res => {
                if (res.data.data) {
                  this.tableData = res.data.data.records;
                  this.totalAll = res.data.data.total;
                  this.tableData.forEach((v, i) => {
                    if (v.discountFromMe) {
                      v.discountFromMe = v.discountFromMe.toFixed(1);
                    }
                    if (v.discountToMe) {
                      v.discountToMe = v.discountToMe.toFixed(1);
                    }
                    if (v.cardDividePercent) {
                      v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
                    }
                  });
                }
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 折扣
    discount(scope) {
      this.tgtMemberId = scope.row.memberId;
      if (scope.row.memberId === this.unionMemberId) {
        this.dialogVisible1 = true;
        this.form1.discountFromMe = scope.row.discountFromMe;
      } else {
        this.dialogVisible2 = true;
        this.form2.discountToMe = scope.row.discountToMe;
        this.form2.discountFromMe = scope.row.discountFromMe;
      }
    },
    // 确认折扣
    discountConfirm1() {
      $http
        .put(
          `/unionMemberDiscount/memberId/${this.unionMemberId}?tgtMemberId=${this.tgtMemberId}&discount=${this.form1
            .discount}`
        )
        .then(res => {
          this.$message({ showClose: true, message: '设置成功', type: 'success', duration: 5000 });
          this.dialogVisible1 = false;
          $http
            .get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=${this.current}`)
            .then(res => {
              if (res.data.data) {
                this.tableData = res.data.data.records;
                this.totalAll = res.data.data.total;
                this.tableData.forEach((v, i) => {
                  if (v.discountFromMe) {
                    v.discountFromMe = v.discountFromMe.toFixed(1);
                  }
                  if (v.discountToMe) {
                    v.discountToMe = v.discountToMe.toFixed(1);
                  }
                  if (v.cardDividePercent) {
                    v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
                  }
                });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    discountConfirm2() {
      $http
        .put(
          `/unionMemberDiscount/memberId/${this.unionMemberId}?tgtMemberId=${this.tgtMemberId}&discount=${this.form2
            .discountFromMe}`
        )
        .then(res => {
          this.$message({ showClose: true, message: '设置成功', type: 'success', duration: 5000 });
          this.dialogVisible2 = false;
          $http
            .get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=${this.current}`)
            .then(res => {
              if (res.data.data) {
                this.tableData = res.data.data.records;
                this.totalAll = res.data.data.total;
                this.tableData.forEach((v, i) => {
                  if (v.discountFromMe) {
                    v.discountFromMe = v.discountFromMe.toFixed(1);
                  }
                  if (v.discountToMe) {
                    v.discountToMe = v.discountToMe.toFixed(1);
                  }
                  if (v.cardDividePercent) {
                    v.cardDividePercent = v.cardDividePercent.toFixed(2) + '%';
                  }
                });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出盟员列表
    output() {
      let url =
        this.$store.state.baseUrl +
        `/unionMember/exportMap/memberId/${this.unionMemberId}?enterpriseName=${this.input}`;
      window.open(url);
    },
    // 关闭弹窗重置数据
    resetData1() {
      this.form1.discount = '';
    },
    resetData2() {
      this.form2.discountFromMe = '';
    }
  }
};
</script>




<style  lang='less' rel="stylesheet/less">
/*.el-table_1_column_6{*/

.model__10 {
  .el-dialog__body {
    padding: 0;
    > div {
      margin: 20px 40px 50px;
      > span {
        float: right;
        margin-bottom: 20px;
      }
    }
  }
  .el-dialog--tiny {
    width: 550px;
  }
  .el-form-item__label {
    width: 110px !important;
  }
}
</style>
