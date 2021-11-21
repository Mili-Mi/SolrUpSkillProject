package tv.cadent.ctaas.dis.service;

import java.io.IOException;

import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.json.JSONArray;

import tv.cadent.ctaas.dis.vo.SolrQueryVo;

public interface SolrQueryServiceInterface {

	public String getAllProducts(SolrQueryVo vo) throws SolrServerException, IOException;

	public String deleteProductById() throws SolrServerException, IOException;

	public void addProducts(@Valid SolrQueryVo vo) throws SolrServerException, IOException;

	public SolrDocument getProductById() throws SolrServerException, IOException;
}
