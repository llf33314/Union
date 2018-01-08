<template>
  <div id="app">
      <transition :name="transitionName">
        <router-view  class="child-view"></router-view>
      </transition>
  </div>
</template>

<script>
export default {
  name: 'app',
  data(){
    return{
      transitionName: 'slide-left',
    }
  },
  watch: {
    '$route' (to, from) {
      const toDepth = to.path.split('/').length
      const fromDepth = from.path.split('/').length
      this.transitionName = toDepth < fromDepth ? 'slide-right' : 'slide-left'
    }
  }
}
</script>

<style scoped>
  .child-view {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    transition: all .5s cubic-bezier(.55, 0, .1, 1);
  }

  .slide-left-enter,
  .slide-right-leave-active {
    opacity: 0;
    -webkit-transform: translate(30px, 0);
    transform: translate(30px, 0);
  }

  .slide-left-leave-active,
  .slide-right-enter {
    opacity: 0;
    -webkit-transform: translate(-30px, 0);
    transform: translate(-30px, 0);
  }

</style>


