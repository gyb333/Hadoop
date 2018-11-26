package Study.SpringCloud.GYB.controller;

import Study.SpringCloud.GYB.domain.User;
import Study.SpringCloud.GYB.redis.RedisClusterService;
import Study.SpringCloud.GYB.redis.RedisService;
import Study.SpringCloud.GYB.redis.UserKey;
import Study.SpringCloud.GYB.result.CodeMsg;
import Study.SpringCloud.GYB.result.Result;
import Study.SpringCloud.GYB.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/demo")
public class SampleController {

	@Autowired
    UserService userService;
	
	@Autowired
    RedisService redisService;

	@Autowired
    RedisClusterService redisClusterService;
	
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> home() {
        return Result.success("Hello，world");
    }

    @RequestMapping("/test")
    @ResponseBody
    public String Test(){
        return " hello world test!";
    }

    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error() {
        return Result.error(CodeMsg.SESSION_ERROR);
    }
    
    @RequestMapping("/hello/themaleaf")
    public String themaleaf(Model model) {
        model.addAttribute("name", "Joshua");
        return "hello";
    }
    
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
    	User user = userService.getById(1);
        return Result.success(user);
    }
    
    
    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
    	userService.tx();
        return Result.success(true);
    }
    
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
//    	User  user  = redisService.get(UserKey.getById, ""+1, User.class);
        User  user  = redisClusterService.get(UserKey.getById, ""+1, User.class);
        return Result.success(user);
    }
    
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
    	User user  = new User();
    	user.setId(1);
    	user.setName("1111");
//    	redisService.set(UserKey.getById, ""+1, user);//UserKey:id1
        redisClusterService.set(UserKey.getById, ""+1, user);//UserKey:id1
        return Result.success(true);
    }
    
    
}
