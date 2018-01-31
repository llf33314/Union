<template>
  <div id="merchantDescription">
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="保存到微信小程序中...">
    </div>
      <nav class="descriptionNav">商家简介</nav>
      <p class="nav">下面的编辑信息，编辑完之后，将在商家联盟粉丝端进行展示</p>
        <div class="dr" style="height: 520px;">
          <vqEditor @getValue = "getContent"></vqEditor>
        </div>
      <div class="save">
          <el-button type="primary" @click="comfirmAddInfo">保存</el-button>
      </div>
  </div>
</template>

<script>
  import $http from '@/utils/http.js';
  import  vqEditor from './vuequilleditor.vue'
  export default {
    components: {
      vqEditor
    },
    data () {
      return {
        editor: {
          list: ''
        },
        fullscreenLoading:false
      }
    },
    mounted () {
      var _this = this
      var strs = new Array()
      window.addEventListener("message", function (e) {
        strs = e.data.replace(/[()'']/g,'').split(',');
        _this.editor.list.dialogVisible = false;
        document.querySelector(".ql-editor").innerHTML =
          _this.editor.list.content + '<img class="ql_image" src="'+strs[1]+'" />'
      })
    },
    methods: {
      //获取文本编辑器的内容
      getContent(list){
        this.editor.list = list
      },
      //点击保存事件
      comfirmAddInfo() {
        var that = this;
        that.fullscreenLoading=true;
        console.log("已保存 ")
        let data = {
          content: that.editor.list.content
        };
        $http.post(`/unionUserIntroduction`,data)
          .then(res => {
              if (res.data.success) {
                setTimeout(() => {
                  that.fullscreenLoading=false;
                  that.$message({ showClose: true, message: '保存成功，可到小程序观看', type: 'success', duration: 3000 });
                }, 300);
              }
          })
          .catch(err => {
            that.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      },
    },
    created() {
      this.$emit('getValue', this.form)
    }
  }
</script>

<style lang='less' rel="stylesheet/less">
  #merchantDescription {
    margin: 30px;
    .descriptionNav {
      padding-bottom: 10px;
      border-bottom: 1px solid #ddd;
      font-weight: bold;
    }
    .nav{
      margin: 20px 0;
    }
  }
  .el-dialog--small{
    width: 800px;
    height: 600px;
    .el-dialog__body{
      overflow: auto;
      iframe{
        width: 100%;
        min-height:450px ;
      }
    }
  }
  .save{
    margin: 100px 0 50px 147px;
  }
</style>
