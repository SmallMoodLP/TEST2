package com.lp.e3mall.content.service;

import java.util.List;

import com.lp.e3mall.common.pojo.EasyuiResultJson;
import com.lp.e3mall.pojo.TbContent;

public interface ContentService {
	public List<TbContent> getContent(Long categoryId);
	
	public EasyuiResultJson updateContent(TbContent tbContent);
	
	public EasyuiResultJson deleteContent(String ids);
	
	public EasyuiResultJson saveContent(TbContent tbContent);
}
