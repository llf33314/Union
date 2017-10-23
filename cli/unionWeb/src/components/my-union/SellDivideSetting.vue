<template>
  <!-- 售卡佣金比例设置 -->
  <div id="setScale">
    <el-button type="primary" @click="dialogShow()">比例设置</el-button>
    <!-- 弹出框 比例设置 -->
    <el-dialog title="比例设置" :visible.sync="dialogVisible" size="small" @close="resetData">
      <hr>
      <div class="model_setting">
        <div>
          <span>盟主比例:</span>
          <el-input v-model="input" placeholder="请输入比例" @change="onChange"   style="width:26%"></el-input>
          <span>&nbsp;%&nbsp;</span>
          <el-button type="primary" @click="onAverage()">平均分配</el-button>
        </div>
        <p>售卡佣金分成总比例之和不得超过100%，当前总比例为{{ sum }}%，剩余{{100 - sum}}%可分配</p>
        <el-table :data="tableData3" style="width: 100%" id="table3">
          <el-table-column prop="enterpriseName" label="企业名称">
          </el-table-column>
          <el-table-column prop="cardDividePercent" label="分成比例(%)">
            <template scope="scope">
              <el-input placeholder="请输入比例" @change="onChange(scope)"></el-input>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onSave">保存</el-button>
        <el-button @click="dialogVisible=false">取 消</el-button>
      </span>
    </el-dialog>
    <el-button type="warning" style="padding: 10px 15px 10px 32px;">
      <img src="../../assets/images/Videos.png" style="width: 17px;position: absolute;top: 9px;left: 101px;">
      视频教程
    </el-button>
    <!-- 比例设置列表 -->
    <el-table :data="tableData" style="width: 100%" class="table_footer">
      <el-table-column prop="enterpriseName" label="盟员名称">
      </el-table-column>
      <el-table-column prop="cardDividePercent" label="售卡分成比例">
      </el-table-column>
    </el-table>
    <el-pagination @current-change="handleCurrentChange"
                   :current-page.sync="currentPage" :page-size="10"
                   layout="prev, pager, next, jumper" :total="totalAll" v-if="tableData.length>0">
    </el-pagination>
  </div>
</template>

<script>
import $http from '@/utils/http.js'
export default {
  name: 'sell-divide-setting',
  data() {
    return {
      input: '',
      dialogVisible: false,
      tableData: [],
      currentPage: 1,
      sum: 0,
      tableData3: [], // 渲染视图
      _tableData3: [], // 传输数据
      totalAll: 0,
    }
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    }
  },
  mounted: function() {
    $http.get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=1`)
      .then(res => {
        if (res.data.data) {
          this.tableData = res.data.data.records;
          this.totalAll = res.data.data.pages;
          if (this.tableData[0].isUnionOwner) {
            this.tableData[0].enterpriseName += '(盟主)';
          }
          this.tableData.forEach((v, i) => {
            v.cardDividePercent = (v.cardDividePercent).toFixed(2) + '%';
          });
        }
      })
      .catch(err => {
        this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
      });
  },
  methods: {
    // 比例设置 分页 获取数据
    handleCurrentChange(val) {
      $http.get(`/unionMember/pageMap/memberId/${this.unionMemberId}?current=${val}`)
        .then(res => {
          if (res.data.data) {
            this.tableData = res.data.data.records;
            this.totalAll = res.data.data.pages;
            if (val === 1) {
              if (this.tableData[0].isUnionOwner) {
                this.tableData[0].enterpriseName += '(盟主)';
              }
            }
            this.tableData.forEach((v, i) => {
              v.cardDividePercent = (v.cardDividePercent).toFixed(2) + '%';
            });
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 比例设置 弹出框 获取数据
    dialogShow() {
      this.dialogVisible = true;
      $http.get(`/unionMember/listMap/memberId/${this.unionMemberId}`)
        .then(res => {
          if (res.data.data) {
            this.tableData3 = res.data.data;
            if (this.tableData3[0].isUnionOwner) {
              this.input = this.tableData3[0].cardDividePercent;
              this.sum = parseFloat(this.input);
              this.tableData3.splice(0, 1);
            };
          };
        })
        .then(res => {
          let table3 = document.getElementById('table3');
          let inputs3 = table3.getElementsByTagName('input');
          for (let i = 0; i < inputs3.length; i++) {
            inputs3[i].value = this.tableData3[i].cardDividePercent;
            this.sum += parseFloat(inputs3[i].value);
          };
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 平均分配
    onAverage() {
      let _sum = 100 - this.input;
      let table3 = document.getElementById('table3');
      let inputs3 = table3.getElementsByTagName('input');
      let len = inputs3.length;
      if (len !== 0) {
        let average = (_sum / len).toFixed(2);
        if (this.input && 0 < this.input && 100 > this.input) {
          for (let i = 0; i < len; i++) {
            inputs3[i].value = average;
          }
          this.input = (100 - average * len).toFixed(2);
          this.sum = 100;
        } else {
          this.$message({
            showClose: true,
            message: '商机总比例之和不得超过100%,必须设置盟主比例',
            type: 'warning',
            duration: 5000,
          });
        }
      }
    },
    // 计算分配比例
    onChange() {
      let table3 = document.getElementById('table3');
      let inputs3 = table3.getElementsByTagName('input');
      this.sum = 0;
      $http.get(`/unionMember/listMap/memberId/${this.unionMemberId}`)
        .then(res => {
          if (res.data.data) {
            this._tableData3 = res.data.data;
            this._tableData3[0].cardDividePercent = this.input - 0;
            for (let i = 1, j = i - 1; i < inputs3.length + 1; i++ , j++) {
              inputs3[j].value = inputs3[j].value;
              this._tableData3[i].cardDividePercent = inputs3[j].value - 0;
            }
            this._tableData3.forEach((v, i) => {
              this.sum += parseFloat(v.cardDividePercent);
            })
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        })
    },
    // 保存
    onSave() {
      if (this.sum === 100) {
        let url = `/unionMember/cardDividePercent/memberId/${this.unionMemberId}`;
        // 处理数据
        let data = [];
        this._tableData3.forEach((v, i) => {
          data[i].cardDividePercent = v.cardDividePercent;
          data[i].memberId = v.memberId;
        })
        data.cardDividePercent =
          data.memberId = this._tableData3.memberId;
        $http.put(url, data)
          .then(res => {
            this.$message({ showClose: true, message: '保存成功', type: 'success', duration: 5000 });
          })
          .catch(err => {
            this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
          })
      } else {
        this.$message({
          showClose: true,
          message: '商机总比例之和应为100%',
          type: 'warning',
          duration: 5000
        });
      };
    },
    // 关闭弹窗重置数据
    resetData() {
      this.input = '';
      let table3 = document.getElementById('table3');
      let inputs3 = table3.getElementsByTagName('input');
      // inputs3 是伪数组 不能foreach遍历?
      for (let i = 0; i < inputs3.length; i++) {
        inputs3[i].value = '';
      }
    },
  }
}
</script>

<style lang='less' rel="stylesheet/less" scoped>
.table_footer {
  margin-top: 22px;
}

.model_setting {
  padding: 30px 30px;
  p {
    padding: 10px 0 10px 60px;
  }
}
</style>


