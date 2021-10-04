package com.hw.userservice.commons.entity.base;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {

  @CreatedBy
  @Column(updatable = false)
  private String createdBy;

  @LastModifiedBy private String lastModifiedBy;

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }
}
