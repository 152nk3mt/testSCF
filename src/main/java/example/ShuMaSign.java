//package example;
//
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//
///**
// * @ClassName ShuMaSign
// * @Author T480
// * @Date 2020/9/28 11:28
// * @Description TODO
// * @Version 1.0
// **/
//public class ShuMaSign {
//    private static class innerWebClient {
//        private static final WebClient webClient = new WebClient();
//    }
//
//    /**
//     * 获取指定网页实体
//     *
//     * @param url
//     * @return
//     */
//    public static HtmlPage getHtmlPage(String url) {
//        //调用此方法时加载WebClient
//        WebClient webClient = innerWebClient.webClient;
//        // 取消 JS 支持
//        //  webClient.setJavaScriptEnabled(false);
//        // 取消 CSS 支持
//        //  webClient.setCssEnabled(false);
//        HtmlPage page = null;
//        try {
//            // 获取指定网页实体
//            page = (HtmlPage) webClient.getPage(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return page;
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        // 获取指定网页实体
//        HtmlPage page = getHtmlPage("https://www.mydigit.cn/plugin.php?id=k_misign:sign");
//        System.out.println(page.asText());  //asText()是以文本格式显示
////
////        // 获取搜索按钮
////        HtmlInput btn = page.getHtmlElementById("su");
////        // “点击” 搜索
////        HtmlPage page2 = btn.click();
////        System.out.println(page2.asText());  //asText()是以文本格式显示
//    }
//}
//
//
//
