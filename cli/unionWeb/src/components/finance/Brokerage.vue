<template>
  <div>
    <div id="union_people">
      <ul class="clearfix">
        <li>
          <p>可提现金额</p>
          <div class="special">
            <span> {{ withdrawalSum }} </span>元
            <span class="btn1">
              <el-button type="primary" @click="dialogVisible1=true">提现</el-button>
            </span>
          </div>
        </li>
      </ul>
    </div>
    <p class="union_set">佣金平台管理者设置</p>
    <div class="footer_">
        <el-button type="primary" @click="addAdmin">新增</el-button>
        <!-- 管理者列表 -->
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="employeeName" label="姓名">
            <template slot-scope="scope">
              <img v-if="!scope.row.id" src="~assets/images/finance1.png" style="width: 18px;position: absolute;left: 8px;" alt="">
              <span> {{ scope.row.employeeName }} </span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号">
          </el-table-column>
          <el-table-column prop="shopName" label="门店">
          </el-table-column>
          <el-table-column prop="action" label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="small" @click="del(scope)" v-if="scope.row.id">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
        </el-pagination>
        <!-- 弹出框 提现二维码 -->
        <div class="code_">
          <el-dialog title="提现" :visible.sync="dialogVisible1" size="tiny">
            <hr>
            <div class="model_">
              <p><img v-bind:src="$store.state.baseUrl + '/unionVerifier/h5Code'"></p>
              <p>扫二维码可进入商家联盟佣金平台</p>
            </div>
          </el-dialog>
        </div>
        <!-- 弹出框 新增 -->
        <el-dialog title="新增管理者" :visible.sync="dialogVisible2" size="tiny" @close="resetForm('ruleForm')">
          <hr>
          <div class="Popup_">
            <el-form :label-position="labelPosition" label-width="100px" :model="ruleForm" :rules="rules" ref="ruleForm">
              <el-form-item label="门店：" prop="shop">
                <el-select v-model="ruleForm.shop" clearable placeholder="请选择门店" style="width:180px;" @change="shopChange">
                  <el-option v-for="item in options1" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="姓名：" prop="name">
                <el-select v-model="ruleForm.name" clearable placeholder="请选择管理者姓名" style="width:180px" @change="employeeChange">
                  <el-option v-for="item in options2" :key="item.id" :label="item.name" :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="手机号码：" prop="phone">
                <el-input style="width: 180px;" v-model="ruleForm.phone" placeholder="请输入管理者手机号码" disabled></el-input>
              </el-form-item>
              <el-form-item label="验证码：" prop="code">
                <el-input style="width: 180px;" v-model="ruleForm.code" placeholder="请输入验证码"></el-input>
                <el-button type="primary" style="margin-left: 20px" @click="getVerificationCode" :disabled="form1.getVerificationCode || !ruleForm.phone">{{ form1.countDownTime>0?form1.countDownTime+'s':'获取验证码' }}</el-button>
              </el-form-item>
              <el-form-item>
                绑定后，该手机号将拥有佣金平台的登录及提现权限，请谨慎设置
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
                <el-button @click="resetForm('ruleForm')">取 消</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-dialog>
        <!-- 弹出框 删除确认 -->
        <div class="model_2">
          <el-dialog title="删除" :visible.sync="visible1" size="tiny">
            <hr>
            <div>
              <img src="../../assets/images/delect01.png" class="fl">
              <span>是否确认删除“ {{ employeeName }} ”</span>
              <p>点击确定后，该财务管理者不可登录佣金平台</p>
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
import $http from '@/utils/http.js';
import { cellPhonePass } from '@/utils/validator.js';
export default {
  name: 'brokerage',
  data() {
    return {
      dialogVisible1: false,
      dialogVisible2: false,
      labelPosition: 'right',
      withdrawalSum: 0,
      tableData: [],
      currentPage: 1,
      totalAll: 0,
      options1: [],
      options2: [],
      ruleForm: {
        shop: '',
        name: '',
        code: '',
        phone: ''
      },
      rules: {
        shop: [{ type: 'number', required: true, message: '门店不能为空，请重新输入', trigger: 'blur' }],
        name: [{ type: 'number', required: true, message: '姓名不能为空，请重新输入', trigger: 'blur' }],
        code: [{ required: true, message: '验证码不能为空，请重新输入', trigger: 'blur' }],
        phone: [{ validator: cellPhonePass, trigger: 'change' }]
      },
      form1: {
        getVerificationCode: false,
        countDownTime: ''
      },
      visible1: false,
      employeeName: '',
      id: '',
      timeOut: ''
    };
  },
  mounted: function() {
    // 首页查询我的联盟信息
    $http
      .get(`/unionIndex`)
      .then(res => {
        if (res.data.data) {
          // 判断是否创建或加入联盟
          if (!res.data.data.currentUnion) {
            this.$router.push({ path: '/no-union' });
          } else {
            this.init1();
            this.init2();
          }
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
      });
  },
  methods: {
    // 可提现金额
    init1() {
      $http
        .get(`/unionBrokerageWithdrawal/available`)
        .then(res => {
          if (res.data.data) {
            this.withdrawalSum = res.data.data;
          } else {
            this.withdrawalSum = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 佣金平台管理者
    init2() {
      $http
        .get(`/unionVerifier/page?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
          this.$emit('loadingFinish');
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 分页获取佣金平台管理者
    handleCurrentChange() {
      this.currentPage = val;
      $http
        .get(`/unionVerifier/page?current=${this.currentPage}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
          } else {
            this.tableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 新增管理者
    addAdmin() {
      this.dialogVisible2 = true;
      // 获取门店列表
      $http
        .get(`/api/shop/list`)
        .then(res => {
          if (res.data.data) {
            this.options1 = res.data.data || [];
          } else {
            this.options1 = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 选择门店获取店员列表
    shopChange() {
      if (this.ruleForm.shop) {
        $http
          .get(`/api/staff/list/${this.ruleForm.shop}`)
          .then(res => {
            if (res.data.data) {
              this.options2 = res.data.data || [];
            } else {
              this.options2 = [];
            }
          })
          .catch(err => {
            this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
          });
      }
    },
    // 选择店员获取对应电话
    employeeChange() {
      if (this.ruleForm.name) {
        let item = this.options2.find(item => {
          return item.id === this.ruleForm.name;
        });
        this.ruleForm.phone = item.phone;
      } else {
        this.ruleForm.phone = '';
      }
    },
    // 获取验证码
    getVerificationCode() {
      $http
        .get(`/api/sms/5?phone=${this.ruleForm.phone}`)
        .then(res => {
          if (res.data.success) {
            this.form1.getVerificationCode = true;
            this.form1.countDownTime = 60;
            this.timeOut = setInterval(() => {
              if (this.form1.countDownTime === 0) {
                this.form1.getVerificationCode = false;
                this.form1.countDownTime = '';
                clearInterval(this.timeOut);
                return;
              }
              this.form1.countDownTime--;
            }, 1000);
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    },
    // 提交新增
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionVerifier?code=${this.ruleForm.code}`;
          let data = {};
          data.shopId = this.ruleForm.shop;
          data.employeeId = this.ruleForm.name;
          data.phone = this.ruleForm.phone;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                this.dialogVisible2 = false;
                this.$message({ showClose: true, message: '新增成功', type: 'success', duration: 3000 });
                clearInterval(this.timeOut);
                this.form1.getVerificationCode = false;
                this.form1.countDownTime = '';
                this.init2();
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
    // 取消提交清空表格
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.dialogVisible2 = false;
      clearInterval(this.timeOut);
      this.form1.getVerificationCode = false;
      this.form1.countDownTime = '';
    },
    // 删除
    del(scope) {
      this.visible1 = true;
      this.id = scope.row.id;
      this.employeeName = scope.row.employeeName;
    },
    // 删除确认
    confirm1() {
      this.visible1 = false;
      $http
        .del(`/unionVerifier/${this.id}`)
        .then(res => {
          if (res.data.success) {
            this.$message({ showClose: true, message: '删除成功', type: 'success', duration: 3000 });
            this.init2();
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: '网络错误', type: 'error', duration: 3000 });
        });
    }
  }
};
</script>

<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px;
  #union_people {
    padding: 13px 0 17px;
    background: #f8f8f8;
    > ul > li {
      float: left;
      width: 22%;
      border-left: 1px solid #eeeeee;
      padding-left: 22px;
      color: #999999;
      p {
        margin-bottom: 19px;
      }
      span {
        color: #ff6600;
        font-size: 25px;
        font-weight: bold;
        margin-right: 10px;
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
    > button {
      margin-bottom: 15px;
    }
  }
}
</style>
