<template>
  <div class="container" id="myMainUnion">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
      <!--显示整页加载，1秒后消失-->
    </div>
    <div id="container_myUnion" style="display: none">
      <!--创建和加入联盟-->
      <div class="notice">
        <el-row justify="center">
          <el-col :xs="8" :sm="8" :md="8" :lg="8">
            <div class="grid-content bg-purple notice-list">我创建的联盟</div>
            <div class="el_btn">
              <el-button class="fl" v-if="unionMainData.myCreateUnion.img" @click="changUnion(unionMainData.myCreateUnion.id)">
                <el-tooltip :content="unionMainData.myCreateUnionName" placement="bottom">
                  <img v-bind:src="unionMainData.myCreateUnion.img" alt="" class="fl unionImg">
                </el-tooltip>
              </el-button>
            </div>
            <create v-if="!unionMainData.myCreateUnion.id && unionListLength < 3"></create>
          </el-col>
          <el-col :xs="8" :sm="8" :md="8" :lg="8">
            <div class="grid-content bg-purple notice-list">我加入的联盟</div>
            <div class="el_btn">
              <el-button v-for="item in unionMainData.myJoinUnionList" :key="item.id" class="fl" @click="changUnion(item.id)">
                <el-tooltip :content="item.name" placement="bottom">
                  <img v-bind:src="item.img" alt="" class="fl unionImg">
                </el-tooltip>
              </el-button>
              <join v-if="unionListLength < 3"></join>
            </div>
          </el-col>
          <el-col :xs="8" :sm="8" :md="8" :lg="8">
            <union-notice></union-notice>
          </el-col>
        </el-row>
      </div>
      <!--中间栏信息（联盟设置等）-->
      <div class="nav clearfix">
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-setting', name: 'UnionSetting'}" v-if="isUnionOwner">
              <el-button type="success" size="mini">
                <img src="~assets/images/icon01.png" style="width: 11px;">
              </el-button>
              <span>联盟设置</span>
            </router-link>
            <router-link :to="{ path: '/my-union/union-setting', name: 'UnionSetting'}" v-if="!isUnionOwner">
              <el-button type="success" size="mini">
                <img src="~assets/images/icon01.png" style="width: 11px;">
              </el-button>
              <span>基础设置</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8" v-if="isUnionOwner">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-check', name: 'UnionCheck'}">
              <el-button type="info" size="mini">
                <img src="~assets/images/icon02.png" style="width: 11px;">
              </el-button>
              <span>入盟审核</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-recommend', name: 'UnionRecommend'}">
              <el-button type="danger" size="mini">
                <img src="~assets/images/icon03.png">
              </el-button>
              <span>推荐入盟</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-percent', name: 'UnionPercent'}">
              <el-button type="warning" size="mini">
                <img src="~assets/images/icon04.png" style="width: 11px;">
              </el-button>
              <span>售卡分成管理</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-card-setting', name: 'UnionCardSetting'}">
              <el-button type="success" size="mini">
                <img src="~assets/images/icon05.png" style="width: 11px;">
              </el-button>
              <span>联盟卡设置</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple ">
            <router-link :to="{ path: '/my-union/union-quit', name: 'UnionQuit'}">
              <el-button type="danger" size="mini">
                <img src="~assets/images/icon06.png" alt="">
              </el-button>
              <span>退盟管理</span>
            </router-link>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8" v-if="unionMainData.unionTransfer">
          <div class="grid-content bg-purple nav-list">
            <a @click="transfer" style="cursor: pointer">
              <el-button type="primary" size="mini">
                <img src="~assets/images/icon07.png" style="width: 13px;height: 11px;">
              </el-button>
              <span>盟主权限转移</span>
            </a>
          </div>
        </el-col>
        <!-- 弹出框 提示 -->
        <div class="model_2">
          <el-dialog title="提示" :visible.sync="visible" size="tiny">
            <hr style="margin-top: 15px;">
            <div>
              <img src="~assets/images/delect01.png"  class="fl">
              <span>盟主({{ unionMainData.ownerMember.enterpriseName }}) 将“{{ unionMainData.currentUnion.name }}”权限转移给您，
          </span>
              <p>请确认！</p>
            </div>
            <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm">确定</el-button>
            <el-button @click="reject">取消</el-button>
          </span>
          </el-dialog>
        </div>
      </div>
      <!-- 联盟基础信息 -->
      <div class="info">
        <ul class="gt_union">
          <li style="width:50%">
            <div class="clearfix" id="gt_lm">
              <img :src="unionMainData.currentUnion.img" class="fl unionImg">
              <ul class="fl">
                <li>
                  <span>联盟名称 :</span> {{ unionMainData.currentUnion.name }}</li>
                <li>
                  <span>盟&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;主 :</span> {{ unionMainData.ownerMember.enterpriseName }}</li>
                <li>
                  <span>联盟说明 :</span> {{ unionMainData.currentUnion.illustration }}</li>
                <li>
                  <span>创建时间 :</span> {{ unionMainData.currentUnion.createTime }}</li>
              </ul>
            </div>
          </li>
          <li>
            <div class="gt_right" id="gt_rt">
              <ul>
                <li>
                  <span>企业名称 :</span> {{ unionMainData.currentMember.enterpriseName }}
                </li>
                <li>
                  <span>职&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;位 :</span>
                  {{ unionMainData.currentMember.isUnionOwner }}
                </li>
                <li>
                  <span>已加入盟员数</span> {{ unionMainData.memberCount }}
                  <span>,剩余盟员数</span> {{ unionMainData.memberSurplus }}
                </li>
              </ul>
            </div>
          </li>
        </ul>
        <div id="union_people">
          <ul class="clearfix">
            <li>
              <p>联盟成员</p>
              <span> {{ unionMainData.memberCount }} </span>人
            </li>
            <li v-if="unionMainData.currentUnion.isIntegral">
              <p>联盟积分</p>
              <span> {{ unionMainData.integral }} </span>积分
            </li>
          </ul>
        </div>
      </div>
      <!--列表数据-->
      <div class="list">
        <el-tabs v-model="activeName">
          <el-tab-pane label="盟员列表" name="first">
            <union-member></union-member>
          </el-tab-pane>
          <el-tab-pane label="联盟卡" name="second">
            <union-card></union-card>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import Join from '@/components/my-union/join-union/Join';
