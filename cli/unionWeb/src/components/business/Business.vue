<template>
  <div class="container" id="Business">
    <el-tabs v-model="activeName" type="card">
      <el-tab-pane label="我的商机" name="first">
        <my-business></my-business>
      </el-tab-pane>
      <el-tab-pane label="我要推荐" name="second">
        <recommend></recommend>
      </el-tab-pane>
      <el-tab-pane label="佣金结算" name="third">
        <commission-settle></commission-settle>
      </el-tab-pane>
      <el-tab-pane label="数据统计图" name="fourth">
        <datachart></datachart>
      </el-tab-pane>
      <el-tab-pane label="商机佣金比例设置" name="fifth">
        <percent></percent>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import MyBusiness from './MyBusiness'
import Recommend from './Recommend'
import CommissionSettle from './CommissionSettle'
import Datachart from './Datachart'
import Percent from './Percent'
import $http from '@/utils/http.js'
export default {
  name: 'business',
  components: {
    MyBusiness,
    Recommend,
    CommissionSettle,
    Datachart,
    Percent
  },
  data() {
    return {
      activeName: 'first',
    };
  },
  created: function() {
    // 首页查询我的联盟信息
    $http.get(`/union/index`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnionId) {
            this.$router.push({ path: '/no-union' });
          } else {
            // 全局存储信息
            this.$store.commit('unionIdChange', res.data.data.currentUnionId);
          };
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  }
}
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
