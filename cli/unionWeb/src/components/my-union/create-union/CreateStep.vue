<template>
  <div>
    <Breadcrumb :header-name="['创建联盟']"></Breadcrumb>
    <div class="container container_one">
      <div class="step-top">
        <el-steps :active="active" :center="true" :align-center="true">
          <el-step title="商家基础信息设置" status="finish"></el-step>
          <el-step title="联盟设置"></el-step>
          <el-step title="创建联盟成功"></el-step>
        </el-steps>
      </div>
      <div v-show="active === 0" class="tabs">
        <keep-alive>
          <create-step-basic @activeChange="activeChange" @basicForm='basicForm'></create-step-basic>
        </keep-alive>
      </div>
      <div v-show="active === 1" class="tabs">
        <create-step-union @activeChange="activeChange" :basicFormData="this.basicFormData"></create-step-union>
      </div>
      <div v-show="active === 2" class="sucess_if">
        <div><img src="~assets/images/success01.png" alt=""></div>
        <p>创建联盟成功</p>
        <el-col>
          <el-button style="margin-top: 12px;" @click="back">返回</el-button>
        </el-col>
      </div>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import CreateStepBasic from './CreateStepBasic';
import CreateStepUnion from './CreateStepUnion';
export default {
  name: 'create-step',
  components: {
    Breadcrumb,
    CreateStepBasic,
    CreateStepUnion
  },
  data() {
    return {
      active: 0,
      basicFormData: {}
    };
  },
  methods: {
    activeChange(v) {
      this.active = v;
    },
    basicForm(v) {
      this.basicFormData = v;
    },
    back() {
      this.$router.push({ path: '/my-union' });
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  .footer_public {
    margin-left: 130px;
  }
  .tabs {
    /*一个图标*/
    .tubiao {
      width: 25px;
      height: 25px;
      border: 1px solid #1c8de0;
      border-radius: 50%;
      display: block;
      text-align: center;
      line-height: 25px;
      color: #1d90e6;
      font-weight: bold;
      position: absolute;
      top: 5px;
      left: 250px;
    }
  }
  .middle {
    position: relative;
    left: -70px;
  }
  .sucess_if {
    text-align: center;
    p {
      margin: 31px 0 49px 0;
      color: #666666;
    }
  }
  .percent {
    position: absolute;
    top: 1px;
    left: 250px;
  }
}
</style>
