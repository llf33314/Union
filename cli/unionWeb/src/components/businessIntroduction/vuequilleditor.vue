<template>
  <quill-editor v-model="form.content" :options="editorOption" @change="onEditorChange">
    <div id="toolbar" slot="toolbar">
      <button class="ql-bold">粗体</button>
      <button class="ql-italic">斜体</button>
      <!--<button class="ql-underline">下划线</button>-->
      <!--<button class="ql-strike">删除线</button>-->
      <select class="ql-size newBorder">
        <option value="small">12px</option>
        <option selected="selected">14px</option>
        <option value="large">16px</option>
        <option value="huge">18px</option>
      </select>
      <!--<button class="ql-blockquote"></button>-->
      <!--<button class="ql-code-block"></button>-->
      <button class="ql-list ql-active" value="ordered"></button>
      <button class="ql-list" value="bullet"></button>
      <button class="ql-script" value="sub"></button>
      <button class="ql-script" value="super"></button>
      <button class="ql-indent" value="-1"></button>
      <button class="ql-indent" value="+1"></button>
      <!--<button class="ql-direction" value="rtl"></button>-->
      <select class="ql-header newBorder">
        <option value="1">h1</option>
        <option value="2">h2</option>
        <option value="3">h3</option>
        <option value="4">h4</option>
        <option value="5">h5</option>
        <option value="6">h6</option>
        <option selected="selected">p</option>
      </select>
      <select class="ql-color">
        <option selected="selected"></option>
        <option value="#e60000"></option>
        <option value="#ff9900"></option>
        <option value="#ffff00"></option>
        <option value="#008a00"></option>
        <option value="#0066cc"></option>
        <option value="#9933ff"></option>
        <option value="#ffffff"></option>
        <option value="#facccc"></option>
        <option value="#ffebcc"></option>
        <option value="#ffffcc"></option>
        <option value="#cce8cc"></option>
        <option value="#cce0f5"></option>
        <option value="#ebd6ff"></option>
        <option value="#bbbbbb"></option>
        <option value="#f06666"></option>
        <option value="#ffc266"></option>
        <option value="#ffff66"></option>
        <option value="#66b966"></option>
        <option value="#66a3e0"></option>
        <option value="#c285ff"></option>
        <option value="#888888"></option>
        <option value="#a10000"></option>
        <option value="#b26b00"></option>
        <option value="#b2b200"></option>
        <option value="#006100"></option>
        <option value="#0047b2"></option>
        <option value="#6b24b2"></option>
        <option value="#444444"></option>
        <option value="#5c0000"></option>
        <option value="#663d00"></option>
        <option value="#666600"></option>
        <option value="#003700"></option>
        <option value="#002966"></option>
        <option value="#3d1466"></option>
      </select>
      <select class="ql-background">
        <option value="#000000"></option>
        <option value="#e60000"></option>
        <option value="#ff9900"></option>
        <option value="#ffff00"></option>
        <option value="#008a00"></option>
        <option value="#0066cc"></option>
        <option value="#9933ff"></option>
        <option selected="selected"></option>
        <option value="#facccc"></option>
        <option value="#ffebcc"></option>
        <option value="#ffffcc"></option>
        <option value="#cce8cc"></option>
        <option value="#cce0f5"></option>
        <option value="#ebd6ff"></option>
        <option value="#bbbbbb"></option>
        <option value="#f06666"></option>
        <option value="#ffc266"></option>
        <option value="#ffff66"></option>
        <option value="#66b966"></option>
        <option value="#66a3e0"></option>
        <option value="#c285ff"></option>
        <option value="#888888"></option>
        <option value="#a10000"></option>
        <option value="#b26b00"></option>
        <option value="#b2b200"></option>
        <option value="#006100"></option>
        <option value="#0047b2"></option>
        <option value="#6b24b2"></option>
        <option value="#444444"></option>
        <option value="#5c0000"></option>
        <option value="#663d00"></option>
        <option value="#666600"></option>
        <option value="#003700"></option>
        <option value="#002966"></option>
        <option value="#3d1466"></option>
      </select>
      <select class="ql-font newBorder">
        <option selected="selected">微软雅黑</option>
        <option value="serif">宋体</option>
        <option value="monospace">黑体</option>
      </select>
      <select class="ql-align">
        <option selected="selected"></option>
        <option value="center"></option>
        <option value="right"></option>
        <option value="justify"></option>
      </select>
      <!--<button class="ql-clean"></button>-->
      <!--<button class="ql-link"></button>-->
      <button @click="materiallayer('2')">
        <svg viewBox="0 0 18 18">
          <rect class="ql-stroke" height="10" width="12" x="3" y="4"></rect>
          <circle class="ql-fill" cx="6" cy="7" r="1"></circle>
          <polyline class="ql-even ql-fill" points="5 12 5 11 7 9 8 10 11 7 13 9 13 12 5 12"></polyline>
        </svg>
      </button>
      <!--<button class="ql-video"></button>-->

      <el-dialog title="素材库" :visible.sync="form.dialogVisible" class="newFrom">
        <iframe class="uploadIframe" :src="form.materialUrl"></iframe>
      </el-dialog>
    </div>
  </quill-editor>
</template>

<script>
  import Vue from 'vue'
  import  VueQuillEditor from 'vue-quill-editor'
  Vue.use(VueQuillEditor)
  export default {
    name: 'vuequilleditor',
    data () {
      return {
        form: {
          chooseStatus: '',
          dialogVisible: false,
          materialUrl: '',
          content: '',
        },
        editorOption: {
          modules: {
            toolbar: '#toolbar'
          }
        }
      }
    },
    methods: {
      materiallayer(status) {
        this.form.dialogVisible = true;
        this.form.materialUrl =  this.$store.state.materialUrl + window.location.href;
      },
      onEditorChange() {    // 编辑器文本发生变化
        this.$emit('getValue', this.form)
      }
    },
    created() {
      this.$emit('getValue', this.form)
    }
  }
</script>

<style>

</style>
