<template>
  <div id="UnionQuit">
    <Breadcrumb :header-name="['退盟管理']"></Breadcrumb>
    <div class="container" id="Refund">
      <el-tabs v-model="activeName">
        <el-tab-pane label="退盟审核" name="first" v-if="isUnionOwner">
          <union-quit-check></union-quit-check>
        </el-tab-pane>
        <el-tab-pane label="退盟过渡期" name="second">
          <union-quit-transition></union-quit-transition>
        </el-tab-pane>
      </el-tabs>

    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import UnionQuitCheck from '@/components/my-union/union-quit/UnionQuitCheck';
import UnionQuitTransition from '@/components/my-union/union-quit/UnionQuitTransition';
export default {
  name: 'union-quit',
  components: {
    Breadcrumb,
    UnionQuitCheck,
    UnionQuitTransition
  },
  data() {
    return {
      activeName: 'second'
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>

</style>
