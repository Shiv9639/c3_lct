package Email;

import com.mongodb.client.FindIterable;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.bson.Document;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;

public class sendemail {

    public void send(FindIterable<Document> lastFiveDocs, String task) throws EmailException, MessagingException, EmailException {
        String recipients = "shikha.patel@loblaw.ca,vartika.srivastava@loblaw.ca,boopathy.subramaniam@blueyonder.com,Shivam.Sharma@blueyonder.com";
        String[] recipientList = recipients.split(",");
        ArrayList<InternetAddress> addressesTo = new ArrayList<InternetAddress>();
        InternetAddress iternnetAddress[] = new InternetAddress[recipientList.length];
        for (int i = 0; i < recipientList.length; i++) {
            iternnetAddress[i] = new InternetAddress(recipientList[i].trim().toLowerCase());
        }
        for (int j = 0; j < iternnetAddress.length; j++) {
            addressesTo.add(iternnetAddress[j]);
        }

        HtmlEmail mail = new HtmlEmail();
        mail.setHostName("lclkrsmtp1.gslbint.ngco.com");
        mail.setSmtpPort(587);
        mail.setFrom("donotreply@loblaw.ca", "Loblaw SCH Monitoring");
        // mail.addTo("shikha.patel@loblaw.ca", "shikha.patel@loblaw.ca","To");
        mail.setTo(addressesTo);
        if(task.equals("OutboundException")) {
            mail.setSubject("[Alert from SCH-PT] Outbound Message Flow Exception");
        }else if(task.equals("QueueException")){
            mail.setSubject("[Alert from SCH-PT] Message Queue");
        }
        else if(task.equals("sendLastTwelveRuns")) {
            mail.setSubject("[Alert from SCH-PT] Files Posted to LCT");
        }
        mail.setMsg("Test.....");
        //mail.setHtmlMsg(writeDataLineByLine(lastFiveDocs,"OutboundException").toString());
        mail.send();
        //System.out.println("message sent successfully....");

    }

    private StringBuffer writeDataLineByLine(FindIterable<Document> lastFiveDocs, String task) {
        StringBuffer email = new StringBuffer();
        email.append("<html><body>" + "<table >");
        if(task.equals("QueueException")) {
            email.append("<p style=\"color:blue;\">Message Queue Log</p>");}
        email.append("<tr >");
        email.append("<th>");
        email.append("Date");
        email.append("</th>");
        email.append("<th>");
        email.append("Time_Stamp");
        email.append("</th>");
        email.append("<th>");
        email.append("Count");
        email.append("</th>");

        for (Document d : lastFiveDocs) {
            email.append("<tr style=\"color: blue;\">");
            email.append("<td>");
            email.append(d.get("date"));
            email.append("</td>");
            email.append("<td>");
            email.append(d.get("time_stamp"));
            email.append("</td>");
            email.append("<td>");
            if(task.equals("sendLastTwelveRuns")||task.equals("OutboundException")) {
                email.append(d.get("processed_PO"));}

            else {
                email.append(d.get("queue"));
            }
            email.append("</td>");
            email.append("<tr>");
        }

        email.append("</table></body></html>");
        return email;

    }



}
