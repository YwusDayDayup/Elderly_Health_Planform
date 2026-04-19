
package com.example.common.result;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

  private long total;
  private List<T> list;

  
  public static <T> PageResult<T> of(long total, List<T> list) {
    PageResult<T> r = new PageResult<>();
    r.total = total;
    r.list = list;
    return r;
  }

  public long getTotal() {
    return total;
  }

  public List<T> getList() {
    return list;
  }
}
