<template>
  <div id="myUnionCard1">
    <el-row class="user_search">
      <el-col :xs="2" :sm="2" :md="2" :lg="2" style="width:120px;">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" icon="search" @keyup.enter.native="show1($event)"
                    v-model="input" :on-icon-click="search" class="input-search fl">
          </el-input>
        </div>
      </el-col>
      <!-- 导出按钮 -->
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;padding-left: 20px">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="cardNo" label="联盟卡号">
      </el-table-column>
      <el-table-column prop="type" label="联盟卡类型">
      </el-table-column>
      <el-table-column prop="phone" label="手机号">
      </el-table-column>
      <el-table-column prop="integral" label="联盟积分">
      </el-table-column>
      <el-table-column prop="createtime" label="升级时间">
        <template scope="scope">
          <el-popover trigger="hover" placement="bottom">
            <p>升级时间：{{ scope.row.createtime }}</p>
            <div slot="reference" class="name-wrapper">
              <span>{{ scope.row.createtime }}</span>
            </div>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column prop="validity" label="有效期">
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'union-card',
  data() {
    return {
      value: '',
      options: [
        {
          value: 'cardNo',
          label: '联盟卡号'
        },
        {
          value: 'phone',
          label: '联系电话'
        }
      ],
      input: '',
      tableData: [],
      currentPage: 1,
      totalAll: 0
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    },
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    if (this.unionMemberId) {
      if (this.unionId) {
        this.init();
      }
    }
  },
  activated: function() {
    if (this.unionMemberId) {
      if (this.unionId) {
        this.init();
      }
    }
  },
  watch: {
    unionId: function() {
      if (this.unionMemberId) {
        if (this.unionId) {
          this.init();
        }
      }
    }
  },
  methods: {
    show1: function(ev) {
      if (ev.keyCode == 13) {
        this.search();
      }
    },
    init() {
      $http
        .get(`/unionCard/unionId/${this.unionId}?current=1`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.type) {
                case 1:
                  v.type = '黑卡';
                  break;
                case 2:
                  v.type = '红卡';
                  break;
              }
              if (v.type == '黑卡' && v.is_charge == 0) {
                v.validity = '无';
              }
            });
          } else {
            this.tableData = [];
            this.totalAll = 0;
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 带条件查询联盟卡
    search() {
      $http
        .get(`/unionCard/unionId/${this.unionId}?current=1&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.type) {
                case 1:
                  v.type = '黑卡';
                  break;
                case 2:
                  v.type = '红卡';
                  break;
              }
              if (v.type == '黑卡' && v.is_charge == 0) {
                v.validity = '无';
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 分页查询联盟卡
    handleCurrentChange(val) {
      $http
        .get(`/unionCard/unionId/${this.unionId}?current=${val}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.total;
            this.tableData.forEach((v, i) => {
              switch (v.type) {
                case 1:
                  v.type = '黑卡';
                  break;
                case 2:
                  v.type = '红卡';
                  break;
              }
              if (v.type == '黑卡' && v.is_charge == 0) {
                v.validity = '无';
              }
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 导出联盟卡
    output() {
      let url = this.$store.state.baseUrl + `/unionCard/export/${this.unionId}?` + this.value + '=' + this.input;
      window.open(url);
    }
  }
};
</script>
<style scoped lang='less' rel="stylesheet/less">

</style>
