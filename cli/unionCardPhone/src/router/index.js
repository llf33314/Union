import Vue from 'vue'
import Router from 'vue-router'

//联盟卡手机端路由配置
import unionCard from '@/components/phone/unionCard'
import unionLogin from '@/components/phone/unionLogin'

Vue.use(Router)

export default new Router({
  //mode: 'history',
  routes: [
    //默认路由指到登录页面
    {
      path: '/',
      name: 'unionLogin',
      redirect: '/toUnionLogin',
      component: unionLogin,
    },
    //以下为联盟卡手机端路由
    {
      path: '/toUnionCard',
      name: 'unionCard',
      component: unionCard,
    },
    //联盟卡手机端登录页面
    {
      path:'/toUnionLogin',
      name:'unionLogin',
      component:unionLogin,
    },
  ]
})
