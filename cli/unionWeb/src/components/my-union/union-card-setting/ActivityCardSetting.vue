<template>
  <div id="ActivityCardSetting">
    <!-- 活动卡售卡比例设置-->
    <!--说明部分-->
    <div class="explain">
      <span>说明：</span>
      <p>活动卡由盟主创建，以活动的形式发布于联盟，盟员可以报名参加活动，将各自的服务项目添加至活动卡中，联盟会员可享受各个商家的优惠项目。</p>
      <add-activity-card></add-activity-card>
    </div>
    <!--没有相关数据-->
    <div id="noUnion" style="margin: 40px 0" v-if="!tableData.length">
      <img src="~assets/images/noCurrent.png">
      <p>
        还没有相关数据
      </p>
    </div>
    <!-- 活动卡列表 -->
    <div class="activityCardsList" v-for="(item,index1) in tableData" :key="item.activity.id">
      <!--表头-->
      <ul class="clearfix nav">
        <li>{{ item.activity.name }}</li>
        <li>活动卡规则</li>
        <li>活动卡概况</li>
        <li>操作</li>
      </ul>
      <!--表内容主体-->
      <ul class="clearfix contentList">
        <li>
          <!-- todo  更换样式-->
          <img :class="'m'+item.color2+index1" :src="item.activity.img">
          <div>
            <span>{{ item.activityStatus }}</span>
          </div>
        </li>
        <!-- 活动卡规则 -->
        <li>
          <p>
            <span>项目报名时间</span>
            <span style="margin-left: 45px; ">{{ item.activity.applyBeginTime }}</span>
          </p>
          <p>
            <span>活动卡有效天数</span>
            <span style="margin-left: 30px;">{{ item.activity.validityDay }} 天</span>
          </p>
          <p>
            <span>活动卡对外售价</span>
            <span style="color: #ff4949;margin-left: 28px;">￥{{ (item.activity.price.toFixed(2)) }}</span>
          </p>
          <p>
            <span>活动卡发行量</span>
            <span style="margin-left: 45px;">{{ item.activity.amount }}</span>
          </p>
          <p>
            <span>活动卡说明</span>
            <span style="margin-left: 62px;">{{ item.activity.illustration }}</span>
          </p>
        </li>
        <!-- 活动卡概况 -->
        <li>
          <p v-if="item.activityStatus !== '未开始'">
            <join-member :joinMemberCount="item.joinMemberCount" :activityId="item.activity.id"></join-member>
          </p>
          <check-activity v-if="isUnionOwner && (item.activityStatus === '报名中' || item.activityStatus === '报名结束')" :projectCheckCount="item.projectCheckCount" :activityId="item.activity.id"></check-activity>
          <p v-if="item.activityStatus === '售卡中' || item.activityStatus === '已停售'">
            <span>已售活动卡</span>
            <span>{{ item.cardSellCount }}/{{ item.activity.amount }}</span>
          </p>
          <el-progress v-if="item.activityStatus === '售卡中' || item.activityStatus === '已停售'" :text-inside="true" :stroke-width="20" :percentage="item.cardSellCount/item.activity.amount*100" status="success">
          </el-progress>
        </li>
        <!--  操作  -->
        <li>
          <div class="btn" v-if="item.activityStatus !== '未开始'">
            <el-button @click="myActivity(item)">我的活动项目</el-button>
          </div>
          <activity-delete v-if="isUnionOwner && (item.activityStatus !== '售卡中' && item.activityStatus !== '已停售')" :activityId="item.activity.id"></activity-delete>
        </li>
      </ul>
    </div>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>

  </div>
</template>

<script>
import AddActivityCard from '@/components/my-union/union-card-setting/AddActivityCard';
import CheckActivity from '@/components/my-union/union-card-setting/CheckActivity';
import JoinMember from '@/components/my-union/union-card-setting/JoinMember';
import ActivityDelete from '@/components/my-union/union-card-setting/ActivityDelete';
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
import { activityCardStatusFilter } from '@/utils/filter.js';
export default {
  name: 'ActivityCardSetting',
  components: {
    AddActivityCard,
    CheckActivity,
    JoinMember,
    ActivityDelete
  },
  data() {
    return {
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      form: {
        isProjectCheck: false
      }
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('newActivityCard', () => {
      this.init();
    });
    eventBus.$on('activityDelete', () => {
      this.init();
    });
  },
  methods: {
    init() {
      $http
        .get(`/unionCardActivity/unionId/${this.unionId}/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.activityStatus = activityCardStatusFilter(v.activityStatus);
              v.activity.applyBeginTime = timeFilter(v.activity.applyBeginTime);
              v.activity.applyEndTime = timeFilter(v.activity.applyEndTime);
              let color1 = (v.color1 = v.activity.color.split(',')[0]);
              let color2 = (v.color2 = v.activity.color.split(',')[1]);
              let mDiv = 'm' + color2 + i;
              // $("." + mDiv)[0].style.backgroundImage = `linear-gradient(90deg, #${color1} 0%, #${color2} 100%)`;
            });
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    handleCurrentChange(val) {
      $http
        .get(`/unionCardActivity/unionId/${this.unionId}/page?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.tableData.forEach((v, i) => {
              v.activityStatus = activityCardStatusFilter(v.activityStatus);
              v.activity.applyBeginTime = timeFilter(v.activity.applyBeginTime);
              v.activity.applyEndTime = timeFilter(v.activity.applyEndTime);
              let color1 = (v.color1 = v.activity.color.split(',')[0]);
              let color2 = (v.color2 = v.activity.color.split(',')[1]);
              let mDiv = 'm' + color2 + i;
              // $("." + mDiv)[0].style.backgroundImage = `linear-gradient(90deg, #${color1} 0%, #${color2} 100%)`;
            });
          } else {
            this.tableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 我的活动项目
    myActivity(item) {
      eventBus.$emit('myActivityAddTabs', item);
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.unionImg {
  width: 220px;
  height: 72px;
}
</style>
