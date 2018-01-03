<template>
  <div>
    <p>项目列表</p>
    <div class="addUnion" v-if="canEdit">
      <el-button type="primary" @click="addNonErpProject">新增项目</el-button>
    </div>
    <!-- 非ERP 没有相关数据 -->
    <div id="noUnion" v-if="nonErpTextList.length === 0">
      <img src="~assets/images/noCurrent.png">
      <p>
        还没有相关数据
      </p>
    </div>
    <!-- 非ERP 列表数据 -->
    <el-table v-if="nonErpTextList.length > 0" :data="nonErpTextList" style="width: 100%" max-height="450" id="table1" v-show="canEdit">
      <el-table-column prop="name" label="项目名称">
        <template slot-scope="scope">
          <el-input v-model="scope.row.name" placeholder="请输入项目名称" @change="nonErpTextListChange"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="number" label="数量">
        <template slot-scope="scope">
          <el-input v-model="scope.row.number" placeholder="请输入数量" @keyup.native="check(scope)" @change="nonErpTextListChange"></el-input>
        </template>
      </el-table-column>
      <el-table-column prop="" label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="small" @click="handleDelete(scope)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-table v-if="nonErpTextList.length > 0" :data="nonErpTextList" style="width: 100%" max-height="450" id="table1" v-show="!canEdit">
      <el-table-column prop="name" label="项目名称">
      </el-table-column>
      <el-table-column prop="number" label="数量">
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { numberCheck } from '@/utils/filter.js';
export default {
  name: 'my-non-erp-list',
  props: ['nonErpTextList'],
  data() {
    return {};
  },
  computed: {
    canEdit() {
      return this.$store.state.activityCanEdit;
    }
  },
  methods: {
    // 校验折扣输入为数字类型
    check(scope) {
      scope.row.number = numberCheck(scope.row.number);
    },
    addNonErpProject() {
      this.nonErpTextList.push({ name: '', number: '' });
      this.nonErpTextListChange();
    },
    handleDelete(scope) {
      this.nonErpTextList.splice(scope.$index, 1);
      this.nonErpTextListChange();
    },
    nonErpTextListChange() {
      this.$emit('nonErpTextListChange', this.nonErpTextList);
    }
  }
};
</script>
