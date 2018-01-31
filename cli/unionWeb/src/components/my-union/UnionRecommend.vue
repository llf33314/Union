<template>
  <div id="recommended">
    <Breadcrumb :header-name="['推荐入盟']"></Breadcrumb>
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
          <el-input type="textarea" :rows="4" id="feedbackcontent" placeholder="请输入推荐理由" v-model="form.reason" ></el-input>
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
import { enterpriseNamePass, directorNamePass, cellPhonePass, emailPass, reasonPass } from '@/utils/validator.js';
import Breadcrumb from '@/components/public-components/Breadcrumb';
export default {
  name: 'union-recommend',
  components: {
    Breadcrumb
  },
  data() {
    return {
      labelPosition: 'right',
      form: {},
      rules: {
        busUserName: [{ required: true, message: '联盟账号内容不能为空，请重新输入', trigger: 'blur' }],
        enterpriseName: [{ validator: enterpriseNamePass, trigger: 'blur' }],
        directorName: [{ validator: directorNamePass, trigger: 'blur' }],
        directorPhone: [{ validator: cellPhonePass, trigger: 'blur' }],
        directorEmail: [{ validator: emailPass, trigger: 'blur' }],
        reason: [{ validator: reasonPass, trigger: 'blur' }]
      },
      checkList: ['busUserName'],
      datas: []
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  mounted: function() {
    // 获取必填字段
    $http
      .get(`/unionMain/${this.unionId}`)
      .then(res => {
        if (res.data.data) {
          let checkList_ = res.data.data.itemList;
          checkList_.forEach((v, i) => {
            if (this.checkList.indexOf(v.itemKey) === -1) {
              this.checkList.push(v.itemKey);
            }
          });
          for (let key in this.rules) {
            if (this.checkList.indexOf(key) === -1) {
              this.rules[key] = [];
            }
          }
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
      });
  },
  methods: {
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionMemberJoin/unionId/${this.unionId}/type/2`;
          // 处理数据
          let data = {};
          data = this.form;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '推荐成功', type: 'success', duration: 3000 });
                this.form = {};
                if (this.isUnionOwner) {
                  eventBus.$emit('unionUpdata');
                }
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
            });
        } else {
          return false;
        }
      });
    },
    back() {
      this.$router.push({ path: '/my-union/index' });
    }
  }
};
</script>
<style lang='less' rel="stylesheet/less" scoped>
.tabs {
  margin: 40px 50px;
}
</style>
