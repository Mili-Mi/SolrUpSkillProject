package tv.cadent.ctaas.dis.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class FileUtil {

	public void deleteFilesInsideDiretory(Path path) throws IOException {
		if (path.toFile().exists()) {
			List<File> files = Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).collect(Collectors.toList());

			if (files.size() > 1) {
				files.stream().limit(files.size() - 1).forEach(file -> {
					//log.debug("Deleting: {}", file.getAbsolutePath());
					file.delete();
				});
			}
		}
	}
}
