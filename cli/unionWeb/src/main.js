// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-default/index.css'
import './assets/css/index.less'
import store from './store/index.js'
import echarts from 'echarts'
import $ from 'jquery'
import iconfont from './assets/iconfont/iconfont.css'

Vue.use(ElementUI)
Vue.use(echarts)


Vue.config.productionTip = false

window.eventBus = new Vue({})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App),
  components: {
    App
  }
})
