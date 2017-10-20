<template>
  <div id="app" class="main">
    <div class="Orders">
      <router-view @getValue="getValue"></router-view>
    </div>
    <!--底部公共样式-->
    <div class="supportIcon">
      <img src="./assets/images/toLogin01.png" v-if="toLogin1 == 'ceshi'" @click="handleClick">
      <img src="./assets/images/supprot-black.png" v-else @click="handleClick">
    </div>
    <div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'app',
  data(){
    return{
      toLogin1:'',

    }
  },
  methods:{
    getValue(val){
      this.toLogin1=val;
    },
    handleClick(){
      console.log(this.toLogin1)
    }
  }

}
</script>

<style>

</style>


