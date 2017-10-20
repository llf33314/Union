<template>
  <div class="container" id="myMainUnion">
    <!--创建和加入联盟-->
    <div class="notice">
      <el-row justify="center">
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple notice-list">我创建的联盟</div>
          <div class="el_btn">
            <el-tooltip content="联盟名(等等修改).." placement="bottom">
              <el-button class="fl" v-if="unionMainData.myCreateUnionImg" @click="changUnion1(unionMainData.myCreateUnionMemberId)">
                <img v-bind:src="unionMainData.myCreateUnionImg" alt="" class="fl unionImg">
              </el-button>
            </el-tooltip>
          </div>
          <create v-if="!unionMainData.myCreateUnionId && unionListLength < 3"></create>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <div class="grid-content bg-purple notice-list">我加入的联盟</div>
          <div class="el_btn">
            <el-tooltip content="联盟名(等等修改).." placement="bottom">
              <el-button v-for="item in unionMainData.myJoinUnionList" :key="item.myJoinUnionMemberId" class="fl" @click="changUnion2(item.myJoinUnionMemberId)">
                <img v-bind:src="item.myJoinUnionImg" alt="" class="fl unionImg">
              </el-button>
            </el-tooltip>
            <join v-if="unionListLength < 3"></join>
          </div>
        </el-col>
        <el-col :xs="8" :sm="8" :md="8" :lg="8">
          <union-notice></union-notice>
        </el-col>
      </el-row>
    </div>
    <!--中间栏信息（红卡，售卡，入盟，联盟设置...）-->
    <div class="nav clearfix">
      <el-col :xs="8" :sm="8" :md="8" :lg="8">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-setting', name: 'UnionSetting'}" v-if="isUnionOwner">
            <el-button type="success" size="mini">
              <img src="../../assets/images/icon01.png" style="width: 11px;">
            </el-button>
            <span>联盟设置</span>
          </router-link>
          <router-link :to="{ path: '/my-union/union-setting', name: 'UnionSetting'}" v-if="!isUnionOwner">
            <el-button type="success" size="mini">
              <img src="../../assets/images/icon01.png" style="width: 11px;">
            </el-button>
            <span>基础设置</span>
          </router-link>
        </div>
      </el-col>
      <el-col :xs="8" :sm="8" :md="8" :lg="8" v-if="isUnionOwner">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-check', name: 'UnionCheck'}">
            <el-button type="info" size="mini">
              <img src="../../assets/images/icon02.png" style="width: 11px;">
            </el-button>
            <span>入盟审核</span>
          </router-link>
        </div>
      </el-col>
      <el-col :xs="8" :sm="8" :md="8" :lg="8">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-recommend', name: 'UnionRecommend'}">
            <el-button type="danger" size="mini">
              <img src="../../assets/images/icon03.png" >
            </el-button>
            <span>推荐入盟</span>
          </router-link>
        </div>
      </el-col>
      <el-col :xs="8" :sm="8" :md="8" :lg="8">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-percent', name: 'UnionPercent'}">
            <el-button type="warning" size="mini">
              <img src="../../assets/images/icon04.png" style="width: 11px;">
            </el-button>
            <span>售卡分成管理</span>
          </router-link>
        </div>
      </el-col>
      <el-col :xs="8" :sm="8" :md="8" :lg="8">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-discount', name: 'UnionDiscount'}">
            <el-button type="success" size="mini">
              <img src="../../assets/images/icon05.png" style="width: 11px;">
            </el-button>
            <span>红卡优惠设置</span>
          </router-link>
        </div>
      </el-col>
      <el-col :xs="8" :sm="8" :md="8" :lg="8">
        <div class="grid-content bg-purple nav-list">
          <router-link :to="{ path: '/my-union/union-quit', name: 'UnionQuit'}">
            <el-button type="danger" size="mini">
              <img src="../../assets/images/icon06.png" alt="">
            </el-button>
            <span>退盟管理</span>
          </router-link>
        </div>
      </el-col>
    </div>
    <!-- 联盟基础信息 -->
    <div class="info">
      <ul class="gt_union">
        <li style="width:50%">
          <div class="clearfix" id="gt_lm">
            <img :src="unionMainData.currentUnionImg" class="fl unionImg">
            <ul class="fl">
              <li>
                <span>联盟名称 :</span> {{ unionMainData.currentUnionName }}</li>
              <li>
                <span>盟&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;主 :</span> {{ unionMainData.currentUnionOwnerEnterpriseName }}</li>
              <li>
                <span>联盟说明 :</span> {{ unionMainData.currentUnionIllustration }}</li>
              <li>
                <span>创建时间 :</span> {{ unionMainData.currentUnionCreatetime }}</li>
            </ul>
          </div>
        </li>
        <li>
          <div class="gt_right" id="gt_rt">
            <ul>
              <li>
                <span>企业名称 :</span> {{ unionMainData.currentUnionMemberEnterpriseName }}
              </li>
              <li>
                <span>职&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;位 :</span>
                {{ unionMainData.currentUnionMemberIsUnionOwner }}
              </li>
              <li>
                <span>已加入盟员数</span> {{ unionMainData.currentUnionMemberCount }}
                <span>,剩余盟员数</span> {{ unionMainData.currentUnionSurplusMemberCount }}
              </li>
            </ul>
          </div>
        </li>
      </ul>
      <div id="union_people">
        <ul class="clearfix">
          <li>
            <p>联盟成员</p>
            <span> {{ unionMainData.currentUnionMemberCount }} </span>人
          </li>
          <li v-if="unionMainData.currentUnionIsIntegral">
            <p>联盟积分</p>
            <span> {{ unionMainData.currentUnionIntegralSum }} </span>积分
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
</template>

