package tv.cadent.ctaas.dis.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
public class RulesConfig {

	@Value("${dis.rules.fetchurl}")
	private String rulesFetchUrl;
	
	@Value(value = "${dis.solrUrl}") 
	private String solrUrl;
	
	@Value(value = "${dis.rules.runtimeEnvironment}") 
	private String runtimeEnvironment;
	
	@Bean
	@Qualifier("solrUpdateUrl")
	public String getSolrUpdateUrl() {
		String solrUpdateUrl = String.format("%s/update?commit=true", solrUrl);
		//log.info("Solr Update URL: {}", solrUpdateUrl);
		System.out.println("Solr Update URL: {}"+ solrUpdateUrl);
		return solrUpdateUrl;
	}
	
	@Bean
	@Qualifier("rulesHttpUrl")
	public String rulesHttpFetchUrl(@Autowired @Qualifier("rulesFileName") String rulesFileName) {
		
		if(!rulesFetchUrl.endsWith("/")) {
			rulesFetchUrl = String.format("%s/", rulesFetchUrl);
		}
		String rulesHttpFetchUrl = String.format("%s%s", rulesFetchUrl, rulesFileName);
		//log.info("Rules Http Fetch Url URL: {}", rulesHttpFetchUrl);
		System.out.println("Rules Http Fetch Url URL: {}"+rulesFetchUrl);
		return rulesHttpFetchUrl;
	}
	
	@Bean
	@Qualifier("rulesFileName")
	public String rulesFilename() {
		String rulesHttpFetchUrl = String.format("%s-%s-%s-LATEST.tgz", runtimeEnvironment, RulesConstant.PRODUCT_NAME, RulesConstant.RULES_VERSION);
		//log.info("Rules Http Fetch Url URL: {}", rulesHttpFetchUrl);
		System.out.println("Rules Http Fetch Url URL: {}"+rulesHttpFetchUrl);
		return rulesHttpFetchUrl;
	}
}
