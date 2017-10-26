<template>
  <div id="myRecommend">
    <h4> 我要推荐 </h4>
    <div v-show="visible" class="btn1">
      <el-button type="primary" @click="show">我要推荐</el-button>
      <el-button type="warning" style="padding: 10px 15px 10px 32px;position: relative">
        <img src="../../assets/images/Videos.png" style="width: 17px;position: absolute;top:8px;left: 7px;">
        视频教程
      </el-button>
    </div>
    <div class="MyRecommend" v-show="visible1">
      <el-form :label-position="labelPosition" :model="ruleForm1" :rules="rules" ref="ruleForm1" label-width="100px">
        <el-form-item label="意向客户姓名：" prop="clientName">
          <el-col :span="5">
            <el-input v-model="ruleForm1.clientName" placeholder="请输入客户姓名"></el-input>
          </el-col>
        </el-form-item>
        <el-form-item label="意向客户电话：" prop="clientPhone">
          <el-col :span="5">
            <el-input v-model="ruleForm1.clientPhone" placeholder="请输入客户电话"></el-input>
          </el-col>
        </el-form-item>
        <div class="selectUnion">
          <el-form-item label="选择联盟：" prop="unionRadio">
            <el-radio-group v-model="ruleForm1.unionRadio">
              <el-radio-button v-for="item in options1" :key="item.value" :label="item.value">
                <div class="dddddd clearfix">
                  <img v-bind:src="item.unionMain.img" alt="" class="fl unionImg">
                  <div class="fl" style="margin-left: 20px">
                    <h6 style="margin-bottom: 17px">{{item.name}}</h6>
                  </div>
                  <i></i>
                </div>
              </el-radio-button>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="推荐商家：" prop="toMemberId">
          <el-select v-model="ruleForm1.toMemberId" placeholder="请选择推荐的商家">
            <el-option v-for="item in options2" :key="item.value" :label="item.label" :value="item.value" v-if="item.value != this.memberId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="业务备注：" prop="businessMsg">
          <el-input type="textarea" :rows="3" id="feedbackcontent" placeholder="请输入业务备注" v-model="ruleForm1.businessMsg" :maxlength="unionNoticeMaxlength" @focus="unionNoticeFocus" @blur="unionNoticeBlur" @change="unionNoticeKeydown($event)" @keydown="unionNoticeKeydown($event)" @keyup="unionNoticeKeydown($event)" @input="unionNoticeKeydown($event)" @onpropertychange="unionNoticeKeydown($event)"></el-input>
        </el-form-item>
        <el-form-item style="margin-left: 15px;">
          <el-button type="primary" @click="submitForm1('ruleForm1')">确定</el-button>
          <el-button @click="resetForm1('ruleForm1')">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <recommend-record></recommend-record>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import RecommendRecord from './RecommendRecord';
export default {
  name: 'recommend',
  components: {
    RecommendRecord
  },
  data() {
    // 验证规则
    let clientPhonePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('意向客户手机内容不能为空，请重新输入'));
      } else if (!value.match(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    return {
      visible: true,
      visible1: false,
      labelPosition: 'right',
      ruleForm1: {
        clientName: '',
        clientPhone: '',
        unionRadio: '',
        toMemberId: '',
        businessMsg: ''
      },
      rules: {
        clientName: [{ required: true, message: '意向客户内容不能为空，请重新输入', trigger: 'blur' }],
        unionRadio: [{ type: 'number', required: true, message: '联盟不能为空，请重新选择', trigger: 'change' }],
        toMemberId: [{ type: 'number', required: true, message: '推荐商家不能为空，请重新选择', trigger: 'change' }],
        businessMsg: [{ required: true, message: '业务备注内容不能为空，请重新输入', trigger: 'blur' }],
        clientPhone: [{ validator: clientPhonePass, trigger: 'blur' }]
      },
      options1: [],
      options2: [],
      unionNoticeMaxlength: 40,
      memberId: ''
    };
  },
  computed: {
    unionRadio() {
      return this.ruleForm1.unionRadio;
    },
    initUnionId() {
      return this.$store.state.unionId;
    }
  },
  watch: {
    initUnionId: function() {
      this.init();
    },
    // 获取商家列表
    unionRadio: function() {
      this.ruleForm1.toMemberId = '';
      this.options2 = [];
      // 通过对应的unionId获取对应的memberId
      $http
        .get(`/unionMember/listMap`)
        .then(res => {
          if (res.data.data) {
            res.data.data.forEach((v, i) => {
              if (v.unionMain.id === this.unionRadio) {
                this.memberId = v.unionMember.id;
                $http
                  .get(`/unionMember/listMap/memberId/${this.memberId}`)
                  .then(res => {
                    if (res.data.data) {
                      this.options2 = res.data.data;
                      res.data.data.forEach((v, i) => {
                        this.options2[i].value = v.memberId;
                        this.options2[i].label = v.enterpriseName;
                      });
                      // 不能推荐给自己
                      this.options2.forEach((v, i) => {
                        if (v.value === this.memberId) this.options2.splice(i, 1);
                      });
                    } else {
                      this.options2 = [];
                    }
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      if (this.initUnionId) {
        // 获取联盟列表
        $http
          .get(`/unionMember/listMap`)
          .then(res => {
            if (res.data.data && res.data.data.length > 0) {
              this.options1 = res.data.data;
              res.data.data.forEach((v, i) => {
                this.options1[i].value = v.unionMain.id;
                this.options1[i].label = v.unionMain.name;
              });
            } else {
              this.options1 = [];
            }
            if (this.options1.length === 1) {
              this.ruleForm1.unionRadio = this.options1[0].value;
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    },
    show() {
      this.visible = !this.visible;
      this.visible1 = !this.visible1;
    },
    submitForm1(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionOpportunity/memberId/${this.memberId}`;
          let data = {};
          data = this.ruleForm1;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.$message({ showClose: true, message: '推荐成功', type: 'success', duration: 5000 });
                eventBus.$emit('newRecommend');
                this.$refs[formName].resetFields();
                this.init();
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
    resetForm1(formName) {
      this.show();
      this.$refs[formName].resetFields();
      this.init();
    },
    // 判断业务备注字数
    unionNoticeFocus() {
      let valueLength = this.unionNoticeCheck()[0];
      let len = this.unionNoticeCheck()[1];
      if (valueLength > 20) {
        this.ruleForm1.businessMsg = this.ruleForm1.businessMsg.substring(0, len + 1);
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
        // this.ruleForm1.businessMsg = this.ruleForm1.businessMsg.substring(0, len + 1);
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
      let str = this.ruleForm1.businessMsg || '';
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
.container {
  margin: 40px 50px;
  .sucess_if {
    text-align: center;
    p {
      margin: 31px 0 49px 0;
      color: #666666;
    }
  }
}

.unionImg {
  width: 80px;
  height: 80px;
}

.btn1 {
  margin-bottom: 20px;
}
#feedbackcontent::-webkit-input-placeholder {
  /* WebKit browsers */
  color: #97a8be;
}
#feedbackcontent:-moz-placeholder {
  /* Mozilla Firefox 4 to 18 */
  color: #97a8be;
}
#feedbackcontent::-moz-placeholder {
  /* Mozilla Firefox 19+ */
  color: #97a8be;
}
#feedbackcontent::-ms-input-placeholder {
  /* Internet Explorer 10+ */
  color: #97a8be;
}
</style>
