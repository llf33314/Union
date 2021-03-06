import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  name: 'state',
  state: {
    unionId: sessionStorage.unionId,
    unionMemberId: sessionStorage.unionMemberId,
    infoId: sessionStorage.infoId,
    addressLatitude: '',
    addressLongitude: '',
    enterpriseAddress: '',
    address: '',
    isUnionOwner: sessionStorage.isUnionOwner,
    busId: sessionStorage.busId,
    permitId: '',
    // baseUrl: 'http://union.yifriend.net:7884', // 调试
    baseUrl: 'https://union.deeptel.com.cn', // 测试
    // baseUrl: 'http://nb.union.deeptel.com.cn', // 堡垒
  },
  actions: {

  },
  mutations: {
    unionIdChange(state, id) {
      state.unionId = id;
    },
    unionMemberIdChange(state, id) {
      state.unionMemberId = id;
      sessionStorage.unionMemberId = id;
    },
    infoIdChange(state, id) {
      state.infoId = id;
    },
    busIdChange(state, id) {
      state.busId = id;
      sessionStorage.busId = id;
    },
    latitudeChange(state, value) {
      state.addressLatitude = value;
    },
    longitudeChange(state, value) {
      state.addressLongitude = value;
    },
    // 点击地址
    enterpriseAddress(state, value) {
      state.enterpriseAddress = value;
    },
    // 行政区域
    addressChange(state, value) {
      state.address = value;
    },
    // 是否盟主
    isUnionOwnerChange(state, value) {
      state.isUnionOwner = value;
      sessionStorage.isUnionOwner = value;
    },
    // 创建联盟permitId
    permitIdChange(state, value) {
      state.permitId = value;
    },
  },
  getters: {

  }
})
