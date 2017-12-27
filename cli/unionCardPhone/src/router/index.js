import Vue from 'vue'
import Router from 'vue-router'

//联盟卡手机端路由配置
import unionCard from '@/components/phone/unionCard'
import myInformation from '@/components/phone/myInformation'
import consumeRecord from '@/components/phone/consumeRecord'
import discountCard from '@/components/phone/discountCard'
import activityCard from '@/components/phone/activityCard'
import infiniteScroll from '@/components/phone/infiniteScroll'

Vue.use(Router)

export default new Router({
  //mode: 'history',
  routes: [
    //默认路由指到登录页面
    {
      path: '/:bid',
      name: 'unionCard',
      redirect: '/toUnionCard/:bid',
      component: unionCard,
    },
    //以下为联盟卡手机端路由------------------------------------
    //联盟卡主页面------------------1
    {
      path: '/toUnionCard/:bid',
      name: 'unionCard',
      component: unionCard,
    },
    //联盟卡个人信息路由-------------2
    {
      path: '/toMyInformation/:bid',
      name: 'myInformation',
      component: myInformation,
    },
    //消费记录页面--------------------3
    {
      path:"/toConsumeRecord/:bid",
      name:'consumeRecord',
      component:consumeRecord
    },
    //折扣卡页面-------------------4
    {
      path:"/toDiscountCard/:bid/:uid",
      name:'discountCard',
      component:discountCard
    },
    //活动卡页面--------------------5
    {
      path:"/toActivityCard/:bid/:aid/:uid",
      name:'activityCard',
      component:activityCard
    },
    //加载更多页面--------------------6
    {
      path:"/toInfiniteScroll/:bid",
      name:'infiniteScroll',
      component:infiniteScroll
    },
  ]
})
