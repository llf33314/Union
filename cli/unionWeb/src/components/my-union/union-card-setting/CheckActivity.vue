<template>
  <div>
    <el-button @click="visible1=true">
      待审核 ({{ projectCheckCount }})
    </el-button>
    <!-- 弹出框 待审核详情 -->
    <el-dialog title="审核项目" :visible.sync="visible1" @close="resetSelection">
      <el-table :data="tableData" style="width: 100%" ref="multipleTable" @selection-change="handleSelectionChange">
        <el-table-column type="selection" min-width="55px"></el-table-column>
        <el-table-column prop="member.enterpriseName" label="盟员名称">
        </el-table-column>
        <el-table-column prop="itemList_" label="项目名称">
        </el-table-column>
      </el-table>
      <div>
        <el-button :disabled="!multipleSelection.length" @click="visible2=true">通过</el-button>
        <el-button :disabled="!multipleSelection.length" @click="visible3=true">不通过</el-button>
      </div>
    </el-dialog>
    <!-- 弹出框 确认通过 -->
    <el-dialog title="提示" :visible.sync="visible2">
      <span>确认通过盟员的项目？</span>
      <span>点击确定后，盟员的项目立即生效，该操作不可撤回。</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm2">确 定</el-button>
        <el-button @click="visible2=false">取 消</el-button>
      </span>
    </el-dialog>
    <!-- 弹出框 确认不通过 -->
    <el-dialog title="审核不通过" :visible.sync="visible3">
      <span>不通过理由：</span>
      <el-input v-model="rejectReason"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm3">确 定</el-button>
        <el-button @click="visible3=false">取 消</el-button>
      </span>
    </el-dialog>
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
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
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
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
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
            this.init();
            this.$message({ showClose: true, message: '审核通过', type: 'success', duration: 5000 });
            this.visible2 = false;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 确认不通过
    confirm3() {
      let url = `/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}/projectCheck?isPass=0`;
      let data = {};
      data.rejectReason = this.rejectReason;
      $http
        .put(url, data)
        .then(res => {
          if (res.data.success) {
            this.init();
            this.$message({ showClose: true, message: '审核不通过', type: 'success', duration: 5000 });
            this.visible3 = false;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>

