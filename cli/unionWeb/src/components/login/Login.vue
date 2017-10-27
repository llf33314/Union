<template>
  <button @click="fn1"> 登陆 </button>
  <div>test</div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'login',
  methods: {
    fn1() {
      let data = {
        userName: 'gt123',
        password: 'gt123456'
      };
      $http
        .get('http://192.168.3.40:8080/login/sign', data)
        .then(res => {
          console.log(res);
          let data1 = {};
          data1.login_name = data.userName;
          data1.password = data.password;
          data1.sign = res.data.data;
          // test,,,,,,
          // data1.sign = JSON.stringify(res.data.data);
          // data1.sign = res.data.data.sign;
          // data1.sign = JSON.stringify(res.data.data.sign);
          $http
            .post('http://192.168.3.40:8080/login', data1)
            .then(res => {
              console.log(res);
            })
            .catch(err => {
              console.log(err);
            });
        })
        .catch(err => {
          console.log(err);
        });
    }
  }
};
</script>
