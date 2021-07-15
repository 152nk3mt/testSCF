package example;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName WXPush
 * @Author cy
 * @Date 2021/6/25 16:19
 * @Description TODO
 * @Version 1.0
 **/

public class WXPush {
    //企业ID
    public static String CORPID = "ww95f35899d2f3a23b";

    //企业应用的id
    //短信
//    public static Integer AGENTID = 1000004;
    //妖火
    public static Integer AGENTID = 1000002;

    //应用的凭证密钥
    //短信
//    public static String CORPSECRET = "EGf9h4gKDYOsDkXAxPVxrvhSeaAPd9ueBjl3IxrqOxw";
    //妖火
    public static String CORPSECRET = "eW96IoSuAGDkKZtBxYVOSrXlKO0you-e-m9khV4amvM";


    //调用接口凭证
    //private static String ACCESS_TOKEN = "3RXyejGuux57pJvHq5ZMl3GvMX_oNq0b6PN3nYwlIcgEvytvN0AuN_OO9cDRW4K-4dXG_moQZf41BnuWC6gnPse4rJcJkNFagU8JFaw2AubTyDKvCHXtWUW08itxjh7NlVOr9PVtibhF05BzI9IECw0uGRqnJtz0rG9Hq_csTmOKcxLShW-xsHEM1bsQRpUh1LUww23p9Hh7LfULIsrgVw";
    private static String ACCESS_TOKEN;

    //get请求
    private String getTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    //post请求
    private String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    //发送之前调用一次，用于更新凭证，或者校验参数
    private boolean httpGetAccessToken() {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("corpid", CORPID);
        paramMap.put("corpsecret", CORPSECRET);
        String resp = HttpUtil.get(getTokenUrl, paramMap);
        JSONObject respJson = JSONUtil.parseObj(resp);
        if (respJson.getInt("errcode") == 0) {
            setAccessToken(respJson.getStr("access_token"));
            return true;
        }
        return false;
    }

    /**
     *
     * @param map 一个key占一行
     * @return
     */
    public boolean sendText(Map<String,Object> map, String touser,String toparty) {
        httpGetAccessToken();
        JSONObject text = setContent(map);
        String resp = HttpUtil.post(sendUrl + "?access_token=" + ACCESS_TOKEN, oprateParam(touser,toparty, text));
        JSONObject respJson = JSONUtil.parseObj(resp);
        if (respJson.getInt("errcode") != 0) {
           return false;
        }
        return true;
    }

    /**
     * 用于组装发送参数
     * @param touser 发给谁
     * @param text 内容
     * @return 拼装后的参数字符串
     */
    private String oprateParam(String touser,String toparty, Object text) {
        JSONObject param = new JSONObject();
        param.putOnce("msgtype", "text");
        param.putOnce("agentid", AGENTID);
        param.putOnce("text", text);
        if (touser != null)
        param.putOnce("touser", touser);
        if (toparty != null)
        param.putOnce("toparty", toparty);
        return param.toString();
    }

    /**
     * 转换参数：将map转为微信消息类型字符串
     * @param map
     * @return
     */
    private JSONObject setContent(Map<String,Object> map){
        JSONObject jsonObject = new JSONObject();
        StringBuffer bf = new StringBuffer();
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> next = iterator.next();
            bf.append(next.getKey()).append("：").append(next.getValue()).append("\n");
        }
        jsonObject.putOnce("content", bf.toString());
        return jsonObject;
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static String setAccessToken(String accessToken) {
        return ACCESS_TOKEN = accessToken;
    }
}