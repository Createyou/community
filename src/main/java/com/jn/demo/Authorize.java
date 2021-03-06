package com.jn.demo;

import com.jn.demo.dto.AccesssTokenDTO;
import com.jn.demo.dto.GithubUser;
import com.jn.demo.mapper.UserMapper;
import com.jn.demo.model.User;
import com.jn.demo.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class Authorize {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public  String callback(@RequestParam(name = "code") String code,
                            @RequestParam(name="state") String state,
                            HttpServletResponse response){
        AccesssTokenDTO accesssTokenDTO = new AccesssTokenDTO();
        accesssTokenDTO.setClient_id(clientId);
        accesssTokenDTO.setClient_secret(clientSecret);
        accesssTokenDTO.setCode(code);
        accesssTokenDTO.setRedirect_uri(redirectUri );
        accesssTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accesssTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if(githubUser!=null){
            User user=new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtcreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtcreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";



        }
        else{
            return "redirect:/";
            //登陆失败 ,重新登陆
        }


    }

}
