<template>
  <div>
    <div>
      <p style="text-align: right;color:#20A0FF">
        <span @click="visible1=true" style="cursor: pointer">退盟说明?</span>
      </p>
      <el-table :data="tableData" style="margin-top: 25px">
        <el-table-column prop="member.enterpriseName" label="盟员名称">
        </el-table-column>
        <el-table-column prop="memberOut.createTime" label="申请时间">
        </el-table-column>
        <el-table-column prop="memberOut.applyOutReason" label="退盟理由">
        </el-table-column>
        <el-table-column prop="" label="操作" width="150">
          <template slot-scope="scope">
            <div class="sizeAndColor">
              <el-button size="small" @click="agree(scope)">同意</el-button>
              <el-button size="small" @click="disagree(scope)">拒绝</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="footer">
      <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
      </el-pagination>
    </div>
    <!--弹出框 退盟说明 -->
    <div class="tuimeng">
      <el-dialog title="退盟规则说明" :visible.sync="visible1" width="30%">
        <hr>
        <div class="model_12">
          <p><span>1、</span> 盟主在盟主权限正式转移之前，不能退出自己的联盟</p>
          <p><span>2、</span> 盟员申请退出联盟，需要等待联盟盟主确认后才进入退盟过渡期，过渡期时长为15天 </p>
          <p><span>3、 </span> 盟主确认盟员退出联盟申请至盟员正式退盟的时间段视为退盟过渡期</p>
          <p><span>4、 </span> 盟主可将盟员移出联盟，即时生效；且可在15天的退盟过渡期内“取消移出”</p>
          <p><span>5、</span> 盟员完全退出某联盟后，可再次申请加入该联盟</p>
          <p><span>6、</span> 盟员进入退盟过渡期，可对该联盟继续进行消费核销、优惠项目核销、办理联盟卡等操作</p>
          <p><span>7、</span> 盟员进入退盟过渡期，可推荐商机给联盟内的其他盟员，但不可接收其他盟员推荐的商机</p>
          <p><span>8、</span> 盟员进入退盟过渡期，不可再参与活动卡的报名；若已经报名成功的，在正式退盟之时,盟员所发布的优惠项目将不再显示在联盟活动卡内（如果盟员在售卖时间内再次加入联盟，发布的项目不进行恢复再过15天后视为盟员正式退盟</p>
          <p><span>9、</span> 盟员进入退盟过渡期，已结算佣金可提现</p>
          <p><span>10、 </span> 联盟其中一位盟员退出联盟，盟主及其他盟员可收到信息或者短信通知</p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="visible1=false">确 定</el-button>
        </span>
      </el-dialog>
    </div>
    <!--弹出框 同意 拒绝 -->
    <div class="tuimeng">
      <el-dialog title="" :visible.sync="visible2" width="30%">
        <div class="model_12">
          <p>是否同意“{{ outEnterpriseName }}”退出联盟?</p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm2">确定</el-button>
          <el-button @click="visible2=false">取消</el-button>
        </span>
      </el-dialog>
      <el-dialog title="" :visible.sync="visible3" width="30%">
        <div class="model_12">
          <p>是否拒绝“{{ outEnterpriseName }}”退出联盟</p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm3">拒绝</el-button>
          <el-button @click="visible3=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'union-quit-check',
  data() {
    return {
      visible1: false,
      tableData: [],
      currentPage: 1,
      totalAll: '',
      visible2: false,
      visible3: false,
      outEnterpriseName: '',
      outId: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    eventBus.$on('newOutApply', () => {
      this.init();
    });
    this.init();
  },
  methods: {
    init() {
      this.currentPage = 1;
      this.getTableData();
    },
    getTableData() {
      $http
        .get(`/unionMemberOut/unionId/${this.unionId}/page?current=${this.currentPage}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 分页查询
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 弹出框 同意
    agree(scope) {
      this.visible2 = true;
      this.outId = scope.row.memberOut.id;
      this.outEnterpriseName = scope.row.member.enterpriseName;
    },
    // 弹出框 拒绝
    disagree(scope) {
      this.visible3 = true;
      this.outId = scope.row.memberOut.id;
      this.outEnterpriseName = scope.row.member.enterpriseName;
    },
    // 弹出框 同意 确认
    confirm2() {
      $http
        .put(`/unionMemberOut/${this.outId}/unionId/${this.unionId}?isPass=1`)
        .then(res => {
          if (res.data.success) {
            this.visible2 = false;
            $http
              .get(`/unionMemberOut/unionId/${this.unionId}/page?current=1`)
              .then(res => {
                if (res.data.data) {
                  this.tableData = res.data.data.records || [];
                  this.totalAll = res.data.data.total;
                }
              })
              .catch(err => {
                this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
              });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 弹出框 拒绝 确认
    confirm3() {
      $http
        .put(`/unionMemberOut/${this.outId}/unionId/${this.unionId}?isPass=0`)
        .then(res => {
          if (res.data.success) {
            this.visible3 = false;
            $http
              .get(`/unionMemberOut/unionId/${this.unionId}/page?current=1`)
              .then(res => {
                if (res.data.data) {
                  this.tableData = res.data.data.records || [];
                  this.totalAll = res.data.data.total;
                }
              })
              .catch(err => {
                this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
              });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    }
  }
};
</script>

