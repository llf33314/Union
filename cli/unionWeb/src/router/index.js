import Vue from 'vue';
import Router from 'vue-router';

// 入口
const MyUnionEntrance = () => import('@/components/entrance/MyUnionEntrance');
const BusinessEntrance = () => import('@/components/entrance/BusinessEntrance');
const FinanceEntrance = () => import('@/components/entrance/FinanceEntrance');
const FrontEntrance = () => import('@/components/entrance/FrontEntrance');
const BusinessIntroductionEntrance = () => import('@/components/entrance/BusinessIntroductionEntrance');

// 我的联盟
// 我的联盟index
const MyUnion = () => import(/* webpackChunkName: "my-union-index" */ '@/components/my-union/my-union-index/MyUnion');
const NoCurrentUnion = () =>
  import(/* webpackChunkName: "my-union-index" */ '@/components/my-union/my-union-index/NoCurrentUnion');
const NoRegister = () =>
  import(/* webpackChunkName: "my-union-index" */ '@/components/my-union/my-union-index/NoRegister');
const UnionIntroduced = () =>
  import(/* webpackChunkName: "my-union-index" */ '@/components/my-union/my-union-index/UnionIntroduced');

// 我的联盟其他设置
const CreateStep = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/create-union/CreateStep');
const JoinStep = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/join-union/JoinStep');
const UnionSetting = () =>
  import(/* webpackChunkName: "my-union" */ '@/components/my-union/union-setting/UnionSetting');
const UnionCheck = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/UnionCheck');
const UnionRecommend = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/UnionRecommend');
const UnionPercent = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/sell-divide/UnionPercent');
const UnionQuit = () => import(/* webpackChunkName: "my-union" */ '@/components/my-union/union-quit/UnionQuit');

const UnionCardSetting = () =>
  import(/* webpackChunkName: "union-card-setting" */ '@/components/my-union/union-card-setting/UnionCardSetting');
const MyActivityCard = () =>
  import(/* webpackChunkName: "union-card-setting" */ '@/components/my-union/union-card-setting/MyActivityCard');

// 我的联盟外其他模块
const Front = () => import('@/components/front/Front');
const Business = () => import('@/components/business/Business');
const Finance = () => import('@/components/finance/Finance');
const UserIntroduction = () => import('@/components/businessIntroduction/UserIntroduction');

const NoUnion = () => import('@/components/public-components/NoUnion');

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
          path: 'my-activity-card/:id',
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
    },
    // 商家简介
    {
      path: '/business-introduction',
      name: 'BusinessIntroductionEntrance',
      component: BusinessIntroductionEntrance
    },
    {
      path: '/business-introduction/index',
      name: 'UserIntroduction',
      component: UserIntroduction
    }
  ]
});
