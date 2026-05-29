package com.easy.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easy.bean.Product;
import com.easy.service.BrandService;
import com.easy.service.CategoryService;
import com.easy.service.ProductService;
import com.easy.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    BrandService brandService;
    @PostMapping("/")
    public Result add(@RequestBody Product product){
        productService.save(product);
        product=productService.getById(product.getId());
        return Result.success("新增数据成功",product);
    }
    @PutMapping("/")
    public Result edit(@RequestBody Product product){
        productService.updateById(product);
        product=productService.getById(product.getId());
        return Result.success("编辑数据成功",product);
    }
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable int id){
        productService.removeById(id);
        return Result.success("删除数据成功");
    }
    //单个查询
    @GetMapping("/{id}")
    public Result getById(@PathVariable int id){
        Product product=productService.getById(id);
        return Result.success("",product);
    }
    //分页查询
    @GetMapping("/")
    public Result getPage(
            Page<Product> page,
            @RequestParam(value = "searchtext", required = false) String searchtext,
            @RequestParam(value = "brandId", required = false) Integer brandId  // 新增品牌ID参数
    ) {
        LambdaQueryWrapper<Product> lambdaQueryWrapper = new LambdaQueryWrapper();
        // 文本搜索
        if (searchtext != null && !searchtext.trim().isEmpty()) {
            lambdaQueryWrapper.like(Product::getName, searchtext);
        }
        // 品牌筛选，如果品牌ID不为空，添加品牌ID相等查询条件
        if (brandId != null) {
            lambdaQueryWrapper.eq(Product::getBrand_id, brandId);
        }
        //执行分页查询
        page = productService.page(page, lambdaQueryWrapper);
        //遍历查询结果，填充关联的分类和品牌信息
        for (Product item : page.getRecords()) {
            // 如果产品有分类ID，查询并设置分类信息
            if (item.getCategory_id() != null) {
                item.setCategory(categoryService.getById(item.getCategory_id()));
            }
            // 如果产品有品牌ID，查询并设置品牌信息
            if (item.getBrand_id() != null) {
                item.setBrand(brandService.getById(item.getBrand_id()));
            }
        }
        return Result.success("", page);
    }
    //更新状态status的函数
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> params) {
        //从URL请求参数中获取产品ID
        Integer id = (Integer) params.get("id");
        //从请求参数中获取状态值
        Integer status = (Integer) params.get("status");
        //参数校验
        if (id == null || status == null) {
            return Result.error("参数错误");
        }
        //创建产品对象并设置ID和状态
        Product product = new Product();
        product.setId(id);
        //执行更新操作
        product.setStatus(status);
        boolean success = productService.updateById(product);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
}