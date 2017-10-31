'use strict';
import axios from 'axios';
import { Message } from 'element-ui';

// axios 配置
// axios.defaults.baseURL = 'http://union.yifriend.net:7884';
axios.defaults.baseURL = 'https://union.deeptel.com.cn';           //测试环境
// axios.defaults.baseURL = 'http://nb.union.deeptel.com.cn';         //堡垒
// axios.defaults.baseURL = 'http://union.duofee.com';

// axios.defaults.timeout = 5000;

// http request 拦截器
axios.interceptors.request.use(
  config => {
    // 发送请求之前做一些处理
    return config;
  },
  err => {
    return Promise.reject(err);
  }
);

// http response 拦截器
axios.interceptors.response.use(
  res => {
    // 返回响应时做一些处理
    return res;
  },
  err => {
    return Promise.reject(err);
  }
);

function checkStatus(res) {
  // loading
  // 如果http状态码正常，则直接返回数据
  if (res && (res.status === 200 || res.status === 304 || res.status === 400)) {
    return res;
    // 如果不需要除了data之外的数据，可以直接 return res.data
  }
  // 异常状态下，把错误信息返回去
  return {
    status: -404,
    errorMsg: res.data.errorMsg
  }
}

function checkCode(res) {
  // 如果code异常(这里已经包括网络错误，服务器错误的错，后端抛出误)，可以弹出一个错误提示，告诉用户
  if (res.status === -404) {
    Message({
      showClose: true,
      message: res.errorMsg,
      type: 'warning',
      duration: 0
    });
  }
  if (res.data && (!res.data.success)) {
    Message({
      showClose: true,
      message: res.data.errorMsg,
      type: 'warning',
      duration: 0
    });
  }
  return res;
}

export default {
  post(url, data) {
    return axios({
      method: 'post',
      url,
      data: data,
      timeout: 10000,
      headers: {
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json; charset=UTF-8'
      }
    }).then(
      res => {
        return checkStatus(res);
      }
    ).then(
      res => {
        return checkCode(res);
      }
    ).then(
      res => {
        if (res.data.redirectUrl && res.data.redirectUrl !== '') {
          window.location = res.data.redirectUrl;
        }
        return res;
      }
    )
  },
  get(url, params) {
    return axios({
      method: 'get',
      url,
      params, // get 请求时带的参数
      timeout: 10000,
      headers: {
        'X-Requested-With': 'XMLHttpRequest'
      }
    }).then(
      res => {
        return checkStatus(res);
      }
    ).then(
      res => {
        return checkCode(res);
      }
    ).then(
      res => {
        if (res.data.redirectUrl && res.data.redirectUrl !== '') {
          window.location = res.data.redirectUrl;
        }
        return res;
      }
    )
  }
}
