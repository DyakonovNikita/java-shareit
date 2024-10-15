package ru.practicum.shareit.error;

import lombok.Data;

@Data
public class ErrorResponse {
	// название ошибки
	private String error;
	// подробное описание
	private String description;

	public ErrorResponse(String error, String description) {
		this.error = error;
		this.description = description;
	}
}