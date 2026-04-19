
package com.example.common.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.example.common.exception.BizException;
import com.example.common.model.BaseEntity;
import com.example.common.model.PageQuery;
import com.example.common.result.PageResult;
import com.example.common.result.Result;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

public abstract class MpCrudController<E extends BaseEntity, M extends MPJBaseMapper<E>> {

  protected final M mapper;

  protected MpCrudController(M mapper) {
    this.mapper = mapper;
  }

  
  @GetMapping
  public Result<PageResult<E>> page(
      @RequestParam(value = "pageNo", required = false, defaultValue = "1") Long pageNo,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize) {
    IPage<E> page = mapper.selectPage(new Page<>(PageQuery.pageNo(pageNo), PageQuery.pageSize(pageSize)),
        new QueryWrapper<E>().orderByDesc("id"));
    return Result.ok(PageResult.of(page.getTotal(), page.getRecords()));
  }

  
  @GetMapping("/{id}")
  public Result<E> detail(@PathVariable Long id) {
    E entity = mapper.selectById(id);
    if (ObjectUtil.isNull(entity)) {
      throw new BizException(BizException.Code.NOT_FOUND, "数据不存在");
    }
    return Result.ok(entity);
  }

  
  @PostMapping
  @Transactional
  public Result<Long> create(@RequestBody E entity) {
    entity.setId(null);
    mapper.insert(entity);
    return Result.ok(entity.getId());
  }

  
  @PutMapping("/{id}")
  @Transactional
  public Result<Void> update(@PathVariable Long id, @RequestBody E entity) {
    if (!mapper.exists(new QueryWrapper<E>().eq("id", id))) {
      throw new BizException(BizException.Code.NOT_FOUND, "数据不存在");
    }
    entity.setId(id);
    mapper.updateById(entity);
    return Result.ok(null);
  }

  
  @DeleteMapping("/{id}")
  @Transactional
  public Result<Void> delete(@PathVariable Long id) {
    if (mapper.deleteById(id) <= 0) {
      throw new BizException(BizException.Code.NOT_FOUND, "数据不存在");
    }
    return Result.ok(null);
  }
}
