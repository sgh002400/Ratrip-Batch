package ddd.batch.domain;

import java.util.List;

import lombok.Getter;

@Getter
public enum Category {
	CAFE(List.of("CE7")),
	RESTAURANT(List.of("FD6")),
	TOURIST_ATTRACTION(List.of("AT4", "CT1")),
	ACCOMMODATION(List.of("AD5")),
	MART(List.of("MT1", "CS2")),
	ETC(List.of("ETC"));

	private List<String> code;

	Category(List<String> code) {
		this.code = code;
	}

}