package com.gitlab.jeeto.oboco.api.v1.book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkStatus;
import com.gitlab.jeeto.oboco.common.NameHelper;
import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;

public class BookService {
	@Inject
	private EntityManager entityManager;
	
	public BookService() {
		super();
	}
	
	public Book createBook(Book book) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.persist(book);
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
		
        return book;
	}
	
	public Book updateBook(Book book) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			book = entityManager.merge(book);
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
		
        return book;
	}
	
	public Book getBookByBookCollectionIdAndId(Long rootBookCollectionId, Long id) throws ProblemException {
		Book book = null;
		
		try {
			book = entityManager.createQuery("select b from Book b where b.rootBookCollection.id = :rootBookCollectionId and b.id = :id", Book.class)
					.setParameter("rootBookCollectionId", rootBookCollectionId)
					.setParameter("id", id)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return book;
	}
	
	public Book getBookByUpdateDateAndFilePath(Date updateDate, String filePath) throws ProblemException {
		Book book = null;
		
		try {
			book = entityManager.createQuery("select b from Book b where b.updateDate = :updateDate and b.filePath = :filePath", Book.class)
					.setParameter("updateDate", updateDate)
					.setParameter("filePath", filePath)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return book;
	}
	
	public Book getBookByBookCollectionIdAndFilePath(Long rootBookCollectionId, String filePath) throws ProblemException {
		Book book = null;
		
		try {
			book = entityManager.createQuery("select b from Book b where b.rootBookCollection.id = :rootBookCollectionId and b.filePath = :filePath", Book.class)
					.setParameter("rootBookCollectionId", rootBookCollectionId)
					.setParameter("filePath", filePath)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return book;
	}
	
	public List<Book> getBooksByFileId(String fileId) throws ProblemException {
		List<Book> bookList = entityManager.createQuery("select b from Book b where b.fileId = :fileId", Book.class)
				.setParameter("fileId", fileId)
				.getResultList();
        
        return bookList;
	}
	
	public PageableList<Book> getBooksByBookCollectionId(Long rootBookCollectionId, Integer page, Integer pageSize) throws ProblemException {
		Long bookListSize = (Long) entityManager.createQuery("select count(b.id) from Book b where b.rootBookCollection.id = :rootBookCollectionId")
				.setParameter("rootBookCollectionId", rootBookCollectionId)
				.getSingleResult();
	
		List<Book> bookList = entityManager.createQuery("select b from Book b where b.rootBookCollection.id = :rootBookCollectionId", Book.class)
				.setParameter("rootBookCollectionId", rootBookCollectionId)
				.setFirstResult((page - 1) * pageSize)
				.setMaxResults(pageSize)
				.getResultList();
        
        PageableList<Book> bookPageableList = new PageableList<Book>(bookList, bookListSize, page, pageSize);
        
        return bookPageableList;
	}
	
	public PageableList<Book> getBooksByBookCollectionIdAndName(Long rootBookCollectionId, String name, Integer page, Integer pageSize) throws ProblemException {
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookListQueryString = " where 1 = 1";
		
		bookListQueryString = bookListQueryString + " and b.rootBookCollection.id = :rootBookCollectionId";
		
		if("".equals(normalizedName) == false) {
			bookListQueryString = bookListQueryString + " and b.normalizedName like :normalizedName";
		}
		
		Query bookListSizeQuery = entityManager.createQuery("select count(b.id) from Book b" + bookListQueryString);
		bookListSizeQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		
		if("".equals(normalizedName) == false) {
			bookListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookListSize = (Long) bookListSizeQuery.getSingleResult();
		
		TypedQuery<Book> bookListQuery = entityManager.createQuery("select b from Book b" + bookListQueryString, Book.class);
		bookListQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		
		if("".equals(normalizedName) == false) {
			bookListQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		bookListQuery.setFirstResult((page - 1) * pageSize);
		bookListQuery.setMaxResults(pageSize);
		
		List<Book> bookList = bookListQuery.getResultList();
        
        PageableList<Book> bookPageableList = new PageableList<Book>(bookList, bookListSize, page, pageSize);
        
        return bookPageableList;
	}
	
	public PageableList<Book> getBooksByBookCollectionIdAndId(Long rootBookCollectionId, Long bookCollectionId, Long id) throws ProblemException {
		TypedQuery<Book> bookListQuery = entityManager.createQuery("select b from Book b where b.rootBookCollection.id = :rootBookCollectionId and b.bookCollection.id = :bookCollectionId", Book.class);
		bookListQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		bookListQuery.setParameter("bookCollectionId", bookCollectionId);
		
		List<Book> bookList = bookListQuery.getResultList();
		
		List<Book> bookListById = new ArrayList<Book>();
		
		Integer index = 0;
		while(index < bookList.size()) {
			Book book = bookList.get(index);
			
			if(book.getId().equals(id)) {
				if(index - 1 >= 0) {
					bookListById.add(bookList.get(index - 1));
				}
				bookListById.add(book);
				if(index + 1 < bookList.size()) {
					bookListById.add(bookList.get(index + 1));
				}
				break;
			}
			
			index = index + 1;
		}
		
		PageableList<Book> bookPageableList = new PageableList<Book>(bookListById);
		
		return bookPageableList;
	}
	
	public PageableList<Book> getBooksByBookCollectionId(Long rootBookCollectionId, Long bookCollectionId, Integer page, Integer pageSize) throws ProblemException {
		String bookListQueryString = " where 1 = 1";
		
		bookListQueryString = bookListQueryString + " and b.rootBookCollection.id = :rootBookCollectionId";
		
		bookListQueryString = bookListQueryString + " and b.bookCollection.id = :bookCollectionId";
		
		Query bookListSizeQuery = entityManager.createQuery("select count(b.id) from Book b" + bookListQueryString);
		bookListSizeQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		bookListSizeQuery.setParameter("bookCollectionId", bookCollectionId);
		
		Long bookListSize = (Long) bookListSizeQuery.getSingleResult();
		
		TypedQuery<Book> bookListQuery = entityManager.createQuery("select b from Book b" + bookListQueryString, Book.class);
		bookListQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		bookListQuery.setParameter("bookCollectionId", bookCollectionId);
		bookListQuery.setFirstResult((page - 1) * pageSize);
		bookListQuery.setMaxResults(pageSize);
		
		List<Book> bookList = bookListQuery.getResultList();
        
        PageableList<Book> bookPageableList = new PageableList<Book>(bookList, bookListSize, page, pageSize);
        
        return bookPageableList;
	}
	
	public PageableList<Book> getBooksByBookCollectionIdAndUserIdAndBookMarkStatus(Long rootBookCollectionId, Long bookCollectionId, Long userId, BookMarkStatus bookMarkStatus, Integer page, Integer pageSize) throws ProblemException {
		String bookListQueryString = " where 1 = 1";
		
		bookListQueryString = bookListQueryString + " and b.rootBookCollection.id = :rootBookCollectionId";
		
		bookListQueryString = bookListQueryString + " and b.bookCollection.id = :bookCollectionId";
		
		if(BookMarkStatus.READ.equals(bookMarkStatus)) {
			bookListQueryString = bookListQueryString + " and b.id in (select bmr.book.id from BookMarkReference bmr where bmr.book.bookCollection.id = :bookCollectionId and bmr.user.id = :userId and bmr.book.numberOfPages = bmr.bookMark.page)";
		} else if(BookMarkStatus.UNREAD.equals(bookMarkStatus)) {
			bookListQueryString = bookListQueryString + " and b.id not in (select bmr.book.id from BookMarkReference bmr where bmr.book.bookCollection.id = :bookCollectionId and bmr.user.id = :userId)";
		} else if(BookMarkStatus.READING.equals(bookMarkStatus)) {
			bookListQueryString = bookListQueryString + " and b.id in (select bmr.book.id from BookMarkReference bmr where bmr.book.bookCollection.id = :bookCollectionId and bmr.user.id = :userId and bmr.book.numberOfPages <> bmr.bookMark.page)";
		}
		
		Query bookListSizeQuery = entityManager.createQuery("select count(b.id) from Book b" + bookListQueryString);
		bookListSizeQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		bookListSizeQuery.setParameter("bookCollectionId", bookCollectionId);
		bookListSizeQuery.setParameter("userId", userId);
		
		Long bookListSize = (Long) bookListSizeQuery.getSingleResult();
		
		TypedQuery<Book> bookListQuery = entityManager.createQuery("select b from Book b" + bookListQueryString, Book.class);
		bookListQuery.setParameter("rootBookCollectionId", rootBookCollectionId);
		bookListQuery.setParameter("bookCollectionId", bookCollectionId);
		bookListQuery.setParameter("userId", userId);
		bookListQuery.setFirstResult((page - 1) * pageSize);
		bookListQuery.setMaxResults(pageSize);
		
		List<Book> bookList = bookListQuery.getResultList();
        
        PageableList<Book> bookPageableList = new PageableList<Book>(bookList, bookListSize, page, pageSize);
        
        return bookPageableList;
	}
	
	public void deleteBook() throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.createQuery("delete from Book")
				.executeUpdate();
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
	}
	
	public void deleteBookByUpdateDate(Date updateDate) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.createQuery("delete from Book b where b.updateDate != :updateDate")
				.setParameter("updateDate", updateDate)
				.executeUpdate();
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
	}
}
