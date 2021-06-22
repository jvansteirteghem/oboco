package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.gitlab.jeeto.oboco.api.v1.user.User;
import com.gitlab.jeeto.oboco.common.NameHelper;
import com.gitlab.jeeto.oboco.common.PageableList;
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
	
	public BookCollection getRootBookCollectionById(Long id) throws ProblemException {
		BookCollection rootBookCollection = null;
		
		try {
			rootBookCollection = entityManager.createQuery("select bc from BookCollection bc where bc.parentBookCollection.id is null and bc.id = :id", BookCollection.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return rootBookCollection;
	}
	
	public List<BookCollection> getRootBookCollections() throws ProblemException {
		List<BookCollection> rootBookCollectionList = entityManager.createQuery("select bc from BookCollection bc where bc.parentBookCollection.id is null order by bc.number asc", BookCollection.class)
				.getResultList();
		
        return rootBookCollectionList;
	}
	
	public BookCollection getBookCollectionByUserAndId(User user, Long id) throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId) and bc.id = :id", BookCollection.class)
					.setParameter("rootBookCollectionId", user.getRootBookCollection().getId())
					.setParameter("id", id)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public BookCollection getRootBookCollectionByName(String name) throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where bc.parentBookCollection.id is null and bc.name = :name", BookCollection.class)
					.setParameter("name", name)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public BookCollection getBookCollectionByBookCollectionIdAndDirectoryPath(Long rootBookCollectionId, String directoryPath) throws ProblemException {
		BookCollection bookCollection = null;
		
		try {
			bookCollection = entityManager.createQuery("select bc from BookCollection bc where (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId) and bc.directoryPath = :directoryPath", BookCollection.class)
					.setParameter("rootBookCollectionId", rootBookCollectionId)
					.setParameter("directoryPath", directoryPath)
					.getSingleResult();
		} catch(NoResultException e) {
			
		}
		
        return bookCollection;
	}
	
	public PageableList<BookCollection> getBookCollectionsByUser(User user, Integer page, Integer pageSize) throws ProblemException {
		Long bookCollectionListSize = (Long) entityManager.createQuery("select count(bc.id) from BookCollection bc where (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId)")
				.setParameter("rootBookCollectionId", user.getRootBookCollection().getId())
				.getSingleResult();
	
		List<BookCollection> bookCollectionList = entityManager.createQuery("select bc from BookCollection bc where (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId) order by bc.number asc", BookCollection.class)
				.setParameter("rootBookCollectionId", user.getRootBookCollection().getId())
				.setFirstResult((page - 1) * pageSize)
				.setMaxResults(pageSize)
				.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollectionsByUserAndName(User user, String name, Integer page, Integer pageSize) throws ProblemException {
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookCollectionListQueryString = " where 1 = 1";
		
		bookCollectionListQueryString = bookCollectionListQueryString + " and (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId)";
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.normalizedName like :normalizedName";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		bookCollectionListSizeQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString + " order by bc.number asc", BookCollection.class);
		bookCollectionListQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<BookCollection> bookCollectionList = bookCollectionListQuery.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollectionsByUser(User user, Long parentBookCollectionId, Integer page, Integer pageSize) throws ProblemException {
		String bookCollectionListQueryString = " where 1 = 1 ";
		
		bookCollectionListQueryString = bookCollectionListQueryString + " and (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId)";
		
		if(parentBookCollectionId == null) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.parentBookCollection.id is null";
		} else {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.parentBookCollection.id = :parentBookCollectionId";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		bookCollectionListSizeQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(parentBookCollectionId != null) {
			bookCollectionListSizeQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString + " order by bc.number asc", BookCollection.class);
		bookCollectionListQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(parentBookCollectionId != null) {
			bookCollectionListQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<BookCollection> bookCollectionList = bookCollectionListQuery.getResultList();
        
        PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
        
        return bookCollectionPageableList;
	}
	
	public PageableList<BookCollection> getBookCollectionsByUserAndName(User user, Long parentBookCollectionId, String name, Integer page, Integer pageSize) throws ProblemException {
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookCollectionListQueryString = " where 1 = 1";
		
		bookCollectionListQueryString = bookCollectionListQueryString + " and (bc.id = :rootBookCollectionId or bc.rootBookCollection.id = :rootBookCollectionId)";
		
		if(parentBookCollectionId != null) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.id in (select cbc.id from BookCollection pbc join pbc.childBookCollections cbc where pbc.id = :parentBookCollectionId)";
		}
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bc.normalizedName like :normalizedName";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(bc.id) from BookCollection bc" + bookCollectionListQueryString);
		bookCollectionListSizeQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(parentBookCollectionId != null) {
			bookCollectionListSizeQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		TypedQuery<BookCollection> bookCollectionListQuery = entityManager.createQuery("select bc from BookCollection bc" + bookCollectionListQueryString + " order by bc.number asc", BookCollection.class);
		bookCollectionListQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		
		if(parentBookCollectionId != null) {
			bookCollectionListQuery.setParameter("parentBookCollectionId", parentBookCollectionId);
		}
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
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
	
	@SuppressWarnings("unchecked")
	public PageableList<BookCollection> getLatestBookCollectionsByUserAndName(User user, String name, Integer page, Integer pageSize) throws ProblemException {
		String normalizedName = NameHelper.getNormalizedName(name);
		
		String bookCollectionListQueryString = " where 1 = 1";
		
		bookCollectionListQueryString = bookCollectionListQueryString + " and bmr.rootBookCollection.id = :rootBookCollectionId and bmr.user.id = :userId";
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListQueryString = bookCollectionListQueryString + " and bmr.bookCollection.normalizedName like :normalizedName";
		}
		
		Query bookCollectionListSizeQuery = entityManager.createQuery("select count(distinct bmr.bookCollection.id) from BookMarkReference bmr" + bookCollectionListQueryString);
		bookCollectionListSizeQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		bookCollectionListSizeQuery.setParameter("userId", user.getId());
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListSizeQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		Long bookCollectionListSize = (Long) bookCollectionListSizeQuery.getSingleResult();
		
		Query bookCollectionListQuery = entityManager.createQuery("select bmr.bookCollection, max(bmr.bookMark.updateDate) from BookMarkReference bmr" + bookCollectionListQueryString + " group by bmr.bookCollection order by max(bmr.bookMark.updateDate) desc");
		bookCollectionListQuery.setParameter("rootBookCollectionId", user.getRootBookCollection().getId());
		bookCollectionListQuery.setParameter("userId", user.getId());
		
		if(normalizedName != null && "".equals(normalizedName) == false) {
			bookCollectionListQuery.setParameter("normalizedName", "%" + normalizedName + "%");
		}
		
		bookCollectionListQuery.setFirstResult((page - 1) * pageSize);
		bookCollectionListQuery.setMaxResults(pageSize);
		
		List<Object[]> bookCollectionObjectList = (List<Object[]>) bookCollectionListQuery.getResultList();
		
		List<BookCollection> bookCollectionList = new ArrayList<BookCollection>();
		
		for(Object[] bookCollectionObject: bookCollectionObjectList) {
			BookCollection bookCollection = (BookCollection) bookCollectionObject[0];
			
			bookCollectionList.add(bookCollection);
		}
		
		PageableList<BookCollection> bookCollectionPageableList = new PageableList<BookCollection>(bookCollectionList, bookCollectionListSize, page, pageSize);
		
		return bookCollectionPageableList;
	}
}
