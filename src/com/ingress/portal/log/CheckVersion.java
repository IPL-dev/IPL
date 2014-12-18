package com.ingress.portal.log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class CheckVersion extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {

		String URL = "https://raw.githubusercontent.com/IPL-dev/IPL/master/version.txt";
		String linha = "";
		boolean ret = true;

		try {

			HttpClient client = new DefaultHttpClient();
			HttpGet requisicao = new HttpGet();
			requisicao.setHeader("Content-Type",
					"text/plain; charset=utf-8");
			requisicao.setURI(new URI(URL));
			HttpResponse resposta = client.execute(requisicao);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					resposta.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");

			while ((linha = br.readLine()) != null) {
				sb.append(linha);
			}

			br.close();

			linha = sb.toString();
			if(linha.compareTo(params[0])==0) {
				ret = false;
			}

		} catch (Exception e) {
			ret = false;
			CheckPortalsFragment.outdated = true;
		}

		return ret;
	}
}