import Create from '@/components/my-union/create-union/Create';
import UnionMember from '@/components/my-union/my-union-index/UnionMember';
import UnionCard from '@/components/my-union/my-union-index/UnionCard';
import UnionNotice from '@/components/my-union/UnionNotice';
import $http from '@/utils/http.js';
import $todate from '@/utils/todate.js';
export default {
  name: 'my-union',
  components: {
    Join,
    Create,
    UnionMember,
    UnionCard,
    UnionNotice
  },
  data() {
    return {
      unionMainData: {
        myCreateUnion: {},
        myJoinUnionList: [],
        currentUnion: {},
        currentMember: {},
        ownerMember: {},
        unionTransfer: {},
        integral: '',
        memberCount: '',
        memberSurplus: ''
      },
      activeName: 'first',
      unionListLength: '',
      visible: false,
      fullscreenLoading: true
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('unionUpdata', () => {
      if (this.unionMainData.currentUnion) {
        $http
          .get(`/unionIndex?unionId=${this.unionMainData.currentUnion.id}`)
          .then(res => {
            if (res.data.data) {
              // todo
              // 更新联盟基础信息
              // 处理当前页面数据展示格式
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    });
  },
  methods: {
    init() {
      // 清空缓存的数据
      this.$store.commit('unionIdChange', '');
      this.$store.commit('isUnionOwnerChange', '');
      // 首页查询我的联盟信息
      $http
        .get(`/unionIndex`)
        .then(res => {
          if (res.data.data) {
            setTimeout(() => {
              this.fullscreenLoading = false;
              this.unionMainData = res.data.data;
              // 判断是否创建或加入联盟
              if (!this.unionMainData.currentUnion.id) {
                this.$router.push({ path: '/my-union/no-currentUnion' });
              } else {
                // 判断创建和加入联盟的数量
                this.unionMainData.myCreateUnion.id ? (this.unionListLength = 1) : (this.unionListLength = 0);
                if (this.unionMainData.myJoinUnionList) {
                  this.unionListLength += this.unionMainData.myJoinUnionList.length;
                }
                // 全局存储信息
                this.$store.commit('unionIdChange', this.unionMainData.currentUnion.id);
                this.$store.commit('isUnionOwnerChange', this.unionMainData.currentMember.isUnionOwner);
                // 处理当前页面数据展示格式
                this.unionMainData.currentUnion.createTime = $todate.todate(
                  new Date(this.unionMainData.currentUnion.createTime)
                );
                this.unionMainData.currentMember.isUnionOwner
                  ? (this.unionMainData.currentMember.isUnionOwner = '盟主')
                  : (this.unionMainData.currentMember.isUnionOwner = '盟员');
                this.unionMainData.integral = res.data.data.integral || 0;
              }
              container_myUnion.style.display = 'block';
            }, 300);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 点击联盟图片切换联盟
    changUnion(id) {
      if (id !== this.unionMainData.currentUnion.id) {
        this.unionMainData.currentUnion.id = id;
        $http
          .get(`/unionIndex?unionId=${this.unionMainData.currentUnion.id}`)
          .then(res => {
            if (res.data.data) {
              this.unionMainData = res.data.data;
              // 全局存储信息
              this.$store.commit('unionIdChange', this.unionMainData.currentUnion.id);
              this.$store.commit('isUnionOwnerChange', this.unionMainData.currentMember.isUnionOwner);
              // 处理当前页面数据展示格式
              this.unionMainData.currentUnion.createTime = $todate.todate(
                new Date(this.unionMainData.currentUnion.createTime)
              );
              this.unionMainData.currentMember.isUnionOwner == 1
                ? (this.unionMainData.currentMember.isUnionOwner = '盟主')
                : (this.unionMainData.currentMember.isUnionOwner = '盟员');
              this.unionMainData.integral = res.data.data.integral || 0;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    // 盟主权限转移
    transfer() {
      this.visible = true;
    },
    confirm() {
      $http
        .put(`/unionMainTransfer/${this.unionMainData.unionTransfer.id}/unionId/${this.unionId}?isAccept=1`)
        .then(res => {
          if (res.data.success) {
            this.$message({ showClose: true, message: '接受成功', type: 'success', duration: 5000 });
            this.init();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    reject() {
      $http
        .put(`/unionMainTransfer/${this.unionMainData.unionTransfer.id}/unionId/${this.unionId}?isAccept=0`)
        .then(res => {
          if (res.data.success) {
            this.$message({ showClose: true, message: '拒绝成功', type: 'success', duration: 5000 });
            this.init();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>

<style scoped lang='less' rel="stylesheet/less">
.container {
  margin: 41px 119px 10px 56px;
}
/*创建和加入联盟---------------------------------*/
.notice {
  min-height: 100px;
  margin: 0 0 14px 0;
  .grid-content {
    font-family: 'Microsoft YaHei';
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 13px;
  }
  .el_btn {
    .el-button {
      padding: 0;
      margin-right: 15px;
    }
  }
}

/*中间栏信息（红卡，售卡，入盟，联盟设置...）--------------------------*/
.nav .grid-content {
  background: #f8f8f8;
  line-height: 60px;
  margin: 2px;
  height: 60px;
  span {
    font-size: 14px;
    color: #333333;
  }
  a {
    display: inline-block;
    width: 100%;
    height: 100%;
    .el-button {
      margin-left: 20px;
    }
  }
  a:hover {
    transition: all 0.5s;
    background-color: #eee;
    > span {
      font-size: 16px;
      transition: all 0.5s;
    }
  }
}

/*谷通联盟样式--------------------------------*/
.info {
  margin-top: 14px;
  border: 1px solid #ddd;
  ul.gt_union {
    max-height: 120px;
    background: #f8f8f8;
    overflow: hidden;
    border-bottom: 1px solid #ddd;
    > li {
      float: left;
    }
  }

  #gt_rt {
    border-left: 1px solid #eeeeee;
    height: 80px;
    margin-top: 19px;
    padding-left: 19px;
    > ul > li {
      margin-bottom: 6px;
      font-size: 14px;
    }
    span {
      font-size: 14px;
      color: #a0a0a0;
    }
  }
}

#union_people {
  padding: 13px 0 17px;
  > ul > li {
    float: left;
    width: 32%;
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
  }
}

/*我的联盟公共图片的大小----------------------------------*/
.unionImg {
  width: 80px;
  height: 80px;
}

/*用户管理和表格*/
.list {
  margin-top: 30px;
}
</style>
