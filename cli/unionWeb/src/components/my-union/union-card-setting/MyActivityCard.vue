<template>
  <div id="MyActivityCard" v-loading="loading" element-loading-text="拼命加载中">
    <div v-show="loadingVisible">
      <div class="explain">
        <span>说明：</span>
        <div style="margin-top: 10px;">
          <div style="float: right;color: #666666" v-if="projectData.project.status" class="showCheckRecord">项目/商品 状态：
            <span style="color: #20A0FF"> {{ projectData.project.status }} </span>
            <span class="icon" @click="showCheckRecord">!</span>
            <!-- 审核记录 -->
            <check-record :activityId="this.activityId" v-show="visible1" class="auditRecord"></check-record>
          </div>
          <span v-if="projectData.isErp" style="color: #666;">
            您已开通ERP系统，您可将ERP系统的服务项目添加至联盟活动卡中，联盟会员办理活动卡后即可享受各个ERP系统的服务项目。
          </span>
          <span v-if="!projectData.isErp" style="color: #666;">
            联盟活动卡可关联ERP项目进行核销，您还没开通行业ERP，优先推荐您开通行业ERP后关联活动卡。
          </span>
        </div>
      </div>
      <!-- 项目列表 -->
      <div class="unionList">
        <!-- 非ERP -->
        <div v-show="!projectData.isErp">
          <my-non-erp-list :nonErpTextList="projectData.nonErpTextList" @nonErpTextListChange="nonErpTextListUpdate"></my-non-erp-list>
        </div>
        <!-- ERP -->
        <div v-show="projectData.isErp">
          <!-- ERP项目 -->
          <my-erp-list :erpTextList="projectData.erpTextList" @erpTextListChange="erpTextListUpdate"></my-erp-list>
          <!-- ERP项目商品 -->
          <my-erp-goods-list :erpGoodsList="projectData.erpGoodsList" @erpGoodsListChange="erpGoodsListUpdate"></my-erp-goods-list>
        </div>
      </div>
      <!-- 页面底部固定 -->
      <footer v-if="canEdit">
        <el-button @click="save">保存</el-button>
        <el-button type="primary" @click="visible=true" :disabled="!canSubmitFlag">提交审核</el-button>
      </footer>
      <!-- 弹出框 确认提交审核 -->
      <div class="model_2">
        <el-dialog title="通过" :visible.sync="visible" size="tiny">
          <hr>
          <div>
            <img src="~assets/images/delect01.png" class="fl">
            <span>请确认您的项目，提交审核后不可修改项目内容，且不可</span>
            <p>再次提交项目！</p>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirmSubmit">确定</el-button>
            <el-button @click="visible=false">取消</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import CheckRecord from '@/components/my-union/union-card-setting/CheckRecord';
import MyNonErpList from '@/components/my-union/union-card-setting/MyNonErpList';
import MyErpList from '@/components/my-union/union-card-setting/MyErpList';
import MyErpGoodsList from '@/components/my-union/union-card-setting/MyErpGoodsList';
import $http from '@/utils/http.js';
import { projectStatusFilter } from '@/utils/filter.js';
export default {
  name: 'my-activity-card',
  components: {
    CheckRecord,
    MyNonErpList,
    MyErpList,
    MyErpGoodsList
  },
  data() {
    return {
      projectData: {
        activityStatus: '',
        isErp: '',
        project: {
          status: ''
        },
        nonErpTextList: [],
        erpTextList: [],
        erpGoodsList: []
      },
      visible: false,
      loading: true,
      loadingVisible: false,
      canEdit: false,
      visible1: false
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    activityId() {
      return this.$route.params.id;
    },
    canSubmitFlag() {
      if (
        this.projectData.nonErpTextList.length < 1 &&
        this.projectData.erpTextList.length < 1 &&
        this.projectData.erpGoodsList.length < 1
      ) {
        return false;
      } else {
        return true;
      }
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('newActivityCheck', () => {
      this.init();
    });
    $(document).on('click', e => {
      if ($(e.target).closest('.showCheckRecord').length === 0) {
        this.visible1 = false;
      }
    });
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
            this.loadingVisible = true;
            this.loading = false;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    // 显示隐藏审核记录
    showCheckRecord() {
      if (this.visible1) {
        this.visible1 = false;
      } else {
        eventBus.$emit('showCheckRecord');
        this.visible1 = true;
      }
    },
    // 更新非ERP项目
    nonErpTextListUpdate(value) {
      this.projectData.nonErpTextList = value;
    },
    // 更新ERP项目
    erpTextListUpdate(value) {
      this.projectData.erpTextList = value;
    },
    // 更新ERP项目商品
    erpGoodsListUpdate(value) {
      this.projectData.erpGoodsList = value;
    },
    // 保存
    save() {
      let url = `/unionCardProjectItem/activityId/${this.activityId}/unionId/${this.unionId}/save`;
      let data = {};
      if (!this.projectData.isErp) {
        data.nonErpTextList = this.projectData.nonErpTextList;
        let canSaveFlag = data.nonErpTextList.find(item => {
          return item.name === '' || (item.number === '' || 0);
        });
        if (canSaveFlag) {
          this.$message({ showClose: true, message: '项目名称或数量不能为空', type: 'error', duration: 3000 });
        } else {
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 3000 });
                this.init();
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
            });
        }
      } else {
        data.erpTextList = this.projectData.erpTextList;
        data.erpGoodsList = this.projectData.erpGoodsList;
        let canSaveFlag1 = data.erpTextList.find(item => {
          return item.number === '' || 0;
        });
        let canSaveFlag2 = data.erpGoodsList.find(item => {
          return item.number === '' || 0;
        });
        if (canSaveFlag1) {
          this.$message({ showClose: true, message: '项目数量不能为空', type: 'error', duration: 3000 });
        } else if (canSaveFlag2) {
          this.$message({ showClose: true, message: '商品数量不能为空', type: 'error', duration: 3000 });
        } else {
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 3000 });
                this.init();
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
            });
        }
      }
    },
    // 确认提交
    confirmSubmit() {
      let url = `/unionCardProjectItem/activityId/${this.activityId}/unionId/${this.unionId}/commit`;
      let data = {};
      if (!this.projectData.isErp) {
        data.nonErpTextList = this.projectData.nonErpTextList;
        let canSaveFlag = data.nonErpTextList.find(item => {
          return item.name === '' || (item.number === '' || 0);
        });
        if (canSaveFlag) {
          this.$message({ showClose: true, message: '项目名称或数量不能为空', type: 'error', duration: 3000 });
        } else {
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '提交审核成功', type: 'success', duration: 3000 });
                eventBus.$emit('newActivityProject');
                this.init();
                this.visible = false;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
            });
        }
      } else {
        data.erpTextList = this.projectData.erpTextList;
        data.erpGoodsList = this.projectData.erpGoodsList;
        let canSaveFlag1 = data.erpTextList.find(item => {
          return item.number === '' || 0;
        });
        let canSaveFlag2 = data.erpGoodsList.find(item => {
          return item.number === '' || 0;
        });
        if (canSaveFlag1) {
          this.$message({ showClose: true, message: '项目数量不能为空', type: 'error', duration: 3000 });
        } else if (canSaveFlag2) {
          this.$message({ showClose: true, message: '商品数量不能为空', type: 'error', duration: 3000 });
        } else {
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '提交审核成功', type: 'success', duration: 3000 });
                eventBus.$emit('newActivityProject');
                this.init();
                this.visible = false;
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
            });
        }
      }
    }
  }
};
</script>
