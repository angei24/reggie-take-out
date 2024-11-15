package com.itheima.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    Employee getUser(Employee employee);

    void addEmployee(Employee employee);

    Page<Employee> employeePage(Integer page, Integer pageSize, String name);
}
