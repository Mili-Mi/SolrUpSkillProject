package tv.cadent.ctaas.dis.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RuleCronConfig {

//	@Value(value = "${dis.cronEnabled}")
//	private boolean cronEnabled;
//	
//	@Autowired
//	private RulesLoader rulesLoader;
//	
//	@Scheduled(cron = "${dis.cron}")
//	public void schedule() {
//		if(cronEnabled) {
//			try {
//				rulesLoader.loadRules();
//			} catch (DisException e) {
//			//	log.error("Error while executing rulesloader cron", e);
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void setCronEnabled(boolean cronEnabled) {
//		this.cronEnabled = cronEnabled; 
//	}
//	
//	public boolean isCronEnabled() {
//		return cronEnabled;
//	}
}
