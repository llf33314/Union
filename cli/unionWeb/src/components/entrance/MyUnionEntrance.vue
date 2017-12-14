<template>

</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'my-union-entrance',
  created: function() {
    // 首页查询我的联盟信息
    $http.get(`/index`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnion) {
            this.$router.push({ path: '/my-union/no-currentUnion' });
          } else {
            this.$router.push({ path: '/my-union/index' });
          };
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
}
</script>

