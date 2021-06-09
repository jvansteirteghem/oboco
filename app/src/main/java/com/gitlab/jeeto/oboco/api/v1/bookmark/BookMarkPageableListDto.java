package com.gitlab.jeeto.oboco.api.v1.bookmark;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.common.PageableListDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookMarkPageableList", description = "A pageable list of bookMarks.")
@XmlRootElement(name = "BookMarkPageableList")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookMarkPageableListDto extends PageableListDto<BookMarkDto> {

}
