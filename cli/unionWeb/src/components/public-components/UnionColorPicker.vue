<template>
  <div>
    <div class="selectColorPublic" @click="showToggle" >
      <p class="triangleDown"></p>
      <div :class="'m'+myColor" style="">
      </div>
    </div>
    <!-- 备选色列表 -->
    <div class="OptionalColor">
      <el-radio-group v-model="color" v-show="visible" @change="colorSelect">
      <!-- blue -->
      <el-radio-button label="03ABFF,008CD4">
        <template slot-scope="scope">
          <div class="m008FD7">
          </div>
        </template>
      </el-radio-button>
      <!-- green -->
      <el-radio-button label="06BD7D,009560">
        <template slot-scope="scope">
          <div class="m019762">
          </div>
        </template>
      </el-radio-button>
      <!-- blue-green -->
      <el-radio-button label="4FC6B4,43AE9E">
        <template slot-scope="scope">
          <div class="m45B0A0">
          </div>
        </template>
      </el-radio-button>
      <!-- coffee -->
      <el-radio-button label="F6C9A5,C68A6A">
        <template slot-scope="scope">
          <div class="mC88D6E">
          </div>
        </template>
      </el-radio-button>
      <!-- red -->
      <el-radio-button label="E62229,BD231E">
        <template slot-scope="scope">
          <div class="mC1231F">
          </div>
        </template>
      </el-radio-button>
    </el-radio-group>
    </div>
  </div>
</template>

<script>
export default {
  name: 'union-color-picker',
  data() {
    return {
      visible: false,
      color: ''
    };
  },
  computed: {
    myColor() {
      return this.$store.state.myColor;
    }
  },
  methods: {
    showToggle() {
      this.visible = !this.visible;
    },
    colorSelect() {
      this.$emit('colorSelect', this.color);
      this.$store.commit('myColorChange', this.color);
    }
  }
};
</script>

<style scoped>
.m {
  width: 20px;
  height: 20px;
  background-color: #019762;
}
.m008FD7 {
  width: 20px;
  height: 20px;
  background-color: #008fd7;
}
.m019762 {
  width: 20px;
  height: 20px;
  background-color: #019762;
}
.m45B0A0 {
  width: 20px;
  height: 20px;
  background-color: #45b0a0;
}
.mC88D6E {
  width: 20px;
  height: 20px;
  background-color: #c88d6e;
}
.mC1231F {
  width: 20px;
  height: 20px;
  background-color: #c1231f;
}
.selectColorPublic{
  width: 56px;
  height: 32px;
  border-radius: 4px;
  border: solid 1px #bfcbd9;
  cursor: pointer;
}
/*绘制三角形*/
.selectColorPublic .triangleDown{
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 9px solid #BFCBD9;
  position: absolute;
  top: 12px;
  left: 34px;
}
.selectColorPublic>div{
  margin: 6px;
}

</style>

