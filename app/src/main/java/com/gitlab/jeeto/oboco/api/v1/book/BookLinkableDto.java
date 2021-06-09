package com.gitlab.jeeto.oboco.api.v1.book;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.common.LinkableDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookLinkable", description = "A linkable of books.")
@XmlRootElement(name = "BookLinkable")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookLinkableDto extends LinkableDto<BookDto> {

}
