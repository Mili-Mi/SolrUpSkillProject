package tv.cadent.ctaas.dis.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.noggit.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import tv.cadent.ctaas.dis.rules.RulesLoader;
import tv.cadent.ctaas.dis.util.SolConnectionUtil;
import tv.cadent.ctaas.dis.vo.SolrQueryVo;

@Service
@Log4j2
public class SolrQueryService implements SolrQueryServiceInterface {

	@Autowired
	private SolConnectionUtil solConnectionUtil;
	
////	@Autowired
////	private RulesLoader loader;
//
	public String getAllProducts(SolrQueryVo vo) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		SolrQuery query = new SolrQuery();
		query.setQuery(generateQuery(vo));
		query.setRows(Integer.MAX_VALUE);
		QueryResponse response = client.query(query);
		SolrDocumentList results = response.getResults();
		client.close();
		//return results.toString();
		
		return parseResult(results).toString();
	}

	public String generateQuery(SolrQueryVo vo)  {
		StringBuilder bu = new StringBuilder();
		bu.append("select?indent=true&q.op=OR&q=*%3A*");
		//String utcDate=convertDateToUTC(vo.getEventTime());
		//bu.append("active_date_range: \"" + utcDate + "\"");
//		if (null != vo.getContentProvider()) {
//			bu.append(" AND ((*:* -content_providers:*) OR ");
//			bu.append("content_providers: \"" + vo.getContentProvider()+"\")");
//		}else {
//			bu.append(" AND (*:* -content_providers:*)");
//		}
//		if (null != vo.getDistributor()) {
//			bu.append(" AND ");
//			bu.append("distributors: " + vo.getDistributor());
//		}
//		if (null != vo.getZip()) {
//			bu.append(" AND ((*:* -ZIP:*) OR ");
//			bu.append("ZIP: " + vo.getZip()+")");
//		}else {
//			bu.append(" AND (*:* -ZIP:*)");
//		}
//		Long version = loader.getRulesVersionLoaded();
//		if (null != version) {
//			bu.append(" AND ");
//			bu.append("version: " + version);
//		}
//		log.info("DIS Solr Query: {}", bu.toString());
		return ""; // bu.toString();
	}

	private JSONArray parseResult(SolrDocumentList results) {
		JSONArray rulesArray = new JSONArray();
		for (SolrDocument solrDocument : results) {
			JSONObject obj = new JSONObject(solrDocument);
			rulesArray.put(obj);
		}
		return rulesArray;
	}
	
	
//	public String convertDateToUTC(String date)  {
//		DateTimeFormatter DATE_TIME_FORMATTER = null;
//		if (date.contains("."))
//			DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		else
//			DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//		OffsetDateTime odtInstanceAtOffset = OffsetDateTime.parse(date, DATE_TIME_FORMATTER);
//		OffsetDateTime odtInstanceAtUTC = odtInstanceAtOffset.withOffsetSameInstant(ZoneOffset.UTC);
//		DateTimeFormatter DATE_TIME_FORMATTER_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//		String dateStringInUTC = odtInstanceAtUTC.format(DATE_TIME_FORMATTER_UTC);
//		return dateStringInUTC;
//	}
	 
}
