package example;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.scf.runtime.Context;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyRequestEvent;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NewMainMini {
    public String mainHandler(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) throws Exception {
        String cookie = "_ga=GA1.2.1476050461.1565873644; __gads=ID=1199029fe1b1ab69:T=1584598851:S=ALNI_Ma_s9dOP0YVVJZR3ESr3bD7urp7ig; GUID=c1f9b32621285196; sidyaohuo=052B79C815ECD6E_E65_00322_24890_71001-2-0-0-0-0; OUTFOX_SEARCH_USER_ID_NCOO=181236485.58399308; ASP.NET_SessionId=jdb32t55bdn5ig453x3iui55; ___rl__test__cookies=1608364617250";
        String serverKey = "SCU91060T5cb87947822b6ba7d2eb10838ed1f8a85e7acf498ba91";
        NewUtils newUtils = new NewUtils();
        HttpRequest httpRequest = new HttpRequest("https://yaohuo.me/").method(Method.GET)
                .header("cache-control", "max-age=0")
                .header(Header.HOST, "yaohuo.me")
                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header(Header.ACCEPT_ENCODING, "gzip, deflate")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,en-US;q=0.9");
        httpRequest.header(Header.USER_AGENT, getRandomUserAgent());
        newUtils.setHttpRequest(httpRequest);

        if (!newUtils.validCookie(cookie)) {
            System.out.println("cookie：" + cookie);
            //HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火推送&desp=cookie失效");
            throw new Exception("cookie无效！");
        }

        //拼接消息体
        StringBuilder descSb = new StringBuilder(URLEncoder.encode("<br>## 私信</br>"));
        descSb.append("%0D%0A%0D%0A");
        //私信
        int message = newUtils.getMessage("https://yaohuo.me/");
        descSb.append(URLEncoder.encode("<br>收到私信" + message + "封 </br>"));
        descSb.append("%0D%0A%0D%0A");
        descSb.append(URLEncoder.encode(String.valueOf(new Random().nextFloat())));
        if (message != 0) {
            HttpUtil.get("https://sc.ftqq.com/" + serverKey + ".send?text=妖火推送&desp=" + descSb.toString());
        }
        return "success";
    }

    private static String getRandomUserAgent() {
        List<String> ua = Arrays.asList("Mozilla/5.0 (Windows NT 6.2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1464.0 Safari/537.36",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.3319.102 Safari/537.36",
                "Mozilla/5.0 (X11; CrOS i686 3912.101.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.93 Safari/537.36",
                "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36",
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/20100101 Firefox/17.0.6",
                "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3 Safari/537.36",
                "Mozilla/5.0 (X11; CrOS i686 3912.101.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
        return ua.get(new Random().nextInt());
    }
}
