<template>
  <div>
    <Breadcrumb :header-name="'联盟设置'"></Breadcrumb>
    <div class="container tabs" id="UnionSetting">
      <el-tabs v-model="activeName" type="card" >
        <el-tab-pane label="基础设置" name="first">
          <union-setting-basic></union-setting-basic>
        </el-tab-pane>
        <el-tab-pane label="联盟基本信息" name="second" v-if="isUnionOwner">
          <union-setting-Owner></union-setting-Owner>
        </el-tab-pane>
        <el-tab-pane label="盟主权限转移" name="third" v-if="isUnionOwner">
          <union-setting-transfer></union-setting-transfer>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import UnionSettingBasic from './UnionSettingBasic';
import UnionSettingOwner from './UnionSettingOwner';
import UnionSettingTransfer from './UnionSettingTransfer';
export default {
  name: 'union-setting',
  components: {
    Breadcrumb,
    UnionSettingBasic,
    UnionSettingOwner,
    UnionSettingTransfer
  },
  data() {
    return {
      activeName: 'first'
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  watch: {
    isUnionOwner: function() {
      if (!this.isUnionOwner) {
        this.activeName = 'first';
      }
    }
  },
  methods: {}
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  .percent {
    position: absolute;
    top: 1px;
    left: 250px;
  }

  .middle {
    position: relative;
    left: -70px;
  }
}
</style>
