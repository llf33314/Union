import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
  name: 'state',
  state: {
    // unionId: sessionStorage.getItem('unionId'),
    // memberId: sessionStorage.getItem('memberId'),
    // isUnionOwner: sessionStorage.getItem('isUnionOwner'),
    unionId: '',
    memberId: '',
    isUnionOwner: '',
    addressLatitude: '',
    addressLongitude: '',
    enterpriseAddress: '',
    address: '',
    permitId: '',
    myColor: '',
    // baseUrl: 'http://192.168.3.40:8080',
    // baseUrl: 'http://192.168.3.59:8080',
    // baseUrl: 'http://union.yifriend.net:7884', // 调试
    baseUrl: 'https://union.deeptel.com.cn', // 测试
    // baseUrl: 'http://nb.union.deeptel.com.cn', // 堡垒
    memberUrl: 'https://member.deeptel.com.cn',
    socketUrl: 'https://socket.deeptel.com.cn', // 测试
    // socketUrl: 'https://socket1.duofriend.com', // 堡垒
    materialUrl: 'https://suc.deeptel.com.cn/common/material.do?retUrl=', // 测试
    // materialUrl: 'http://nb.suc.deeptel.com.cn/common/material.do?retUrl=', // 堡垒
    // wxmpUrl: 'http://hz1.yifriend.net' // 调试
    wxmpUrl: 'https://deeptel.com.cn' // 测试
  },
  actions: {},
  mutations: {
    unionIdChange(state, id) {
      state.unionId = id;
      // sessionStorage.setItem('unionId', id);
    },
    memberIdChange(state, id) {
      state.memberId = id;
      // sessionStorage.setItem('memberId', id);
    },
    // 是否盟主
    isUnionOwnerChange(state, value) {
      state.isUnionOwner = value;
      // sessionStorage.setItem('isUnionOwner', value);
    },
    latitudeChange(state, value) {
      state.addressLatitude = value;
    },
    longitudeChange(state, value) {
      state.addressLongitude = value;
    },
    // 点击地址
    enterpriseAddressChange(state, value) {
      state.enterpriseAddress = value;
    },
    // 创建联盟permitId
    permitIdChange(state, value) {
      state.permitId = value;
    },
    myColorChange(state, value) {
      state.myColor = value;
    }
  },
  getters: {}
});
