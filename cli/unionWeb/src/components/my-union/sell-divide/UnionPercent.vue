<template>
  <div>
    <Breadcrumb :header-name="header"></Breadcrumb>
    <div id="sellCard">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="售卡佣金分成记录" name="first">
          <sell-divide-proportion></sell-divide-proportion>
        </el-tab-pane>
        <el-tab-pane label="活动卡售卡比例设置" name="second" v-if="isUnionOwner">
          <activity-card-sell-divide></activity-card-sell-divide>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import $http from '@/utils/http.js';
import SellDivideProportion from '@/components/my-union/sell-divide/SellDivideProportion';
import ActivityCardSellDivide from '@/components/my-union/sell-divide/ActivityCardSellDivide';
export default {
  name: 'union-percent',
  components: {
    Breadcrumb,
    SellDivideProportion,
    ActivityCardSellDivide
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  data() {
    return {
      header: ['售卡佣金分成管理'],
      activeName: 'first'
    };
  },
  methods: {
    handleClick(tab) {
      if (tab.name === 'second') {
        eventBus.$emit('sellDivideVisibleChange');
      }
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
#sellCard {
  padding: 40px 80px 0 50px;
}

.table_footer {
  margin-top: 22px;
}
</style>
