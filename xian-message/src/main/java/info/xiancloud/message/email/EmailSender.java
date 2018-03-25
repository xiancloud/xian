package info.xiancloud.message.email;

import com.sun.mail.util.MailSSLSocketFactory;
import info.xiancloud.core.conf.XianConfig;
import info.xiancloud.core.util.ArrayUtil;
import info.xiancloud.core.util.LOG;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailSender {

    private static String account;//登录用户名
    private static String pass;        //登录密码
    private static String from;        //发件地址
    private static String host;        //服务器地址
    private static String port;        //端口
    private static String protocol; //协议

    static {
        account = XianConfig.get("email.account");
        pass = XianConfig.get("email.pass");
        from = XianConfig.get("email.from");
        host = XianConfig.get("email.host");
        port = XianConfig.get("email.port");
        protocol = XianConfig.get("email.protocol");
    }

    //用户名密码验证，需要实现抽象类Authenticator的抽象方法PasswordAuthentication
    static class MyAuthenricator extends Authenticator {
        String u = null;
        String p = null;

        public MyAuthenricator(String u, String p) {
            this.u = u;
            this.p = p;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(u, p);
        }
    }

    private List<String> tos;
    private String content;
    private String subject;

    public EmailSender(String tos, String subject, String content) {
        this.tos = ArrayUtil.toList(tos.split(","), String.class);
        this.content = content;
        this.subject = subject;
    }

    public EmailSender(List<String> tos, String subject, String content) {
        this.tos = tos;
        this.content = content;
        this.subject = subject;
    }

    public EmailSender(String[] tos, String subject, String content) {
        this.tos = ArrayUtil.toList(tos, String.class);
        this.content = content;
        this.subject = subject;
    }

    public void send() {
        Properties prop = new Properties();
        prop.setProperty("mail.transport.protocol", protocol);
        prop.setProperty("mail.smtp.host", host);
        prop.setProperty("mail.smtp.port", port);
        prop.setProperty("mail.smtp.auth", "true");//使用smtp身份验证
        //使用SSL，企业邮箱必需！
        //开启安全协议
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e1) {
            LOG.error(e1);
        }
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);
        Session session = Session.getDefaultInstance(prop, new MyAuthenricator(account, pass));
        session.setDebug(true);
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(from, "xian"));
            for (String to : tos) {
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            }
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(new Date());
            mimeMessage.setText(content);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }

}
