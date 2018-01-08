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
    $(window).on('resize', function() {
      /* 这里添加执行代码 */
      resizeBannerImage();
    });
    let resizeBannerImage = function() {
      var winW = parseInt($(window).width()) - 190;
    };
  }
};
</script>

<style>

</style>


