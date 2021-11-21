package tv.cadent.ctaas.dis.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SolrQueryVo {
	private int pid;
	private String pname;
	private String pdescription;
	private int pquantity;
	private float pprice;
	private String pcategory;

}
