package com.lp.e3mall.content.service;

import java.util.List;

import com.lp.e3mall.common.pojo.EasyuiResultJson;
import com.lp.e3mall.common.pojo.EasyuiTreeResult;

public interface ContentCategoryService {

	public List<EasyuiTreeResult> getAllContentCategory(Long parentId);
	public EasyuiResultJson addContentCategory(Long parentId,String name);
	public EasyuiResultJson updateContentCategory(Long id,String name);
	public EasyuiResultJson deleteContentCategory(Long id);
}
