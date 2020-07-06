package com.pushstats.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.zendesk.maxwell.row.RowMap;

public class HttpUtil {
	public void doPost(String[] pair, RowMap row, String[] excluded){
		String db = pair[0];
		String url = pair[1];
		if (!db.equals(row.getDatabase())) {
			System.out.println("No '" + db + "|" + url + "' pair there, passed.");
			return;
		}
		
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        Iterator<Entry<String, Object>> it = row.getData().entrySet().iterator();
        while (it.hasNext()) {
        	Entry<String, Object>entry = it.next();
        	if (excluded == null) {
        		formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        	} else {
        		if (!Arrays.asList(excluded).contains(row.getDatabase() + "." + row.getTable() + "." + entry.getKey())) {
        			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        		}
        	}
        }
        UrlEncodedFormEntity uefEntity;
        try {
        	uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        	post.setEntity(uefEntity);
            System.out.println("executing request " + post.getURI());
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
