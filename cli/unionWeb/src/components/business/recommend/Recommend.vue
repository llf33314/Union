<template>
  <div id="myRecommend">
    <h4> 我要推荐 </h4>
    <div v-show="visible" class="btn1">
      <el-button type="primary" @click="show">我要推荐</el-button>
      <el-button type="warning" style="padding: 10px 15px 10px 32px;position: relative">
        <img src="~assets/images/Videos.png" style="width: 17px;position: absolute;top:8px;left: 7px;"> 视频教程
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
          <el-form-item label="选择联盟：" prop="unionId">
            <el-radio-group v-model="ruleForm1.unionId">
              <el-radio-button v-for="item in options1" :key="item.value" :label="item.value">
                <div class="dddddd clearfix">
                  <img v-bind:src="item.img" alt="" class="fl unionImg">
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
          <el-col :span="4">
            <el-input type="textarea" :rows="3" id="feedbackcontent" placeholder="请输入业务备注" v-model="ruleForm1.businessMsg"></el-input>
          </el-col>
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
import { clientNamePass, cellPhonePass, businessMsgPass } from '@/utils/validator.js';
import RecommendRecord from './RecommendRecord';
export default {
  name: 'recommend',
  components: {
    RecommendRecord
  },
  data() {
    return {
      visible: true,
      visible1: false,
      labelPosition: 'right',
      ruleForm1: {
        clientName: '',
        clientPhone: '',
        unionId: '',
        toMemberId: '',
        businessMsg: ''
      },
      rules: {
        clientName: [{ validator: clientNamePass, trigger: 'blur' }],
        unionId: [{ type: 'number', required: true, message: '联盟不能为空，请重新选择', trigger: 'change' }],
        toMemberId: [{ type: 'number', required: true, message: '推荐商家不能为空，请重新选择', trigger: 'change' }],
        businessMsg: [{ validator: businessMsgPass, trigger: 'blur' }],
        clientPhone: [{ validator: cellPhonePass, trigger: 'blur' }]
      },
      options1: [],
      options2: [],
      unionNoticeMaxlength: 40,
      memberId: ''
    };
  },
  computed: {
    unionId() {
      return this.ruleForm1.unionId;
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
    unionId: function() {
      this.ruleForm1.toMemberId = '';
      this.options2 = [];
      if (this.unionId) {
        $http
          .get(`/unionMember/unionId/${this.unionId}/read/other`)
          .then(res => {
            if (res.data.data) {
              this.options2 = res.data.data || [];
              res.data.data.forEach((v, i) => {
                this.options2[i].value = v.id;
                this.options2[i].label = v.enterpriseName;
              });
            } else {
              this.options2 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          });
      }
    }
  },
  mounted: function() {
    this.init();
  },
  methods: {
    init() {
      if (this.initUnionId) {
        // 获取我的当前有效的联盟
        $http
          .get(`/unionMain/busUser/valid`)
          .then(res => {
            if (res.data.data) {
              this.options1 = res.data.data || [];
              this.options1.forEach((v, i) => {
                v.value = v.id;
                v.label = v.name;
              });
            } else {
              this.options1 = [];
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
          let url = `/unionOpportunity/unionId/${this.unionId}`;
          let data = {
            toMember: {},
            opportunity: {}
          };
          data.toMember.id = this.ruleForm1.toMemberId;
          data.opportunity.clientName = this.ruleForm1.clientName;
          data.opportunity.clientPhone = this.ruleForm1.clientPhone;
          data.opportunity.businessMsg = this.ruleForm1.businessMsg;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                eventBus.$emit('newBusinessRecommend');
                this.$refs[formName].resetFields();
                this.init();
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
    resetForm1(formName) {
      this.show();
      this.$refs[formName].resetFields();
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
