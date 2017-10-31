<template>
  <div id="createStep">
    <p class="union_set">联盟设置</p>
    <div class="second_">
      <el-form :model="form" :rules="rules" ref="form" label-width="200px" style="margin-left: -68px;">
      <el-form-item label="联盟名称：" prop="unionName">
        <el-input v-model="form.unionName" placeholder="请输入联盟名称"></el-input>
      </el-form-item>
      <el-form-item label="加入方式：">
        <el-radio-group v-model="form.joinType">
          <el-radio :label="1">联盟推荐</el-radio>
          <el-radio :label="2">联盟推荐与自由申请（仅针对多粉商家）</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="联盟积分：">
        <el-switch on-text="" off-text="" v-model="form.isIntegral"></el-switch>
        <el-tooltip content="开启联盟积分后，可让粉丝消费获得积分回扣，粉丝可使用积分抵扣金额，该功能开启后不可关闭" placement="right">
          <span class="tubiao">!</span>
        </el-tooltip>
      </el-form-item>
      <el-form-item label="黑卡收费：">
        <el-switch on-text="" off-text="" v-model="form.blackIsCharge"></el-switch>
      </el-form-item>
      <el-form-item label="老会员升级收费：">
        <el-switch on-text="" off-text="" v-model="form.oldMemberCharge"></el-switch>
      </el-form-item>
      <div class="Price_Card" v-if="form.blackIsCharge">
        <el-form-item label="黑卡价格：" prop="blackChargePrice">
          <el-input v-model="form.blackChargePrice">
            <template slot="prepend">￥</template>
          </el-input>
        </el-form-item>
      </div>
      <el-form-item label="黑卡时效：" prop="blackValidityDay" v-if="form.blackIsCharge">
        <div class="special">
          <el-input v-model="form.blackValidityDay" placeholder="请输入有效期（天）"></el-input>
        </div>
      </el-form-item>
      <el-form-item label="黑卡说明：" v-if="form.blackIsCharge">
        <el-input type="textarea" :rows="2" placeholder="请输入黑卡说明" v-model="form.blackIllustration">
        </el-input>
      </el-form-item>
      <el-form-item label="红卡：">
        <el-switch on-text="" off-text="" v-model="form.redIsAvailable"></el-switch>
      </el-form-item>
      <el-form-item label="红卡价格：" prop="redChargePrice" v-if="form.redIsAvailable">
        <div class="Price_Card">
          <el-input v-model="form.redChargePrice">
            <template slot="prepend">￥</template>
          </el-input>
        </div>
      </el-form-item>
      <el-form-item label="红卡时效：" prop="redValidityDay" v-if="form.redIsAvailable">
        <div class="special">
          <el-input v-model="form.redValidityDay" placeholder="请输入有效期（天）">
          </el-input>
        </div>
      </el-form-item>
      <el-form-item label="红卡说明：" v-if="form.redIsAvailable">
        <el-input type="textarea" :rows="3" placeholder="请输入红卡说明" v-model="form.redIllustration">
        </el-input>
      </el-form-item>
      <el-form-item label="联盟申请/推荐收集信息：" class="lianmeng">
        <el-checkbox-group v-model="checkList">
          <el-checkbox label="enterpriseName" disabled>企业名称</el-checkbox>
          <el-checkbox label="directorName">负责人</el-checkbox>
          <el-checkbox label="directorPhone" disabled>联系电话</el-checkbox>
          <el-checkbox label="directorEmail">邮箱</el-checkbox>
          <el-checkbox label="applyReason">申请/推荐理由</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="联盟图标："  prop="unionImg">
        <img v-if="this.form.unionImg" v-bind:src="this.form.unionImg" class="unionImg" @click="materiallayer">
        <!-- todo button 改变样式 -->
        <el-button @click="materiallayer" v-if="!this.form.unionImg">
          <i class="el-icon-plus"></i>
        </el-button>
        <span style="position: relative;top: 25px;left: 2px;color:#bbbbbb;" v-if="!this.form.unionImg">图片尺寸：100*100px</span>
        <el-dialog  title="素材库" :visible.sync="materialVisible">
          <hr>
          <iframe :src="materialUrl" width="95%" height="500" frameborder="0" scrolling="no"></iframe>
        </el-dialog>
      </el-form-item>
      <el-form-item label="联盟说明：" prop="unionIllustration">
        <el-input type="textarea" :rows="3" placeholder="请输入联盟说明" v-model="form.unionIllustration">
        </el-input>
      </el-form-item>
    </el-form>
    </div>
    <div class="footer_public">
      <el-button type="primary" @click="submitForm('form')">保存</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import $ from 'jquery';
