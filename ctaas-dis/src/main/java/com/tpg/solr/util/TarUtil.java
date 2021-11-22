package com.tpg.solr.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Component;

import com.tpg.solr.exception.DisException;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class TarUtil {

	public List<Path> unTar(Path inputFile, final Path outputDir) throws DisException {

		final List<Path> untaredFiles = new LinkedList<Path>();
		InputStream is = null;
		TarArchiveInputStream tarInputStream = null;
		try {
			if (inputFile.endsWith(".gz")) {
				is = new GzipCompressorInputStream(Files.newInputStream(inputFile));
			} else {
				is = Files.newInputStream(inputFile);
			}

			tarInputStream = new TarArchiveInputStream(is);
			TarArchiveEntry entry = null;
			OutputStream outputFileStream;
			while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
				Path outputPath = Paths.get(outputDir + "/" + entry.getName());

				if (entry.isDirectory()) {
					if (!outputPath.toFile().exists()) {
						if (!outputPath.toFile().mkdirs()) {
							throw new DisException(String.format("Could not create directory %s.", outputPath.toFile().getAbsolutePath()));
						}
					}
				} else {
					if (!outputPath.toFile().exists()) {
						if (!!outputPath.getParent().toFile().getAbsoluteFile().mkdirs()) {
							throw new DisException(String.format("Couldn't create directory %s.", outputPath.toFile().getParentFile().getAbsolutePath()));
						}
					}
					outputFileStream = Files.newOutputStream(outputPath);
					IOUtils.copy(tarInputStream, outputFileStream);
					outputFileStream.close();
				}
				untaredFiles.add(outputPath);
			}
			tarInputStream.close();
			return untaredFiles;

		} catch (IOException ioe) {
			String msg = String.format("Error while extracting tar from %s to %s", inputFile.toFile().getAbsolutePath(), outputDir.toFile().getAbsolutePath());
			//log.warn(msg, ioe);
			throw new DisException(msg, ioe);
		}

		finally {
			if (tarInputStream != null) {
				try {
					tarInputStream.close();
				} catch (Exception e) {
					//log.warn("Error while closing input stream", e);
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					//log.warn("Error while closing input stream", e);
				}
			}
		}
	}
}
