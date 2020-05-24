# java-sso-cas
Java sso cas 单点登录系统
## 系统说明
业务系统通过 http://localhost:8080/?site=siteUrl 的形式接入。
CAS系统会通过 siteUrl/login?st={token} 的形式让浏览器进行重定向，业务系统需要处理浏览器的这个请求，获取st，携带st到CAS校验正确性，并且获得userId。`

业务系统st校验地址：
POST http://localhost:800/auth?st=token
响应：
```
{
    "result":true,
    "userId":1,
    "username":"Happyjava"
}
```
