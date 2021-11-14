package tv.cadent.ctaas.dis.service;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;

import tv.cadent.ctaas.dis.vo.SolrQueryVo;

public interface SolrQueryServiceInterface {

	public String getAllProducts(SolrQueryVo vo) throws SolrServerException, IOException;
}
