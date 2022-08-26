package com.atguigu.gmall.model.to;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/*
* 支持无限层级
* 当前项目只有三级
* */
@Data
public class CategoryTreeTo implements Serializable {
    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild;
}
