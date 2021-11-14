package tv.cadent.ctaas.dis.util;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SolConnectionUtil {
	
	@Autowired
	Environment env;
	
	public SolrClient getSolrClient() {
		return new HttpSolrClient.Builder(env.getProperty("dis.solrUrl")).build();
	}
}
