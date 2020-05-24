package cn.happyjava.cas.controller;

import cn.happyjava.cas.entity.User;
import cn.happyjava.cas.mapper.UsersMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class IndexController {

    /**
     * TODO 临时解决方案，真实项目请替换掉。eg:redis、过期时间、过期清理等
     */
    private static final Map<String, Integer> ST_MAP = new ConcurrentHashMap<>();

    private final UsersMapper usersMapper;

    public IndexController(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @RequestMapping(value = {"/"})
    public String index(HttpServletRequest request, HttpSession session) {
        String site = request.getParameter("site");
        if (site == null) {
            site = "";
        }
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            // 1、登录了
            String token = UUID.randomUUID().toString();
            ST_MAP.put(token, userId);
            if ("".equals(site)) {
                // TODO：不考虑找不到user
                User user = usersMapper.selectById(userId);
                request.setAttribute("username", user.getUsername());
                return "index";
            } else {
                return "redirect:" + site + "/login?st=" + token;
            }
        } else {
            // 2、未登录。重定向到登录界面
            request.setAttribute("site", site);
            return "login";
        }
    }

    @PostMapping(value = " ")
    public String login(String username, String password, String site,
                        HttpServletRequest request, HttpSession session) {
        if (username == null || password == null) {
            throw new IllegalArgumentException();
        }
        List<User> users = usersMapper.selectByMap(new HashMap<>() {{
            put("username", username);
            put("password", password);
        }});
        if (users == null || users.size() == 0) {
            return "passworderror";
        }
        User user = users.get(0);
        session.setAttribute("userId", user.getId());
        String st = UUID.randomUUID().toString();
        ST_MAP.put(st, user.getId());
        if (site == null || site.equals("")) {
            request.setAttribute("username", user.getUsername());
            return "redirect:/";
        } else {
            return String.format("redirect:%s/login?st=%s", site, st);
        }
    }

    @PostMapping(value = "/auth")
    @ResponseBody
    public Object auth(String st) {
        if (st == null || "".equals(st)) {
            throw new IllegalArgumentException();
        }
        Integer userId = ST_MAP.get(st);
        if (userId == null) {
            return new HashMap<String, Object>() {{
                put("result", false);
                put("errorMsg", "st异常");
            }};
        }
        User user = usersMapper.selectById(userId);
        ST_MAP.remove(st);
        return new HashMap<String, Object>() {{
            put("result", true);
            put("userId", userId);
            put("username", user.getUsername());
        }};
    }

}
