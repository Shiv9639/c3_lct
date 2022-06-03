package com.C3Collection.C3.Service;

import com.C3Collection.C3.Model.LpvToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LpvTokenServiceImpl {
    private String BY_TOKEN_URL;
    private String BY_TOKEN_CLIENT_ID;
    private String BY_TOKEN_CLIENT_SECRET;
    private String BY_TOKEN_GRANT_TYPE;
    private String BY_TOKEN_SCOPE;

    public LpvTokenServiceImpl() {
        super();
    }

    public LpvTokenServiceImpl(String bY_TOKEN_URL, String bY_TOKEN_CLIENT_ID, String bY_TOKEN_CLIENT_SECRET,
                               String bY_TOKEN_GRANT_TYPE, String bY_TOKEN_SCOPE) {
        super();
        BY_TOKEN_URL = bY_TOKEN_URL;
        BY_TOKEN_CLIENT_ID = bY_TOKEN_CLIENT_ID;
        BY_TOKEN_CLIENT_SECRET = bY_TOKEN_CLIENT_SECRET;
        BY_TOKEN_GRANT_TYPE = bY_TOKEN_GRANT_TYPE;
        BY_TOKEN_SCOPE = bY_TOKEN_SCOPE;
    }



    public LpvToken getLpvToken() throws Exception {
        LpvToken token = new LpvToken();

        try {
            HttpPost post = new HttpPost(BY_TOKEN_URL);

            List<BasicNameValuePair> params = new ArrayList();
            params.add(new BasicNameValuePair("client_id", BY_TOKEN_CLIENT_ID));
            params.add(new BasicNameValuePair("client_secret", BY_TOKEN_CLIENT_SECRET));
            params.add(new BasicNameValuePair("grant_type", BY_TOKEN_GRANT_TYPE));
            params.add(new BasicNameValuePair("scope", BY_TOKEN_SCOPE));

            post.setEntity(new UrlEncodedFormEntity(params));

            HttpClient httpClient = HttpClientBuilder.create().build();

            HttpResponse response = httpClient.execute(post);
            int code = response.getStatusLine().getStatusCode();

            if (code == 200) {
                InputStream stream = response.getEntity().getContent();
                HttpEntity responseEntity = response.getEntity();
                String responseStr = EntityUtils.toString(responseEntity);
                JSONObject responseJSON = new JSONObject(responseStr);
                token.setToken_type(responseJSON.get("token_type").toString());
                token.setExpires_in(Integer.parseInt(responseJSON.get("expires_in").toString()));
                token.setExt_expires_in(Integer.parseInt(responseJSON.get("ext_expires_in").toString()));
                token.setAccess_token(responseJSON.getString("access_token"));

            } else {
                throw new Exception("Invalid Response from " + BY_TOKEN_URL);
            }
        } catch (Exception ex) {
            throw ex;
        }

        return token;
    }

    public void setBY_TOKEN_URL(String bY_TOKEN_URL) {
        BY_TOKEN_URL = bY_TOKEN_URL;
    }

    public void setBY_TOKEN_CLIENT_ID(String bY_TOKEN_CLIENT_ID) {
        BY_TOKEN_CLIENT_ID = bY_TOKEN_CLIENT_ID;
    }

    public void setBY_TOKEN_CLIENT_SECRET(String bY_TOKEN_CLIENT_SECRET) {
        BY_TOKEN_CLIENT_SECRET = bY_TOKEN_CLIENT_SECRET;
    }

    public void setBY_TOKEN_GRANT_TYPE(String bY_TOKEN_GRANT_TYPE) {
        BY_TOKEN_GRANT_TYPE = bY_TOKEN_GRANT_TYPE;
    }

    public void setBY_TOKEN_SCOPE(String bY_TOKEN_SCOPE) {
        BY_TOKEN_SCOPE = bY_TOKEN_SCOPE;
    }
    }
