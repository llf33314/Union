<template>
  <div id="noRegister">
    <div class="container">
      <div class="top_top clearfix">
        <img src="~assets/images/chapter01.png" class="fl">
        <ul class="fl">
          <li>
            <span class="first_">{{levelDesc + '(' + unionLevelDesc + ')'}}</span>
          </li>
          <li>
            <span class="two_">可创建一个 {{ number }} 位成员的联盟，成为盟主</span>
          </li>
          <li>
            <span class="two_">价格:</span>
            <span class="special">￥{{ price | formatPrice }}</span>
          </li>
          <li class="version">
            <span class="two_">版本:</span>
            <el-radio-group v-model="id">
              <el-radio-button v-for="item in payItems" :key="item.id" :label="item.id"> {{ item.desc }} </el-radio-button>
            </el-radio-group>
          </li>
          <li>
            <el-button type="primary" @click="buy">立即购买</el-button>
            <el-button @click="tryFree">升级至尊版 免费试用</el-button>
          </li>
        </ul>
        <p class="fr">若您对功能购买有任何疑问，请咨询客服400 889 4522</p>
      </div>
      <div class="bottom_bottom">
        <div class="fontColor">
          <span>商家联盟介绍</span>
          <p>多粉推出的商家联盟系统，是在移动互联网时代下，新零售的颠覆性创新理念。</p>
          <p>商家联盟可以让商家选择可靠、值得信任的伙伴组成联盟，</p>
          <p>整合了不同行业的资源，让商家一起共享客户资源，为联盟内的商家带来更多的商机机会。</p>
        </div>
        <p>应用页面</p>
        <div class="photo">
          <img src="~assets/images/brandedCard01.png" alt="">
          <img src="~assets/images/brandedCard02.png" alt="">
        </div>
        <div class="photo">
          <img src="~assets/images/brandedCard03.png" alt="">
          <img src="~assets/images/brandedCard04.png" alt="">
        </div>
      </div>
      <!-- 弹出框 扫码支付 -->
      <div class="model_1">
        <el-dialog title="付款" :visible.sync="visible1" size="tiny">
          <hr>
          <div class="model_">
            <p><img v-bind:src="codeSrc" class="codeImg" style="width:240px;height:240px;"></p>
            <p style="margin-bottom: 50px;">请使用微信/支付宝扫描该二维码付款</p>
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>
<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
export default {
  name: 'no-register',
  data() {
    return {
      number: 0,
      payItems: [{}],
      levelDesc: '',
      unionLevelDesc: '',
      id: '',
      price: 0,
      visible1: false,
      codeSrc: '',
      socket: '',
      socketKey: '',
      socketFlag: {
        socketKey: '',
        status: ''
      }
    };
  },
  filters: {
    // 价格格式
    formatPrice: function(value) {
      if (!value) {
        return Number(0).toFixed(2);
      } else {
        return Number(value).toFixed(2);
      }
    }
  },
  mounted: function() {
    $http
      .get(`/unionMainPackage/option`)
      .then(res => {
        if (res.data.data) {
          this.levelDesc = res.data.data.busVersionName;
          this.unionLevelDesc = res.data.data.unionVersionName;
          this.payItems = res.data.data.packageList;
          if (this.payItems.length) {
            this.number = this.payItems[1].number;
            this.id = this.payItems[1].id;
            this.price = this.payItems[1].price;
          }
        } else {
          this.levelDesc = '';
          this.unionLevelDesc = '';
          this.payItems = [];
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  watch: {
    id: function() {
      this.payItems.forEach((v, i) => {
        if (v.id === this.id) {
          this.price = v.price;
        }
      });
    }
  },
  methods: {
    // 购买
    buy() {
      if (this.id) {
        $http
          .post(`/unionMainPermit/packageId/${this.id}`)
          .then(res => {
            if (res.data.data) {
              this.codeSrc = res.data.data.payUrl;
              this.socketKey = res.data.data.socketKey;
            } else {
              this.codeSrc = '';
              this.socketKey = '';
            }
          })
          .then(res => {
            this.visible1 = true;
          })
          .then(res => {
            var _this = this;
            var socketUrl = this.$store.state.socketUrl;
            if (!this.socket) {
              this.socket = io.connect(socketUrl);
              var socketKey = this.socketKey;
              this.socket.on('connect', function() {
                let jsonObject = { userId: socketKey, message: '0' };
                _this.socket.emit('auth', jsonObject);
              });
            }
            this.socket.on('chatevent', function(data) {
              let msg = eval('(' + data.message + ')');
              console.log(msg, 111);
              // 避免 socket 重复调用
              if (!(_this.socketFlag.socketKey == msg.socketKey && _this.socketFlag.status == msg.status)) {
                if (_this.socketKey == msg.socketKey) {
                  if (msg.status == '1') {
                    _this.$message({ showClose: true, message: '支付成功', type: 'success', duration: 5000 });
                    _this.socketFlag.socketKey = msg.socketKey;
                    _this.socketFlag.status = msg.status;
                    _this.visible1 = false;
                    _this.$router.push({ path: '/my-union' });
                  } else if (msg.status == '0') {
                    _this.$message({ showClose: true, message: '支付失败', type: 'error', duration: 5000 });
                  }
                }
              }
            });
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      } else {
        this.$message({ showClose: true, message: '请选择版本', type: 'error', duration: 5000 });
      }
    },
    // 免费使用
    tryFree() {
      top.window.location = this.$store.state.wxmpUrl + '/trading/index.do?setType=trading';
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
.codeImg {
  width: 480px;
  height: 480px;
}

.container {
  margin: 40px 50px;
  .top_top {
    border: 1px solid #ddd;
    padding: 20px 20px 20px 50px;
    background: #f8f8f8;
    margin-bottom: 25px;
    p {
      margin-top: 170px;
      font-size: 14px;
      color: #666666;
    }
    .first_ {
      color: #333333;
    }
    .special {
      font-size: 24px;
      color: #ff4949;
    }
    .two_ {
      font-size: 14px;
      color: #666666;
    }
    ul {
      margin-left: 20px;
      height: 200px;
      li {
        margin-bottom: 13px;
      }
    }
  }
  .bottom_bottom {
    padding: 26px 0 26px 50px;
    border: 1px solid #ddd;
    .fontColor {
      p {
        color: #666666;
      }
    }
    div {
      span {
        font-size: 14px;
        color: #333333;
      }
      p {
        font-size: 14px;
        color: #333333;
        margin-top: 5px;
      }
    }
    > p {
      margin: 22px 0 30px 0;
      font-size: 14px;
      color: #333333;
    }
    .photo {
      img {
        margin-right: 15px;
        margin-bottom: 15px;
      }
    }
  }
}
</style>
