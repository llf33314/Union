<template><!--佣金明细模块-->
  <div id="toDetailList">
    <!--公共头部样式-->
    <div class="public_stylecss">
      <div class="clear wantMoney">
        <el-breadcrumb-item :to="{ path: '/index' }">
          <img class="back__" src="../../assets/images/back.png" alt="" style="width: 1rem;">
        </el-breadcrumb-item>
        <p class="fr union_head" @click="boxWarp">
          <img src="../../assets/images/switchover.png">
          <span class="unionName">全部</span>
        </p>
      </div>
      <div class="union_second payed">
        <p>{{recommendMoney}}</p>
        <span>推荐佣金明细(元)</span>
      </div>
      <!--如果有 passive 类样式，就隐藏-->
      <div class="union_second passive unpay clear">
        <div class="fl">
          <p>{{sellCardMoney}}</p>
          <span>售卡佣金明细(元)</span>
        </div>
      </div>
    </div>
    <!-- 推荐佣金 以及 售卡佣金 导航条-部分-->
    <div class=" detail-wrap clear">
      <div class='payTab flex flex-pack-center'>
        <div class="focusPay">推荐佣金</div>
        <div class="">售卡佣金</div>
      </div>
      <p class="record">佣金明细<p>
      <!--推荐佣金的页面-->
      <div class="payed">
        <ul >
          <li class="clear" v-for="(item, index) in recommendList">
            <div class="clear fl">
              <div class="fl">
                <div class='fl'>
                  <p>{{item.unionName}}</p>
                  <span style="color: #7e7e7e">{{item.time}}</span>
                </div>
              </div>
            </div>
            <div class="clearfr">
              <div class="fr">
                <span>{{item.name}}</span>
                <p class="" style="color: #20a0ff;text-align: right;">+{{item.money.toFixed(2)}}</p>
              </div>
            </div>
          </li>
        </ul>
        <!--<div class="loadMore hasPayLoadMore">加载更多</div>-->
        <div class="loadMore hasPayLoadMore1"  @click="loadMore1">加载更多</div>
        <div class="nothing noPayNothing" style="display:none;">没有更多数据</div>
      </div>
    <!--售卡佣金页面-->
    <div class="unpay passive">
      <ul >
        <li class="clear" v-for="(item, index) in sellCardList">
          <div class="clear fl">
            <div class="fl clear">
              <div class='fl'>
                <p class="">{{item.unionName}}</p>
                <span style="color: #7e7e7e">{{item.time}}</span>
              </div>
            </div>
          </div>
          <div class="clear fr">
            <div class="fr">
              <span>{{item.name}}</span>
              <p class="" style="color: #20a0ff;text-align: right;">+{{item.money.toFixed(2)}}</p>
            </div>
          </div>
        </li>
      </ul>
      <div class="loadMore hasPayLoadMore2"  @click="loadMore2">加载更多</div>
      <div class="nothing hasPayNothing " style="display: none">没有更多数据</div>
    </div>
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
    name: 'toDetailList',
    data() {
      return {
        //推荐佣金列表
        recommendList:[],
        //推荐佣金显示的总金额
        recommendMoney:0,
        //售卡佣金列表
        sellCardList:[],
        //售卡佣金显示的总金额
        sellCardMoney:0,
        //盟员列表
        unionList:[],
        //显示的列表条数
        size:6,
        //当前列表第一页
        current:1,
        //当前的联盟id号
        unionId:'',
      }
    },
    mounted(){
      //切换推荐佣金和售卡佣金的变换
      $('.payTab div').on('touchend', function(){
        let index = $(this).index();
        $(this).addClass('focusPay').siblings('.payTab div').removeClass('focusPay');
        if(index == 0){//已支付
          if($(".payed").hasClass("passive")){
            $(".payed").removeClass("passive");
            $(".unpay").addClass("passive");
          }
        }else if(index == 1){//未支付
          if($(".unpay").hasClass("passive")){
            $(".payed").addClass("passive");
            $(".unpay").removeClass("passive");
          }
        }
      });
    },
    methods: {
      //点击按钮弹出框显示
      boxWarp(){
        $('.box-wrap').show();
      },
      //点击隐藏
      hide_(){
        $('.box-wrap').hide();
      },
//      联盟表点击全部按钮
      fullyLoaded(){
        this.unionId='';
        document.querySelector('.unionName').innerHTML='全部';
        //推荐佣金的页面的请求---------------------------1
        $http.get(`/unionH5Brokerage/opportunity/list`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                $('.noPayNothing').show();
                $('.hasPayLoadMore1').hide();
                this.recommendMoney = 0;
                this.recommendList = [];
              } else if (res.data.data.records.length < this.size) {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').hide();
              }
              else {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').show();
              }
              //渲染数据
              this.recommendList = res.data.data.records;
              this.recommendList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //售卡佣金对应id号的总金额
              $http.get(`/unionH5Brokerage/opportunitySum`)
                .then(res => {
                  if(res.data.data) {
                    this.recommendMoney = res.data.data.toFixed(2);
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 0});
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
        //售卡佣金的页面的请求------------------------------2
        $http.get(`/unionH5Brokerage/cardDivide/list`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                this.sellCardMoney = 0;
                this.sellCardList = [];
                $('.hasPayNothing').show();
                $('.hasPayLoadMore2').hide();
              } else if (res.data.data.records.length < this.size) {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore2').hide();
              }
              else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore2').show();
              }
              this.sellCardList = res.data.data.records;
              this.sellCardList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //售卡佣金对应id号的总金额
              $http.get(`/unionH5Brokerage/cardDivideSum`)
                .then(res => {
                  if(res.data.data) {this.sellCardMoney = res.data.data.toFixed(2);}
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 0});
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
            console.log('111')
          });
        //隐藏弹出框
        $('.box-wrap').hide();
      },
