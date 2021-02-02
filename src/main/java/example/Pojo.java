package example;

public class Pojo {
    //监听的词，逗号隔开
    String chars;

    String cookie;

    //server酱
    String serverKey;

    public Pojo(String chars, String cookie, String serverKey) {
        this.chars = chars;
        this.cookie = cookie;
        this.serverKey = serverKey;
    }

    public Pojo() {
    }

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getServerKey() {
        return serverKey;
    }

    public void setServerKey(String serverKey) {
        this.serverKey = serverKey;
    }
}
