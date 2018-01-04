<template>
  <div v-if="codeSrc">
    <!-- 是否关注公众号办理 -->
    <div class="fr drop_down">
      <p>扫码二维码关注公众号</p>
      <div class="middle_">
        <img v-bind:src="codeSrc" class="codeImg" style="width:240px;height:240px;">
        <div class="code_">
          <el-button @click="downloadCode">下载该二维码</el-button>
        </div>
      </div>
      <span>
        <p>提示：</p>
        <p>已关注过公众号的粉丝，扫码成功后将不在新增粉丝列表中呈现，粉丝可在公众号进行联盟卡注册的相关操作。</p>
      </span>
    </div>
    <!-- 新增粉丝信息 -->
    <div class="fr drop_down1 step2" v-if="this.publicId" v-show="visible">
      <p>新增粉丝信息</p>
      <div class="dddddd clearfix">
        <img v-bind:src="wxData.headurl" alt="" class="fl unionImg">
        <div class="fl" style="margin-left: 20px">
          <h6 style="margin-bottom: 17px">{{ wxData.nickName }}</h6>
          <span>时间：{{ wxData.time }} </span>
        </div>
        <i></i>
      </div>
    </div>
  </div>
</template>

<script>
import io from 'socket.io-client';
import $http from '@/utils/http.js';
export default {
  name: 'is-follow',
  data() {
    return {
      publicId: '',
      visible: false,
      codeSrc: '',
      wxUser: false,
      wxData: {},
      socket: '',
      socketKey: '',
      socketFlag: {
        socketKey: '',
        status: ''
      }
    };
  },
  watch: {
    wxData: function() {
      this.visible = true;
    }
  },
  mounted: function() {
    this.canFollow();
  },
  methods: {
    // 能否关注公众号 然后获取二维码
    canFollow() {
      $http
        .get(`/api/user/wxPublicUser`)
        .then(res => {
          if (res.data.data) {
            this.publicId = res.data.data;
          } else {
            this.publicId = '';
          }
        })
        .then(res => {
          if (this.publicId && !this.wxUser) {
            this.wxUser = true;
            $http
              .get(`/api/user/wxPublicUserQRCode/${this.publicId}`)
              .then(res => {
                if (res.data.data) {
                  this.codeSrc = res.data.data.qrCodeUrl;
                  this.socketKey = res.data.data.qrCodeSocketKey;
                } else {
                  this.codeSrc = '';
                  this.socketKey = '';
                }
              })
              .then(res => {
                if (!this.socket) {
                  var _this = this;
                  var socketUrl = this.$store.state.socketUrl;
                  this.socket = io.connect(socketUrl);
                  var socketKey = this.socketKey;
                  this.socket.on('connect', function() {
                    let jsonObject = { userId: socketKey, message: '0' };
                    _this.socket.emit('auth', jsonObject);
                  });
                }
                //重连机制
                let socketindex = 0;
                this.socket.on('reconnecting', function() {
                  socketindex += 1;
                  if (socketindex > 4) {
                    _this.socket.destroy(); //不在链接
                  }
                });
                this.socket.on('chatevent', function(data) {
                  console.log(data, 111);
                  if (_this.visible) {
                    let msg = eval('(' + data.message + ')');
                    _this.wxData = msg;
                  }
                });
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 下载二维码
    downloadCode() {
      let url = this.$store.state.memberUrl + `/addMember/downQcode.do?url=${this.codeSrc}`;
      window.open(url);
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
/*右边下拉框的样式*/

.drop_down {
  margin-top: 30px;
  border: 1px solid #ddd;
  height: 720px;
  overflow: auto;
  width: 420px;
  > p {
    background: #eef1f6;
    padding: 15px 30px;
    font-size: 12px;
  }
  /*二维码的显示框的样式*/
  .middle_ {
    text-align: center;
    padding: 50px 42px 40px 58px;
    > img {
      display: block;
      margin: 0 40px 30px 40px;
    }
  }
  > span {
    display: block;
    margin: 0px 42px 40px 58px;
    border-top: 1px solid #ddd;
    padding-top: 40px;
    p {
      color: #999999;
    }
  }
}
.drop_down1 {
  margin-top: 30px;
  border: 1px solid #ddd;
  overflow: auto;
  width: 420px;
  > p {
    background: #eef1f6;
    padding: 15px 30px;
    font-size: 12px;
  }
}
</style>
