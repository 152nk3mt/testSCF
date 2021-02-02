//package example;
//
//import cn.hutool.http.HttpUtil;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//public class Yaohuo {
//    //public static void main(String[] args) {
////    public String mainHandler(Pojo pojo) {
////        //server酱的key
////        //String cookie = "ASP.NET_SessionId=3icnaw451cpflhmm54apy155; GUID=619e042508481476; _ga=GA1.2.1860482968.1598316549; _gid=GA1.2.1632253940.1598316549; _gat_gtag_UA_88858350_1=1; sidyaohuo=0C56FF0706DD5A1_957_01540_36430_31001-2; __gads=ID=fa09b183522b8453-22ff859420c300f6:T=1598316548:RT=1598316548:S=ALNI_MZ-ppin_RZ3Zt1-WpEEBj4_8s---g";
////        String serverKey = "SCU91060T5cb87947822b6ba7d2eb10838ed1f8a85e7acf498ba91";
////        String cookie = pojo.getMessage();
////        if (null == cookie || "".equals(cookie)) {
////            System.out.println("cookie 为空！");
////            throw new RuntimeException("cookie 不能为空！");
////        }
////        List<String> titleUrl = new ArrayList<String>();
////        for (int i = 0; i < 2; i++) {
////            String bookListUrl = "https://yaohuo.me/bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=" + i;
////            titleUrl.addAll(YaoHuoUtils.getTitleUrl(bookListUrl));
////        }
////        Random random = new Random();
////        int nextInt = random.nextInt(titleUrl.size());
////        //String url = "/bbs-830769.html";
////        String url = titleUrl.get(nextInt);
////        String tieContent = YaoHuoUtils.broUrl(url, cookie);
////        //String tieContent = YaoHuoUtils.reply("水自己帖子",url, cookie);
//////        YaoHuoUtils.copyOtherReply(url, cookie);
////        if (tieContent.contains("<div class=\"subtitle\">温馨提示：</div><div class=\"tip\">请先登录网站！")) {
////            if (!"".equals(serverKey)) {
////                HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火cookie挂掉啦~");
////            }
////            throw new RuntimeException("cookie失效！" + cookie);
////        }
////
////        return "success";
////    }
//    public String mainHandler(Pojo pojo) {
//    //public static void main(String[] args) {
//        String serverKey = "";
//        String bookListUrl = "bbs/book_list.aspx?action=new&siteid=1000&classid=0&getTotal=2020&page=";
//        String cookie = pojo.getMessage();
//        StringBuffer sb = new StringBuffer();
//        ArrayList<String> titleUrlList = new ArrayList<String>();
//        for (int i = 0; i < 2; i++) {
//            sb.append(YaoHuoUtils.broUrl(bookListUrl + i, cookie));
//        }
//        Document document = Jsoup.parse(sb.toString());
//        Elements elesLine1 = document.getElementsByClass("line1");
//        //elesLine1.addAll(document.getElementsByClass("line2"));
//        for (Element element : elesLine1) {
//            titleUrlList.add(element.selectFirst("a[href]").attr("href"));
//        }
//        int nextInt = new Random().nextInt(15);
//        String tieContent = YaoHuoUtils.broUrl(titleUrlList.get(nextInt), cookie);
//        if (tieContent.contains("<div class=\"subtitle\">温馨提示：</div><div class=\"tip\">请先登录网站！")) {
//            if (!"".equals(serverKey)) {
//                HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火cookie挂掉啦~");
//            }
//            throw new RuntimeException("cookie失效！" + cookie);
//        }
//        return "success";
//    }
//
//}