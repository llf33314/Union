<template>
  <div class="btnCheck">
    <el-button @click="showDetail" size="small">
      待审核 ({{ projectCheckCount }})
    </el-button>
    <!-- 弹出框 待审核详情 -->
    <div class="checkReview">
      <el-dialog title="审核项目" :visible.sync="visible1" @close="resetSelection">
        <p>温馨提示：盟员的服务项目审核通过后，请及时到售卡分成管理设置佣金比例</p>
        <el-table :data="tableData" style="width: 100%" height="450" ref="multipleTable" @selection-change="handleSelectionChange">
          <el-table-column type="selection" min-width="55px"></el-table-column>
          <el-table-column prop="member.enterpriseName" label="盟员名称">
          </el-table-column>
          <el-table-column prop="itemList_" label="项目名称">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="bottom">
                <p v-for="item in scope.row.itemList" :key="item.id">项目名称：{{ item.name }}, 数量：{{ item.number }}</p>
                <div slot="reference" class="name-wrapper">
                  <span>{{ scope.row.itemList_ }}</span>
                </div>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin: 15px 0 25px;">
          <el-button :disabled="!multipleSelection.length" @click="visible2=true">通过</el-button>
          <el-button :disabled="!multipleSelection.length" @click="visible3=true">不通过</el-button>
        </div>
      </el-dialog>
    </div>
    <!-- 弹出框 确认通过 -->
    <div class="model_2">
      <el-dialog title="提示" :visible.sync="visible2" width="30%">
        <hr>
        <div>
          <img src="~assets/images/delect01.png" class="fl">
          <span>确认通过盟员的项目？</span>
          <p>点击确定后，盟员的项目立即生效，该操作不可撤回。</p>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm2">确定</el-button>
          <el-button @click="visible2=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 确认不通过 -->
    <div class="noPass">
      <el-dialog title="审核不通过" :visible.sync="visible3" @close="resetData">
        <hr>
        <div>
          <span>不通过理由：</span>
          <el-input v-model="rejectReason" type="textarea" placeholder="请输入内容"></el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirm3">确 定</el-button>
          <el-button @click="visible3=false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'check-activity',
  props: ['projectCheckCount', 'activityId'],
  data() {
    return {
      visible1: false,
      tableData: [],
      multipleSelection: [],
      visible2: false,
      visible3: false,
      rejectReason: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {},
  methods: {
    showDetail() {
      this.getTableData();
      this.visible1 = true;
    },
    getTableData() {
      $http
        .get(`/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}/projectCheck`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data || [];
            this.tableData.forEach((v, i) => {
              v.itemList_ = [];
              v.itemList.forEach(val => {
                v.itemList_.push(val.name);
              });
              v.itemList_ = v.itemList_.join(',');
            });
          } else {
            this.tableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    resetSelection() {
      this.$refs.multipleTable.clearSelection();
    },
    // 确认通过
    confirm2() {
      let url = `/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}/projectCheck?isPass=1`;
      let data = {};
      data.projectIdList = [];
      this.multipleSelection.forEach(v => {
        data.projectIdList.push(v.project.id);
      });
      $http
        .put(url, data)
        .then(res => {
          if (res.data.success) {
            this.getTableData();
            eventBus.$emit('newActivityCheck');
            this.$message({ showClose: true, message: '审核通过', type: 'success', duration: 3000 });
            this.visible2 = false;
            setTimeout(() => {
              parent.window.postMessage('openMask()', '*');
            }, 0);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 确认不通过
    confirm3() {
      let url = `/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}/projectCheck?isPass=0`;
      let data = {};
      data.projectIdList = [];
      this.multipleSelection.forEach(v => {
        data.projectIdList.push(v.project.id);
      });
      data.rejectReason = this.rejectReason;
      $http
        .put(url, data)
        .then(res => {
          if (res.data.success) {
            this.getTableData();
            eventBus.$emit('newActivityCheck');
            this.$message({ showClose: true, message: '审核不通过', type: 'success', duration: 3000 });
            this.visible3 = false;
            setTimeout(() => {
              parent.window.postMessage('openMask()', '*');
            }, 0);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 关闭弹窗清空数据
    resetData() {
      this.rejectReason = '';
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" >

</style>
