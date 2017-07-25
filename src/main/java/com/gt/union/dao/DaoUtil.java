package com.gt.union.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class DaoUtil extends JdbcTemplate {


	/**
	 * 调用jdbcTemplate中的execute(StatementCallback)操作;
	 *
	 * @param executSql
	 * @return 返回添加对象的Id值;
	 */

	private Object invokeExecuteStatementCallBack(final String executSql) {
		Object key = this.execute(new StatementCallback() {
			public Object doInStatement(Statement stm) throws SQLException,
					DataAccessException {
				stm.executeUpdate(executSql);
				ResultSet rs = null;
				try {
					rs = stm.getGeneratedKeys();
					rs.next();
					return rs.getObject(1);
				} finally {
					DaoUtil.this.close(null, null, rs);
				}
			}
		});
		return key;
	}

	/**
	 * 获取字段中设置的值;
	 *
	 * @param obj
	 * @param field
	 * @return
	 */
	private String getFieldReturnValue(Object obj, Field field) {
		String str = "";
		field.setAccessible(true);
		try {
			Class type = field.getType();
			Object value = field.get(obj);
			if (type.getSimpleName().equalsIgnoreCase("String")) {
				str = "'" + value.toString() + "'";
			} else if (type.getSimpleName().equalsIgnoreCase("Integer")
					|| type.getSimpleName().equalsIgnoreCase("int")
					|| type.getSimpleName().equalsIgnoreCase("Long")
					|| type.getSimpleName().equalsIgnoreCase("long")
					|| type.getSimpleName().equalsIgnoreCase("Double")
					|| type.getSimpleName().equalsIgnoreCase("Float")
					|| type.getSimpleName().equalsIgnoreCase("double")
					|| type.getSimpleName().equalsIgnoreCase("float")) {
				str = value.toString();
			} else if (value instanceof Date) {
				Date dd = (Date) value;
				str = "'" + new Timestamp(dd.getTime()).toString() + "'";
			} else if (type.getSimpleName().equalsIgnoreCase("boolean")) {
				throw new RuntimeException("暂不支持boolean类型数据。");
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("获对象中取字段值的内容出现异常！");
		}

	}


	/**
	 * 关闭相应资源;
	 * 
	 * @param conn
	 * @param stm
	 * @param rs
	 */
	private void close(Connection conn, Statement stm, ResultSet rs) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭连接异常！");
			}
		}
		if (stm != null) {
			try {
				stm.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭Statement异常！");
			}
		}
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭结果集异常！");
			}
		}
	}

	/**
	 * 反射获取所有的字段
	 * 
	 * @param obj
	 * @return
	 */
	private List<String> getAllFieldNames(Class obj) {
		Field[] fields = obj.getDeclaredFields();
		List<String> nameList = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			nameList.add(fields[i].getName());
		}
		return nameList;
	}

	/**
	 * 反射获取所有以get开头的方法
	 * 
	 * @param obj
	 * @return
	 */
	private List<Method> findAllGetMethods(Class obj) {
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = obj.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().startsWith("get")) {
				methodList.add(methods[i]);
			}
		}
		return methodList;
	}






	/**
	 * 批量存储调用的批量操作方法;
	 * 
	 * @param sql
	 * @param allValueList
	 * @return
	 */
	private int[] invokeBatchUpdate(String sql, final List<List> allValueList) {
		return this.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public int getBatchSize() {
				return allValueList.size();
			}

			public void setValues(PreparedStatement pstm, int i)
					throws SQLException {
				List list = allValueList.get(i);
				for (int k = 0; k < list.size(); k++) {
					pstm.setObject(k + 1, list.get(k));
				}
			}
		});
	}

	/**
	 * 通过反射方式获取对应get方法中所返回的数据对象;
	 * 
	 * @param obj
	 * @param methodList
	 * @param temp
	 * @return
	 */
	private Object getInsertToPreparedStetmentSetValue(Object obj,
			List<Method> methodList, String temp) {
		// TODO Auto-generated method stub
		Iterator<Method> mit = methodList.iterator();
		Method method = null;
		while (mit.hasNext()) {
			method = mit.next();
			if (method.getName().equalsIgnoreCase(temp)) {
				mit.remove();
				try {
					Object obj1 = method.invoke(obj);
					return obj1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(
							"方法getInsertToPreparedStetmentSetValue中调用get方法出现问题;");
				}
			}
		}
		return null;
		// return null;
	}


	/**
	 * 使用jdbc操作，给定对应的数据Map对象，进行数据的存储操作;
	 * 
	 * @param dbName
	 *            数据库名称;可以不写，不写的情况下默认使用的是当前datasouce定义的数据库。
	 * @param tableName
	 *            要写入的数据表名;
	 * @param map
	 *            写入的数据表的数据内容;格式key=[对应操作数据表的字段名],value[对应操作数据表字段的值]
	 * @return
	 */
	public Object saveObjectByMap(String dbName, String tableName,
			Map<String, Object> map) {
		map.remove("class");
		// 声明操作sql
		StringBuffer sql = new StringBuffer();
		StringBuffer values = new StringBuffer();
		// 声明对应存储数据的List对象;
		final List list = new ArrayList();
		// 编写代码;
		this.setInsertSqlByMap(sql, values, dbName, tableName);
		// 判断是否Map为空;
		if (map == null || map.size() == 0) {
			throw new RuntimeException("没有要写入数据表对应的字段和内容.");
		} else {
			// 遍历要写入的内容Map;
			Iterator<Map.Entry<String, Object>> mapIt = map.entrySet()
					.iterator();
			Map.Entry<String, Object> me = null;
			while (mapIt.hasNext()) {
				me = mapIt.next();
				sql.append(me.getKey()).append(",");
				values.append("?,");
				list.add(me.getValue());
			}
			// 截取最后一位",";
			String sql1 = sql.toString().substring(0,
					sql.toString().lastIndexOf(","))
					+ ")";
			String value1 = values.toString().subSequence(0,
					values.toString().lastIndexOf(","))
					+ ")";
			sql = new StringBuffer();
			sql.append(sql1).append(value1);
			
			final String insertSql=sql.toString();
			return this.execute(new PreparedStatementCreator() {
				
				@Override
				public PreparedStatement createPreparedStatement(Connection connection)
						throws SQLException {
					PreparedStatement ps =connection.prepareStatement(insertSql,PreparedStatement.RETURN_GENERATED_KEYS);
					for (int i = 0; i < list.size(); i++) {
						ps.setObject(i + 1, list.get(i));
					}
					return ps;
				}
			}, new PreparedStatementCallback() {
				public Object doInPreparedStatement(
						PreparedStatement pstm) throws SQLException,
						DataAccessException {
					
					pstm.executeUpdate();
					ResultSet rs = pstm.getGeneratedKeys();
					rs.next();
					Object obj = rs.getObject(1);
					DaoUtil.this.close(null, null, rs);
					return obj;
				}
			});
		}
	}

	/**
	 * 批量添加数据到数据库中的数据表中； (只针对同一数据表的数据批量写入操作，注意这个方法效率相对比较低)
	 * 
	 * @param dbName
	 *            数据库名
	 * @param tableName
	 *            数据库中对应的数据表名;
	 * @param mapList
	 *            需要存储的数据字段以及数据;
	 * @return List 对应参数List中一一对应的存储后返回的数据Id;
	 */
	public List<Object> saveObjectByMapList(String dbName, String tableName,
			List<Map<String, Object>> mapList) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < mapList.size(); i++) {
			list.add(this.saveObjectByMap(dbName, tableName, mapList.get(i)));
		}
		return list;
	}

	/**
	 * 批量写入一组数据到数据库
	 * 
	 * @param dbName
	 *            数据库名称可以为null或""，表示使用当前数据源的数据库
	 * @param tableName
	 *            数据表名称（必须）
	 * @param mapList
	 *            添加的数据内容List集合;
	 * @return 执行后影响的数据量;
	 */
	public int[] saveObjectByMapList2(String dbName, String tableName,
			List<Map<String, Object>> mapList) {
		StringBuffer sql = new StringBuffer();
		StringBuffer values = new StringBuffer();
		this.setInsertSqlByMap(sql, values, dbName, tableName);
		if (mapList != null && mapList.size() != 0) {
			final List<List> allValuesList = new ArrayList<List>();
			Map<String, Object> map = null;
			Iterator<Map.Entry<String, Object>> mapIt = null;
			Map.Entry<String, Object> me = null;
			List<Object> paramList = null;
			for (int i = 0; i < mapList.size(); i++) {
				paramList = new ArrayList<Object>();
				map = mapList.get(i);
				map.remove("class");
				if (i == 0) {
					// 第一次循环将sql和values内容写入完整;
					mapIt = map.entrySet().iterator();
					while (mapIt.hasNext()) {
						me = mapIt.next();
						sql.append(me.getKey()).append(",");
						values.append("?,");
						paramList.add(me.getValue());
					}
					String sql1 = sql.toString().substring(0,
							sql.toString().lastIndexOf(","))
							+ ")";
					String value1 = values.toString().subSequence(0,
							values.toString().lastIndexOf(","))
							+ ")";
					sql = new StringBuffer();
					sql.append(sql1).append(value1);
				} else {
					mapIt = map.entrySet().iterator();
					while (mapIt.hasNext()) {
						me = mapIt.next();
						paramList.add(me.getValue());
					}
				}
				allValuesList.add(paramList);
			}
			return this.batchUpdate(sql.toString(),
					new BatchPreparedStatementSetter() {

						public int getBatchSize() {
							return allValuesList.size();
						}

						public void setValues(PreparedStatement pstm, int i)
								throws SQLException {
							List<Object> list = allValuesList.get(i);
							for (int j = 0; j < list.size(); j++) {
								pstm.setObject(j + 1, list.get(j));
							}
						}
					});
		} else {
			throw new RuntimeException(
					"saveObjectByMapList2方法参数List<Map<String,Object>>没有要写入的数据内容..");
		}
	}

	/**
	 * 用于生成批量写入数据库之前的sql数据;
	 * 
	 * @param sql
	 *            传入的sql前一部分:insert into
	 * @param values
	 *            传入sql的后一部分
	 * @param dbName
	 *            数据库名称
	 * @param tableName
	 *            数据库对应数据表名称;
	 */
	private void setInsertSqlByMap(StringBuffer sql, StringBuffer values,
			String dbName, String tableName) {
		sql.append("insert into ");
		if (dbName != null && !dbName.equals("")) {
			sql.append(dbName).append(".");
		}
		if (tableName == null || tableName.equals("")) {
			throw new RuntimeException("没有对应要写入数据表的名字!");
		} else {
			sql.append(tableName).append(" (");
			values.append(" values (");
		}
	}

	/**
	 * sql的分页查询(仅仅支持Mysql)
	 * 
	 * @param sql
	 * @param start
	 * @param pageSize
	 * @param objs
	 * @return
	 */
	public List<Map<String, Object>> findObjectByPage(String sql,
			Integer start, Integer pageSize, Object... objs) {
		start = this.defaultCurrentPage(start);
		pageSize = this.defaultPageSize(pageSize);
		List<Map<String, Object>> resultList = null;
		StringBuilder sb = new StringBuilder();
		Integer startNum = this.getNextPageNumber(start, pageSize);
		sb.append(sql).append(" LIMIT ").append(startNum.intValue())
				.append(",").append(pageSize.intValue());
		resultList = this.queryForList(sb.toString(), objs);
		return resultList;
	}
	
	/**
	 * 查询所有数据 cz
	 * @param sql
	 * @param objs
	 * @return
	 */
	public List<Map<String, Object>> findAllList(String sql,Object... objs) {
		List<Map<String, Object>> resultList = null;
		resultList = this.queryForList(sql, objs);
		return resultList;
	}
	
	

	/**
	 * 装拼 统计sql语句
	 * @param sql
	 * @return
	 */
	private String splitSql(String sql) {
		String[] countSql = sql.split("from");
		if(countSql.length>1){
		}else{
			countSql = sql.split("FROM");
			if(countSql.length<=1){
				throw new RuntimeException("sql 语句中没有含有 from 或 FROM ..请确认！");
			}
		}
		String ss = "select count(*) from "+countSql[1];
		return ss;
	}


	private Integer getNextPageNumber(Integer start, Integer pageSize) {
		if (start != null && pageSize != null) {
			if (start.intValue() == 1 || start.intValue() == 0
					|| start.intValue() < 0) {
				return 0;
			} else {
				return (start.intValue() - 1) * pageSize.intValue();
			}
		} else {
			throw new RuntimeException("分页的参数出现问题，start或pageSize为null...");
		}
	}


	/**
	 * 使用sql根据id删除一条数据记录(单一数据表)
	 * 
	 * @param dbName
	 *            数据库名称 可以为null或“”，默认是用DataSource配置的数据库;
	 * @param tableName
	 *            数据表名称
	 * @param id
	 */
	public void deleteDataById(String dbName, String tableName, String idName,
			final Serializable id) {
		StringBuffer sql = new StringBuffer();
		this.setDeleteSql(dbName, tableName, idName, sql);
		this.update(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement pstm) throws SQLException {
				pstm.setObject(1, id);
			}
		});
	}

	/**
	 * 组合要删除的数据表的sql语句;
	 * 
	 * @param dbName
	 * @param tableName
	 * @param idName
	 * @param sql
	 */
	private void setDeleteSql(String dbName, String tableName, String idName,
			StringBuffer sql) {
		sql.append("DELETE FROM ");
		if (dbName != null && !dbName.equals("")) {
			sql.append(dbName).append(".");
		}
		if (tableName != null && !tableName.equals("")) {
			if (idName != null && !idName.equals("")) {
				sql.append(tableName).append(" WHERE ").append(idName)
						.append("=?");
			} else {
				throw new RuntimeException("setDeleteSql方法中idName没有删除id的名字");
			}
		} else {
			throw new RuntimeException(
					"setDeleteSql方法中tableName没有数据表的名字，无法操作..");
		}
	}


	/**
	 * 使用sql根据id集合删除一批数据记录(单一数据表);
	 * 
	 * @param dbName
	 *            数据库名称 可以为null或“”，默认是用DataSource配置的数据库;
	 * @param tableName
	 *            数据表名称
	 * @param idList
	 *            数据表id的List集合;
	 */
	public void deleteDataByIds(String dbName, String tableName, String idName,
			final List<Serializable> idList) {
		StringBuffer sql = new StringBuffer();
		this.setDeleteSql(dbName, tableName, idName, sql);
		this.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstm, int index)
					throws SQLException {
				Serializable ser = idList.get(index);
				pstm.setObject(1, ser);
			}

			@Override
			public int getBatchSize() {
				return idList.size();
			}
		});
	}




	/**
	 * 根据id更新一张数据表中的数据内容;
	 * 
	 * @param dbName
	 *            数据库名,可以为null或空字符"",
	 * @param tableName
	 *            数据库中对应数据表名称（必须的）
	 * @param idColumn
	 *            数据表中对应的id字段名称（必须的）
	 * @param idValue
	 *            数据表中对应的id字段对应的数据值（必须的）
	 * @param updateMap
	 *            要更新的数据列以及对应的数据.Map Key:字段名 value:字段值
	 */
	public void updateDateByMap(String dbName, String tableName,
			String idColumn, Serializable idValue, Map<String, Object> updateMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ");
		if (dbName != null && !dbName.equals("")) {
			sql.append(dbName).append(".");
		}
		if (tableName == null || tableName.equals("")) {
			throw new RuntimeException("数据表名称没有...");
		} else {
			sql.append(tableName).append(" SET ");
		}
		if (idColumn == null || idColumn.equals("")) {
			throw new RuntimeException("没有对应的数据表的id字段...");
		}
		if (updateMap == null || updateMap.size() == 0) {
			throw new RuntimeException("没有需要更新的数据内容....");
		} else {
			Iterator<Map.Entry<String, Object>> mit = updateMap.entrySet()
					.iterator();
			Map.Entry<String, Object> me = null;
			final List<Object> paramList = new ArrayList<Object>();
			while (mit.hasNext()) {
				me = mit.next();
				sql.append(me.getKey()).append(" =? ,");
				paramList.add(me.getValue());
			}
			String str = sql.toString();
			str = str.substring(0, str.length() - 1);
			sql = new StringBuffer();
			sql.append(str);
			sql.append(" WHERE ").append(idColumn).append("=?");
			paramList.add(idValue);
			this.update(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement pstm)
						throws SQLException {
					for (int i = 0; i < paramList.size(); i++) {
						pstm.setObject(i + 1, paramList.get(i));
					}
				}
			});
		}
	}




	
	/**
	 * 设置分页的当前页为空时的默认值
	 * @param currentPage
	 * 
	 */
	private Integer defaultCurrentPage(Integer currentPage){
		if(currentPage==null){
			currentPage = 1;
		}
		return currentPage;
	}
	/**
	 * 当大小为空时或小于0时设置为默认值
	 * @param pageSize
	 * @return
	 */
	private Integer defaultPageSize(Integer pageSize){
		if(pageSize == null||pageSize <= 0){
			pageSize = 10;
		}
		return pageSize;
	}

	public int queryForInt(String sql, Object... args)
			throws DataAccessException {
		Integer i = queryForObject(sql, Integer.class, args);
		return i==null?0:i;
	}
	@Override
	public <T> T queryForObject(String sql, Class<T> requiredType,
			Object... args) throws DataAccessException {
		List<T> list = queryForList(sql, requiredType, args);
		if(list!=null && list.size()>0)return list.get(0);
		return null;
	}
	
	/**
	 * 重写queryForMap
	 */
	@Override
	public Map<String, Object> queryForMap(String sql,Object...objects){
		List<Map<String, Object>> list = this.queryForList(sql,objects);
		if(list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 重写queryForMap
	 */
	@Override
	public Map<String, Object> queryForMap(String sql){
		List<Map<String, Object>> list = this.queryForList(sql);
		if(list.size() > 0){
			return list.get(0);
		}else{
			return null;
		}
	}
}
