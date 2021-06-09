package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.common.PageableListDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookCollectionPageableList", description = "A pageable list of bookCollections.")
@XmlRootElement(name = "BookCollectionPageableList")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCollectionPageableListDto extends PageableListDto<BookCollectionDto> {

}
