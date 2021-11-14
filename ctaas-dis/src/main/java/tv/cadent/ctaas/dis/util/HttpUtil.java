package tv.cadent.ctaas.dis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tv.cadent.ctaas.dis.exception.DisException;


@Component
@Log4j2
public class HttpUtil {

	
	public long downloadContentsIfModified(String urlStr, long lastLoadedRuleTime, Path downloadPath, boolean gzip) throws DisException {
		URL url = getUrl(urlStr);
		try {
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			int responseCode = con.getResponseCode();
			log.debug("Trying to fetch url {}", urlStr);
			if (responseCode == HttpsURLConnection.HTTP_OK ) {
				if(Objects.isNull(lastLoadedRuleTime) || con.getLastModified() > lastLoadedRuleTime) {
					InputStream inputStream = con.getInputStream();
					OutputStream outputStream = Files.newOutputStream(downloadPath);
					if(gzip) {
						IOUtils.copy(new GzipCompressorInputStream(inputStream), outputStream);
					}else {
						IOUtils.copy(inputStream, outputStream);
					}
					inputStream.close();
					outputStream.close();
					lastLoadedRuleTime = con.getLastModified();
				}else {
					log.debug("Content was not modified for URL {}", urlStr);
				}

			} else {
				log.warn("Response Code is not 200 while conneting to the URL {}", urlStr);
			}
		} catch (IOException e) {
			String msg = String.format("Error while connecting to URL %s", urlStr);
			log.warn(msg, e);
			throw new DisException(msg, e);
		}
		
		return lastLoadedRuleTime;
	}




	private URL getUrl(String url) throws DisException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			String msg = String.format("Invalid URL %s ", url);
			log.warn(msg, e);
			throw new DisException(msg, e);
		}
	}
	
	
	
	
	public int postFileContents(String urlStr, Path decryptedFile) throws DisException {
		URL url = getUrl(urlStr);
		int code;
		try {
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Accept", "application/json");

			con.setDoOutput(true);
			InputStream input = Files.newInputStream(decryptedFile);
			OutputStream output = con.getOutputStream();
			IOUtils.copy(input, output);
			code = con.getResponseCode();
			input.close();
			output.close();

			log.debug("Http Status code {} for URL {}", code, urlStr);

			if(log.isDebugEnabled()) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					log.debug("URL: {} ,Http Ressponse: ", urlStr, response);
				}
			}	
		} catch (IOException e) {
			String msg = String.format("Error while connecting to URL %s", urlStr);
			log.warn(msg, e);
			throw new DisException(msg, e);
		}
		
		
		return code;
	}
	
}
