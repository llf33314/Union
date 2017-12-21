<template>
  <el-button @click="create" class="avatar-uploader">
    <i class="el-icon-plus"></i>
    <p class="el-bottom">去创建联盟</p>
  </el-button>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'create',
  methods: {
    create: function() {
      $http
        .get(`/unionMainCreate/checkPermit`)
        .then(res => {
          if (res.data.success && !res.data.data.isPay) {
            this.$store.commit('permitIdChange', res.data.data.permitId);
            this.$router.push({ path: '/my-union/create-step' });
          } else if (res.data.success && res.data.data.isPay) {
            this.$router.push({ path: '/my-union/no-register' });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>

<style scoped>
.avatar-uploader {
  display: block;
  text-decoration: none;
  width: 80px;
  height: 80px;
  border: 1px dashed #c8d3df;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  font-size: 12px;
  text-align: center;
  background-color: #fbfdff;
}

.avatar-uploader:hover {
  border-color: #20a0ff;
}

.el-icon-plus {
  font-size: 16px;
  color: #8c939d;
  position: absolute;
  top: 50%;
  left: 50%;
  margin: -8px 0 0 -8px;
}

.el-bottom {
  position: relative;
  left: -6px;
  margin-top: 45px;
  line-height: 20px;
  color: #a1a1a1;
}
</style>
