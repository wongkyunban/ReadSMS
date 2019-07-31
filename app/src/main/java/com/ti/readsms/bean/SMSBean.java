package com.ti.readsms.bean;

public class SMSBean {

    private int _id;// 短信序号
    private String thread_id;// 对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
    private String address;// 手机号码
    private String person;// 发件人地址，即手机号，如+8613811810000
    private long date;// 收到日期
    private long date_sent;// 发送日期
    private int read;// read：是否阅读0未读，1已读
    private int status;// status：短信状态-1接收，0complete,64pending,128failed
    private int type;// 短信类型1是接收到的，2是已发出
    private String body;// 短信具体内容
    private int protocol;//协议0 SMS_RPOTO短信，1 MMS_PROTO彩信



    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public long getDate_sent() {
        return date_sent;
    }

    public void setDate_sent(long date_sent) {
        this.date_sent = date_sent;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
