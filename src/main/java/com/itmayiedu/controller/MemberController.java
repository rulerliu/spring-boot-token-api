package com.itmayiedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itmayiedu.controller.base.BaseApiService;
import com.itmayiedu.controller.base.ResponseBase;
import com.itmayiedu.entity.AppEntity;
import com.itmayiedu.mapper.AppMapper;
import com.itmayiedu.utils.BaseRedisService;

@RestController
@RequestMapping("/openApi")
public class MemberController extends BaseApiService {

	@Autowired
	private BaseRedisService baseRedisService;
	
	@Autowired
	private AppMapper appMapper;
	
	@RequestMapping("/getUser")
	public ResponseBase getUser(String accessToken) {
		if (StringUtils.isEmpty(accessToken)) {
			return setResultError("accessToken is null");
		}
		
		String appId = (String) baseRedisService.getString(accessToken);
		if (StringUtils.isEmpty(appId)) {
			return setResultError("this accessToken is invalid");
		}
		
		AppEntity app = appMapper.findByAppId(appId);
		if (app == null) {
			return setResultError("app not found");
		}
		
		if (app.getIsFlag() == 1) {
			// 权限不足
			return setResultError("no permission");
		}
		
		
		return setResultSuccess("调用接口成功...");
	}
	
	
}
