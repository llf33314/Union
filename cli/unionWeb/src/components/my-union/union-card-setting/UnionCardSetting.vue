<template>
  <div id="UnionCardSetting">
    <Breadcrumb :header-name="['联盟卡设置']"></Breadcrumb>
    <div class="container">
      <el-tabs v-model="editableTabsValue" type="card" @tab-remove="removeTab" @tab-click="jumpTo">
        <el-tab-pane label="折扣卡设置" name="1">
          <discount-card-setting></discount-card-setting>
        </el-tab-pane>
        <el-tab-pane label="活动卡设置" name="2">
          <activity-card-setting></activity-card-setting>
        </el-tab-pane>
        <el-tab-pane v-for="item in editableTabs" :key="item.name" :label="item.title" :name="item.name" :url="item.url" closable>
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
      editableTabs: [],
      closable: true,
      tabIndex: 2
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
  mounted: function() {
    eventBus.$on('myActivityAddTabs', item => {
      let newTabName = ++this.tabIndex + '';
      let url = '/my-union/union-card-setting/my-activity-card/' + item.activity.id;
      let jumpFlag = true;
      // 已经添加tab的活动不再新增tab
      this.editableTabs.forEach((v, i) => {
        if (v.url === url) {
          this.editableTabsValue = v.name;
          jumpFlag = false;
        }
      });
      if (jumpFlag) {
        this.editableTabs.push({
          title: item.activity.name,
          name: newTabName,
          url: url
        });
        this.editableTabsValue = newTabName;
        this.$router.push({ path: url });
      }
    });
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
            } else {
              activeName = '2';
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

