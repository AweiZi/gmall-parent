package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Awei
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-22 19:16:05
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(String c1Id, String c2Id, String c3Id) {
        List<BaseAttrInfo> list = baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);
        return list;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo info) {

        if (info.getId() == null) {
            //1、进行属性新增操作
            addBaseAttrInfo(info);
        } else {
//          2.该属性名信息
            updateBaseAttrInfo(info);
        }

    }


    private void updateBaseAttrInfo(BaseAttrInfo info) {
        //            2.1该属性名信息
        baseAttrInfoMapper.updateById(info);
//            改属性值
//            1.老记录全删，新提交全新增，导致引用失效
//            2.正确做法
        List<BaseAttrValue> valueList = info.getAttrValueList();

//           删除
        //1.前端提交的所有属性id
        ArrayList<Long> vids = new ArrayList<>();
        for (BaseAttrValue attrValue : valueList) {
            Long id = attrValue.getId();
            if (id!= null){
                vids.add(id);
            }
        }
        //   delete * from base_attr_value where attr_id = 11 and id not in(59,61)
        if (vids.size()>0){
//                部分删除
            LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseAttrValue::getAttrId, info.getId()).notIn(BaseAttrValue::getId,vids);
            baseAttrValueMapper.delete(wrapper);
        }else {
//                全删，前端一个属性值id都没带，全删
            LambdaQueryWrapper<BaseAttrValue> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseAttrValue::getAttrId, info.getId());
            baseAttrValueMapper.delete(wrapper);
        }

        for (BaseAttrValue attrValue : valueList) {
//                修改属性值
            if (attrValue.getId() != null) {
//                    属性只有id，说明数据库以前有，此次只需要修改即可
                baseAttrValueMapper.updateById(attrValue);
            }
            if (attrValue.getId() == null) {
//                    说明数据库以前没有是新增操作
                attrValue.setAttrId(info.getId());
                baseAttrValueMapper.insert(attrValue);
            }

        }
    }

    private void addBaseAttrInfo(BaseAttrInfo info) {
        //        保存属性名
        baseAttrInfoMapper.insert(info);
//        拿到刚才保存的属性名的自增id
        Long id = info.getId();

//        保存属性值
        List<BaseAttrValue> valueList = info.getAttrValueList();
        for (BaseAttrValue value : valueList) {
            value.setAttrId(id);
            baseAttrValueMapper.insert(value);
        }
    }
}




