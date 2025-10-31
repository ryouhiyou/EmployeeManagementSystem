package com.company.ems.dao;

import com.company.ems.model.Employee;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmployeeDAO {

    /**
     * 查询符合搜索条件的总记录数。
     * @param search 搜索关键词
     * @return 总记录数
     */
    int getTotalRecords(@Param("search") String search);

    /**
     * 分页查询员工记录，支持搜索、排序字段和排序方向。
     * 确保所有参数都有 @Param 注解，以避免 MyBatis 绑定错误。
     * @param offset 偏移量（起始位置）
     * @param limit 每页限制条数
     * @param search 搜索关键词
     * @param sortBy 排序字段名 (如: 'id', 'name', 'salary')
     * @param sortOrder 排序方向 (如: 'ASC', 'DESC')
     * @return 员工列表
     */
    List<Employee> listAllEmployees(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("search") String search,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder
    );

    int insertEmployee(Employee employee);
    int updateEmployee(Employee employee);
    int deleteEmployee(int id);
    Employee selectEmployeeById(int id);
}
