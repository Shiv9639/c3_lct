package Email;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.gte;

public class sendemail {

    public void send(String task) throws EmailException, MessagingException, EmailException {
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
      //  mail.setHtmlMsg(writeDataLineByLine("OutboundException").toString());
        mail.send();
        //System.out.println("message sent successfully....");

    }
    private StringBuffer writeDataLineByLine(String po_number,String reasonCode, String reason_desc) {
        String last_time_stamp="";
        MongoClient mongo = null;
        mongo = MongoClients.create("mongodb://localhost:27017/C3");
        MongoCollection<Document> reasoncode_collection = mongo.getDatabase("C3")
                .getCollection("ReasonCodes");

        MongoTemplate mongoTemplate = new MongoTemplate(mongo, "C3");
       // FindIterable<Document> reasonCodes = reasoncode_collection.find();
        FindIterable<Document> reasonCodes = reasoncode_collection.find().sort(new BasicDBObject("timeStamp", -1));

        StringBuffer email = new StringBuffer();
        email.append("<html><body>" + "<table >");

        for (Document findLatestTimeStamp : reasonCodes) {
            last_time_stamp+=(String)findLatestTimeStamp.get("timeStamp");
            break;
        }
        int r33=0;
        int r34=0;
        int r35=0;
        int r37=0;
        int sum=0;
        for (Document d : reasonCodes) {
            if (d.get("reasonCode").equals("33"))
            {
                r33++;
            }
            if(d.get("reasonCode").equals("34")){r34++;}
            if(d.get("reasonCode").equals("35")){r35++;}
            if(d.get("reasonCode").equals("37")){r37++;}
        }
        sum=r33+r34+r35+r37;


        Bson filter = gte("reasonCodes.timeStamp",last_time_stamp.trim());
        if(filter==null){
            for (Document d : reasonCodes) {
                if (d.get("reasonCode").equals("33") || d.get("reasonCode").equals("34") || d.get("reasonCode").equals("35") || d.get("reasonCode").equals("37")) {

                    email.append("<tr style=\"color: blue;\">");
                    email.append("<td>");
                    email.append(d.get("purchaseOrderNumber"));
                    email.append("</td>");
                    email.append("<td>");
                    email.append(d.get("reasonCode"));
                    email.append("</td>");
                    email.append("<td>");
                    email.append(d.get("reasonCodeDescription"));
                    email.append("</td>");
                    email.append(d.get("row_count"));
                    email.append("</td>");
                    email.append("<tr>");
                }
            }
            email.append("</table></body></html>");
        }
else {
            FindIterable<Document> new_data_to_process = reasoncode_collection.find(filter);
            for (Document d : reasonCodes) {
                if(d.get("reasonCode").equals("33") || d.get("reasonCode").equals("34") || d.get("reasonCode").equals("35") || d.get("reasonCode").equals("37")) {
                    email.append("<tr style=\"color: blue;\">");
                    email.append("<td>");
                    email.append(d.get("purchaseOrderNumber"));
                    email.append("</td>");
                    email.append("<td>");
                    email.append(d.get("reasonCode"));
                    email.append("</td>");
                    email.append("<td>");
                    email.append(d.get("reasonCodeDescription"));
                    email.append("</td>");
                    email.append("<tr>");
                }
            }
        }
        email.append("</table></body></html>");
        return email;

    }



}
