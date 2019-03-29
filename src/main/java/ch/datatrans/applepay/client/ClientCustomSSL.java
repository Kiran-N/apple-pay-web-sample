package ch.datatrans.applepay.client;

/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.File;
import java.io.IOException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * This example demonstrates how to create secure connections with a custom SSL
 * context.
 */
@Component
public class ClientCustomSSL {
	@Value("${ch.datatrans.applepay.merchantIdentifier}") String merchantIdentifier;
    @Value("${ch.datatrans.applepay.displayName}") String displayName;
 	String retSrc;

	 public String createSession(String validationUrl, String origin) throws IOException {
			 try {
				 SSLContext sslcontext = SSLContexts.custom()
			                .loadTrustMaterial(ResourceUtils.getFile("classpath:tls/apple-pay.p12"), "123456".toCharArray(),
			                        new TrustSelfSignedStrategy())
			                .build();
			        // Allow TLSv1 protocol only
			        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
			                sslcontext,
			                new String[] { "TLSv1.2" },
			                null,
			                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
			        CloseableHttpClient httpclient = HttpClients.custom()
			                .setSSLSocketFactory(sslsf)
			                .build();	
	            HttpPost httppost = new HttpPost(validationUrl);
	            httppost.addHeader("content-type", "application/json");
	            HttpEntity entity = new StringEntity("{\"merchantIdentifier\":\"merchant.com.herokuapp.ewallet-applepay\", \"domainName\":\"ewallet-applepay.herokuapp.com\", \"displayName\":\"ewallet-applepay\"}");
	            httppost.setEntity(entity);
	            System.out.println("Executing request " + httppost.getRequestLine());
	            CloseableHttpResponse response = httpclient.execute(httppost);
	            try {
	                HttpEntity responseEntity = response.getEntity();

	                System.out.println("----------------------------------------");
	                System.out.println(response.getStatusLine());
	                EntityUtils.consume(responseEntity);
	                retSrc = EntityUtils.toString(responseEntity);
	            } finally {
	                response.close();
	            }
	        } 
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 finally {
	            httpclient.close();
	     }
		 
		 
	        return retSrc;
	    }
    

}
