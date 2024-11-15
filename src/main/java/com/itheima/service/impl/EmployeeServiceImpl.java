package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;
import com.itheima.mapper.EmployeeMapper;
import com.itheima.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService{

    @Override
    public Employee getUser(Employee employee) {
        //根据用户名查询数据
        return lambdaQuery().eq(Employee::getUsername, employee.getUsername()).one();
    }

    @Override
    public void addEmployee(Employee employee) {
        //设置初始密码，进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        save(employee);
    }

    @Override
    public Page<Employee> employeePage(Integer page, Integer pageSize, String name) {
        //构建分页条件
        Page<Employee> empPage = Page.of(page, pageSize);
        //添加排序条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Employee::getUsername, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行分页查询
        page(empPage, queryWrapper);
        return empPage;
    }
}
