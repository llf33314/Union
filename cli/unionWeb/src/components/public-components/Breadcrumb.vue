<template>
  <div class="Breadcrumb">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/my-union' }">
        <span class="myunion">我的联盟</span>
      </el-breadcrumb-item>
      <el-breadcrumb-item>{{ HeaderName }}</el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>
<script>
export default {
  props: ['HeaderName'],
}
</script>
<style>

</style>
