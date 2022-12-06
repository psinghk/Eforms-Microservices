package in.nic.ashwini.eForms.models;

import java.util.List;
import java.util.Set;

import javax.naming.Name;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public final class Po  {
    private Name dn;
    
	private Set<String> bo;
}