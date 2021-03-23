package com.gitlab.jeeto.oboco.api.v1.bookscanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitlab.jeeto.oboco.api.v1.book.Book;
import com.gitlab.jeeto.oboco.api.v1.book.BookService;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollection;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionService;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMark;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkReference;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkService;
import com.gitlab.jeeto.oboco.common.NameHelper;
import com.gitlab.jeeto.oboco.common.configuration.Configuration;
import com.gitlab.jeeto.oboco.common.configuration.ConfigurationManager;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.plugin.FileType;
import com.gitlab.jeeto.oboco.plugin.FileWrapper;
import com.gitlab.jeeto.oboco.plugin.NaturalOrderComparator;
import com.gitlab.jeeto.oboco.plugin.PluginManager;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReader;
import com.gitlab.jeeto.oboco.plugin.archive.ArchiveReaderFactory;
import com.gitlab.jeeto.oboco.plugin.hash.HashManager;
import com.gitlab.jeeto.oboco.plugin.hash.HashManagerFactory;
import com.gitlab.jeeto.oboco.plugin.hash.HashType;
import com.gitlab.jeeto.oboco.plugin.image.ImageManager;
import com.gitlab.jeeto.oboco.plugin.image.ImageManagerFactory;
import com.gitlab.jeeto.oboco.plugin.image.ScaleType;

public class DefaultBookScannerService implements BookScannerService {
	private static Logger logger = LoggerFactory.getLogger(DefaultBookScannerService.class.getName());
	@Inject
	private BookMarkService bookMarkService;
	@Inject
	private BookService bookService;
	@Inject
	private BookCollectionService bookCollectionService;
	private Configuration configuration;
	
	private Configuration getConfiguration() {
		if(configuration == null) {
			ConfigurationManager configurationManager = ConfigurationManager.getInstance();
			configuration = configurationManager.getConfiguration();
		}
		return configuration;
	}
	
	private String id;
	private BookScannerServiceStatus status;
	private Date updateDate;
	private List<BookPage> defaultBookPageList;
	
	public DefaultBookScannerService() {
		super();
		id = "DEFAULT";
		status = BookScannerServiceStatus.STOPPED;
		updateDate = null;
		defaultBookPageList = new ArrayList<BookPage>();
	}
	
