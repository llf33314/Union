<template>
  <div>
    <Breadcrumb :header-name="'退盟管理'"></Breadcrumb>
    <div class="container" id="Refund">
      <el-tabs v-model="activeName" v-if="isUnionOwner">
        <el-tab-pane label="退盟审核" name="first">
          <div>
            <p style="text-align: right;color:#20A0FF">
              <span @click="dialogVisible1 = true" style="cursor: pointer">退盟说明?</span>
            </p>
            <div style="margin-top: 25px">
              <el-table :data="tableData1">
                <el-table-column prop="outEnterpriseName" label="盟员名称">
                </el-table-column>
                <el-table-column prop="applyOutTime" label="申请时间">
                </el-table-column>
                <el-table-column prop="applyOutReason" label="退盟理由">
                </el-table-column>
                <el-table-column prop="" label="操作" width="150">
                  <template scope="scope">
                    <div class="sizeAndColor">
                      <el-button size="small" @click="agree(scope)">同意</el-button>
                      <el-button size="small" @click="disagree(scope)">拒绝</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <div class="footer">
            <el-pagination @current-change="handleCurrentChange1" :current-page.sync="currentPage1" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll1" v-if="tableData1.length>0">
            </el-pagination>
          </div>
        </el-tab-pane>
        <el-tab-pane label="退盟过渡期" name="second">
          <p class="information">退盟过渡期</p>
          <div>
            <div id="popBox">
              <el-button type="primary" @click="dialogVisible = true"  v-if="!isUnionOwner">退盟申请</el-button>
              <!-- 弹出框 退盟申请 -->
              <el-dialog title="退盟申请" :visible.sync="dialogVisible" size="tiny" @close="resetData">
                <hr>
                <div class="main">
                  <el-form ref="form" :model="form" label-width="80px">
                    <el-form-item label="退盟理由:">
                      <el-input type="textarea" v-model="form.outReason" :rows="3" placeholder="请输入退盟理由">
                      </el-input>
                    </el-form-item>
                  </el-form>
                  <p style="width: 350px;overflow: hidden">
                    申请推出联盟，盟主同意后有15天过渡期，是否确定？
                  </p>
                </div>
                <span slot="footer" class="dialog-footer">
                  <el-button type="primary" @click="outConfirm">确 定</el-button>
                  <el-button @click="dialogVisible=false">取 消</el-button>
                </span>
              </el-dialog>
              <el-button type="warning" style="padding: 10px 15px 10px 32px;">
                <img src="../../assets/images/Videos.png" style="width: 17px;position: absolute;top: 67px;left: 9px;">
                视频教程
              </el-button>
              <p class="fr" style="color:#20A0FF;cursor:pointer;" @click="dialogVisible1 = true">退盟说明?</p>
            </div>
            <div style="margin-top: 25px" class="second">
              <el-table :data="tableData2">
                <el-table-column prop="outEnterpriseName" label="盟员名称">
                </el-table-column>
                <el-table-column prop="confirmOutTime" label="退盟时间">
                </el-table-column>
                <el-table-column prop="outType" label="申请状态">
                </el-table-column>
                <el-table-column prop="remainDay" label="退盟期限" width="150">
                </el-table-column>
              </el-table>
              <div class="footer">
                <el-pagination @current-change="handleCurrentChange2" :current-page.sync="currentPage2" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll2" v-if="tableData2.length>0">
                </el-pagination>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
      <!--弹出框 退盟说明 -->
      <div class="tuimeng">
        <el-dialog title="退盟规则说明" :visible.sync="dialogVisible1" size="tiny">
          <hr>
          <div class="model_12">
            <p>1. 盟主不能退出自己的联盟</p>
            <p>2. 盟员申请退出联盟，需要等待联盟盟主确认后进入过渡期
              <p>3. 盟主确认盟员退出联盟申请至盟员正式退盟的时间段视为退盟过渡期</p>
              <p>4. 盟员进入退盟过渡期，不可对该联盟进行消费核销、商家推荐等操作</p>
              <p>5. 盟员进入退盟过渡期，已结算佣金可提现</p>
              <p>6. 盟员进入退盟过渡期，该盟员的会员不可在该联盟进入联盟卡升级操作</p>
              <p>7. 盟员进入退盟过渡期，若收费联盟卡的最大有效期已过，15天之后视为盟员正式退盟</p>
              <p>8. 盟员进入退盟过渡期，若收费联盟卡的最大有效期未过，需等待最大有效期时间结束后，</p>
              <p>&nbsp;&nbsp;&nbsp;&nbsp;再过15天后视为盟员正式退盟</p>
              <p>9. 联盟其中一位盟员退出联盟，盟主及其他盟员可收到信息或者短信通知</p>
              <p>10. 盟员完全退出某联盟后，可再次申请加入该联盟</p>
            </p>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="dialogVisible1 = false">确 定</el-button>
          </span>
        </el-dialog>
      </div>
      <!--弹出框 同意 拒绝 -->
      <div class="tuimeng">
        <el-dialog title="" :visible.sync="dialogVisible2" size="tiny">
          <div class="model_12">
            <p>是否同意“{{ outEnterpriseName }}”退出联盟?</p>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm">确定</el-button>
            <el-button @click="dialogVisible2 = false">取消</el-button>
          </span>
        </el-dialog>
        <el-dialog title="" :visible.sync="dialogVisible3" size="tiny">
          <div class="model_12">
            <p>是否拒绝“{{ outEnterpriseName }}”退出联盟</p>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm1">拒绝</el-button>
            <el-button @click="dialogVisible3=false">取消</el-button>
          </span>
        </el-dialog>

      </div>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import $http from '@/utils/http.js';
