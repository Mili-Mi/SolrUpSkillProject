package tv.cadent.ctaas.dis.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
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

	public String getAllProducts(SolrQueryVo vo) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		SolrQuery query = new SolrQuery();
		//query.setQuery(generateQuery(vo));
		query.setQuery("*:*");
		query.setRows(Integer.MAX_VALUE);
		QueryResponse response = client.query(query);
		SolrDocumentList results = response.getResults();
		client.close();
		return parseResult(results).toString();
	}
	public void addProducts(SolrQueryVo vo) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("pid", UUID.randomUUID().toString());
		doc.addField("pname", "Dell Laptop");
		doc.addField("ptags", "new,Laptop");
		doc.addField("pprice", 300000);
		doc.addField("pdescription", "Dell 15 (2021) Athlon Silver 3050U Laptop, 4GB");
		doc.addField("pquantity", 3); 
		doc.addField("pcategory","Laptop"); 
		final UpdateResponse updateResponse = client.add(doc);
		// Indexed documents must be committed
		client.commit("");
	}
	public SolrDocument getProductById() throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		SolrDocument doc = client.getById("947ca20b-e8f1-4721-9473-22c2e1d2387b");
		client.close();	
		return doc;
	}
	public String deleteProductById() throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		client.deleteById("947ca20b-e8f1-4721-9473-22c2e1d2387b");
		client.commit();
		SolrQuery query = new SolrQuery();
		query.set("q", "id:123456");
		QueryResponse response = client.query(query);
		SolrDocumentList docList = response.getResults();
		client.close();	
		return parseResult(docList).toString();
	}
	
	public String generateQuery(SolrQueryVo vo)  {
		StringBuilder bu = new StringBuilder();
		bu.append("select?indent=true&q.op=OR&q=*%3A*");
		return bu.toString();
	}

	private JSONArray parseResult(SolrDocumentList results) {
		JSONArray rulesArray = new JSONArray();
		for (SolrDocument solrDocument : results) {
			JSONObject obj = new JSONObject(solrDocument);
			rulesArray.put(obj);
		}
		return rulesArray;
	}
	
	 
}
