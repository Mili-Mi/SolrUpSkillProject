package tv.cadent.ctaas.dis.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import tv.cadent.ctaas.dis.service.SolrQueryServiceInterface;
import tv.cadent.ctaas.dis.vo.SolrQueryVo;

@RestController
public class SolrQueryController {
	
	@Autowired
	private SolrQueryServiceInterface SolrQueryService;
	
	@GetMapping(value="/query")
	public String getAllProducts(@Valid SolrQueryVo vo,HttpServletRequest request) throws SolrServerException, IOException {
//	 if("v1".equals(request.getHeader("version"))) {
	  String result = SolrQueryService.getAllProducts(vo);
	  return result;
//	 }else {
//		 return "Version not supported";
//	 }
		//return "";
	}
	
}
