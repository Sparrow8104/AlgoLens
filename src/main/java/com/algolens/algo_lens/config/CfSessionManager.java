package com.algolens.algo_lens.config;

import com.algolens.algo_lens.exception.CfAuthException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CfSessionManager {

    @Value("${codeforces.handle}")
    private String handle;

    @Value("${codeforces.password}")
    private String password;

    private final Map<String,String> cookies = new ConcurrentHashMap<>();
    private LocalDateTime lastLogin;

    private final Map<Long,String> codeCache=new ConcurrentHashMap<>();

    public Map<String,String> getCookies(){
        if (cookies.isEmpty()||lastLogin==null
                || lastLogin.isBefore(LocalDateTime.now().minusHours(6))) {
            login();
        }
        return cookies;
    }


    public void invalidateSession() {
        cookies.clear();
        lastLogin = null;
    }

    public boolean hasCodeCached(Long submissionId){
        return codeCache.containsKey(submissionId);
    }


    public String getCachedCode(Long submissionId) {
        return codeCache.get(submissionId);
    }

    public void cacheCode(Long submissionId, String code) {
        codeCache.put(submissionId, code);
    }

    private void login(){
        try{
            Connection.Response loginPage = Jsoup.connect(
                            "https://codeforces.com/enter")
                    .userAgent("Mozilla/5.0 (compatible; AlgoLens/1.0)")
                    .method(Connection.Method.GET)
                    .execute();

            Map<String,String> initialCookies = loginPage.cookies();
            String csrf=loginPage.parse()
                    .select("input[name=csrf_token]")
                    .attr("value");
            if(csrf==null||csrf.isEmpty()){
                throw new CfAuthException(
                        "Failed to extract CSRF token from Codeforces login page"
                );
            }

            Connection.Response loginResponse = Jsoup.connect(
                    "https://codeforces.com/enter")
                    .userAgent("Mozilla/5.0 (compatible; AlgoLens/1.0)")
                    .data("handleOrEmail",handle)
                    .data("password",password)
                    .data("csrf_token",csrf)
                    .data("action","enter")
                    .cookies(loginPage.cookies())
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .execute();

            if(loginResponse.url().toString().contains("/enter")){
                throw new CfAuthException(
                        "Codeforces login failed-check credentials"
                );
            }

            cookies.clear();
            cookies.putAll(initialCookies);
            cookies.putAll(loginResponse.cookies());
            if(!cookies.containsKey("JSESSIONID")){
                throw new CfAuthException(
                        "Codeforces login succeeded but no session cookie received"
                );
            }

            lastLogin = LocalDateTime.now();

            System.out.println("Codeforces login succeeded for "+lastLogin);

        }catch (IOException e){
          throw new CfAuthException(
                  "Failed to connect to Codeforces:"+e.getMessage()
          );
        }

    }
}
