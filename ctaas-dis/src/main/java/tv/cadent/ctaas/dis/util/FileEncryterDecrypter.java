package tv.cadent.ctaas.dis.util;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.annotation.PostConstruct;

import org.encryptor4j.util.FileEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileEncryterDecrypter {
	
	@Value("${dis.file-encrypter.passphrase}")	
	private String passphrase;

	private FileEncryptor fileEncryptor;
	
	@PostConstruct
	public void init() {
		fileEncryptor = new FileEncryptor(passphrase);

	}
	
	public void encrypt(File inputFile, File outoutFile) throws GeneralSecurityException, IOException  {
		fileEncryptor.encrypt(inputFile, outoutFile);			
	}

	public void decrypt(File inputFile, File outoutFile) throws GeneralSecurityException, IOException  {
		fileEncryptor.decrypt(inputFile, outoutFile);			
	}
	
}
