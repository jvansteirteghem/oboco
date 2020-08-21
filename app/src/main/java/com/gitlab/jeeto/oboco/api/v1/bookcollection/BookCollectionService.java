package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.NameHelper;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;

public class BookCollectionService {
	@Inject
	private EntityManager entityManager;
	
	public BookCollectionService() {
		super();
	}
	
	public BookCollection createBookCollection(BookCollection bookCollection) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.persist(bookCollection);
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
		
        return bookCollection;
	}
	
	public BookCollection updateBookCollection(BookCollection bookCollection) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			bookCollection = entityManager.merge(bookCollection);
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
		
        return bookCollection;
	}
	
	public BookCollection getRootBookCollection() throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where bc.parentBookCollection.id is null", BookCollection.class)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public BookCollection getBookCollectionById(Long id) throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where bc.id = :id", BookCollection.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public BookCollection getBookCollectionByDirectoryPath(String directoryPath) throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where bc.directoryPath = :directoryPath", BookCollection.class)
					.setParameter("directoryPath", directoryPath)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public PageableList<BookCollection> getBookCollections(Integer page, Integer pageSize) throws ProblemException {
		Long bookCollectionListSize = (Long) entityManager.createQuery("select count(bc.id) from BookCollection bc")
				.getSingleResult();
	
		List<BookCollection> bookCollectionList = entityManager.createQuery("select bc from BookCollection bc", BookCollection.class)
				.setFirstResult((page - 1) * pageSize)
				.setMaxResults(pageSize)
				.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollections(String name, Integer page, Integer pageSize) throws ProblemException {
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookCollectionListQueryString = " where 1 = 1";
		
		if("".equals(normalizedName) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.normalizedName like :normalizedName";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		
		if("".equals(normalizedName) == false) {
			bookCollectionListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString, BookCollection.class);
		
		if("".equals(normalizedName) == false) {
			bookCollectionListQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<BookCollection> bookCollectionList = bookCollectionListQuery.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollectionsByParentBookCollectionId(Long parentBookCollectionId, Integer page, Integer pageSize) throws ProblemException {
		String bookCollectionListQueryString = " where 1 = 1 ";
		
		if(parentBookCollectionId == null) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.parentBookCollection.id is null";
		} else {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.parentBookCollection.id = :parentBookCollectionId";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		
		if(parentBookCollectionId != null) {
			bookCollectionListSizeQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString, BookCollection.class);
		
		if(parentBookCollectionId != null) {
			bookCollectionListQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<BookCollection> bookCollectionList = bookCollectionListQuery.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollectionsByParentBookCollectionId(Long parentBookCollectionId, String name, Integer page, Integer pageSize) throws ProblemException {
		String directoryPath = "";
		
		if(parentBookCollectionId != null) {
			BookCollection parentBookCollection = getBookCollectionById(parentBookCollectionId);
			
			if(parentBookCollection != null) {
				directoryPath = parentBookCollection.getDirectoryPath();
				directoryPath = directoryPath.replaceAll("[\\\\]", "\\\\\\\\");
				directoryPath = directoryPath.replaceAll("[\\%]", "\\\\%");
				directoryPath = directoryPath.replaceAll("[\\_]", "\\\\_");
			}
		}
		
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookCollectionListQueryString = " where 1 = 1";
		
		if("".equals(directoryPath) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.directoryPath like :directoryPath";
		}
		
		if("".equals(normalizedName) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.normalizedName like :normalizedName";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		
		if("".equals(directoryPath) == false) {
			bookCollectionListSizeQuery.setParameter("directoryPath", directoryPath + "%");
		}
		
		if("".equals(normalizedName) == false) {
			bookCollectionListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString, BookCollection.class);
		
		if("".equals(directoryPath) == false) {
			bookCollectionListQuery.setParameter("directoryPath", directoryPath + "%");
		}
		
		if("".equals(normalizedName) == false) {
			bookCollectionListQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<BookCollection> bookCollectionList = bookCollectionListQuery.getResultList();
		
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public void deleteBookCollection() throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.createQuery("delete from BookCollection")
				.executeUpdate();
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
	}
	
	public void deleteBookCollectionByUpdateDate(Date updateDate) throws ProblemException {
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try {
			entityManager.createQuery("delete from BookCollection bc where bc.updateDate != :updateDate")
				.setParameter("updateDate", updateDate)
				.executeUpdate();
			
			entityTransaction.commit();
		} catch(Exception e) {
			entityTransaction.rollback();
			
			throw new ProblemException(new Problem(500, "PROBLEM", "Problem."), e);
		}
	}
}
