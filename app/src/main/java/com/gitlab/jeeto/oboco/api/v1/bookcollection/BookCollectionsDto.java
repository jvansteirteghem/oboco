package com.gitlab.jeeto.oboco.api.v1.bookcollection;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.common.PageableListDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BookCollections", description = "A pageable list of bookCollections.")
@XmlRootElement(name = "BookCollections")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCollectionsDto extends PageableListDto<BookCollectionDto> {

}
