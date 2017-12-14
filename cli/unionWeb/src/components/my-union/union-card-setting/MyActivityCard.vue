<template>
  <div id="MyActivityCard">
    <nav class="explain">
      <span >说明：</span>
      <p>
        <div style="float: right;color: #666666">项目/商品  状态：
          <span style="color: #20A0FF">未提交</span>
          <span class="icon" >!</span>
        </div>
        您已开通ERP系统，您可将ERP系统的服务项目添加至联盟活动卡中，联盟会员办理活动卡后即可享受各个ERP系统的服务项目
      </p>
      <div class="auditRecord">
        <div class="top_">
          <span>审核记录</span>
          <strong @click="closeRecord">×</strong>
        </div>
        <div class="bottom_">
          <p>2017-12-12 12：12：12</p>
          <div>提交审核通过</div>
        </div>
      </div>
    </nav>
    <section class="unionList">
      <p>ERP项目列表</p>
      <div class="addUnion">
        <el-button type="primary" @click="newProject1">新增项目</el-button>
        <span>ERP项目添加至活动卡，联盟会员可通过活动卡到ERP管理系统进行核销</span>
      </div>
      <!--没有相关数据-->
      <div id="noUnion" v-if="0">
        <img src="~assets/images/noCurrent.png">
        <p>
          还没有相关数据
        </p>
      </div>
      <!--列表数据-->
      <el-table :data="tableData1" style="width: 100%" max-height="450" id="table1">
        <el-table-column prop="projectName" label="项目名称">
        </el-table-column>
        <el-table-column prop="amount" label="数量">
          <template slot-scope="scope">
            <el-input placeholder="请输入比例" @change="onChange(scope)"></el-input>
          </template>
        </el-table-column>
        <el-table-column prop="handle" label="操作" width="180">
          <template slot-scope="scope">
            <el-button size="small" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
        </el-table-column>
      </el-table>
    </section>
    <section class="unionList" style="margin-bottom: 35px;">
      <p>ERP项目列表</p>
      <div class="addUnion">
        <el-button type="primary" @click="addCommodity">新增商品</el-button>
        <span>进销存商品添加至活动卡，联盟会员可通过活动卡到ERP管理系统进行核销</span>
      </div>
      <!--没有相关数据-->
      <div id="noUnion1" v-if="0">
        <img src="~assets/images/noCurrent.png">
        <p>
          还没有相关数据
        </p>
      </div>
      <el-table :data="tableData2" style="width: 100%" max-height="450" id="table2">
        <el-table-column prop="projectName" label="商品名称">
        </el-table-column>
        <el-table-column prop="specification" label="规格">
        </el-table-column>
        <el-table-column prop="amount" label="数量">
          <template slot-scope="scope">
            <el-input placeholder="请输入比例" @change="onChange(scope)"></el-input>
          </template>
        </el-table-column>
        <el-table-column prop="handle" label="操作" width="180">
          <template slot-scope="scope">
            <el-button size="small" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
        </el-table-column>
      </el-table>
    </section>
    <!--页面底部固定-->
    <footer>
      <el-button>保存</el-button>
      <el-button type="primary" @click="commitAudit">提交审核</el-button>
    </footer>
    <!--提交审核的弹出框-->
    <div class="model_2">
      <el-dialog title="通过" :visible.sync="visible1" size="tiny">
        <hr>
        <div>
          <img src="~assets/images/delect01.png"  class="fl">
          <span>请确认您的项目，提交审核后不可修改项目内容，且不可</span>
          <p>再次提交项目！</p>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="visible1=false">确定</el-button>
            <el-button @click="visible1=false">取消</el-button>
          </span>
      </el-dialog>
    </div>
    <!--新增项目的弹出框-->
    <div class="model_3">
      <el-dialog title="新增项目" :visible.sync="visible2" size="tiny">
        <hr>
        <div>
          <p>
            项目名称：
            <el-input v-model="input1" placeholder="请输入项目名称" style="width:200px"></el-input>
          </p>
          <p>
            <span style="margin-left: 28px;">数量：</span>
            <el-input v-model="input2" placeholder="请输入数量" style="width:200px;margin-top:25px;"></el-input>
          </p>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="visible2=false">确定</el-button>
            <el-button @click="visible2=false">取消</el-button>
          </span>
      </el-dialog>
    </div>
    <!--添加商品的弹出框-->
    <div class="model_4">
      <el-dialog title="新增商品" :visible.sync="visible3" size="tiny">
        <hr>
        <div >
          <div>
            <el-select v-model="value1" clearable placeholder="请选择门店" style="width:180px;">
            <el-option v-for="item in options1"
                       :key="item.value1" :label="item.label" :value="item.value1">
            </el-option>
          </el-select>
            <el-select v-model="value2" clearable placeholder="请选择分类" style="width:180px;margin: 0 20px">
              <el-option v-for="item in options2"
                         :key="item.value2" :label="item.label" :value="item.value2">
              </el-option>
            </el-select>
            <el-input  placeholder="请输入商品名称" style="width:180px;"  icon="search"></el-input>
          </div>
          <div class="section_ clearfix">
            <div style="float: left">
            <el-table ref="multipleTable" :data="tableData3" tooltip-effect="dark"
                      style="width: 585px;" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55">
              </el-table-column>
              <el-table-column prop="ProductName" label="商品名称" >
              </el-table-column>
              <el-table-column prop="specification" label="规格">
              </el-table-column>
              <el-table-column prop="inventory" label="库存">
              </el-table-column>
            </el-table>
              <el-pagination style="position: absolute;right: 278px;bottom: 19px;"
                @size-change="handleSizeChange1"
                @current-change="handleCurrentChange1"
                :current-page.sync="currentPage1"
                :page-size="100"
                layout="prev, pager, next, jumper"
                :total="1000">
              </el-pagination>
            </div>
            <div class="rightContent">
              <p>已选择：2</p>
              <div>
                商品 2
                <el-input-number size="small" v-model="num1"></el-input-number>
                <el-button type="text">删除</el-button>
              </div>
            </div>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="visible3=false">确定</el-button>
            <el-button @click="visible3=false">取消</el-button>
          </span>
      </el-dialog>
    </div>
    <!--新增项目的弹出框-->
    <div class="model_5">
      <el-dialog title="新增项目" :visible.sync="visible4" size="tiny">
        <hr>
        <div >
          <div>
            <el-select v-model="value3" clearable placeholder="请选择行业" style="width:180px;">
              <el-option v-for="item in options3"
                         :key="item.value3" :label="item.label" :value="item.value3">
              </el-option>
            </el-select>
            <el-input  placeholder="请输入ERP项目名称" style="width:180px;margin-left: 20px;"  icon="search"></el-input>
          </div>
          <div class="section_ clearfix">
          <div style="float: left">
            <el-table ref="multipleTable" :data="tableData4" tooltip-effect="dark"
                      style="width: 490px;" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="55">
              </el-table-column>
              <el-table-column prop="ProductName" label="商品名称" >
              </el-table-column>
            </el-table>
            <el-pagination style="position: absolute;left:112px;bottom: 19px;"
                           @size-change="handleSizeChange2"
                           @current-change="handleCurrentChange2"
                           :current-page.sync="currentPage2"
                           :page-size="100"
                           layout="prev, pager, next, jumper"
                           :total="1000">
            </el-pagination>
          </div>
          <div class="rightContent" style="width: 325px;">
            <p>已选择：2</p>
            <div>
              ERP项目名称2
              <el-input-number size="small" v-model="num1"></el-input-number>
              <el-button type="text">删除</el-button>
            </div>
          </div>
        </div>
        </div>
        <span slot="footer" class="dialog-footer">
            <el-button type="primary" @click="visible4=false">确定</el-button>
            <el-button @click="visible4=false">取消</el-button>
          </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyActivityCard',
  data() {
    return {
      tableData1: [{
        projectName: '项目名称一',
        amount: '王小虎'
      }],
      tableData2: [{
        projectName: '项目名称一',
        specification:'太空灰/64G',
        amount: '王小虎'
      }],
      tableData3: [{
        ProductName: '2016-05-03',
        specification: '王小虎',
        inventory: '王小虎'
      }],
      tableData4: [{
        ProductName: 'ERP项目名称1'
      }],
      visible1:false,
      visible2:false,
      visible3:false,
      visible4:false,
      input1:'',
      input2:'',
      options1: [{
        value1: '选项1',
        label: '11111'
      }],
      value1: '',
      options2: [{
        value2: '选项2',
        label: '222222'
      }],
      value2: '',
      options3: [{
        value3: '选项1',
        label: '11111'
      }],
      value3: '',
      num1: 1,
      currentPage1: 5,
      currentPage2: 5,
    }
  },
  mounted(){
    //鼠标移动事件
    $(".icon").mouseenter(function (){
      $(".auditRecord").show();
    });
    $(".auditRecord").mouseenter(function (){
        $(this).show();
    })
  },
  methods: {
    //关闭按钮
    closeRecord(){
      $(".auditRecord").hide();
    },
    //新增项目按钮
    newProject(){
      this.visible2=true;
    },
    newProject1(){
      this.visible4=true;
    },
    //新增商品按钮
    addCommodity(){
      this.visible3=true;
    },
    onChange(scope){

    },
    //提交审核
    commitAudit(){
      this.visible1=true;
    },
    handleSelectionChange(val) {

    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange1(val) {
      console.log(`当前页: ${val}`);
    },
    handleSizeChange1(val) {
      console.log(`每页 ${val} 条`);
    },
    handleCurrentChange2(val) {
      console.log(`当前页: ${val}`);
    },
    handleSizeChange2(val) {
      console.log(`每页 ${val} 条`);
    },
  }
}
</script>

