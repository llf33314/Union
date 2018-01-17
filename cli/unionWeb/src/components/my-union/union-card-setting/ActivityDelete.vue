<template>
  <div>
    <el-button @click="visible=true">删除</el-button>
    <!-- 弹出框 删除确认 -->
    <div class="model_2">
      <el-dialog title="删除" :visible.sync="visible" size="tiny">
        <hr>
        <div>
          <img src="~assets/images/delect01.png" class="fl">
          <span>确认删除盟员的项目？</span>
          <p>点击确定后，盟员的项目立即删除，该操作不可撤回。</p>
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
export default {
  name: 'activity-delete',
  props: ['activityId'],
  data() {
    return {
      visible: false
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  methods: {
    confirm() {
      $http
        .del(`/unionCardActivity/${this.activityId}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.success) {
            this.$message({ showClose: true, message: '删除成功', type: 'success', duration: 3000 });
            this.visible = false;
            eventBus.$emit('activityDelete');
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    }
  }
};
</script>
