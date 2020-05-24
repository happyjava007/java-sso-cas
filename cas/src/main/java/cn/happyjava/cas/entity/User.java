package cn.happyjava.cas.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("users")
public class User {

    Integer id;
    String username;
    String password;

}
