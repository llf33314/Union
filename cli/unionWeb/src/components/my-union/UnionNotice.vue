<template>
  <div>
    <div class="grid-content bg-purple notice-list">联盟公告</div>
    <el-input type="textarea"  id="unionNotice" v-model="unionNotice":rows="3" :maxlength="unionNoticeMaxlength" @focus="unionNoticeFocus" @blur="unionNoticeBlur" @change="unionNoticeKeydown($event)" @keydown="unionNoticeKeydown($event)" @keyup="unionNoticeKeydown($event)" @input="unionNoticeKeydown($event)" @onpropertychange="unionNoticeKeydown($event)" placeholder="这是一条联盟公告" v-if="isUnionOwner" style="border: none;width: 99%;height: 72px;resize:none">
    </el-input>
    <span v-if="!isUnionOwner">{{ unionNotice }}</span>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'union-notice',
  data() {
    return {
      unionNotice: '',
      unionNoticeMaxlength: 100
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionId() {
      return this.$store.state.unionId;
    },
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    if (this.unionMemberId) {
      if (this.unionId) {
        this.init();
      }
    }
  },
  watch: {
    unionId: function() {
      if (this.unionMemberId) {
        if (this.unionId) {
          this.init();
        }
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
        let valueLength = this.unionNoticeCheck()[0];
        let len = this.unionNoticeCheck()[1];
        if (valueLength > 50) {
          this.unionNotice = this.unionNotice.substring(0, len + 1);
          this.unionNoticeMaxlength = len;
          return false;
        } else {
          this.unionNoticeMaxlength = 100;
        }
      }
    },
    unionNoticeBlur() {
      document.querySelector('#unionNotice').style.border = 'none';
      let valueLength = this.unionNoticeCheck()[0];
      if (valueLength > 50) {
        return false;
      } else {
        $http
          .put(`/unionMainNotice/memberId/${this.$store.state.unionMemberId}?noticeContent=${this.unionNotice}`)
          .then(res => {})
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    unionNoticeKeydown(e) {
      let valueLength = this.unionNoticeCheck()[0];
      let len = this.unionNoticeCheck()[1];
      if (valueLength > 50) {
        // this.unionNotice = this.unionNotice.substring(0, len + 1);
        this.unionNoticeMaxlength = len;
        return false;
      } else {
        this.unionNoticeMaxlength = 100;
      }
    },
    unionNoticeCheck() {
      let valueLength = 0;
      let maxLenth = 0;
      let chinese = '[\u4e00-\u9fa5]'; // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
      let str = this.unionNotice || '';
      for (let i = 0; i < str.length; i++) {
        // 获取一个字符
        let temp = str.substring(i, i + 1); // 判断是否为中文字符
        if (temp.match(chinese)) {
          // 中文字符长度为1
          valueLength += 1;
        } else {
          // 其他字符长度为0.5
          valueLength += 0.5;
        }
        if (Math.ceil(valueLength) == 50) {
          maxLenth = i;
        }
      }
      valueLength = Math.ceil(valueLength); //进位取整
      return [valueLength, maxLenth];
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
