package tv.cadent.ctaas.dis.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RulesInitializer implements CommandLineRunner{

	@Autowired
	private RulesLoader rulesLoader;
	
	@Override
	public void run(String... args) throws Exception {
	//	rulesLoader.loadRules();
	}

}
