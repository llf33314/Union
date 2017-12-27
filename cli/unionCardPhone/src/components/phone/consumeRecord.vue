<template>
  <div id="consumeRecord">
    <div v-for="(items,index1) in recordContent">
      <p class="unionTime">
        <span>时间：{{items.consumeTimeStr}}</span>
      </p>
      <div class="unionContent">
        <p class="hair">{{items.shopName}}</p>
        <div class="ListContent">
          <p>消费金额：￥{{items.consumeMoney || filtration}}</p>
          <p>优惠金额：￥{{items.discountMoney  || filtration}}</p>
          <span>折扣{{items.discount}}（抵￥{{items.discountMoney}}）</span>
          <span>积分消耗 {{items.consumeIntegral}}（抵￥{{items.integralMoney}}）</span>
          <p>支付方式：{{items.payType==0?"现金":items.payType==1?"微信":'支付宝'}}</p>
          <p >
            优惠项目：
            <span v-if="items.items">
              {{items.items.name}}
            </span>
          </p>
          <p v-if="items.items" class="special_P">
            {{items.items.number}}
          </p>
        </div>
        <div class="payMoney">
          <p>
            赠送积分：{{items.giveIntegral}}
          </p>
          实付金额：
          <span style=" ">￥{{items.payMoney}}</span>
        </div>
      </div>
    </div>
    <FooterVue></FooterVue>
  </div>
</template>

<script>
  import $http from '@/utils/http.js'
  import FooterVue from "@/components/phone/FooterVue"

  export default {
    name: 'consumeRecord',
    components:{
      FooterVue
    },
    //数据
    data() {
      return {
        busId:'',
        //主内容
        recordContent:''
      }
    },
    //页面加载后调用的函数
    mounted(){

    },
    methods: {

    },
    //过滤器
    filters:{
      filtration:function(value){
        return value = value.toFixed(2);
      },
      moneyfFilters:(value)=>{
        return value='￥'+value;
      }
    },
    created (){
      $('#title_').text('消费记录');
      this.busId = this.$route.params.bid;
      setTimeout(function () {
        $('.footerLeft li').css({
          color:"#ddd"
        });
        $('.footerRight li').css({
          color:"#44bf97"
        });
      },0)
      $http.get(`/h5Card/79B4DE7C/myCardConsume/${this.busId}?url=toConsumeRecord`)
        .then(res => {
            console.log(res.data)
          if(res.data.success){
              if(res.data.data.records.length>0){
                this.recordContent=res.data.data.records;
              }
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
</script>
<style lang='less' rel="stylesheet/less" scoped>

</style>
