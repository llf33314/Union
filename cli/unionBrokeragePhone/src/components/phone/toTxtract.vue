<template>
  <div id="toTxtract">
        <!--顶部公共样式-->
        <div class="public_stylecss">
          <div class="clear wantMoney">
            <el-breadcrumb-item :to="{ path: '/index' }">
              <img class="back__" src="../../assets/images/back.png" alt="" style="width: 1rem;">
            </el-breadcrumb-item>
            <!--<p class="fr union_head" >-->
            <!--<img src="../../assets/images/switchover.png" alt="">-->
            <!--<span @click="boxWarp">多粉大联盟</span>-->
            <!--</p>-->
          </div>
          <div class="union_second payed clear">
            <div class="fl">
              <p>{{moneyList.ableGet}}</p>
              <span style="font-size: 0.7rem;font-weight:normal">可提佣金=推荐佣金+售卡佣金</span>
            </div>
            <div class="fr payment">
              <router-link :to="{ path: '/toTxtract/toDetailList', name: 'toDetailList'}">
                佣金明细
              </router-link>
            </div>
          </div>
        </div>
        <!--中间表单-->
        <div class="wantMoney-input">
          <input placeholder='输入要提现的金额,最低一元' v-model="inputNumber" class='wantInput' style='color:black
        ;text-align: left;' id="importMoney">
        </div>
        <!--立即体现按钮-->
        <button class="button-btn" @click="showModel_" disabled="true">
          立即提现
        </button>
        <!--体现弹出框-->
        <div class="box-wrap2" style="display: none">
          <div class="mask" @click="hide1_"></div>
          <div class="box">
            <p>将要提现￥{{inputNumber}}，是否确认？</p>
            <div class="fr" @click="send1_">确认</div>
            <div style="border-left:none" @click="hide1_">取消</div>
          </div>
        </div>
        <!--中间栏4项-->
        <ul class=" wantMoney-wrap clear">
          <li>
            <div>
              <p>推荐所得总佣金</p>
              <h2>{{moneyList.opportunitySum}}</h2>
            </div>
          </li>
          <li>
            <div>
              <p>售卡所得总佣金</p>
              <h2>{{moneyList.divideSum}}</h2>
            </div>
          </li>
          <li>
            <div>
              <p>历史提现佣金</p>
              <h2>{{moneyList.sumWithdrawals}}</h2>
            </div>
          </li>
          <li>
            <div>
              <p>未收佣金</p>
              <h2>{{moneyList.sumUnCome}}</h2>
            </div>
          </li>
        </ul>
        <div class="wantMoney-table">
          <p>提现记录</p>
          <table class="table-list">
            <thead class="table-list-head">
            <tr>
              <td>时间</td>
              <td>金额</td>
              <td>提现人</td>
            </tr>
            </thead>
            <tbody class="table-list-head" id="extract">
            <tr v-for="(item, index) in withdrawRecord">
              <td width="100">{{item.time}}</td>
              <td>{{"￥"+item.money}}</td>
              <td>{{item.nickName}}</td>
            </tr>
            </tbody>
          </table>
          <p class="hasPayLoadMore" style="display:none" @click="loadMore">加载更多</p>
          <div class="nothing hasPayNothing" style="display:none" >没有更多数据</div>
        </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  import $todate from '@/utils/todate.js'

  export default {
    name: 'toTxtract',
    data() {
      return {
        //金额数据列表
        moneyList:[],
        //体现记录数据
        withdrawRecord:[],
        //输入框的值
        inputNumber:'',
        //显示的列表条数
        size:4,
        //当前列表第一页
        current:1,
      }
    },
    methods: {
      showModel_(){
        $('.box-wrap2').show(200);
      },
      hide1_(){
        $('.box-wrap2').hide(200);
      },
      send1_(){
        let money=this.inputNumber;
        let url='toTxtract';
        $http.post(`unionH5Brokerage/withdrawals?fee=${money}&url=${url}`)
          .then(res => {

          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
        $('.box-wrap2').hide(200);
      },
      //加载更多列表数据
      loadMore(){
        ++this.current
        $http.get(`/unionH5Brokerage/withdrawals/list?size=${this.size}&current=${this.current}`)
          .then(res => {
            if(res.data.data) {
              let list = res.data.data.records;
              if (list.length < 4) {
                $('.hasPayNothing').show();
                $('.hasPayLoadMore').hide();
              } else {
                $('.hasPayNothing').hide();
                $('.hasPayLoadMore').show();
              }
              this.withdrawRecord = this.withdrawRecord.concat(list);
              this.withdrawRecord.forEach((v, i) => {
                v.time = $todate.todate(new Date(v.time));
              })
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
          });
      },
    },
    //监听数据的变化
    watch: {
      inputNumber(){
        let payMoney=parseFloat($("#importMoney").val());
        let that_=this;
        if(payMoney>=1){
          $(".button-btn").css({
            "color":"white",
            "backgroundColor":"#39ABFF"
          });
          $('.button-btn').attr("disabled", false);
        }else if(!payMoney || isNaN(this.inputNumber)){
          $(".button-btn").css({
            "background-color":"#CCCCCC"
          });
          $('.button-btn').attr("disabled", true);
          let that_=this;
          setTimeout(()=>{
            that_.inputNumber="";
          },250);
        }
      }
    },
    created(){
      //页面的title变换
      $("#title_").text('我要体现');
      //获取盟员列表的数据
      $http.get(`/unionH5Brokerage/withdrawals`)
        .then(res => {
          if(res.data.data) {
            this.moneyList = res.data.data;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });
      //获取提现记录表的数据
      $http.get(`/unionH5Brokerage/withdrawals/list?size=${this.size}&current=${this.current}`)
        .then(res => {
          if(res.data.data) {
            if (res.data.data.records.length === 0) {
              $('.hasPayNothing').show();
              $('.hasPayLoadMore').hide();
            } else if (res.data.data.records.length < 4) {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore').hide();
            }
            else {
              $('.hasPayNothing').hide();
              $('.hasPayLoadMore').show();
            }
            this.withdrawRecord = res.data.data.records;
            this.withdrawRecord.forEach((v, i) => {
              v.time = $todate.todate(new Date(v.time));
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 0 });
        });
    },
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>
  .wantMoney-input>input::-webkit-input-placeholder { /* WebKit browsers */
    color:    #CDCDCD!important;
    font-size:0.8054rem;
  }
  .wantMoney-input>input:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
    color:    #CDCDCD!important;
    font-size:0.8054rem;
  }
  .wantMoney-input>input::-moz-placeholder { /* Mozilla Firefox 19+ */
    color:    #CDCDCD!important;
    font-size:0.8054rem;
  }
  .wantMoney-input>input:-ms-input-placeholder { /* Internet Explorer 10+ */
    color:    #CDCDCD!important;
    font-size:0.8054rem;
  }
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
