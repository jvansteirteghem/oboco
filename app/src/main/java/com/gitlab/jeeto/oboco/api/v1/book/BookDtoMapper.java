package com.gitlab.jeeto.oboco.api.v1.book;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollection;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionDto;
import com.gitlab.jeeto.oboco.api.v1.bookcollection.BookCollectionDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkReference;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkDto;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkDtoMapper;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookMarkService;
import com.gitlab.jeeto.oboco.api.v1.user.User;
import com.gitlab.jeeto.oboco.common.GraphDto;
import com.gitlab.jeeto.oboco.common.Linkable;
import com.gitlab.jeeto.oboco.common.LinkableDto;
import com.gitlab.jeeto.oboco.common.PageableList;
import com.gitlab.jeeto.oboco.common.PageableListDto;
import com.gitlab.jeeto.oboco.common.exception.Problem;
import com.gitlab.jeeto.oboco.common.exception.ProblemException;
import com.gitlab.jeeto.oboco.common.security.authentication.UserPrincipal;

public class BookDtoMapper {
	@Context
    private SecurityContext securityContext;
	private BookCollectionDtoMapper bookCollectionDtoMapper;
	@Inject
	private Provider<BookCollectionDtoMapper> bookCollectionDtoMapperProvider;
	
	private BookCollectionDtoMapper getBookCollectionDtoMapper() {
		if(bookCollectionDtoMapper == null) {
			bookCollectionDtoMapper = bookCollectionDtoMapperProvider.get();
		}
		return bookCollectionDtoMapper;
	}
	
	@Inject
	private BookMarkService bookMarkService;
	
	private BookMarkService getBookMarkService() {
		return bookMarkService;
	}
	
	private BookMarkDtoMapper bookMarkDtoMapper;
	@Inject
	private Provider<BookMarkDtoMapper> bookMarkDtoMapperProvider;
	
	private BookMarkDtoMapper getBookMarkDtoMapper() {
		if(bookMarkDtoMapper == null) {
			bookMarkDtoMapper = bookMarkDtoMapperProvider.get();
		}
		return bookMarkDtoMapper;
	}
	
	public BookDto getBookDto(Book book, GraphDto graphDto) throws ProblemException {
		BookDto bookDto = null;
		if(book != null) {
			bookDto = new BookDto();
			bookDto.setId(book.getId());
			bookDto.setUpdateDate(book.getUpdateDate());
			bookDto.setName(book.getName());
			bookDto.setNumberOfPages(book.getNumberOfPages());
			
			if(graphDto != null) {
				if(graphDto.containsKey("bookCollection")) {
					GraphDto nestedGraphDto = graphDto.get("bookCollection");
					
					BookCollection bookCollection = book.getBookCollection();
					BookCollectionDto bookCollectionDto = getBookCollectionDtoMapper().getBookCollectionDto(bookCollection, nestedGraphDto);
					
					bookDto.setBookCollection(bookCollectionDto);
				}
				
				if(graphDto.containsKey("bookMark")) {
					GraphDto nestedGraphDto = graphDto.get("bookMark");
					
					User user = ((UserPrincipal) securityContext.getUserPrincipal()).getUser();
					
					if(user.getRootBookCollection() == null) {
						throw new ProblemException(new Problem(404, "PROBLEM_USER_ROOT_BOOK_COLLECTION_NOT_FOUND", "The user.rootBookCollection is not found."));
					}
					
					BookMarkReference bookMarkReference = getBookMarkService().getBookMarkReferenceByUserAndBook(user, book);
					BookMarkDto bookMarkDto = getBookMarkDtoMapper().getBookMarkDto(bookMarkReference, nestedGraphDto);
					
					bookDto.setBookMark(bookMarkDto);
				}
			}
		}
		
		return bookDto;
	}
	
	public List<BookDto> getBooksDto(List<Book> bookList, GraphDto graphDto) throws ProblemException {
		List<BookDto> bookListDto = null;
		if(bookList != null) {
			bookListDto = new ArrayList<BookDto>();
			
			for(Book book: bookList) {
				BookDto bookDto = getBookDto(book, graphDto);
				
				bookListDto.add(bookDto);
			}
		}
		
		return bookListDto;
	}
	
	public LinkableDto<BookDto> getBooksDto(Linkable<Book> bookLinkable, GraphDto graphDto) throws ProblemException {
		LinkableDto<BookDto> bookLinkableDto = null;
		if(bookLinkable != null) {
			bookLinkableDto = new LinkableDto<BookDto>();
			
			Book book = bookLinkable.getElement();
			BookDto bookDto = getBookDto(book, graphDto);
			
			bookLinkableDto.setElement(bookDto);
			
			Book previousBook = bookLinkable.getPreviousElement();
			BookDto previousBookDto = getBookDto(previousBook, graphDto);
			
			bookLinkableDto.setPreviousElement(previousBookDto);
			
			Book nextBook = bookLinkable.getNextElement();
			BookDto nextBookDto = getBookDto(nextBook, graphDto);
			
			bookLinkableDto.setNextElement(nextBookDto);
		}
		
		return bookLinkableDto;
	}
	
	public PageableListDto<BookDto> getBooksDto(PageableList<Book> bookPageableList, GraphDto graphDto) throws ProblemException {
		PageableListDto<BookDto> bookPageableListDto = null;
		if(bookPageableList != null) {
			bookPageableListDto = new PageableListDto<BookDto>();
			
			List<BookDto> bookListDto = new ArrayList<BookDto>();
			for(Book book: bookPageableList.getElements()) {
				BookDto bookDto = getBookDto(book, graphDto);
				
				bookListDto.add(bookDto);
			}
			bookPageableListDto.setElements(bookListDto);
			bookPageableListDto.setNumberOfElements(bookPageableList.getNumberOfElements());
			bookPageableListDto.setPage(bookPageableList.getPage());
			bookPageableListDto.setPageSize(bookPageableList.getPageSize());
			bookPageableListDto.setFirstPage(bookPageableList.getFirstPage());
			bookPageableListDto.setLastPage(bookPageableList.getLastPage());
			bookPageableListDto.setPreviousPage(bookPageableList.getPreviousPage());
			bookPageableListDto.setNextPage(bookPageableList.getNextPage());
		}
		
		return bookPageableListDto;
	}
}
