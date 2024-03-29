package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.api.v1.book.BookDto;
import com.gitlab.jeeto.oboco.api.v1.book.BookPageableListDto;
import com.gitlab.jeeto.oboco.api.v1.bookmark.BookCollectionMarkDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookCollection", description = "A bookCollection.")
@XmlRootElement(name = "BookCollection")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCollectionDto {
	private Long id;
	private Date createDate;
	private Date updateDate;
	private String name;
	private BookCollectionDto parentBookCollection;
	private List<BookCollectionDto> bookCollections;
	private Integer numberOfBookCollections;
	private List<BookDto> books;
	private Integer numberOfBooks;
	private Integer numberOfBookPages;
	private BookCollectionMarkDto bookCollectionMark;
	public BookCollectionDto() {
		super();
	}
	@Schema(name = "id")
	@XmlElement(name = "id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Schema(name = "createDate")
	@XmlElement(name = "createDate")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Schema(name = "updateDate")
	@XmlElement(name = "updateDate")
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	@Schema(name = "name")
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Schema(name = "parentBookVolleaction")
	@XmlElement(name = "parentBookCollection")
	public BookCollectionDto getParentBookCollection() {
		return parentBookCollection;
	}
	public void setParentBookCollection(BookCollectionDto parentBookCollection) {
		this.parentBookCollection = parentBookCollection;
	}
	@Schema(name = "bookCollections", implementation = BookCollectionPageableListDto.class)
	@XmlElement(name = "bookCollections")
	public List<BookCollectionDto> getBookCollections() {
		return bookCollections;
	}
	public void setBookCollections(List<BookCollectionDto> bookCollections) {
		this.bookCollections = bookCollections;
	}
	@Schema(name = "numberOfBookCollections")
	@XmlElement(name = "numberOfBookCollections")
	public Integer getNumberOfBookCollections() {
		return numberOfBookCollections;
	}
	public void setNumberOfBookCollections(Integer numberOfBookCollections) {
		this.numberOfBookCollections = numberOfBookCollections;
	}
	@Schema(name = "books", implementation = BookPageableListDto.class)
	@XmlElement(name = "books")
	public List<BookDto> getBooks() {
		return books;
	}
	public void setBooks(List<BookDto> books) {
		this.books = books;
	}
	@Schema(name = "numberOfBooks")
	@XmlElement(name = "numberOfBooks")
	public Integer getNumberOfBooks() {
		return numberOfBooks;
	}
	public void setNumberOfBooks(Integer numberOfBooks) {
		this.numberOfBooks = numberOfBooks;
	}
	@Schema(name = "numberOfBookPages")
	@XmlElement(name = "numberOfBookPages")
	public Integer getNumberOfBookPages() {
		return numberOfBookPages;
	}
	public void setNumberOfBookPages(Integer numberOfBookPages) {
		this.numberOfBookPages = numberOfBookPages;
	}
	@Schema(name = "bookCollectionMark")
	@XmlElement(name = "bookCollectionMark")
	public BookCollectionMarkDto getBookCollectionMark() {
		return bookCollectionMark;
	}
	public void setBookCollectionMark(BookCollectionMarkDto bookCollectionMark) {
		this.bookCollectionMark = bookCollectionMark;
	}
}
