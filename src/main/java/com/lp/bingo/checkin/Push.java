package com.lp.bingo.checkin;

import com.alibaba.fastjson.JSON;
import com.lp.bingo.utils.HttpClientUtils;
import com.lp.bingo.utils.Result;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class Push {


    HttpClient httpClient =  null;;
    PostMethod postMethod = null;
    // 登陆 Url
    final String loginUrl = "http://itsp.orientsec.com.cn/dfzq-sign/login.do?xcase=doLogin";

    StringBuffer tmpcookies = new StringBuffer();

    int statusCode;



    public Push(){


        // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式

        httpClient = new HttpClient();

        httpClient.getParams().setContentCharset("UTF-8");

        postMethod = new PostMethod(loginUrl);

        // 设置登陆时要求的信息，用户名和密码
        NameValuePair[] data = {new NameValuePair("userName", "lipeng5"), new NameValuePair("password", "li0329gg"), new NameValuePair("saveCookies", "1")};
        postMethod.setRequestBody(data);

        // 获得登陆后的 Cookie
        Cookie[] cookies = httpClient.getState().getCookies();

        for (Cookie c : cookies) {
            tmpcookies.append(c.toString() + ";");
            System.out.println("cookies = " + c.toString());
        }
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 签到
    @Scheduled(cron = "")
    public void checkIn() {


        if (checkDate()) return;

        // 需登陆后访问的 Url
        String dataUrl = "http://itsp.orientsec.com.cn/dfzq-sign/sign.do?xcase=doSign";
            try {

                if (statusCode == 302) {//重定向到新的URL
                    System.out.println("模拟登录成功");
                    // 进行登陆后的操作
                    PostMethod postMethod1 = new PostMethod(dataUrl);
                    // 每次访问需授权的网址时需带上前面的 cookie 作为通行证
                    postMethod1.setRequestHeader("cookie", tmpcookies.toString());
                    // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
                    // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
                    postMethod1.setRequestHeader("Referer", "http://itsp.orientsec.com.cn/dfzq-sign/sign.do");
                    postMethod1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
                    Random random = new Random();
                    int i = random.nextInt(9);
                    NameValuePair[] data1 = {new NameValuePair("optionCode", "1"), new NameValuePair("signTime", "08:3" + i + ":00"), new NameValuePair("signDesc", "")};
                    postMethod1.setRequestBody(data1);
                    int i1 = httpClient.executeMethod(postMethod1);
                    System.out.println(i1);
                    // 打印出返回数据，检验一下是否成功
                    String text = postMethod1.getResponseBodyAsString();
                    System.out.println(text);
                } else {
                    System.out.println("登录失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    // 工作日时间获取
    public boolean checkDate(){
        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        Map<String,String> map = new HashMap<>();
        map.put("date",now.format(dtf));
        String s = null;
        try {
            s = HttpClientUtils.simpleGetInvoke("http://api.goseek.cn/Tools/holiday", map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Result result = (Result) JSON.parseObject(s,Result.class);

        if(result.getStatus() != 0) {
            return true;
        }
        return false;
    }



    // 写周报
    public  void checkLog(){

        if (checkDate()) return;

        // 需登陆后访问的 Url
        String dataUrl = "http://itsp.orientsec.com.cn/dfzq-sign/sign.do?xcase=doKqLog";

        try{
            if(statusCode==302){//重定向到新的URL
                System.out.println("模拟登录成功");
                // 进行登陆后的操作
                PostMethod postMethod1 = new PostMethod(dataUrl);
                // 每次访问需授权的网址时需带上前面的 cookie 作为通行证
                postMethod1.setRequestHeader("cookie", tmpcookies.toString());
                // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
                // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
                postMethod1.setRequestHeader("Referer", "http://itsp.orientsec.com.cn/dfzq-sign/sign.do?xcase=forwardLog");
                postMethod1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
                Random random = new Random();
                int i = random.nextInt(9);
                NameValuePair[] data1 = { new NameValuePair("kqLog.uid", "lipeng5"), new NameValuePair("kqLog.userName", "李鹏5"),
                        new NameValuePair("kqLog.lid", "DFZQ_SIGN_20190429162423066"),
                        new NameValuePair("kqLog.logWeek", "2019-04-29~2019-05-03"),
                        new NameValuePair("kqLog.logContent1", "沪深行情数据文件落地测试"),
                        new NameValuePair("kqLog.logContent2", "沪深行情数据文件落地测试"),
                        new NameValuePair("kqLog.logContent3", "沪深行情数据文件落地测试"),
                        new NameValuePair("kqLog.logContent4", "沪深行情数据文件落地测试"),
                        new NameValuePair("kqLog.logContent5", "沪深行情数据文件落地测试")};
                postMethod1.setRequestBody(data1);
                int i1 = httpClient.executeMethod(postMethod1);
                System.out.println(i1);
                // 打印出返回数据，检验一下是否成功
                String text = postMethod1.getResponseBodyAsString();
                System.out.println(text);
            }
            else {
                System.out.println("登录失败");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    // 签退
    public  void checkOut() throws IOException, URISyntaxException {

        if (checkDate()) return;

        // 需登陆后访问的 Url
        String dataUrl = "http://itsp.orientsec.com.cn/dfzq-sign/sign.do?xcase=doSign";
        try {
            if(statusCode==302){//重定向到新的URL
                System.out.println("模拟登录成功");
                // 进行登陆后的操作
                PostMethod postMethod1 = new PostMethod(dataUrl);
                // 每次访问需授权的网址时需带上前面的 cookie 作为通行证
                postMethod1.setRequestHeader("cookie", tmpcookies.toString());
                //postMethod1.setRequestHeader("X-Requested-With:","XMLHttpRequest");
                // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
                // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
                postMethod1.setRequestHeader("Referer", "http://itsp.orientsec.com.cn/dfzq-sign/sign.do?xcase=signIndex");
                postMethod1.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
                Random random = new Random();
                int i = random.nextInt(9);
                NameValuePair[] data1 = { new NameValuePair("optionCode", "2"), new NameValuePair("signTime", "17:40:08"),
                        new NameValuePair("signDesc", "")};
                postMethod1.setRequestBody(data1);
                int i1 = httpClient.executeMethod(postMethod1);
                System.out.println(i1);
                // 打印出返回数据，检验一下是否成功
                String text = postMethod1.getResponseBodyAsString();
                System.out.println(text);
            }
            else {
                System.out.println("登录失败");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws URISyntaxException, IOException {



       /* DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate now = LocalDate.now();
        System.out.println( now.format(dtf));


        Map<String,String> map = new HashMap<>();
        map.put("date",now.format(dtf));
        String s = HttpClientUtils.simpleGetInvoke("http://api.goseek.cn/Tools/holiday", map);
        System.out.println(s);

        Result result = (Result) JSON.parseObject(s,Result.class);

        System.out.println(result.toString());*/

        new Push().checkOut();
    }



}
