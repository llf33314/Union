<template>
  <div id="front">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
      <!--显示整页加载，0.3秒后消失-->
    </div>
    <el-tabs v-model="activeName" type="card" @tab-click="handleClick" style="display: none">
      <el-tab-pane label="联盟卡消费核销" name="first">
        <verification></verification>
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
import Verification from '@/components/front/Verification';
import ExpenseRecord from './ExpenseRecord';
import Transaction from './Transaction';
import $http from '@/utils/http.js';
export default {
  name: 'front',
  components: {
    Verification,
    ExpenseRecord,
    Transaction
  },
  data() {
    return {
      activeName: 'first',
      fullscreenLoading: true
    };
  },
  created: function() {
    // 首页查询我的联盟信息
    $http
      .get(`/index`)
      .then(res => {
        if (res.data.data) {
          setTimeout(() => {
            this.fullscreenLoading = false;
            // 判断是否创建或加入联盟
            if (!res.data.data.currentUnion) {
              this.$router.push({ path: '/no-union' });
            }
            $('.el-tabs--card').show();
          }, 300);
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
