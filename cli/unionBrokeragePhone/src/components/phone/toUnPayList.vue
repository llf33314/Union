<template>
  <div id="toUnPayList" class="main">
    <div class="Orders">
      <div class="public_stylecss">
        <div class="clear wantMoney">
          <el-breadcrumb-item :to="{ path: '/index' }">
            <img class="back__" src="../../assets/images/back.png" alt="" style="width: 1rem;">
          </el-breadcrumb-item>
          <p class="fr union_head" @click="boxWarp">
            <img src="../../assets/images/switchover.png" alt="">
            <span class="unionName">全部</span>
          </p>
        </div>
        <div class="union_second payed">
          <p>{{totalMoney | filtration}}</p>
          <span>我未收佣金(元)</span>
        </div>
      </div>
      <p class="record">我未收记录</p>
      <div class="footer_Css">
        <ul >
          <li class="clear" v-for="(item, index) in unCommission">
            <div class="clear fl">
              <div class="fl">
                <p>{{item.union.name}}</p>
                <span style="color: #7e7e7e">{{item.opportunity.createTime}}</span>
              </div>
              <img src="../../assets/images/urge.png" @click="showModel_">
              <div class="box-wrap2" style="display: none">
                <div class="mask" @click="hide1_"></div>
                <div class="box">
                  <p>是否发送佣金支付短信提醒?</p>
                  <div class="fr" @click="send1_(item.opportunity.id)">发送</div>
                  <div style="border-left:none" @click="hide1_">取消</div>
                </div>
              </div>
            </div>
            <div class="clearfr">
              <div class="fr">
                <span>{{item.toMember.enterpriseName}}</span>
                <p class="" style="color: #5EB6FF;text-align: right;">{{item.opportunity.money}}</p>
              </div>
            </div>
          </li>
        </ul>
        <div class="loadMore hasPayLoadMore" @click="loadMore" >加载更多</div>
        <div class="nothing hasPayNothing" style="color:#868686;" >没有更多数据</div>
      </div>
      <!--多粉大联盟按钮弹框-->
      <div class="box-wrap" style="display: none">
      <div class="mask" @click="hide_"></div>
      <div class="box">
        <div id="triangle-up"></div>
        <ul>
          <li  @click="fullyLoaded">全部</li>
          <li v-for="(item, index) in unionList" @click="partLoaded(item.id,item.name)">{{item.name}}</li>
        </ul>
      </div>
    </div>
    </div>
    <div class="supportIcon">
      <img src="../../assets/images/supprot-black.png" alt="">
    </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  import $todate from '@/utils/todate.js'
  export default {
    name: 'toUnPayList',
    data() {
      return {
        //未收佣金列表数据
        unCommission:[],
        //盟员列表
        unionList:[],
        //未收佣金的总金额
        totalMoney:0,
        //显示的列表条数
        size:6,
        //当前列表第一页
        current:1,
        //当前的联盟id号码
        unionId:'',
      }
    },
    methods: {
      boxWarp(){
        //点击按钮弹出框显示/隐藏
        $('.box-wrap').show();
      },
      hide_(){
        $('.box-wrap').hide();
      },
      showModel_(){
        $('.box-wrap2').show();
      },
      hide1_(){
        $('.box-wrap2').hide();
      },
      //催促佣金
      send1_(did){
        $('.box-wrap2').hide();
        $http.post(`/h5Brokerage/unreceived/urge?opportunityId=${did}`)
          .then(res => {
            console.log(res.data);
            if(res.data.success){
                this.$message({
                  showClose: true,
                  message: "已催促",
                  type: 'success',
                  duration: 3500
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
      //点击全部联盟
      fullyLoaded(){
        document.querySelector('.unionName').innerHTML='全部';
        //页面的请求
        let data={
          size:this.size=6,
          current:this.current=1
        }
        $http.get(`/h5Brokerage/unreceived/page`,data)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                $('.hasPayNothing').show();
                $('.hasPayLoadMore').hide();
                this.unCommission = [];
                this, totalMoney = 0;
              }
              else if (res.data.data.records.length < this.size) {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').hide();
              } else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').show();
              }
              this.unCommission = res.data.data.records;
              this.unCommission.forEach((v, i) => {
                v.opportunity.createTime = $todate.todate(new Date(v.opportunity.createTime));
              })
              //总金额
              $http.get(`/h5Brokerage/unreceived/sum`)
                .then(res => {
                  if(res.data.data) {
                    this.totalMoney = res.data.data;
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 3000});
                });
              $('.box-wrap').hide();
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
      //点击部分联盟
      partLoaded(did,uname){
        $('.box-wrap').hide();
        //点击时把联盟id放到公共的地方
        this.unionId=did;
        document.querySelector('.unionName').innerHTML=uname;
        //点击未支付联盟的对应id的页面
        let data1={
          size:this.size=6,
          current:this.current=1,
          unionId:did
        };
        $http.get(`/h5Brokerage/unreceived/page`,data1)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                this.totalMoney = 0;
                this.unCommission = [];
                $('.hasPayNothing').show();
                $('.hasPayLoadMore').hide();
                return;
              } else if (res.data.data.records.length < this.size) {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').hide();
              }
              else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').show();
              }
              this.unCommission = res.data.data.records;
              this.unCommission.forEach((v, i) => {
                v.opportunity.createTime = $todate.todate(new Date(v.opportunity.createTime));
              })
              //获取该联盟id对应的总金额
              $http.get(`/h5Brokerage/unreceived/sum`,{unionId:did})
                .then(res => {
                  if(res.data.data) {
                    this.totalMoney = res.data.data;
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 3000});
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
      //加载更多列表数据
      loadMore(){
        console.log(++this.current);
        let data1={
          size:this.size=6,
          current:this.current,
          unionId:this.unionId
        }
        $http.get(`/h5Brokerage/unreceived/page`,data1)
          .then(res => {
            if(res.data.data) {
              let list = res.data.data.records;
              if (list.length < this.size) {
                $('.hasPayNothing').show();
                $('.hasPayLoadMore').hide();
              } else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').show();
              }
              this.unCommission = this.unCommission.concat(list);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
    },
  //过滤器
    filters:{
      filtration:function(value){
        return value = value.toFixed(2);
      }
    },
    created (){
      //页面的title变换
      $("#title_").text('我未收佣金');
      //页面加载前获取数据列表---------------------------------------------------------------1
      let data={
        size:this.size,
        current:this.current
      }
      $http.get(`/h5Brokerage/unreceived/page`,data)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              this.totalMoney = 0;
              this.unCommission = [];
              $('.hasPayNothing').show();
              $('.hasPayLoadMore').hide();
            } else if (res.data.data.records.length < this.size) {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore').hide();
            }
            else {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore').show();
            }
            this.unCommission = res.data.data.records;
            this.unCommission.forEach((v, i) => {
              v.opportunity.createTime = $todate.todate(new Date(v.opportunity.createTime));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      //获取盟员列表的数据----------------------------------------------------------------2
      $http.get(`/h5Brokerage/myUnion`)
        .then(res => {
          if(res.data.data) {
            this.unionList = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      //未收取佣金的总金额----------------------------------------------------------------3
      $http.get(`/h5Brokerage/unreceived/sum`)
        .then(res => {
          if(res.data.data) {
            this.totalMoney = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>
  .hasPayLoadMore,.hasPayNothing{
    font-size: 0.85rem;
    color: #39ACFF;
    text-align: center;
    padding: 0.3rem;
  }
</style>
