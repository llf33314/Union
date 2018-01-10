<template>
  <div id="grid-content-notice">
    <div class="grid-content bg-purple notice-list">联盟公告</div>
    <el-input type="textarea" id="unionNotice" v-model="unionNotice" :rows="3" :maxlength="unionNoticeMaxlength" @focus="unionNoticeFocus" @blur="unionNoticeBlur" @change="unionNoticeKeydown($event)" @keydown="unionNoticeKeydown($event)" @keyup="unionNoticeKeydown($event)" @input="unionNoticeKeydown($event)" @onpropertychange="unionNoticeKeydown($event)" placeholder="这是一条联盟公告" v-if="isUnionOwner" style="border: none;width: 99%;height: 83px;resize:none">
    </el-input>
    <span v-if="!isUnionOwner">{{ unionNotice }}</span>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { lengthCheck } from '@/utils/length-check.js';
export default {
  name: 'union-notice',
  data() {
    return {
      unionNotice: '',
      unionNoticeMaxlength: 160
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    if (this.unionId) {
      this.init();
    }
  },
  watch: {
    unionId: function() {
      if (this.unionId) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      $http
        .get(`/unionMainNotice/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.unionNotice = res.data.data.content;
          } else {
            this.unionNotice = '';
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 处理公告
    unionNoticeFocus() {
      if (this.isUnionOwner) {
        document.querySelector('#unionNotice').style.border = '1px solid #ddd';
        let valueLength = lengthCheck(this.unionNotice, 80)[0];
        let len = lengthCheck(this.unionNotice, 80)[1];
        if (valueLength > 80) {
          this.unionNotice = this.unionNotice.substring(0, len + 1);
          this.unionNoticeMaxlength = len;
          return false;
        } else {
          this.unionNoticeMaxlength = 160;
        }
      }
    },
    unionNoticeBlur() {
      document.querySelector('#unionNotice').style.border = 'none';
      let valueLength = lengthCheck(this.unionNotice, 80)[0];
      if (valueLength > 80) {
        return false;
      } else {
        $http
          .put(`/unionMainNotice/unionId/${this.unionId}`, this.unionNotice)
          .then(res => {})
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    unionNoticeKeydown(e) {
      let valueLength = lengthCheck(this.unionNotice, 80)[0];
      let len = lengthCheck(this.unionNotice, 80)[1];
      if (valueLength > 80) {
        this.unionNoticeMaxlength = len;
        return false;
      } else {
        this.unionNoticeMaxlength = 160;
      }
    }
  }
};
</script>

<style scoped lang='less' rel="stylesheet/less">
  .grid-content {
    font-family: 'Microsoft YaHei';
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 13px;
  }
</style>
