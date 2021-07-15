package example;

import com.qcloud.scf.runtime.Context;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyRequestEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewMain {
//    public static void main(String[] args) {
    public String mainHandler(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) throws Exception {
        HashMap<String, Object> paramMap = new HashMap<>();
        //Ctrl的cookie
        String cookie = "ASP.NET_SessionId=luyjsg45205ypvzxg5m2tk55; GUID=63ab3a2019514338; sidyaohuo=0C56FF0706DD5A1_957_01544_31130_61001-2";
        String myCookie = "GUID=30e8ee1315085275; _ga=GA1.2.1680205759.1618299025; OUTFOX_SEARCH_USER_ID_NCOO=214860038.96583498; sidyaohuo=052B79C815ECD6E_E65_00328_26430_41001-2-0-0-0-0; ASP.NET_SessionId=vbbiwpurefiv2pnqdbxzj145";

        if (!NewUtils.validCookie(cookie)) {
            System.out.println("cookie已失效");
            paramMap.put("cookie已失效", null);
        }

        List<String> titleUrl = new ArrayList<String>();
        for (int i = 1; i <= 2; i++) {
            String bookListUrl = "https://yaohuo.me/bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=" + i;
            titleUrl.addAll(NewUtils.getTitleUrl(bookListUrl));
        }

        List<Map<String, String>> meatList = new ArrayList<>();

        LocalDateTime minAgo = LocalDateTime.now().plusMinutes(-5).plusHours(8);
        System.out.println("5分钟前时间：" + minAgo.toString());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/M/d H:mm:ss");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter dateFormat3 = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
        for (String url : titleUrl) {
            boolean breakFlag = false;
            boolean isAdd = false;
            Document document = Jsoup.parse(NewUtils.getHttpRequest().setUrl("https://yaohuo.me" + url).execute().body());
            Element contentElement = document.selectFirst("div[class=content]");
            List<Node> nodes = contentElement.childNodes();
            for (int i=0;i<nodes.size();i++) {
                Node childNode = nodes.get(i);
                String string = childNode.toString();
                if (string.contains("[时间]")) {
                    String timeStr = string.replace("[时间] ", "");
                    LocalDateTime time;
                    try {
                        time = LocalDateTime.parse(timeStr, dateFormat3);
                    } catch (Exception exception) {
                        try {
                            time = LocalDateTime.parse(timeStr, dateFormat2);
                        } catch (Exception exception2) {
                            time = LocalDateTime.parse(timeStr, dateFormat);
                        }

                    }

                    if (minAgo.compareTo(time) > 0) {
                        breakFlag = true;
                        break;
                    }
                    System.out.println("帖子时间：" + timeStr);
                    isAdd = true;
                }

            }
            if (breakFlag) break;
            if (isAdd) {
                Map<String, String> bbs = NewUtils.getBbs(document);
                if ("1".equals(bbs.get("isMeat"))) {
                    HashMap<String, String> meatMap = new HashMap<>();
                    meatMap.put("title", bbs.get("title"));
                    meatMap.put("onceMeat", bbs.get("onceMeat"));
                    meatMap.put("url", url);
                    meatList.add(meatMap);
                }

            }
        }

        WXPush wxPush = new WXPush();
        //私信
        System.out.println("拼接消息体");
        NewUtils.validCookie(myCookie);
        int message = NewUtils.getMessage("https://yaohuo.me/");
        if (message > 0) {
            paramMap.put("收到私信",message+ "封");
            paramMap.put("收到私信","<a href=\"https://yaohuo.me/bbs/messagelist.aspx>"+message+ "封</a>");
            wxPush.sendText(paramMap,"CHANGYUAN",null);
            paramMap.clear();
        }
        if (meatList.size() != 0) {
            for (Map<String, String> meatMap : meatList) {
                paramMap.put("········", "");
                paramMap.put("肉贴标题", meatMap.get("title"));
                paramMap.put("每次派肉", meatMap.get("onceMeat"));
                paramMap.put("肉贴链接", "<a href=\"https://yaohuo.me" +meatMap.get("url") + ">点我吃肉</a>");
            }
        }
        if (paramMap.size() > 0) {
            wxPush.sendText(paramMap,"@all","3");
        }
        return "success";
    }
}