export default {
  name: 'union-quit',
  components: {
    Breadcrumb
  },
  data() {
    return {
      activeName: 'second',
      currentPage1: 1,
      currentPage2: 1,
      dialogVisible: false,
      dialogVisible1: false,
      dialogVisible2: false,
      dialogVisible3: false,
      tableData1: [],
      tableData2: [],
      form: {
        outReason: ''
      },
      outId: '',
      outEnterpriseName: '',
      totalAll1: 0,
      totalAll2: 0
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    if (this.isUnionOwner) {
      $http
        .get(`/unionMemberOut/page/applyOut/memberId/${this.unionMemberId}?current=1`)
        .then(res => {
          if (this.isUnionOwner) {
            this.activeName = 'first';
          }
          if (res.data.data) {
            this.tableData1 = res.data.data.records;
            this.totalAll1 = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
    $http
      .get(`/unionMemberOut/page/outing/memberId/${this.unionMemberId}?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData2 = res.data.data.records;
          this.totalAll2 = res.data.data.total;
          this.tableData2.forEach((v, i) => {
            v.remainDay += '天';
            switch (v.outType) {
              case 1:
                v.outType = '盟员申请退盟';
                break;
              case 2:
                v.outType = '盟主移出盟员';
                break;
            }
          });
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 弹出框 同意
    agree(scope) {
      this.dialogVisible2 = true;
      this.outId = scope.row.outId;
      this.outEnterpriseName = scope.row.outEnterpriseName;
    },
    // 弹出框 拒绝
    disagree(scope) {
      this.dialogVisible3 = true;
      this.outId = scope.row.outId;
      this.outEnterpriseName = scope.row.outEnterpriseName;
    },
    // 弹出框 同意 确认
    confirm() {
      $http
        .put(`/unionMemberOut/${this.outId}/memberId/${this.unionMemberId}?isOK=1`)
        .then(res => {
          this.dialogVisible2 = false;
          $http
            .get(`/unionMemberOut/page/applyOut/memberId/${this.unionMemberId}?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData1 = res.data.data.records;
                this.totalAll1 = res.data.data.total;
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
    // 弹出框 拒绝 确认
    confirm1() {
      $http
        .put(`/unionMemberOut/${this.outId}/memberId/${this.unionMemberId}?isOK=0`)
        .then(res => {
          this.dialogVisible3 = false;
          $http
            .get(`/unionMemberOut/page/applyOut/memberId/${this.unionMemberId}?current=1`)
            .then(res => {
              if (res.data.data) {
                this.tableData1 = res.data.data.records;
                this.totalAll1 = res.data.data.total;
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
    // 分页查询
    handleCurrentChange1(val) {
      $http
        .get(`/unionMemberOut/page/applyOut/memberId/${this.unionMemberId}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData1 = res.data.data.records;
            this.totalAll1 = res.data.data.total;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange2(val) {
      $http
        .get(`/unionMemberOut/page/outing/memberId/${this.unionMemberId}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records;
            this.totalAll2 = res.data.data.total;
            this.tableData2.forEach((v, i) => {
              v.remainDay += '天';
              switch (v.outType) {
                case 1:
                  v.outType = '盟员申请退盟';
                  break;
                case 2:
                  v.outType = '盟主移出盟员';
                  break;
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 申请退盟
    outConfirm() {
      $http
        .post(`/unionMemberOut/memberId/${this.unionMemberId}/applyOut?applyOutReason=${this.form.outReason}`)
        .then(res => {
          $http.get(`/unionMemberOut/page/outing/memberId/${this.unionMemberId}?current=1`).then(res => {
            this.tableData2 = res.data.data.records;
            this.totalAll2 = res.data.data.total;
            this.tableData2.forEach((v, i) => {
              v.remainDay += '天';
              switch (v.outType) {
                case 1:
                  v.outType = '盟员申请退盟';
                  break;
                case 2:
                  v.outType = '盟主移出盟员';
                  break;
              }
            });
          });
        })
        .then(res => {
          this.dialogVisible = false;
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 关闭弹窗重置数据
    resetData() {
      this.form.outReason = '';
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  .second {
    margin-top: 11px !important;
  }
  .main {
    margin-bottom: 45px;
    p {
      margin-left: 140px;
      color: #999999;
    }
  }
  .tuimeng {
    .model_12 {
      margin-left: 30px;
      margin-top: 20px;
      p {
        margin-bottom: 7px;
        color: #333333;
      }
    }
  }
}
</style>
