<template>
  <div>
    <p class="information">退盟过渡期</p>
    <div>
      <div id="popBox">
        <el-button type="primary" @click="visible1=true" v-if="isUnionOwner">退盟申请</el-button>
        <el-button type="warning" style="padding: 10px 15px 10px 32px;position: relative">
          <img src="~assets/images/Videos.png" style="width: 17px;position: absolute;top: 8px;left: 7px;"> 视频教程
        </el-button>
        <p class="fr" style="color:#20A0FF;cursor:pointer;" @click="visible2=true">退盟说明?</p>
      </div>
      <div style="margin-top: 25px" class="second">
        <el-table :data="tableData">
          <el-table-column prop="member.enterpriseName" label="盟员名称">
          </el-table-column>
          <el-table-column prop="memberOut.confirmOutTime" label="退盟时间">
          </el-table-column>
          <el-table-column prop="memberOut.type" label="申请状态">
          </el-table-column>
          <el-table-column label="退盟期限" width="150">
            <template slot-scope="scope">
              {{ scope.row.periodDay }} 天
            </template>
          </el-table-column>
        </el-table>
        <div class="footer">
          <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
          </el-pagination>
        </div>
      </div>
      <!-- 弹出框 退盟申请 -->
      <div class="RefundAu">
        <el-dialog title="退盟申请" :visible.sync="visible1" size="tiny" @close="resetData">
          <hr>
          <div class="main">
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
              <el-form-item label="退盟理由:" prop="outReason">
                <el-input type="textarea" v-model="form.outReason" :rows="3" placeholder="请输入退盟理由">
                </el-input>
              </el-form-item>
            </el-form>
            <p>
              申请推出联盟，盟主同意后有15天过渡期，是否确定？
            </p>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="outConfirm('form')">确 定</el-button>
            <el-button @click="visible1=false">取 消</el-button>
          </span>
        </el-dialog>
      </div>
      <!--弹出框 退盟说明 -->
      <div class="tuimeng">
        <el-dialog title="退盟规则说明" :visible.sync="visible2" size="tiny">
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
            <el-button type="primary" @click="visible2=false">确 定</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'union-quit-transition',
  data() {
    return {
      visible1: false,
      visible2: false,
      tableData: [],
      currentPage: 1,
      totalAll: '',
      form: {},
      rules: {
        outReason: [{ required: true, message: '退盟理由不能为空，请重新输入', trigger: 'blur' }]
      }
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
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
        .get(`/unionMemberOut/unionId/${this.unionId}/period/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.tableData.forEach((v, i) => {
              v.memberOut.confirmOutTime = timeFilter(v.memberOut.confirmOutTime);
              v.memberOut.type === 1 ? (v.memberOut.type = '盟员申请退盟') : (v.memberOut.type = '盟主移出');
            });
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
    // 分页查询
    handleCurrentChange(val) {
      $http
        .get(`/unionMemberOut/unionId/${this.unionId}/period/page?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.tableData.forEach((v, i) => {
              v.memberOut.confirmOutTime = timeFilter(v.memberOut.confirmOutTime);
              v.memberOut.type === 1 ? (v.memberOut.type = '盟员申请退盟') : (v.memberOut.type = '盟主移出');
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 确认退盟申请
    outConfirm(formName) {
      let url = `/unionMemberOut/unionId/${this.unionId}`;
      let data = this.form.outReason;
      this.$refs[formName].validate(valid => {
        if (valid) {
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '申请成功，请等待审核', type: 'success', duration: 5000 });
                eventBus.$emit('newOutApply');
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      });
    },
    // 关闭弹窗重置数据
    resetData() {
      this.form.outReason = '';
    }
  }
};
</script>
