package com.easy.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easy.bean.Category;
import com.easy.service.CategoryService;
import com.easy.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@CrossOrigin
@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @PostMapping("/")
    //@RequestBody将json数据映射成category数据
    public Result add(@RequestBody Category category){
        categoryService.save(category);
        category=categoryService.getById(category.getId());
        return Result.success("新增数据成功",category);
    }
    @PutMapping("/")
    public Result edit(@RequestBody Category category){
        categoryService.updateById(category);
        category=categoryService.getById(category.getId());
        return Result.success("编辑数据成功",category);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id){
        categoryService.removeById(id);
        return Result.success("删除数据成功");
    }
    @GetMapping("/{id}")
    public Result getById(@PathVariable int id){
        Category category=categoryService.getById(id);
        return Result.success("",category);
    }
    @GetMapping("/")
    public Result getPage(Page page,@RequestParam(value="searchtext",required = false) String searchtext){
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper();
        if(searchtext!=null) {
            lambdaQueryWrapper.like(Category::getName, searchtext);
        }
        page=categoryService.page(page,lambdaQueryWrapper);
        return Result.success("",page);
    }
}
