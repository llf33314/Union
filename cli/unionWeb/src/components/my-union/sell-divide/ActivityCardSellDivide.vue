<template>
  <div id="ActivityCardSellDivide">
    <div v-show="visible" class="clearfix">
      <el-row style="margin-bottom: 35px">
        <el-button type="warning" style="padding: 10px 15px 10px 32px;position: relative;float: right">
          <img src="~assets/images/Videos.png" style="width: 17px;position: absolute;top:8px;left: 7px;"> 视频教程
        </el-button>
      </el-row>
      <!--主体内容-->
      <div v-if="data1.length === 0">
        <img src="~assets/images/noCurrent.png">
        <p>
          还没有相关数据
        </p>
      </div>
      <div v-if="data1.length > 0" class="contentList" v-for="item in data1" :key="item.activity.id">
        <div>
          <img :src="item.activity.img">
          <span>{{ item.activityStatus }}</span>
        </div>
        <p>
          <span>{{ item.activity.name }} </span>
          <el-button @click="setting(item.activity.id)" size="small">设置折扣</el-button>
        </p>
      </div>
      <el-row style="margin-bottom: 85px">
        <el-pagination @current-change="handleCurrentChange1" :current-page.sync="currentPage1" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll1" v-if="data1.length>0">
        </el-pagination>
      </el-row>
    </div>
    <div v-show="!visible">
      <p v-if="isUnionOwner" style="margin-bottom: 15px">
        <el-button type="primary" @click="showSettingDialog">比例设置</el-button>
        <el-button type="warning" style="padding: 10px 15px 10px 32px;position: relative">
          <img src="~assets/images/Videos.png" style="width: 17px;position: absolute;top:8px;left: 7px;"> 视频教程
        </el-button>
      </p>
      <!-- 比例设置列表 -->
      <el-table :data="tableData2" style="width: 100%" class="table_footer">
        <el-table-column prop="member.enterpriseName" label="盟员名称">
        </el-table-column>
        <el-table-column prop="sharingRatio.ratio" label="售卡分成比例">
          <template slot-scope="scope">
            {{ (scope.row.sharingRatio.ratio).toFixed(2) }} %
          </template>
        </el-table-column>
      </el-table>
      <el-pagination @current-change="handleCurrentChange2" :current-page.sync="currentPage2" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll2" v-if="tableData2.length>0">
      </el-pagination>
      <!-- 弹出框 比例设置 -->
      <div class="setScale">
        <el-dialog title="比例设置" :visible.sync="visible3" size="small" @close="resetData">
          <hr>
          <div class="model_setting">
            <div>
              <el-button type="primary" @click="onAverage()">平均分配</el-button>
            </div>
            <p>商机总比例之和不得超过100%，当前总比例为{{ sum3 }}%，剩余{{(100 - sum3).toFixed(0)}}%可分配。</p>
            <el-table :data="tableData3" style="width: 100%" height="450">
              <el-table-column prop="member.enterpriseName" label="企业名称">
              </el-table-column>
              <el-table-column prop="sharingRatio.ratio" label="分成比例(%)">
                <template slot-scope="scope">
                  <el-input v-model="scope.row.sharingRatio.ratio" placeholder="请输入比例" @change="onChange(scope)"></el-input>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="onSave">保存</el-button>
            <el-button @click="visible3=false">取 消</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'activity-card-sell-divie',
  data() {
    return {
      data1: [],
      totalAll1: '',
      currentPage1: 1,
      visible: true,
      visibleChangeFlag: false,
      tableData2: [],
      totalAll2: '',
      currentPage2: 1,
      visible3: false,
      sum3: 0,
      tableData3: [],
      activityId: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('sellDivideVisibleChange', () => {
      if (this.visible) {
        this.visibleChangeFlag = false;
      }
      if (this.visibleChangeFlag) {
        this.visible = !this.visible;
      }
    });
  },
  methods: {
    // 获取活动卡列表
    init() {
      $http
        .get(`/unionCardActivity/unionId/${this.unionId}/sharingRatio/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.data1 = res.data.data.records || [];
            this.totalAll1 = res.data.data.total;
          } else {
            this.data1 = [];
            this.totalAll1 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange1() {
      $http
        .get(`/unionCardActivity/unionId/${this.unionId}/sharingRatio/page?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.data1 = res.data.data.records || [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 获取活动参加盟员比例分配列表
    setting(value) {
      this.activityId = value;
      $http
        .get(`/unionCardSharingRatio/activityId/${this.activityId}/unionId/${this.unionId}/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records || [];
            this.tableData2.forEach(v => {
              if (!v.sharingRatio) {
                v.sharingRatio = {};
                v.sharingRatio.ratio = 0;
              }
            });
            this.totalAll2 = res.data.data.total;
            this.visibleChangeFlag = true;
            this.visible = false;
          } else {
            this.tableData2 = [];
            this.totalAll2 = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange2() {
      $http
        .get(`/unionCardSharingRatio/activityId/${this.activityId}/unionId/${this.unionId}/page?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData2 = res.data.data.records || [];
            this.tableData2.forEach(v => {
              if (!v.sharingRatio) {
                v.sharingRatio = {};
                v.sharingRatio.ratio = 0;
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 比例设置 弹出框 获取数据
    showSettingDialog() {
      this.visible3 = true;
      $http
        .get(`/unionCardSharingRatio/activityId/${this.activityId}/unionId/${this.unionId}`)
        .then(res => {
          this.sum3 = 0;
          if (res.data.data) {
            this.tableData3 = res.data.data;
            this.tableData3.forEach(v => {
              if (!v.sharingRatio) {
                v.sharingRatio = {};
                v.sharingRatio.ratio = 0;
              }
              v.sharingRatio.ratio = v.sharingRatio.ratio.toFixed(0);
              this.sum3 += parseFloat(v.sharingRatio.ratio);
            });
            this.sum3 = this.sum3.toFixed(0);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 平均分配
    onAverage() {
      let len = this.tableData3.length;
      if (len !== 0) {
        let average = (100 / len).toFixed(0);
        this.tableData3.forEach(v => {
          v.sharingRatio.ratio = average;
        });
        this.tableData3[0].sharingRatio.ratio = 100 - average * (len - 1);
      }
      this.sum3 = 100;
    },
    // 计算分配比例
    onChange() {
      let len = this.tableData3.length;
      this.sum3 = 0;
      this.tableData3.forEach(v => {
        v.sharingRatio.ratio = parseFloat(v.sharingRatio.ratio || 0);
        this.sum3 += v.sharingRatio.ratio;
      });
      this.sum3 = parseFloat(this.sum3.toFixed(0));
    },
    // 保存
    onSave() {
      if (this.sum3 === 100) {
        let url = `/union/unionCardSharingRatio/activityId/${this.activityId}/unionId/${this.unionId}`;
        // 处理数据
        let data = this.tableData3;
        $http
          .put(url, data)
          .then(res => {
            if (res.data.success) {
              this.init();
              this.visible3 = false;
              this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 5000 });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      } else {
        this.$message({ showClose: true, message: '售卡分成总比例之和应为100%', type: 'warning', duration: 5000 });
      }
    },
    // 关闭弹窗重置数据
    resetData() {
      this.tableData3 = [];
    }
  }
};
</script>
