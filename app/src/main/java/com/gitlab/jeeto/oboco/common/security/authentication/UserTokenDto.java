package com.gitlab.jeeto.oboco.common.security.authentication;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserToken", description = "A userToken.")
@XmlRootElement(name = "UserToken")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTokenDto {
	private String token;
	public UserTokenDto() {
		super();
	}
	@Schema(name = "token")
	@XmlElement(name = "token")
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
