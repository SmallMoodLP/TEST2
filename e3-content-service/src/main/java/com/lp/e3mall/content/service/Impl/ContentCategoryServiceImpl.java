package com.lp.e3mall.content.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lp.e3mall.common.pojo.EasyuiResultJson;
import com.lp.e3mall.common.pojo.EasyuiTreeResult;
import com.lp.e3mall.content.service.ContentCategoryService;
import com.lp.e3mall.mapper.TbContentCategoryMapper;
import com.lp.e3mall.pojo.TbContentCategory;
import com.lp.e3mall.pojo.TbContentCategoryExample;
import com.lp.e3mall.pojo.TbContentCategoryExample.Criteria;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper tbContentCategoryMapper;
	@Override
	public List<EasyuiTreeResult> getAllContentCategory(Long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		
		List<EasyuiTreeResult> easyuiTreeResultList = new ArrayList<EasyuiTreeResult>();
		for (TbContentCategory tbContentCategory : list) {
			EasyuiTreeResult easyuiTreeResult = new EasyuiTreeResult();
			easyuiTreeResult.setId(tbContentCategory.getId());
			easyuiTreeResult.setText(tbContentCategory.getName());
			easyuiTreeResult.setState(tbContentCategory.getIsParent()?"closed":"open");
			easyuiTreeResultList.add(easyuiTreeResult);
		}
		
		return easyuiTreeResultList;
	}
	
	
	
	
	@Override
	public EasyuiResultJson addContentCategory(Long parentId,String name) {
		TbContentCategory tbContentCategory = new TbContentCategory();
		tbContentCategory.setParentId(parentId);
		tbContentCategory.setName(name);
		//可选值:1(正常),2(删除)
		tbContentCategory.setStatus(1);
		tbContentCategory.setSortOrder(1);
		tbContentCategory.setIsParent(false);
		tbContentCategory.setCreated(new Date());
		tbContentCategory.setUpdated(new Date());
		tbContentCategoryMapper.insert(tbContentCategory);
		
		//判断它的父类目是不是父节点，如果不是，设置成是
		TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(parentId);
		if (!parent.getIsParent()) {
			parent.setIsParent(true);
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(tbContentCategory);
		return easyuiResultJson;
	}




	@Override
	public EasyuiResultJson updateContentCategory(Long id, String name) {
		TbContentCategory category = tbContentCategoryMapper.selectByPrimaryKey(id);
		category.setName(name);
		tbContentCategoryMapper.updateByPrimaryKey(category);
		
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(null);
		
		return easyuiResultJson;
	}




	@Override
	public EasyuiResultJson deleteContentCategory(Long id) {
		
		//再查询父id，然后根据父id查询结果集，如果没有查询到结果，就将父类目的isparent修改成false
		TbContentCategory category = tbContentCategoryMapper.selectByPrimaryKey(id);
		
		tbContentCategoryMapper.deleteByPrimaryKey(id);
		
		
		
		TbContentCategoryExample example = new TbContentCategoryExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(category.getParentId());
		List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
		if (list.size() == 0) {
			TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(category.getParentId());
			parent.setIsParent(false);
			tbContentCategoryMapper.updateByPrimaryKey(parent);
		}
		
		EasyuiResultJson easyuiResultJson = new EasyuiResultJson(null);
		return easyuiResultJson;
	}

}
