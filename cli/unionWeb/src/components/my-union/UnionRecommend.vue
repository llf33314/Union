<template>
  <div>
    <Breadcrumb :header-name="'推荐入盟'"></Breadcrumb>
    <div class="tabs">
      <h4 class="union_set">推荐盟员加入联盟</h4>
      <el-form :label-position="labelPosition" label-width="100px" :model="form" :rules="rules" ref="form">
        <el-form-item label="联盟账号:" prop="busUserName">
          <el-col :span="6">
            <el-input v-model="form.busUserName" placeholder="请输入联盟账号"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="申请企业：" prop="enterpriseName">
          <el-col :span="6">
            <el-input v-model="form.enterpriseName" placeholder="请输入申请入盟的企业名称"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="负责人：" prop="directorName">
          <el-col :span="6">
            <el-input v-model="form.directorName" placeholder="请输入负责人"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="联系电话：" prop="directorPhone">
          <el-col :span="6">
            <el-input v-model="form.directorPhone" placeholder="请输入联系电话"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="邮箱：" prop="directorEmail">
          <el-col :span="6">
            <el-input v-model="form.directorEmail" placeholder="请输入邮箱"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="推荐理由：" prop="reason">
          <textarea :rows="4" placeholder="请输入推荐理由" v-model="form.reason"
          :maxlength="unionNoticeMaxlength" @focus="unionNoticeFocus" @blur="unionNoticeBlur" @change="unionNoticeKeydown($event)" @keydown="unionNoticeKeydown($event)" @keyup="unionNoticeKeydown($event)" @input="unionNoticeKeydown($event)" @onpropertychange="unionNoticeKeydown($event)"></textarea>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit('form')">保存</el-button>
          <el-button @click="back">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import Breadcrumb from '@/components/public-components/Breadcrumb';
export default {
  name: 'union-recommend',
  components: {
    Breadcrumb
  },
  data() {
    // 验证规则
    let directorPhonePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('联系电话内容不能为空，请重新输入'));
      } else if (!value.match(/(^1[3|4|5|6|7|8][0-9][0-9]{8}$)|(^0\d{2,3}-?\d{7,8}$)/)) {
        callback(new Error('请输入正确的联系电话'));
      } else {
        callback();
      }
    };
    let emailPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('邮箱内容不能为空，请重新输入'));
      } else if (!value.match(/^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/)) {
        callback(new Error('请输入正确的邮箱'));
      } else {
        callback();
      }
    };
    return {
      labelPosition: 'right',
      form: {},
      rules: {
        busUserName: [{ required: true, message: '联盟账号内容不能为空，请重新输入', trigger: 'blur' }],
        enterpriseName: [{ required: true, message: '申请企业内容不能为空，请重新输入', trigger: 'blur' }],
        directorName: [{ required: true, message: '负责人内容不能为空，请重新输入', trigger: 'blur' }],
        directorPhone: [{ validator: directorPhonePass, trigger: 'blur' }],
        directorEmail: [{ validator: emailPass, trigger: 'blur' }],
        reason: [{ required: true, message: '推荐理由不能为空，请重新输入', trigger: 'blur' }]
      },
      unionNoticeMaxlength: 40,
      checkList: ['busUserName']
    };
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    },
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    // 获取必填字段
    $http.get(`/unionMainDict/${this.unionId}`).then(res => {
      if (res.data.data) {
        res.data.data.forEach((v, i) => {
          if (this.checkList.indexOf(v.itemKey) === -1) {
            this.checkList.push(v.itemKey);
          }
        });
      }
      for (let key in this.rules) {
        if (this.checkList.indexOf(key) === -1) {
          this.rules[key] = [];
        }
      }
    });
  },
  methods: {
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `unionMemberJoin/memberId/${this.unionMemberId}`;
          // 处理数据
          let data = {};
          data = this.form;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '推荐成功', type: 'success', duration: 5000 });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
        } else {
          return false;
        }
      });
    },
    back() {
      this.$router.push({ path: '/my-union/index' });
    },
    // 判断推荐理由字数
    unionNoticeFocus() {
      let valueLength = this.unionNoticeCheck()[0];
      let len = this.unionNoticeCheck()[1];
      if (valueLength > 20) {
        this.form.reason = this.form.reason.substring(0, len + 1);
        this.unionNoticeMaxlength = len;
        return false;
      } else {
        this.unionNoticeMaxlength = 40;
      }
    },
    unionNoticeBlur() {
      let valueLength = this.unionNoticeCheck()[0];
      if (valueLength > 20) {
        return false;
      }
    },
    unionNoticeKeydown(e) {
      let valueLength = this.unionNoticeCheck()[0];
      let len = this.unionNoticeCheck()[1];
      if (valueLength > 20) {
        this.form.reason = this.form.reason.substring(0, len + 1);
        this.unionNoticeMaxlength = len;
        return false;
      } else {
        this.unionNoticeMaxlength = 40;
      }
    },
    unionNoticeCheck() {
      let valueLength = 0;
      let maxLenth = 0;
      let chinese = '[\u4e00-\u9fa5]'; // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
      let str = this.form.reason;
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
        if (Math.ceil(valueLength) == 20) {
          maxLenth = i;
        }
      }
      valueLength = Math.ceil(valueLength); //进位取整
      return [valueLength, maxLenth];
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
.tabs {
  margin: 40px 50px;
}
</style>
