package cn.happyjava.sso.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    private final static String CAS_URL = "http://cas.com:8080";

    @Value("${localServerUrl}")
    private String localServerUrl;

    @GetMapping(value = "/")
    public String index(HttpServletRequest request, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null || "".equals(username)) {
            return String.format("redirect:%s/?site=%s", CAS_URL, localServerUrl);
        }
        request.setAttribute("username", username);
        return "index";
    }

    @GetMapping(value = "/login")
    public String login(String st, HttpServletRequest request) throws JsonProcessingException {
        if (st == null || "".equals(st)) {
            throw new IllegalArgumentException();
        }
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.postForObject(CAS_URL + "/auth?st=" + st, null, String.class);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.readValue(s, ObjectNode.class);
        boolean success = result.get("result").asBoolean();
        if (!success) {
            throw new RuntimeException("st异常");
        }
        String username = result.get("username").asText();
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        return "index";
    }

}
