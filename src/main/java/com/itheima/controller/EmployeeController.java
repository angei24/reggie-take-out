package com.itheima.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //将密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        Employee emp = employeeService.getUser(employee);
        //没有查到数据则登录失败
        if (emp == null) {
            return R.error("登录失败");
        }
        //密码不一致，返回登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //查看员工状态，若禁用，则返回禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //登陆成功，将id放入session并返回结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //1.清除session中保存的id
        request.getSession().removeAttribute("employee");
        //2.返回结果
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> addEmployee(@RequestBody Employee employee) {
        log.info("用户信息：{}", employee.toString());
        Employee emp = employeeService.getUser(employee);
        if (emp != null)
            return R.error("用户已存在");
        employeeService.addEmployee(employee);
        return R.success("员工新增成功");
    }

    @GetMapping("/page")
    public R<Page> employeePage(Integer page, Integer pageSize, String name) {
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);
        //分页查询
        Page<Employee> p = employeeService.employeePage(page, pageSize, name);
        return R.success(p);
    }

    @PutMapping
    public R<String> updateEmployee(@RequestBody Employee employee) {
        log.info("员工信息：{}", employee.toString());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("通过id查询员工信息");
        Employee emp = employeeService.getById(id);
        return R.success(emp);
    }
}
