<template>
  <div class="Breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/my-union' }">
        <span class="myunion">我的联盟</span>
      </el-breadcrumb-item>
      <el-breadcrumb-item v-for="item in HeaderName" :key="item">{{ item }}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>
<script>
export default {
  props: {
    HeaderName: Array
  }
};
</script>
<style>

</style>
