import Vue from 'vue'
import Router from 'vue-router'

//佣金平台手机端路由配置
import Index from '@/components/phone/Index'
import toExtract from '@/components/phone/toExtract'
import toPayList from '@/components/phone/toPayList'
import toUnPayList from '@/components/phone/toUnPayList'
import toDetailList from '@/components/phone/toDetailList'
import toLogin from '@/components/phone/toLogin'

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    //默认路由
    {
      path: '/',
      name: 'home',
      redirect: '/Index',
      component: Index,
    },
    //佣金平台路由
    {
      path: '/index',
      name: 'Index',
      component: Index,
    },
    {
      path: '/toExtract',
      name: 'toExtract',
      component: toExtract,
    },
    {
      path: '/toPayList',
      name: 'toPayList',
      component: toPayList,
    },
    {
      path: '/toUnPayList',
      name: 'toUnPayList',
      component: toUnPayList,
    },
    {
      path: '/toExtract/toDetailList',
      name: 'toDetailList',
      component: toDetailList,
    },
      //佣金平台登录页面
    {
      path: '/toLogin',
      name: 'toLogin',
      component: toLogin,
    }
  ]
})
