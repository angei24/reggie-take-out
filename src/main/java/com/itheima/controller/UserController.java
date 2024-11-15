package com.itheima.controller;

import cn.hutool.core.util.StrUtil;
import com.itheima.common.R;
import com.itheima.entity.User;
import com.itheima.service.UserService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> getMsg(@RequestBody User user, HttpServletRequest request) {
        //获取手机号
        String phone = user.getPhone();
        log.info("phone:{}", phone);
        if (StrUtil.isNotEmpty(phone)) {
            //随机生成4为验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);
            //调用阿里云服务发送短信
//            SMSUtils.sendMessage("瑞吉外卖", "", phone, code);
            //保存密码到session
//            request.getSession().setAttribute(phone, code);
            //保存密码到Redis中并设置有效期5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("短信发送成功");
        }
        return R.error("断行发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        log.info("map:{}", map.toString());
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //从session中取出验证码并比较
//        String sessionCode = request.getSession().getAttribute(phone).toString();
        //从Redis中取出验证码
        String sessionCode = redisTemplate.opsForValue().get(phone).toString();
        User user = userService.lambdaQuery().eq(User::getPhone, phone).one();
        if (sessionCode != null && sessionCode.equals(code)) {
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());
            //登陆成功删除验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return R.success("退出登录成功");
    }
}
