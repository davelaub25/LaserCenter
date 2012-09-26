/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lasersched;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * @author Dave
 */
public class Email {
    
    private static final String SMTP_HOST_NAME = "mail.fmidm.com";
    private static final String SMTP_PORT = "25"; // default port is  465 !
    private static final String SMTP_AUTH_USER = "jabberwock";
    private static final String SMTP_AUTH_PWD = "!shunThefrumiousBandersnatch";
    //private static final String SMTP_AUTH_USER = "info";
    //private static final String SMTP_AUTH_PWD = "!T3chM0nk3y";
    /*
    private static final String SMTP_HOST_NAME = "mail.davelaub.com";
    private static final String SMTP_PORT = "26"; // default port is  465 !
    private static final String SMTP_AUTH_USER = "dlaub+davelaub.com";
    private static final String SMTP_AUTH_PWD = "admin";
    */
    //public static final String SMTP_FROM_ADDRESS = System.getProperty("user.name") + "@fmidm.com";
    public static final String SMTP_FROM_ADDRESS = "jabberwock@fmidm.com";
    public void sendMail(String email, String subject, String text) throws Exception {
        String[] emailList = new String[1];
        emailList[0] = email;
        sendMail(emailList, subject, text);
    }
    public void sendMail(String[] emailList, String subject, String text) throws Exception {
        boolean debug = true;

        Properties props = new Properties();

        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.user", SMTP_AUTH_USER);
        props.put("mail.smtp.password", SMTP_AUTH_PWD);
        props.put("mail.smtp.port", SMTP_PORT );
        //props.put("mail.smtp.ssl.enable", "true" );

        Authenticator auth = new  SMTPAuthenticator();

        Session session = Session.getDefaultInstance(props, auth);

        session.setDebug(debug);

        Message msg = new MimeMessage(session);

        InternetAddress addressFrom = new InternetAddress(SMTP_FROM_ADDRESS);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[emailList.length];

        for (int i = 0; i < emailList.length; i++)
        {
            addressTo[i] = new InternetAddress(emailList[i]);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        msg.setSubject(subject);
        msg.setContent(text, "text/plain");

        Transport.send(msg);

    }

    private class SMTPAuthenticator extends javax.mail.Authenticator
    {
        @Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;
            return new PasswordAuthentication(username, password);
        }
    }
}
