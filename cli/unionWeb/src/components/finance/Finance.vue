<template>
  <div class="container" id="finance">
    <div id="union_people">
      <ul class="clearfix">
        <li>
          <p>可提现金额</p>
          <div class="special">
            <span> {{ withdrawalSum }} </span>元
            <span class="btn1">
              <el-button type="primary" @click="dialogVisible1 = true">提现</el-button>
            </span>
            <div class="model_1">
              <!-- 弹出框 提现 -->
              <el-dialog title="提现" :visible.sync="dialogVisible1" size="tiny">
                <hr>
                <div class="model_">
                  <p><img v-bind:src="imgUrl"></p>
                  <p>扫二维码可进入商家联盟佣金平台</p>
                </div>
              </el-dialog>
            </div>
          </div>
        </li>
      </ul>
    </div>
    <p class="union_set">佣金平台管理者设置</p>
    <div class="footer_">
      <el-button type="primary" @click="dialogVisible2 = true">新增</el-button>
      <!-- 弹出框 新增 -->
      <el-dialog title="新增管理者" :visible.sync="dialogVisible2" size="tiny" @close="resetForm('ruleForm')">
        <hr>
        <div class="Popup_">
          <el-form :label-position="labelPosition" label-width="100px" :model="ruleForm" :rules="rules" ref="ruleForm">
            <el-form-item label="姓名：" prop="name">
              <el-col :span="8">
                <el-input v-model="ruleForm.name" placeholder="请输入管理者姓名"></el-input>
              </el-col>
            </el-form-item>
            <el-form-item label="手机号码：" prop="phone">
              <el-col :span="8">
                <el-input v-model="ruleForm.phone" placeholder="请输入管理者手机号码"></el-input>
              </el-col>
            </el-form-item>
            <el-form-item label="验证码：" prop="code">
              <el-col :span="8">
                <el-input v-model="ruleForm.code" placeholder="请输入验证码"></el-input>
              </el-col>
              <el-button type="primary" style="margin-left: 20px" @click="getVerificationCode" :disabled="form1.getVerificationCode || !ruleForm.phone">获取验证码{{form1.countDownTime}}</el-button>
            </el-form-item>
            <el-form-item>
              该手机号是联盟佣金平台的验证登录号码，请管理人员慎重设置号码
            </el-form-item>
          </el-form>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
          <el-button @click="resetForm('ruleForm')">取 消</el-button>
        </span>
      </el-dialog>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="name" label="姓名">
        </el-table-column>
        <el-table-column prop="phone" label="手机号">
        </el-table-column>
        <el-table-column prop="action" label="操作" width="150">
          <template scope="scope">
            <el-button size="small" @click="del(scope)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 弹出框 删除确认 -->
      <div class="model_2">
        <el-dialog title="删除" :visible.sync="visible1" size="tiny">
          <hr>
          <div>
            <img src="../../assets/images/delect01.png"  class="fl">
            <span>是否确认删除“ {{ name }} ”</span>
          </div>
          <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="confirm1">确定</el-button>
            <el-button @click="visible1=false">取消</el-button>
          </span>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'finance',
  data() {
    let phonePass = (rule, value, callback) => {
      if (!value) {
        callback(new Error('手机号码内容不能为空，请重新输入'));
      } else if (!value.match(/^1[3|4|5|6|7|8][0-9][0-9]{8}$/)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    return {
      dialogVisible1: false,
      dialogVisible2: false,
      labelPosition: 'right',
      withdrawalSum: 0,
      imgUrl: '',
      ruleForm: {
        name: '',
        code: '',
        phone: '',
      },
      rules: {
        name: [
          { required: true, message: '姓名不能为空，请重新输入', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '验证码不能为空，请重新输入', trigger: 'blur' }
        ],
        phone: [
          { validator: phonePass, trigger: 'blur' }
        ],
      },
      tableData: [],
      form1: {
        getVerificationCode: false,
        countDownTime: '',
      },
      visible1: false,
      name: '',
      id: '',
    }
  },
  created: function() {
    // 首页查询我的联盟信息
    $http.get(`/union/index`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnionId) {
            this.$router.push({ path: '/no-union' });
          } else {
            //获取佣金平台手机端的二维码
            $http.get(`/unionBrokerageIncome/indexQRUrl`)
              .then(res => {
                if (res.data.data) {
                  this.imgUrl = res.data.data;
                } else {
                  this.imgUrl = '';
                };
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
            // 可提现金额
            $http.get(`/unionBrokerageIncome/withdrawalSum`)
              .then(res => {
                if (res.data.data) {
                  this.withdrawalSum = res.data.data;
                } else {
                  this.withdrawalSum = 0;
                };
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
            // 佣金平台管理者列表
            $http.get(`/unionVerifier`)
              .then(res => {
                if (res.data.data) {
                  this.tableData = res.data.data.records;
                } else {
                  this.tableData = [];
                };
              })
              .catch(err => {
                this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
              });
          };
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 获取验证码
    getVerificationCode() {
      $http.get(`/unionVerifier/phone/${this.ruleForm.phone}?name=${this.ruleForm.name}`)
        .then(res => {
          if (res.data.success) {
            this.form1.getVerificationCode = true;
            this.form1.countDownTime = 60;
            let timer1 = setInterval(() => {
              this.form1.countDownTime--;
              if (this.form1.countDownTime === 0) {
                clearInterval(timer1);
                this.form1.getVerificationCode = false;
                this.form1.countDownTime = '';
              }
            }, 1000)
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 删除
    del(scope) {
      this.visible1 = true;
      this.id = scope.row.id;
      this.name = scope.row.name;
    },
    // 删除确认
    confirm1() {
      this.visible1 = false;
      $http.del(`/unionVerifier/${this.id}`)
        .then(res => {
            if(res.data.success){
              $http.get(`/unionVerifier`)
                .then(res => {
                  if (res.data.data) {
                    this.tableData = res.data.data.records;
                  } else {
                    this.tableData = [];
                  };
                })
                .catch(err => {
                  this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                });
            }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 提交新增
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.dialogVisible2 = false;
          let url = `/unionVerifier`;
          let data = this.ruleForm;
          $http.post(url, data)
            .then(res => {
              if(res.data.success){
                $http.get(`/unionVerifier`)
                  .then(res => {
                    if (res.data.data) {
                      this.tableData = res.data.data.records;
                    } else {
                      this.tableData = [];
                    };
                  })
                  .catch(err => {
                    this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
                  });
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
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.dialogVisible2 = false;
    }
  }
}

</script>
<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 120px 20px 60px;
  #union_people {
    padding: 13px 0 17px;
    background: #F8F8F8;
    >ul>li {
      float: left;
      width: 22%;
      border-left: 1px solid #EEEEEE;
      padding-left: 22px;
      color: #999999;
      p {
        margin-bottom: 19px;
      }
      span {
        color: #FF6600;
        font-size: 25px;
        font-weight: bold;
        margin-right: 10px
      }
      .special {
        display: block;
        .btn1 {
          button {
            float: right;
            margin-right: 20px;
          }
        }
      }
    }
  }
  .footer_ {
    >button {
      margin-bottom: 15px;
    }
  }
}
</style>
