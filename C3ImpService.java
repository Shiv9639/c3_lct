package com.C3Collection.C3.Service;
import java.lang.String;
import com.C3Collection.C3.Model.C3Master;
import com.C3Collection.C3.Model.LpvToken;
import com.C3Collection.C3.Model.R9333LpvPoInterface;
import com.C3Collection.C3.Model.ReasonCodeMaster;
import com.C3Collection.C3.Repository.C3MasterRepo;
import com.C3Collection.C3.Repository.DatFileReaderRepo;
import com.C3Collection.C3.Repository.R9333LpvPoInterfaceRepo;
import com.C3Collection.C3.Repository.ReasonCodeInterface;
import com.C3Collection.C3.constants.LpvConstants;
import com.opencsv.CSVWriter;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class C3ImpService {
    private String fileName;
    @Autowired
    private ReasonCodeInterface reasonCodeMaster;
    @Autowired
    private C3MasterRepo repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private R9333LpvPoInterfaceRepo r9333LpvPoInterfaceRepo;
    @Autowired
    private DatFileReaderRepo datFileReaderRepo;
    private final String CONTENT_TYPE = "application/csv";
    private final String SENDER = "LOBLAWS.INC";
    private final String RECEIVERS = "LCT.GLOBAL";
    private final String BULK_FORMAT = "CSV";
    private final String ENTITY_VERSION = "BY-2020.1.0";
    private final String MODEL = "native";
    private static final SimpleDateFormat DATETIMEFORMATTER = new SimpleDateFormat("YYYYMMDDHHMMSSsss");
    private static final SimpleDateFormat LPVDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    // private static final String LPV_DELIVERY_END_POINT_VAIRABLE = System.getenv("BY_LPV_PO_DELIVERY_API_URL");
    //private static final String LPV_PO_END_POINT_VAIRABLE = System.getenv("BY_LPV_PO_API_URL");

    private static final String LPV_DELIVERY_END_POINT_VAIRABLE = "https://loblawtestinglaz.jdadelivers.com/ingestion/api/v1/ingestions?sender=LOBLAWS.INC&receivers=LCT.GLOBAL&entity=purchaseOrderC3Message&bulkFormat=CSV&entityVersion=BY-2020.1.0&model=native";
    private static final String LPV_PO_END_POINT_VAIRABLE = "https://loblawtestinglaz.jdadelivers.com/ingestion/api/v1/ingestions?sender=LOBLAWS.INC&receivers=LCT.GLOBAL&entity=deliveryShipmentC3Message&bulkFormat=CSV&entityVersion=BY-2020.1.0&model=native";

    LpvToken lpvtoken=null;
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    private List<BasicNameValuePair> getLpvParams(String entityName) {
        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("sender", SENDER));
        params.add(new BasicNameValuePair("receivers", RECEIVERS));
        params.add(new BasicNameValuePair("entity", entityName));
        params.add(new BasicNameValuePair("bulkFormat", BULK_FORMAT));
        params.add(new BasicNameValuePair("entityVersion", ENTITY_VERSION));
        params.add(new BasicNameValuePair("model", MODEL));
        // new parameter requested from BY to send CSV file name
        params.add(new BasicNameValuePair("bulkId",getFileName().replace(".csv", "")));
//		LoggingUtilities.generateInfoLog("bulkId :" + getFileName().replace(".csv", ""));
        return params;
    }









    public void postCSVFile(File csvFile, LpvToken token, String url, String entityName) throws Exception {
        try {
            setFileName(csvFile.getName());
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(url.concat("&bulkId="+getFileName().replace(".csv", "")));
            post.setHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
            post.setHeader(HttpHeaders.AUTHORIZATION, token.getToken_type() + " " + token.getAccess_token());
            post.setEntity(new UrlEncodedFormEntity(getLpvParams(entityName)));
            post.setEntity(EntityBuilder.create().setBinary(Files.readAllBytes(csvFile.toPath()))
                    .setContentType(org.apache.http.entity.ContentType.create("application/csv")).build());
            HttpResponse response = client.execute(post);
        } catch (Exception ex) {
            throw ex;
        }
    }


    public void C3DataFiltering() throws Exception {
        String arr[] = {"ZIPR", "ZMAN", "ZITR", "ZORM", "ZCTO", "ZIBM", "ZIBA", "ZIBI", "ZRX", "ZNAR",
                "ZAWP", "ZSDM", "ZAWR", "ZSNL", "ZIBR", "ZCOV", "ZIBN", "ZPRO", "NB" };
        List<C3Master> l = repository.findAll();
        List<String> po = new ArrayList<>();

        System.out.println("Data in format--" +DATETIMEFORMATTER.format(new Date()));

        String SUB_State_Time = "";

        String DO_format = "";

        List<String[]> actual_data = new ArrayList<String[]>();
        int i = 0;
        for (C3Master c3 : l) {

            //flag_check_po=0;
            if (c3 != null) {

                String po_no = c3.getPurchaseOrderNumber();

                    String[] split_po = po_no.split(",");
                    for(String ssss:split_po) {
                        System.out.println("---------PO's are-----------");
                        System.out.println(ssss);
                    }

                if ((split_po.length) > 1) {
                    for (String s1 : split_po) {
                        if ((!c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) ||
                                (!c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door"))

                                || (!c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused"))) {
//                            reasonCodeMaster=new ReasonCodeMaster();
//                            reasonCodeMaster.setPurchaseOrderNumber(s1);
//                            reasonCodeMaster.setReasonCode("36");
//                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                            reasonCodeMaster.setReasonCodeDescription(" Status not relevant for LCT");
//                            reasonCodeMaster.setPoType("ZIPR");
//                            mongoTemplate.save(reasonCodeMaster);
                        }
                    }

                    if (!c3.getPurchaseOrderNumber().trim().equals("") && !c3.getCurrent_WorkflowStateName_ID().trim().equals("") && !c3.getSite_ExternalReference().trim().equals("")) {
                        if ((c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) ||
                                (c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door"))

                                || (c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused"))) {
                            if (c3.getSite_ExternalReference().length() == 1) {
                                String append_O = "0" + c3.getSite_ExternalReference();
                                DO_format = "DO" + append_O;
                            } else if (c3.getSite_ExternalReference().length() == 2) {
                                DO_format = "DO" + c3.getSite_ExternalReference();
                            }


                            String Current_WorkFlow = c3.getCurrent_WorkflowStateName_ID();
                            String d_format = DO_format;
                            HashMap<String,Integer> check_dup_in_same_file= new HashMap<>();
                            for (String s : split_po) {

                                    if(!check_dup_in_same_file.containsKey(s)){
                                        check_dup_in_same_file.put(s,1);
                                    }
                                    else{
                                        check_dup_in_same_file.put(s,check_dup_in_same_file.get(s)+1);
                                    }
                                if(s.trim().length()!=10){
//                                    reasonCodeMaster=new ReasonCodeMaster();
//                                    reasonCodeMaster.setPurchaseOrderNumber(s);
//                                    reasonCodeMaster.setReasonCode("30");
//                                    reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                    reasonCodeMaster.setReasonCodeDescription("Number of digits is not equal to 10");
//                                    reasonCodeMaster.setPoType("ZIPR");
//                                    mongoTemplate.save(reasonCodeMaster);

                                }


                                R9333LpvPoInterface check_if_PO_exists = r9333LpvPoInterfaceRepo.findByPurchaseOrderId(s);
                                if(check_if_PO_exists==null) {
//                                    reasonCodeMaster=new ReasonCodeMaster();
//                                    reasonCodeMaster.setPurchaseOrderNumber(s);
//                                    reasonCodeMaster.setReasonCode("32");
//                                    reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                    reasonCodeMaster.setReasonCodeDescription(" PO not found in SCH collection");
//                                    reasonCodeMaster.setPoType("ZIPR");
//                                    mongoTemplate.save(reasonCodeMaster);

                                }

                                if (check_if_PO_exists != null) {

                                    boolean test
                                            = Arrays.asList(arr)
                                            .contains(check_if_PO_exists.getErpOrderType());

                                    if(test==false){
//                                        reasonCodeMaster=new ReasonCodeMaster();
//                                        reasonCodeMaster.setPurchaseOrderNumber(s);
//                                        reasonCodeMaster.setReasonCode("34");
//                                        reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                        reasonCodeMaster.setReasonCodeDescription("Invalid PO Type");
//                                        reasonCodeMaster.setPoType("ZIPR");
//                                        mongoTemplate.save(reasonCodeMaster);

                                    }

                                    if((!check_if_PO_exists.getIncoTerms1().equals("TMS2")
                                            || !check_if_PO_exists.getIncoTerms1().equals("SDM") || !check_if_PO_exists.getIncoTerms1().equals("")))
                                    {
//                                        reasonCodeMaster=new ReasonCodeMaster();
//                                        reasonCodeMaster.setPurchaseOrderNumber(s);
//                                        reasonCodeMaster.setReasonCode("35");
//                                        reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                        reasonCodeMaster.setReasonCodeDescription("Invalid INCO1");
//                                        reasonCodeMaster.setPoType("ZIPR");
//                                        mongoTemplate.save(reasonCodeMaster);




                                    }
                                    if(!check_if_PO_exists.getProcessIndicator().equals("") || check_if_PO_exists.getProcessIndicator()!=null){
//                                        reasonCodeMaster=new ReasonCodeMaster();
//                                        reasonCodeMaster.setPurchaseOrderNumber(s);
//                                        reasonCodeMaster.setReasonCode("33");
//                                        reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                        reasonCodeMaster.setReasonCodeDescription("PO in SCH collection have PO Close Indicator");
//                                        reasonCodeMaster.setPoType("ZIPR");
//                                        mongoTemplate.save(reasonCodeMaster);
                                    }


                                    if (check_if_PO_exists != null &&
                                            test==true &&

                                            (check_if_PO_exists.getProcessIndicator().equals("") || check_if_PO_exists.getProcessIndicator()==null) && (check_if_PO_exists.getIncoTerms1().equals("TMS2")
                                            || check_if_PO_exists.getIncoTerms1().equals("SDM") || check_if_PO_exists.getIncoTerms1().equals(""))) {
                                        File file = new File("/home/shivang/Documents/LCTFiles/lct_po_C3_" + c3.getPurchaseOrderNumber() +"_"+DATETIMEFORMATTER.format(new Date())+"_"+DATETIMEFORMATTER.format(new Date())+ ".csv");
                                        FileWriter outputfile = new FileWriter(file);
                                        List<String[]> data = new ArrayList<String[]>();
                                        CSVWriter writer = new CSVWriter(outputfile);

                                        if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) {
                                            SUB_State_Time = c3.getCustomDateTime01UTC();

                                            LpvToken lpvtoken=null;
                                            File file_deliveryPO = new File("/home/shivang/Documents/LCTFiles/lct_dl_C3_" + c3.getPurchaseOrderNumber() +"_"+DATETIMEFORMATTER.format(new Date())+"_"+DATETIMEFORMATTER.format(new Date())+ ".csv");
                                            FileWriter outputfile_deliveryPO = new FileWriter(file_deliveryPO);
                                            List<String[]> data_delivery_PO = new ArrayList<String[]>();
                                            CSVWriter writer_to_deliver_PO = new CSVWriter(outputfile_deliveryPO);

                                            data.clear();
                                            data.add(new String[]{"Delivery No", "Delivery Type", "Shipment Type", "Customer", "Supplier", "Operation Name", "ATA", "ATAC3","Transaction Id"});
                                            writer_to_deliver_PO.writeAll(data);

                                            data_delivery_PO.add(new String[]{c3.getPurchaseOrderNumber(), "InboundDelivery", "InboundShipment", "Loblaw", check_if_PO_exists.getSupplierName(), "CreateDelivery", (c3.getCustomDateTime01UTC()), (c3.getCustomDateTime01UTC()),"C3_"+DATETIMEFORMATTER.format(new Date())});
                                            writer_to_deliver_PO.writeAll(data_delivery_PO);
                                            writer_to_deliver_PO.close();
                                         //   postCSVFile(file_deliveryPO,lpvtoken, LPV_DELIVERY_END_POINT_VAIRABLE,"c3collection");
                                            data_delivery_PO.clear();
                                            data.clear();


                                        } else if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door")) {
                                            SUB_State_Time = c3.getCustomDateTime02UTC();
                                        } else if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused")) {
                                            SUB_State_Time = c3.getCustomDateTime04UTC();
                                        }


                                        data.add(new String[]{"Order No", "Order Type", "Process Type", "Customer", "Supplier", "Operation Name", "UDF C3 Sub State", "Sub State Time", "Door Number","Transaction Id"});
                                        writer.writeAll(data);


                                        //System.out.println("if found"+check_if_PO_exists);
                                        //flag_check_po=1;
                                        ///	C3DataComparision c3model=new C3DataComparision();
                                        String po_from_new_data = c3.getPurchaseOrderNumber();
                                        String Current_WorkflowStateName_ID = c3.getCurrent_WorkflowStateName_ID();
                                        String Site_ExternalReference = c3.getSite_ExternalReference();
                                        //String indicator=check_if_PO_exists.getIndicator();
                                        //	System.out.print("from other collection matching.. "+po_from_new_data + Current_WorkflowStateName_ID+ Site_ExternalReference +indicator);
                                        actual_data.add(new String[]{po_from_new_data, "standardPO", "supply", "Loblaw", check_if_PO_exists.getSupplierName(), "CreateOrder", Current_WorkflowStateName_ID, SUB_State_Time, c3.getCustomField05(),"C3_"+DATETIMEFORMATTER.format(new Date())});
                                        writer.writeAll(actual_data);
                                        writer.close();
                                      //  postCSVFile(file,lpvtoken,LPV_PO_END_POINT_VAIRABLE,"c3collection");
                                        actual_data.clear();
                                        //writer.flush();
                                        i++;

                                    }
                                }


                            }


                            for(String check_dup:check_dup_in_same_file.keySet()){
                                if(check_dup_in_same_file.get(check_dup)>1){
//                                    reasonCodeMaster=new ReasonCodeMaster();
//                                    reasonCodeMaster.setPurchaseOrderNumber(check_dup);
//                                    reasonCodeMaster.setReasonCode("31");
//                                    reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                    reasonCodeMaster.setReasonCodeDescription("Duplications within the input file");
//                                    reasonCodeMaster.setPoType("ZIPR");
//                                    mongoTemplate.save(reasonCodeMaster);


                                }
                            }

                        }

                    }


                } else {
                    if (c3 != null) {

                        int len_check_po=c3.getPurchaseOrderNumber().trim().length();

                        if(len_check_po!=10){
//                            reasonCodeMaster=new ReasonCodeMaster();
//                            reasonCodeMaster.setPurchaseOrderNumber(c3.getPurchaseOrderNumber().trim());
//                            reasonCodeMaster.setReasonCode("30");
//                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                            reasonCodeMaster.setReasonCodeDescription("Number of digits is not equal to 10");
//                            reasonCodeMaster.setPoType("ZIPR");
//                            mongoTemplate.save(reasonCodeMaster);
                        }


                        if ((!c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) ||
                                (!c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door"))

                                || (!c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused"))) {
//                            reasonCodeMaster=new ReasonCodeMaster();
//                            reasonCodeMaster.setPurchaseOrderNumber(c3.getPurchaseOrderNumber());
//                            reasonCodeMaster.setReasonCode("36");
//                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                            reasonCodeMaster.setReasonCodeDescription("Status not relevant for LCT");
//                            reasonCodeMaster.setPoType("ZIPR");
//                            mongoTemplate.save(reasonCodeMaster);
                        }


                        if (!c3.getPurchaseOrderNumber().trim().equals("") && !c3.getCurrent_WorkflowStateName_ID().trim().equals("") && !c3.getSite_ExternalReference().trim().equals("")) {
                            if ((c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) || (c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door"))
                                    || (c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused"))) {
                                if (c3.getSite_ExternalReference().length() == 1) {
                                    String append_O = "0" + c3.getSite_ExternalReference();
                                    DO_format = "DO" + append_O;
                                } else if (c3.getSite_ExternalReference().length() == 2) {
                                    DO_format = "DO" + c3.getSite_ExternalReference();
                                }


                                if (!c3.getPurchaseOrderNumber().trim().equals("") && !c3.getCurrent_WorkflowStateName_ID().trim().equals("") && !DO_format.trim().equals("")) {
                                    //	System.out.println("inside if after adding to file");

                                    //R9333LpvPoInterface check_if_PO_exists=r9333LpvPoInterfaceRepo.findBypurchaseOrderId(c3.getPurchaseOrderNumber());
                                    R9333LpvPoInterface check_if_PO_exists = r9333LpvPoInterfaceRepo.findByPurchaseOrderId(c3.getPurchaseOrderNumber());

                                    if(check_if_PO_exists==null) {
//                                        reasonCodeMaster=new ReasonCodeMaster();
//                                    reasonCodeMaster.setPurchaseOrderNumber(c3.getPurchaseOrderNumber());
//                                    reasonCodeMaster.setReasonCode("32");
//                                    reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                    reasonCodeMaster.setReasonCodeDescription(" PO not found in SCH collection");
//                                    reasonCodeMaster.setPoType("ZIPR");
//                                    mongoTemplate.save(reasonCodeMaster);

                                }

                                    if (check_if_PO_exists != null) {


                                        boolean test
                                                = Arrays.asList(arr)
                                                .contains(check_if_PO_exists.getErpOrderType());

                                        if(test==false){
//                                            reasonCodeMaster=new ReasonCodeMaster();
//                                            reasonCodeMaster.setPurchaseOrderNumber(check_if_PO_exists.getPurchaseOrderId());
//                                            reasonCodeMaster.setReasonCode("34");
//                                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                            reasonCodeMaster.setReasonCodeDescription("Invalid PO Type");
//                                            reasonCodeMaster.setPoType("ZIPR");
//                                            mongoTemplate.save(reasonCodeMaster);

                                        }

                                        if((!check_if_PO_exists.getIncoTerms1().equals("TMS2")
                                                || !check_if_PO_exists.getIncoTerms1().equals("SDM") || !check_if_PO_exists.getIncoTerms1().equals("")))
                                        {

//                                            reasonCodeMaster.setPurchaseOrderNumber(check_if_PO_exists.getPurchaseOrderId());
//                                            reasonCodeMaster.setReasonCode("35");
//                                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                            reasonCodeMaster.setReasonCodeDescription("Invalid INCO1");
//                                            reasonCodeMaster.setPoType("ZIPR");
//                                            mongoTemplate.save(reasonCodeMaster);


                                        }

                                        if(!check_if_PO_exists.getProcessIndicator().equals("") || check_if_PO_exists.getProcessIndicator()!=null){
//                                            reasonCodeMaster=new ReasonCodeMaster();
//                                            reasonCodeMaster.setPurchaseOrderNumber(check_if_PO_exists.getPurchaseOrderId());
//                                            reasonCodeMaster.setReasonCode("33");
//                                            reasonCodeMaster.setTimeStamp(LocalDateTime.now());
//                                            reasonCodeMaster.setReasonCodeDescription("PO in SCH collection have PO Close Indicator");
//                                            reasonCodeMaster.setPoType("ZIPR");
//                                            mongoTemplate.save(reasonCodeMaster);
                                        }



                                        if (check_if_PO_exists != null && test==true && (check_if_PO_exists.getProcessIndicator().equals("") || check_if_PO_exists.getProcessIndicator()==null)
                                                && (check_if_PO_exists.getIncoTerms1().equals("TMS2") ||
                                                check_if_PO_exists.getIncoTerms1().equals("SDM") || check_if_PO_exists.getIncoTerms1().equals(""))) {


                                            File file = new File("/home/shivang/Documents/LCTFiles/lct_po_C3_" + c3.getPurchaseOrderNumber() +"_"+DATETIMEFORMATTER.format(new Date())+"_"+DATETIMEFORMATTER.format(new Date())+ ".csv");
                                            FileWriter outputfile = new FileWriter(file);
                                            List<String[]> data = new ArrayList<String[]>();
                                            CSVWriter writer = new CSVWriter(outputfile);

                                            if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("Arrived")) {
                                                SUB_State_Time = c3.getCustomDateTime01UTC();


                                                File file_deliveryPO = new File("/home/shivang/Documents/LCTFiles/lct_dl_C3_" + c3.getPurchaseOrderNumber() +"_"+DATETIMEFORMATTER.format(new Date())+"_"+DATETIMEFORMATTER.format(new Date())+ ".csv");
                                                FileWriter outputfile_deliveryPO = new FileWriter(file_deliveryPO);
                                                List<String[]> data_delivery_PO = new ArrayList<String[]>();
                                                CSVWriter writer_to_deliver_PO = new CSVWriter(outputfile_deliveryPO);

                                                data.clear();
                                                data.add(new String[]{"Delivery No", "Delivery Type", "Shipment Type", "Customer", "Supplier", "Operation Name", "ATA", "ATAC3","Transaction Id"});
                                                writer_to_deliver_PO.writeAll(data);

                                                data_delivery_PO.add(new String[]{c3.getPurchaseOrderNumber(), "InboundDelivery", "InboundShipment", "Loblaw", check_if_PO_exists.getSupplierName(), "CreateDelivery", (c3.getCustomDateTime01UTC()), (c3.getCustomDateTime01UTC()),"C3_"+DATETIMEFORMATTER.format(new Date())});
                                                writer_to_deliver_PO.writeAll(data_delivery_PO);
                                                writer_to_deliver_PO.close();
                                            //    postCSVFile(file_deliveryPO,lpvtoken, LPV_DELIVERY_END_POINT_VAIRABLE,"c3collection");
                                                data_delivery_PO.clear();
                                                data.clear();
                                            } else if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("In Door")) {
                                                SUB_State_Time = c3.getCustomDateTime02UTC();
                                            } else if (c3.getCurrent_WorkflowStateName_ID() != null && c3.getCurrent_WorkflowStateName_ID().trim().equals("Refused")) {
                                                SUB_State_Time = c3.getCustomDateTime04UTC();
                                            }
                                            data.add(new String[]{"Order No", "Order Type", "Process Type", "Customer", "Supplier", "Operation Name", "UDF C3 Sub State", "Sub State Time", "Door Number","Transaction Id"});
                                            writer.writeAll(data);
                                            //System.out.println("if found"+check_if_PO_exists);

                                            String po_from_new_data = c3.getPurchaseOrderNumber();
                                            String Current_WorkflowStateName_ID = c3.getCurrent_WorkflowStateName_ID();
                                            String Site_ExternalReference = c3.getSite_ExternalReference();
                                            //String indicator=check_if_PO_exists.getIndicator();
                                            System.out.print("from other collection matching.." + po_from_new_data + Current_WorkflowStateName_ID + Site_ExternalReference + check_if_PO_exists.getSupplierName());
                                            //actual_data.add(new String[] {po_from_new_data,Current_WorkflowStateName_ID,Site_ExternalReference,indicator});
                                            actual_data.add(new String[]{po_from_new_data, "standardPO", "supply", "Loblaw", check_if_PO_exists.getSupplierName(), "CreateOrder", Current_WorkflowStateName_ID, SUB_State_Time, c3.getCustomField05(),"C3_"+DATETIMEFORMATTER.format(new Date())});
                                            writer.writeAll(actual_data);
                                            writer.close();
                                       //     postCSVFile(file,lpvtoken, LPV_PO_END_POINT_VAIRABLE,"c3collection");
                                            actual_data.clear();
                                            //writer.flush();
                                            i++;

                                        }
                                    }


                                }

                            }
                        }
                    }
                }
            }


        }

    }
}


















































