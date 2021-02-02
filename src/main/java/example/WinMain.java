package example;

import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WinMain {
    public static void main(String[] args) throws Exception {
        String cookie = "ASP.NET_SessionId=luyjsg45205ypvzxg5m2tk55; GUID=63ab3a2019514338; sidyaohuo=0C56FF0706DD5A1_957_01544_31130_61001-2";
        String serverKey = "SCU137350T8dac6b4a5a2ba2eec6bb4b74901dbffa5fdf363727230";
        String keyWords = "手环";

        String cookieMy = "_ga=GA1.2.1476050461.1565873644; __gads=ID=1199029fe1b1ab69:T=1584598851:S=ALNI_Ma_s9dOP0YVVJZR3ESr3bD7urp7ig; GUID=c1f9b32621285196; sidyaohuo=052B79C815ECD6E_E65_00322_24890_71001-2-0-0-0-0; OUTFOX_SEARCH_USER_ID_NCOO=181236485.58399308; ASP.NET_SessionId=jdb32t55bdn5ig453x3iui55; ___rl__test__cookies=1608364617250";
        String serverKeyMy = "SCU91060T5cb87947822b6ba7d2eb10838ed1f8a85e7acf498ba91";
        String keyWordsMy = "哈罗,电费";
        //冯
        String serverKey30762 = "SCU138774T593a86ae32e36d4dfb14f810fbbc3acb5fe467b208f8d";

        if (!NewUtils.validCookie(cookieMy)) {
             throw new Exception("cookie无效！");
        }

        List<String> titleUrl = new ArrayList<String>();
        for (int i = 1; i <= 1; i++) {
            String bookListUrl = "https://yaohuo.me/bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=" + i;
            titleUrl.addAll(NewUtils.getTitleUrl(bookListUrl));
        }

        List<Map<String, String>> meatList = new ArrayList<>();
        List<Map<String, String>> keyWordList = new ArrayList<>();
        List<Map<String, String>> keyWordListMy = new ArrayList<>();

        LocalDateTime minAgo = LocalDateTime.now().plusMinutes(-5);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        DateTimeFormatter dateFormat2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        for (String url : titleUrl) {
            boolean isAdd = false;
            Document document = Jsoup.parse(NewUtils.getHttpRequest().setUrl("https://yaohuo.me" + url).execute().body());
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
                    break;
                }
            }
            if (isAdd) {
                Map<String, String> bbs = NewUtils.getBbs(document);
                if ("1".equals(bbs.get("isMeat"))) {
                    HashMap<String, String> meatMap = new HashMap<>();
                    meatMap.put("title", bbs.get("title"));
                    meatMap.put("onceMeat", bbs.get("onceMeat"));
                    meatMap.put("url", url);
                    meatList.add(meatMap);
                }

                //关键字
                if (!StringUtil.isBlank(keyWords)) {
                    String containsKey = NewUtils.keyWord(keyWords, bbs.get("all"));
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
                    String containsKey = NewUtils.keyWord(keyWordsMy, bbs.get("all"));
                    if (!StringUtil.isBlank(containsKey)) {
                        HashMap<String, String> keyWordMap = new HashMap<>();
                        keyWordMap.put("keyWord", containsKey);
                        keyWordMap.put("title", bbs.get("title"));
                        keyWordMap.put("url", url);
                        keyWordListMy.add(keyWordMap);
                    }
                }
            }
        }

        //拼接消息体
        StringBuilder descSb = new StringBuilder(URLEncoder.encode("**私信**", "UTF8"));
        descSb.append("%0D%0A%0D%0A");
        StringBuilder descSbMy2 = new StringBuilder();
        descSbMy2.append("%0D%0A%0D%0A");

        StringBuilder descSbMeat = new StringBuilder();
        descSbMeat.append("%0D%0A%0D%0A");

        //私信
        int message = NewUtils.getMessage("https://yaohuo.me/");
        descSb.append(URLEncoder.encode("收到私信["+ message + "](https://yaohuo.me/bbs/messagelist.aspx)封", "UTF8")+" %0D%0A%0D%0A ");
        descSb.append("%0D%0A%0D%0A");
        if (meatList.size() != 0) {
            descSb.append(URLEncoder.encode("** 肉贴 **", "UTF8") + " %0D%0A%0D%0A ");
            descSbMy2.append(URLEncoder.encode("** 肉贴 **", "UTF8") + " %0D%0A%0D%0A ");
            for (Map<String, String> meatMap : meatList) {
                descSb.append(URLEncoder.encode("每次派肉：" + meatMap.get("onceMeat"), "UTF8") + " %0D%0A%0D%0A ");
                descSb.append(URLEncoder.encode("标题：" + meatMap.get("title"), "UTF8") + " %0D%0A%0D%0A ");
                descSb.append(URLEncoder.encode("链接：["+ meatMap.get("url")+"](https://yaohuo.me" + meatMap.get("url") +")", "UTF8") + " %0D%0A%0D%0A ");
                descSb.append("%0D%0A%0D%0A%0D%0A%0D%0A");

                descSbMy2.append(URLEncoder.encode("每次派肉：" + meatMap.get("onceMeat"), "UTF8") + " %0D%0A%0D%0A ");
                descSbMy2.append(URLEncoder.encode("标题：" + meatMap.get("title"), "UTF8") + " %0D%0A%0D%0A ");
                descSbMy2.append(URLEncoder.encode("链接：["+ meatMap.get("url")+"](https://yaohuo.me" + meatMap.get("url") +")", "UTF8") + " %0D%0A%0D%0A ");
                descSbMy2.append("%0D%0A%0D%0A%0D%0A%0D%0A");

                descSbMeat.append(URLEncoder.encode("每次派肉：" + meatMap.get("onceMeat"), "UTF8") + " %0D%0A%0D%0A ");
                descSbMeat.append(URLEncoder.encode("标题：" + meatMap.get("title"), "UTF8") + " %0D%0A%0D%0A ");
                descSbMeat.append(URLEncoder.encode("链接：["+ meatMap.get("url")+"](https://yaohuo.me" + meatMap.get("url") +")", "UTF8") + " %0D%0A%0D%0A ");
                descSbMeat.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            }
        }

        if (keyWordList.size() != 0) {
            descSb.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            descSb.append(URLEncoder.encode("** 关键字帖子 **","UTF8")+" %0D%0A%0D%0A ");
            descSb.append("%0D%0A%0D%0A");
            for (Map<String, String> keywordMap : keyWordList) {
                descSb.append(URLEncoder.encode("关键字：" + keywordMap.get("keyWord"), "UTF8") + "%0D%0A%0D%0A");
                descSb.append("%0D%0A%0D%0A");
                descSb.append(URLEncoder.encode("标题：" + keywordMap.get("title"), "UTF8") + " %0D%0A%0D%0A ");
                descSb.append(URLEncoder.encode("链接：["+ keywordMap.get("url")+"](https://yaohuo.me" + keywordMap.get("url") +")", "UTF8") + " %0D%0A%0D%0A ");
                descSb.append("%0D%0A%0D%0A");
            }
        }

        if (keyWordListMy.size() != 0) {
            descSbMy2.append("%0D%0A%0D%0A%0D%0A%0D%0A");
            descSbMy2.append(URLEncoder.encode("** 关键字帖子 **", "UTF8"));
            descSbMy2.append("%0D%0A%0D%0A");
            for (Map<String, String> keywordMap : keyWordListMy) {
                descSbMy2.append(URLEncoder.encode("关键字：" + keywordMap.get("keyWord"), "UTF8") + "%0D%0A%0D%0A");
                descSbMy2.append("%0D%0A%0D%0A");
                descSbMy2.append(URLEncoder.encode("标题：" + keywordMap.get("title"), "UTF8") + " %0D%0A%0D%0A ");
                descSbMy2.append(URLEncoder.encode("链接：["+ keywordMap.get("url")+"](https://yaohuo.me" + keywordMap.get("url") +")", "UTF8") + " %0D%0A%0D%0A ");
                descSbMy2.append("%0D%0A%0D%0A");
            }
        }


        System.out.println("准备Ctrl发送消息：");
        descSb.append(URLEncoder.encode(String.valueOf(new Random().nextFloat()), "UTF8"));
        if (message != 0 || meatList.size() != 0 || keyWordList.size() != 0) {
            System.out.println("发送Ctrl消息：");
            HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火推送&desp=" + descSb.toString());
        }

        System.out.println("准备发送我的消息");
        StringBuilder descSbMy1 = new StringBuilder();
        NewUtils.setCookie(cookieMy);
        int messageMy = NewUtils.getMessage("https://yaohuo.me/");
        descSbMy1.append(URLEncoder.encode("收到私信["+ messageMy + "封](https://yaohuo.me/bbs/messagelist.aspx)", "UTF8")+" %0D%0A%0D%0A ");
        descSbMy1.append("%0D%0A%0D%0A");

        if (messageMy != 0 || meatList.size() != 0 || keyWordListMy.size() != 0) {
            System.out.println("发送我的消息：");
            HttpUtil.get("https://sc.ftqq.com/" + serverKeyMy + ".send?text=妖火推送&desp=" + descSbMy1.toString() + descSbMy2.toString() + new Random().nextFloat());
        }

        if (meatList.size() != 0){
            HttpUtil.get("https://sc.ftqq.com/" + serverKey30762 + ".send?text=妖火推送&desp=" + descSbMeat.toString() + descSbMy2.toString() + new Random().nextFloat());
        }
        return;
    }
}
