package com.gt.union;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/11 0011.
 */
public class MybatisGenerator {
    public static final String projectPath = System.getProperty("user.dir") + "/mybatisGenerator";
    public static final String javaPath = projectPath + "/src/main/java";
    public static final String xmlPath = javaPath + "/com/gt/union/mapper/";

    public static final String packagePath = "com.gt.union";

    public static final String mapperName = "%sMapper";
    public static final String xmlName = "%sMapper";
    public static final String serviceName = "I%sService";
    public static final String serviceImplName = "%sServiceImpl";
    public static final String controllerName = "%sController";
    public static final String[] tablePrefixes = new String[]{"t_"};

    public static final DbType dbType = DbType.MYSQL;
    public static final String driverName = "com.mysql.jdbc.Driver";
    public static final String url = "jdbc:mysql://113.106.202.51:3306/gt_aliyuan?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false";
    public static final String userName = "root";
    public static final String password = "gt123456";

    //basic
    /*public static final String[] generateTables = new String[]{"t_union_main","t_union_apply", "t_union_apply_info", "t_union_member"
            , "t_union_discount", "t_union_info_dict", "t_union_notice", "t_union_establish_record", "t_union_transfer_record"
            , "t_union_member_preferential_service", "t_union_member_preferential_manager"};*/

    //business
    /*public static final String[] generateTables = new String[]{"t_union_business_recommend", "t_union_business_recommend_info"
            , "t_union_brokerage", "t_union_pay_recommend", "t_union_brokerage_pay_record"};*/

    //card
    /*public static final String[] generateTables = new String[]{"t_union_bus_member_card", "t_union_card_divide_record"
            , "t_union_card_info", "t_union_card_info_detail", "t_union_bus_member_card", "t_union_card_divide_info"
            , "t_union_upcard_msg", "t_union_upgrade_card_record"};*/

    //consume
    /*public static final String[] generateTables = new String[]{"t_union_consume_record", "t_union_consume_service_record"
            , "t_union_card_consume_preferential_record"};*/

    //brokerage
    /*public static final String[] generateTables = new String[]{"t_union_brokerage_withdrawals_record", "t_union_verify_member"};*/

    public static final String[] generateTables = new String[]{"t_union_brokerage_withdrawals_record"};

    public static final String author = "linweicong";

    public static void main(String[] args) {
        AutoGenerator autoGenerator = new AutoGenerator();

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setAuthor(author);
        globalConfig.setControllerName(controllerName);
        globalConfig.setServiceName(serviceName);
        globalConfig.setServiceImplName(serviceImplName);
        globalConfig.setMapperName(mapperName);
        globalConfig.setXmlName(xmlName);
        globalConfig.setOutputDir(javaPath);
        globalConfig.setFileOverride(true);
        globalConfig.setActiveRecord(true);
        globalConfig.setEnableCache(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(false);
        //全部配置
        autoGenerator.setGlobalConfig(globalConfig);

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(dbType);
        dataSourceConfig.setTypeConvert(new MySqlTypeConvert(){
            @Override
            public DbColumnType  processTypeConvert(String fieldType) {
                return super.processTypeConvert(fieldType);
            }
        });
        dataSourceConfig.setDriverName(driverName);
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setUsername(userName);
        dataSourceConfig.setPassword(password);
        //数据源配置
        autoGenerator.setDataSource(dataSourceConfig);

        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setTablePrefix(tablePrefixes);
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setInclude(generateTables);
        //生成策略配置
        autoGenerator.setStrategy(strategyConfig);

        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(packagePath);
        packageConfig.setController("controller");
        packageConfig.setMapper("mapper");
        packageConfig.setEntity("entity");
        packageConfig.setService("service");
        packageConfig.setServiceImpl("service.impl");
        packageConfig.setXml("mapper");
        //包配置
        autoGenerator.setPackageInfo(packageConfig);

        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                this.setMap(map);
            }
        };
        List<FileOutConfig> fileOutConfigs = new ArrayList<>();
        fileOutConfigs.add(new FileOutConfig("/template/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return xmlPath + tableInfo.getMapperName() + ".xml";
            }
        });
        injectionConfig.setFileOutConfigList(fileOutConfigs);
        autoGenerator.setCfg(injectionConfig);

        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        //模版配置
        autoGenerator.setTemplate(templateConfig);

        autoGenerator.execute();
    }
}
