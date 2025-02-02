package ml.shiwei.teamproject.controller;

import io.jsonwebtoken.Claims;
import ml.shiwei.teamproject.entity.Post;
import ml.shiwei.teamproject.service.PostService;
import ml.shiwei.teamproject.utils.identicon.Result;
import ml.shiwei.teamproject.utils.identicon.ResultCode;
import ml.shiwei.teamproject.utils.posting.UploadUtils;
import ml.shiwei.teamproject.utils.token.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class PostController {

    @Autowired
    private PostService postService;

    //接收前端返回Post实例，初始化其余参数并存储进数据库
    @RequestMapping("/posting")
    public Result posting(@RequestBody Post post,@RequestHeader("token") String string){
        //解析Token获取用户id
        Claims claims= JwtUtils.checkToken(string);
        long userId= (int) claims.get("id");
        //初始化Post并存储
        postService.publish(post,userId);
        return new Result(ResultCode.Success);
    }

    //接收上传的图片并存储
    @PostMapping("/posting/upload")
    public Result uploadImage(@RequestParam(value = "file") MultipartFile multipartFile){
        if (multipartFile.isEmpty()){
            return new Result(ResultCode.File_Empty);
        }
        return new Result(ResultCode.Success,UploadUtils.uploadImage(multipartFile));
    }

    //根据饭堂返回相应帖子
    @RequestMapping("/post/list")
    public Result list(@RequestBody String canteen){
        List<Map<String,String>> list=postService.getList(canteen);
        if(list!=null){
            list.forEach(System.out::println);
            return new Result(ResultCode.Success,list);
        }
        return new Result(ResultCode.PostEmptyOrEnterError);
    }
}
