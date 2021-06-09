package com.gitlab.jeeto.oboco.api.v1.user;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gitlab.jeeto.oboco.common.PageableListDto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserPageableList", description = "A pageable list of users.")
@XmlRootElement(name = "UserPageableList")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserPageableListDto extends PageableListDto<UserDto> {

}
