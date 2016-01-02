package com.stolser.nettyserver.server.data;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.stolser.nettyserver.server.handlers.FullDataCollector;


public class FileStatisticsStorage implements StatisticsDataStorage {
	private static final Logger logger = LoggerFactory.getLogger(FileStatisticsStorage.class);
	private static Map<String, FileStatisticsStorage> fileAccessors = new HashMap<>();
	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private String storageFileName;
	private File file;
	
	private FileStatisticsStorage(String storageFileName) {
		this.storageFileName = storageFileName;
		file = Paths.get(storageFileName).toFile();
	}
	
	public static FileStatisticsStorage getInstance(String fileName) {
		Preconditions.checkNotNull(fileName, "a file name cannot be null.");
		FileStatisticsStorage storageToUse = null;
		FileStatisticsStorage existingStorage = null;
		
		if ((existingStorage = fileAccessors.get(fileName)) != null) {
			storageToUse = existingStorage;
		} else {
			storageToUse = new FileStatisticsStorage(fileName);
			fileAccessors.put(fileName, storageToUse);
		}
		
		return storageToUse;
	}

	@Override
	public FullStatisticsData retrieveData() {
		
		lock.readLock().lock();
		FullStatisticsData fullData = readDataFromFile();
		lock.readLock().unlock();
		
		return fullData;
	}

	@Override
	public void persistData(FullStatisticsData data) {

		lock.writeLock().lock();
		eraseFileOldContent();
		writeDataIntoFile(data);
		lock.writeLock().unlock();
	}
	
	private void writeDataIntoFile(FullStatisticsData data) {
		try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)))) {
	
			out.writeObject(data);
			out.flush();
			
		} catch (Exception e) {
			logger.debug("exception during writing into the file {}", storageFileName, e);
		}
	}

	private void eraseFileOldContent() {
		try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)))) {
			
			out.flush();
			out.close();
			
		} catch (Exception e) {
			logger.debug("exception during erasing the file {}", storageFileName, e);
		}
	}

	private FullStatisticsData readDataFromFile() {
		FullStatisticsData fullData = null;
		try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream(file)))) {
			
			fullData = (FullStatisticsData)in.readObject();

		} catch (Exception e) {
			logger.debug("exception during reading a file {}", storageFileName, e);
		}
		
		return fullData;
		
	}
}
