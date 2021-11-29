package com.tpg.solr.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpg.solr.common.model.ProductDocument;
import com.tpg.solr.util.SolConnectionUtil;
import com.tpg.solr.vo.SolrQueryVo;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SolrQueryService implements SolrQueryServiceInterface {

	@Autowired
	private SolConnectionUtil solConnectionUtil;

	public String getAllProducts() throws SolrServerException, IOException {
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
	public void addProducts( List<SolrQueryVo> vo) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		for (int i = 0; i < vo.size(); i++) {
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("pid", UUID.randomUUID().toString());
		doc.addField("pname",vo.get(i).getPname());
		doc.addField("ptags", vo.get(i).getPcategory());
		doc.addField("pprice", vo.get(i).getPprice());
		doc.addField("pdescription", vo.get(i).getPdescription());
		doc.addField("pquantity", vo.get(i).getPquantity()); 
		doc.addField("pcategory",vo.get(i).getPcategory()); 
		final UpdateResponse updateResponse = client.add(doc);
		// Indexed documents must be committed
		client.commit("");
		}
	}
	public SolrDocument getProductById(String pid) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		SolrDocument doc = client.getById(pid);
		client.close();	
		return doc;
	}
	public String deleteProductById(String pid) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		client.deleteById(pid);
		client.commit();
		SolrQuery query = new SolrQuery();
		//client.deleteByQuery("*");
		//client.commit();
		QueryResponse response = client.query(query);
		SolrDocumentList docList = response.getResults();
		client.close();	
		return parseResult(docList).toString();
	}
	public void updateProducts(SolrQueryVo vo,String pid) throws SolrServerException, IOException {
		SolrClient client = solConnectionUtil.getSolrClient();
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("pid", pid);
		doc.addField("pname",vo.getPname());
		doc.addField("ptags", vo.getPcategory());
		doc.addField("pprice", vo.getPprice());
		doc.addField("pdescription", vo.getPdescription());
		doc.addField("pquantity", vo.getPquantity()); 
		doc.addField("pcategory",vo.getPcategory()); 
		final UpdateResponse updateResponse = client.add(doc);
		// Indexed documents must be committed
		client.commit("");
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
