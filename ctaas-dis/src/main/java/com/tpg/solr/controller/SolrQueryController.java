package com.tpg.solr.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpg.solr.service.SolrQueryServiceInterface;
import com.tpg.solr.vo.SolrQueryVo;

@RestController
public class SolrQueryController {
	
	@Autowired
	private SolrQueryServiceInterface SolrQueryService;
	
	@GetMapping(value="/query/listProducts")
	public String getAllProducts(HttpServletRequest request) throws SolrServerException, IOException {
	  String result = SolrQueryService.getAllProducts();
	  return result;
	}
	@PostMapping(value="/query/addProducts")
	public String addProducts(@Valid @RequestBody List<SolrQueryVo> vo,HttpServletRequest request) throws SolrServerException, IOException {
	  SolrQueryService.addProducts(vo);
	  return "document added";
	}
	@PostMapping(value="/query/deleteProduct/{pid}")
	public String deleteProductById(@PathVariable String pid) throws SolrServerException, IOException {
	  String result = SolrQueryService.deleteProductById(pid);
	  return result;
	}
	@GetMapping(value="/query/getProduct/{pid}")
	public SolrDocument getProduct(@PathVariable String pid, HttpServletRequest request) throws SolrServerException, IOException {
	  SolrDocument doc = SolrQueryService.getProductById(pid);
	  return doc;
	}
	@PostMapping(value="/query/updateProducts/{pid}")
	public String updateProducts(@Valid @RequestBody SolrQueryVo vo,@PathVariable String pid,HttpServletRequest request) throws SolrServerException, IOException {
	  SolrQueryService.updateProducts(vo,pid);
	  return "document updated";
	}
}
