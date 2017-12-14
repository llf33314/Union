<template>
  <div id="UnionCardSetting">
    <Breadcrumb :header-name="['联盟卡设置']"></Breadcrumb>
    <div class="container">
      <el-tabs v-model="editableTabsValue" type="card" @tab-remove="removeTab" @tab-click="jumpTo">
        <el-tab-pane label="折扣卡设置" name="1" v-if="isUnionOwner">
          <discount-card-setting></discount-card-setting>
        </el-tab-pane>
        <el-tab-pane label="活动卡设置" name="2">
          <activity-card-setting></activity-card-setting>
        </el-tab-pane>
        <el-tab-pane v-for="(item, index) in editableTabs" :key="item.name" :label="item.title" :name="item.name" :url="item.url" closable>
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import Breadcrumb from '@/components/public-components/Breadcrumb';
import DiscountCardSetting from './DiscountCardSetting';
import ActivityCardSetting from './ActivityCardSetting';
export default {
  name: 'UnionCardSetting',
  components: {
    Breadcrumb,
    DiscountCardSetting,
    ActivityCardSetting
  },
  data() {
    return {
      editableTabsValue: '1',
      editableTabs: [
        { title: 'test3', name: '3', url: '/my-activity-card' },
        { title: 'test4', name: '44', url: '/my-activity-card' }
      ],
      closable: true
    };
  },
  computed: {
    unionMemberId() {
      return this.$store.state.unionMemberId;
    },
    isUnionOwner() {
      return this.$store.state.isUnionOwner;
    }
  },
  methods: {
    // 点击标签页跳转
    jumpTo(tab, event) {
      if (tab.$attrs.url) {
        this.$router.push({ path: tab.$attrs.url });
      }
    },
    // 关闭标签页
    removeTab(targetName) {
      let tabs = this.editableTabs;
      let activeName = this.editableTabsValue;
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            let nextTab = tabs[index + 1] || tabs[index - 1];
            if (nextTab) {
              activeName = nextTab.name;
            }
          }
        });
      }
      this.editableTabsValue = activeName;
      this.editableTabs = tabs.filter(tab => tab.name !== targetName);
    }
  }
};
</script>

