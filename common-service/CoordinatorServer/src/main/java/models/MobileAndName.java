package models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public final class MobileAndName {
	private String cn = "";
	private String mobile = "";
}
