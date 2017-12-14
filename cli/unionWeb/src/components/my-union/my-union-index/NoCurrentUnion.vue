<template>
  <div id="noCurrent">
    <div class="notice">
      <el-row>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content">我创建的联盟</div>
          <create></create>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content">我加入的联盟</div>
          <join></join>
        </el-col>
      </el-row>
      <div class="second">
        <img src="~assets/images/noCurrent.png">
        <p>
          你还没有任何联盟，赶紧去创建联盟或加入一个联盟吧！
          <router-link :to="{ path: '/my-union/union-introduced' }">
            <span>了解详情</span>
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import Join from '@/components/my-union/join-union/Join';
import Create from '@/components/my-union/create-union/Create';
export default {
  name: 'no-currentUnion',
  components: {
    Join,
    Create
  }
};
</script>

<style scoped lang='less' rel="stylesheet/less">
#noCurrent {
  margin: 40px 110px 0 50px;
  .grid-content {
    color: #333333;
    font-weight: bold;
    margin-bottom: 14px;
  }
  .second {
    margin-top: 30px;
    border: 1px solid #ddd;
    text-align: center;
    padding: 28px 0;
    img {
      width: 35px;
      margin-bottom: 10px;
    }
    p {
      color: #999999;
      font-size: 14px;
      span {
        color: #20a0ff;
        font-size: 14px;
      }
    }
  }
}
</style>

