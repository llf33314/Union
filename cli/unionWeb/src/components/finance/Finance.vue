<template>
  <div class="container" id="finance">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
      <!--显示整页加载，1秒后消失-->
    </div>
    <div v-show="loadingVisible">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="佣金" name="first">
          <brokerage @loadingFinish="loadingFinish"></brokerage>
        </el-tab-pane>
        <el-tab-pane label="数据统计" name="second">
          <data-statistics></data-statistics>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Brokerage from '@/components/finance/Brokerage';
import DataStatistics from '@/components/finance/data-statistics/DataStatistics';
export default {
  name: 'finance',
  components: {
    Brokerage,
    DataStatistics
  },
  data() {
    return {
      activeName: 'second',
      fullscreenLoading: true,
      loadingVisible: false
    };
  },
  methods: {
    loadingFinish() {
      setTimeout(() => {
        this.fullscreenLoading = false;
        this.loadingVisible = true;
      }, 300);
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px;
  #union_people {
    padding: 13px 0 17px;
    background: #f8f8f8;
    > ul > li {
      float: left;
      width: 22%;
      border-left: 1px solid #eeeeee;
      padding-left: 22px;
      color: #999999;
      p {
        margin-bottom: 19px;
      }
      span {
        color: #ff6600;
        font-size: 25px;
        font-weight: bold;
        margin-right: 10px;
      }
      .special {
        display: block;
        .btn1 {
          button {
            float: right;
            margin-right: 20px;
          }
        }
      }
    }
  }
  .footer_ {
    > button {
      margin-bottom: 15px;
    }
  }
}
</style>
