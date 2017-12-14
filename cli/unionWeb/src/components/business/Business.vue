<template>
  <div class="container" id="Business">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
      <!--显示整页加载，0.3秒后消失-->
    </div>
    <el-tabs v-model="activeName" type="card" style="display: none">
      <el-tab-pane label="我的商机" name="first">
        <my-business></my-business>
      </el-tab-pane>
      <el-tab-pane label="我要推荐" name="second">
        <recommend></recommend>
      </el-tab-pane>
      <el-tab-pane label="佣金结算" name="third">
        <commission></commission>
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
import MyBusiness from './MyBusiness';
import Recommend from '@/components/business/recommend/Recommend';
import Commission from '@/components/business/commission/Commission';
import Datachart from './Datachart';
import Percent from './Percent';
import $http from '@/utils/http.js';
export default {
  name: 'business',
  components: {
    MyBusiness,
    Recommend,
    Commission,
    Datachart,
    Percent
  },
  data() {
    return {
      activeName: 'first',
      fullscreenLoading: true
    };
  },
  mounted: function() {
    setTimeout(() => {
      this.fullscreenLoading = false;
      $('.el-tabs--card').show();
    }, 300);
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
