package se.plushogskolan.casemanagement.model;

import java.time.LocalDate;

import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public abstract class AuditedAbstractEntity extends AbstractEntity {

	@CreatedBy
	protected String createdBy;

	@LastModifiedBy
	protected String lastModifiedBy;

	@CreatedDate
	protected LocalDate createdDate;

	@LastModifiedDate
	protected LocalDate lastModifiedDate;

	public String getCreatedBy() {
		return createdBy;
	}

	public <T extends AuditedAbstractEntity> T setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return (T) this;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public <T extends AuditedAbstractEntity> T setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
		return (T) this;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public <T extends AuditedAbstractEntity> T setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
		return (T) this;
	}

	public LocalDate getLastModifiedDate() {
		return lastModifiedDate;
	}

	public <T extends AuditedAbstractEntity> T setLastModifiedDate(LocalDate lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
		return (T) this;
	}
}
