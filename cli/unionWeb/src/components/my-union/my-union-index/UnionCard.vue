<template>
  <div id="myUnionCard1">
    <el-row class="user_search">
      <el-col style="width:120px;">
        <div class="grid-content bg-purple">
          <el-select v-model="value" clearable placeholder="请选择" class="fl">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col style="width:190px;">
        <div class="grid-content1 bg-purple">
          <el-input placeholder="请输入关键字" icon="search" @keyup.enter.native="search" v-model="input" :on-icon-click="search" class="input-search fl">
          </el-input>
        </div>
      </el-col>
      <el-col :xs="3" :sm="3" :md="3" :lg="3" style="width:190px;padding-left: 20px">
        <el-button type="primary" @click="output">导出</el-button>
      </el-col>
    </el-row>
    <!-- 联盟卡 表格 -->
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="fan.number" label="联盟卡号">
      </el-table-column>
      <el-table-column prop="fan.phone" label="手机号">
      </el-table-column>
      <el-table-column prop="integral" label="联盟积分">
      </el-table-column>
      <el-table-column prop="" label="操作" width="200">
        <template slot-scope="scope">
          <el-button @click="showDetail(scope)" size="small">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange" :current-page.sync="currentPage" :page-size="10" layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
    <!-- 弹出框 详情 -->
    <el-dialog title="联盟卡详情" :visible.sync="visible" size="tiny">
      <hr>
      <div if="detailData.discountCard">
        <div> {{ detailData.discountCard.name }} </div>
        <p> 办理时间：
          <span>{{ detailData.discountCard.createTime }}</span>
        </p>
        <p> 享受折扣：
          <span>{{ detailData.discount * 10 }}</span>
        </p>
      </div>
      <div v-for="item in detailData.activityCardList" :key="item.id">
        <div> {{ item.name }} </div>
        <p> 办理时间：
          <span>{{ item.createTime }} </span>
        </p>
        <p> 有效时间：
          <span>{{ item.validity }}</span>
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'union-card',
  data() {
    return {
      value: '',
      options: [
        {
          value: 'number',
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
      totalAll: 0,
      visible: false,
      detailData: {
        discountCard: {},
        discount: '',
        activityCardList: []
      }
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    if (this.unionId) {
      this.init();
    }
  },
  activated: function() {
    if (this.unionId) {
      this.init();
    }
  },
  watch: {
    unionId: function() {
      if (this.unionId) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      this.currentPage = 1;
      this.value = '';
      this.input = '';
      this.getTableData();
    },
    getTableData() {
      $http
        .get(`/unionCardFan/page?current=${this.currentPage}&unionId=${this.unionId}&` + this.value + '=' + this.input)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records || [];
            this.totalAll = res.data.data.total;
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
      this.currentPage = 1;
      this.getTableData();
    },
    // 分页查询联盟卡
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getTableData();
    },
    // 导出联盟卡
    output() {
      let url =
        this.$store.state.baseUrl + `/unionCardFan/export?unionId=${this.unionId}&` + this.value + '=' + this.input;
      window.open(url);
    },
    // 弹出框 详情
    showDetail(scope) {
      $http
        .get(`/unionCardFan/${scope.row.fan.id}/detail?unionId=${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.detailData = res.data.data;
            this.visible = true;
            this.detailData.discountCard.createTime = timeFilter(this.detailData.discountCard.createTime);
            this.detailData.activityCardList.forEach((v, i) => {
              v.createTime = timeFilter(v.createTime);
              v.validity = timeFilter(v.validity);
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>
<style scoped lang='less' rel="stylesheet/less">

</style>
