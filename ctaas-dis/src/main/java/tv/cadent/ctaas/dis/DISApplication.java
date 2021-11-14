package tv.cadent.ctaas.dis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
@Log4j2
@EnableScheduling
public class DISApplication {

	public static void main(String[] args) {
		SpringApplication.run(DISApplication.class, args);
	}
	
}
