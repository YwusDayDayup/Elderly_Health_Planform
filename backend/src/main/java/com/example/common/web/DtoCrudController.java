
package com.example.common.web;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import org.springframework.web.bind.annotation.*;

public abstract class DtoCrudController<PageVO, DetailVO, CreateReq, UpdateReq> {

  @GetMapping
  public abstract Result<PageResult<PageVO>> page(
      @RequestParam(value = "pageNo", required = false, defaultValue = "1") Long pageNo,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String role);

  @GetMapping("/{id}")
  public abstract Result<DetailVO> detail(@PathVariable Long id);

  @PostMapping
  public abstract Result<Void> create(@RequestBody CreateReq req);

  @PutMapping("/{id}")
  public abstract Result<Void> update(@PathVariable Long id, @RequestBody UpdateReq req);

  @DeleteMapping("/{id}")
  public abstract Result<Void> delete(@PathVariable Long id);
}
