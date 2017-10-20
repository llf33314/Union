<template>
  <div>
    <Breadcrumb :header-name="'售卡佣金分成管理'"></Breadcrumb>
    <div class="sell_card">
      <el-tabs v-model="activeName">
        <el-tab-pane label="售卡佣金分成记录" name="first">
          <sell-divide-proportion></sell-divide-proportion>
        </el-tab-pane>
        <el-tab-pane label="比例设置" name="second" v-if="isUnionOwner">
          <sell-divide-setting></sell-divide-setting>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb'
import $http from '@/utils/http.js'
import SellDivideProportion from './SellDivideProportion'
import SellDivideSetting from './SellDivideSetting'
export default {
  name: 'union-percent',
  components: {
    Breadcrumb,
    SellDivideProportion,
    SellDivideSetting
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  data() {
    return {
      activeName: 'first',
    }
  },
  methods: {

  }
}
</script>
<style lang='less' rel="stylesheet/less" scoped>
.sell_card {
  padding: 40px 80px 0 50px;
}

.table_footer {
  margin-top: 22px;
}

.model_setting {
  padding: 30px 30px;
  p {
    padding: 10px 0 10px 60px;
  }
}
/*@media screen and (max-width: 1280px){
  #card{
      width:120px;
    .el-input{
      width: 120px;
    }
  }
}*/
</style>
