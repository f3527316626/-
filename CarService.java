package com.easy.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easy.bean.Product;
import com.easy.mapper.ProductMapper;
import org.springframework.stereotype.Service;
@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {

}

