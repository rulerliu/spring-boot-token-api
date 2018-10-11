package com.itmayiedu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.itmayiedu.controller.base.BaseApiService;
import com.itmayiedu.controller.base.ResponseBase;
import com.itmayiedu.entity.AppEntity;
import com.itmayiedu.mapper.AppMapper;
import com.itmayiedu.utils.BaseRedisService;
import com.itmayiedu.utils.TokenUtils;

/**
 * 必须拿最新的accessToken才可以访问接口
 * @author mayn
 *
 */
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseApiService {

	@Autowired
	private AppMapper appMapper;
	
	@Autowired
	private BaseRedisService baseRedisService;
	
	@RequestMapping("/getAccessToken")
	public ResponseBase getAccessToken(AppEntity appEntity) {
		
		// 白话文
		// 1.获取对应生成的appid+appSecret，验证是否可用
		AppEntity app = appMapper.findApp(appEntity);
		if (app == null) {
			return setResultError("没有对应的机构信息...");
		}
		if (app.getIsFlag() == 1) {
			return setResultError("暂时对该机构不开放，请联系客服...");
		}
		
		// 2.appid+appSecret 生成唯一对应的accessToken
		String accessToken = TokenUtils.getAccessToken();
		baseRedisService.setString(accessToken, app.getAppId(), 20 * 60 * 60L);
		
		// 3.删除之前的accessToken
		String preAccessToken = app.getAccessToken();
		if (!StringUtils.isEmpty(preAccessToken)) {
			baseRedisService.delKey(preAccessToken);
		}
		appMapper.updateAccessToken(accessToken, app.getAppId());
		
		// 4.返回最新的accessToken
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accessToken", accessToken);
		return setResultSuccessData(jsonObject);
	}
	
	
}
