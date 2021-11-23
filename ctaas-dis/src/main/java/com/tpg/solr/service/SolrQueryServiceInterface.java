package com.tpg.solr.service;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;

import com.tpg.solr.vo.SolrQueryVo;

public interface SolrQueryServiceInterface {

	public String getAllProducts() throws SolrServerException, IOException;

	public String deleteProductById(String pid) throws SolrServerException, IOException;

	public void addProducts(@Valid List<SolrQueryVo> vo) throws SolrServerException, IOException;

	public SolrDocument getProductById(String pid) throws SolrServerException, IOException;
}
