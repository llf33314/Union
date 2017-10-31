<template>
  <div id="toUnPayList">
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
        <p>{{totalMoney.toFixed(2)}}</p>
        <span>我未收佣金(元)</span>
      </div>
    </div>
    <p class="record">我未收记录</p>
    <div class="footer_Css">
      <ul >
        <li class="clear" v-for="(item, index) in unCommission">
          <div class="clear fl">
            <div class="fl">
              <p>{{item.unionName}}</p>
              <span style="color: #7e7e7e">{{item.time}}</span>
            </div>
            <img src="../../assets/images/urge.png" @click="showModel_">
            <div class="box-wrap2" style="display: none">
              <div class="mask" @click="hide1_"></div>
              <div class="box">
                <p>是否发送佣金支付短信提醒?</p>
                <div class="fr" @click="send1_(item.id)">发送</div>
                <div style="border-left:none" @click="hide1_">取消</div>
              </div>
            </div>
          </div>
          <div class="clearfr">
            <div class="fr">
              <span>{{item.name}}</span>
              <p class="" style="color: #5EB6FF;text-align: right;">{{item.money.toFixed(2)}}</p>
            </div>
          </div>
        </li>
      </ul>
      <div class="loadMore hasPayLoadMore" @click="loadMore" >加载更多</div>
      <div class="nothing hasPayNothing" style="display:none;color:#868686;" >没有更多数据</div>
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
</template>

<script>
  import $http from '@/utils/http.js'
  import $todate from '@/utils/todate.js'
  export default {
    name: 'toUnPayList',
    data() {
      return {
//        底部颜色切换
        toLogin: 'ceshi1',
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
      send1_(id){
        $('.box-wrap2').hide();
        let did=id;
        $http.post(`/unionH5Brokerage/urge/${did}`)
          .then(res => {
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
        this.current=1;
        document.querySelector('.unionName').innerHTML='全部';
        //页面的请求
        $http.get(`unionH5Brokerage/unCome/list?size=${this.size}&current=${this.current}`)
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
                v.time = $todate.todate(new Date(v.time));
              });
              //总金额
              $http.get(`/unionH5Brokerage/unComeSum`)
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
        this.current=1;
        let Uid=did;
        //点击时把联盟id放到公共的地方
        this.unionId=did;
        document.querySelector('.unionName').innerHTML=uname;
        //点击未支付联盟的对应id的页面
        $http.get(`/unionH5Brokerage/unCome/list?size=${this.size}&current=${this.current}&unionId=${Uid}`)
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
                v.time = $todate.todate(new Date(v.time));
              })
              //获取该联盟id对应的总金额
              $http.get(`/unionH5Brokerage/unComeSum?unionId=${Uid}`)
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
        let uid=this.unionId;
        console.log(++this.current);
        $http.get(`/unionH5Brokerage/unCome/list?size=${this.size}&current=${this.current}&unionId=${uid}`)
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
              this.unCommission.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
    },
    created (){
      //页面的title变换
      $("#title_").text('我未收佣金');
      //图片底部的颜色切换（白和灰切换）
      this.$emit('getValue',this.toLogin);
      //页面加载前获取数据列表---------------------------------------------------------------1
      $http.get(`/unionH5Brokerage/unCome/list?size=${this.size}&current=${this.current}`)
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
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      //获取盟员列表的数据----------------------------------------------------------------2
      $http.get(`/unionH5Brokerage/unionList`)
        .then(res => {
          if(res.data.data) {
            this.unionList = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      //未收取佣金的总金额----------------------------------------------------------------3
      $http.get(`/unionH5Brokerage/unComeSum`)
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
  .hasPayLoadMore{
    font-size: 0.85rem;
    color: #39ACFF;
    text-align: center;
    padding: 0.3rem;
  }
  .nothing{
    margin: 0.8rem 6rem;
    font-size: 0.85rem;
  }
</style>
