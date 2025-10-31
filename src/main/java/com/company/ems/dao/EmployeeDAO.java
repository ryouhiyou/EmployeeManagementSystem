package com.company.ems.dao;

import com.company.ems.model.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeDAO {

    /**
     * 查询符合搜索条件的总记录数。
     * 对应 EmployeeDAO.xml 中的 <select id="getTotalRecords">
     * @param search 搜索关键词（用于动态SQL）
     * @return 总记录数
     */
    int getTotalRecords(@Param("search") String search);

    /**
     * 分页查询所有员工记录，并支持搜索。
     * 对应 EmployeeDAO.xml 中的 <select id="listAllEmployees">
     * @param offset 偏移量（起始位置）
     * @param limit 每页限制条数
     * @param search 搜索关键词（用于动态SQL）
     * @return 员工列表
     */
    List<Employee> listAllEmployees(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("search") String search
    );

    /**
     * 插入新员工记录。
     * 对应 EmployeeDAO.xml 中的 <insert id="insertEmployee">
     * @param employee 员工对象
     * @return 影响的行数
     */
    int insertEmployee(Employee employee);

    /**
     * 更新员工记录。
     * 对应 EmployeeDAO.xml 中的 <update id="updateEmployee">
     * @param employee 员工对象
     * @return 影响的行数
     */
    int updateEmployee(Employee employee);

    /**
     * 删除员工记录。
     * 对应 EmployeeDAO.xml 中的 <delete id="deleteEmployee">
     * @param id 员工ID
     * @return 影响的行数
     */
    int deleteEmployee(int id);

    /**
     * 根据ID查询员工记录。
     * 对应 EmployeeDAO.xml 中的 <select id="selectEmployeeById">
     * @param id 员工ID
     * @return 员工对象
     */
    Employee selectEmployeeById(int id);
}