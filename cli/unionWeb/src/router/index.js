import Vue from 'vue';
import Router from 'vue-router';
// 入口
import MyUnionEntrance from '@/components/entrance/MyUnionEntrance';
import BusinessEntrance from '@/components/entrance/BusinessEntrance';
import FinanceEntrance from '@/components/entrance/FinanceEntrance';
import FrontEntrance from '@/components/entrance/FrontEntrance';
// 我的联盟
import MyUnion from '@/components/my-union/my-union-index/MyUnion';
import NoCurrentUnion from '@/components/my-union/my-union-index/NoCurrentUnion';
import NoRegister from '@/components/my-union/my-union-index/NoRegister';
import UnionIntroduced from '@/components/my-union/my-union-index/UnionIntroduced';
import UnionSetting from '@/components/my-union/union-setting/UnionSetting';
import JoinStep from '@/components/my-union/join-union/JoinStep';
import CreateStep from '@/components/my-union/create-union/CreateStep';
import UnionPercent from '@/components/my-union/sell-divide/UnionPercent';

import UnionCardSetting from '../components/my-union/union-card-setting/UnionCardSetting';
import MyActivityCard from '../components/my-union/union-card-setting/MyActivityCard';

import UnionQuit from '@/components/my-union/union-quit/UnionQuit';

import UnionCheck from '@/components/my-union/UnionCheck';
import UnionRecommend from '@/components/my-union/UnionRecommend';
import UnionDiscount from '@/components/my-union/UnionDiscount';

// 我的联盟外其他模块
import Business from '@/components/business/Business';
import Finance from '@/components/finance/Finance';
import Front from '@/components/front/Front';
import NoUnion from '@/components/public-components/NoUnion';

Vue.use(Router);

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      name: 'index',
      redirect: '/my-union',
      component: MyUnionEntrance
    },
    {
      path: '/no-union',
      name: 'NoUnion',
      component: NoUnion
    },
    // 我的联盟
    {
      path: '/my-union',
      name: 'MyUnionEntrance',
      component: MyUnionEntrance
    },
    {
      path: '/my-union/no-register',
      name: 'NoRegister',
      component: NoRegister
    },
    {
      path: '/my-union/no-currentUnion',
      name: 'NoCurrentUnion',
      component: NoCurrentUnion
    },
    {
      path: '/my-union/union-introduced',
      name: 'UnionIntroduced',
      component: UnionIntroduced
    },
    {
      path: '/my-union/index',
      name: 'MyUnionIndex',
      component: MyUnion
    },
    {
      path: '/my-union/join-step',
      name: 'JoinStep',
      component: JoinStep
    },
    {
      path: '/my-union/create-step',
      name: 'CreateStep',
      component: CreateStep
    },
    {
      path: '/my-union/union-setting',
      name: 'UnionSetting',
      component: UnionSetting
    },
    {
      path: '/my-union/union-check',
      name: 'UnionCheck',
      component: UnionCheck
    },
    {
      path: '/my-union/union-recommend',
      name: 'UnionRecommend',
      component: UnionRecommend
    },
    {
      path: '/my-union/union-percent',
      name: 'UnionPercent',
      component: UnionPercent
    },
    {
      path: '/my-union/union-discount',
      name: 'UnionDiscount',
      component: UnionDiscount
    },
    {
      path: '/my-union/union-quit',
      name: 'UnionQuit',
      component: UnionQuit
    },
    // 联盟卡设置
    {
      path: '/my-union/union-card-setting',
      name: 'UnionCardSetting',
      component: UnionCardSetting,
      children: [
        {
          path: '/my-activity-card',
          name: 'MyActivityCard',
          component: MyActivityCard
        }
      ]
    },
    // 商机
    {
      path: '/business',
      name: 'BusinessEntrance',
      component: BusinessEntrance
    },
    {
      path: '/business/index',
      name: 'BusinessIndex',
      component: Business
    },
    // 财务
    {
      path: '/finance',
      name: 'FinanceEntrance',
      component: FinanceEntrance
    },
    {
      path: '/finance/index',
      name: 'FinanceIndex',
      component: Finance
    },
    // 前台
    {
      path: '/front',
      name: 'FrontEntrance',
      component: FrontEntrance
    },
    {
      path: '/front/index',
      name: 'FrontIndex',
      component: Front
    }
  ]
});