export default {
  name: 'create-step-union',
  props: ['basicFormData'],
  data() {
    // 验证规则
    let blackChargePricePass = (rule, value, callback) => {
      if (this.form.blackIsCharge) {
        if (value !== 0 && !value) {
          callback(new Error('黑卡价格内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('黑卡价格内容必须为数字值，请重新输入'));
        } else if (value < 1) {
          callback(new Error('黑卡价格内容必须大于1，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let blackValidityDayPass = (rule, value, callback) => {
      if (this.form.blackIsCharge) {
        if (value !== 0 && !value) {
          callback(new Error('黑卡时效内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('黑卡时效内容必须为数字值，请重新输入'));
        } else if (value < 1) {
          callback(new Error('黑卡时效内容必须大于1，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let redChargePricePass = (rule, value, callback) => {
      if (this.form.redIsAvailable) {
        if (value !== 0 && !value) {
          callback(new Error('红卡价格内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('红卡价格内容必须为数字值，请重新输入'));
        } else if (value < 1) {
          callback(new Error('红卡价格内容必须大于1，请重新输入'));
        } else if (value < this.form.blackChargePrice) {
          callback(new Error('红卡价格需大于黑卡价格，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let redValidityDayPass = (rule, value, callback) => {
      if (this.form.redIsAvailable) {
        if (value !== 0 && !value) {
          callback(new Error('红卡时效内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('红卡时效内容必须为数字值，请重新输入'));
        } else if (value < 1) {
          callback(new Error('红卡时效内容必须大于1，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    return {
      childrenData: '',
      form: {
        unionName: '',
        joinType: 2,
        isIntegral: false,
        oldMemberCharge: false,
        blackIsCharge: false,
        blackChargePrice: '',
        blackValidityDay: '',
        blackIllustration: '',
        redIsAvailable: false,
        redChargePrice: '',
        redValidityDay: '',
        redIllustration: ''
      },
      checkList: ['enterpriseName', 'directorPhone'],
      rules: {
        unionName: [{ required: true, message: '联盟名称内容不能为空，请重新输入', trigger: 'blur' }],
        blackChargePrice: [{ validator: blackChargePricePass, trigger: 'blur' }],
        blackValidityDay: [{ validator: blackValidityDayPass, trigger: 'blur' }],
        redChargePrice: [{ validator: redChargePricePass, trigger: 'blur' }],
        redValidityDay: [{ validator: redValidityDayPass, trigger: 'blur' }],
        unionImg: [{ required: true, message: '联盟图标内容不能为空，请重新输入', trigger: 'change' }],
        unionIllustration: [{ required: true, message: '联盟说明内容不能为空，请重新输入', trigger: 'blur' }]
      },
      materialVisible: false,
      materialUrl: ''
    };
  },
  computed: {
    permitId() {
      return this.$store.state.permitId;
    }
  },
  mounted: function() {
    let _this = this;
    window.addEventListener('message', function(e) {
      // alert("这个是fu页面" + e.data);
      if (e.data && e.data != 'go_back()') {
        _this.form.unionImg = e.data.split(',')[1].replace(/\'|\)/g, '');
      }
      _this.materialVisible = false;
    });
  },
  methods: {
    // 调用素材库
    materiallayer() {
      this.materialVisible = true;
      this.materialUrl = 'http://nb.suc.deeptel.com.cn/common/material.do?retUrl=' + window.location.href;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = '/unionMainCreate/instance';
          // 处理要提交的数据
          let data = {};
          data.permitId = this.permitId;
          data.unionMainVO = {};
          data.unionMainVO.isIntegral = this.form.isIntegral - 0;
          data.unionMainVO.joinType = this.form.joinType;
          data.unionMainVO.unionIllustration = this.form.unionIllustration;
          data.unionMainVO.unionImg = this.form.unionImg;
          data.unionMainVO.unionName = this.form.unionName;
          data.unionMainVO.unionMainChargeVO = {};
          data.unionMainVO.unionMainChargeVO.blackChargePrice = this.form.blackChargePrice;
          data.unionMainVO.unionMainChargeVO.blackIllustration = this.form.blackIllustration;
          data.unionMainVO.unionMainChargeVO.blackIsAvailable = 1;
          data.unionMainVO.unionMainChargeVO.blackIsCharge = this.form.blackIsCharge - 0;
          data.unionMainVO.unionMainChargeVO.blackIsOldCharge = this.form.oldMemberCharge - 0;
          data.unionMainVO.unionMainChargeVO.blackValidityDay = this.form.blackValidityDay - 0;
          data.unionMainVO.unionMainChargeVO.redChargePrice = this.form.redChargePrice - 0;
          data.unionMainVO.unionMainChargeVO.redIllustration = this.form.redIllustration;
          data.unionMainVO.unionMainChargeVO.redIsAvailable = this.form.redIsAvailable - 0;
          data.unionMainVO.unionMainChargeVO.redIsCharge = this.form.redIsAvailable - 0;
          data.unionMainVO.unionMainChargeVO.redIsOldCharge = this.form.oldMemberCharge - 0;
          data.unionMainVO.unionMainChargeVO.redValidityDay = this.form.redValidityDay - 0;
          data.unionMainVO.unionMainDictList = [];
          this.checkList.forEach((v, i) => {
            data.unionMainVO.unionMainDictList.push({ itemKey: v });
          });
          data.unionMemberVO = {};
          data.unionMemberVO.addressCityCode = this.basicFormData.addressCityCode;
          data.unionMemberVO.addressDistrictCode = this.basicFormData.addressDistrictCode;
          data.unionMemberVO.addressLatitude = this.basicFormData.addressLatitude + '';
          data.unionMemberVO.addressLongitude = this.basicFormData.addressLongitude + '';
          data.unionMemberVO.addressProvinceCode = this.basicFormData.addressProvinceCode;
          data.unionMemberVO.directorEmail = this.basicFormData.directorEmail;
          data.unionMemberVO.directorName = this.basicFormData.directorName;
          data.unionMemberVO.directorPhone = this.basicFormData.directorPhone;
          data.unionMemberVO.enterpriseAddress = this.basicFormData.enterpriseAddress;
          data.unionMemberVO.enterpriseName = this.basicFormData.enterpriseName;
          data.unionMemberVO.integralExchangePercent = 0;
          data.unionMemberVO.notifyPhone = this.basicFormData.notifyPhone;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.childrenData = 2;
                this.$emit('activeChange', this.childrenData);
                eventBus.$emit('unionUpdata');
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
      this.childrenData = 0;
      this.$emit('activeChange', this.childrenData);
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
#un_set {
  form {
    margin-left: -70px;
  }
}

.unionImg {
  width: 80px;
  height: 80px;
}
/*一个图标*/
.tubiao {
  width: 18px;
  height: 18px;
  border: 1px solid #1c8de0;
  border-radius: 50%;
  display: block;
  text-align: center;
  line-height: 18px;
  color: #1d90e6;
  font-weight: bold;
  position: absolute;
  top: 9px;
  left: 55px;
  cursor: pointer;
}
</style>
