<template>
  <div>
    <el-button type="primary" @click="visible=true" v-if="isUnionOwner">新增活动卡</el-button>
    <!-- 弹出框 新建活动卡 -->
    <div class="NewActivityCard">
      <el-dialog title="新建活动卡" :visible.sync="visible" @close="resetData">
        <hr>
        <el-form ref="form" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="活动卡名称：" prop="name">
            <el-input v-model="form.name" placeholder="请输入活动卡名称"></el-input>
          </el-form-item>
          <el-form-item label="价格：" prop="price">
            <el-input v-model="form.price" placeholder="请输入活动卡价格">
              <template slot="prepend">￥</template>
            </el-input>
          </el-form-item>
          <el-form-item label="颜色：" prop="color" style="height: 80px;">
            <!-- todo 需要更改 -->
            <union-color-picker @colorSelect="colorSelect"></union-color-picker>
          </el-form-item>
          <el-form-item label="发行量：" prop="amount">
            <el-input v-model="form.amount" placeholder="请输入活动卡发行量" @keyup.native="check()"></el-input>
            <span>发行活动卡的最大数量</span>
          </el-form-item>
          <el-form-item label="有效天数：" prop="validityDay">
            <el-input v-model="form.validityDay" placeholder="请输入活动卡有效天数"></el-input>
            <span>粉丝办理活动卡后，可使用的有效天数</span>
          </el-form-item>
          <el-form-item label="报名时间：" prop="applyTime">
            <el-date-picker v-model="form.applyTime" type="datetimerange" placeholder="请选择项目报名时间">
            </el-date-picker>
            <span>项目报名时间段中，盟员可添加各自的项目至活动卡</span>
          </el-form-item>
          <el-form-item label="售卖时间：" prop="sellTime">
            <el-date-picker v-model="form.sellTime" type="datetimerange" placeholder="请选择活动卡售卖时间">
            </el-date-picker>
            <span>活动卡售卖时间段，粉丝可购买活动卡享受联盟内项目</span>
          </el-form-item>
          <el-form-item label="活动卡说明：" prop="illustration">
            <el-input type="textarea" v-model="form.illustration"></el-input>
          </el-form-item>
          <el-form-item label="项目审核：">
            <el-switch v-model="form.isProjectCheck" on-text="" off-text="">
            </el-switch>
            <span>审核开启后，盟员提交的项目需要审核</span>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="activityCardConfirm('form')">确定</el-button>
          <el-button @click="visible=false">取消</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { numberCheck } from '@/utils/filter.js';
import { activityCardNamePass, activityCardIllustrationPass } from '@/utils/validator.js';
import UnionColorPicker from '@/components/public-components/UnionColorPicker';
export default {
  name: 'add-activity-card',
  components: {
    UnionColorPicker
  },
  data() {
    // 验证规则
    let applyTimePass = (rule, value, callback) => {
      if (!value[0]) {
        return callback(new Error('报名时间不能为空'));
      } else if (value[0].getTime() < new Date()) {
        callback(new Error('报名开始时间必须不早于当前时间'));
      } else {
        callback();
      }
    };
    let sellTimePass = (rule, value, callback) => {
      if (!value[0]) {
        return callback(new Error('售卡时间不能为空'));
      } else if (value[0].getTime() < this.form.applyTime[1].getTime()) {
        callback(new Error('售卡开始时间必须不早于报名结束时间'));
      } else {
        callback();
      }
    };
    return {
      visible: false,
      form: {
        name: '',
        price: '',
        color: '69BFD0',
        amount: '',
        validityDay: '',
        applyTime: ['', ''],
        sellTime: ['', ''],
        illustration: '',
        isProjectCheck: ''
      },
      rules: {
        name: [{ validator: activityCardNamePass, trigger: 'blur' }],
        price: [{ required: true, message: '请输入活动卡价格', trigger: 'blur' }],
        color: [{ required: true, message: '请选择活动卡颜色', trigger: 'change' }],
        amount: [{ required: true, message: '请输入活动卡最大发行量', trigger: 'blur' }],
        validityDay: [{ required: true, message: '请输入活动卡有效天数', trigger: 'blur' }],
        applyTime: [{ validator: applyTimePass, trigger: 'change' }],
        sellTime: [{ validator: sellTimePass, trigger: 'change' }],
        illustration: [{ validator: activityCardIllustrationPass, trigger: 'blur' }]
      }
    };
  },
  computed: {
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    },
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {},
  methods: {
    // 选择颜色
    colorSelect(v) {
      this.form.color = v;
    },
    // 校验折扣输入为数字类型
    check() {
      this.form.amount = numberCheck(this.form.amount);
    },
    // 确定新增活动卡
    activityCardConfirm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          let url = `/unionCardActivity/unionId/${this.unionId}`;
          let data = {};
          data.name = this.form.name;
          data.price = this.form.price - 0;
          data.color = this.form.color;
          data.amount = this.form.amount - 0;
          data.validityDay = this.form.validityDay - 0;
          data.applyBeginTime = this.form.applyTime[0].getTime();
          data.applyEndTime = this.form.applyTime[1].getTime();
          data.sellBeginTime = this.form.sellTime[0].getTime();
          data.sellEndTime = this.form.sellTime[1].getTime();
          data.illustration = this.form.illustration;
          data.isProjectCheck = this.form.isProjectCheck - 0;
          $http
            .post(url, data)
            .then(res => {
              if (res.data.success) {
                eventBus.$emit('newActivityCard');
                this.$message({ showClose: true, message: '新建活动卡成功', type: 'success', duration: 5000 });
                this.visible = false;
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
    // 关闭弹窗清空填写信息
    resetData() {
      this.form = {
        name: '',
        price: '',
        color: '',
        amount: '',
        validityDay: '',
        applyTime: ['', ''],
        sellTime: ['', ''],
        illustration: '',
        isProjectCheck: ''
      };
      this.$store.commit('myColorChange', this.form.color);
    }
  }
};
</script>
