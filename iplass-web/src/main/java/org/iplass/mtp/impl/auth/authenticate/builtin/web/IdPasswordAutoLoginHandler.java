/*
 * Copyright (C) 2018 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
 * 
 * Unless you have purchased a commercial license,
 * the following license terms apply:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.iplass.mtp.impl.auth.authenticate.builtin.web;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.iplass.mtp.ApplicationException;
import org.iplass.mtp.SystemException;
import org.iplass.mtp.auth.login.IdPasswordCredential;
import org.iplass.mtp.auth.login.LoginFailedException;
import org.iplass.mtp.command.RequestContext;
import org.iplass.mtp.impl.auth.UserContext;
import org.iplass.mtp.impl.auth.authenticate.AutoLoginHandler;
import org.iplass.mtp.impl.auth.authenticate.AutoLoginInstruction;
import org.iplass.mtp.impl.session.SessionService;
import org.iplass.mtp.impl.web.token.TokenStore;
import org.iplass.mtp.impl.webapi.rest.RestRequestContext;
import org.iplass.mtp.spi.ServiceRegistry;
import org.iplass.mtp.web.WebRequestConstants;
import org.iplass.mtp.web.actionmapping.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ID/PasswordベースのAutoLoginHandler。
 * 
 * @author K.Higuchi
 *
 */
public class IdPasswordAutoLoginHandler implements AutoLoginHandler {
	
	private static Logger logger = LoggerFactory.getLogger(IdPasswordAutoLoginHandler.class);
	
	public static final String AUTH_ID_HEADER = "X-Auth-Id";
	public static final String AUTH_PASSWORD_HEADER = "X-Auth-Password";
	
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String AUTH_SCHEME_BASIC = "Basic";
	
	private SessionService sessionService = ServiceRegistry.getRegistry().getService(SessionService.class);
	
	private boolean enableBasicAuthentication;
	private boolean rejectAmbiguousRequest;
	
	public boolean isRejectAmbiguousRequest() {
		return rejectAmbiguousRequest;
	}

	public void setRejectAmbiguousRequest(boolean rejectAmbiguousRequest) {
		this.rejectAmbiguousRequest = rejectAmbiguousRequest;
	}

	public boolean isEnableBasicAuthentication() {
		return enableBasicAuthentication;
	}

	public void setEnableBasicAuthentication(boolean enableBasicAuthentication) {
		this.enableBasicAuthentication = enableBasicAuthentication;
	}

	private IdPasswordCredential idPassFromHeader(RequestContext req) {
		HttpServletRequest hr = (HttpServletRequest) req.getAttribute(WebRequestConstants.SERVLET_REQUEST);
		String id = null;
		String pass = null;
		
		//カスタムヘッダーによる認証処理
		id = hr.getHeader(AUTH_ID_HEADER);
		if (id != null && id.length() > 0) {
			logger.debug("handle custom header authentication");
			pass = hr.getHeader(AUTH_PASSWORD_HEADER);
			return new IdPasswordCredential(id, pass);
		}
		
		//Basic認証
		if (enableBasicAuthentication) {
			String authHeaderValue = hr.getHeader(HEADER_AUTHORIZATION);
			if (authHeaderValue != null && authHeaderValue.regionMatches(true, 0, AUTH_SCHEME_BASIC + " ", 0, AUTH_SCHEME_BASIC.length() + 1)) {
				logger.debug("handle basic authentication");
				
				String idpassEncoded = authHeaderValue.substring(AUTH_SCHEME_BASIC.length() + 1).trim();
				String idpass;
				try {
					idpass = new String(Base64.getDecoder().decode(idpassEncoded), "utf-8");
				} catch (UnsupportedEncodingException e) {
					throw new SystemException(e);
				}
				
				int index = idpass.indexOf(':');
				if (index < 0) {
					id = idpass.trim();
				} else {
					id = idpass.substring(0, index).trim();
					pass = idpass.substring(index + 1).trim();
				}
				
				return new IdPasswordCredential(id, pass);
			}
		}
		
		return null;
	}
	
	@Override
	public AutoLoginInstruction handle(RequestContext req, boolean isLogined, UserContext user) {
		if (!(req instanceof RestRequestContext)) {
			return AutoLoginInstruction.THROUGH;
		}
		
		IdPasswordCredential cre = idPassFromHeader(req);
		if (cre == null) {
			return AutoLoginInstruction.THROUGH;
		}
		
		if (isLogined) {
			if (!cre.getId().equals(user.getAccount().getCredential().getId())) {
				if (rejectAmbiguousRequest) {
					//セッション上のUserと、HeaderのIDが等しくないならエラー
					throw new LoginFailedException("another login session is avaliable");
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("login session is avaliable, but another id/pass is specified. current id:" + user.getAccount().getCredential().getId() + ", request id:" + cre.getId());
					} else {
						logger.warn("login session is avaliable, but another id/pass is specified.");
					}
				}
			}
			
			return AutoLoginInstruction.THROUGH;
		} else {
			return new AutoLoginInstruction(cre);
		}
	}

	@Override
	public void handleSuccess(AutoLoginInstruction ali, RequestContext req, UserContext user) {
		if (!sessionService.isSessionStateless()) {
			//transaction token を返却
			String token = TokenStore.getFixedToken(req.getSession());
			ResponseHeader res = (ResponseHeader) req.getAttribute(WebRequestConstants.RESPONSE_HEADER);
			res.setHeader(TokenStore.TOKEN_HEADER_NAME, token);
		}
	}

	@Override
	public Exception handleException(AutoLoginInstruction ali, ApplicationException e, RequestContext req, boolean isLogined,
			UserContext user) {
		if (isBasicAuth(req)) {
			throw new WWWAuthenticateException(AUTH_SCHEME_BASIC, null, "Login with BASIC Authentication failed.");
		} else {
			throw e;
		}
	}

	private boolean isBasicAuth(RequestContext req) {
		if (!enableBasicAuthentication) {
			return false;
		}
		
		HttpServletRequest hr = (HttpServletRequest) req.getAttribute(WebRequestConstants.SERVLET_REQUEST);
		if (hr.getHeader(AUTH_ID_HEADER) != null) {
			return false;
		}
		
		return true;
	}

}