import Vue from 'vue'
import Router from 'vue-router'
import MyUnion from '@/components/my-union/MyUnion'
import UnionSetting from '@/components/my-union/UnionSetting'
import JoinStep from '@/components/my-union/JoinStep'
import CreateStep from '@/components/my-union/CreateStep'
import UnionCheck from '@/components/my-union/UnionCheck'
import UnionRecommend from '@/components/my-union/UnionRecommend'
import UnionPercent from '@/components/my-union/UnionPercent'
import UnionDiscount from '@/components/my-union/UnionDiscount'
import UnionQuit from '@/components/my-union/UnionQuit'
import NoCurrentUnion from '@/components/my-union/NoCurrentUnion'
import NoRegister from '@/components/my-union/NoRegister'
import UnionIntroduced from '@/components/my-union/UnionIntroduced'

import Login from '@/components/login/Login'

import Business from '@/components/business/Business'
import Finance from '@/components/finance/Finance'
import Front from '@/components/front/Front'
import NoUnion from '@/components/public-components/NoUnion'

import MyUnionEntrance from '@/components/entrance/MyUnionEntrance'
import BusinessEntrance from '@/components/entrance/BusinessEntrance'
import FinanceEntrance from '@/components/entrance/FinanceEntrance'
import FrontEntrance from '@/components/entrance/FrontEntrance'

Vue.use(Router)

export default new Router({
  // mode: 'history',
  routes: [{
      path: '/',
      name: 'index',
      redirect: '/my-union',
      component: MyUnionEntrance,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/my-union',
      name: 'MyUnionEntrance',
      component: MyUnionEntrance,
    },
    {
      path: '/my-union/index',
      name: 'MyUnionIndex',
      component: MyUnion,
    },
    {
      path: '/my-union/join-step',
      name: 'JoinStep',
      component: JoinStep,
    },
    {
      path: '/my-union/create-step',
      name: 'CreateStep',
      component: CreateStep,
    },
    {
      path: '/my-union/union-setting',
      name: 'UnionSetting',
      component: UnionSetting,
    },
    {
      path: '/my-union/union-check',
      name: 'UnionCheck',
      component: UnionCheck,
    },
    {
      path: '/my-union/union-recommend',
      name: 'UnionRecommend',
      component: UnionRecommend,
    },
    {
      path: '/my-union/union-percent',
      name: 'UnionPercent',
      component: UnionPercent,
    },
    {
      path: '/my-union/union-discount',
      name: 'UnionDiscount',
      component: UnionDiscount,
    },
    {
      path: '/my-union/union-quit',
      name: 'UnionQuit',
      component: UnionQuit,
    },
    {
      path: '/my-union/no-register',
      name: 'NoRegister',
      component: NoRegister,
    },
    {
      path: '/my-union/no-currentUnion',
      name: 'NoCurrentUnion',
      component: NoCurrentUnion,
    },
    {
      path: '/my-union/union-introduced',
      name: 'UnionIntroduced',
      component: UnionIntroduced,
    },
    {
      path: '/business',
      name: 'BusinessEntrance',
      component: BusinessEntrance,
    },
    {
      path: '/business/index',
      name: 'BusinessIndex',
      component: Business,
    },
    {
      path: '/finance',
      name: 'FinanceEntrance',
      component: FinanceEntrance,
    },
    {
      path: '/finance/index',
      name: 'FinanceIndex',
      component: Finance,
    },
    {
      path: '/front',
      name: 'FrontEntrance',
      component: FrontEntrance,
    },
    {
      path: '/front/index',
      name: 'FrontIndex',
      component: Front,
    },
    {
      path: '/no-union',
      name: 'NoUnion',
      component: NoUnion,
    }
  ]
})
