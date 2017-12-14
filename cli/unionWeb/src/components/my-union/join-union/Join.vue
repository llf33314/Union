<template>
  <router-link :to="{ path: '/my-union/join-step', name: 'JoinStep'}" class="avatar-uploader">
    <i class="el-icon-plus"></i>
    <p class="el-bottom">去加入联盟</p>
  </router-link>
</template>

<script>
export default {
  name: 'join',
}
</script>

<style scoped>
.avatar-uploader {
  display: block;
  text-decoration: none;
  width: 80px;
  height: 80px;
  border: 1px dashed #c8d3df;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  font-size: 12px;
  text-align: center;
  background-color: #fbfdff;
}

.avatar-uploader:hover {
  border-color: #20a0ff;
}

.el-icon-plus {
  font-size: 16px;
  color: #8c939d;
  position: absolute;
  top: 50%;
  left: 50%;
  margin: -8px 0 0 -8px;
}

.el-bottom {
  margin-top: 55px;
  line-height: 20px;
  color: #a1a1a1;
}
</style>
