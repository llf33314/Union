package com.gt.dao.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.GeneratedKeyHolder;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class DaoUtil extends JdbcTemplate {

	/** 
	 * 保存一个对象到数据库中。
	 * 
	 * @param obj
	 * @return
	 */
	@Deprecated
	public Object save(Object obj) {
		StringBuffer sql = new StringBuffer();
		StringBuffer values = new StringBuffer();
		values.append(" values (");
		sql.append("insert into ");
		sql.append(this.getInsertTableSql(obj.getClass()));
		// 判断在字段上面有没有对应的标注
		if (this.testFieldAnnotation(obj.getClass())) {
			return this.saveObjectByFieldAnnotation(obj, sql, values);
		} else {
			return this.saveObjectByGetterAnnotation(obj, sql, values);
		}
	}

	/**
	 * 使用getter方法中的标注进行保存对象;
	 * 
	 * @param obj
	 * @param sql
	 * @param values
	 * @return 返回的Id值；
	 */
	private Object saveObjectByGetterAnnotation(Object obj, StringBuffer sql,
			StringBuffer values) {
		List<String> nameList = this.getAllFieldNames(obj.getClass());
		Class c = obj.getClass();
		// 字段上面没有对应的标注信息；
		List<Method> methodList = this.findAllGetMethods(obj.getClass());
		// 判断是否有Id标注
		// 判断有Id标注下面是否有对应的GeneratedValue
		Method idMethod = null;
		Iterator<Method> idMit = methodList.iterator();
		Method m = null;
		Id id = null;
		GeneratedValue gv = null;
		while (idMit.hasNext()) {
			m = idMit.next();
			id = m.getAnnotation(Id.class);
			if (id == null) {
				continue;
			} else {
				gv = m.getAnnotation(GeneratedValue.class);
				if (gv != null) {
					// idMethod = null;
					idMethod = m;
					// 移除含有id这个标注的方法;
					idMit.remove();
				}
				break;
			}
		}
		String temp = null;
		Iterator<Method> mit = null;
		Method method = null;
		for (int i = 0; i < nameList.size(); i++) {
			temp = "get" + nameList.get(i);
			mit = methodList.iterator();
			if (i == nameList.size() - 1) {
				while (mit.hasNext()) {
					method = mit.next();
					if (method.getName().equalsIgnoreCase(temp)) {
						Column column = method.getAnnotation(Column.class);
						sql.append(column.name() + ")");
						Object methodReturn = null;
						try {
							methodReturn = method.invoke(obj);
						} catch (Exception e1) {
							e1.printStackTrace();
							throw new RuntimeException("调用get方法出现异常！");
						}
						if (methodReturn instanceof java.util.Date) {
							java.util.Date date = (Date) methodReturn;
							values.append("'")
									.append(new java.sql.Timestamp(date
											.getTime()).toString())
									.append("')");
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("String")) {
							try {
								values.append("'")
										.append(method.invoke(obj).toString())
										.append("')");
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("转换String出现异常！");
							}
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("double")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("float")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("int")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("long")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("Integer")) {
							try {
								values.append(method.invoke(obj).toString())
										.append(")");
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("转换数字出现异常！");
							}
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("boolean")) {
						}
						break;
					}
				}
			} else {
				while (mit.hasNext()) {
					method = mit.next();
					if (method.getName().equalsIgnoreCase(temp)) {
						Column column = method.getAnnotation(Column.class);
						sql.append(column.name() + ",");
						if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("Date")) {
							java.util.Date date;
							try {
								date = (Date) method.invoke(obj);
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("转换Date出现异常！");
							}
							values.append("'")
									.append(new java.sql.Timestamp(date
											.getTime()).toString())
									.append("',");
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("String")) {
							try {
								Object str = method.invoke(obj);
								if (str == null) {
									values.append("");
								} else {
									values.append("'").append(str.toString())
											.append("',");
								}
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("转换String出现异常！");
							}
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("double")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("float")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("int")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("long")
								|| method.getReturnType().getSimpleName()
										.equalsIgnoreCase("Integer")) {
							try {
								Object val = method.invoke(obj);
								if (val == null) {
									
								} else {
									values.append(method.invoke(obj).toString())
											.append(",");
								}
							} catch (Exception e) {
								e.printStackTrace();
								throw new RuntimeException("转换数字出现异常！");
							}
						} else if (method.getReturnType().getSimpleName()
								.equalsIgnoreCase("boolean")) {
						}
						break;
					}
				}
			}
		}

		String methodName = idMethod.getName();
		methodName = methodName.replaceFirst("get", "set");
		Object key = this.invokeExecuteStatementCallBack(sql.append(
				values.toString()).toString());
		try {
			Method setId = c.getMethod(methodName, idMethod.getReturnType());
			setId.invoke(obj, key);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("设置id的操作方法出现问题，可能未遵循getter/setter方法规范！");
		}
		return key;
	}

	/**
	 * 获取Id标注的字段;
	 * 
	 * @param class1
	 * @return
	 */
	private Field findIdField(Class c) {
		Field[] fields = c.getDeclaredFields();
		Field f = null;
		for (int i = 0; i < fields.length; i++) {
			Id id = fields[i].getAnnotation(Id.class);
			if (id != null) {
				f = fields[i];
				return f;
			}
		}
		return null;
	}

	/**
	 * 使用Field中的标注保存对象;
	 * 
	 * @param obj
	 * @param sql
	 * @param values
	 * @return 保存后的id值;
	 */
	private Object saveObjectByFieldAnnotation(Object obj, StringBuffer sql,
			StringBuffer values) {
		Field idField = this.findIdField(obj.getClass());
		List<Field> fieldList = this.findAllFields(obj.getClass());
		Field field = null;
		Id id = null;
		GeneratedValue gv = null;
		Column col = null;
		if (fieldList.size() > 0) {
			// 获取第一个id标注的字段;
			if (fieldList.size() == 1) {// 判断一个类中只有一个字段的情况;
				field = fieldList.get(0);
				gv = field.getAnnotation(GeneratedValue.class);
				if (gv == null) {
					col = field.getAnnotation(Column.class);
					if (col == null) {
						sql.append(field.getName() + ")");
						// field.setAccessible(true);
					} else {
						sql.append(col.name());
					}
					values.append(this.getFieldReturnValue(obj, field) + ")");
				} else {
					col = field.getAnnotation(Column.class);
					if (col == null) {
						sql.append(field.getName() + ")");
						// field.setAccessible(true);
					} else {
						sql.append(col.name());
					}
					values.append("default)");
				}
			} else {// 判断类中有多个字段的情况;
				for (int i = 0; i < fieldList.size(); i++) {
					if (i == 0) {
						field = fieldList.get(i);
						gv = field.getAnnotation(GeneratedValue.class);
						if (gv == null) {
							col = field.getAnnotation(Column.class);
							if (col == null) {
								sql.append(field.getName()).append(",");
								// field.setAccessible(true);
							} else {
								sql.append(col.name());
							}
							values.append(this.getFieldReturnValue(obj, field))
									.append(",");
						}
					} else if (i == fieldList.size() - 1) {
						col = field.getAnnotation(Column.class);
						col = field.getAnnotation(Column.class);
						if (col.name() == null
								|| col.name().equalsIgnoreCase("")) {
							sql.append(field.getName()).append(")");
						} else {
							sql.append(col.name()).append(")").append(")");
						}
						values.append(this.getFieldReturnValue(obj, field));
					} else {
						col = field.getAnnotation(Column.class);
						if (col.name() == null
								|| col.name().equalsIgnoreCase("")) {
							sql.append(field.getName()).append(",");
						} else {
							sql.append(col.name()).append(",");
						}
						values.append(this.getFieldReturnValue(obj, field))
								.append(",");
					}
				}
			}
		}

		String executSql = sql.append(values.toString()).toString();
		Object key = this.invokeExecuteStatementCallBack(executSql);
		if (idField != null) {
			idField.setAccessible(true);
			try {
				idField.set(obj, key);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Id标注的字段设置出现异常!");
			}
		}
		return key;
	}

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
			} else if (value instanceof java.util.Date) {
				java.util.Date dd = (Date) value;
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
	 * 获取类中的所有字段;
	 * 
	 * @param c
	 * @return 类中的所有字段；
	 */
	private List<Field> findAllFields(Class c) {
		List<Field> fieldList = new ArrayList<Field>();
		Field[] fields = c.getDeclaredFields();
		Field field = null;
		Id id = null;
		Column col = null;
		// 获取所有字段并且判断字段上面对应的Id和column
		// 优先获取Id标注的字段;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			id = field.getAnnotation(Id.class);
			if (id == null) {
				continue;
			} else {
				fieldList.add(field);
				break;
			}
		}
		// 再次循环获取剩下的column标注的字段;这里需要排除有Id标注的字段;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			id = field.getAnnotation(Id.class);
			if (id == null) {
				col = field.getAnnotation(Column.class);
				if (col != null) {
					fieldList.add(field);
				}
			} else {
				// fieldList.add(field);
				continue;
			}
		}
		return fieldList;
	}

	/**
	 * 检查字段中是否有对应的标注;
	 * 
	 * @param c
	 * @return
	 */
	private boolean testFieldAnnotation(Class c) {
		Field[] fields = c.getDeclaredFields();
		Column col = null;
		for (int i = 0; i < fields.length; i++) {
			col = fields[i].getAnnotation(Column.class);
			if (col == null) {
				continue;
			} else {
				return true;
			}
		}
		return false;
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
	 * 批量保存一组对象集合，并且对象集合中的每个对象都含有对应的id数据；
	 * 此方法只是针对Id自动增长的对象有效，如果不需要对象中的Id数据，不建议采用此方法。 此方法运行效率较低。
	 * 
	 * @param List
	 *            需要保存的对象集合;
	 */
	@Deprecated
	public void saveAllBySetIds(List lists) {
		for (Object obj : lists) {
			this.save(obj);
		}
	}

	/**
	 * 反射获取类级别中的Table标注内容；
	 * 
	 * @param c
	 * @return
	 */
	private String getInsertTableSql(Class c) {
		String str = "";
		Table table = (Table) c.getAnnotation(Table.class);
		if (table != null) {
			if (table.catalog() != null) {
				str = table.catalog() + "." + table.name() + " (";
			} else {
				str = table.name() + " (";
			}
			return str;
		} else {
			throw new RuntimeException("类：" + c.getName() + " 没有Table标注!");
		}
	}

	/**
	 * 批量保存一组相同类型的数据对象到数据库中； 此方法只是做批量写入，对于需要写入后返回Id数据的不允许采用此方法,可以使用
	 * 
	 * @param lists
	 * @param c
	 * @return
	 */
	@Deprecated
	public int[] saveAll(List lists, Class c) {
		StringBuffer sql = new StringBuffer();
		StringBuffer values = new StringBuffer();
		values.append(" values (");
		sql.append("insert into ");
		sql.append(this.getInsertTableSql(c));
		if (testFieldAnnotation(c)) {
			List<List> allValueList = this.invokeBatchInsertByFieldAnnotation(
					lists, c, sql, values);
			return this.invokeBatchUpdate(sql.toString(), allValueList);
		} else {
			List<List> allValueList = this.invokeBatchInsertByGetterAnnotation(
					lists, c, sql, values);
			return this.invokeBatchUpdate(sql.toString(), allValueList);
		}
	}

	private List<List> invokeBatchInsertByFieldAnnotation(List<Object> lists,
			Class c, StringBuffer sql, StringBuffer values) {
		// TODO Auto-generated method stub

		List<List> allValueList = new ArrayList<List>();
		// 根据字段内容保存一组数据对象集合;
		List<Field> fieldList = this.findAllFields(c);
		Field field = null;
		GeneratedValue gv = null;
		Id id = null;
		Column col = null;
		Object obj = null;
		if (fieldList.size() == 1) {
			// 只有一个字段的实体类
			field = fieldList.get(0);
			id = field.getAnnotation(Id.class);
			if (id != null) {
				gv = field.getAnnotation(GeneratedValue.class);
				if (gv != null) {
					col = field.getAnnotation(Column.class);
					if (col.name() == null || col.name().equals("")) {
						sql.append(field.getName()).append(")");
					} else {
						sql.append(col.name()).append(")");
					}
					// 这里采用的对应Mysql数据库操作方式。
					values.append("default").append(")");
					List list = null;
					for (int i = 0; i < lists.size(); i++) {
						obj = lists.get(i);
						list = new ArrayList();
						allValueList.add(list);
					}

				}
			}
			col = field.getAnnotation(Column.class);
			if (col.name() == null || col.name().equals("")) {
				sql.append(field.getName()).append(")");
			} else {
				sql.append(col.name()).append(")");
			}
			values.append("?").append(")");
			List<Object> list = null;
			Object valueObj = null;
			for (int i = 0; i < lists.size(); i++) {
				obj = lists.get(i);
				list = new ArrayList<Object>();
				try {
					valueObj = field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("调用的获取字段中的内容出现错误。");
				}
				list.add(valueObj);
			}
			allValueList.add(list);
			sql.append(values.toString());
			return allValueList;
			// return this.invokeBatchUpdate(sql.toString(), allValueList);
		} else {
			// 一个对象
			List<Object> list = null;
			// Object valueObj = null;
			Map<String, Object> map = null;
			Map.Entry<String, Object> me = null;
			for (int i = 0; i < lists.size(); i++) {
				obj = lists.get(i);
				list = new ArrayList();
				if (i == 0) {
					for (int j = 0; j < fieldList.size(); j++) {
						field = fieldList.get(j);
						if (j == fieldList.size() - 1) {
							map = this.getFieldToMap(field, obj);
							if (map != null) {
								me = map.entrySet().iterator().next();
								sql.append(me.getKey()).append(")");
								values.append("?").append(")");
								list.add(me.getValue());
							}
						} else {
							map = this.getFieldToMap(field, obj);
							if (map != null) {
								me = map.entrySet().iterator().next();
								sql.append(me.getKey()).append(",");
								values.append("?").append(",");
								list.add(me.getValue());
							}
						}
					}
					allValueList.add(list);
				} else {
					for (int j = 0; j < fieldList.size(); j++) {
						field = fieldList.get(j);
						if (j == fieldList.size() - 1) {
							map = this.getFieldToMap(field, obj);
							if (map != null) {
								me = map.entrySet().iterator().next();
								list.add(me.getValue());
							}
						} else {
							map = this.getFieldToMap(field, obj);
							if (map != null) {
								me = map.entrySet().iterator().next();
								list.add(me.getValue());
							}
						}
					}
					allValueList.add(list);
				}
			}
			sql.append(values.toString());
			return allValueList;
		}
	}

	/**
	 * 利用字段标注的对象，获取字段中的数据值，注意如果Id是标注中含有GeneratedValue标注 那么返回值为null;
	 * 
	 * @param field
	 * @param obj
	 * @return
	 */
	private Map<String, Object> getFieldToMap(Field field, Object obj) {
		Map<String, Object> map = null;
		Id id = field.getAnnotation(Id.class);
		GeneratedValue gv = null;
		if (id != null) {
			gv = field.getAnnotation(GeneratedValue.class);
			if (gv == null) {
				map = this.getFieldToMapByColumn(field, obj);
			}
		} else {
			map = this.getFieldToMapByColumn(field, obj);
		}
		return map;
	}

	/**
	 * 获取字段对象中的名字，以及对应的值;
	 * 
	 * @param field
	 * @param obj
	 * @return
	 */
	private Map<String, Object> getFieldToMapByColumn(Field field, Object obj) {
		field.setAccessible(true);
		Map<String, Object> map = new HashMap<String, Object>();
		String columnName = "";
		Object value = null;
		Column col = field.getAnnotation(Column.class);
		if (col == null) {
			columnName = field.getName();
			try {
				value = field.get(obj);
				map.put(columnName, value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("字段内容去值出现问题...");
			}
		} else {
			if (col.name() == null || col.name().equals("")) {
				columnName = field.getName();
				try {
					value = field.get(obj);
					map.put(columnName, value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("字段内容取值出现问题...");
				}
			} else {
				columnName = col.name();
				try {
					value = field.get(obj);
					map.put(columnName, value);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("字段内容取值出现问题...");
				}
			}
		}
		return map;
	}

	/**
	 * 批量处理的getter方法标注解析。
	 * 
	 * @param lists
	 * @param c
	 * @param sql
	 * @param values
	 * @return
	 */
	private List<List> invokeBatchInsertByGetterAnnotation(List<Object> lists,
			Class c, StringBuffer sql, StringBuffer values) {
		List<String> fieldNames = this.getAllFieldNames(c);
		List<Method> methodList = this.findAllGetMethods(c);
		// Method idMethod = null;
		Iterator<Method> idMit = methodList.iterator();
		Method m = null;
		Id id = null;
		GeneratedValue gv = null;
		while (idMit.hasNext()) {
			m = idMit.next();
			id = m.getAnnotation(Id.class);
			if (id == null) {
				continue;
			} else {
				gv = m.getAnnotation(GeneratedValue.class);
				if (gv != null) {
					// idMethod = null;
					// idMethod = m;
					// 移除含有id这个标注的方法;
					idMit.remove();
				}
				break;
			}
		}
		String temp = null;
		// 存储方法参数lists中的对象临时变量；
		Object obj = null;
		// 复制methodList给另一个List集合
		List<Method> other = null;
		List<Method> other1 = null;
		// 存储一个对象中的所有需要添加的"?"内容；
		List valueList = null;
		// 通过反射获取的对应"?"中的参数内容;
		Object valueObj = null;
		String columnName = null;
		// 所有的对象中的"?"对象；
		final List<List> allValueList = new ArrayList<List>();
		// 遍历lists中的所有对象；
		for (int j = 0; j < lists.size(); j++) {
			obj = lists.get(j);
			other = new ArrayList<Method>(methodList);
			other1 = new ArrayList<Method>(methodList);
			valueList = new ArrayList();
			for (int i = 0; i < fieldNames.size(); i++) {
				temp = "get" + fieldNames.get(i);
				if (i == fieldNames.size() - 1) {
					if (j == 0) {
						columnName = this.getInsertField(temp, other);
						if (!columnName.equals("")) {
							sql.append(columnName).append(")");
							values.append("?)");
							valueObj = this
									.getInsertToPreparedStetmentSetValue(obj,
											other1, temp);
							if (valueObj != null) {
								valueList.add(valueObj);
							}
						}
					}
					valueObj = this.getInsertToPreparedStetmentSetValue(obj,
							other1, temp);
					if (valueObj != null) {
						valueList.add(valueObj);
					}
				} else {
					if (j == 0) {
						columnName = this.getInsertField(temp, other);
						if (!columnName.equals("")) {
							sql.append(columnName).append(",");
							values.append("?,");
							valueObj = this
									.getInsertToPreparedStetmentSetValue(obj,
											other1, temp);
							if (valueObj != null) {
								valueList.add(valueObj);
							}
						}
					}
					valueObj = this.getInsertToPreparedStetmentSetValue(obj,
							other1, temp);
					if (valueObj != null) {
						valueList.add(valueObj);
					}
				}
			}
			allValueList.add(valueList);
		}
		sql.append(values.toString());
		return allValueList;
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
	 * 根据字段名获取对应get方法体上的Column标注中对应的column中name的属性值
	 * 
	 * @param temp
	 * @param methodList
	 * @return
	 */
	private String getInsertField(String temp, List<Method> methodList) {
		// TODO Auto-generated method stub
		String str = "";
		Iterator<Method> mit = methodList.iterator();
		Method method = null;
		while (mit.hasNext()) {
			method = mit.next();
			// 匹配相关的对应的方法
			if (method.getName().equalsIgnoreCase(temp)) {
				// 获取get方法上面的标注。
				Column column = method.getAnnotation(Column.class);
				mit.remove();
				return column.name();
			}
		}
		return str;
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
	 * sql的分页查询  返回分页对象
	 * @param sql
	 * @param start
	 * @param pageSize
	 * @param objs
	 * @return
	 */
	public PageList findObjectByPageList(String sql,
			Integer start, Integer pageSize, Object... objs){
		start = this.defaultCurrentPage(start);
		pageSize = this.defaultPageSize(pageSize);
		List<Map<String, Object>> resultList = null;
		resultList = this.findObjectByPage(sql, start, pageSize, objs);
		String countSql = this.splitSql(sql);
		long count = this.queryForLong(countSql,objs);
		PageList page = new PageList(start, count, pageSize);
		page.setList(resultList);
		return page;
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
	 * 根据类型删除一个对象;
	 * 
	 * @param c
	 *            数据表映射的类
	 * @param id
	 *            数据表id
	 */
	public void deleteDataByObject(Class c, final Serializable id) {
		StringBuffer sql = new StringBuffer();
		this.setDeleteSqlByObject(c, sql);
		sql.append(" WHERE ").append(this.getClassIdField(c)).append("=?");
		this.update(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement pstm) throws SQLException {
				pstm.setObject(1, id);
			}
		});
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
	 * 删除根据id集合删除数据表的一批数据(单一数据表),
	 * 
	 * @param c
	 *            数据表映射的类
	 * @param idList
	 *            数据表id的List集合;
	 */
	public void deleteDataByObjectList(Class c, final List<Serializable> idList) {
		StringBuffer sql = new StringBuffer();
		this.setDeleteSqlByObject(c, sql);
		sql.append(" WHERE ").append(this.getClassIdField(c)).append("=?");
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
	 * 删除一批对应的数据表映射对象,注意这个对象中必须有Id标注，并且数据对象的id必须要有对应的数据值；（可以不同数据表） 注意此操作效率相对较低;
	 * 
	 * @param objList
	 */
	public void deleteDataByObjectList(List<Object> objList) {
		List<String> sqlList = new ArrayList<String>();
		for (Object object : objList) {
			sqlList.add(this.parseSqlByObject(object));
		}
		this.batchUpdate(sqlList.toArray(new String[] {}));
	}

	/**
	 * 删除一个表映射对象;
	 * 
	 * @param obj
	 *            必须是带有标注的数据表映射对象;并且必须要有id标注和对应的属性值;
	 */
	public void deleteDataByObject(Object obj) {
		String sql = this.parseSqlByObject(obj);
		this.update(sql);
	}

	/**
	 * 根据对象解析成对应操作sql语句;
	 * 
	 * @param obj
	 *            必须是带有标注的数据表映射对象;并且必须要有id标注和对应的属性值;
	 * @return
	 */
	private String parseSqlByObject(Object obj) {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		Class c = obj.getClass();
		Table table = (Table) c.getAnnotation(Table.class);
		if (table == null) {
			throw new RuntimeException(c.getName() + "类中没有对应的Table标注");
		} else {
			if (table.catalog() != null && !table.catalog().equals("")) {
				sql.append(table.catalog()).append(".");
			}
			if (table.name() != null && !table.name().equals("")) {
				sql.append(table.name()).append(" WHERE ");
			} else {
				sql.append(c.getSimpleName()).append(" WHERE ");
			}
		}
		Field[] fields = c.getDeclaredFields();
		Field field = null;
		Id id = null;
		Column column = null;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			field.setAccessible(true);
			id = field.getAnnotation(Id.class);
			if (id != null) {
				break;
			}
		}
		if (id != null) {
			column = field.getAnnotation(Column.class);
			if (column != null && column.name() != null
					&& !column.name().equals("")) {
				sql.append(column.name());
			} else {
				sql.append(field.getName());
			}
			try {
				Object idObj = field.get(obj);
				if (idObj == null) {
					throw new RuntimeException(c.getName()
							+ "对象中的id属性获取内容为null;");
				} else {
					if (idObj.getClass().getSimpleName()
							.equalsIgnoreCase("String")) {
						sql.append("='").append(idObj.toString()).append("'");
					} else {
						sql.append("=").append(idObj.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(c.getName() + "对象中的id属性获取内容出现异常;");
			}

		} else {
			Method[] methods = c.getMethods();
			Method method = null;
			for (int i = 0; i < methods.length; i++) {
				method = methods[i];
				if (method.getName().startsWith("get")) {// 只查找get开头的方法上面有对应标注信息的；
					id = method.getAnnotation(Id.class);
					if (id != null) {
						break;
					}
				}
			}
			if (id != null) {
				column = method.getAnnotation(Column.class);
				if (column != null && column.name() != null
						&& !column.name().equals("")) {
					sql.append(column.name());
				} else {
					String str = method.getName().replace("get", "");
					str = str.substring(0, 1).toUpperCase()
							+ str.subSequence(1, str.length());
					sql.append(str);
				}
				try {
					Object idObj = method.invoke(obj);
					if (idObj == null) {
						throw new RuntimeException(c.getName()
								+ "对象中Id标注的方法调用内容获取值为null;");
					} else {
						if (idObj.getClass().getSimpleName()
								.equalsIgnoreCase("String")) {
							sql.append("='").append(idObj.toString())
									.append("'");
						} else {
							sql.append("=").append(idObj.toString());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(c.getName()
							+ "对象中Id标注的方法调用内容获取出现异常;");
				}

			}
		}
		return sql.toString();
	}

	/**
	 * 设置要删除的数据表对应类型中的sql语句;
	 * 
	 * @param c
	 * @param sql
	 */
	private void setDeleteSqlByObject(Class c, StringBuffer sql) {
		Table table = (Table) c.getAnnotation(Table.class);
		// 判断类上面有没有具体的Table标注字段;
		if (table == null) {
			throw new RuntimeException("对象上面没有具体的table标注");
		} else {
			sql.append("DELETE FROM ");
			// 1.判断Table标注上面是否有catalog属性
			if (table.catalog() == null || table.catalog().equals("")) {
				// 表示使用datasource默认的数据库
			} else {
				sql.append(table.catalog()).append(".");
			}
			// 判断Table标注上面是否有对应的name属性;
			if (table.name() == null || table.name().equals("")) {
				// 表示类的名字就是数据库表明;
				sql.append(c.getSimpleName()).append(" ");
			} else {
				sql.append(table.name()).append(" ");
			}
		}
	}

	private String getClassIdField(Class c) {
		String str = null;
		Field[] fields = c.getDeclaredFields();
		Field field = null;
		Id id = null;
		Column column = null;
		for (int i = 0; i < fields.length; i++) {
			field = fields[i];
			field.setAccessible(true);
			id = field.getAnnotation(Id.class);
			if (id != null) {
				break;
			}
		}
		if (id != null) {
			column = field.getAnnotation(Column.class);
			if (column != null && column.name() != null
					&& !column.name().equals("")) {
				return column.name();
			} else {
				return field.getName();
			}
		} else {
			Method[] methods = c.getMethods();
			Method method = null;
			for (int i = 0; i < methods.length; i++) {
				method = methods[i];
				if (method.getName().startsWith("get")) {
					id = method.getAnnotation(Id.class);
					if (id != null) {
						break;
					}
				}
			}
			if (id != null) {
				column = method.getAnnotation(Column.class);
				if (column != null && column.name() != null
						&& !column.name().equals("")) {
					return column.name();
				} else {
					str = method.getName().replace("get", "");
					str = str.substring(0, 1).toUpperCase()
							+ str.subSequence(1, str.length());
					return str;
				}
			}
		}
		return null;
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
	 * 增加单个对象到数据库中;
	 * @param dbName 数据库名可以为null或""，使用配置中datasource默认的数据库；
	 * @param tableName 数据表名
	 * @param idName 主键名称;
	 * @param obj 对象;
	 * @return
	 */
	public Object saveDataByObject(String dbName,String tableName,String idName,Object obj){
		try {
			Map map = BeanUtils.describe(obj);
			map.remove(idName);
			return this.saveObjectByMap(dbName, tableName, map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 增加批量的数据到数据库中;效率低，但是可以返回对应id;
	 * @param dbName 数据库名可以为null或""，使用配置中datasource默认的数据库；
	 * @param tableName 数据表名
	 * @param list 批量数据集合;
	 * @return 返回id的list集合;
	 */
	public List<Object> saveDataByObjectList(String dbName,String tableName,String idName,List list){
		List<Object> idList = new ArrayList<Object>();
		Object id = null;
		for (Object object : list) {
			id = this.saveDataByObject(dbName, tableName,idName, object);
			idList.add(id);
		}
		return idList;
	}

	/**
	 * 增加批量的数据到数据库中;效率高，不可以返回对应id;
	 * @param dbName 数据库名可以为null或""，使用配置中datasource默认的数据库；
	 * @param tableName 数据表名
	 * @param idName id字段名称;
	 * @param list 批量数据集合;
	 */
	public void saveDataByObjectList2(String dbName,String tableName,String idName,List list){
		List<Map<String,Object>> objList = new ArrayList<Map<String,Object>>();
		Map<String,Object> m = null;
		for (Object obj : list) {
			try {
				m = BeanUtils.describe(obj);
				m.remove(idName);
				objList.add(m);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		this.saveObjectByMapList2(dbName, tableName, objList);
	}
	
	/**
	 * 根据对象更新数据库;
	  * @param dbName
	 *            数据库名,可以为null或空字符"",
	 * @param tableName
	 *            数据库中对应数据表名称（必须的）
	 * @param idColumn
	 *            数据表中对应的id字段名称（必须的）
	 * @param idValue
	 *            数据表中对应的id字段对应的数据值（必须的）
	 * @param obj 需要更新的对象;
	 */
	public void updateDateByObject(String dbName, String tableName,
			String idColumn, Serializable idValue, Object obj){
		Map map = null;
		try {
			map = BeanUtils.describe(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		map.remove(idColumn);
		map.remove("class");
		this.updateDateByMap(dbName, tableName, idColumn, idValue, map);
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

	@Override
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
