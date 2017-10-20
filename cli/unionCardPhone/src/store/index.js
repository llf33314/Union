import Vue from 'vue'
import Vuex from 'vuex'
import $http from '@/utils/http.js'

Vue.use(Vuex)

export default new Vuex.Store({
  name: 'state',
  state: {
    unionId: sessionStorage.unionId,
    infoId: sessionStorage.infoId,
    addressLatitude: '',
    addressLongitude: '',
    delivery: '',
    address: '',
  },
  actions: {

  },
  mutations: {
    unionIdChange(state, id) {
      state.unionId = id;
      sessionStorage.unionId = id;
    },
    infoIdChange(state, id) {
      state.infoId = id;
      sessionStorage.infoId = id;
    },
    latitudeChange(state, value) {
      state.addressLatitude = value;
    },
    longitudeChange(state, value) {
      state.addressLongitude = value;
    },
    // 点击地址
    deliveryChange(state, value) {
      state.delivery = value;
    },
    // 行政区域
    addressChange(state, value) {
      state.address = value;
    }
  },
  getters: {

  }
})