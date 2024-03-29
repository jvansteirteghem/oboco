package com.gitlab.jeeto.oboco.api.v1.book;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.api.PageableListDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookPageableList", description = "A pageable list of books.")
@XmlRootElement(name = "BookPageableList")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookPageableListDto extends PageableListDto<BookDto> {

}
