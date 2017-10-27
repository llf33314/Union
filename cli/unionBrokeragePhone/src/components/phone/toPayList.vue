<template>
  <div id="toPayList" >
    <!--公共头部样式-->
    <div class="public_stylecss">
      <div class="clear wantMoney">
        <el-breadcrumb-item :to="{ path: '/index' }">
          <img class="back__" src="../../assets/images/back.png" alt="" style="width: 1rem;">
        </el-breadcrumb-item>
        <p class="fr union_head" @click="boxWarp">
          <img src="../../assets/images/switchover.png" alt="">
          <span  class="unionName">全部</span>
        </p>
      </div>
      <div class="union_second payed passive">
        <p>{{payMoney.toFixed(2)}}</p>
        <span>已支付(元)</span>
      </div>
      <!--如果有 passive 类样式，就隐藏-->
      <div class="union_second  unpay clear">
        <div class="fl">
          <p>{{totalMoney.toFixed(2)}}</p>
          <span>未支付(元)</span>
        </div>
        <div class="fr payment" @click="showModel3_">
          一键支付
        </div>
        <div class="box-wrap3" style="display: none">
          <div class="mask" @click="hide3_"></div>
          <div class="box">
            <p>将要支付对方￥{{totalMoney.toFixed(2)}}，是否确认?</p>
            <div class="fr" @click="send3_()">确认</div>
            <div style="border-left:none" @click="hide3_">取消</div>
          </div>
        </div>
      </div>
    </div>
    <!--支出明细部分-->
    <div class=" detail-wrap clear">
      <div class='payTab flex flex-pack-center'>
        <div class="focusPay">未支付</div>
        <div class="">已支付</div>
      </div>
      <p class="record">支出明细<p>
      <!--未支付的页面-->
      <div class="unpay ">
        <ul >
          <li class="clear" v-for="(item1, index) in unPayList">
            <div class="clear fl">
              <div class="fl clear">
                <div class='fl'>
                  <p class="">{{item1.unionName}}</p>
                  <span style="color: #7e7e7e">{{item1.time}}</span>
                </div>
                <div class='fl'>
                  <el-button class='goPay'  @click="showModel_(index)">去支付</el-button>
                </div>
                <div class="box-wrap2" style="display: none">
                  <div class="mask" @click="hide1_"></div>
                  <div class="box">
                    <p>将要支付对方￥{{item1.money.toFixed(2)}}，是否确认?</p>
                    <div class="fr" @click="send1_(item1.id)">确认</div>
                    <div style="border-left:none" @click="hide1_">取消</div>
                  </div>
                </div>
              </div>
            </div>
            <div class="clear fr">
              <div class="fr">
                <span>{{item1.name}}</span>
                <p class="" style="color: #90e88f;text-align: right;">-{{item1.money.toFixed(2)}}</p>
              </div>
            </div>
          </li>
        </ul>
        <div class="loadMore hasPayLoadMore1"  @click="loadMore1" style="color:#868686;">加载更多</div>
        <div class="nothing noPayNothing" style="display: none;color:#868686;">没有更多数据</div>
      </div>
      <!--已经支付的页面-->
      <div class="payed passive">
        <ul >
          <li class="clear" v-for="(item, index) in payList">
            <div class="clear fl">
              <div class='fl'>
                <p>
                  {{item.unionName}}
                </p>
                <span style="color: #7e7e7e">
                      {{item.time}}
                    </span>
              </div>
            </div>
            <div class="clearfr">
              <div class="fr">
                <span>{{item.name}}</span>
                <p class="" style="color: #90e88f;text-align: right;">{{item.money.toFixed(2)}}</p>
              </div>
            </div>
          </li>
        </ul>
        <div class="loadMore hasPayLoadMore2" style="display: none;color:#868686;" @click="loadMore2">加载更多</div>
        <div class="nothing hasPayNothing" style="display: none;color:#868686;">没有更多数据</div>
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
    name: 'toPayList',
    data() {
      return {
        //未支付的列表数据
        unPayList:[],
        //已支付的列表数据
        payList:[],
        //盟员列表
        unionList:[],
        //未支付的金额
        totalMoney:0,
        //支付的金额
        payMoney:0,
        //显示的列表条数
        size:6,
        //当前列表第一页
        current:1,
        //当前的联盟id号
        unionId:'',
        //一键支付的总金额
        totalMoney12:0
      }
    },
    mounted(){
      //切换支付和未支付
      $('.payTab div').on('touchend', function(){
        let index = $(this).index();
        $(this).addClass('focusPay').siblings('.payTab div').removeClass('focusPay');
        if(index == 1){//已支付
          if($(".payed").hasClass("passive")){
            $(".payed").removeClass("passive");
            $(".unpay").addClass("passive");
          }
        }else if(index == 0){//未支付
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
      //隐藏弹出框
      hide_(){
        $('.box-wrap').hide();
      },
      //点击去支付弹出框
      showModel_(index){
        $($('.box-wrap2')[index]).show();
      },
      hide1_(){
        $('.box-wrap2').hide();
      },
      //对应id的去支付
      send1_(id){
        $('.box-wrap2').hide();
//        去支付的请求，并跳转到微信支付页面
//        console.log(window.location.href.lastIndexOf('/'));
        let did=id;
        let url='toPayList';
        $http.post(`/unionH5Brokerage/onePay/${did}?url=${url}`)
          .then(res => {
            if(res.data.data) {
              //跳转到微信支付的页面
              location.href = res.data.data;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
      //一键支付的弹出框
      showModel3_(){
        if(this.totalMoney>0) {
          $('.box-wrap3').show();
        }else{
          this.$message({ showClose: true, message: '没有可支付的金额', type: 'error', duration: 3000 });
        }
      },
      //隐藏一键支付弹出框
      hide3_(){
        $('.box-wrap3').hide();
      },
      //一键支付确认按钮
      send3_(){
        let uid=this.unionId;
        //未支付金额的总和
        $http.get(`/unionH5Brokerage/unPaySum?unionId=${uid}`)
          .then(res => {
            if(res.data.data) {
              this.totalMoney12 = res.data.data;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
        let url='toPayList';
        $http.post(`/unionH5Brokerage/allPay?unionId=${uid}&fee=${this.totalMoney12}&url=${url}`)
          .then(res => {
            if(res.data.data) {
              //清除页面列表的数据
              location.href = res.data.data;
              //页面刷新
//            history.go(0)
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
        $('.box-wrap3').hide();
      },
//    点击'全部'时候触发
      fullyLoaded(){
        //当前的页数
        this.current=1;
        //清除联盟Id号码
        this.unionId='';
        document.querySelector('.unionName').innerHTML='全部';
        //未支付的页面的请求-------------1
        $http.get(`/unionH5Brokerage/unPay/list?size=${this.size}&current=${this.current}`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                $('.noPayNothing').show();
                $('.hasPayLoadMore1').hide();
                this.totalMoney = 0;
                this.unPayList = [];
              } else if (res.data.data.records.length < this.size) {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').hide();
              }
              else {
                console.log(res.data.data.records)
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').show();
              }
              //渲染数据
              this.unPayList = res.data.data.records;
              this.unPayList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              });
              //未支付金额的总和
              $http.get(`/unionH5Brokerage/unPaySum`)
                .then(res => {
                  if(res.data.data){
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
        //已支付的页面的请求-------------2
          $http.get(`/unionH5Brokerage/pay/list?size=${this.size}&current=${this.current}`)
            .then(res => {
              if(res.data.data) {
                console.log(res.data.data.records)
                if (res.data.data.records.length === 0) {
                  this.payMoney = 0;
                  this.payList = [];
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
                //渲染数据
                this.payList = res.data.data.records;
                this.payList.forEach((v, i) => {
                  v.time = $todate.todate(new Date(v.time));
                })
                //已支付的佣金总和
                $http.get(`/unionH5Brokerage/paySum`)
                  .then(res => {
                    if(res.data.data) {
                      this.payMoney = res.data.data;
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
        $('.box-wrap').hide();
      },
//      点击'对应联盟'时候触发;
      partLoaded(did,uname){
        let Uid=did;
        //点击时把联盟id放到公共的地方
        this.unionId=did;
        document.querySelector('.unionName').innerHTML=uname;
        console.log(Uid);
        //未支付的页面的请求-----------------1
        $http.get(`/unionH5Brokerage/unPay/list?size=${this.size}&current=${this.current}&unionId=${Uid}`)
          .then(res => {
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                $('.noPayNothing').show();
                $('.hasPayLoadMore1').hide();
                this.totalMoney = 0;
                this.unPayList = [];
              } else if (res.data.data.records.length < this.size) {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').hide();
              }
              else {
                $('.noPayNothing').hide();
                $('.hasPayLoadMore1').show();
              }
              //渲染数据
              this.unPayList = res.data.data.records;
              this.unPayList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              //未支付金额的总和
              $http.get(`/unionH5Brokerage/unPaySum?unionId=${Uid}`)
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
        //已支付的页面的请求----------------2
        $http.get(`/unionH5Brokerage/pay/list?size=${this.size}&current=${this.current}&unionId=${Uid}`)
          .then(res=>{
            if(res.data.data) {
              if (res.data.data.records.length === 0) {
                this.payMoney = 0;
                this.payList = [];
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
              //渲染数据
              this.payList = res.data.data.records;
              this.payList.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              $http.get(`/unionH5Brokerage/paySum?unionId=${Uid}`)
                .then(res => {
                  if(res.data.data) {
                    this.payMoney = res.data.data;
                  }
                })
                .catch(err => {
                  this.$message({showClose: true, message: err.toString(), type: 'error', duration: 3000});
                });
            }
          }
        )
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
        //只要点击就隐藏弹出框
        $('.box-wrap').hide();
      },
      //未支付页面加载更多列表数据
      loadMore1(){
        let uid=this.unionId;
        console.log(++this.current);
        $http.get(`/unionH5Brokerage/unPay/list?size=${this.size}&current=${this.current}&unionId=${uid}`)
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
              //渲染数据
              list.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
              this.unPayList = this.unPayList.concat(list);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
      },
      //已支付页面加载更多列表数据
      loadMore2(){
        let uid=this.unionId;
        console.log(++this.current);
        $http.get(`/unionH5Brokerage/pay/list?size=${this.size}&current=${this.current}&unionId=${uid}`)
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
                v.time = $todate.todate(new Date(v.time));
              })
              this.payList = this.payList.concat(list);
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          });
        console.log(this.unCommission);
      },
    },
    created (){
      //页面的title变换
      $("#title_").text('我佣金支出')
//以下部分为未支付的--------------------------------------------------------------------------1
//      未支付页面（页面加载时就渲染数据）
      $http.get(`/unionH5Brokerage/unPay/list?size=${this.size}&current=${this.current}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              $('.noPayNothing').show();
              $('.hasPayLoadMore1').hide();
              this.totalMoney = 0;
              this.unPayList = [];
            } else if (res.data.data.records.length < this.size) {
              $('.noPayNothing').hide();
              $('.hasPayLoadMore1').hide();
            }
            else {
              $('.noPayNothing').hide();
              $('.hasPayLoadMore1').show();
            }
            //渲染数据
            this.unPayList = res.data.data.records;
            this.unPayList.forEach((v, i) => {
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
      //未支付金额的总和
      $http.get(`/unionH5Brokerage/unPaySum`)
        .then(res => {
          if(res.data.data) {
            this.totalMoney = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
// 以下为已支付的------------------------------------------------------------------------------2
      //已支付页面（页面加载时就渲染数据）
      $http.get(`/unionH5Brokerage/pay/list?size=${this.size}&current=${this.current}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              this.payMoney = 0;
              this.payList = [];
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
            //渲染数据
            this.payList = res.data.data.records;
            this.payList.forEach((v, i) => {
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
          console.log(666)
        });
      //已支付的佣金总和
      $http.get(`/unionH5Brokerage/paySum`)
        .then(res => {
          if(res.data.data) {
            this.payMoney = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
        });
//多粉弹出框的请求盟员列表-----------------------------------------------------------------------3
      $http.get(`/unionH5Brokerage/unionList`)
        .then(res => {
          let that_=this;
          if(res.data.data){
            res.data.data.forEach((item,index)=>{
              that_.unionList.push(item);
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 3000 });
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
