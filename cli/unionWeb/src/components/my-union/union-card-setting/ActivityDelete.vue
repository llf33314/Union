<template>
  <div>
    <el-button @click="visible=true">删除</el-button>
    <!-- 弹出框 确认删除 -->
    <el-dialog title="提示" :visible.sync="visible">
      <span>确认删除盟员的项目？</span>
      <span>点击确定后，盟员的项目立即删除，该操作不可撤回。</span>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="confirm">确 定</el-button>
        <el-button @click="visible=false">取 消</el-button>
      </span>
    </el-dialog>
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
            this.$message({ showClose: true, message: '删除成功', type: 'success', duration: 5000 });
            eventBus.$emit('activityDelete')
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>
