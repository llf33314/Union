<template>
  <div id="myUnionCard1">
    <el-row class="user_search">
      <el-col style="width:120px;">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col style="width:190px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" icon="search" @keyup.enter.native="search" v-model="input" :on-icon-click="search" class="input-search fl">
          </el-input>
        </div>
      </el-col>
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;padding-left: 20px">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <!-- 联盟卡 表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="fan.number" label="联盟卡号">
      </el-table-column>
      <el-table-column prop="fan.phone" label="手机号">
      </el-table-column>
      <el-table-column prop="integral" label="联盟积分">
      </el-table-column>
      <el-table-column prop="" label="操作" width="200">
        <template slot-scope="scope">
          <el-button @click="showDetail(scope)" size="small">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper"
      :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 详情 -->
    <el-dialog title="联盟卡详情" :visible.sync="visible" size="tiny">
      <nav>顾客已拥有联盟卡：</nav>
      <main class="unionCardDetails">
        <!-- 左侧联盟卡 -->
        <el-radio-group v-model="unionCardId">
          <el-radio-button v-if="detailData.disCountCard" :key="detailData.disCountCard.card.id" :label="detailData.disCountCard.card.id">
            <div class="UnionDiscountCard"></div>
            <div>{{detailData.disCountCard.card.name}}</div>
          </el-radio-button>
          <el-radio-button v-if="detailData.activityCardList" v-for="item in detailData.activityCardList" :key="item.card.id" :label="item.card.id">
            <div class="UnionDiscountCard"></div>
            <div>{{item.card.name}}fsdfdf</div>
            <img class="outOfDate" src="~assets/images/outOfDate02.png" v-if="item.isExpired">
          </el-radio-button>
        </el-radio-group>
        <!-- 右侧联盟卡详情 -->
        <div class="unionCardDetailsRight">
          <!--折扣卡详情-->
          <div v-if="detailData.disCountCard" v-show="unionCardId===detailData.disCountCard.card.id">
            <p class="DetailsName">{{detailData.disCountCard.card.name}}</p>
            <p class="DetailsTime">办卡时间 {{detailData.disCountCard.createTime}}</p>
            <p>消费特权：可在下列商家消费时享受折扣</p>
            <ol>
              <li v-for="item in detailData.disCountCard.memberList" :key="item.id" :label="item.id">
                <span>{{item.enterpriseName}}</span>
                <span v-if="item.discount">{{(item.discount*10).toFixed(1)}}折</span>
                <span v-else> 无折扣</span>
              </li>
            </ol>
          </div>
          <!--活动卡详情-->
          <div v-if="detailData.activityCardList" v-for="item in detailData.activityCardList" :key="item.card.id" :label="item.card.id"
          v-show="unionCardId === item.card.id">
            <p class="DetailsName">{{item.card.name}}</p>
            <p class="DetailsTime">办卡时间： {{item.card.createTime}}</p>
            <p class="DetailsTime">有效时间： {{item.card.validity}}</p>
            <p class="DetailsItems">优惠项目： 共{{item.projectItemCount}}项</p>
            <ol>
              <li v-for="(item1,index) in item.cardProjectList" :key="item1.member.id" :label="item1.member.id">
                <span>{{index+1+'. '}}{{item1.member.enterpriseName}}</span>
                <ul class="companyName">
                  <li v-for="item2 in item1.projectItemList" :key="item2.id" :label="item2.id">
                    <i class="circle"></i>
                    <span>{{item2.name}}</span>
                    <!-- todo * 样式更换 -->
                    <span>{{item2.number}}</span>
                  </li>
                </ul>
              </li>
            </ol>
          </div>
        </div>
      </main>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'union-card',
  data() {
    return {
      value: 'number',
      options: [
        {
          value: 'number',
          label: '联盟卡号'
        },
        {
          value: 'phone',
          label: '联系电话'
        }
      ],
      input: '',
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      visible: false,
      unionCardId: '',
      detailData: {
        discountCard: {},
        discount: ''
      }
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    if (this.unionId) {
      this.init();
    }
  },
  activated: function() {
    if (this.unionId) {
      this.init();
    }
  },
  watch: {
    unionId: function() {
      if (this.unionId) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      this.currentPage = 1;
      this.value = 'number';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(`/unionCardFan/page?current=${this.currentPage}&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({
            showClose: true,
            message: '网络错误',
            type: 'error',
            duration: 3000
          });
        });
    },
    // 带条件查询联盟卡
    search() {
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页查询联盟卡
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 导出联盟卡
    output() {
      let url =
        this.$store.state.baseUrl + `/unionCardFan/export?unionId=${this.unionId}&` + this.value + '=' + this.input;
      window.open(url);
    },
    // 弹出框 详情
    showDetail(scope) {
      $http
        .get(`/unionCardFan/${scope.row.fan.id}/detail?unionId=${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.detailData = res.data.data;
            this.visible = true;
            if (this.detailData.discountCard) {
              this.detailData.discountCard.card.createTime = timeFilter(this.detailData.discountCard.createTime);
            }
            if (this.detailData.activityCardList) {
              this.detailData.activityCardList.forEach((v, i) => {
                v.card.createTime = timeFilter(v.card.createTime);
                v.card.validity = timeFilter(v.card.validity);
              });
            }
          }
        })
        .catch(err => {
          this.$message({
            showClose: true,
            message: '网络错误',
            type: 'error',
            duration: 3000
          });
        });
    }
  }
};
</script>
<style scoped lang='less' rel="stylesheet/less">
  /*滚动条样式*/
  .unionCardDetailsRight>div::-webkit-scrollbar {/*滚动条整体样式*/
    width: 4px;     /*高宽分别对应横竖滚动条的尺寸*/
    height: 4px;
  }
  .unionCardDetailsRight>div::-webkit-scrollbar-thumb {/*滚动条里面小方块*/
    border-radius: 5px;
    -webkit-box-shadow: inset 0 0 5px rgba(0,0,0,0.2);
    background: rgba(0,0,0,0.2);
  }
  .unionCardDetailsRight>div::-webkit-scrollbar-track {/*滚动条里面轨道*/
    -webkit-box-shadow: inset 0 0 5px rgba(0,0,0,.2);
  }
</style>
