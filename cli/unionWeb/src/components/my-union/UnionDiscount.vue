<template>
  <div>
    <Breadcrumb :header-name="'红卡优惠设置'"></Breadcrumb>
    <div class="container" id="redCard">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="优惠项目审核" name="first" v-if="isUnionOwner">
          <my-discount-check></my-discount-check>
        </el-tab-pane>
        <el-tab-pane label="我的优惠项目" name="second">
          <my-discount-project></my-discount-project>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb'
import MyDiscountCheck from './MyDiscountCheck'
import MyDiscountProject from './MyDiscountProject'
export default {
  name: 'union-discount',
  components: {
    Breadcrumb,
    MyDiscountCheck,
    MyDiscountProject
  },
  data() {
    return {
      activeName: 'second',
    }
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  methods: {

  }
}
</script>
<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  /*//模型库样式*/
  .model_setting {
    padding: 30px 30px;
    .verify {
      margin: 15px 0;
    }
  }
  .preferenceItems {
    p {
      margin-top: 90px;
    }
    h4 {
      margin-bottom: 14px;
    }
  }
  #table4 {
    margin: 10px 0;
  }
}
</style>
