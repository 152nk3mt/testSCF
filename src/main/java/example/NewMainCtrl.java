package example;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.scf.runtime.Context;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyRequestEvent;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NewMainCtrl {
    public String mainHandler(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) throws Exception {
        String cookie = "ASP.NET_SessionId=luyjsg45205ypvzxg5m2tk55; GUID=63ab3a2019514338; sidyaohuo=0C56FF0706DD5A1_957_01544_31130_61001-2";
        String serverKey = "SCU137350T8dac6b4a5a2ba2eec6bb4b74901dbffa5fdf363727230";
        String keyWords = "手环";

        String serverKeyMy = "SCU91060T5cb87947822b6ba7d2eb10838ed1f8a85e7acf498ba91";
        String keyWordsMy = "电费,哈罗";

        NewUtils newUtils = new NewUtils();
        HttpRequest httpRequest = new HttpRequest("https://yaohuo.me/").method(Method.GET)
                .header("cache-control", "max-age=0")
                .header(Header.HOST, "yaohuo.me")
                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,en-US;q=0.9")
                .header(Header.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 10; zh-cn; MI 9 Build/QKQ1.190825.002) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/71.0.3578.141 Mobile Safari/537.36 XiaoMi/MiuiBrowser/13.3.2");
        newUtils.setHttpRequest(httpRequest);
        System.out.println("关键字：" + keyWords);
        if (!newUtils.validCookie(cookie)) {
            System.out.println("cookie：" + cookie);
            //HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火推送&desp=cookie失效");
            throw new Exception("cookie无效！");
        }

        System.out.println("获得titleUrlNew");
        List<String> titleUrlNew = new ArrayList<String>();
        List<String> titleUrl = new ArrayList<String>();
        for (int i = 1; i <= 2; i++) {
            String bookListUrl = "https://yaohuo.me/bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=" + i;
            titleUrl.addAll(newUtils.getTitleUrl(bookListUrl));
        }

        System.out.println("minAgo");
        LocalDateTime minAgo = LocalDateTime.now().plusMinutes(-5);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (String url : titleUrl) {
            boolean isAdd = false;
            httpRequest.setUrl("https://yaohuo.me" + url);
            Document document = Jsoup.parse(httpRequest.execute().body());
            Element contentElement = document.selectFirst("div[class=content]");
            for (Node childNode : contentElement.childNodes()) {
                String string = childNode.toString();
                if (string.contains("[时间]")) {
                    String timeStr = string.replace("[时间] ", "");
                    LocalDateTime time;
                    try {
                        time = LocalDateTime.parse(timeStr, dateFormat);
                    } catch (Exception exception) {
                        time = LocalDateTime.parse(timeStr, dateFormat2);
                    }

                    if (minAgo.compareTo(time) > 0) {
                        break;
                    }
                    isAdd = true;
                }
            }
            if (isAdd)
                titleUrlNew.add("https://yaohuo.me" + url);
        }

        System.out.println("循环titleUrlNew");
        List<Map<String, String>> meatList = new ArrayList<>();
        List<Map<String, String>> keyWordList = new ArrayList<>();
        List<Map<String, String>> keyWordListMy = new ArrayList<>();
        for (String url : titleUrlNew) {
            //肉贴
            Map<String, String> bbs = newUtils.getBbs(url);
            if ("1".equals(bbs.get("isMeat"))) {
                HashMap<String, String> meatMap = new HashMap<>();
                meatMap.put("title", bbs.get("title"));
                meatMap.put("onceMeat", bbs.get("onceMeat"));
                meatMap.put("url", url);
                meatList.add(meatMap);
            }

            //关键字
            if (!StringUtil.isBlank(keyWords)) {
                String containsKey = newUtils.keyWord(keyWords, bbs.get("all"));
                if (!StringUtil.isBlank(containsKey)) {
                    HashMap<String, String> keyWordMap = new HashMap<>();
                    keyWordMap.put("keyWord", containsKey);
                    keyWordMap.put("title", bbs.get("title"));
                    keyWordMap.put("url", url);
                    keyWordList.add(keyWordMap);
                }
            }
            //关键字
            if (!StringUtil.isBlank(keyWordsMy)) {
                String containsKey = newUtils.keyWord(keyWordsMy, bbs.get("all"));
                if (!StringUtil.isBlank(containsKey)) {
                    HashMap<String, String> keyWordMap = new HashMap<>();
                    keyWordMap.put("keyWord", containsKey);
                    keyWordMap.put("title", bbs.get("title"));
                    keyWordMap.put("url", url);
                    keyWordListMy.add(keyWordMap);
                }
            }
        }

        System.out.println("拼接消息体");
        //拼接消息体
        StringBuilder descSb = new StringBuilder(URLEncoder.encode("<br>## 私信</br>"));
        descSb.append("%0D%0A%0D%0A");

        StringBuilder descSbMy = new StringBuilder(URLEncoder.encode("<br>## 私信</br>"));
        descSbMy.append("%0D%0A%0D%0A");

        //私信
        int message = newUtils.getMessage("https://yaohuo.me/");
        descSb.append(URLEncoder.encode("<br>收到私信" + message + "封 </br>"));
        descSb.append("%0D%0A%0D%0A");
        if (meatList.size() != 0) {
            descSb.append(URLEncoder.encode("<br>## 肉贴</br>"));
            descSb.append("%0D%0A%0D%0A");

            descSbMy.append(URLEncoder.encode("<br>## 肉贴</br>"));
            descSbMy.append("%0D%0A%0D%0A");

            for (Map<String, String> meatMap : meatList) {
                descSb.append(URLEncoder.encode("<br>每次派肉：" + meatMap.get("onceMeat") + "</br>"));
                descSb.append("%0D%0A%0D%0A");
                descSb.append(URLEncoder.encode("<br>标题：" + meatMap.get("title") + "</br>"));
                descSb.append("%0D%0A%0D%0A");
                descSb.append(URLEncoder.encode("<br>链接：" + meatMap.get("url") + "</br>"));
                descSb.append("%0D%0A%0D%0A%0D%0A%0D%0A");

                descSbMy.append(URLEncoder.encode("<br>每次派肉：" + meatMap.get("onceMeat") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A");
                descSbMy.append(URLEncoder.encode("<br>标题：" + meatMap.get("title") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A");
                descSbMy.append(URLEncoder.encode("<br>链接：" + meatMap.get("url") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            }
        }

        if (keyWordList.size() != 0) {
            descSb.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            descSb.append(URLEncoder.encode("<br>## 关键字帖子</br>"));
            descSb.append("%0D%0A%0D%0A");
            for (Map<String, String> keywordMap : keyWordList) {
                descSb.append(URLEncoder.encode("<br>关键字：" + keywordMap.get("keyWord") + "</br>"));
                descSb.append("%0D%0A%0D%0A");
                descSb.append(URLEncoder.encode("<br>标题：" + keywordMap.get("title") + "</br>"));
                descSb.append("%0D%0A%0D%0A");
                descSb.append(URLEncoder.encode("<br>链接：" + keywordMap.get("url") + "</br>"));
                descSb.append("%0D%0A%0D%0A");
            }
        }

        if (keyWordListMy.size() != 0) {
            descSbMy.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            descSbMy.append(URLEncoder.encode("<br>## 关键字帖子</br>"));
            descSbMy.append("%0D%0A%0D%0A");
            for (Map<String, String> keywordMap : keyWordListMy) {
                descSbMy.append(URLEncoder.encode("<br>关键字：" + keywordMap.get("keyWord") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A");
                descSbMy.append(URLEncoder.encode("<br>标题：" + keywordMap.get("title") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A");
                descSbMy.append(URLEncoder.encode("<br>链接：" + keywordMap.get("url") + "</br>"));
                descSbMy.append("%0D%0A%0D%0A");
            }
        }


        System.out.println("发送消息：");
        descSb.append(URLEncoder.encode(String.valueOf(new Random().nextFloat())));
        if (message != 0 || meatList.size() != 0 || keyWordList.size() != 0) {
            HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火推送&desp=" + descSb.toString());
        }

        System.out.println("发送我的消息");
        if (meatList.size() != 0 || keyWordListMy.size() != 0) {
            HttpUtil.get("https://sc.ftqq.com/" + serverKeyMy + ".send?text=妖火推送&desp=" + descSb.toString());
        }
        return "success";
    }
}