<script>
import Join from './Join'
import Create from './Create'
import UnionMember from './UnionMember'
import UnionCard from './UnionCard'
import UnionNotice from './UnionNotice'
import $http from '@/utils/http.js'
import $todate from '@/utils/todate.js'
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
        myJoinUnionList: [],
        myCreateUnionImg: '',
        myCreateUnionMemberId: '',
        myCreateUnionId: '',
        currentUnionImg: '',
        currentUnionName: '',
        currentUnionOwnerEnterpriseName: '',
        currentUnionIllustration: '',
        currentUnionCreatetime: '',
        currentUnionMemberEnterpriseName: '',
        currentUnionMemberIsUnionOwner: '',
        currentUnionMemberCount: '',
        currentUnionSurplusMemberCount: '',
        currentUnionIsIntegral: '',
        currentUnionIntegralSum: 0,
      },
      activeName: 'first',
      memberId: '',
      unionListLength: '',
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  created: function() {
    // 首页查询我的联盟信息
    $http.get(`/union/index`)
      .then(res => {
        if (res.data.data) {
          this.unionMainData = res.data.data;
          // 判断是否创建或加入联盟
          if (!this.unionMainData.currentUnionId) {
            this.$router.push({ path: '/my-union/no-currentUnion' });
          } else {
            // 判断创建和加入联盟的数量
            this.unionMainData.myCreateUnionId ? this.unionListLength = 1 : this.unionListLength = 0;
            if (this.unionMainData.myJoinUnionList) {
              this.unionListLength += this.unionMainData.myJoinUnionList.length;
            }
            // 全局存储信息
            this.$store.commit('unionIdChange', this.unionMainData.currentUnionId);
            this.$store.commit('unionMemberIdChange', this.unionMainData.currentUnionMemberId);
            this.$store.commit('infoIdChange', this.unionMainData.infoId);
            this.$store.commit('busIdChange', this.unionMainData.busId);
            this.$store.commit('isUnionOwnerChange', this.unionMainData.currentUnionMemberIsUnionOwner);
            // 处理当前页面数据展示格式
            this.unionMainData.currentUnionCreatetime = $todate.todate(new Date(this.unionMainData.currentUnionCreatetime));
            this.unionMainData.currentUnionMemberIsUnionOwner == 1 ? this.unionMainData.currentUnionMemberIsUnionOwner = '盟主' : this.unionMainData.currentUnionMemberIsUnionOwner = '盟员';
            this.unionMainData.currentUnionIntegralSum = res.data.data.currentUnionIntegralSum || 0;
          }
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 点击联盟图片切换联盟
    changUnion1(myCreateUnionMemberId) {
      if (myCreateUnionMemberId !== this.memberId) {
        this.memberId = myCreateUnionMemberId;
        $http.get(`/union/index/memberId/${myCreateUnionMemberId}`)
          .then(res => {
            if (res.data.data) {
              this.unionMainData = res.data.data;
              // 全局存储信息
              this.$store.commit('unionIdChange', this.unionMainData.currentUnionId);
              this.$store.commit('unionMemberIdChange', this.unionMainData.currentUnionMemberId);
              this.$store.commit('infoIdChange', this.unionMainData.infoId);
              this.$store.commit('busIdChange', this.unionMainData.busId);
              this.$store.commit('isUnionOwnerChange', this.unionMainData.currentUnionMemberIsUnionOwner);
              // 处理当前页面数据展示格式
              this.unionMainData.currentUnionCreatetime = $todate.todate(new Date(this.unionMainData.currentUnionCreatetime));
              this.unionMainData.currentUnionMemberIsUnionOwner == 1 ? this.unionMainData.currentUnionMemberIsUnionOwner = '盟主' : this.unionMainData.currentUnionMemberIsUnionOwner = '盟员';
              this.unionMainData.currentUnionIntegralSum = res.data.data.currentUnionIntegralSum || 0;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    changUnion2(myJoinUnionMemberId) {
      if (myJoinUnionMemberId !== this.memberId) {
        this.memberId = myJoinUnionMemberId;
        $http.get(`/union/index/memberId/${myJoinUnionMemberId}`)
          .then(res => {
            if (res.data.data) {
              this.unionMainData = res.data.data;
              // 全局存储信息
              this.$store.commit('unionIdChange', this.unionMainData.currentUnionId);
              this.$store.commit('unionMemberIdChange', this.unionMainData.currentUnionMemberId);
              this.$store.commit('infoIdChange', this.unionMainData.infoId);
              this.$store.commit('busIdChange', this.unionMainData.busId);
              this.$store.commit('isUnionOwnerChange', this.unionMainData.currentUnionMemberIsUnionOwner);
              // 处理当前页面数据展示格式
              this.unionMainData.currentUnionCreatetime = $todate.todate(new Date(this.unionMainData.currentUnionCreatetime));
              this.unionMainData.currentUnionMemberIsUnionOwner == 1 ? this.unionMainData.currentUnionMemberIsUnionOwner = '盟主' : this.unionMainData.currentUnionMemberIsUnionOwner = '盟员';
              this.unionMainData.currentUnionIntegralSum = res.data.data.currentUnionIntegralSum || 0;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    }
  },
}
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
    font-family: "Microsoft YaHei";
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
  background: #F8F8F8;
  line-height: 60px;
  margin: 2px;
  height: 60px;
  span {
    font-size: 14px;
    color: #333333;
  }
  a{
    display: inline-block;
    width: 100%;
    height: 100%;
    .el-button{
      margin-left: 20px;
    }
  }
  a:hover{
    transition: all .5s;
    background-color: #eee;
    >span{
      font-size: 16px;
      transition: all .5s;
    }
  }
}

/*谷通联盟样式--------------------------------*/
.info {
  margin-top: 14px;
  border: 1px solid #ddd;
  ul.gt_union {
    max-height: 120px;
    background: #F8F8F8;
    overflow: hidden;
    border-bottom: 1px solid #ddd;
    >li {
      float: left;
    }
  }

  #gt_rt {
    border-left: 1px solid #EEEEEE;
    height: 80px;
    margin-top: 19px;
    padding-left: 19px;
    >ul>li {
      margin-bottom: 6px;
      font-size: 14px;
    }
    span {
      font-size: 14px;
      color: #A0A0A0
    }
  }
}

#union_people {
  padding: 13px 0 17px;
  >ul>li {
    float: left;
    width: 32%;
    border-left: 1px solid #EEEEEE;
    padding-left: 22px;
    color: #999999;
    p {
      margin-bottom: 19px;
    }
    span {
      color: #FF6600;
      font-size: 25px;
      font-weight: bold;
      margin-right: 10px
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
  margin-top: 30px
}
</style>
