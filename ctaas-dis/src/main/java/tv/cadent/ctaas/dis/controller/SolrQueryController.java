package tv.cadent.ctaas.dis.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import tv.cadent.ctaas.dis.service.SolrQueryServiceInterface;
import tv.cadent.ctaas.dis.vo.SolrQueryVo;

@RestController
public class SolrQueryController {
	
	@Autowired
	private SolrQueryServiceInterface SolrQueryService;
	
	@GetMapping(value="/query/getAllProducts")
	public String getAllProducts(@Valid SolrQueryVo vo,HttpServletRequest request) throws SolrServerException, IOException {
	  String result = SolrQueryService.getAllProducts(vo);
	  return result;
	}
	@PostMapping(value="/query/addProducts")
	public String addProducts(@Valid SolrQueryVo vo,HttpServletRequest request) throws SolrServerException, IOException {
	  SolrQueryService.addProducts(vo);
	  return "document added";
	}
	@PostMapping(value="/query/deleteProductById")
	public String deleteProductById() throws SolrServerException, IOException {
	  String result = SolrQueryService.deleteProductById();
	  return result;
	}
	@GetMapping(value="/query/getProductById")
	public SolrDocument getProduct(@Valid SolrQueryVo vo,HttpServletRequest request) throws SolrServerException, IOException {
	  SolrDocument doc = SolrQueryService.getProductById();
	  return doc;
	}
}
