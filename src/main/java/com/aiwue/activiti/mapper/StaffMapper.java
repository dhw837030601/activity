package com.aiwue.activiti.mapper;


import com.aiwue.common.entity.hr.Staff;
import com.aiwue.activiti.mapper.provider.StaffSqlProvider;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface StaffMapper extends tk.mybatis.mapper.common.Mapper<Staff> {

    @SelectProvider(type = StaffSqlProvider.class, method = "getStaffListByConditions")
    public List<Staff> getStaffListByConditions(Map<String, Object> param);

    /**
     *
     * @description 根据员工用户名查询员工信息
     * @param staffName
     * @return
     * @throws
     * @return Staff    返回类型
     * @author 梁凌
     * @date 2016年8月26日 下午5:17:43
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE name = #{staffName} ")
    public Staff getStaffByName(@Param("staffName") String staffName);

    /**
     *
     * @description 查询指定Id之外是否还有该用户名的员工信息
     * @param staffName
     * @param staffId
     * @return
     * @return Staff    返回类型
     * @author 梁凌
     * @date 2016年8月31日 下午4:22:25
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE name = #{staffName} AND id != #{staffId} ")
    public Staff queryByNameExceptId(@Param("staffName") String staffName, @Param("staffId") Integer staffId);

    /**
     *
     * @description 根据员工手机号查询员工信息
     * @param mobile
     * @return
     * @return Staff    返回类型
     * @author 梁凌
     *
     * @date 2016年8月26日 下午5:17:43
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE mobile = #{mobile} ")
    public  Staff getStaffByMobile(@Param("mobile") String mobile);

    /**
     *
     * @description 查询指定Id之外是否还有该手机号的员工信息
     * @param mobile
     * @param staffId
     * @return
     * @return Staff    返回类型
     * @author 梁凌
     * @date 2016年8月31日 下午4:22:25
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE mobile = #{mobile} AND id != #{staffId} ")
    public  Staff queryByMobileExceptId(@Param("mobile") String mobile, @Param("staffId") Integer staffId);

    /**
     *
     * @description 根据部门id获取员工
     * @param departmentId
     * @return
     * @throws
     *
     * @return List<Staff>    返回类型
     * @author 梁凌
     * @date 2016年10月8日 上午10:20:57
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE departmentId = #{departmentId} ")
    public List<Staff> getStaffByDepartmentId(@Param("departmentId") Integer departmentId);


    /**
     * 员工id
     * @param id
     * @return
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE id = #{id} ")
    public List<Staff> getStaffById(@Param("id") Integer id);

     /**
     *
     * @description 查询所有员工信息
     * @return
     * @return List<Staff>    返回类型
     * @throws
     * @author 张海涛
     * @date 2017年8月29日 下午6:47:03
     */
    @Select(" SELECT * FROM hr_staff_tbl")
    public List<Staff> getAllStaff();

    /**
     * 根据 staffId和accesstion 查询 staff
     * @param staffId
     * @param at
     * @return
     * 张欢
     */
    @Select(" SELECT * FROM hr_staff_tbl WHERE id = #{staffId} and accessToken = #{at}")
    Staff getStaffByStaffIdAndAccessToken(@Param("staffId") Integer staffId, @Param("at") String at);


/**
 *
 * @Description: 员工角色列表
 *
 * @Param: query
 * @return: java.util.List<com.aiwue.common.entity.hr.Staff>
 * @auther: 芦毓
 * @date: 2018-08-02 14:01
 */
    // 分页显示
    @SelectProvider(type = StaffSqlProvider.class, method = "selectStaffByPage")
    @Results({@Result(id = true, property = "id", column = "id"),
            @Result(property = "roleList", column = "id", javaType = List.class,
                    many = @Many(select = "com.aiwue.mapper.mgmt.staffRole.mapper.PrivStaffRoleMapper.getStaffRoleListByStaffId"))
    })
    public List<Staff> selectStaffByPage(Map<String, Object> query);

    @Select("SELECT id,`name` FROM hr_staff_tbl  ")
    ArrayList<Staff> getStaffList();
}
