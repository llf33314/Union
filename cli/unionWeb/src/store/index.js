import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  name: 'state',
  state: {
    unionId: sessionStorage.getItem('unionId'),
    unionMemberId: sessionStorage.getItem('unionMemberId'),
    addressLatitude: '',
    addressLongitude: '',
    enterpriseAddress: '',
    address: '',
    isUnionOwner: sessionStorage.getItem('isUnionOwner'),
    permitId: '',
    // baseUrl: 'http://union.yifriend.net:7884', // 调试
    // baseUrl: 'https://union.deeptel.com.cn', // 测试
    baseUrl: 'http://nb.union.deeptel.com.cn', // 堡垒
  },
  actions: {

  },
  mutations: {
    unionIdChange(state, id) {
      state.unionId = id;
      sessionStorage.setItem('unionId', id);
    },
    unionMemberIdChange(state, id) {
      state.unionMemberId = id;
      sessionStorage.setItem('unionMemberId', id);
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
      sessionStorage.setItem('isUnionOwner', value);
    },
    // 创建联盟permitId
    permitIdChange(state, value) {
      state.permitId = value;
    },
  },
  getters: {

  }
})
