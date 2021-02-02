package example;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName YaoHuo
 * @Author T480
 * @Date 2020/8/24 15:51
 * @Description TODO
 * @Version 1.0
 **/
public class YaoHuoUtils {
    //获得当前页的帖子标题URL
    public static List<String> getTitleUrl(String bookListUrl) {
        ArrayList<String> titleUrlList = new ArrayList<String>();
        String bookListContent = HttpUtil.get(bookListUrl);
        Document document = Jsoup.parse(bookListContent);
        //Elements elesLine1 = document.getElementsByTag("div[class^=line]");
        Elements elesLine1 = document.getElementsByClass("line1");
        elesLine1.addAll(document.getElementsByClass("line2"));
        for (Element element : elesLine1) {
            Element eleTagA = element.selectFirst("a[href]");
            titleUrlList.add(eleTagA.attr("href"));
            //获得标题
            //String text = eleTagA.text();
        }
        return titleUrlList;
    }

    public static String broUrl(String url, String cookies) {
        String content = HttpRequest.get("https://yaohuo.me/" + url)
                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate, br")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9")
                .header(Header.CONNECTION, "keep-alive")
                .header("Sec_Fetch_Dest", "document")
                .header("Sec_Fetch_Mode", "navigate")
                .header("Sec_Fetch_Site", "same_origin")
                .header("Sec_Fetch_User", "same_origin")
                .header("Upgrade_Insecure_Requests", "?1")
                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
                .header(Header.COOKIE, cookies)
                .execute().body();
        /*Elements titles = Jsoup.parse(content).getElementsByClass("content");
        if (titles != null && titles.size() != 0) {
            Element element = titles.get(0);
            //标题
            String titleStr = "";
            for (Node node : element.childNodes()) {
                String tempTitle = node.outerHtml();
                if (tempTitle.contains("礼金")) {
                    titleStr += "【这是一个肉贴！！！】";
                }
                if (tempTitle.contains("标题")) {
                    titleStr = tempTitle + titleStr;
                    break;
                }
            }
            System.out.println("浏览帖子：" + titleStr);
            System.out.println("浏览帖子URl：" + url);
        }*/
        System.out.println("浏览帖子URl：" + url);
        return content;
    }

//    //通过帖子内容获得form表单的字段
//    public static HashMap<String, Object> getFormParam(String content) {
//        HashMap<String, Object> mapParam = new HashMap<String, Object>();
//        Elements element = Jsoup.parse(content).selectFirst("form:eq(0)").getAllElements();
//        List<Node> formChildNodes = element.get(0).childNodes();
//        for (Node formChildNode : formChildNodes) {
//            String name = formChildNode.attr("name");
//            if (name != null && !"".equals(name)) {
//                String value = formChildNode.attr("value");
//                mapParam.put(name, value);
//            }
//        }
//        return mapParam;
//    }

//    //回复功能
//    public static String reply(String content, String bbsUrl, String cookies) {
//        HashMap<String, Object> formParam = getFormParam(broUrl(bbsUrl, cookies));
//        formParam.put("content", content);
//        String body = HttpRequest.post("https://yaohuo.me/bbs/book_re.aspx")
//                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .header(Header.ACCEPT_ENCODING, "gzip, deflate, br")
//                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9")
//                .header(Header.CONNECTION, "keep-alive")
//                .header("Sec_Fetch_Dest", "document")
//                .header("Sec_Fetch_Mode", "navigate")
//                .header("Sec_Fetch_Site", "same_origin")
//                .header("Sec_Fetch_User", "same_origin")
//                .header("Upgrade_Insecure_Requests", "?1")
//                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
//                .header(Header.COOKIE, cookies)
//                .form(formParam)
//                .execute().body();
//        System.out.println(body);
//        return body;
//    }
    //获得其他楼层的回复信息
//    public static ArrayList<String> copyOtherReply(String bbsUrl,  String cookies){
//        ArrayList<String> replays = new ArrayList<String>();
//        String content = HttpRequest.get("https://yaohuo.me/" + bbsUrl)
//                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                .header(Header.ACCEPT_ENCODING, "gzip, deflate, br")
//                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9")
//                .header(Header.CONNECTION, "keep-alive")
//                .header("Sec_Fetch_Dest", "document")
//                .header("Sec_Fetch_Mode", "navigate")
//                .header("Sec_Fetch_Site", "same_origin")
//                .header("Sec_Fetch_User", "same_origin")
//                .header("Upgrade_Insecure_Requests", "?1")
//                .header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36")
//                .header(Header.COOKIE, cookies)
//                .execute().body();
//        Elements moreLists = Jsoup.parse(content).getElementsByClass("more");
//        if(moreLists == null || moreLists.size() == 0){
//            throw new RuntimeException("未找到更多标签");
//        }
//        Node node = moreLists.first().childNode(0);
//        System.out.println(node.attr("href"));
//        String moreHtml = node.childNode(0).outerHtml();
//        System.out.println(moreHtml.substring(5,moreHtml.length()-1));
//
//
//        return replays;
//    }
}

