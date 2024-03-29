package com.gitlab.jeeto.oboco.data.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;

public class BookReaderPoolManager {
	private static Logger logger = LoggerFactory.getLogger(BookReaderPoolManager.class.getName());
	private static BookReaderPoolManager instance;
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	private BookReaderPool bookReaderPool;
	
	public static BookReaderPoolManager getInstance() {
		if(instance == null) {
			synchronized(BookReaderPoolManager.class) {
				if(instance == null) {
					instance = new BookReaderPoolManager();
				}
			}
		}
		return instance;
	}
	
	private BookReaderPoolManager() {
		super();
	}
	
	public BookReader getBookReader(BookType bookType) throws Exception {
		BookReader bookReader = new BookReaderPoolDelegator(bookType, bookReaderPool);
		
        return bookReader;
	}
	
	public void start() {
		logger.info("start bookReaderPoolManager");
		
		Integer size = getConfiguration().getAsInteger("data.book.bookReaderPool.size", "25");
		Long interval = getConfiguration().getAsLong("data.book.bookReaderPool.interval", "60") * 1000L;
		Long age = getConfiguration().getAsLong("data.book.bookReaderPool.age", "600") * 1000L;
		
		bookReaderPool = new BookReaderPool(size, interval, age);
		bookReaderPool.start();
	}
	
	public void stop() {
		logger.info("stop bookReaderPoolManager");
		
		bookReaderPool.stop();
	}
}
