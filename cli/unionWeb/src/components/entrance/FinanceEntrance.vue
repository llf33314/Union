<template>

</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'finance-entrance',
  created: function() {
    parent.document.title = '商家联盟管理系统';
    // 首页查询我的联盟信息
    $http.get(`/union/index`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnionId) {
            this.$router.push({ path: '/no-union' });
          } else {
            this.$router.push({ path: '/finance/index' });
          };
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
}
</script>

