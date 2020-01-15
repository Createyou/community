package com.jn.demo.provider;

import com.alibaba.fastjson.JSON;
import com.jn.demo.dto.AccesssTokenDTO;
import com.jn.demo.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccesssTokenDTO accesssTokenDTO)
    {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accesssTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
               String string=response.body().string();
            String token = string.split("&")[0].split("=")[1];
            //System.out.println(string);
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token="+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string=response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);//Json对象自动转换为java类对象
            return githubUser;
        } catch (IOException e) {
        }
        return null;

    }

}
