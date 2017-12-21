<template>
  <div class="auditRecord">
    <div class="top_">
      <span>审核记录</span>
      <strong @click="closeRecord">×</strong>
    </div>
    <div class="bottom_">
      <div v-for="item in checkRecord" :key="item.id">
        <p> {{ item.createTime }} </p>
        <div> {{ item.illustration }} </div>
      </div>
    </div>
  </div>
</template>

<script>
import $http from '@/utils/http.js';
import { timeFilter } from '@/utils/filter.js';
export default {
  name: 'check-record',
  props: ['activityId'],
  data() {
    return {
      checkRecord: []
    };
  },
  computed: {
    unionId() {
      return this.$store.state.unionId;
    }
  },
  mounted: function() {
    this.init();
    // 鼠标移动事件 显示审核记录
    $('.icon').mouseenter(function() {
      $('.auditRecord').show();
    });
    $('.auditRecord').mouseenter(function() {
      $(this).show();
    });
  },
  methods: {
    init() {
      $http
        .get(`/unionActivityFlow/activityId/${this.activityId}/unionId/${this.unionId}`)
        .then(res => {
          if (res.data.data) {
            this.checkRecord = res.data.data || [];
            this.checkRecord.forEach(v => {
              v.createTime = timeFilter(v.createTime);
            });
          } else {
            this.checkRecord = [];
          }
        })
        .catch(err => {
          this.$message({ showClose: true, message: err.toString(), type: 'error', duration: 5000 });
        });
    },
    // 关闭审核记录
    closeRecord() {
      $('.auditRecord').hide();
    }
  }
};
</script>
