// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import 'lib-flexible'
import './assets/css/theme.less'
import $ from 'jquery'
import iconfont from './assets/iconfont/iconfont.css';
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// Vue.use(ElementUI)
Vue.config.productionTip = true;


new Vue({
  el: '#app',
  router,
  render: h => h(App),
  components: {
    App
  }
})

