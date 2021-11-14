package tv.cadent.ctaas.dis.rules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class RulesLoaderImpl implements RulesLoader {
//
//	private long lastLoadedRuleModifiedTime = 0l;
//
//	@Autowired
//	private TarUtil tarUtil;
//
//	@Autowired
//	private FileUtil fileUtil;
//
//	@Autowired
//	private HttpUtil httpUtil;
//
//	@Autowired
//	private FileEncryterDecrypter fileEncryterDecrypter;
//
//	@Autowired
//	@Qualifier("rulesHttpUrl")
//	private String rulesHttpUrl;
//	
//	
//	@Autowired
//	@Qualifier("rulesFileName")
//	private String rulesFileName;
//
//	@Autowired
//	@Qualifier("solrUpdateUrl")
//	private String solrUpdateUrl;
//
//	@Value(value = "${dis.delete-intermediary-files:true}")
//	private boolean deleteIntermediaryFiles;
//
//	public static final String DIS_FILENAME_REGEX = "(.*?-)(DIS)-(\\d+\\.?\\d*)-(\\d+)-(.*)(\\.json.enc)";
//
//	private Long rulesVersionLoaded;
//
//	@Value("${rules.dir:rules}")
//	private String compilationDir;
//
//	private ReentrantLock lock = new ReentrantLock();
//
//	private final static boolean GZIPPED = true;
//	
//	public Long getRulesVersionLoaded() {
//		return rulesVersionLoaded;
//	}
//	public void setRulesVersionLoaded(Long rulesVersionLoaded) {
//		this.rulesVersionLoaded = rulesVersionLoaded;
//	}
//	
//	public boolean loadRules() throws DisException {
//		boolean locked = lock.tryLock();
//		Path rulesFile = null;
//		Path decryptedFile = null;
//		Path compilationPath = Paths.get(compilationDir);
//		if(locked) {
//			try {
//				
//				if(!compilationPath.toFile().exists()) {
//					compilationPath.toFile().mkdirs();
//				}
//				rulesFile = Paths.get(compilationDir,rulesFileName);
//				long currentRuleModifiedTime = httpUtil.downloadContentsIfModified(rulesHttpUrl, lastLoadedRuleModifiedTime, rulesFile, GZIPPED);
//				if (currentRuleModifiedTime != lastLoadedRuleModifiedTime) {
//					List<Path> untarredFiles =tarUtil.unTar(rulesFile, compilationPath);
//					List<Path> childFiles = untarredFiles.stream().filter(Files::isRegularFile).collect(Collectors.toList());
//					if (childFiles.size() == 1) {
//						Path encryptedRulesFile = childFiles.iterator().next();
//						long rulesVersionParsed = getRulesVersion(encryptedRulesFile.toFile().getName());
//						if(rulesVersionLoaded != null && rulesVersionParsed <= rulesVersionLoaded) {
//							log.warn("Trying to load old or existing rules, loaded version: {}, new rules version: {}", rulesVersionLoaded, rulesVersionParsed);
//							return locked;
//						}
//						decryptedFile = Paths.get(compilationDir, String.format("%s", encryptedRulesFile.toFile().getName()+".decrypted"));
//						fileEncryterDecrypter.decrypt(encryptedRulesFile.toFile(), decryptedFile.toFile());
//						int statusCode = httpUtil.postFileContents(solrUpdateUrl, decryptedFile);
//						if(statusCode != HttpStatus.OK.value()) {
//							String msg = String.format("Unable to update on Solr, received http status code %s", statusCode);
//							log.warn(msg);
//							throw new DisException(msg);
//						}else {
//							lastLoadedRuleModifiedTime = currentRuleModifiedTime;
//							rulesVersionLoaded = rulesVersionParsed;
//						}
//					} else if (childFiles.size() == 0) {
//						throw new DisException("No files found inside tar file ");
//					} else {
//						String msg = String.format("Multiple files found inside tar file %s", childFiles.stream().map(t -> t.toFile().getName()).collect(Collectors.joining(",", "[", "]")));
//						log.error(msg);
//						throw new DisException(msg);
//					}
//				} else {
//					log.info("No new rules found");
//				}
//				
//			} catch (Exception e) {
//				log.error("Error while loading rules", e);
//				throw new DisException("Error while loading rules", e);
//			} finally {
//				try {
//					if(deleteIntermediaryFiles) {
//						fileUtil.deleteFilesInsideDiretory(compilationPath);
//					}
//				} catch (IOException e) {
//					log.warn("Error while deleting temporary files from {}", compilationPath.toFile().getAbsolutePath());
//				}finally {
//					lock.unlock();
//				}
//				
//			}
//		}else {
//			log.warn("Loading Rules already in progress");
//		}
//			
//		return locked;
//
//	}
//
//	private long getRulesVersion(String filename) throws DisException {
//		Pattern pattern = Pattern.compile(DIS_FILENAME_REGEX);
//		Matcher matcher = pattern.matcher(filename);
//		long version;
//		if (matcher.matches()) {
//			String versionStr = matcher.group(4);
//			try {
//				version = Long.parseLong(versionStr);
//				return version;
//			} catch (Exception e) {
//				String message = String.format("Invalid version %s", versionStr);
//				log.error(message, e);
//				throw new DisException(message);
//			}
//		} else {
//			String message = String.format("Invalid filename %s inside compiled rules ", filename);
//			log.error(message);
//			throw new DisException(message);
//		}
//	}
}
