// src/main/java/com/monarchsolutions/sms/dto/common/PageResult.java
package com.monarchsolutions.sms.dto.common;

import java.util.List;

public class PageResult<T> {
  private List<T> content;
  private long   totalElements;
  private int    totalPages;
  private int    page;
  private int    size;

  public PageResult(List<T> content, long totalElements, int page, int size) {
    this.content       = content;
    this.totalElements = totalElements;
    this.page          = page;
    this.size          = size;
    this.totalPages    = (int)((totalElements + size - 1) / size);
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }
  
}
