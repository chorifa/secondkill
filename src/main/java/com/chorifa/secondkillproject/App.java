package com.chorifa.secondkillproject;

import com.chorifa.secondkillproject.dao.UserDataObjectMapper;
import com.chorifa.secondkillproject.dataobject.UserDataObject;
import org.mybatis.spring.annotation.MapperScan;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.SpringApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */

@SpringBootApplication(scanBasePackages = {"com.chorifa.secondkillproject"})
@RestController
// 注意在这里设置了扫描dao下所有的mapper接口，等同的可以在所有的mapper接口上添加@Mapper注解
@MapperScan("com.chorifa.secondkillproject.dao")
public class App 
{
    @Autowired
    private UserDataObjectMapper userDataObjectMapper;

    @RequestMapping("/")
    public String sayHello() {
        // 生成1w个用户数据
        /*
        UserDataObject userDataObject = new UserDataObject();
        userDataObject.setAge(20);
        userDataObject.setGender((byte)1);
        userDataObject.setRegisterMode("byphone");
        for(int i = 1000; i < 10000; i++){
            userDataObject.setId(null);
            userDataObject.setName("user:"+i);
            userDataObject.setPhone("13861" + (100000 + i));
            userDataObjectMapper.insertSelective(userDataObject);
            if(i%100 == 0)System.out.print(i+"    ");
            if(i%1000 == 0) System.out.println();
        }
        System.out.println("done");
         */
        return "done";
    }

    public static void main( String[] args ){
        SpringApplication.run(App.class, args);
        System.out.println( "Hello World!" );
        /*
        try{
            Path path = Paths.get("D:\\user_info.txt");
            try(FileWriter fileWriter = new FileWriter(path.toFile());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)){
                for(int i = 0; i < 10000; i++)
                    bufferedWriter.write(i+"\r\n");
                bufferedWriter.flush();
            }
        }catch (IOException e){
            System.out.println("写入文件出错");
        }
        System.out.println("done");
         */
    }
}
