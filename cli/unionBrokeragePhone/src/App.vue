<template>
  <div id="app" class="main">
    <div class="Orders">
      <transition :name="transitionName">
        <router-view @getValue="getValue" class="child-view"></router-view>
      </transition>
    </div>
    <!--底部公共样式-->
    <div class="supportIcon">
      <img src="./assets/images/toLogin01.png" alt="" v-if="toLogin1 == 'ceshi'" @click="handleClick">
      <img src="./assets/images/supprot-black.png" alt="" v-else @click="handleClick">
    </div>
  </div>
</template>

<script>
export default {
  name: 'app',
  data(){
    return{
      transitionName: 'slide-left',
      toLogin1:'',

    }
  },
  watch: {
    '$route' (to, from) {
      const toDepth = to.path.split('/').length
      const fromDepth = from.path.split('/').length
      this.transitionName = toDepth < fromDepth ? 'slide-right' : 'slide-left'
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

<style scoped>
  .child-view {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
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


