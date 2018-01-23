<template>

</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'business-entrance',
  created: function() {
    // 首页查询我的联盟信息
    $http
      .get(`/unionIndex`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnion) {
            this.$router.push({ path: '/no-union' });
          } else {
            this.$router.push({ path: '/business/index' });
          }
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
      });
  }
};
</script>