	private List<BookPage> createBookPageList() throws Exception {
		List<BookPage> bookPageList = new ArrayList<BookPage>();
		
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader("./data.csv"));
		    String line = null;
		    line = bufferedReader.readLine();
		    if(line != null && line.equals("page,scaleType,scaleWidth,scaleHeight")) {
			    while((line = bufferedReader.readLine()) != null) {
			        String[] values = line.split(",");
			        
			        Integer page = null;
			        try {
			        	page = Integer.valueOf(values[0]);
			        } catch(Exception e) {
			        	// pass
			        }
			        ScaleType scaleType = null;
			        try {
			        	scaleType = ScaleType.valueOf(values[1]);
			        } catch(Exception e) {
			        	// pass
			        }
			        Integer scaleWidth = null;
			        try {
			        	scaleWidth = Integer.valueOf(values[2]);
			        } catch(Exception e) {
			        	// pass
			        }
			        Integer scaleHeight = null;
			        try {
			        	scaleHeight = Integer.valueOf(values[3]);
			        } catch(Exception e) {
			        	// pass
			        }
			        
			        BookPage bookPage = getBookPage(bookPageList, page);
			        if(bookPage == null) {
			        	bookPage = new BookPage();
			        	bookPage.setPage(page);
			    		
			    		bookPageList.add(bookPage);
			        }
			        BookPageConfiguration bookPageConfiguration = new BookPageConfiguration();
			        bookPageConfiguration.setScaleType(scaleType);
			        bookPageConfiguration.setScaleWidth(scaleWidth);
			        bookPageConfiguration.setScaleHeight(scaleHeight);
					
					bookPage.getBookPageConfigurationList().add(bookPageConfiguration);
			    }
		    }
		} finally {
			if(bufferedReader != null) {
				bufferedReader.close();
			}
		}
		
		return bookPageList;
	}
	
	private BookPage getBookPage(List<BookPage> bookPageList, Integer page) throws Exception {
		for(BookPage bookPage: bookPageList) {
        	if(bookPage.getPage() == page) {
        		return bookPage;
        	}
        }
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public BookScannerServiceStatus getStatus() {
		return status;
	}
	
	public void start() throws ProblemException {
		status = BookScannerServiceStatus.STARTING;
		updateDate = new Date();
		status = BookScannerServiceStatus.STARTED;
		try {
			defaultBookPageList = createBookPageList();
			
			String directoryPath = getConfiguration().getAsString("application.data.path", "./data");
	    	
	    	File directory = new File(directoryPath);
	    	if(directory.isDirectory() == false) {
	    		throw new ProblemException(new Problem(500, "PROBLEM_APPLICATION_DATA_PATH_INVALID", "The application.data.path is invalid."));
	    	}
	    	
	    	Properties dataProperties = new Properties();
	    	dataProperties.load(new FileInputStream("./data.properties"));
			
			for(Entry<Object, Object> entry: dataProperties.entrySet()) {
				String name = entry.getKey().toString();
				String pathListString = entry.getValue().toString();
				
				if(pathListString == null) {
					pathListString = "";
				}
				
				String[] paths = pathListString.split(",");
				
				List<String> pathList = new ArrayList<String>();
				
				for(String path: paths) {
					path = path.trim();
					
					File file = new File(path);
					if(file.isDirectory()) {
				        path = file.getAbsolutePath();
				        
				        pathList.add(path);
					}
				}
				
				BookCollection bookCollection = bookCollectionService.getRootBookCollectionByName(name);
				
				if(bookCollection == null) {
					logger.info("create rootBookCollection");
					
					bookCollection = new BookCollection();
					bookCollection.setDirectoryPath("");
					bookCollection.setRootBookCollection(null);
					bookCollection.setParentBookCollection(null);
					bookCollection.setUpdateDate(updateDate);
					
					bookCollection.setName(name);
					
					String normalizedName = NameHelper.getNormalizedName(name);
					
					bookCollection.setNormalizedName(normalizedName);
					bookCollection.setNumberOfBookCollections(0);
					bookCollection.setNumberOfBooks(0);
					bookCollection.setChildBookCollections(new ArrayList<BookCollection>());
					bookCollection.setNumber(1);
			        
			        bookCollection = bookCollectionService.createBookCollection(bookCollection);
				} else {
					logger.info("update rootBookCollection");
					
					bookCollection.setUpdateDate(updateDate);
					
					bookCollection.setName(name);
					
					String normalizedName = NameHelper.getNormalizedName(name);
					
					bookCollection.setNormalizedName(normalizedName);
					bookCollection.setNumberOfBookCollections(0);
					bookCollection.setNumberOfBooks(0);
					bookCollection.setChildBookCollections(new ArrayList<BookCollection>());
					bookCollection.setNumber(1);
					
					bookCollection = bookCollectionService.updateBookCollection(bookCollection);
				}
				
				for(String path: pathList) {
					File file = new File(path);
					
				    add(bookCollection, bookCollection, file);
				}
				
				if(status.equals(BookScannerServiceStatus.STOPPING)) {
		    		logger.info("stopping!");
		    		return;
				}
	    	}
			
			logger.info("delete bookMarkReferences");
	        
	        bookMarkService.deleteBookMarkReferenceByUpdateDate(updateDate);
	        
	        logger.info("delete books");
	        
	        bookService.deleteBookByUpdateDate(updateDate);
	        
	        logger.info("delete bookCollections");
	        
	        bookCollectionService.deleteBookCollectionByUpdateDate(updateDate);
	        
	        deleteBookPageByUpdateDate();
		} catch(ProblemException e) {
			throw e;
		} catch(Exception e) {
			logger.error("Error.", e);
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		} finally {
			status = BookScannerServiceStatus.STOPPED;
			updateDate = null;
		}
    }
	
	public void stop() throws ProblemException {
		logger.info("stopping..");
		status = BookScannerServiceStatus.STOPPING;
	}
    
	private List<BookCollection> add(BookCollection rootBookCollection, BookCollection parentBookCollection, File parentFile) throws Exception {
		Integer numberOfBookCollections = parentBookCollection.getNumberOfBookCollections();
		Integer numberOfBooks = parentBookCollection.getNumberOfBooks();
		List<BookCollection> childBookCollections = parentBookCollection.getChildBookCollections();
		
    	File[] files = parentFile.listFiles();
    	
    	List<File> fileList = Arrays.asList(files);
    	fileList.sort(new NaturalOrderComparator<File>() {
    		@Override
    		public String toString(File o) {
				return o.getName();
		   }
    	});
    	
		for(File file: fileList) {
			if(status.equals(BookScannerServiceStatus.STOPPING)) {
	    		logger.info("stopping!");
	    		break;
			}
			
			String path = file.getPath();
			
			if(file.isDirectory()) {
				BookCollection bookCollection = bookCollectionService.getBookCollectionByBookCollectionIdAndDirectoryPath(rootBookCollection.getId(), path);
				
				if(bookCollection == null) {
					logger.info("create bookCollection " + path);
					
					bookCollection = new BookCollection();
					bookCollection.setDirectoryPath(path);
					bookCollection.setRootBookCollection(rootBookCollection);
					bookCollection.setParentBookCollection(parentBookCollection);
					bookCollection.setUpdateDate(updateDate);
					
					String name = NameHelper.getName(file.getName());
					
					bookCollection.setName(name);
					
					String normalizedName = NameHelper.getNormalizedName(file.getName());
					
					bookCollection.setNormalizedName(normalizedName);
					bookCollection.setNumberOfBookCollections(0);
					bookCollection.setNumberOfBooks(0);
					bookCollection.setChildBookCollections(new ArrayList<BookCollection>());
					
					numberOfBookCollections = numberOfBookCollections + 1;
					
					bookCollection.setNumber(numberOfBookCollections);
			        
			        bookCollection = bookCollectionService.createBookCollection(bookCollection);
				} else {
					logger.info("update bookCollection " + path);
					
					bookCollection.setUpdateDate(updateDate);
					
					String name = NameHelper.getName(file.getName());
					
					bookCollection.setName(name);
					
					String normalizedName = NameHelper.getNormalizedName(file.getName());
					
					bookCollection.setNormalizedName(normalizedName);
					bookCollection.setNumberOfBookCollections(0);
					bookCollection.setNumberOfBooks(0);
					bookCollection.setChildBookCollections(new ArrayList<BookCollection>());
					
					numberOfBookCollections = numberOfBookCollections + 1;
					
					bookCollection.setNumber(numberOfBookCollections);
					
					bookCollection = bookCollectionService.updateBookCollection(bookCollection);
				}
				
				childBookCollections.add(bookCollection);
				
				List<BookCollection> bookCollections = add(rootBookCollection, bookCollection, file);
				
				childBookCollections.addAll(bookCollections);
			} else {
				FileType fileType = FileType.getFileType(file);
				
				FileWrapper<File> fileWrapper = new FileWrapper<File>(file, fileType);
				
				if(FileType.ZIP.equals(fileType) || FileType.RAR.equals(fileType) || FileType.RAR5.equals(fileType)) {
					Book book = bookService.getBookByBookCollectionIdAndFilePath(rootBookCollection.getId(), path);
					
					Date fileUpdateDate = new Date(file.lastModified());
					
					if(book == null) {
						logger.info("create book " + path);
						
						book = new Book();
						
						String fileId = createFileId(fileWrapper);
				    	
				    	book.setFileId(fileId);
				    	book.setFilePath(file.getPath());
				    	
				    	book.setRootBookCollection(rootBookCollection);
				    	book.setBookCollection(parentBookCollection);
						
						try {
							book = createBookPages(fileWrapper, book);
						} catch(Exception e) {
							logger.error("error create book " + path, e);
							continue;
						}
				    	
				    	book.setUpdateDate(updateDate);
				    	
						String name = NameHelper.getName(file.getName());
						
						book.setName(name);
						
						String normalizedName = NameHelper.getNormalizedName(file.getName());
						
						book.setNormalizedName(normalizedName);
						
						numberOfBooks = numberOfBooks + 1;
						
						book.setNumber(numberOfBooks);
						
						book = bookService.createBook(book);
						
						List<BookMark> bookMarkList = bookMarkService.getBookMarksByFileId(book.getFileId());
						for(BookMark bookMark: bookMarkList) {
							logger.info("create bookMarkReference");
							
							BookMarkReference bookMarkReference = new BookMarkReference();
							bookMarkReference.setUser(bookMark.getUser());
							bookMarkReference.setFileId(bookMark.getFileId());
							bookMarkReference.setUpdateDate(updateDate);
							bookMarkReference.setBook(book);
							bookMarkReference.setBookMark(bookMark);
							bookMarkReference.setRootBookCollection(rootBookCollection);
							
							bookMarkReference = bookMarkService.createBookMarkReference(bookMarkReference);
						}
					} else {
						logger.info("update book " + path);
						
						try {
							if(book.getUpdateDate().compareTo(fileUpdateDate) < 0) {
								book = createBookPages(fileWrapper, book);
							} else {
								book = updateBookPages(fileWrapper, book);
							}
						} catch(Exception e) {
							logger.error("error update book " + path, e);
							continue;
						}
						
						book.setUpdateDate(updateDate);
				    	
						String name = NameHelper.getName(file.getName());
						
						book.setName(name);
						
						String normalizedName = NameHelper.getNormalizedName(file.getName());
						
						book.setNormalizedName(normalizedName);
						
						numberOfBooks = numberOfBooks + 1;
						
						book.setNumber(numberOfBooks);
						
						book = bookService.updateBook(book);
						
						List<BookMarkReference> bookMarkReferenceList = bookMarkService.getBookMarkReferencesByFileId(book.getFileId());
						for(BookMarkReference bookMarkReference: bookMarkReferenceList) {
							logger.info("update bookMarkReference");
							
							bookMarkReference.setUpdateDate(updateDate);
							
							bookMarkReference = bookMarkService.updateBookMarkReference(bookMarkReference);
						}
					}
				}
			}
		}
		
		logger.info("update parentBookCollection");
		
		parentBookCollection.setUpdateDate(updateDate);
		parentBookCollection.setNumberOfBookCollections(numberOfBookCollections);
		parentBookCollection.setNumberOfBooks(numberOfBooks);
		parentBookCollection.setChildBookCollections(childBookCollections);
		
		parentBookCollection = bookCollectionService.updateBookCollection(parentBookCollection);
		
		return childBookCollections;
    }
    
    private Book createBookPages(FileWrapper<File> bookInputFileWrapper, Book book) throws Exception {
		List<BookPage> bookPageList = new ArrayList<BookPage>();
    	for(BookPage defaultBookPage: defaultBookPageList) {
    		Integer defaultPage;
    		Integer defaultLastPage;
    		
    		if(defaultBookPage.getPage() != null) {
    			defaultPage = defaultBookPage.getPage();
        		defaultLastPage = defaultPage;
    		} else {
    			defaultPage = 1;
        		defaultLastPage = book.getNumberOfPages();
    		}
    		
    		while(defaultPage <= defaultLastPage) {
	    		for(BookPageConfiguration defaultBookPageConfiguration: defaultBookPage.getBookPageConfigurationList()) {
		    		BookPage bookPage = getBookPage(bookPageList, defaultPage);
		    		if(bookPage == null) {
			    		bookPage = new BookPage();
						bookPage.setPage(defaultPage);
						
						bookPageList.add(bookPage);
		    		}
		    		
		    		bookPage.getBookPageConfigurationList().add(defaultBookPageConfiguration);
	    		}
	    		
	    		defaultPage = defaultPage + 1;
    		}
    	}
		
    	PluginManager pluginManager = PluginManager.getInstance();
		
		ArchiveReaderFactory archiveReaderFactory = pluginManager.getFactory(ArchiveReaderFactory.class);
    	ArchiveReader archiveReader = null;
		try {
			archiveReader = archiveReaderFactory.getArchiveReader(bookInputFileWrapper.getFileType());
			archiveReader.openArchive(bookInputFileWrapper);
			
			Integer numberOfPages = archiveReader.readSize();
			
			book.setNumberOfPages(numberOfPages);
			
			for(BookPage bookPage: bookPageList) {
				FileWrapper<File> bookPageInputFileWrapper = null;
				try {
					bookPageInputFileWrapper = archiveReader.readFile(bookPage.getPage() - 1);
					
					for(BookPageConfiguration bookPageConfiguration: bookPage.getBookPageConfigurationList()) {
						FileWrapper<File> bookPageOutputFileWrapper = createBookPageFileWrapper(
			    				book, 
			    				bookPage.getPage(), 
			    				bookPageConfiguration.getScaleType(), 
			    				bookPageConfiguration.getScaleWidth(), 
			    				bookPageConfiguration.getScaleHeight()
			    		);
						
						if(FileType.JPG.equals(bookPageInputFileWrapper.getFileType()) 
								&& bookPageConfiguration.getScaleType() == null 
								&& bookPageConfiguration.getScaleWidth() == null 
								&& bookPageConfiguration.getScaleHeight() == null) {
							createBookPage(bookPageInputFileWrapper, bookPageOutputFileWrapper);
						} else {
							FileWrapper<File> bookPageInputFileWrapper2 = null;
							try {
								bookPageInputFileWrapper2 = createBookPage(
										bookPageInputFileWrapper, 
										bookPage.getPage(), 
										bookPageConfiguration.getScaleType(), 
										bookPageConfiguration.getScaleWidth(), 
										bookPageConfiguration.getScaleHeight()
								);
								
								createBookPage(bookPageInputFileWrapper2, bookPageOutputFileWrapper);
							} finally {
								try {
									if(bookPageInputFileWrapper2 != null) {
										File bookPageInputFile2 = bookPageInputFileWrapper2.getFile();
										if(bookPageInputFile2.isFile()) {
											bookPageInputFile2.delete();
										}
									}
								} catch(Exception e) {
									// pass
								}
							}
						}
					}
				} finally {
					try {
						if(bookPageInputFileWrapper != null) {
							File bookPageInputFile = bookPageInputFileWrapper.getFile();
							if(bookPageInputFile.isFile()) {
								bookPageInputFile.delete();
							}
						}
					} catch(Exception e) {
						// pass
					}
				}
			}
		} finally {
			try {
				if(archiveReader != null) {
					archiveReader.closeArchive();
				}
			} catch(Exception e) {
				// pass
			}
		}
		
		return book;
    }
    
    private Book updateBookPages(FileWrapper<File> bookInputFileWrapper, Book book) throws Exception {
    	List<BookPage> bookPageList = new ArrayList<BookPage>();
    	for(BookPage defaultBookPage: defaultBookPageList) {
    		Integer defaultPage;
    		Integer defaultLastPage;
    		
    		if(defaultBookPage.getPage()  != null) {
    			defaultPage = defaultBookPage.getPage();
        		defaultLastPage = defaultPage;
    		} else {
    			defaultPage = 1;
        		defaultLastPage = book.getNumberOfPages();
    		}
    		
    		while(defaultPage <= defaultLastPage) {
	    		for(BookPageConfiguration defaultBookPageConfiguration: defaultBookPage.getBookPageConfigurationList()) {
		    		FileWrapper<File> bookPageOutputFileWrapper = createBookPageFileWrapper(
		    				book, 
		    				defaultPage, 
		    				defaultBookPageConfiguration.getScaleType(), 
		    				defaultBookPageConfiguration.getScaleWidth(), 
		    				defaultBookPageConfiguration.getScaleHeight()
		    		);
			    	
			    	if(bookPageOutputFileWrapper.getFile().isFile()) {
			    		updateBookPage(bookPageOutputFileWrapper);
			    	} else {
			    		BookPage bookPage = getBookPage(bookPageList, defaultPage);
			    		if(bookPage == null) {
				    		bookPage = new BookPage();
							bookPage.setPage(defaultPage);
							
							bookPageList.add(bookPage);
			    		}
			    		
			    		bookPage.getBookPageConfigurationList().add(defaultBookPageConfiguration);
			    	}
	    		}
	    		
	    		defaultPage = defaultPage + 1;
    		}
    	}
    	
    	if(bookPageList.size() > 0) {
    		PluginManager pluginManager = PluginManager.getInstance();
    		
    		ArchiveReaderFactory archiveReaderFactory = pluginManager.getFactory(ArchiveReaderFactory.class);
	    	ArchiveReader archiveReader = null;
			try {
				archiveReader = archiveReaderFactory.getArchiveReader(bookInputFileWrapper.getFileType());
				archiveReader.openArchive(bookInputFileWrapper);
				
				for(BookPage bookPage: bookPageList) {
					FileWrapper<File> bookPageInputFileWrapper = null;
					try {
						bookPageInputFileWrapper = archiveReader.readFile(bookPage.getPage() - 1);
						
						for(BookPageConfiguration bookPageConfiguration: bookPage.getBookPageConfigurationList()) {
							FileWrapper<File> bookPageOutputFileWrapper = createBookPageFileWrapper(
				    				book, 
				    				bookPage.getPage(), 
				    				bookPageConfiguration.getScaleType(), 
				    				bookPageConfiguration.getScaleWidth(), 
				    				bookPageConfiguration.getScaleHeight()
				    		);
							
							if(FileType.JPG.equals(bookPageInputFileWrapper.getFileType()) 
									&& bookPageConfiguration.getScaleType() == null 
									&& bookPageConfiguration.getScaleWidth() == null 
									&& bookPageConfiguration.getScaleHeight() == null) {
								createBookPage(bookPageInputFileWrapper, bookPageOutputFileWrapper);
							} else {
								FileWrapper<File> bookPageInputFileWrapper2 = null;
								try {
									bookPageInputFileWrapper2 = createBookPage(
											bookPageInputFileWrapper, 
											bookPage.getPage(), 
											bookPageConfiguration.getScaleType(), 
											bookPageConfiguration.getScaleWidth(), 
											bookPageConfiguration.getScaleHeight()
									);
									
									createBookPage(bookPageInputFileWrapper2, bookPageOutputFileWrapper);
								} finally {
									try {
										if(bookPageInputFileWrapper2 != null) {
											File bookPageInputFile2 = bookPageInputFileWrapper2.getFile();
											if(bookPageInputFile2.isFile()) {
												bookPageInputFile2.delete();
											}
										}
									} catch(Exception e) {
										// pass
									}
								}
							}
						}
					} finally {
						try {
							if(bookPageInputFileWrapper != null) {
								File bookPageInputFile = bookPageInputFileWrapper.getFile();
								if(bookPageInputFile.isFile()) {
									bookPageInputFile.delete();
								}
							}
						} catch(Exception e) {
							// pass
						}
					}
				}
			} finally {
				try {
					if(archiveReader != null) {
						archiveReader.closeArchive();
					}
				} catch(Exception e) {
					// pass
				}
			}
    	}
    	
    	return book;
    }
    
    private String createFileId(FileWrapper<File> bookInputFileWrapper) throws Exception {
    	PluginManager pluginManager = PluginManager.getInstance();
    	
    	HashManagerFactory hashManagerFactory = pluginManager.getFactory(HashManagerFactory.class);
    	HashManager hashManager = hashManagerFactory.getHashManager(HashType.SHA256);
    	
    	String fileId = hashManager.createHash(bookInputFileWrapper, HashType.SHA256);
    	
    	return fileId;
    }
    
    private FileWrapper<File> createBookPageFileWrapper(Book book, Integer page, ScaleType scaleType, Integer scaleWidth, Integer scaleHeight) throws Exception {
    	String dataDirectoryPath = getConfiguration().getAsString("application.data.path", "./data");
    	
    	String bookPageFilePath = book.getFileId().substring(0, 2) + "/" + book.getFileId().substring(2) + "/" + page;
        if(scaleType != null) {
        	bookPageFilePath = bookPageFilePath + "-scaleType" + scaleType;
        }
        if(scaleWidth != null) {
        	bookPageFilePath = bookPageFilePath + "-scaleWidth" + scaleWidth;
        }
        if(scaleHeight != null) {
        	bookPageFilePath = bookPageFilePath + "-scaleHeight" + scaleHeight;
        }
        bookPageFilePath = bookPageFilePath + ".jpg";
        
		File bookPageFile = new File(dataDirectoryPath, bookPageFilePath);
		FileType bookPageFileType = FileType.JPG;
		
		FileWrapper<File> bookPageFileWrapper = new FileWrapper<File>(bookPageFile, bookPageFileType);
		
		return bookPageFileWrapper;
    }
    
    private FileWrapper<File> createBookPage(FileWrapper<File> bookPageInputFileWrapper, Integer page, ScaleType scaleType, Integer scaleWidth, Integer scaleHeight) throws Exception {
    	PluginManager pluginManager = PluginManager.getInstance();
		
		ImageManagerFactory imageManagerFactory = pluginManager.getFactory(ImageManagerFactory.class);
    	ImageManager imageManager = imageManagerFactory.getImageManager(bookPageInputFileWrapper.getFileType(), FileType.JPG);
		
    	FileWrapper<File> bookPageOutputFileWrapper = imageManager.createImage(bookPageInputFileWrapper, FileType.JPG, scaleType, scaleWidth, scaleHeight);
		
		return bookPageOutputFileWrapper;
	}
    
    private void createBookPage(FileWrapper<File> bookPageInputFileWrapper, FileWrapper<File> bookPageOutputFileWrapper) throws Exception {
		File bookPageOutputFile = bookPageOutputFileWrapper.getFile();
		File bookPageOutputDirectory = bookPageOutputFile.getParentFile();
		File bookPageOutputDirectory2 = bookPageOutputDirectory.getParentFile();
		
		if(bookPageOutputDirectory2.isDirectory() == false) {
			bookPageOutputDirectory2.mkdir();
		}
		
		if(bookPageOutputDirectory.isDirectory() == false) {
			bookPageOutputDirectory.mkdir();
		}
    	
		InputStream bookPageInputStream = null;
		OutputStream bookPageOutputStream = null;
		try {
			bookPageInputStream = new FileInputStream(bookPageInputFileWrapper.getFile());
			bookPageOutputStream = new FileOutputStream(bookPageOutputFile);
			
			byte[] buffer = new byte[8 * 1024];
		    int bufferSize;
		    while ((bufferSize = bookPageInputStream.read(buffer)) != -1) {
		    	bookPageOutputStream.write(buffer, 0, bufferSize);
		    }
		} finally {
			try {
				if(bookPageOutputStream != null) {
					bookPageOutputStream.close();
				}
			} catch(Exception e) {
				// pass
			}
			
			try {
				if(bookPageInputStream != null) {
					bookPageInputStream.close();
				}
			} catch(Exception e) {
				// pass
			}
		}
		
		bookPageOutputFile.setLastModified(updateDate.getTime());
		bookPageOutputDirectory.setLastModified(updateDate.getTime());
		bookPageOutputDirectory2.setLastModified(updateDate.getTime());
	}
    
    private void updateBookPage(FileWrapper<File> bookPageOutputFileWrapper) throws Exception {
    	File bookPageOutputFile = bookPageOutputFileWrapper.getFile();
		File bookPageOutputDirectory = bookPageOutputFile.getParentFile();
		File bookPageOutputDirectory2 = bookPageOutputDirectory.getParentFile();
		
		bookPageOutputFile.setLastModified(updateDate.getTime());
		bookPageOutputDirectory.setLastModified(updateDate.getTime());
		bookPageOutputDirectory2.setLastModified(updateDate.getTime());
    }
    
    private void deleteBookPageByUpdateDate() throws Exception {
    	String directoryPath = getConfiguration().getAsString("application.data.path", "./data");
    	
    	File directory = new File(directoryPath);
    	if(directory.isDirectory()) {
    		File[] bookPageDirectoryList = directory.listFiles();
    		
			for(File bookPageDirectory: bookPageDirectoryList) {
				if(status.equals(BookScannerServiceStatus.STOPPING)) {
		    		logger.info("stopping!");
		    		break;
				}
				
				if(bookPageDirectory.isDirectory()) {
					Date bookPageDirectoryUpdateDate = new Date(bookPageDirectory.lastModified());
					
					File[] bookPageDirectoryList2 = bookPageDirectory.listFiles();
					
					for(File bookPageDirectory2: bookPageDirectoryList2) {
						if(status.equals(BookScannerServiceStatus.STOPPING)) {
				    		logger.info("stopping!");
				    		break;
						}
						
						if(bookPageDirectory2.isDirectory()) {
							Date bookPageDirectoryUpdateDate2 = new Date(bookPageDirectory2.lastModified());
							
							File[] bookPageFileList = bookPageDirectory2.listFiles();
							
							for(File bookPageFile: bookPageFileList) {
								if(bookPageFile.isFile()) {
									Date bookPageUpdateDate = new Date(bookPageFile.lastModified());
									
									if(bookPageUpdateDate.compareTo(updateDate) < 0) {
										bookPageFile.delete();
									}
								}
							}
							
							if(bookPageDirectoryUpdateDate2.compareTo(updateDate) < 0) {
								bookPageDirectory2.delete();
							}
						}
					}
					
					if(bookPageDirectoryUpdateDate.compareTo(updateDate) < 0) {
						bookPageDirectory.delete();
					}
				}
			}
    	}
    }
}
