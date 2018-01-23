<template>
  <div>
    <p class="union_set">请填写基础信息</p>
    <el-form :label-position="labelPosition" label-width="125px" :model="form" :rules="rules" ref="form">
      <el-form-item label="企业名称：" prop="enterpriseName">
        <el-input v-model="form.enterpriseName" placeholder="请输入企业名称"></el-input>
      </el-form-item>
      <el-form-item label="负责人：" prop="directorName">
        <el-input v-model="form.directorName" placeholder="请输入负责人"></el-input>
      </el-form-item>
      <el-form-item label="联系电话：" prop="directorPhone">
        <el-input v-model="form.directorPhone" placeholder="请输入联系电话"></el-input>
      </el-form-item>
      <el-form-item label="邮箱：" prop="directorEmail">
        <el-input v-model="form.directorEmail" placeholder="请输入邮箱"></el-input>
      </el-form-item>
      <el-form-item label="手机短信通知：" prop="notifyPhone">
        <el-input v-model="form.notifyPhone" placeholder="请输入接收短信手机号"></el-input>
        <el-tooltip content="该手机短信通知可用于催付可提佣金、盟员退盟通知" placement="right">
          <span class="tubiao">!</span>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="积分折扣率：" :prop="this.isIntegral && 'integralExchangeRatio'" v-if="this.isIntegral">
        <el-input v-model="form.integralExchangeRatio" placeholder="积分折扣率不可超过30%"></el-input>
        <span class="percent">%</span>
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
      <el-button type="primary" @click="submitForm('form')">保存</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import {
  enterpriseNamePass,
  directorNamePass,
  cellPhonePass,
  emailPass,
  integralExchangeRatioPass
} from '@/utils/validator.js';
import UnionMap from '@/components/public-components/UnionMap';
export default {
  name: 'union-setting-basic',
  components: {
    UnionMap
  },
  data() {
    return {
      childrenData: '',
      labelPosition: 'right',
      mapShow: false,
      form: {
        enterpriseAddress: '' // 处理无地址内容时刚进页面触发校验chang
      },
      rules: {
        enterpriseName: [{ validator: enterpriseNamePass, trigger: 'blur' }],
        directorName: [{ validator: directorNamePass, trigger: 'blur' }],
        directorPhone: [{ validator: cellPhonePass, trigger: 'blur' }],
        directorEmail: [{ validator: emailPass, trigger: 'blur' }],
        notifyPhone: [{ validator: cellPhonePass, trigger: 'blur' }],
        integralExchangeRatio: [{ validator: integralExchangeRatioPass, trigger: 'blur' }],
        enterpriseAddress: [{ required: true, message: '我的地址内容不能为空，请重新输入', trigger: 'change' }]
      },
      isIntegral: ''
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    this.init();
    eventBus.$on('unionSettingTabChange', () => {
      this.init();
    });
  },
  methods: {
    init() {
      $http
        .get(`/unionMember/unionId/${this.unionId}/busUser`)
        .then(res => {
          if (res.data.data) {
            // 处理无地址内容时刚进页面触发校验chang
            if (!res.data.data.member.enterpriseAddress) {
              res.data.data.member.enterpriseAddress = '';
            }
            this.form = res.data.data.member;
            this.isIntegral = res.data.data.union.isIntegral;
            this.form.integralExchangeRatio = (this.form.integralExchangeRatio * 100 || 0).toFixed(2);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    mapClick() {
      this.form.enterpriseAddress = this.$store.state.enterpriseAddress;
      this.form.addressLatitude = this.$store.state.addressLatitude;
      this.form.addressLongitude = this.$store.state.addressLongitude;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionMember/${this.form.id}/unionId/${this.unionId}`;
          // 处理要提交的数据
          let data = {};
          data.addressLongitude = this.form.addressLongitude;
          data.addressLatitude = this.form.addressLatitude;
          data.addressProvinceCode = this.form.addressProvinceCode;
          data.addressCityCode = this.form.addressCityCode;
          data.addressDistrictCode = this.form.addressDistrictCode;
          data.enterpriseAddress = this.form.enterpriseAddress;
          data.enterpriseName = this.form.enterpriseName;
          data.directorName = this.form.directorName;
          data.directorPhone = this.form.directorPhone;
          data.directorEmail = this.form.directorEmail;
          data.integralExchangeRatio = this.form.integralExchangeRatio / 100;
          data.notifyPhone = this.form.notifyPhone;
          $http
            .put(url, data)
            .then(res => {
              if (res.data.success) {
                eventBus.$emit('unionUpdata');
                this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 3000 });
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
    },
    handleIconClick(ev) {
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
  left: 305px;
}

/*地图的样式*/

#container {
  width: 700px;
  height: 290px;
  border: 1px solid #bfcbd9;
  margin-top: 10px;
}
</style>

