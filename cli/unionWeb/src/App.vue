<template>
  <div id="app">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="拼命加载中">
      <!--显示整页加载，3 秒后消失-->
    </div>
    <keep-alive include="my-union">
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
export default {
  name: 'app',
  data() {
    return {
      fullscreenLoading: true,
    }
  },
  mounted() {
    $(window).on('resize', function() {
      /* 这里添加执行代码 */
      resizeBannerImage();
    });
    let resizeBannerImage=function()
    {
      var winW = parseInt($(window).width())-190;
//      console.log('减去左边一，二级菜单的宽度后：右边Iframe 宽度为：'+winW+'px');
    }
  },
  created(){
    setTimeout(() => {
      this.fullscreenLoading = false;
    }, 1000);
  }
};
</script>

<style>

</style>


