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
        <el-form-item label="联盟申请/推荐收集信息：" class="lianmeng">
          <el-checkbox-group v-model="checkList">
            <el-checkbox label="enterpriseName" disabled>企业名称</el-checkbox>
            <el-checkbox label="directorName">负责人</el-checkbox>
            <el-checkbox label="directorPhone" disabled>联系电话</el-checkbox>
            <el-checkbox label="directorEmail">邮箱</el-checkbox>
            <el-checkbox label="applyReason">申请/推荐理由</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="联盟图标：" prop="unionImg">
          <img v-if="this.form.unionImg" v-bind:src="this.form.unionImg" class="unionImg" @click="materiallayer">
          <el-button @click="materiallayer" v-if="!this.form.unionImg">
            <i class="el-icon-plus"></i>
          </el-button>
          <span style="position: relative;top: 25px;left: 2px;color:#bbbbbb;" v-if="!this.form.unionImg">图片尺寸：100*100px</span>
          <el-dialog title="素材库" :visible.sync="materialVisible">
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
    return {
      childrenData: '',
      form: {
        unionName: '',
        joinType: 2,
        isIntegral: false
      },
      checkList: ['enterpriseName', 'directorPhone'],
      rules: {
        unionName: [{ required: true, message: '联盟名称内容不能为空，请重新输入', trigger: 'blur' }],
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
      this.materialUrl = this.$store.state.materialUrl + window.location.href;
      this.form.unionImg =
        'http://maint.deeptel.com.cn/upload//image/3/goodtom/3/20171019/5908D42D1AFEFCC221A5FFF09705BF46.jpg';
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = '/unionMainCreate';
          // 处理要提交的数据
          let data = {};
          data.permitId = this.permitId;
          data.itemList = [];
          this.checkList.forEach((v, i) => {
            data.itemList.push({ itemKey: v });
          });
          data.member = {};
          data.member.enterpriseName = this.basicFormData.enterpriseName;
          data.member.enterpriseAddress = this.basicFormData.enterpriseAddress;
          data.member.directorName = this.basicFormData.directorName;
          data.member.directorPhone = this.basicFormData.directorPhone;
          data.member.directorEmail = this.basicFormData.directorEmail;
          data.member.addressLongitude = this.basicFormData.addressLongitude + '';
          data.member.addressLatitude = this.basicFormData.addressLatitude + '';
          data.member.addressProvinceCode = this.basicFormData.addressProvinceCode;
          data.member.addressCityCode = this.basicFormData.addressCityCode;
          data.member.addressDistrictCode = this.basicFormData.addressDistrictCode;
          data.member.notifyPhone = this.basicFormData.notifyPhone;
          data.union = {};
          data.union.name = this.form.unionName;
          data.union.img = this.form.unionImg;
          data.union.joinType = this.form.joinType;
          data.union.illustration = this.form.unionIllustration;
          data.union.isIntegral = this.form.isIntegral - 0;
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
