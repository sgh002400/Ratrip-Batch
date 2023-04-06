package ddd.batch.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Blog {
	@Column(name = "blog_title", columnDefinition = "VARCHAR(255)")
	private String title;

	@Column(name = "blog_link", columnDefinition = "VARCHAR(255)")
	private String link;

}
