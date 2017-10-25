<template>
  <div id="app">
    <keep-alive include="my-union">
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
export default {
  name: 'app',
  mounted() {
    $(document).ready(function() {
      firstLoad();
    });
    var firstLoad = function() {
      var widthWindow = $(window).width();
      console.log('屏幕宽度为：' + widthWindow);
    };
  }
};
</script>

<style>

</style>


