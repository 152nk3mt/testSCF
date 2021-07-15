package example;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qcloud.scf.runtime.Context;
import com.qcloud.services.scf.runtime.events.APIGatewayProxyRequestEvent;
import org.jsoup.internal.StringUtil;

import java.util.HashMap;

/**
 * @ClassName RoeWe
 * @Author cy
 * @Date 2021/6/29 8:37
 * @Description 查询荣威车架号
 * @Version 1.0
 **/
public class RoeWe {
    public String mainHandler(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
//    public static void main(String[] args) {
        String url = "https://mp.ebanma.com/app-mp/vowner/1.0/weakBindCar";
        HashMap<String, Object> param = new HashMap<>();
        param.put("token","f55f3ec0fa334250afc5e3c985ab8a4c-prod_SAIC");
        param.put("brandCode","1");
        param.put("vin","LSJW74069W2088977");
        param.put("engineNumber","10152");
        String resp = HttpUtil.get(url, param);
        System.out.println("返回值：" + resp);
        WXPush wxPush = new WXPush();
        HashMap<String, Object> paramMap = new HashMap<>();
        if (!StringUtil.isBlank(resp)) {
            JSONObject respJson = JSONUtil.parseObj(resp);
            if (!"51015".equals(respJson.getJSONObject("data").getStr("rtnCode"))){
                paramMap.put("返回内容",respJson);
                wxPush.sendText(paramMap,"CHANGYUAN",null);
            }
        } else {
            paramMap.put("查询状态失败",null);
            wxPush.sendText(paramMap,"CHANGYUAN",null);
        }
        return "success";
    }
}
