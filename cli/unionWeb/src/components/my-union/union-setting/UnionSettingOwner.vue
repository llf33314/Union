<template>
  <div id="un_set">
    <p class="union_set">联盟基本信息设置</p>
    <div class="second_">
      <el-form :model="form" :rules="rules" ref="form" label-width="200px" style="margin-left: -68px;">
        <el-form-item label="联盟名称：" prop="name">
          <el-input v-model="form.name" placeholder="请输入联盟名称"></el-input>
        </el-form-item>
        <el-form-item label="加入方式：">
          <el-radio-group v-model="form.joinType">
            <el-radio :label="1">联盟推荐</el-radio>
            <el-radio :label="2">联盟推荐与自由申请（仅针对多粉商家）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联盟积分：">
          <el-switch on-text="" off-text="" v-model="form.isIntegral" :disabled="isIntegral_"></el-switch>
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
            <el-checkbox label="reason">申请/推荐理由</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="联盟图标：">
          <img v-if="this.form.img" v-bind:src="this.form.img" class="unionImg" @click="materiallayer">
          <el-button @click="materiallayer" v-if="!this.form.img">
            <i class="el-icon-plus"></i>
          </el-button>
          <span style="position: relative;top: -1px;left: 6px;color:#bbbbbb;" v-if="!this.form.unionImg">图片尺寸：100*100px</span>
          <el-dialog :visible.sync="materialVisible" title="素材库">
            <hr>
            <iframe :src="materialUrl" width="95%" height="500" frameborder="0" scrolling="no"></iframe>
          </el-dialog>
        </el-form-item>
        <el-form-item label="联盟说明：" prop="illustration">
          <el-input type="textarea" :rows="3" placeholder="请输入联盟说明" v-model="form.illustration">
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
export default {
  name: 'union-setting-owner',
  data() {
    return {
      childrenData: '',
      form: {
        name: '',
        img: '',
        joinType: 2,
        isIntegral: true,
        illustration: ''
      },
      checkList: ['enterpriseName', 'directorPhone'],
      rules: {
        name: [{ required: true, message: '联盟名称内容不能为空，请重新输入', trigger: 'blur' }],
        img: [{ required: true, message: '联盟图标内容不能为空，请重新输入', trigger: 'change' }],
        illustration: [{ required: true, message: '联盟说明内容不能为空，请重新输入', trigger: 'blur' }]
      },
      materialVisible: false,
      materialUrl: '',
      isIntegral_: false // 判断联盟积分能否选择
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  watch: {},
  mounted: function() {
    let _this = this;
    window.addEventListener('message', function(e) {
      if (e.data && e.data != 'go_back()') {
        _this.form.unionImg = e.data.split(',')[1].replace(/\'|\)/g, '');
      }
      _this.materialVisible = false;
    });
    this.init();
    eventBus.$on('unionSettingTabChange', () => {
      this.init();
    });
  },
  methods: {
    init() {
      // 获取联盟基本信息
      $http
        .get(`/unionMain/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.form.name = res.data.data.union.name;
            this.form.joinType = res.data.data.union.joinType;
            this.form.isIntegral = Boolean(res.data.data.union.isIntegral);
            this.isIntegral_ = Boolean(res.data.data.union.isIntegral);
            this.form.illustration = res.data.data.union.illustration;
            this.form.img = res.data.data.union.img;
            if (res.data.data.itemList) {
              res.data.data.itemList.forEach((v, i) => {
                if (this.checkList.indexOf(v.itemKey) === -1) {
                  this.checkList.push(v.itemKey);
                }
              });
            }
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 调用素材库
    materiallayer() {
      this.materialVisible = true;
      this.materialUrl = this.$store.state.materialUrl + window.location.href;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionMain/${this.unionId}`;
          // 处理要提交的数据
          let data = {};
          data.union = {};
          data.union.isIntegral = this.form.isIntegral - 0;
          data.union.joinType = this.form.joinType - 0;
          data.union.illustration = this.form.illustration;
          data.union.img = this.form.img;
          data.union.name = this.form.name;
          data.itemList = [];
          this.checkList.forEach((v, i) => {
            data.itemList.push({ itemKey: v, unionId: this.unionId });
          });
          $http
            .put(url, data)
            .then(res => {
              if (res.data.success) {
                eventBus.$emit('unionUpdata');
                this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 5000 });
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
