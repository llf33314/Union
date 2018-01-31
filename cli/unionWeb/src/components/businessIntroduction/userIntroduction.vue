<template>
  <div id="merchantDescription">
    <div v-loading.fullscreen.lock="fullscreenLoading1" element-loading-text="页面加载中...">
    </div>
    <div v-loading.fullscreen.lock="fullscreenLoading" element-loading-text="保存中...">
    </div>
    <div v-show="isLode">
      <nav class="descriptionNav">商家简介</nav>
      <p class="nav">下面的编辑信息，编辑完之后，将在商家联盟粉丝端进行展示</p>
        <div class="dr" style="height: 520px;">
          <vqEditor @getValue = "getContent"></vqEditor>
        </div>
      <div class="save">
          <el-button type="primary" @click="comfirmAddInfo">保存</el-button>
      </div>
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
        fullscreenLoading:false,
        fullscreenLoading1:true,
        isLode:false,
        theOldDataContent:''
      }
    },
    mounted () {
      var that = this
      var strs = new Array()
      window.addEventListener("message", function (e) {
        strs = e.data.replace(/[()'']/g,'').split(',');
        that.editor.list.dialogVisible = false;
        document.querySelector(".ql-editor").innerHTML =
          that.editor.list.content + '<img class="ql_image" src="'+strs[1]+'" />'
      })
    },
    methods: {
      //获取文本编辑器的内容
      getContent(list){
        this.editor.list = list
        if(this.theOldDataContent == "") {
          this.theOldDataContent = list.content
        };
      },
      //点击保存事件
      comfirmAddInfo() {
        var that = this;
        let data = {
          content: that.editor.list.content
        };
        if (that.theOldDataContent != data.content) {
          $http.post(`/unionUserIntroduction`,data)
            .then(res => {
                if (res.data.success) {
                  that.fullscreenLoading=true;
                  that.theOldDataContent = data.content;
                  setTimeout(() => {
                    that.fullscreenLoading=false;
                    that.$message({ showClose: true, message: '保存成功', type: 'success', duration: 3000 });
                  }, 300);
                }
            })
            .catch(err => {
              that.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
            });
        }else{
          that.fullscreenLoading=true;
          setTimeout(() => {
            that.fullscreenLoading=false;
            that.$message({ showClose: true, message: '保存成功', type: 'success', duration: 3000 });
          }, 300);
        }
      },
    },
    created() {
      var that=this;
      this.$emit('getValue', this.form);
      setTimeout(function () {
        that.fullscreenLoading1=false;
        that.isLode=true;
      },600)
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
