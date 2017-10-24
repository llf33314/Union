<template>
  <div id="front">
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
      <el-tab-pane label="联盟卡消费核销" name="first">
        <UnionCard></UnionCard>
      </el-tab-pane>
      <el-tab-pane label="消费核销记录" name="second">
        <ExpenseRecord></ExpenseRecord>
      </el-tab-pane>
      <el-tab-pane label="办理联盟卡" name="third">
        <Transaction></Transaction>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import UnionCard from './UnionCard';
import ExpenseRecord from './ExpenseRecord';
import Transaction from './Transaction';
import $http from '@/utils/http.js';
export default {
  name: 'front',
  components: {
    UnionCard,
    ExpenseRecord,
    Transaction
  },
  data() {
    return {
      activeName: 'first'
    };
  },
  created: function() {
    // 首页查询我的联盟信息
    $http
      .get(`/union/index`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnionId) {
            this.$router.push({ path: '/no-union' });
          }
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    handleClick(tab, event) {
      if (tab.name !== 'first') {
        eventBus.$emit('tabChange1');
      }
      if (tab.name !== 'third') {
        eventBus.$emit('tabChange3');
      }
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
#front {
  margin: 40px 70px 20px 60px;
}
</style>
