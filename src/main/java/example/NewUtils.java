package example;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewUtils {

    private static HttpRequest httpRequest;


    /**
     * 校验cookie是否有效
     *
     * @param cookie
     * @return
     */
    public static boolean validCookie(String cookie) {
        //访问首页 如果class=top包含登录 ，则cookie无效
        System.out.println("校验cookie");
        setCookie(cookie);
        getHttpRequest();
        httpRequest.setUrl("https://yaohuo.me?random=" + new Random().nextFloat());
        String body = httpRequest.execute().body();
        Document document = Jsoup.parse(body);
        Element element = document.selectFirst("div[class^=top]");
        return !((element == null || element.toString().contains("登录")));
    }

    /**
     * 监听关键词
     *
     * @param keyWords
     * @param bbsContent
     * @return
     */
    public static String keyWord(String keyWords, String bbsContent) {
        String[] keyWordArr = keyWords.split(",");
        StringBuilder sbKeyWord = new StringBuilder();
        for (String keyWord : keyWordArr) {
            if (bbsContent.contains(keyWords))
                sbKeyWord.append(keyWord).append(",");
        }
        return sbKeyWord.toString();
    }

    /**
     * 获取URL的帖子内容
     */
    public static Map<String, String> getBbs(String url) {
        System.out.println("获取URL的帖子内容");
        httpRequest.setUrl(url);
        getHttpRequest();
        HashMap<String, String> bbsMap = new HashMap<String, String>();
        Document document = Jsoup.parse(httpRequest.execute().body());
        Element contentElement = document.selectFirst("div[class=content]");
        bbsMap.put("all", contentElement.toString());
        for (Node childNode : contentElement.childNodes()) {
            String string = childNode.toString();
            if (string.contains("[标题]")) {
                bbsMap.put("title", string);
            }
            if (string.contains("礼金：")) {
                bbsMap.put("isMeat", "1");
            }
            if (string.contains("每人每日一次派礼：")) {
                bbsMap.put("onceMeat", string.replace("每人每日一次派礼：", ""));
            }
        }
        Element bbsContentElement = document.selectFirst("div[class=bbscontent]");
        try {
            bbsMap.put("content", bbsContentElement.toString());
        } catch (Exception e) {
            bbsMap.put("content", "未获得");
        }

        return bbsMap;
    }
    /**
     * 获取URL的帖子内容
     */
    public static Map<String, String> getBbs(Document document) {
        HashMap<String, String> bbsMap = new HashMap<String, String>();
        Element contentElement = document.selectFirst("div[class=content]");
        bbsMap.put("all", contentElement.toString());
        for (Node childNode : contentElement.childNodes()) {
            String string = childNode.toString();
            if (string.contains("[标题]")) {
                bbsMap.put("title", string);
            }
            if (string.contains("礼金：") && !string.contains("余0")) {
                bbsMap.put("isMeat", "1");
            }
            if (string.contains("每人每日一次派礼：")) {
                bbsMap.put("onceMeat", string.replace("每人每日一次派礼：", ""));
            }
        }
        Element bbsContentElement = document.selectFirst("div[class=bbscontent]");
        try {
            bbsMap.put("content", bbsContentElement.toString());
        } catch (Exception e) {
            bbsMap.put("content", "未获得");
        }

        return bbsMap;
    }
    /**
     * 获得私信个数
     *
     * @param url
     * @return
     */
    public static int getMessage(String url) {
        getHttpRequest();
        int messageSize = 0;
        NewUtils.httpRequest.setUrl(url);
        Document document = Jsoup.parse(NewUtils.httpRequest.execute().body());
        Element messageEle = document.selectFirst("a");
        if (messageEle == null) return messageSize;
        String href = messageEle.attr("href");
        if (href.contains("messagelist")) {
            String string = messageEle.childNode(1).toString();
            Pattern pattern = Pattern.compile("\\d");
            Matcher matcher = pattern.matcher(string);
            if (matcher.find()) {
                messageSize = Integer.valueOf(matcher.group());
            }
        }
        return messageSize;
    }

    /**
     * 获得随机URL
     *
     * @return
     */
    public static String getBbsUrl(int min) {
        System.out.println("获得随机URL");
        List<String> titleUrlNew = new ArrayList<String>();
        List<String> titleUrl = new ArrayList<String>();
        for (int i = 0; i < 2; i++) {
            String bookListUrl = "https://yaohuo.me/bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=" + i;
            titleUrl.addAll(getTitleUrl(bookListUrl));
        }

        LocalDateTime minAgo = LocalDateTime.now().plusMinutes(-min);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss");
        for (String url : titleUrl) {
            httpRequest.setUrl("https://yaohuo.me" + url);
            Document document = Jsoup.parse(httpRequest.execute().body());
            Element contentElement = document.selectFirst("div[class=content]");
            for (Node childNode : contentElement.childNodes()) {
                String string = childNode.toString();
                if (string.contains("[时间]")) {
                    String timeStr = string.replace("[时间] ", "");
                    LocalDateTime time = LocalDateTime.parse(timeStr, dateFormat);
                    if (minAgo.compareTo(time) > 0) {
                        break;
                    }
                    titleUrlNew.add(url);
                }
            }
        }
        Random random = new Random();
        int nextInt = random.nextInt(titleUrl.size());
        return titleUrl.get(nextInt);
    }

    //获得当前页的帖子标题URL
    public static List<String> getTitleUrl(String bookListUrl) {
        ArrayList<String> titleUrlList = new ArrayList<String>();
        String bookListContent = new HttpRequest(bookListUrl).method(Method.GET).header(Header.USER_AGENT, getRandomAgent()).timeout(3000).execute().body();
        //String bookListContent = HttpUtil.get(bookListUrl);
        Document document = Jsoup.parse(bookListContent);
        Elements elesLine = document.select("div[class^=line]");
        for (Element element : elesLine) {
            Element eleTagA = element.selectFirst("a[href]");
            titleUrlList.add(eleTagA.attr("href"));
        }
        return titleUrlList;
    }


    public static void setCookie(String cookie) {
        System.out.println("设置cookie");
        httpRequest.removeHeader(Header.COOKIE);
        httpRequest.header(Header.COOKIE, cookie);
    }

    public static String getRandomAgent() {
        return RandomUtil.randomEle(agentList);
    }

    private static List<String> agentList = CollUtil.newArrayList();

    static {
        agentList.add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        agentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36");
        agentList.add("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
        agentList.add("Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.6; Windows NT 6.1; Trident/5.0; InfoPath.2; SLCC1; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 2.0.50727) 3gpp-gba UNTRUSTED/1.0");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 7.0; InfoPath.3; .NET CLR 3.1.40767; Trident/6.0; en-IN)");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)");
        agentList.add("Mozilla/5.0 (compatible; MSIE 10.0; Macintosh; Intel Mac OS X 10_7_3; Trident/6.0)");
        agentList.add("Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/5.0)");
        agentList.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/532.2 (KHTML, like Gecko) ChromePlus/4.0.222.3 Chrome/4.0.222.3 Safari/532.2");
        agentList.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 ChromePlus/4.0.222.3 Chrome/4.0.222.3 Safari/525.28.3");
        agentList.add("Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16");
        agentList.add("Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14");
        agentList.add("Mozilla/5.0 (Windows NT 6.0; rv:2.0) Gecko/20100101 Firefox/4.0 Opera 12.14");
        agentList.add("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0) Opera 12.14");
        agentList.add("Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02");
        agentList.add("Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00");
        agentList.add("Opera/9.80 (Windows NT 5.1; U; zh-sg) Presto/2.9.181 Version/12.00");
        agentList.add("Opera/12.0(Windows NT 5.2;U;en)Presto/22.9.168 Version/12.00");
        agentList.add("Opera/12.0(Windows NT 5.1;U;en)Presto/22.9.168 Version/12.00");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
        agentList.add("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0");
        agentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10; rv:33.0) Gecko/20100101 Firefox/33.0");
        agentList.add("Mozilla/5.0 (X11; Linux i586; rv:31.0) Gecko/20100101 Firefox/31.0");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20130401 Firefox/31.0");
        agentList.add("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
        agentList.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.13 Safari/537.36");
        agentList.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3756.400 QQBrowser/10.5.4043.400");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1");
        agentList.add("Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6");
        agentList.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6");
        agentList.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1");
        agentList.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5");
        agentList.add("Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3");
        agentList.add("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3");
        agentList.add("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24");
        agentList.add("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24");

        httpRequest = new HttpRequest("https://yaohuo.me/").method(Method.GET)
                .header("cache-control", "max-age=0")
                .header(Header.HOST, "yaohuo.me")
                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,en-US;q=0.9");
    }

    public static HttpRequest getHttpRequest() {
        httpRequest.removeHeader(Header.USER_AGENT);
        httpRequest.header(Header.USER_AGENT,getRandomAgent());
        return httpRequest;
    }

    public static void setHttpRequest(HttpRequest httpRequest) {
        NewUtils.httpRequest = httpRequest;
    }
}
