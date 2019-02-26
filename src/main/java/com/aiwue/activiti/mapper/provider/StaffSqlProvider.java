package com.aiwue.activiti.mapper.provider;


import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class StaffSqlProvider {

	public String getStaffListByConditions(Map<String, Object> parameter){

		final String staffName = (parameter != null) ? (String) parameter.get("name") : null;
		final String mobile = (parameter != null) ? (String) parameter.get("mobile") : null;
		final Integer status = (parameter != null) ? (Integer) parameter.get("status") : null;
		final Integer type = (parameter != null) ? (Integer) parameter.get("type") : null;
		String sql = new SQL(){{

			SELECT(" *," +
					"(select name from sys_role_tbl where d.roleId= sys_role_tbl.id )roleName   ");

			FROM("hr_staff_tbl d ");

			if (staffName != null && !StringUtils.isEmpty(staffName) && !"".equals(staffName)) {

				WHERE(" d.name LIKE CONCAT('%', #{name},'%' )");

			}
			if (mobile != null && !StringUtils.isEmpty(mobile)&&!"".equals(mobile)) {

				WHERE(" d.mobile LIKE CONCAT('%', #{mobile},'%' )");

			}
			if (status != null && status != 100) {

				WHERE(" d.status = #{status}");

			}
			if (type != null) {

				WHERE(" d.type = #{type}");

			}
			if (status != null && status == 100){

			}

			ORDER_BY(" d.id ");

		}}.toString();

		return sql;

	}


	public String selectStaffByPage(Map<String, Object> parameter){
		final String staffName = (parameter != null) ? (String) parameter.get("name") : null;
		final String mobile = (parameter != null) ? (String) parameter.get("mobile") : null;
		final Integer status = (parameter != null) ? (Integer) parameter.get("status") : null;
		String sql = new SQL(){{

			SELECT("*");

			FROM("hr_staff_tbl d ");

			if (staffName != null && !StringUtils.isEmpty(staffName) && !"".equals(staffName)) {

				WHERE(" d.name LIKE CONCAT('%', #{name},'%' )");

			}
			if (mobile != null && !StringUtils.isEmpty(mobile) && !"".equals(mobile)) {

				WHERE(" d.mobile LIKE CONCAT('%', #{mobile},'%' )");

			}
			if (status != null && status != 100) {

				WHERE(" d.status = #{status}");

			}

			if (status != null && status == 100){

			}

			ORDER_BY(" d.id ");

		}}.toString();

		return sql;
	}


}
