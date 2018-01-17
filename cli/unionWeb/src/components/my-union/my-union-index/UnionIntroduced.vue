<template>
  <div>
    <div class="container">
      <!-- 了解详情 -->
      <div class="detail">
        <span>商家联盟了解详情</span>
        <div class="center_detail">
          <div>
            <span class="Union_what">商家联盟是什么？</span>
            <p>多粉推出的商家联盟系统，是在移动互联网时代下，新零售的颠覆性创新理念</p>
            <p>商家联盟让商家选择可靠、值得信任的伙伴组成联盟</p>
            <p>整合了不同行业的资源，让商家一起共享客户资源，为联盟内的商家带来更多的商机机会</p>
          </div>
        </div>
      </div>
      <!-- 联盟优势 -->
      <div class="advantage">
        <span>联盟的优势</span>
        <div class="footer_detail">
          <ul>
            <li>
              <div>
                <img src="~assets/images/introduced01.png">
                <p class="first_p">免费入驻</p>
                <p>对于开通多粉升级版及以上套餐的商家，</p>
                <p> 商家联盟实行免费入驻的模式。</p>
              </div>
            </li>
            <li>
              <div>
                <img src="~assets/images/introduced02.png">
                <p class="first_p">折扣互通</p>
                <p>商家内的会员即可申请升级成为联盟会员，</p>
                <p> 畅享联盟内所有商家的折扣优惠</p>
              </div>
            </li>
            <li>
              <div>
                <img src="~assets/images/introduced03.png">
                <p class="first_p">商机推荐</p>
                <p>联盟内的商家可以相互推荐客户,</p>
                <p> 并获得相应的佣金奖励。</p>
              </div>
            </li>
            <li>
              <div>
                <img src="~assets/images/introduced04.png">
                <p class="first_p">联盟积分</p>
                <p>联盟卡会员在联盟内进行消费，</p>
                <p> 即可获得相应的联盟积分。</p>
              </div>
            </li>
          </ul>
          <div class="btn1">
            <el-button type="primary" @click="create">创建</el-button>
            <el-button @click="back">返回</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import $http from '@/utils/http.js';
export default {
  name: 'union-introduced',
  methods: {
    create: function() {
      $http
        .get(`/unionMainCreate/checkPermit`)
        .then(res => {
          if (res.data.success && !res.data.data.isPay) {
            this.$store.commit('permitIdChange', res.data.data.permitId);
            this.$router.push({ path: '/my-union/create-step' });
          } else if (res.data.success && res.data.data.isPay) {
            this.$router.push({ path: '/my-union/no-register' });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    },
    back: function() {
      this.$router.push({ path: '/my-union/no-currentUnion' });
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  .detail {
    margin-bottom: 50px;
    > span {
      color: #333333;
      font-weight: bold;
    }
    .center_detail {
      margin-top: 20px;
      height: 200px;
      border: 1px solid #eeeeee;
      background: #f8f8f8;
      text-align: center;
      > div {
        margin-top: 48px;
        .Union_what {
          display: block;
          margin-bottom: 20px;
          color: #333333;
        }
        > p {
          font-size: 14px;
          color: #666666;
        }
      }
    }
  }
  .advantage {
    > span {
      color: #333333;
      font-weight: bold;
    }
    .footer_detail {
      margin-top: 20px;
      height: 340px;
      border: 1px solid #f8f8f8;
      box-sizing: border-box;
      background: #f8f8f8;
      > ul {
        margin-top: 55px;
        overflow: hidden;
        > li {
          margin-left: 7%;
          float: left;
          width: 17%;
          text-align: center;
          .first_p {
            margin: 28px 0;
          }
          p {
            font-size: 14px;
          }
        }
      }
      .btn1 {
        text-align: center;
        margin-top: 65px;
      }
    }
  }
}
</style>
