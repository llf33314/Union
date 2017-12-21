<template>
  <div>
    <span>参与盟员数</span>
    <span style="color:#20a0ff; cursor:pointer" @click="showMember">{{ joinMemberCount }}</span>
    <!-- 弹出框 参与盟员数 -->
    <div class="membership">
      <el-dialog title="参与盟员数" :visible.sync="visible">
        <hr>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column prop="member.enterpriseName" label="盟员名称">
          </el-table-column>
          <el-table-column prop="" label="活动卡项目名称">
            <template slot-scope="scope">
              <el-popover trigger="hover" placement="bottom">
                <p v-for="item in scope.row.itemList" :key="item.id">活动卡名称：{{ item.name }}, 数量：{{ item.number }}</p>
                <div slot="reference" class="name-wrapper">
                  <span>{{ scope.row.itemList_ }}</span>
                </div>
              </el-popover>
            </template>
          </el-table-column>
        </el-table>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
export default {
  name: 'join-member',
  props: ['joinMemberCount', 'activityId'],
  data() {
    return {
      visible: false,
      tableData: []
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  methods: {
    showMember() {
      $http
        .get(`/unionCardProject/activityId/${this.activityId}/unionId/${this.unionId}/joinMember`)
        .then(res => {
          if (res.data.data) {
            this.visible = true;
            this.tableData = res.data.data || [];
            this.tableData.forEach((v, i) => {
              v.itemList_ = [];
              v.itemList.forEach(val => {
                v.itemList_.push(val.name);
              });
              v.itemList_ = v.itemList_.join(',');
            });
          } else {
            this.tableData = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    }
  }
};
</script>
