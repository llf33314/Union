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
          <el-input v-model="form.notifyPhone" placeholder="请输入请输入接收短信手机号"></el-input>
        </el-col>
        <el-tooltip content="该手机短信通知可用于催付可提佣金、盟员退盟通知" placement="right">
          <span class="tubiao">!</span>
        </el-tooltip>
      </el-form-item>
      <!-- <el-form-item label="积分折扣率：" prop="integralExchangePercent">
        <el-col :span="6">
          <el-input v-model="form.integralExchangePercent" placeholder="积分折扣率不可超过30%"></el-input>
        </el-col>
        <span class="percent">%</span>
      </el-form-item> -->
      <el-form-item label="地区：" prop="region">
        <el-col :span="6">
          <region-choose v-model="form.region" @regionChange="regionChange"></region-choose>
        </el-col>
      </el-form-item>
      <el-form-item label="我的地址：" prop="enterpriseAddress">
        <el-col :span="6">
          <div class="dingwei">
            <el-input v-model="form.enterpriseAddress" icon="search" :on-icon-click="handleIconClick" placeholder="请输入详细地址" disabled>
            </el-input>
            <t-map @mapClick="mapClick" v-show="mapShow"></t-map>
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
import $http from '@/utils/http.js'
import RegionChoose from '@/components/public-components/RegionChoose'
import TMap from '@/components/public-components/TMap'

export default {
  name: 'create-step-basic',
  components: {
    RegionChoose,
    TMap
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
    let notifyPhonePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('手机短信通知内容不能为空，请重新输入'));
      } else if (!value.match(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    let integralExchangePercentPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('积分折扣率内容不能为空，请重新输入'));
      } else if (isNaN(value)) {
        callback(new Error('积分折扣率必须为数字值，请重新输入'));
      } else if (value < 0) {
        callback(new Error('积分折扣率不能小于0%，请重新输入'));
      } else if (value > 30) {
        callback(new Error('积分折扣率不能大于30%，请重新输入'));
      } else {
        callback();
      }
    };
    return {
      childrenData: '',
      labelPosition: 'right',
      mapShow: false,
      form: {
        enterpriseAddress: '',
      },
      rules: {
        enterpriseName: [
          { required: true, message: '企业名称内容不能为空，请重新输入', trigger: 'blur' }
        ],
        directorName: [
          { required: true, message: '负责人内容不能为空，请重新输入', trigger: 'blur' }
        ],
        directorPhone: [
          { validator: directorPhonePass, trigger: 'blur' }
        ],
        directorEmail: [
          { validator: emailPass, trigger: 'blur' }
        ],
        notifyPhone: [
          { validator: notifyPhonePass, trigger: 'blur' }
        ],
        // integralExchangePercent: [
        //   { validator: integralExchangePercentPass, trigger: 'blur' }
        // ],
        region: [
          { type: 'array', required: true, message: '地区内容不能为空，请重新输入', trigger: 'change' }
        ],
        enterpriseAddress: [
          { required: true, message: '我的地址内容不能为空，请重新输入', trigger: 'change' }
        ],
      },
    }
  },
  methods: {
    regionChange(v) {
      this.form.addressProvinceCode = v[0].split(',')[1] || '';
      this.form.addressCityCode = v[1].split(',')[1] || '';
      this.form.addressDistrictCode = v[2].split(',')[1] || '';
      this.form.region = [this.form.addressProvinceCode, this.form.addressCityCode, this.form.addressDistrictCode];
      this.form.enterpriseAddress = '';
    },
    mapClick() {
      this.form.enterpriseAddress = this.$store.state.enterpriseAddress;
      this.form.addressLatitude = this.$store.state.addressLatitude;
      this.form.addressLongitude = this.$store.state.addressLongitude;
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
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
    handleIconClick(ev) {
      this.mapShow = !this.mapShow;
    },
  },

}
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

