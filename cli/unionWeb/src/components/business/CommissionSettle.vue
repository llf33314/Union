<template>
  <div id="CommossionSettle">
    <h4>佣金结算</h4>
    <el-tabs v-model="activeName">
      <el-tab-pane label="我的佣金收入" name="first">
        <commission-income></commission-income>
      </el-tab-pane>
      <el-tab-pane label="我需支付的佣金" name="second">
        <commission-pay></commission-pay>
      </el-tab-pane>
      <el-tab-pane label="支付明细" name="third">
        <commission-detail></commission-detail>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import CommissionIncome from './CommissionIncome';
import CommissionPay from './CommissionPay';
import CommissionDetail from './CommissionDetail';
export default {
  name: 'commission-settle',
  components: {
    CommissionIncome,
    CommissionPay,
    CommissionDetail
  },
  data() {
    return {
      activeName: 'first'
    };
  },
  methods: {}
};
</script>
<style lang='less' rel="stylesheet/less" >
.model_02 {
  .el-dialog--tiny {
    width: 50%;
  }
  .el-dialog__body {
    padding: 0;
    .model_02_detail {
      margin: 18px 37px 60px 30px;
      p {
        margin-bottom: 20px;
      }
    }
  }
}
</style>
