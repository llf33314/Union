<template>
  <div>
    <div @click="showToggle" style="border:1px solid">
      <div :class="'m'+myColor">
      </div>
    </div>
    <!-- 备选色列表 -->
    <el-radio-group v-model="color" v-show="visible" @change="colorSelect">
      <!-- blue -->
      <el-radio-button label="008FD7">
        <template slot-scope="scope">
          <div class="m008FD7">
          </div>
        </template>
      </el-radio-button>
      <!-- green -->
      <el-radio-button label="019762">
        <template slot-scope="scope">
          <div class="m019762">
          </div>
        </template>
      </el-radio-button>
      <!-- blue-green -->
      <el-radio-button label="45B0A0">
        <template slot-scope="scope">
          <div class="m45B0A0">
          </div>
        </template>
      </el-radio-button>
      <!-- brown -->
      <el-radio-button label="C88D6E">
        <template slot-scope="scope">
          <div class="mC88D6E">
          </div>
        </template>
      </el-radio-button>
      <!-- red -->
      <el-radio-button label="C1231F">
        <template slot-scope="scope">
          <div class="mC1231F">
          </div>
        </template>
      </el-radio-button>
    </el-radio-group>
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
</style>

