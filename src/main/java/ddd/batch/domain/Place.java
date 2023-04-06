package ddd.batch.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends AuditingTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column
	private String kakaoId;

	@NotNull
	@Column(columnDefinition = "VARCHAR(100)")
	private String name;

	@Enumerated(EnumType.STRING)
	private Category category;

	@NotNull
	@Embedded
	private Location location;

	@NotNull
	@Embedded
	private Address address;

	@NotNull
	@Column
	private long viewCount;

	@NotNull
	@Column
	private long tripCount;

	@NotNull
	@Column
	private long bookmarkCount;

	@NotNull
	@Column
	private long totalScore;

	@Column(columnDefinition = "VARCHAR(255)")
	private String imageLink;

	@Column(columnDefinition = "VARCHAR(255)")
	private String placeLink;

	@Column(columnDefinition = "VARCHAR(100)")
	private String telephone;

	@NotNull
	@Column(columnDefinition = "TINYINT(1)")
	private Boolean isDeleted;

	@ElementCollection
	private List<Blog> blogs = new ArrayList<>();

	public void decreaseTotalScore(long totalScore) {
		this.totalScore = totalScore;
	}
}