package tv.cadent.ctaas.dis.vo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Rules {
	private long id;
	private String deal_extref;
	private double bid_floor;
	private String bid_floor_cur;
	private String bidders[];
	private String content_providers[];
	private String distributors[];
	private String start_date;
	private String end_date;
	private String active_date_range;
	private String account_id[];
	private String version[];
	private long ZIP[];
	private String _root_ [];
}
