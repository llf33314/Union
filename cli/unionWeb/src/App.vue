<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'app',
  mounted(){
    $(document).ready(function () {
      firstLoad();
    })
    var firstLoad = function () {
      var widthWindow = $(window).width();
      console.log("屏幕宽度为："+widthWindow);
    }
  }
}

  name: 'app'
};
</script>

<style>

</style>