//      点击对应的按钮
      partLoaded(did,uname){
        let Uid=did;
        //点击时把联盟id放到公共的地方
        this.unionId=did;
        document.querySelector('.unionName').innerHTML=uname;
        //推荐佣金的页面的请求--------------------------------1
        $http.get(`/unionH5Brokerage/opportunity/list?unionId=${Uid}`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                $('.noPayNothing').show();
                $('.hasPayLoadMore1').hide();
                this.recommendMoney = 0;
                this.recommendList = [];
              } else if (res.data.data.records.length < this.size) {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').hide();
              }
              else {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').show();
              }
              //渲染数据
              this.recommendList = res.data.data.records;
              this.recommendList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //售卡佣金对应id号的总金额
              $http.get(`/unionH5Brokerage/opportunitySum?unionId=${Uid}`)
                .then(res => {
                  if(res.data.data) {
                    this.recommendMoney = res.data.data.toFixed(2);
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 0});
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });

        //售卡佣金的页面的请求--------------------------------2
        $http.get(`/unionH5Brokerage/cardDivide/list?unionId=${Uid}`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                this.sellCardMoney = 0;
                this.sellCardList = [];
                $('.hasPayNothing').show();
                $('.hasPayLoadMore2').hide();
              } else if (res.data.data.records.length < this.size) {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore2').hide();
              }
              else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore2').show();
              }
              this.sellCardList = res.data.data.records;
              this.sellCardList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //售卡佣金对应id号的总金额
              $http.get(`/unionH5Brokerage/cardDivideSum?unionId=${Uid}`)
                .then(res => {
                  if(res.data.data) {
                    this.sellCardMoney = res.data.data.toFixed(2);
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 0});
                });
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
        //隐藏弹出框
        $('.box-wrap').hide(300);
      },
      //推荐佣金页面加载更多列表数据
      loadMore1(){
        let uid=this.unionId;
        console.log(++this.current);
        $http.get(`/unionH5Brokerage/opportunity/list?size=${this.size}&current=${this.current}&unionId=${uid}`)
          .then(res => {
            if(res.data.data) {
              let list = res.data.data.records;
              if (list.length < this.size) {
                $('.noPayNothing').show();
                $('.hasPayLoadMore1').hide();
              } else {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').show();
              }
              list.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //渲染数据
              this.recommendList = this.recommendList.concat(list);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
      },
      //售卡佣金页面加载更多列表数据
      loadMore2(){
        let uid=this.unionId;
        console.log(++this.current);
        $http.get(`/unionH5Brokerage/cardDivide/list?size=${this.size}&current=${this.current}&unionId=${uid}`)
          .then(res => {
            if(res.data.data) {
              let list = res.data.data.records;
              if (list.length < this.size) {
                $('.hasPayNothing').show();
                $('.hasPayLoadMore2').hide();
              } else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore2').show();
              }
              //渲染数据
              list.forEach((v, i) => {
                if(res.data) {
                  v.time = $todate.todate(new Date(v.time));
                }
              })
              this.sellCardList = this.sellCardList.concat(list);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
        console.log(this.unCommission);
      },
    },
    created (){
      //页面的title变换
      $("#title_").text('佣金明细');
//以下伟推荐佣金部分------------------------------------------------------------------------------------------01
    //推荐佣金
      $http.get(`/unionH5Brokerage/opportunity/list?size=${this.size}&current=${this.current}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              $('.noPayNothing').show();
              $('.hasPayLoadMore1').hide();
              this.recommendMoney = 0;
              this.recommendList = [];
            } else if (res.data.data.records.length < this.size) {
              $('.noPayNothing').hide();
              $('.hasPayLoadMore1').hide();
            }
            else {
              $('.noPayNothing').hide();
              $('.hasPayLoadMore1').show();
            }
            //渲染数据
            this.recommendList = res.data.data.records;
            this.recommendList.forEach((v, i) => {
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });
      //推荐佣金明细的总金额
      $http.get(`/unionH5Brokerage/opportunitySum`)
        .then(res => {
          if(res.data.data) {
            this.recommendMoney = res.data.data.toFixed(2);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });

  //以下为售卡佣金的明细--------------------------------------------------------------------------------------02
      //售卡佣金
      $http.get(`/unionH5Brokerage/cardDivide/list?size=${this.size}&current=${this.current}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              this.sellCardMoney = 0;
              this.sellCardList = [];
              $('.hasPayNothing').show();
              $('.hasPayLoadMore2').hide();
            } else if (res.data.data.records.length < this.size) {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore2').hide();
            }
            else {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore2').show();
            }
            this.sellCardList = res.data.data.records;
            this.sellCardList.forEach((v, i) => {
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });
      //售卡佣金明细的总金额
      $http.get(`/unionH5Brokerage/cardDivideSum`)
        .then(res => {
          if(res.data.data) {
            this.sellCardMoney = res.data.data.toFixed(2);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });

//多粉弹出框的请求盟员列表-------------------------------------------------------------------------------------03
      $http.get(`/unionH5Brokerage/unionList`)
        .then(res => {
          if(res.data.data) {
            this.unionList = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>
  .hasPayLoadMore1,.hasPayLoadMore2{
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
