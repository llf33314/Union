<template>
  <div id="app" class="main">
    <div class="Orders">
      <router-view @getValue="getValue"></router-view>
    </div>
    <!--底部公共样式-->
    <div class="supportIcon">
      <img src="./assets/images/supprot-black.png">
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
    }
  },
  methods:{
    getValue(val){
      this.toLogin1=val;
    }
  }

}
</script>

<style>

</style>


