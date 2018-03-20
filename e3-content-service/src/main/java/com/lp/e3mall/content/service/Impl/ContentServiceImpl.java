package com.lp.e3mall.content.service.Impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.lp.e3mall.common.jedis.JedisClient;
import com.lp.e3mall.common.pojo.EasyuiResultJson;
import com.lp.e3mall.common.utils.JsonUtils;
import com.lp.e3mall.content.service.ContentService;
import com.lp.e3mall.mapper.TbContentMapper;
import com.lp.e3mall.pojo.TbContent;
import com.lp.e3mall.pojo.TbContentExample;
import com.lp.e3mall.pojo.TbContentExample.Criteria;
@Service
public class ContentServiceImpl implements ContentService{

	@Autowired
	private TbContentMapper tbContentMapper;
	
	@Autowired
	private JedisClient jedisClient;

	@Value("${CONTENT_LIST}")
	private String CONTENT_LIST;
	
	@Override
	public List<TbContent> getContent(Long categoryId) {
		//先查询redis缓存中有没有数据，如果有，就直接返回
		try {
			String data = jedisClient.hget(CONTENT_LIST, categoryId + "");
			if (StringUtils.isNotBlank(data)) {
				List<TbContent> jedisList = JsonUtils.jsonToList(data, TbContent.class);
				return jedisList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//如果没有，再去查询数据库
		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(example);
		
		//将查询结果添加到redis中
		try {
			jedisClient.hset(CONTENT_LIST, categoryId + "", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}


	@Override
	public EasyuiResultJson updateContent(TbContent tbContent) {
		tbContent.setUpdated(new Date());
		tbContentMapper.updateByPrimaryKeySelective(tbContent);
		
		//修改内容之后，需要将对应的部分的在redis中的数据删除，以做到缓存同步
		jedisClient.hdel(CONTENT_LIST, tbContent.getCategoryId() + "");
				
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(null);
		return easyuiResultJson;
	}


	@Override
	public EasyuiResultJson deleteContent(String ids) {
		String[] contentIds = ids.split(",");
		
		//获取删除的数据的内容分类，然后根据内容分类id删除redis中的数据
		String categoryId = null;
		for (String id : contentIds) {
			TbContent content = tbContentMapper.selectByPrimaryKey(Long.parseLong(id));
			categoryId = content.getCategoryId() + "";
			
			tbContentMapper.deleteByPrimaryKey(Long.parseLong(id));
		}
		//删除内容之后，需要将对应的部分的在redis中的数据删除，以做到缓存同步
		jedisClient.hdel(CONTENT_LIST, categoryId);
		
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(null);
		return easyuiResultJson;
	}


	@Override
	public EasyuiResultJson saveContent(TbContent tbContent) {
		tbContent.setCreated(new Date());
		tbContent.setUpdated(new Date());
		tbContentMapper.insert(tbContent);
		
		//添加内容之后，需要将对应的部分的在redis中的数据删除，以做到缓存同步
		jedisClient.hdel(CONTENT_LIST, tbContent.getCategoryId()+"");
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(null);
		return easyuiResultJson;
	}
	
}
