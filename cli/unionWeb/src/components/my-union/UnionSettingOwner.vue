<template>
  <div id="un_set">
    <p class="union_set">联盟基本信息设置</p>
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
      </el-form-item>
      <el-form-item label="黑卡收费：">
        <el-switch on-text="" off-text="" v-model="form.blackIsCharge"></el-switch>
      </el-form-item>
      <el-form-item label="老会员升级收费：" v-if="form.blackIsCharge">
        <el-switch on-text="" off-text="" v-model="form.oldMemberCharge"></el-switch>
      </el-form-item>
      <div class="Price_Card">
        <el-form-item label="黑卡价格：" prop="blackCardPrice" v-if="form.blackIsCharge">
          <el-input v-model="form.blackChargePrice">
            <template slot="prepend">￥</template>
          </el-input>
        </el-form-item>
      </div>
      <el-form-item label="黑卡时效：" prop="blackValidityDay" v-if="form.blackIsCharge">
        <div class="special">
          <el-input v-model="form.blackValidityDay" placeholder="请输入有效期"></el-input>
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
          <el-input v-model="form.redValidityDay" placeholder="请输入有效期">
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
          <el-checkbox label="reson">申请/推荐理由</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="联盟图标：">
        <img v-if="this.form.unionImg" v-bind:src="this.form.unionImg" class="unionImg" @click="materiallayer">
        <!-- todo button 改变样式 -->
        <el-button @click="materiallayer" v-if="!this.form.unionImg">
          <i class="el-icon-plus"></i>
        </el-button>
        <el-dialog :visible.sync="materialVisible">
          <iframe :src="materialUrl" width="400" height="300"></iframe>
        </el-dialog>
      </el-form-item>
      <el-form-item label="联盟说明：" prop="unionIllustration">
        <el-input type="textarea" :rows="3" placeholder="请输入联盟说明" v-model="form.unionIllustration">
        </el-input>
      </el-form-item>
    </el-form>
    <div class="footer_public">
      <el-button type="primary" @click="submitForm('form')">保存</el-button>
      <el-button @click="back">返回</el-button>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'union-setting-owner',
  data() {
    // 验证规则
    let blackChargePricePass = (rule, value, callback) => {
      if (this.form.blackIsCharge) {
        if (!value) {
          callback(new Error('黑卡价格内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('黑卡价格内容必须为数字值，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let blackValidityDayPass = (rule, value, callback) => {
      if (this.form.blackIsCharge) {
        if (!value) {
          callback(new Error('黑卡时效内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('黑卡时效内容必须为数字值，请重新输入'));
        } else {
          callback();
        }
      } else {
        callback();
      }
    };
    let redChargePricePass = (rule, value, callback) => {
      if (this.form.redIsAvailable) {
        if (!value) {
          callback(new Error('红卡价格内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('红卡价格内容必须为数字值，请重新输入'));
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
        if (!value) {
          callback(new Error('红卡时效内容不能为空，请重新输入'));
        } else if (isNaN(value)) {
          callback(new Error('红卡价格内容必须为数字值，请重新输入'));
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
        isIntegral: true,
        oldMemberCharge: false,
        blackIsCharge: true,
        blackChargePrice: '',
        blackValidityDay: '',
        blackIllustration: '',
        redIsAvailable: true,
        redChargePrice: '',
        redValidityDay: '',
        redIllustration: '',
        unionIllustration: '',
        blackIsAvailable: '',
      },
      checkList: ['enterpriseName', 'directorPhone'],
      rules: {
        unionName: [
          { required: true, message: '联盟名称内容不能为空，请重新输入', trigger: 'blur' }
        ],
        blackChargePrice: [
          { validator: blackChargePricePass, trigger: 'blur' }
        ],
        blackValidityDay: [
          { validator: blackValidityDayPass, trigger: 'blur' }
        ],
        redChargePrice: [
          { validator: redChargePricePass, trigger: 'blur' }
        ],
        redValidityDay: [
          { validator: redValidityDayPass, trigger: 'blur' }
        ],
        unionIllustration: [
          { required: true, message: '联盟说明内容不能为空，请重新输入', trigger: 'blur' }
        ]
      },
      materialVisible: false,
      materialUrl: '',
    }
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
    let _this = this;
    window.addEventListener("message", function(e) {
      // alert("这个是fu页面" + e.data);
      if (e.data && e.data != 'go_back()') {
        _this.form.unionImg = e.data.split(',')[1].replace(/\'|\)/g, '');
      }
      _this.materialVisible = false;
    });
    // 获取联盟基本信息
    $http.get(`/unionMain/${this.unionId}`)
      .then(res => {
        if (res.data.data) {
          this.form.unionName = res.data.data.name;
          this.form.joinType = res.data.data.joinType;
          this.form.isIntegral = Boolean(res.data.data.isIntegral);
          this.form.unionIllustration = res.data.data.illustration;
          this.form.unionImg = res.data.data.img;
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 黑卡信息
    $http.get(`/unionMainCharge/unionId/${this.unionId}/type/1`)
      .then(res => {
        if (res.data.data) {
          this.form.blackChargePrice = res.data.data.chargePrice;
          this.form.blackIllustration = res.data.data.illustration;
          this.form.blackIsAvailable = res.data.data.isAvailable;
          this.form.blackIsCharge = Boolean(res.data.data.isCharge);
          this.form.blackValidityDay = res.data.data.validityDay;
          this.form.oldMemberCharge = Boolean(res.data.data.isOldCharge);
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 红卡信息
    $http.get(`/unionMainCharge/unionId/${this.unionId}/type/2`)
      .then(res => {
        if (res.data.data) {
          this.form.redChargePrice = res.data.data.chargePrice;
          this.form.redIllustration = res.data.data.illustration;
          this.form.redIsAvailable = Boolean(res.data.data.isAvailable);
          this.form.redIsCharge = res.data.data.isCharge;
          this.form.redValidityDay = res.data.data.validityDay;
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
    // 收集信息
    $http.get(`/unionMainDict/${this.unionId}`)
      .then(res => {
        if (res.data.data) {
          res.data.data.forEach((v, i) => {
            let key = v.itemKey;
            if (!this.checkList.key) {
              this.checkList.push(v.itemKey);
            }
          })
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 调用素材库
    materiallayer() {
      this.materialVisible = true;
      this.materialUrl = 'https://suc.deeptel.com.cn/common/material.do?retUrl=' + window.location.href;
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let url = `/unionMain/memberId/${this.unionMemberId}`;
          // 处理要提交的数据
          let data = {};
          data.isIntegral = this.form.isIntegral - 0;
          data.joinType = this.form.joinType;
          data.unionIllustration = this.form.unionIllustration;
          data.unionImg = this.form.unionImg;
          data.unionName = this.form.unionName;
          data.unionMainChargeVO = {};
          data.unionMainChargeVO.blackChargePrice = this.form.blackChargePrice;
          data.unionMainChargeVO.blackIllustration = this.form.blackIllustration;
          data.unionMainChargeVO.blackIsAvailable = this.form.blackIsAvailable;
          data.unionMainChargeVO.blackIsCharge = this.form.blackIsCharge - 0;
          data.unionMainChargeVO.blackIsOldCharge = this.form.oldMemberCharge - 0;
          data.unionMainChargeVO.blackValidityDay = this.form.blackValidityDay;

          data.unionMainChargeVO.redChargePrice = this.form.redChargePrice;
          data.unionMainChargeVO.redIllustration = this.form.redIllustration;
          data.unionMainChargeVO.redIsAvailable = this.form.redIsAvailable - 0;
          data.unionMainChargeVO.redIsCharge = this.form.redIsCharge - 0;
          data.unionMainChargeVO.redIsOldCharge = this.form.oldMemberCharge - 0;
          data.unionMainChargeVO.redValidityDay = this.form.redValidityDay;
          data.unionMainDictList = [];
          this.checkList.forEach((v, i) => {
            data.unionMainDictList.push({ 'itemKey': v });
          });
          $http.put(url, data)
            .then(res => {

            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            })
        } else {
          return false;
        }
      });
    },
    back() {
      this.$router.push({ path: '/my-union/index' });
    },
  }
}
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
</style>
