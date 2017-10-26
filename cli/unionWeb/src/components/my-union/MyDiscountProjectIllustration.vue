<template>
  <div id="itemDescription">
    <div>项目说明</div>
    <el-input type="textarea"  id="Illustration" v-model="Illustration" type="textarea" :rows="3" :maxlength="IllustrationMaxlength" @focus="IllustrationFocus" @blur="IllustrationBlur" @change="IllustrationKeydown($event)" @keydown="IllustrationKeydown($event)" @keyup="IllustrationKeydown($event)" @input="IllustrationKeydown($event)" @onpropertychange="IllustrationKeydown($event)" placeholder="这是项目说明">
    </el-input>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'illustration',
  data() {
    return {
      Illustration: '',
      IllustrationMaxlength: 100,
      projectId: '',
    }
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    $http.get(`/unionPreferentialProject/myProject/memberId/${this.unionMemberId}`)
      .then(res => {
        if (res.data.data) {
          if (res.data.data.project) {
            this.Illustration = res.data.data.project.illustration;
            this.projectId = res.data.data.project.id;
          };
        } else {
          this.Illustration = '';
          this.projectId = '';
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      })
  },
  methods: {
    IllustrationFocus() {
      document.querySelector("#Illustration textarea.el-textarea__inner").style.border = "1px solid #ddd";
      let valueLength = this.IllustrationCheck()[0];
      let len = this.IllustrationCheck()[1];
      if (valueLength > 50) {
        this.illustration = this.illustration.substring(0, len + 1);
        this.unionNoticeMaxlength = len;
        return false;
      } else {
        this.unionNoticeMaxlength = 100;
      }
    },
    IllustrationBlur() {
      let valueLength = this.IllustrationCheck()[0];
      if (valueLength > 50) {
        return false;
      } else {
        document.querySelector("#Illustration textarea.el-textarea__inner").style.border = "none";
        let url = `unionPreferentialProject/${this.projectId}/memberId/${this.unionMemberId}`;
        let data = this.Illustration;
        $http.put(url, data)
          .then(res => {
            $http.get(`/unionPreferentialProject/myProject/memberId/${this.unionMemberId}`)
              .then(res => {
                if (res.data.data) {
                  if (res.data.data.project) {
                    this.Illustration = res.data.data.project.illustration;
                    this.projectId = res.data.data.project.id;
                  };
                } else {
                  this.Illustration = '';
                  this.projectId = '';
                };
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              })
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          })
      }
    },
    IllustrationKeydown(e) {
      let valueLength = this.IllustrationCheck()[0];
      let len = this.IllustrationCheck()[1];
      if (valueLength > 50) {
        // this.Illustration = this.illustration.substring(0, len + 1);
        this.IllustrationMaxlength = len;
        return false;
      } else {
        this.IllustrationMaxlength = 100;
      }
    },
    IllustrationCheck() {
      let valueLength = 0;
      let maxLenth = 0;
      let chinese = "[\u4e00-\u9fa5]";  // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
      let str = this.Illustration || '';
      for (let i = 0; i < str.length; i++) { // 获取一个字符
        let temp = str.substring(i, i + 1); // 判断是否为中文字符
        if (temp.match(chinese)) { // 中文字符长度为1
          valueLength += 1;
        } else { // 其他字符长度为0.5
          valueLength += 0.5;
        };
        if (Math.ceil(valueLength) == 50) {
          maxLenth = i;
        };
      }
      valueLength = Math.ceil(valueLength); //进位取整
      return [valueLength, maxLenth];
    },
  }
}
</script>

