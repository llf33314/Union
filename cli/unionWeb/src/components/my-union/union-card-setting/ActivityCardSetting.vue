<template>
  <div id="ActivityCardSetting"><!-- 活动卡售卡比例设置-->
    <!--说明部分-->
    <div class="explain">
      <span>说明：</span>
      <p>活动卡由盟主创建，以活动的形式发布于联盟，盟员可以报名参加活动，将各自的服务项目添加至活动卡中，联盟会员可享受各个商家的优惠项目。</p>
      <el-button type="primary" @click="visible1=true"  v-if="isUnionOwner">新增活动卡</el-button>
    </div>
    <!--没有相关数据-->
    <div id="noUnion" v-if="!tableData.length">
      <img src="~assets/images/noCurrent.png">
      <p>
        还没有相关数据
      </p>
    </div>
    <!-- 活动卡列表 -->
    <div class="activityCardsList" v-for="item in tableData" :key="item.activity.id">
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
          <img :src="item.activity.img">
          <div>
            <span>{{ item.activityStatus }}</span>
          </div>
        </li>
        <li>
          <p>
            <span>项目报名时间</span>
            <span style="margin-left: 45px; ">{{ item.activity.applyBeginTime }}</span>
          </p>
          <p>
            <span>活动卡有效天数</span>
            <span style="margin-left: 30px;">{{ item.activity.validDay }}</span>
          </p>
          <p>
            <span>活动卡对外售价</span>
            <span style="color: #ff4949;margin-left: 28px;">￥{{ item.activity.price }}</span>
          </p>
          <p>
            <span>活动卡发行量</span>
            <span style="margin-left: 45px;">{{ item.activity.amount }}</span>
          </p>
          <p>
            <span>活动卡说明</span>
            <span style="margin-left: 62px;">{{ item.activity.illustrate }}</span>
          </p>
        </li>
        <li>
          <p>
            <span>参与盟员数</span>
            <span @click="showJoinMembers" style="color:#20a0ff; cursor:pointer">{{ item.joinMemberCount }}</span>
          </p>
          <p>
            <span>已售活动卡</span>
            <span>{{ item.cardSellCount }}/{{ item.activity.amount }}</span>
          </p>
          <el-progress :text-inside="true" :stroke-width="20"
                       :percentage="item.cardSellCount/item.activity.amount"
                       status="success">
          </el-progress>
        </li>
        <li>
          <el-button>报名参加</el-button>
          <div class="btn">
            <el-button >我的活动项目</el-button></div>
          <el-button v-if="isUnionOwner">删除</el-button>
        </li>
      </ul>
    </div>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 新建活动卡 -->
    <div class="NewActivityCard">
      <el-dialog title="新建活动卡" :visible.sync="visible1"  @close="resetData1">
        <hr>
        <el-form ref="form" :model="form" label-width="100px">
          <el-form-item label="活动卡名称：">
            <el-input v-model="form.name" placeholder="请输入活动卡名称"></el-input>
          </el-form-item>
          <el-form-item label="价格：">
            <el-input v-model="form.price" placeholder="请输入活动卡价格">
              <template slot="prepend">￥</template>
            </el-input>
          </el-form-item>
          <el-form-item label="展示图：" style="height: 80px;">
            <!--<el-input v-model="form.img"></el-input>-->
            <!--<img  :src="this.form.img" class="unionImg">-->
            <el-button  v-if="!this.form.unionImg" @click="cardCss">
              <i class="el-icon-plus"></i>
            </el-button>
            <span>图片建议尺寸5:3</span>
          </el-form-item>
          <el-form-item label="发行量：">
            <el-input v-model="form.amount" placeholder="请输入活动卡发行量"></el-input>
            <span>发行活动卡的最大数量</span>
          </el-form-item>
          <el-form-item label="有效天数：">
            <el-input v-model="form.validDay" placeholder="请输入活动卡有效天数"></el-input>
            <span>粉丝办理活动卡后，可使用的有效天数</span>
          </el-form-item>
          <el-form-item label="报名时间：">
            <el-date-picker v-model="form.applyTime" type="daterange" placeholder="请选择项目报名时间">
            </el-date-picker>
            <span>项目报名时间段中，盟员可添加各自的项目至活动卡</span>
          </el-form-item>
          <el-form-item label="售卖时间：">
            <el-date-picker v-model="form.sellTime" type="daterange" placeholder="请选择活动卡售卖时间">
            </el-date-picker>
            <span>活动卡售卖时间段，粉丝可购买活动卡享受联盟内项目</span>
          </el-form-item>
          <el-form-item label="活动卡说明：">
            <el-input type="textarea" v-model="form.illustrate"></el-input>
          </el-form-item>
          <el-form-item label="项目审核：">
            <el-switch v-model="form.isProjectCheck" on-text="" off-text="" @change="ProjectReview">
            </el-switch>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="activityCardConfirm">确定</el-button>
          <el-button @click="visible1 = false">取消</el-button>
        </span>
      </el-dialog>
    </div>
    <!-- 弹出框 参与盟员数 -->
    <div class="membership">
      <el-dialog title="参与盟员数" :visible.sync="visible2">
        <hr>
        <el-table :data="joinMembersData" style="width: 100%">
          <el-table-column label="盟员名称">
          </el-table-column>
          <el-table-column label="活动卡项目名称">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="bottom-start">
                <p>活动卡名称1，数量1</p>
                <p>活动卡名称2，数量2</p>
                <div slot="reference" class="name-wrapper">
                  <span>活动卡名称1，活动卡名称2</span>
                </div>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
    <!--选择活动卡的图样式弹出框-->
    <div class="SelectCard">
      <el-dialog title="提示" :visible.sync="visible3" size="tiny">
        <hr>
        <div class="step2">
          <el-radio-group v-model="radio3">
            <el-radio-button label="上海">
              <div class="dddddd clearfix">
                <img src="assets/images/noCurrent.png" alt="" class="fl" style="width: 293px;height: 117px;">
                <i></i>
              </div>
            </el-radio-button>
            <el-radio-button label="北京">
              <div class="dddddd clearfix">
                <img src="assets/images/noCurrent.png" alt="" class="fl" style="width: 293px;height: 117px;">
                <i></i>
              </div>
            </el-radio-button>
            <el-radio-button label="广州"></el-radio-button>
            <el-radio-button label="深圳"></el-radio-button>
          </el-radio-group>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="visible3 = false">确 定</el-button>
          <el-button @click="visible3 = false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
    <!--参与盟员数的弹出框-->
    <div class="membership">
      <el-dialog title="参与盟员数" :visible.sync="visible4">
        <hr>
        <p> 温馨提示：盟员的服务项目审核通过后，请及时到售卡分成管理设置佣金比例</p>
        <el-table :data="tableData3" style="width: 100%">
          <el-table-column
            type="selection"
            width="38">
          </el-table-column>
          <el-table-column label="盟员名称">
          </el-table-column>
          <el-table-column label="卡项目名称">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="bottom-start">
                <p>活动卡名称1，数量1</p>
                <p>活动卡名称2，数量2</p>
                <div slot="reference" class="name-wrapper">
                  <span>活动卡名称1，活动卡名称2</span>
                </div>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
        <div >
          <el-button @click="pass">通过</el-button>
          <el-button @click="noPass">不通过</el-button>
        </div>
      </el-dialog>
    </div>
    <!--参与盟员数  通过的弹出框-->
    <div class="model_2 pass">
      <el-dialog title="通过" :visible.sync="visible5" size="tiny">
        <hr>
        <div>
          <img src="~assets/images/delect01.png"  class="fl">
          <span>确定通过盟员的项目?</span>
          <p>点击确定后，盟员的项目立即生效，该操作不可撤回。</p>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="visible5=false">确定</el-button>
            <el-button @click="visible5=false">取消</el-button>
          </span>
      </el-dialog>
    </div>
    <!--参与盟员数  不通过的弹出框-->
    <div class="noPass">
      <el-dialog title="不通过" :visible.sync="visible6" size="tiny">
        <hr>
        <div>
          <span>活动卡说明:</span>
          <el-input type="textarea" :rows="2" placeholder="请输入内容"
            v-model="textarea">
          </el-input>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="visible6 = false">确 定</el-button>
          <el-button @click="visible6 = false">取 消</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'ActivityCardSetting',
  data() {
    return {
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      visible1: false,
      form: {
        isProjectCheck:false
      },
      visible2: false,
      visible3: false,
      visible4: false,
      visible5: false,
      visible6: false,
      joinMembersData: [{}],
      radio3: '上海',
      tableData3:[{}],
      textarea: ''
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
  },
  methods: {
    init() {
      $http
        .get(`/unionCardActivity/unionId/${this.unionId}/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
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
    handleCurrentChange() {},
    // 确定新建活动卡
    activityCardConfirm() {},
    // 清空新建活动卡数据
    resetData1() {},
    // 弹出框 参与盟员数
    showJoinMembers() {
      this.visible2 = true;
    },
    //选择活动卡的图样式弹出框
    cardCss(){
      this.visible3=true;
    },
    //项目审核
    ProjectReview(){
      if(this.form.isProjectCheck) {
        this.visible4=true;
      }
      else{
        this.visible4=false;
          this.form.isProjectCheck=false;
      }
    },
    //通过按钮
    pass(){
      this.visible5=true
    },
    //不通过按钮
    noPass(){
      this.visible6=true
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
