<template>
  <div>
    <Breadcrumb :header-name="'去加入联盟'"></Breadcrumb>
    <div class="container">
      <div class="step-top">
        <el-steps :active="active" :center="true" :align-center="true">
          <el-step title="选择联盟"></el-step>
          <el-step title="填写申请信息"></el-step>
          <el-step title="等待审核通过"></el-step>
        </el-steps>
      </div>
      <div class="step2" v-if="active === 0">
        <el-radio-group v-model="unionRadio">
          <el-radio-button v-for="item in datas" :key="item.id" :label="item.id">
            <div class="dddddd clearfix">
              <img v-bind:src="item.img" alt="" class="fl unionImg">
              <div class="fl" style="margin-left: 20px">
                <h6 style="margin-bottom: 17px">{{item.name}}</h6>
                <span>{{item.illustration}}</span>
              </div>
              <i></i>
            </div>
          </el-radio-button>
        </el-radio-group>
        <el-row v-if="datas.length">
          <el-col :span="8">
            <el-button type="primary" style="margin-top: 12px;" @click="next">下一步</el-button>
            <el-button style="margin-top: 12px;" @click="back">返回</el-button>
          </el-col>
          <el-col :span="10" :offset="6">
            <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="datas.length>0">
            </el-pagination>
          </el-col>
        </el-row>
        <div id="noUnion" style="margin: 0;" v-if="!datas.length">
          <img src="../../assets/images/noCurrent.png" >
          <p>
            平台还没有联盟数据
          </p>
        </div>
      </div>
      <div v-if="active === 1" class="tabs">
        <p class="information">填写申请信息</p>
        <el-form :label-position="labelPosition" label-width="100px" :model="form" :rules="rules" ref="form">
          <el-form-item label="企业名称:" prop="enterpriseName">
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
          <el-form-item label="申请理由：" prop="reason">
            <el-input type="textarea" :rows="4" placeholder="请输入申请理由" v-model="form.reason"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit('form')">保存</el-button>
            <el-button @click="pre">返回</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="active === 2" class="sucess_if">
        <div>
          <img src="../../assets/images/success01.png" alt="">
        </div>
        <p>创建联盟成功</p>
        <el-col>
          <el-button type="primary" @click="back">我的联盟</el-button>
          <el-button @click="pre">返回</el-button>
        </el-col>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import Breadcrumb from '@/components/public-components/Breadcrumb'
import $http from '@/utils/http.js'
import NoUnion from '@/components/public-components/NoUnion'
export default {
  name: 'join-step',
  components: {
    Breadcrumb,
    NoUnion
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
    return {
      active: 0,
      currentPage: 1,
      labelPosition: 'right',
      form: {
        enterpriseName: '',
        directorName: '',
        directorPhone: '',
        directorEmail: '',
        reason: ''
      },
      rules: {
        enterpriseName: [
          { required: true, message: '申请企业内容不能为空，请重新输入', trigger: 'blur' }
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
        reason: [
          { required: true, message: '申请理由不能为空，请重新输入', trigger: 'blur' }
        ],
      },
      datas: [],
      unionRadio: '',
      totalAll: 0,
    };
  },
  mounted: function() {
    $http.get(`/unionMain/page/otherUnion/joinType/2`)
      .then(res => {
        if (res.data.data) {
          this.datas = res.data.data.records;
          this.totalAll = res.data.data.total;
        } else {
          this.datas = [];
          this.totalAll = 0;
        };
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      })
  },
  methods: {
    next() {
      if (this.unionRadio) {
        this.active++;
      } else {
        this.$message({
          showClose: true,
          message: '请选择要加入的联盟',
          type: 'warning',
          duration: 5000
        });
      }
    },
    pre() {
      this.active--;
    },
    back() {
      this.$router.push({ path: '/my-union' })
    },
    // 分页查询
    handleCurrentChange(val) {
      $http.get(`/unionMain/page?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.datas = res.data.data.records;
            this.totalAll = res.data.data.total;
          } else {
            this.datas = [];
            this.totalAll = 0;
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    onSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let url = `/unionMemberJoin/unionId/${this.unionRadio}`;
          let data = this.form;
          $http.post(url, data)
            .then(res => {
              if(res.data.success) {
              this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 5000 });
              }
            })
            .catch(err => {
              this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
            });
          this.active++;
        } else {
          return false;
        }
      });
    },
  }
}
</script>

<style lang='less' rel="stylesheet/less" scoped>
.container {
  margin: 40px 50px;
  .sucess_if {
    text-align: center;
    p {
      margin: 31px 0 49px 0;
      color: #666666
    }
  }
}

.unionImg {
  width: 80px;
  height: 80px;
}
</style>
