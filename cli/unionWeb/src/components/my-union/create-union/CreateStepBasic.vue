<template>
  <div id="wodelm">
    <p class="union_set">请填写基础信息</p>
    <el-form :label-position="labelPosition" label-width="125px" :model="form" :rules="rules" ref="form">
      <el-form-item label="企业名称：" prop="enterpriseName">
        <el-col :span="6">
          <el-input v-model="form.enterpriseName" placeholder="请输入企业名称"></el-input>
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
      <el-form-item label="手机短信通知：" prop="notifyPhone">
        <el-col :span="6">
          <el-input v-model="form.notifyPhone" placeholder="请输入接收短信手机号"></el-input>
        </el-col>
        <el-tooltip content="该手机短信通知可用于催付可提佣金、盟员退盟通知" placement="right">
          <span class="tubiao">!</span>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="我的地址：" prop="enterpriseAddress">
        <el-col :span="6">
          <div class="dingwei">
            <el-input v-model="form.enterpriseAddress" icon="search" :on-icon-click="handleIconClick" placeholder="请输入详细地址" disabled>
            </el-input>
            <union-map @mapClick="mapClick" v-show="mapShow" ref="unionMap" :address="form.enterpriseAddress"></union-map>
          </div>
        </el-col>
      </el-form-item>
    </el-form>
    <div class="footer_public">
      <el-button type="primary" @click="submitForm('form')">下一步</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { enterpriseNamePass, directorNamePass, cellPhonePass, emailPass } from '@/utils/validator.js';
import UnionMap from '@/components/public-components/UnionMap';
export default {
  name: 'create-step-basic',
  components: {
    UnionMap
  },
  data() {
    return {
      childrenData: '',
      labelPosition: 'right',
      mapShow: false,
      form: {
        enterpriseAddress: ''
      },
      rules: {
        enterpriseName: [{ validator: enterpriseNamePass, trigger: 'blur' }],
        directorName: [{ alidator: directorNamePass, trigger: 'blur' }],
        directorPhone: [{ validator: cellPhonePass, trigger: 'blur' }],
        directorEmail: [{ validator: emailPass, trigger: 'blur' }],
        notifyPhone: [{ validator: cellPhonePass, trigger: 'blur' }],
        enterpriseAddress: [{ required: true, message: '我的地址内容不能为空，请重新输入', trigger: 'change' }]
      }
    };
  },
  methods: {
    mapClick() {
      this.form.enterpriseAddress = this.$store.state.enterpriseAddress;
      this.form.addressLatitude = this.$store.state.addressLatitude;
      this.form.addressLongitude = this.$store.state.addressLongitude;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.childrenData = 1;
          this.$emit('activeChange', this.childrenData);
          this.$emit('basicForm', this.form);
        } else {
          return false;
        }
      });
    },
    back() {
      this.$router.push({ path: '/my-union' });
    },
    handleIconClick() {
      this.mapShow = !this.mapShow;
      this.$refs.unionMap.init();
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.percent {
  position: absolute;
  top: 1px;
  left: 250px;
}

/*一个图标*/

.tubiao {
  width: 25px;
  height: 25px;
  border: 1px solid #1c8de0;
  border-radius: 50%;
  display: block;
  text-align: center;
  line-height: 25px;
  color: #1d90e6;
  font-weight: bold;
  position: absolute;
  top: 5px;
  left: 250px;
  cursor: pointer;
}

/*地图的样式*/

#container {
  width: 700px;
  height: 290px;
  border: 1px solid #bfcbd9;
  margin-top: 10px;
}
</style>

