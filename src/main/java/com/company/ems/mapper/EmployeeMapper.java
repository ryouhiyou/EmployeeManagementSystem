package com.company.ems.mapper;

import com.company.ems.model.Employee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * MyBatis Mapper 接口，与 EmployeeMapper.xml 文件中的 SQL 语句关联。
 * 方法名必须与 XML 中的 <select/insert/update/delete> 标签的 'id' 属性一致。
 */
public interface EmployeeMapper { // 👈 必须是 interface (接口)

    // 1. 获取总记录数 (对应 XML id="getTotalRecords")
    // 接受 String 类型的 'search' 参数
    int getTotalRecords(@Param("search") String search);

    // 2. 分页查询员工列表 (对应 XML id="listAllEmployees")
    // 接受所有分页和搜索参数
    List<Employee> listAllEmployees(
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("search") String search,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder
    );

    // 3. 插入员工 (对应 XML id="insertEmployee")
    void insertEmployee(Employee employee);

    // 4. 更新员工 (对应 XML id="updateEmployee")
    void updateEmployee(Employee employee);

    // 5. 删除员工 (对应 XML id="deleteEmployee")
    void deleteEmployee(int id);

    // 6. 根据 ID 查询单个员工 (对应 XML id="selectEmployeeById")
    Employee selectEmployeeById(int id);
}