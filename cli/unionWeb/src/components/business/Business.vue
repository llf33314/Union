<template>
  <div class="container" id="Business">
    <!-- 显示整页加载 -->
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
    </div>
    <div v-show="loadingVisible">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="我的商机" name="first">
          <my-business @loadingFinish="loadingFinish"></my-business>
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
      fullscreenLoading: true,
      loadingVisible: false
    };
  },
  methods: {
    loadingFinish() {
      this.fullscreenLoading = false;
      this.loadingVisible = true;
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>

</style>
